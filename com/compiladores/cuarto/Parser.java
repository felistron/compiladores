package com.compiladores.cuarto;

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
    private final int[] longitudes;
    private final String[] parteIzquierda;
    private final int[][] M;
    private final int ACEPTACION = 1337;
    private final Stack<String> pila;

    public Parser(String nombrePrograma, String programa, List<Token> tokens) {
        this.nombrePrograma = nombrePrograma;
        this.programa = programa;
        this.tokens = tokens;

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

        this.noTerminales = new String[7];
        this.noTerminales[0] = "WP";
        this.noTerminales[1] = "W";
        this.noTerminales[2] = "B";
        this.noTerminales[3] = "I";
        this.noTerminales[4] = "C";
        this.noTerminales[5] = "E";
        this.noTerminales[6] = "OP";

        this.longitudes = new int[12];
        this.longitudes[0] = 1;
        this.longitudes[1] = 6;
        this.longitudes[2] = 2;
        this.longitudes[3] = 0;
        this.longitudes[4] = 1;
        this.longitudes[5] = 1;
        this.longitudes[6] = 1;
        this.longitudes[7] = 3;
        this.longitudes[8] = 1;
        this.longitudes[9] = 1;
        this.longitudes[10] = 1;
        this.longitudes[11] = 1;

        this.parteIzquierda = new String[12];
        this.parteIzquierda[0] = "WP";
        this.parteIzquierda[1] = "W";
        this.parteIzquierda[2] = "B";
        this.parteIzquierda[3] = "B";
        this.parteIzquierda[4] = "I";
        this.parteIzquierda[5] = "I";
        this.parteIzquierda[6] = "I";
        this.parteIzquierda[7] = "C";
        this.parteIzquierda[8] = "E";
        this.parteIzquierda[9] = "E";
        this.parteIzquierda[10] = "OP";
        this.parteIzquierda[11] = "OP";

        this.M = new int[20][18];
        this.M[0][0] = 2;
        this.M[0][12] = 1;
        this.M[1][10] = ACEPTACION;
        this.M[2][1] = 3;
        this.M[3][6] = 6;
        this.M[3][7] = 7;
        this.M[3][15] = 4;
        this.M[3][16] = 5;
        this.M[4][2] = 8;
        this.M[5][8] = 10;
        this.M[5][9] = 11;
        this.M[5][17] = 9;
        this.M[6][2] = -8;
        this.M[6][8] = -8;
        this.M[6][9] = -8;
        this.M[7][2] = -9;
        this.M[7][8] = -9;
        this.M[7][9] = -9;
        this.M[8][0] = 2;
        this.M[8][3] = -3;
        this.M[8][4] = 14;
        this.M[8][5] = 15;
        this.M[8][12] = 16;
        this.M[8][13] = 12;
        this.M[8][14] = 13;
        this.M[9][6] = 6;
        this.M[9][7] = 7;
        this.M[9][16] = 17;
        this.M[10][6] = -10;
        this.M[10][7] = -10;
        this.M[11][6] = -11;
        this.M[11][7] = -11;
        this.M[12][3] = 18;
        this.M[13][0] = 2;
        this.M[13][3] = -3;
        this.M[13][4] = 14;
        this.M[13][5] = 15;
        this.M[13][12] = 16;
        this.M[13][13] = 19;
        this.M[13][14] = 13;
        this.M[14][0] = -4;
        this.M[14][3] = -4;
        this.M[14][4] = -4;
        this.M[14][5] = -4;
        this.M[15][0] = -5;
        this.M[15][3] = -5;
        this.M[15][4] = -5;
        this.M[15][5] = -5;
        this.M[16][0] = -6;
        this.M[16][3] = -6;
        this.M[16][4] = -6;
        this.M[16][5] = -6;
        this.M[17][2] = -7;
        this.M[18][0] = -1;
        this.M[18][3] = -1;
        this.M[18][4] = -1;
        this.M[18][5] = -1;
        this.M[18][10] = -1;
        this.M[19][3] = -2;
        
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
        pila.push("0");
        
        Token a = leerToken();
        
        while (true) {
            String s = pila.peek();
            int n = M[Integer.parseInt(s)][terminal(a)];

            if (n == ACEPTACION) {
                break;
            } else if (n > 0) {
                pila.push(a.obtenerLexema());
                pila.push(n + "");
                a = leerToken();
            } else if (n < 0) {
                int m = 2 * longitudes[(n * -1)];
                for (int i = 0; i < m; i++) {
                    pila.pop();
                }
                String tope = pila.peek();
                String A = parteIzquierda[(n * -1)];
                pila.push(A);
                if (M[Integer.parseInt(tope)][noTerminal(A)] == 0) {
                    error();
                } else {
                    pila.push(M[Integer.parseInt(tope)][noTerminal(A)] + "");
                }
            } else {
                error();
            }
        }
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
        System.out.println("error terminal no reconocido " + t);
        error();
        return -1;
    }

    private int noTerminal(String x) {
        for (int i = 0; i < noTerminales.length; i++) {
            if (noTerminales[i].equals(x)) {
                return i + terminales.length;
            }
        }
        System.out.println("error no ñterminal no reconocido");
        error();
        return -1;
    }

    private Token leerToken() {
        return tokens.get(siguiente++);
    }
}
