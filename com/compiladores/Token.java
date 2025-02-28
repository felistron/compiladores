package com.compiladores;

public class Token {
    private TokenType tipo;
    private String lexema;

    public Token(TokenType tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
    }

    @Override
    public String toString() {
        return tipo.name() + "(" + lexema + ")";
    }
}