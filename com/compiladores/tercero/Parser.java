package com.compiladores.tercero;

import java.util.List;
import java.util.Stack;

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

    private final String nombrePrograma;
    private final String programa;
    private final List<Token> tokens;
    private int siguiente;
    private final String[] terminales;
    private final String[] noTerminales;
    private final String[] gramatica;
    private final int[][] M;
    private final Stack<String> pila;

    public Parser(List<Token> tokens, String nombrePrograma, String programa) {
        this.tokens = tokens;
        this.nombrePrograma = nombrePrograma;
        this.programa = programa;
        
        this.M = new int[6][11];
        this.M[0][0] = 1;
        this.M[1][0] = 2;
        this.M[1][3] = 3;
        this.M[1][4] = 2;
        this.M[1][5] = 2;
        this.M[2][0] = 6;
        this.M[2][4] = 4;
        this.M[2][5] = 5;
        this.M[3][6] = 7;
        this.M[3][7] = 7;
        this.M[4][6] = 8;
        this.M[4][7] = 9;
        this.M[5][8] = 10;
        this.M[5][9] = 11;

        this.terminales = new String[11];
        this.terminales[0] = "m";
        this.terminales[1] = "(";
        this.terminales[2] = ")";
        this.terminales[3] = "fm";
        this.terminales[4] = "x";
        this.terminales[5] = "y";
        this.terminales[6] = "id";
        this.terminales[7] = "num";
        this.terminales[8] = ">";
        this.terminales[9] = "<";
        this.terminales[10] = "EOF";

        this.noTerminales = new String[6];
        this.noTerminales[0] = "W";
        this.noTerminales[1] = "B";
        this.noTerminales[2] = "I";
        this.noTerminales[3] = "C";
        this.noTerminales[4] = "E";
        this.noTerminales[5] = "OP";

        this.gramatica = new String[11];
        this.gramatica[0] = "m ( C ) B fm";
        this.gramatica[1] = "I B";
        this.gramatica[2] = "";
        this.gramatica[3] = "x";
        this.gramatica[4] = "y";
        this.gramatica[5] = "W";
        this.gramatica[6] = "E OP E";
        this.gramatica[7] = "id";
        this.gramatica[8] = "num";
        this.gramatica[9] = ">";
        this.gramatica[10] = "<";

        this.pila = new Stack<>();
    }

    private void error() {
        StringBuilder sb = new StringBuilder();

        String contexto = obtenerContexto();
        int columna = tokens.get(siguiente - 1).obtenerColumna();
        int linea = tokens.get(siguiente - 1).obtenerLinea();

        sb.append(ANSI_RED);
        sb.append(nombrePrograma);
        sb.append(":");
        sb.append(linea + 1);
        sb.append(":");
        sb.append(columna + 1);
        sb.append(":");
        sb.append(" ");
        sb.append("[Error sintáctico] No se esperaba token.");
        sb.append("\n");
        sb.append(contexto);
        sb.append("\n");
        sb.append(String.format("%" + (columna + 1) + "s", "^"));
        sb.append(ANSI_RESET);

        System.err.println(sb);

        System.exit(1);
    }

    private String obtenerContexto() {
        if (siguiente - 1 == tokens.size() - 1) {
            siguiente--;
        }
        int linea = tokens.get(siguiente - 1).obtenerLinea();
        return programa.split("\n")[linea];
    }

    public void parse() {
        siguiente = 0;
        pila.clear();
        
        pila.push("EOF");
        pila.push("W");

        Token a = leerToken();

        String x;

        do {
            x = pila.pop();

            if (a.obtenerTipo() == TokenType.EOF && x == "EOF") {
                break;
            } else if (esTerminal(x)) {
                if (terminales[terminal(a)].equals(x)) {
                    a = leerToken();
                } else {
                    error();
                }
            } else {
                int n = M[noTerminal(x)][terminal(a)];
                if (n > 0) {
                    String[] produccion = gramatica[n - 1].split(" ");
                    for (int i = produccion.length - 1; i >= 0; i--){
                        if (produccion[i].isBlank()) continue;
                        pila.push(produccion[i]);
                    }
                } else {
                    error();
                }
            }
        } while(!x.equals("EOF"));
    }

    private int terminal(Token t) {
        if (t.obtenerTipo() == TokenType.ID) {
            return 6;
        }
        if (t.obtenerTipo() == TokenType.NUM) {
            return 7;
        }
        if (t.obtenerTipo() == TokenType.EOF) {
            return 10;
        }
        for (int i = 0; i < terminales.length; i++) {
            if (t.obtenerLexema().equals(terminales[i])) {
                return i;
            }
        }
        error();
        return -1;
    }

    private int noTerminal(String x) {
        for (int i = 0; i < noTerminales.length; i++) {
            if (noTerminales[i].equals(x)) {
                return i;
            }
        }
        error();
        return -1;
    }

    private boolean esTerminal(String x) {
        for (int i = 0; i < terminales.length; i++) {
            if (terminales[i].equals(x)) return true;
        }
        return false;
    }

    private Token leerToken() {
        return tokens.get(siguiente++);
    }
}
