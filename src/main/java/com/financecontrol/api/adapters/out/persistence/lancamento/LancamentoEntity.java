package com.financecontrol.api.adapters.out.persistence.lancamento;

import com.financecontrol.api.adapters.out.persistence.subcategoria.SubcategoriaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "lancamentos",
        indexes = {
                @Index(name = "idx_lancamento_data", columnList = "data"),
                @Index(name = "idx_lancamento_subcategoria_id", columnList = "subcategoria_id")
        }
)
class LancamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate data;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "subcategoria_id", nullable = false, foreignKey = @ForeignKey(name = "fk_lancamento_subcategoria"))
    private SubcategoriaEntity subcategoria;

    @Column(length = 500)
    private String comentario;

    LancamentoEntity(Long id, BigDecimal valor, LocalDate data, SubcategoriaEntity subcategoria, String comentario) {
        this.id = id;
        this.valor = valor;
        this.data = data;
        this.subcategoria = subcategoria;
        this.comentario = comentario;
    }

    protected LancamentoEntity() {
    }

    Long getId() {
        return id;
    }

    BigDecimal getValor() {
        return valor;
    }

    LocalDate getData() {
        return data;
    }

    SubcategoriaEntity getSubcategoria() {
        return subcategoria;
    }

    String getComentario() {
        return comentario;
    }

    void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    void setData(LocalDate data) {
        this.data = data;
    }

    void setSubcategoria(SubcategoriaEntity subcategoria) {
        this.subcategoria = subcategoria;
    }

    void setComentario(String comentario) {
        this.comentario = comentario;
    }
}

