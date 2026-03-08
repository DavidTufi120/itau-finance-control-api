package com.financecontrol.api.domain.shared;

public final class MensagensErro {

    private MensagensErro() {}

    public static final String CODIGO_RECURSO_NAO_ENCONTRADO = "recurso_nao_encontrado";
    public static final String CODIGO_NOME_DUPLICADO = "nome_duplicado";
    public static final String CODIGO_NOME_INVALIDO = "nome_invalido";

    public static final String CAMPO_NOME_OBRIGATORIO = "O campo 'nome' é obrigatório";
    public static final String CAMPO_NOME_TAMANHO_MAXIMO = "O campo 'nome' deve ter no maximo 100 caracteres";
    public static final String NOME_MUITO_CURTO = "O nome deve ter pelo menos 3 caracteres.";

    public static final String CATEGORIA_NAO_ENCONTRADA = "Categoria não encontrada com id: ";
    public static final String CATEGORIA_NOME_DUPLICADO = "Já existe uma categoria com o nome: ";
}

