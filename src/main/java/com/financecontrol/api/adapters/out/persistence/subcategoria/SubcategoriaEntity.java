package com.financecontrol.api.adapters.out.persistence.subcategoria;

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
import jakarta.persistence.UniqueConstraint;
import com.financecontrol.api.adapters.out.persistence.categoria.CategoriaEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(
        name = "subcategoria",
        indexes = {
                @Index(name = "idx_subcategoria_nome", columnList = "nome"),
                @Index(name = "idx_subcategoria_categoria_id", columnList = "categoria_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_subcategoria_nome_categoria", columnNames = {"nome", "categoria_id"})
        }
)
public class SubcategoriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "categoria_id", nullable = false, foreignKey = @ForeignKey(name = "fk_subcategoria_categoria"))
    private CategoriaEntity categoria;

    public SubcategoriaEntity(Long id, String nome, CategoriaEntity categoria) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
    }

    protected SubcategoriaEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    CategoriaEntity getCategoria() {
        return categoria;
    }

    void setNome(String nome) {
        this.nome = nome;
    }

    void setCategoria(CategoriaEntity categoria) {
        this.categoria = categoria;
    }
}
