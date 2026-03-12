package com.financecontrol.api.domain.categoria;

import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;

public class Categoria {

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
        if (this.nome == null || this.nome.isBlank()) {
            throw new NegocioException(
                    MensagensErro.CODIGO_ERRO_VALIDACAO,
                    MensagensErro.CAMPO_NOME_OBRIGATORIO
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

