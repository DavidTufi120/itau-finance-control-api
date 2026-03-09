package com.financecontrol.api.adapters.out.persistence.balanco;

import com.financecontrol.api.adapters.out.persistence.lancamento.LancamentoJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalancoRepositoryAdapterTest {

    @Mock
    private LancamentoJpaRepository jpaRepository;

    @InjectMocks
    private BalancoRepositoryAdapter adapter;

    private final LocalDate dataInicio = LocalDate.of(2021, 1, 1);
    private final LocalDate dataFim = LocalDate.of(2021, 1, 31);

    @Test
    void deveSomarReceitasSemFiltroDeCategoria() {
        when(jpaRepository.somarReceitas(dataInicio, dataFim, null)).thenReturn(new BigDecimal("2320.00"));

        BigDecimal resultado = adapter.somarReceitas(dataInicio, dataFim, null);

        assertThat(resultado).isEqualByComparingTo("2320.00");
        verify(jpaRepository).somarReceitas(dataInicio, dataFim, null);
    }

    @Test
    void deveSomarReceitasComFiltroDeCategoria() {
        when(jpaRepository.somarReceitas(dataInicio, dataFim, 1L)).thenReturn(new BigDecimal("1000.00"));

        BigDecimal resultado = adapter.somarReceitas(dataInicio, dataFim, 1L);

        assertThat(resultado).isEqualByComparingTo("1000.00");
        verify(jpaRepository).somarReceitas(dataInicio, dataFim, 1L);
    }

    @Test
    void deveSomarDespesasSemFiltroDeCategoria() {
        when(jpaRepository.somarDespesas(dataInicio, dataFim, null)).thenReturn(new BigDecimal("1000.00"));

        BigDecimal resultado = adapter.somarDespesas(dataInicio, dataFim, null);

        assertThat(resultado).isEqualByComparingTo("1000.00");
        verify(jpaRepository).somarDespesas(dataInicio, dataFim, null);
    }

    @Test
    void deveSomarDespesasComFiltroDeCategoria() {
        when(jpaRepository.somarDespesas(dataInicio, dataFim, 1L)).thenReturn(new BigDecimal("500.00"));

        BigDecimal resultado = adapter.somarDespesas(dataInicio, dataFim, 1L);

        assertThat(resultado).isEqualByComparingTo("500.00");
        verify(jpaRepository).somarDespesas(dataInicio, dataFim, 1L);
    }
}

