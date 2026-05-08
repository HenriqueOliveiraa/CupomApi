package com.henrique.cupom.domain.exception;

public class CupomNaoEncontradoException extends RuntimeException {

    public CupomNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
