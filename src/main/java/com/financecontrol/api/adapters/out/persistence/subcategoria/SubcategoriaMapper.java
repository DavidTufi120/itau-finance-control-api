package com.financecontrol.api.adapters.out.persistence.subcategoria;

import com.financecontrol.api.adapters.out.persistence.categoria.CategoriaEntity;
import com.financecontrol.api.domain.subcategoria.Subcategoria;
import org.springframework.stereotype.Component;

@Component
class SubcategoriaMapper {
    Subcategoria toDomain(SubcategoriaEntity entity) {
        return new Subcategoria(entity.getId(), entity.getNome(), entity.getCategoria().getId());
    }

    SubcategoriaEntity toEntity(Subcategoria subcategoria, CategoriaEntity categoriaEntity) {
        return new SubcategoriaEntity(subcategoria.getId(), subcategoria.getNome(), categoriaEntity);
    }
}
