package com.financecontrol.api.domain.balanco;

import com.financecontrol.api.domain.categoria.Categoria;
import com.financecontrol.api.domain.categoria.CategoriaRepositoryPort;
import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalancoServiceImplTest {

    @Mock
    private BalancoRepositoryPort balancoRepositoryPort;

    @Mock
    private CategoriaRepositoryPort categoriaRepositoryPort;

    @InjectMocks
    private BalancoServiceImpl balancoService;

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Categoria categoriaTransporte;

    @BeforeEach
    void setUp() {
        dataInicio = LocalDate.of(2021, 1, 1);
        dataFim = LocalDate.of(2021, 1, 31);
        categoriaTransporte = new Categoria(1L, "Transporte");
    }

    @Test
    void deveCalcularBalancoSemFiltroDeCategoria() {
        when(balancoRepositoryPort.somarReceitas(dataInicio, dataFim, null)).thenReturn(new BigDecimal("2320.00"));
        when(balancoRepositoryPort.somarDespesas(dataInicio, dataFim, null)).thenReturn(new BigDecimal("1000.00"));

        Balanco resultado = balancoService.calcular(dataInicio, dataFim, null);

        assertThat(resultado.receita()).isEqualByComparingTo("2320.00");
        assertThat(resultado.despesa()).isEqualByComparingTo("1000.00");
        assertThat(resultado.saldo()).isEqualByComparingTo("1320.00");
        assertThat(resultado.idCategoria()).isNull();
        assertThat(resultado.nomeCategoria()).isNull();
        verify(categoriaRepositoryPort, never()).findById(anyLong());
    }

    @Test
    void deveCalcularBalancoComFiltroDeCategoria() {
        when(categoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(categoriaTransporte));
        when(balancoRepositoryPort.somarReceitas(dataInicio, dataFim, 1L)).thenReturn(new BigDecimal("2320.00"));
        when(balancoRepositoryPort.somarDespesas(dataInicio, dataFim, 1L)).thenReturn(new BigDecimal("1000.00"));

        Balanco resultado = balancoService.calcular(dataInicio, dataFim, 1L);

        assertThat(resultado.receita()).isEqualByComparingTo("2320.00");
        assertThat(resultado.despesa()).isEqualByComparingTo("1000.00");
        assertThat(resultado.saldo()).isEqualByComparingTo("1320.00");
        assertThat(resultado.idCategoria()).isEqualTo(1L);
        assertThat(resultado.nomeCategoria()).isEqualTo("Transporte");
        verify(categoriaRepositoryPort).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoEncontrada() {
        when(categoriaRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> balancoService.calcular(dataInicio, dataFim, 99L))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("99")
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);

        verify(balancoRepositoryPort, never()).somarReceitas(any(), any(), any());
        verify(balancoRepositoryPort, never()).somarDespesas(any(), any(), any());
    }

    @Test
    void deveLancarExcecaoQuandoDataInicioForMaiorQueDataFim() {
        LocalDate dataInicioInvalida = LocalDate.of(2021, 2, 1);
        LocalDate dataFimInvalida = LocalDate.of(2021, 1, 31);

        assertThatThrownBy(() -> balancoService.calcular(dataInicioInvalida, dataFimInvalida, null))
                .isInstanceOf(NegocioException.class)
                .hasMessage(MensagensErro.PERIODO_INVALIDO)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_ERRO_VALIDACAO);

        verify(categoriaRepositoryPort, never()).findById(anyLong());
        verify(balancoRepositoryPort, never()).somarReceitas(any(), any(), any());
        verify(balancoRepositoryPort, never()).somarDespesas(any(), any(), any());
    }

    @Test
    void deveCalcularSaldoPositivoQuandoReceitaMaiorQueDespesa() {
        when(balancoRepositoryPort.somarReceitas(dataInicio, dataFim, null)).thenReturn(new BigDecimal("500.00"));
        when(balancoRepositoryPort.somarDespesas(dataInicio, dataFim, null)).thenReturn(new BigDecimal("200.00"));

        Balanco resultado = balancoService.calcular(dataInicio, dataFim, null);

        assertThat(resultado.saldo()).isEqualByComparingTo("300.00");
    }

    @Test
    void deveCalcularSaldoNegativoQuandoDespesaMaiorQueReceita() {
        when(balancoRepositoryPort.somarReceitas(dataInicio, dataFim, null)).thenReturn(new BigDecimal("200.00"));
        when(balancoRepositoryPort.somarDespesas(dataInicio, dataFim, null)).thenReturn(new BigDecimal("500.00"));

        Balanco resultado = balancoService.calcular(dataInicio, dataFim, null);

        assertThat(resultado.saldo()).isEqualByComparingTo("-300.00");
    }

    @Test
    void deveCalcularSaldoZeroQuandoReceitaIgualDespesa() {
        when(balancoRepositoryPort.somarReceitas(dataInicio, dataFim, null)).thenReturn(new BigDecimal("300.00"));
        when(balancoRepositoryPort.somarDespesas(dataInicio, dataFim, null)).thenReturn(new BigDecimal("300.00"));

        Balanco resultado = balancoService.calcular(dataInicio, dataFim, null);

        assertThat(resultado.saldo()).isEqualByComparingTo("0.00");
    }
}
