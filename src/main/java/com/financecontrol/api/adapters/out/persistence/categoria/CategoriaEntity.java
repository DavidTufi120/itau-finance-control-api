package com.financecontrol.api.adapters.out.persistence.categoria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "categorias", indexes = {
        @Index(name = "idx_categoria_nome", columnList = "nome")
})
class CategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    protected CategoriaEntity() {
    }

    CategoriaEntity(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    Long getId() {
        return id;
    }

    String getNome() {
        return nome;
    }

    void setNome(String nome) {
        this.nome = nome;
    }
}

