package com.financecontrol.api.domain.balanco;

import com.financecontrol.api.domain.categoria.Categoria;
import com.financecontrol.api.domain.categoria.CategoriaRepositoryPort;
import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class BalancoServiceImpl implements BalancoService {

    private static final Logger logger = LoggerFactory.getLogger(BalancoServiceImpl.class);

    private final BalancoRepositoryPort balancoRepositoryPort;
    private final CategoriaRepositoryPort categoriaRepositoryPort;

    public BalancoServiceImpl(BalancoRepositoryPort balancoRepositoryPort,
                              CategoriaRepositoryPort categoriaRepositoryPort) {
        this.balancoRepositoryPort = balancoRepositoryPort;
        this.categoriaRepositoryPort = categoriaRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Balanco calcular(LocalDate dataInicio, LocalDate dataFim, Long idCategoria) {
        Long idCategoriaFiltro = null;
        String nomeCategoria = null;

        if (idCategoria != null) {
            Categoria categoria = categoriaRepositoryPort.findById(idCategoria)
                    .orElseThrow(() -> {
                        logger.warn("Categoria nao encontrada para calculo de balanco. id={}", idCategoria);
                        return new NegocioException(
                                MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO,
                                MensagensErro.CATEGORIA_NAO_ENCONTRADA + idCategoria
                        );
                    });
            idCategoriaFiltro = categoria.getId();
            nomeCategoria = categoria.getNome();
        }

        BigDecimal receita = balancoRepositoryPort.somarReceitas(dataInicio, dataFim, idCategoriaFiltro);
        BigDecimal despesa = balancoRepositoryPort.somarDespesas(dataInicio, dataFim, idCategoriaFiltro);
        BigDecimal saldo = receita.subtract(despesa);

        logger.info("Balanco calculado. dataInicio={}, dataFim={}, idCategoria={}, receita={}, despesa={}, saldo={}",
                dataInicio, dataFim, idCategoria, receita, despesa, saldo);

        return new Balanco(idCategoriaFiltro, nomeCategoria, receita, despesa, saldo);
    }
}

