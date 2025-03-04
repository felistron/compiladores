package com.compiladores;

public class Token {
    private TokenType tipo;
    private String lexema;
    private int fila, columna;

    public Token(TokenType tipo, String lexema, int fila, int columna) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.fila = fila;
        this.columna = columna;
    }

    @Override
    public String toString() {
        return (fila + 1) + ":" + (columna + 1) + ":" + tipo.name() + "(" + lexema + ")";
    }
}