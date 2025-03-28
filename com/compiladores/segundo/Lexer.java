package com.compiladores.segundo;

import java.util.ArrayList;
import java.util.List;

import com.compiladores.Token;
import com.compiladores.TokenType;

public class Lexer {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private String nombrePrograma;
    private String programa;
    private int inicio;
    private int avance;
    private int estadoComienzo;
    private int estado;
    private int linea;
    private int columna;

    public Lexer(String nombrePrograma, String programa) {
        this.nombrePrograma = nombrePrograma;
        this.programa = programa;
    }

    public List<Token> tokenizar() {
        inicio = 0;
        avance = 0;
        linea = 0;
        columna = 0;

        List<Token> tokens = new ArrayList<>();

        while (avance < programa.length()) {
            estado = 0;
            estadoComienzo = 0;
            Token t = token();
            if (t != null) {
                tokens.add(t);
            }
        }

        return tokens;
    }

    private void error() {
        StringBuilder sb = new StringBuilder();

        String contexto = obtenerContexto();

        sb.append(ANSI_RED);
        sb.append(nombrePrograma);
        sb.append(":");
        sb.append(linea + 1);
        sb.append(":");
        sb.append(columna + 1);
        sb.append(":");
        sb.append(" ");
        sb.append("[Error] Token no reconocido.");
        sb.append("\n");
        sb.append(contexto);
        sb.append("\n");
        sb.append(String.format("%" + (columna + 1) + "s", "^"));
        sb.append(ANSI_RESET);

        System.err.println(sb);

        System.exit(1);
    }

    private String obtenerContexto() {
        return programa.split("\n")[linea];
    }

    private int diagrama() {
        columna -= (avance - inicio);
        avance = inicio;
        switch (estadoComienzo) {
            case 0 -> estadoComienzo = 3;
            case 3 -> estadoComienzo = 9;
            case 9 -> estadoComienzo = 12;
            case 12 -> estadoComienzo = 17;
            case 17 -> estadoComienzo = 21;
            case 21 -> error();
        }
        return estadoComienzo;
    }

    private Token token() {
        while (true) {
            switch (estado) {
                case 0 -> {
                    char c = leerCaracter();
                    if (Character.isLetter(c)) {
                        estado = 1;
                    } else {
                        estado = diagrama();
                    }
                }
                case 1 -> {
                    char c = leerCaracter();
                    if (Character.isLetter(c)) {
                        estado = 1;
                    } else if (Character.isDigit(c)) {
                        estado = 1;
                    } else {
                        estado = 2;
                    }
                }
                case 2 -> {
                    String lexema = programa.substring(inicio, avance - 1);
                    inicio = --avance;
                    columna--;
                    return switch (lexema) {
                        case "m" -> new Token(TokenType.MIENTRAS, lexema, linea, columna - lexema.length()); // mientras
                        case "fm" -> new Token(TokenType.FIN_MIENTRAS, lexema, linea, columna - lexema.length()); // fin mientras
                        case "x" -> new Token(TokenType.X, lexema, linea, columna - lexema.length()); // x
                        case "y" -> new Token(TokenType.Y, lexema, linea, columna - lexema.length()); // y
                        default -> new Token(TokenType.ID, lexema, linea, columna - lexema.length()); // id
                    };
                }
                case 3 -> {
                    char c = leerCaracter();
                    if (Character.isDigit(c)) {
                        estado = 4;
                    } else {
                        estado = diagrama();
                    }
                }
                case 4 -> {
                    char c = leerCaracter();
                    if (Character.isDigit(c)) {
                        estado = 4;
                    } else if (c == '.') {
                        estado = 5;
                    } else {
                        estado = 8;
                    }
                }
                case 5 -> {
                    char c = leerCaracter();
                    if (Character.isDigit(c)) {
                        estado = 6;
                    } else {
                        estado = diagrama();
                    }
                }
                case 6 -> {
                    char c = leerCaracter();
                    if (Character.isDigit(c)) {
                        estado = 6;
                    } else {
                        estado = 7;
                    }
                }
                case 7 -> {
                    String lexema = programa.substring(inicio, avance - 1);
                    inicio = --avance;
                    columna--;
                    return new Token(TokenType.NUM, lexema, linea, columna - lexema.length()); // num
                }
                case 8 -> {
                    String lexema = programa.substring(inicio, avance - 1);
                    inicio = --avance;
                    columna--;
                    return new Token(TokenType.NUM, lexema, linea, columna - lexema.length()); // num
                }
                case 9 -> {
                    char c = leerCaracter();
                    switch (c) {
                        case ' ', '\n', '\r', '\t' -> {
                            estado = 10;
                            if (c == '\n') {
                                linea++;
                                columna = 0;
                            }
                        }
                        default -> estado = diagrama();
                    }
                }
                case 10 -> {
                    char c = leerCaracter();
                    switch (c) {
                        case ' ', '\n', '\r', '\t' -> {
                            estado = 10;
                            if (c == '\n') {
                                linea++;
                                columna = 0;
                            }
                        }
                        default -> estado = 11;
                    }
                }
                case 11 -> {
                    inicio = --avance;
                    columna--;
                    return null; // delim
                }
                case 12 -> {
                    char c = leerCaracter();
                    switch (c) {
                        case '>' -> estado = 13;
                        case '<' -> estado = 14;
                        case '(' -> estado = 15;
                        case ')' -> estado = 16;
                        default -> estado = diagrama();
                    }
                }
                case 13 -> {
                    String lexema = programa.substring(inicio, avance);
                    inicio = avance;
                    return new Token(TokenType.OP_REL, lexema, linea, columna - lexema.length()); // mayor
                }
                case 14 -> {
                    String lexema = programa.substring(inicio, avance);
                    inicio = avance;
                    return new Token(TokenType.OP_REL, lexema, linea, columna - lexema.length()); // menor
                }
                case 15 -> {
                    String lexema = programa.substring(inicio, avance);
                    inicio = avance;
                    return new Token(TokenType.OPEN_B, lexema, linea, columna - lexema.length()); // parentesis que abre
                }
                case 16 -> {
                    String lexema = programa.substring(inicio, avance);
                    inicio = avance;
                    return new Token(TokenType.CLOSE_B, lexema, linea, columna - lexema.length()); // parentesis que cierra
                }
                case 17 -> {
                    char c = leerCaracter();
                    switch (c) {
                        case '/' -> estado = 18;
                        default -> estado = diagrama();
                    }
                }
                case 18 -> {
                    char c = leerCaracter();
                    switch (c) {
                        case '/' -> estado = 19;
                        default -> estado = diagrama();
                    }
                }
                case 19 -> {
                    char c = leerCaracter();
                    switch (c) {
                        case '\n', '\r' -> estado = 20;
                        default -> estado = 19;
                    }
                }
                case 20 -> {
                    inicio = --avance;
                    columna--;
                    return null; // comentario
                }
                case 21 -> {
                    char c = leerCaracter();
                    if (c == '\0') {
                        estado = 22;
                    } else {
                        estado = diagrama();
                    }
                }
                case 22 -> {
                    String lexema = programa.substring(inicio, avance);
                    inicio = avance;
                    return new Token(TokenType.EOF, lexema, linea, columna - lexema.length()); // eof
                }
            }
        }
    }

    private char leerCaracter() {
        columna++;
        return programa.charAt(avance++);
    }
}
