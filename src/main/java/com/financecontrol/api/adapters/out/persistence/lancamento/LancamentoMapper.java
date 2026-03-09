package com.financecontrol.api.adapters.out.persistence.lancamento;

import com.financecontrol.api.adapters.out.persistence.subcategoria.SubcategoriaEntity;
import com.financecontrol.api.domain.lancamento.Lancamento;
import org.springframework.stereotype.Component;

@Component
class LancamentoMapper {

    Lancamento toDomain(LancamentoEntity entity) {
        return new Lancamento(
                entity.getId(),
                entity.getValor(),
                entity.getData(),
                entity.getSubcategoria().getId(),
                entity.getComentario()
        );
    }

    LancamentoEntity toEntity(Lancamento lancamento, SubcategoriaEntity subcategoriaEntity) {
        return new LancamentoEntity(
                lancamento.getId(),
                lancamento.getValor(),
                lancamento.getData(),
                subcategoriaEntity,
                lancamento.getComentario()
        );
    }
}

