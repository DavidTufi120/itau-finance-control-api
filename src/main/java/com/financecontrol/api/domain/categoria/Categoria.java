package com.financecontrol.api.domain.categoria;

import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;

public class Categoria {

    private static final int NOME_TAMANHO_MINIMO = 3;

    private final Long id;
    private String nome;

    public Categoria(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public static Categoria criar(String nome) {
        return new Categoria(null, nome);
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

    public void setNome(String nome) {
        this.nome = nome;
    }
}

