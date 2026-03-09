package com.financecontrol.api.domain.lancamento;

import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Lancamento {

    private final Long id;
    private final BigDecimal valor;
    private final LocalDate data;
    private final Long idSubcategoria;
    @Nullable
    private final String comentario;

    public Lancamento(Long id, BigDecimal valor, LocalDate data, Long idSubcategoria, @Nullable String comentario) {
        this.id = id;
        this.valor = valor;
        this.data = data;
        this.idSubcategoria = idSubcategoria;
        this.comentario = comentario;
    }

    public static Lancamento criar(BigDecimal valor, LocalDate data, Long idSubcategoria, @Nullable String comentario) {
        LocalDate dataLancamento = data != null ? data : LocalDate.now();
        return new Lancamento(null, valor, dataLancamento, idSubcategoria, comentario);
    }

    public void validarValor() {
        if (this.valor == null || this.valor.compareTo(BigDecimal.ZERO) == 0) {
            throw new NegocioException(
                    MensagensErro.CODIGO_VALOR_INVALIDO,
                    MensagensErro.LANCAMENTO_VALOR_ZERO
            );
        }
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getData() {
        return data;
    }

    public Long getIdSubcategoria() {
        return idSubcategoria;
    }

    @Nullable
    public String getComentario() {
        return comentario;
    }
}
