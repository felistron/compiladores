package com.compiladores;

public class Token {
    private TokenType tipo;
    private String lexema;
    private int linea, columna;

    public Token(TokenType tipo, String lexema, int linea, int columna) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linea = linea;
        this.columna = columna;
    }

    public TokenType obtenerTipo() {
        return this.tipo;
    }

    public String obtenerLexema() {
        return this.lexema;
    }

    public int obtenerLinea() {
        return this.linea;
    }

    public int obtenerColumna() {
        return this.columna;
    }

    @Override
    public String toString() {
        return (linea + 1) + ":" + (columna + 1) + ":" + tipo.name() + "(" + lexema + ")";
    }
}