package com.financecontrol.api.domain.shared;

public record ParametrosPaginacao(int page, int size) {

	public ParametrosPaginacao {
		if (page < 0) {
			throw new NegocioException(
					MensagensErro.CODIGO_ERRO_VALIDACAO,
					MensagensErro.PAGINA_INVALIDA
			);
		}

		if (size <= 0) {
			throw new NegocioException(
					MensagensErro.CODIGO_ERRO_VALIDACAO,
					MensagensErro.TAMANHO_PAGINA_INVALIDO
			);
		}
	}
}

