package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.request.SubcategoriaRequest;
import com.financecontrol.api.adapters.controller.response.PageResponse;
import com.financecontrol.api.adapters.controller.response.SubcategoriaResponse;
import com.financecontrol.api.domain.subcategoria.Subcategoria;
import com.financecontrol.api.domain.subcategoria.SubcategoriaService;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/subcategorias")
public class SubcategoriaController {
    private final SubcategoriaService subcategoriaService;

    public SubcategoriaController(SubcategoriaService subcategoriaService) {
        this.subcategoriaService = subcategoriaService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<SubcategoriaResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(name = "id_subcategoria", required = false) Long idSubcategoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ParametrosPaginacao paginacao = new ParametrosPaginacao(page, size);
        PaginaResultado<Subcategoria> resultado = subcategoriaService.listar(nome, idSubcategoria, paginacao);
        PageResponse<SubcategoriaResponse> resposta = PageResponse.de(resultado, this::toResponse);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubcategoriaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(subcategoriaService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<SubcategoriaResponse> criar(@RequestBody @Valid SubcategoriaRequest request) {
        Subcategoria subcategoriaCriada = subcategoriaService.criar(
                Subcategoria.criar(request.nome(), request.id_categoria()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(subcategoriaCriada.getId())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(subcategoriaCriada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubcategoriaResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid SubcategoriaRequest request) {
        Subcategoria subcategoriaAtualizada = subcategoriaService.atualizar(
                id, Subcategoria.criar(request.nome(), request.id_categoria()));
        return ResponseEntity.ok(toResponse(subcategoriaAtualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        subcategoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private SubcategoriaResponse toResponse(Subcategoria subcategoria) {
        return new SubcategoriaResponse(subcategoria.getId(), subcategoria.getNome(), subcategoria.getIdCategoria());
    }
}
