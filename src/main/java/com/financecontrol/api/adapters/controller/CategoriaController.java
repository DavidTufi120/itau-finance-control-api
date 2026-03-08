package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.request.CategoriaRequest;
import com.financecontrol.api.adapters.controller.response.CategoriaResponse;
import com.financecontrol.api.adapters.controller.response.PageResponse;
import com.financecontrol.api.domain.categoria.Categoria;
import com.financecontrol.api.domain.categoria.CategoriaService;
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
@RequestMapping("/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<CategoriaResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ParametrosPaginacao paginacao = new ParametrosPaginacao(page, size);
        PaginaResultado<Categoria> resultado = categoriaService.listar(nome, paginacao);
        PageResponse<CategoriaResponse> resposta = PageResponse.de(resultado, this::toResponse);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(categoriaService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(@RequestBody @Valid CategoriaRequest request) {
        Categoria categoriaCriada = categoriaService.criar(Categoria.criar(request.nome()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoriaCriada.getId())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(categoriaCriada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CategoriaRequest request) {
        Categoria categoriaAtualizada = categoriaService.atualizar(id, Categoria.criar(request.nome()));
        return ResponseEntity.ok(toResponse(categoriaAtualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNome());
    }
}