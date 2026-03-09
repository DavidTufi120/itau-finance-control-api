package com.financecontrol.api.domain.shared;

public final class MensagensErro {

    private MensagensErro() {}

    public static final String CODIGO_RECURSO_NAO_ENCONTRADO = "recurso_nao_encontrado";
    public static final String CODIGO_NOME_DUPLICADO = "nome_duplicado";
    public static final String CODIGO_NOME_INVALIDO = "nome_invalido";
    public static final String CODIGO_OPERACAO_NAO_PERMITIDA = "operacao_nao_permitida";
    public static final String CODIGO_VALOR_INVALIDO = "valor_invalido";

    public static final String CAMPO_NOME_OBRIGATORIO = "O campo 'nome' é obrigatório";
    public static final String CAMPO_NOME_TAMANHO_MAXIMO = "O campo 'nome' deve ter no maximo 100 caracteres";
    public static final String NOME_MUITO_CURTO = "O nome deve ter pelo menos 3 caracteres.";

    public static final String CATEGORIA_NAO_ENCONTRADA = "Categoria não encontrada com id: ";
    public static final String CATEGORIA_NOME_DUPLICADO = "Já existe uma categoria com o nome: ";

    public static final String SUBCATEGORIA_NAO_ENCONTRADA = "Subcategoria não encontrada com id: ";
    public static final String SUBCATEGORIA_NOME_DUPLICADO = "Já existe uma subcategoria com o nome informado nesta categoria: ";
    public static final String SUBCATEGORIA_COM_LANCAMENTOS = "Não é possível remover a subcategoria pois existem lançamentos atrelados a ela.";
    public static final String CATEGORIA_COM_LANCAMENTOS = "Não é possível remover a categoria pois uma ou mais subcategorias possuem lançamentos atrelados.";

    public static final String LANCAMENTO_NAO_ENCONTRADO = "Lançamento não encontrado com id: ";
    public static final String LANCAMENTO_VALOR_ZERO = "O valor do lançamento deve ser diferente de zero.";
}
