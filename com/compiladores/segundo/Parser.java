
package com.compiladores.segundo;

import java.util.List;

import com.compiladores.Token;
import com.compiladores.TokenType;

public class Parser {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private final String programa;
    private final String nombrePrograma;
    private final List<Token> tokens;
    private Token cabeza;
    private int inicio;

    public Parser(List<Token> tokens, String programa, String nombrePrograma) {
        this.tokens = tokens;
        this.programa = programa;
        this.nombrePrograma = nombrePrograma;
    }

    public void parse() {
        cabeza = siguienteCabeza();
        mientras();
    }

    private void error() {
        StringBuilder sb = new StringBuilder();

        String contexto = obtenerContexto();
        int columna = cabeza.obtenerColumna();
        int linea = cabeza.obtenerLinea();

        sb.append(ANSI_RED);
        sb.append(nombrePrograma);
        sb.append(":");
        sb.append(linea + 1);
        sb.append(":");
        sb.append(columna + 1);
        sb.append(":");
        sb.append(" ");
        sb.append("[Error] No se esperaba token.");
        sb.append("\n");
        sb.append(contexto);
        sb.append("\n");
        sb.append(String.format("%" + (columna + 1) + "s", "^"));
        sb.append(ANSI_RESET);

        System.err.println(sb);

        System.exit(1);
    }

    private String obtenerContexto() {
        int linea = cabeza.obtenerLinea();
        return programa.split("\n")[linea];
    }

    private void mientras() {
        switch (cabeza.obtenerTipo()) {
            case MIENTRAS -> {
                asocia(TokenType.MIENTRAS);
                asocia(TokenType.OPEN_B);
                condicion();
                asocia(TokenType.CLOSE_B);
                bloque();
                asocia(TokenType.FIN_MIENTRAS);
            }
            default -> error();
        }
    }

    private void condicion() {
        switch (cabeza.obtenerTipo()) {
            case ID, NUM -> {
                expresion();
                operadorLogico();
                expresion();
            }
            default -> error();
        }
    }

    private void expresion() {
        switch (cabeza.obtenerTipo()) {
            case ID -> asocia(TokenType.ID);
            case NUM -> asocia(TokenType.NUM);
            default -> error();
        }
    }

    private void operadorLogico() {
        switch (cabeza.obtenerTipo()) {
            case OP_REL -> asocia(TokenType.OP_REL);
            default -> error();
        }
    }

    private void bloque() {
        switch (cabeza.obtenerTipo()) {
            case X, Y, MIENTRAS -> {
                instruccion();
                bloque();
            }
            default -> { }
        }
    }

    private void instruccion() {
        switch (cabeza.obtenerTipo()) {
            case X -> asocia(TokenType.X);
            case Y -> asocia(TokenType.Y);
            case MIENTRAS -> mientras();
            default -> error();
        }
    }

    private void asocia(TokenType tipo) {
        if (cabeza.obtenerTipo() == tipo) {
            cabeza = siguienteCabeza();
        } else {
            error();
        }
    }

    private Token siguienteCabeza() {
        return tokens.get(inicio++);
    }
}
