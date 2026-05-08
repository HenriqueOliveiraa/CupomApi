package com.henrique.cupom.domain.exception;

public class CupomInvalidoException extends RuntimeException {

    public CupomInvalidoException(String mensagem) {
        super(mensagem);
    }
}
