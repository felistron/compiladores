/*
 * Titulo: Parser SLR(1)
 * Autor: Fernando Espinosa
 * Fecha: 2025/04/22
 */

package com.compiladores.cuarto;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.compiladores.Token;
import com.compiladores.segundo.Lexer;

public class Cuarto {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) {
        String nombrePrograma = "";
        String programa = "";

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

        Lexer lexer = new Lexer(nombrePrograma, programa);

        List<Token> tokens = lexer.tokenizar();

        System.out.println(ANSI_GREEN + "Análisis léxico terminado correctamente" + ANSI_RESET);

        Parser parser = new Parser(nombrePrograma, programa, tokens);
        parser.parse();

        System.out.println(ANSI_GREEN + "Análisis sintáctico terminado correctamente" + ANSI_RESET);
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
}
