package com.financecontrol.api.domain.subcategoria;

import com.financecontrol.api.domain.categoria.Categoria;
import com.financecontrol.api.domain.categoria.CategoriaRepositoryPort;
import com.financecontrol.api.domain.lancamento.LancamentoRepositoryPort;
import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;
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
class SubcategoriaServiceImplTest {

    @Mock
    private SubcategoriaRepositoryPort subcategoriaRepositoryPort;

    @Mock
    private CategoriaRepositoryPort categoriaRepositoryPort;

    @Mock
    private LancamentoRepositoryPort lancamentoRepositoryPort;

    @InjectMocks
    private SubcategoriaServiceImpl subcategoriaService;

    private Subcategoria subcategoriaCombutivel;
    private Categoria categoriaTransporte;
    private ParametrosPaginacao paginacaoPadrao;

    @BeforeEach
    void setUp() {
        categoriaTransporte = new Categoria(1L, "Transporte");
        subcategoriaCombutivel = new Subcategoria(1L, "Combustivel", 1L);
        paginacaoPadrao = new ParametrosPaginacao(0, 20);
    }

    @Test
    void deveBuscarSubcategoriaPorIdComSucesso() {
        when(subcategoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(subcategoriaCombutivel));

        Subcategoria resultado = subcategoriaService.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Combustivel");
        assertThat(resultado.getIdCategoria()).isEqualTo(1L);
        verify(subcategoriaRepositoryPort).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoSubcategoriaNaoEncontradaPorId() {
        when(subcategoriaRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subcategoriaService.buscarPorId(99L))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("99")
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);
    }

