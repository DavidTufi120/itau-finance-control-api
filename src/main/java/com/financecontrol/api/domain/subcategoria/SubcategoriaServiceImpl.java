package com.financecontrol.api.domain.subcategoria;

import com.financecontrol.api.domain.categoria.Categoria;
import com.financecontrol.api.domain.categoria.CategoriaRepositoryPort;
import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubcategoriaServiceImpl implements SubcategoriaService {
    private static final Logger logger = LoggerFactory.getLogger(SubcategoriaServiceImpl.class);
    private final SubcategoriaRepositoryPort subcategoriaRepositoryPort;
    private final CategoriaRepositoryPort categoriaRepositoryPort;

    public SubcategoriaServiceImpl(SubcategoriaRepositoryPort subcategoriaRepositoryPort,
                                   CategoriaRepositoryPort categoriaRepositoryPort) {
        this.subcategoriaRepositoryPort = subcategoriaRepositoryPort;
        this.categoriaRepositoryPort = categoriaRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Subcategoria buscarPorId(Long id) {
        return subcategoriaRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Subcategoria nao encontrada. id={}", id);
                    return new NegocioException(
                            MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO,
                            MensagensErro.SUBCATEGORIA_NAO_ENCONTRADA + id
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResultado<Subcategoria> listar(String nome, Long idSubcategoria, ParametrosPaginacao paginacao) {
        boolean temNome = nome != null && !nome.isBlank();
        boolean temId = idSubcategoria != null;

        if (temNome && temId) {
            return subcategoriaRepositoryPort.findByNomeContendoEId(nome, idSubcategoria, paginacao);
        }
        if (temNome) {
            return subcategoriaRepositoryPort.findByNomeContendo(nome, paginacao);
        }
        if (temId) {
            return subcategoriaRepositoryPort.findByIdPaginado(idSubcategoria, paginacao);
        }
        return subcategoriaRepositoryPort.findAll(paginacao);
    }

    @Override
    @Transactional
    public Subcategoria criar(Subcategoria novaSubcategoria) {
        novaSubcategoria.validarNome();
        Categoria categoria = categoriaRepositoryPort.findById(novaSubcategoria.getIdCategoria())
                .orElseThrow(() -> {
                    logger.warn("Tentativa de criar subcategoria com categoria inexistente. idCategoria={}",
                            novaSubcategoria.getIdCategoria());
                    return new NegocioException(
                            MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO,
                            MensagensErro.CATEGORIA_NAO_ENCONTRADA + novaSubcategoria.getIdCategoria()
                    );
                });
        if (subcategoriaRepositoryPort.existsByNomeAndIdCategoria(novaSubcategoria.getNome(), categoria.getId())) {
            logger.warn("Tentativa de criar subcategoria com nome duplicado na categoria. nome={}, idCategoria={}",
                    novaSubcategoria.getNome(), categoria.getId());
            throw new NegocioException(
                    MensagensErro.CODIGO_NOME_DUPLICADO,
                    MensagensErro.SUBCATEGORIA_NOME_DUPLICADO + novaSubcategoria.getNome()
            );
        }
        Subcategoria subcategoriaSalva = subcategoriaRepositoryPort.save(novaSubcategoria);
        logger.info("Subcategoria criada com sucesso. id={}, nome={}, idCategoria={}",
                subcategoriaSalva.getId(), subcategoriaSalva.getNome(), subcategoriaSalva.getIdCategoria());
        return subcategoriaSalva;
    }

    @Override
    @Transactional
    public Subcategoria atualizar(Long id, Subcategoria dadosAtualizados) {
        dadosAtualizados.validarNome();

        Subcategoria subcategoriaExistente = subcategoriaRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Subcategoria nao encontrada para atualizar. id={}", id);
                    return new NegocioException(
                            MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO,
                            MensagensErro.SUBCATEGORIA_NAO_ENCONTRADA + id
                    );
                });

        Long novoIdCategoria = dadosAtualizados.getIdCategoria();

        categoriaRepositoryPort.findById(novoIdCategoria)
                .orElseThrow(() -> {
                    logger.warn("Tentativa de atualizar subcategoria com categoria inexistente. idCategoria={}", novoIdCategoria);
                    return new NegocioException(
                            MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO,
                            MensagensErro.CATEGORIA_NAO_ENCONTRADA + novoIdCategoria
                    );
                });

        if (subcategoriaRepositoryPort.existsByNomeAndIdCategoriaAndIdDiferente(
                dadosAtualizados.getNome(), novoIdCategoria, id)) {
            logger.warn("Tentativa de atualizar subcategoria com nome duplicado na categoria. nome={}, idCategoria={}",
                    dadosAtualizados.getNome(), novoIdCategoria);
            throw new NegocioException(
                    MensagensErro.CODIGO_NOME_DUPLICADO,
                    MensagensErro.SUBCATEGORIA_NOME_DUPLICADO + dadosAtualizados.getNome()
            );
        }

        subcategoriaExistente.setNome(dadosAtualizados.getNome());
        subcategoriaExistente.setIdCategoria(novoIdCategoria);
        Subcategoria subcategoriaAtualizada = subcategoriaRepositoryPort.save(subcategoriaExistente);
        logger.info("Subcategoria atualizada com sucesso. id={}, nome={}, idCategoria={}",
                subcategoriaAtualizada.getId(), subcategoriaAtualizada.getNome(), subcategoriaAtualizada.getIdCategoria());
        return subcategoriaAtualizada;
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        subcategoriaRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Subcategoria nao encontrada para remover. id={}", id);
                    return new NegocioException(
                            MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO,
                            MensagensErro.SUBCATEGORIA_NAO_ENCONTRADA + id
                    );
                });
        subcategoriaRepositoryPort.deleteById(id);
        logger.info("Subcategoria removida com sucesso. id={}", id);
    }
}
