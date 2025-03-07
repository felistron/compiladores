/*
 * Autor: Fernando Espinosa
 * Fecha: 2025/02/25
 */

package com.compiladores;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;

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

    private static String nombrePrograma;
    private static String programa;
    private static int inicio;
    private static int avance;
    private static int estadoComienzo;
    private static int estado;
    private static int linea;
    private static int columna;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println(ANSI_RED + "[ERROR] Se esperaba el nombre del archivo." + ANSI_RESET);
            System.exit(1);
        }

        nombrePrograma = args[0];

        try {
            programa = leerArchivo(nombrePrograma) + "\0"; 
        } catch (FileNotFoundException e) {
            System.err.println(ANSI_RED + "[ERROR] El archivo no existe." + ANSI_RESET);
            System.exit(1);
        } catch (IOException e) {
            System.err.println(ANSI_RED + "[ERROR] No se pudo leer el archivo." + ANSI_RESET);
            System.exit(1);
        }

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

        System.out.println(ANSI_GREEN + "Análisis léxico terminado correctamente" + ANSI_RESET);

        for (Token t: tokens) {
            System.out.println(t);
        }
    }

    private static String leerArchivo(String nombre) throws FileNotFoundException, IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(nombre))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        }
    }

    private static void error() {
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

    private static String obtenerContexto() {
        return programa.split("\n")[linea];
    }

    private static int diagrama() {
        columna -= (avance - inicio);
        avance = inicio;
        switch (estadoComienzo) {
            case 0 -> estadoComienzo = 12;
            case 12 -> estadoComienzo = 15;
            case 15 -> estadoComienzo = 20;
            case 20 -> estadoComienzo = 23;
            case 23 -> estadoComienzo = 28;
            case 28 -> estadoComienzo = 31;
            case 31 -> error();
        }
        return estadoComienzo;
    }

    private static Token token() {
        while (true) {
            switch (estado) {
                case 0 -> {
                    char c = leerCaracter();
                    switch (c) {
                        case '!' -> estado = 1;
                        case '>' -> estado = 3;
                        case '=' -> estado = 6;
                        case '<' -> estado = 9;
                        default -> estado = diagrama();
                    }
                }
                case 1 -> {
                    char c = leerCaracter();
                    switch (c) {
                        case '=' -> estado = 2;
                        default -> estado = diagrama();
                    }
                }
                case 2 -> {
                    String lexema = programa.substring(inicio, avance);
                    inicio = avance;
                    return new Token(TokenType.OP_REL, lexema, linea, columna - lexema.length()); // dif
                }
                case 3 -> {
                    char c = leerCaracter();
                    switch (c) {
                        case '=' -> estado = 4;
                        default -> estado = 5;
                    }
                }
                case 4 -> {
                    String lexema = programa.substring(inicio, avance);
                    inicio = avance;
                    return new Token(TokenType.OP_REL, lexema, linea, columna - lexema.length()); // mayor igual
                }
                case 5 -> {
                    String lexema = programa.substring(inicio, avance - 1);
                    inicio = --avance;
                    columna--;
                    return new Token(TokenType.OP_REL, lexema, linea, columna - lexema.length()); // mayor
                }
                case 6 -> {
                    char c = leerCaracter();
                    switch (c) {
                        case '=' -> estado = 7;
                        default -> estado = 8;
                    }
                }
                case 7 -> {
                    String lexema = programa.substring(inicio, avance);
                    inicio = avance;
                    return new Token(TokenType.OP_REL, lexema, linea, columna - lexema.length()); // igual
                }
                case 8 -> {
                    String lexema = programa.substring(inicio, avance - 1);
                    inicio = --avance;
                    columna--;
                    return new Token(TokenType.ASIG, lexema, linea, columna - lexema.length()); // asignación
                }
                case 9 -> {
                    char c = leerCaracter();
                    switch (c) {
                        case '=' -> estado = 10;
                        default -> estado = 11;
                    }
                }
                case 10 -> {
                    String lexema = programa.substring(inicio, avance);
                    inicio = avance;
                    return new Token(TokenType.OP_REL, lexema, linea, columna - lexema.length()); // menor igual
                }
                case 11 -> {
                    String lexema = programa.substring(inicio, avance - 1);
                    inicio = --avance;
                    columna--;
                    return new Token(TokenType.OP_REL, lexema, linea, columna - lexema.length()); // menor
                }
                case 12 -> {
                    char c = leerCaracter();
                    if (Character.isLetter(c)) {
                        estado = 13;
                    } else {
                        estado = diagrama();
                    }
                }
                case 13 -> {
                    char c = leerCaracter();
                    if (Character.isLetter(c)) {
                        estado = 13;
                    } else if (Character.isDigit(c)) {
                        estado = 13;
                    } else {
                        estado = 14;
                    }
                }
                case 14 -> {
                    String lexema = programa.substring(inicio, avance - 1);
                    inicio = --avance;
                    columna--;
                    return new Token(TokenType.ID, lexema, linea, columna - lexema.length()); // id
                }
                case 15 -> {
                    char c = leerCaracter();
                    if (Character.isDigit(c)) {
                        estado = 16;
                    } else {
                        estado = diagrama();
                    }
                }
                case 16 -> {
                    char c = leerCaracter();
                    if (Character.isDigit(c)) {
                        estado = 16;
                    } else if (c == '.') {
                        estado = 17;
                    } else {
                        estado = diagrama();
                    }
                }
                case 17 -> {
                    char c = leerCaracter();
                    if (Character.isDigit(c)) {
                        estado = 18;
                    } else {
                        estado = diagrama();
                    }
                }
                case 18 -> {
                    char c = leerCaracter();
                    if (Character.isDigit(c)) {
                        estado = 18;
                    } else {
                        estado = 19;
                    }
                }
                case 19 -> {
                    String lexema = programa.substring(inicio, avance - 1);
                    inicio = --avance;
                    columna--;
                    return new Token(TokenType.NUM, lexema, linea, columna - lexema.length()); // número
                }
                case 20 -> {
                    char c = leerCaracter();
                    if (Character.isDigit(c)) {
                        estado = 21;
                    } else {
                        estado = diagrama();
                    }
                }
                case 21 -> {
                    char c = leerCaracter();
                    if (Character.isDigit(c)) {
                        estado = 21;
                    } else {
                        estado = 22;
                    }
                }
                case 22 -> {
                    String lexema = programa.substring(inicio, avance - 1);
                    inicio = --avance;
                    columna--;
                    return new Token(TokenType.NUM, lexema, linea, columna - lexema.length()); // número
                }
                case 23 -> {
                    char c = leerCaracter();
                    switch (c) {
                        case '+' -> estado = 24;
                        case '-' -> estado = 25;
                        case '*' -> estado = 26;
                        case '/' -> estado = 27;
                        default -> estado = diagrama();
                    }
                }
                case 24 -> {
                    String lexema = programa.substring(inicio, avance);
                    inicio = avance;
                    return new Token(TokenType.BIN_OP, lexema, linea, columna - lexema.length()); // sum
                }
                case 25 -> {
                    String lexema = programa.substring(inicio, avance);
                    inicio = avance;
                    return new Token(TokenType.BIN_OP, lexema, linea, columna - lexema.length()); // res
                }
                case 26 -> {
                    String lexema = programa.substring(inicio, avance);
                    inicio = avance;
                    return new Token(TokenType.BIN_OP, lexema, linea, columna - lexema.length()); // mul
                }
                case 27 -> {
                    String lexema = programa.substring(inicio, avance);
                    inicio = avance;
                    return new Token(TokenType.BIN_OP, lexema, linea, columna - lexema.length()); // div
                }
                case 28 -> {
                    char c = leerCaracter();
                    switch (c) {
                        case ' ', '\n', '\r', '\t' -> {
                            estado = 29;
                            if (c == '\n') {
                                linea++;
                                columna = 0;
                            }
                        }
                        default -> estado = diagrama();
                    }
                }
                case 29 -> {
                    char c = leerCaracter();
                    switch (c) {
                        case ' ', '\n', '\r', '\t' -> {
                            estado = 29;
                            if (c == '\n') {
                                linea++;
                                columna = 0;
                            }
                        }
                        default -> estado = 30;
                    }
                }
                case 30 -> {
                    inicio = --avance;
                    columna--;
                    return null;
                }
                case 31 -> {
                    char c = leerCaracter();
                    if (c == '\0') {
                        estado = 32;
                    } else {
                        estado = diagrama();
                    }
                }
                case 32 -> {
                    inicio = avance;
                    return null;
                }
            }
        }
    }

    private static char leerCaracter() {
        columna++;
        return programa.charAt(avance++);
    }
}
