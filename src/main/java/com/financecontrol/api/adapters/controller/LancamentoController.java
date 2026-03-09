package com.financecontrol.api.adapters.controller;
import com.financecontrol.api.adapters.controller.request.LancamentoRequest;
import com.financecontrol.api.adapters.controller.response.LancamentoResponse;
import com.financecontrol.api.adapters.controller.response.PageResponse;
import com.financecontrol.api.domain.lancamento.Lancamento;
import com.financecontrol.api.domain.lancamento.LancamentoService;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.time.LocalDate;
@RestController
@RequestMapping("/v1/lancamentos")
public class LancamentoController implements LancamentoControllerDocs {
    private final LancamentoService lancamentoService;
    public LancamentoController(LancamentoService lancamentoService) {
        this.lancamentoService = lancamentoService;
    }


    @GetMapping
    public ResponseEntity<PageResponse<LancamentoResponse>> listar(
            @RequestParam(name = "id_subcategoria", required = false) Long idSubcategoria,
            @RequestParam(name = "data_inicio", required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,
            @RequestParam(name = "data_fim", required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ParametrosPaginacao paginacao = new ParametrosPaginacao(page, size);
        PaginaResultado<Lancamento> resultado = lancamentoService.listar(idSubcategoria, dataInicio, dataFim, paginacao);
        return ResponseEntity.ok(PageResponse.de(resultado, this::toResponse));
    }


    @GetMapping("/{id}")
    public ResponseEntity<LancamentoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(lancamentoService.buscarPorId(id)));
    }


    @PostMapping
    public ResponseEntity<LancamentoResponse> criar(@RequestBody @Valid LancamentoRequest request) {
        Lancamento lancamentoCriado = lancamentoService.criar(
                Lancamento.criar(request.valor(), request.data(), request.idSubcategoria(), request.comentario()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(lancamentoCriado.getId())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(lancamentoCriado));
    }


    @PutMapping("/{id}")
    public ResponseEntity<LancamentoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid LancamentoRequest request) {
        Lancamento lancamentoAtualizado = lancamentoService.atualizar(
                id, Lancamento.criar(request.valor(), request.data(), request.idSubcategoria(), request.comentario()));
        return ResponseEntity.ok(toResponse(lancamentoAtualizado));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        lancamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
    private LancamentoResponse toResponse(Lancamento lancamento) {
        return new LancamentoResponse(
                lancamento.getId(),
                lancamento.getValor(),
                lancamento.getData(),
                lancamento.getIdSubcategoria(),
                lancamento.getComentario()
        );
    }
}