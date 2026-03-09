package com.financecontrol.api.domain.categoria;

import com.financecontrol.api.domain.lancamento.LancamentoRepositoryPort;
import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;
import com.financecontrol.api.domain.shared.PaginaResultado;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {
    @Mock
    private CategoriaRepositoryPort categoriaRepositoryPort;
    @Mock
    private LancamentoRepositoryPort lancamentoRepositoryPort;
    @InjectMocks
    private CategoriaServiceImpl categoriaService;
    private Categoria categoriaTransporte;
    private ParametrosPaginacao paginacaoPadrao;

    @BeforeEach
    void setUp() {
        categoriaTransporte = new Categoria(1L, "Transporte");
        paginacaoPadrao = new ParametrosPaginacao(0, 20);
    }

    @Test
    void deveBuscarCategoriaPorIdComSucesso() {
        when(categoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(categoriaTransporte));
        Categoria resultado = categoriaService.buscarPorId(1L);
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Transporte");
        verify(categoriaRepositoryPort).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoEncontradaPorId() {
        when(categoriaRepositoryPort.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> categoriaService.buscarPorId(99L))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("99")
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);
    }

    @Test
    void deveListarTodasAsCategoriasSemFiltroDeNome() {
        PaginaResultado<Categoria> paginaEsperada = new PaginaResultado<>(
                List.of(categoriaTransporte), 0, 20, 1, 1
        );
        when(categoriaRepositoryPort.findAll(paginacaoPadrao)).thenReturn(paginaEsperada);
        PaginaResultado<Categoria> resultado = categoriaService.listar(null, paginacaoPadrao);
        assertThat(resultado.conteudo()).hasSize(1);
        assertThat(resultado.totalElementos()).isEqualTo(1);
        verify(categoriaRepositoryPort).findAll(paginacaoPadrao);
        verify(categoriaRepositoryPort, never()).findByNomeContendo(any(), any());
    }

    @Test
    void deveListarCategoriasComFiltroDeNome() {
        PaginaResultado<Categoria> paginaEsperada = new PaginaResultado<>(
                List.of(categoriaTransporte), 0, 20, 1, 1
        );
        when(categoriaRepositoryPort.findByNomeContendo("Trans", paginacaoPadrao)).thenReturn(paginaEsperada);
        PaginaResultado<Categoria> resultado = categoriaService.listar("Trans", paginacaoPadrao);
        assertThat(resultado.conteudo()).hasSize(1);
        verify(categoriaRepositoryPort).findByNomeContendo("Trans", paginacaoPadrao);
        verify(categoriaRepositoryPort, never()).findAll(any());
    }

    @Test
    void deveListarTodasAsCategoriaQuandoNomeFiltroForBranco() {
        PaginaResultado<Categoria> paginaEsperada = new PaginaResultado<>(
                List.of(categoriaTransporte), 0, 20, 1, 1
        );
        when(categoriaRepositoryPort.findAll(paginacaoPadrao)).thenReturn(paginaEsperada);
        PaginaResultado<Categoria> resultado = categoriaService.listar("  ", paginacaoPadrao);
        verify(categoriaRepositoryPort).findAll(paginacaoPadrao);
        verify(categoriaRepositoryPort, never()).findByNomeContendo(any(), any());
        assertThat(resultado.conteudo()).hasSize(1);
    }

    @Test
    void deveCriarCategoriaComSucesso() {
        Categoria novaCategoria = Categoria.criar("Saude");
        Categoria categoriaSalva = new Categoria(2L, "Saude");
        when(categoriaRepositoryPort.existsByNome("Saude")).thenReturn(false);
        when(categoriaRepositoryPort.save(novaCategoria)).thenReturn(categoriaSalva);
        Categoria resultado = categoriaService.criar(novaCategoria);
        assertThat(resultado.getId()).isEqualTo(2L);
        assertThat(resultado.getNome()).isEqualTo("Saude");
        verify(categoriaRepositoryPort).existsByNome("Saude");
        verify(categoriaRepositoryPort).save(novaCategoria);
    }

    @Test
    void deveLancarExcecaoAoCriarCategoriaComNomeDuplicado() {
        Categoria novaCategoria = Categoria.criar("Transporte");
        when(categoriaRepositoryPort.existsByNome("Transporte")).thenReturn(true);
        assertThatThrownBy(() -> categoriaService.criar(novaCategoria))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("Transporte")
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_NOME_DUPLICADO);
        verify(categoriaRepositoryPort, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoCriarCategoriaComNomeMuitoCurto() {
        Categoria novaCategoria = Categoria.criar("AB");
        assertThatThrownBy(() -> categoriaService.criar(novaCategoria))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_NOME_INVALIDO);
        verify(categoriaRepositoryPort, never()).existsByNome(any());
        verify(categoriaRepositoryPort, never()).save(any());
    }

    @Test
    void deveAtualizarCategoriaComSucesso() {
        Categoria dadosAtualizados = Categoria.criar("Alimentacao");
        Categoria categoriaAtualizada = new Categoria(1L, "Alimentacao");
        when(categoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(categoriaTransporte));
        when(categoriaRepositoryPort.existsByNomeAndIdDiferente("Alimentacao", 1L)).thenReturn(false);
        when(categoriaRepositoryPort.save(categoriaTransporte)).thenReturn(categoriaAtualizada);
        Categoria resultado = categoriaService.atualizar(1L, dadosAtualizados);
        assertThat(resultado.getNome()).isEqualTo("Alimentacao");
        verify(categoriaRepositoryPort).save(categoriaTransporte);
    }

    @Test
    void deveLancarExcecaoAoAtualizarCategoriaInexistente() {
        Categoria dadosAtualizados = Categoria.criar("Alimentacao");
        when(categoriaRepositoryPort.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> categoriaService.atualizar(99L, dadosAtualizados))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);
        verify(categoriaRepositoryPort, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComNomeDuplicado() {
        Categoria dadosAtualizados = Categoria.criar("Farmacia");
        when(categoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(categoriaTransporte));
        when(categoriaRepositoryPort.existsByNomeAndIdDiferente("Farmacia", 1L)).thenReturn(true);
        assertThatThrownBy(() -> categoriaService.atualizar(1L, dadosAtualizados))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("Farmacia")
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_NOME_DUPLICADO);
        verify(categoriaRepositoryPort, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComNomeMuitoCurto() {
        Categoria dadosAtualizados = Categoria.criar("AB");
        assertThatThrownBy(() -> categoriaService.atualizar(1L, dadosAtualizados))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_NOME_INVALIDO);
        verify(categoriaRepositoryPort, never()).findById(any());
        verify(categoriaRepositoryPort, never()).save(any());
    }

    @Test
    void deveDeletarCategoriaComSucesso() {
        when(categoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(categoriaTransporte));
        when(lancamentoRepositoryPort.existsByIdCategoria(1L)).thenReturn(false);
        categoriaService.deletar(1L);
        verify(categoriaRepositoryPort).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarCategoriaComLancamentosAtrelados() {
        when(categoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(categoriaTransporte));
        when(lancamentoRepositoryPort.existsByIdCategoria(1L)).thenReturn(true);
        assertThatThrownBy(() -> categoriaService.deletar(1L))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_OPERACAO_NAO_PERMITIDA);
        verify(categoriaRepositoryPort, never()).deleteById(any());
    }

    @Test
    void deveLancarExcecaoAoDeletarCategoriaInexistente() {
        when(categoriaRepositoryPort.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> categoriaService.deletar(99L))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);
        verify(categoriaRepositoryPort, never()).deleteById(any());
    }
}