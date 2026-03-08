package com.financecontrol.api.adapters.out.persistence.categoria;

import com.financecontrol.api.domain.categoria.Categoria;
import org.springframework.stereotype.Component;

@Component
class CategoriaMapper {

    Categoria toDomain(CategoriaEntity entity) {
        return new Categoria(entity.getId(), entity.getNome());
    }

    CategoriaEntity toEntity(Categoria categoria) {
        return new CategoriaEntity(categoria.getId(), categoria.getNome());
    }
}

