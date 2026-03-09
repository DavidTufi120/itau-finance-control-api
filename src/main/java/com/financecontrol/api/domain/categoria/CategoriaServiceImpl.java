package com.financecontrol.api.domain.categoria;

import com.financecontrol.api.domain.lancamento.LancamentoRepositoryPort;
import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaServiceImpl.class);

    private final CategoriaRepositoryPort categoriaRepositoryPort;
    private final LancamentoRepositoryPort lancamentoRepositoryPort;

    public CategoriaServiceImpl(CategoriaRepositoryPort categoriaRepositoryPort,
                                LancamentoRepositoryPort lancamentoRepositoryPort) {
        this.categoriaRepositoryPort = categoriaRepositoryPort;
        this.lancamentoRepositoryPort = lancamentoRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Categoria nao encontrada. id={}", id);
                    return new NegocioException(
                            MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO,
                            MensagensErro.CATEGORIA_NAO_ENCONTRADA + id
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResultado<Categoria> listar(String nome, ParametrosPaginacao paginacao) {
        if (nome != null && !nome.isBlank()) {
            return categoriaRepositoryPort.findByNomeContendo(nome, paginacao);
        }
        return categoriaRepositoryPort.findAll(paginacao);
    }

    @Override
    @Transactional
    public Categoria criar(Categoria novaCategoria) {
        novaCategoria.validarNome();
        if (categoriaRepositoryPort.existsByNome(novaCategoria.getNome())) {
            logger.warn("Tentativa de criar categoria com nome duplicado. nome={}", novaCategoria.getNome());
            throw new NegocioException(
                    MensagensErro.CODIGO_NOME_DUPLICADO,
                    MensagensErro.CATEGORIA_NOME_DUPLICADO + novaCategoria.getNome()
            );
        }
        Categoria categoriaSalva = categoriaRepositoryPort.save(novaCategoria);
        logger.info("Categoria criada com sucesso. id={}, nome={}", categoriaSalva.getId(), categoriaSalva.getNome());
        return categoriaSalva;
    }

    @Override
    @Transactional
    public Categoria atualizar(Long id, Categoria dadosAtualizados) {
        dadosAtualizados.validarNome();
        Categoria categoriaExistente = categoriaRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Categoria nao encontrada para atualizar. id={}", id);
                    return new NegocioException(
                            MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO,
                            MensagensErro.CATEGORIA_NAO_ENCONTRADA + id
                    );
                });
        if (categoriaRepositoryPort.existsByNomeAndIdDiferente(dadosAtualizados.getNome(), id)) {
            logger.warn("Tentativa de atualizar categoria com nome duplicado. nome={}", dadosAtualizados.getNome());
            throw new NegocioException(
                    MensagensErro.CODIGO_NOME_DUPLICADO,
                    MensagensErro.CATEGORIA_NOME_DUPLICADO + dadosAtualizados.getNome()
            );
        }
        categoriaExistente.setNome(dadosAtualizados.getNome());
        Categoria categoriaAtualizada = categoriaRepositoryPort.save(categoriaExistente);
        logger.info("Categoria atualizada com sucesso. id={}, nome={}", categoriaAtualizada.getId(), categoriaAtualizada.getNome());
        return categoriaAtualizada;
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        categoriaRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Categoria nao encontrada para remover. id={}", id);
                    return new NegocioException(
                            MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO,
                            MensagensErro.CATEGORIA_NAO_ENCONTRADA + id
                    );
                });

        if (lancamentoRepositoryPort.existsByIdCategoria(id)) {
            logger.warn("Tentativa de remover categoria com lancamentos atrelados a subcategorias. id={}", id);
            throw new NegocioException(
                    MensagensErro.CODIGO_OPERACAO_NAO_PERMITIDA,
                    MensagensErro.CATEGORIA_COM_LANCAMENTOS
            );
        }

        categoriaRepositoryPort.deleteById(id);
        logger.info("Categoria removida com sucesso. id={}", id);
    }
}