    @Test
    void deveListarTodasAsSubcategoriasSemFiltro() {
        PaginaResultado<Subcategoria> paginaEsperada = new PaginaResultado<>(
                List.of(subcategoriaCombutivel), 0, 20, 1, 1
        );
        when(subcategoriaRepositoryPort.findAll(paginacaoPadrao)).thenReturn(paginaEsperada);

        PaginaResultado<Subcategoria> resultado = subcategoriaService.listar(null, null, paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
        assertThat(resultado.totalElementos()).isEqualTo(1);
        verify(subcategoriaRepositoryPort).findAll(paginacaoPadrao);
        verify(subcategoriaRepositoryPort, never()).findByNomeContendo(any(), any());
    }

    @Test
    void deveListarSubcategoriasComFiltroDeNome() {
        PaginaResultado<Subcategoria> paginaEsperada = new PaginaResultado<>(
                List.of(subcategoriaCombutivel), 0, 20, 1, 1
        );
        when(subcategoriaRepositoryPort.findByNomeContendo("Combu", paginacaoPadrao)).thenReturn(paginaEsperada);

        PaginaResultado<Subcategoria> resultado = subcategoriaService.listar("Combu", null, paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
        verify(subcategoriaRepositoryPort).findByNomeContendo("Combu", paginacaoPadrao);
        verify(subcategoriaRepositoryPort, never()).findAll(any());
    }

    @Test
    void deveListarSubcategoriasComFiltroDeId() {
        PaginaResultado<Subcategoria> paginaEsperada = new PaginaResultado<>(
                List.of(subcategoriaCombutivel), 0, 20, 1, 1
        );
        when(subcategoriaRepositoryPort.findByIdPaginado(1L, paginacaoPadrao)).thenReturn(paginaEsperada);

        PaginaResultado<Subcategoria> resultado = subcategoriaService.listar(null, 1L, paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
        verify(subcategoriaRepositoryPort).findByIdPaginado(1L, paginacaoPadrao);
        verify(subcategoriaRepositoryPort, never()).findAll(any());
    }

    @Test
    void deveListarSubcategoriasComFiltroDeNomeEId() {
        PaginaResultado<Subcategoria> paginaEsperada = new PaginaResultado<>(
                List.of(subcategoriaCombutivel), 0, 20, 1, 1
        );
        when(subcategoriaRepositoryPort.findByNomeContendoEId("Combu", 1L, paginacaoPadrao)).thenReturn(paginaEsperada);

        PaginaResultado<Subcategoria> resultado = subcategoriaService.listar("Combu", 1L, paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
        verify(subcategoriaRepositoryPort).findByNomeContendoEId("Combu", 1L, paginacaoPadrao);
    }

    @Test
    void deveListarTodasQuandoNomeFiltroForBranco() {
        PaginaResultado<Subcategoria> paginaEsperada = new PaginaResultado<>(
                List.of(subcategoriaCombutivel), 0, 20, 1, 1
        );
        when(subcategoriaRepositoryPort.findAll(paginacaoPadrao)).thenReturn(paginaEsperada);

        PaginaResultado<Subcategoria> resultado = subcategoriaService.listar("   ", null, paginacaoPadrao);

        verify(subcategoriaRepositoryPort).findAll(paginacaoPadrao);
        verify(subcategoriaRepositoryPort, never()).findByNomeContendo(any(), any());
        assertThat(resultado.conteudo()).hasSize(1);
    }

    @Test
    void deveCriarSubcategoriaComSucesso() {
        Subcategoria novaSubcategoria = Subcategoria.criar("Combustivel", 1L);
        when(categoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(categoriaTransporte));
        when(subcategoriaRepositoryPort.existsByNomeAndIdCategoria("Combustivel", 1L)).thenReturn(false);
        when(subcategoriaRepositoryPort.save(novaSubcategoria)).thenReturn(subcategoriaCombutivel);

        Subcategoria resultado = subcategoriaService.criar(novaSubcategoria);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Combustivel");
        assertThat(resultado.getIdCategoria()).isEqualTo(1L);
        verify(subcategoriaRepositoryPort).save(novaSubcategoria);
    }

    @Test
    void deveLancarExcecaoAoCriarSubcategoriaComCategoriaNaoEncontrada() {
        Subcategoria novaSubcategoria = Subcategoria.criar("Combustivel", 99L);
        when(categoriaRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subcategoriaService.criar(novaSubcategoria))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("99")
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);

        verify(subcategoriaRepositoryPort, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoCriarSubcategoriaComNomeDuplicadoNaCategoria() {
        Subcategoria novaSubcategoria = Subcategoria.criar("Combustivel", 1L);
        when(categoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(categoriaTransporte));
        when(subcategoriaRepositoryPort.existsByNomeAndIdCategoria("Combustivel", 1L)).thenReturn(true);

        assertThatThrownBy(() -> subcategoriaService.criar(novaSubcategoria))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("Combustivel")
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_NOME_DUPLICADO);

        verify(subcategoriaRepositoryPort, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoCriarSubcategoriaComNomeMuitoCurto() {
        Subcategoria novaSubcategoria = Subcategoria.criar("AB", 1L);

        assertThatThrownBy(() -> subcategoriaService.criar(novaSubcategoria))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_NOME_INVALIDO);

        verify(subcategoriaRepositoryPort, never()).save(any());
        verify(categoriaRepositoryPort, never()).findById(any());
    }

    @Test
    void deveAtualizarSubcategoriaComSucesso() {
        Subcategoria dadosAtualizados = Subcategoria.criar("Gasolina", 1L);
        Subcategoria subcategoriaAtualizada = new Subcategoria(1L, "Gasolina", 1L);

        when(subcategoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(subcategoriaCombutivel));
        when(categoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(categoriaTransporte));
        when(subcategoriaRepositoryPort.existsByNomeAndIdCategoriaAndIdDiferente("Gasolina", 1L, 1L)).thenReturn(false);
        when(subcategoriaRepositoryPort.save(subcategoriaCombutivel)).thenReturn(subcategoriaAtualizada);

        Subcategoria resultado = subcategoriaService.atualizar(1L, dadosAtualizados);

        assertThat(resultado.getNome()).isEqualTo("Gasolina");
        assertThat(resultado.getIdCategoria()).isEqualTo(1L);
        verify(subcategoriaRepositoryPort).save(subcategoriaCombutivel);
    }

    @Test
    void deveLancarExcecaoAoAtualizarSubcategoriaInexistente() {
        Subcategoria dadosAtualizados = Subcategoria.criar("Gasolina", 1L);
        when(subcategoriaRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subcategoriaService.atualizar(99L, dadosAtualizados))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);

        verify(subcategoriaRepositoryPort, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarSubcategoriaComCategoriaNaoEncontrada() {
        Subcategoria dadosAtualizados = Subcategoria.criar("Gasolina", 99L);
        when(subcategoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(subcategoriaCombutivel));
        when(categoriaRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subcategoriaService.atualizar(1L, dadosAtualizados))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("99")
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);

        verify(subcategoriaRepositoryPort, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComNomeDuplicadoNaCategoria() {
        Subcategoria dadosAtualizados = Subcategoria.criar("Gasolina", 1L);
        when(subcategoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(subcategoriaCombutivel));
        when(categoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(categoriaTransporte));
        when(subcategoriaRepositoryPort.existsByNomeAndIdCategoriaAndIdDiferente("Gasolina", 1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> subcategoriaService.atualizar(1L, dadosAtualizados))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("Gasolina")
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_NOME_DUPLICADO);

        verify(subcategoriaRepositoryPort, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComNomeMuitoCurto() {
        Subcategoria dadosAtualizados = Subcategoria.criar("AB", 1L);

        assertThatThrownBy(() -> subcategoriaService.atualizar(1L, dadosAtualizados))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_NOME_INVALIDO);

        verify(subcategoriaRepositoryPort, never()).findById(any());
        verify(subcategoriaRepositoryPort, never()).save(any());
    }

    @Test
    void deveDeletarSubcategoriaComSucesso() {
        when(subcategoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(subcategoriaCombutivel));
        when(lancamentoRepositoryPort.existsByIdSubcategoria(1L)).thenReturn(false);

        subcategoriaService.deletar(1L);

        verify(subcategoriaRepositoryPort).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarSubcategoriaComLancamentosAtrelados() {
        when(subcategoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(subcategoriaCombutivel));
        when(lancamentoRepositoryPort.existsByIdSubcategoria(1L)).thenReturn(true);

        assertThatThrownBy(() -> subcategoriaService.deletar(1L))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_OPERACAO_NAO_PERMITIDA);

        verify(subcategoriaRepositoryPort, never()).deleteById(any());
    }

    @Test
    void deveLancarExcecaoAoDeletarSubcategoriaInexistente() {
        when(subcategoriaRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subcategoriaService.deletar(99L))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);

        verify(subcategoriaRepositoryPort, never()).deleteById(any());
    }
}

