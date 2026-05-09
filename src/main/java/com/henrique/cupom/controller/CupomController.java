package com.henrique.cupom.controller;

import com.henrique.cupom.dto.CriarCupomRequest;
import com.henrique.cupom.dto.CupomResponse;
import com.henrique.cupom.service.CupomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cupom")
@Tag(name = "Cupom Api", description = "Gerenciamento de cupons de desconto")
public class CupomController {

    private final CupomService service;

    public CupomController(CupomService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
            summary = "Criar cupom",
            description = "Cria um novo cupom de desconto. Caracteres especiais no código são removidos automaticamente.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
                    @ApiResponse(responseCode = "422", description = "Violação de regra de negócio")
            }
    )
    public ResponseEntity<CupomResponse> criar(@Valid @RequestBody CriarCupomRequest request) {
        CupomResponse response = CupomResponse.from(service.criar(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar cupom",
            description = "Realiza soft delete do cupom. O registro é mantido no banco com status DELETADO.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Cupom deletado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cupom não encontrado"),
                    @ApiResponse(responseCode = "409", description = "Cupom já foi deletado")
            }
    )
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
