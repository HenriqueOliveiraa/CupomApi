package com.henrique.cupom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.henrique.cupom.domain.Cupom;
import com.henrique.cupom.domain.exception.CupomInvalidoException;
import com.henrique.cupom.domain.exception.CupomJaDeletadoException;
import com.henrique.cupom.domain.exception.CupomNaoEncontradoException;
import com.henrique.cupom.dto.CriarCupomRequest;
import com.henrique.cupom.exception.GlobalExceptionHandler;
import com.henrique.cupom.service.CupomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CupomControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CupomService service;

    @InjectMocks
    private CupomController controller;

    private ObjectMapper objectMapper;
    private final Instant amanha = Instant.now().plus(1, ChronoUnit.DAYS);

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(converter)
                .build();
    }

    private Cupom cupomAtivo() {
        return Cupom.criar("ABC123", "Desconto 10%", new BigDecimal("10.00"), amanha, false);
    }

    @Test
    void deveCriarCupomComSucesso() throws Exception {
        when(service.criar(any())).thenReturn(cupomAtivo());

        CriarCupomRequest request = new CriarCupomRequest(
                "ABC123", "Desconto 10%", new BigDecimal("10.00"), amanha, false);

        mockMvc.perform(post("/cupom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveCriarCupomPublicadoComSucesso() throws Exception {
        Cupom cupom = Cupom.criar("ABC123", "Desconto", new BigDecimal("5.00"), amanha, true);
        when(service.criar(any())).thenReturn(cupom);

        CriarCupomRequest request = new CriarCupomRequest(
                "ABC123", "Desconto", new BigDecimal("5.00"), amanha, true);

        mockMvc.perform(post("/cupom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.published").value(true));
    }

    @Test
    void deveRetornar422QuandoRegraDeNegocioViolada() throws Exception {
        when(service.criar(any())).thenThrow(new CupomInvalidoException("Desconto abaixo do mínimo"));

        CriarCupomRequest request = new CriarCupomRequest(
                "ABC123", "Desconto", new BigDecimal("0.1"), amanha, false);

        mockMvc.perform(post("/cupom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveDeletarCupomComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(service).deletar(id);

        mockMvc.perform(delete("/cupom/" + id))
                .andExpect(status().isNoContent());

        verify(service).deletar(eq(id));
    }

    @Test
    void deveRetornar404AoDeletarCupomInexistente() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new CupomNaoEncontradoException("Cupom não encontrado"))
                .when(service).deletar(id);

        mockMvc.perform(delete("/cupom/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar409AoDeletarCupomJaDeletado() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new CupomJaDeletadoException("Cupom já deletado"))
                .when(service).deletar(id);

        mockMvc.perform(delete("/cupom/" + id))
                .andExpect(status().isConflict());
    }
}
