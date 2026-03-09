package com.financecontrol.api.domain.lancamento;

import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;
import com.financecontrol.api.domain.subcategoria.SubcategoriaRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    private static final Logger logger = LoggerFactory.getLogger(LancamentoServiceImpl.class);

    private final LancamentoRepositoryPort lancamentoRepositoryPort;
    private final SubcategoriaRepositoryPort subcategoriaRepositoryPort;

    public LancamentoServiceImpl(LancamentoRepositoryPort lancamentoRepositoryPort,
                                 SubcategoriaRepositoryPort subcategoriaRepositoryPort) {
        this.lancamentoRepositoryPort = lancamentoRepositoryPort;
        this.subcategoriaRepositoryPort = subcategoriaRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Lancamento buscarPorId(Long id) {
        return lancamentoRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Lancamento nao encontrado. id={}", id);
                    return new NegocioException(
                            MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO,
                            MensagensErro.LANCAMENTO_NAO_ENCONTRADO + id
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResultado<Lancamento> listar(Long idSubcategoria, LocalDate dataInicio, LocalDate dataFim, ParametrosPaginacao paginacao) {
        boolean temFiltros = idSubcategoria != null || dataInicio != null || dataFim != null;

        if (temFiltros) {
            return lancamentoRepositoryPort.findByFiltros(idSubcategoria, dataInicio, dataFim, paginacao);
        }
        return lancamentoRepositoryPort.findAll(paginacao);
    }

    @Override
    @Transactional
    public Lancamento criar(Lancamento novoLancamento) {
        novoLancamento.validarValor();
        validarSubcategoria(novoLancamento.getIdSubcategoria());

        Lancamento lancamentoSalvo = lancamentoRepositoryPort.save(novoLancamento);
        logger.info("Lancamento criado com sucesso. id={}, valor={}, idSubcategoria={}",
                lancamentoSalvo.getId(), lancamentoSalvo.getValor(), lancamentoSalvo.getIdSubcategoria());
        return lancamentoSalvo;
    }

    @Override
    @Transactional
    public Lancamento atualizar(Long id, Lancamento dadosAtualizados) {
        dadosAtualizados.validarValor();

        lancamentoRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Lancamento nao encontrado para atualizar. id={}", id);
                    return new NegocioException(
                            MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO,
                            MensagensErro.LANCAMENTO_NAO_ENCONTRADO + id
                    );
                });

        validarSubcategoria(dadosAtualizados.getIdSubcategoria());

        LocalDate data = dadosAtualizados.getData() != null ? dadosAtualizados.getData() : LocalDate.now();

        Lancamento lancamentoAtualizado = lancamentoRepositoryPort.save(
                new Lancamento(id, dadosAtualizados.getValor(), data,
                        dadosAtualizados.getIdSubcategoria(), dadosAtualizados.getComentario())
        );
        logger.info("Lancamento atualizado com sucesso. id={}", id);
        return lancamentoAtualizado;
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        lancamentoRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Lancamento nao encontrado para remover. id={}", id);
                    return new NegocioException(
                            MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO,
                            MensagensErro.LANCAMENTO_NAO_ENCONTRADO + id
                    );
                });
        lancamentoRepositoryPort.deleteById(id);
        logger.info("Lancamento removido com sucesso. id={}", id);
    }

    private void validarSubcategoria(Long idSubcategoria) {
        subcategoriaRepositoryPort.findById(idSubcategoria)
                .orElseThrow(() -> {
                    logger.warn("Tentativa de criar/atualizar lancamento com subcategoria inexistente. idSubcategoria={}", idSubcategoria);
                    return new NegocioException(
                            MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO,
                            MensagensErro.SUBCATEGORIA_NAO_ENCONTRADA + idSubcategoria
                    );
                });
    }
}

