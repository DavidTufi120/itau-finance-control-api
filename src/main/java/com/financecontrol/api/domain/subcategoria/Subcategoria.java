package com.financecontrol.api.domain.subcategoria;

import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;

public class Subcategoria {
    private static final int NOME_TAMANHO_MINIMO = 3;
    private final Long id;
    private String nome;
    private Long idCategoria;

    public Subcategoria(Long id, String nome, Long idCategoria) {
        this.id = id;
        this.nome = nome;
        this.idCategoria = idCategoria;
    }

    public static Subcategoria criar(String nome, Long idCategoria) {
        return new Subcategoria(null, nome, idCategoria);
    }

    public void validarNome() {
        if (this.nome == null || this.nome.trim().length() < NOME_TAMANHO_MINIMO) {
            throw new NegocioException(
                    MensagensErro.CODIGO_NOME_INVALIDO,
                    MensagensErro.NOME_MUITO_CURTO
            );
        }
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }
}
