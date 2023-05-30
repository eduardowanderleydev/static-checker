package br.ucsal;

import java.io.*;
import java.util.*;

public class LexicalAnalyzer {
    private static final int MAX_LEXEME_LENGTH = 30;

    private static final Map<String, Integer> atomsWithCodes = new HashMap<>();

    public static void initializeAtomsWithCodes() {
        atomsWithCodes.put("cadeia", 101);
        atomsWithCodes.put("inteiro", 108);
        atomsWithCodes.put("%", 301);
        atomsWithCodes.put("-", 401);
        atomsWithCodes.put("cons-cadeia", 501);
        atomsWithCodes.put("caracter", 102);
        atomsWithCodes.put("logico", 109);
        atomsWithCodes.put("(", 302);
        atomsWithCodes.put("*", 402);
        atomsWithCodes.put("cons-caracter", 502);
        atomsWithCodes.put("declaracoes", 103);
        atomsWithCodes.put("pausa", 110);
        atomsWithCodes.put(")", 303);
        atomsWithCodes.put("/", 403);
        atomsWithCodes.put("cons-inteiro", 503);
        atomsWithCodes.put("enquanto", 104);
        atomsWithCodes.put("programa", 111);
        atomsWithCodes.put(",", 304);
        atomsWithCodes.put("+", 404);
        atomsWithCodes.put("cons-real", 504);
        atomsWithCodes.put("...", 605);
        atomsWithCodes.put("false", 201);
        atomsWithCodes.put("real", 112);
        atomsWithCodes.put(":", 305);
        atomsWithCodes.put("!=", 411);
        atomsWithCodes.put("nom-funcao", 511);
        atomsWithCodes.put("fim-declaracoes", 202);
        atomsWithCodes.put("retorna", 113);
        atomsWithCodes.put(":=", 306);
        atomsWithCodes.put("#", 411);
        atomsWithCodes.put("nom-programa", 512);
        atomsWithCodes.put("fim-enquanto", 203);
        atomsWithCodes.put("se", 114);
        atomsWithCodes.put(";", 307);
        atomsWithCodes.put("<", 412);
        atomsWithCodes.put("variavel", 513);
        atomsWithCodes.put("fim-func", 204);
        atomsWithCodes.put("senao", 115);
        atomsWithCodes.put("?", 308);
        atomsWithCodes.put("<=", 413);
        atomsWithCodes.put("fim-funcoes", 205);
        atomsWithCodes.put("tipo-func", 116);
        atomsWithCodes.put("[", 309);
        atomsWithCodes.put("==", 414);
        atomsWithCodes.put("fim-programa", 206);
        atomsWithCodes.put("tipo-param", 117);
        atomsWithCodes.put("]", 310);
        atomsWithCodes.put(">", 415);
        atomsWithCodes.put("fim-se", 105);
        atomsWithCodes.put("tipo-var", 118);
        atomsWithCodes.put("{", 311);
        atomsWithCodes.put(">=", 416);
        atomsWithCodes.put("funcoes", 106);
        atomsWithCodes.put("true", 119);
        atomsWithCodes.put("}", 312);
        atomsWithCodes.put("imprime", 107);
        atomsWithCodes.put("vazio", 120);
    }

    private final String inputFile;
    private final List<String> symbolsTable;
    private final List<String> lexemes;
    private final List<Integer> codes;
    private final List<Integer> indices;
    private final List<Integer> lines;

    public LexicalAnalyzer(String inputFile) {
        if (!inputFile.endsWith(".231")) {
            throw new IllegalArgumentException("O arquivo de entrada deve ter a extensão .231");
        }

        initializeAtomsWithCodes();

        this.inputFile = inputFile;
        this.symbolsTable = new ArrayList<>();
        this.lexemes = new ArrayList<>();
        this.codes = new ArrayList<>();
        this.indices = new ArrayList<>();
        this.lines = new ArrayList<>();
    }

    public void analyze() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                readAndProcessLine(line, lineNumber);
                lineNumber++;
            }
            reader.close();
            generateLexicalReport();
            generateSymbolTableReport();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readAndProcessLine(String line, int lineNumber) {
        int index = 0;
        int lineLength = line.length();

        while (index < lineLength) {
            char currentChar = line.charAt(index);
            if (Character.isWhitespace(currentChar)) {
                index++;
                continue;
            }

            if (currentChar == '/' && index + 1 < lineLength && line.charAt(index + 1) == '/') {
                // Ignore the rest of the line, as it's a single-line comment
                break;
            } else if (currentChar == '/' && index + 1 < lineLength && line.charAt(index + 1) == '*') {
                // Ignore until finding the closing of the block comment
                int closingIndex = line.indexOf("*/", index + 2);
                if (closingIndex != -1) {
                    index = closingIndex + 2;
                } else {
                    break;
                }
                continue;
            }

            // Form the atom from the current character and subsequent valid characters
            StringBuilder lexemeBuilder = new StringBuilder();

            while (index < lineLength && lexemeBuilder.length() < MAX_LEXEME_LENGTH) {
                if (isValidCharacter(line.charAt(index)) && isValidCharacterForAtom(lexemeBuilder.toString() + line.charAt(index))) {
                    lexemeBuilder.append(line.charAt(index));
                } else if (lexemeBuilder.length() > 0) {
                    if (!Character.isWhitespace(line.charAt(index))) {
                        index++;
                        continue;
                    }
                    break;
                }

                if (Character.isWhitespace(line.charAt(index))) break;

                index++;
            }

            // Continue reading past the 30 character limit until we find a delimiter
            while (index < lineLength && isValidCharacter(line.charAt(index)) && !Character.isWhitespace(line.charAt(index))) {
                index++;
            }

            // Check if the atom is a valid identifier
            String lexeme = lexemeBuilder.toString().toUpperCase();

            if (!lexeme.isEmpty()) {
                int code = getOrDefault(lexeme, -1, atomsWithCodes);
                if (code != -1) {
                    symbolsTable.add(lexeme);
                    lexemes.add(lexeme);
                    codes.add(code);
                    indices.add(symbolsTable.size());
                    lines.add(lineNumber);
                }
            }

            if (index < lineLength && !isValidCharacter(line.charAt(index)) && !Character.isWhitespace(line.charAt(index))) {
                index++;
            }
        }
    }


    private int getOrDefault(Object key, int defaultValue, Map<String, Integer> atomCodes) {
        return atomCodes.keySet().stream().filter(atom -> atom.equalsIgnoreCase((String) key)).findFirst().map(atomCodes::get).orElse(defaultValue);
    }

    private boolean isValidCharacter(Character c) {
        int asciiValue = c;
        return (asciiValue <= 127) && (Character.isLetterOrDigit(asciiValue) || c == '_' || c == '$' || c == '.');
    }

    private boolean isValidCharacterForAtom(String possibleLexeme) {
        return atomsWithCodes.keySet().stream().anyMatch(atom -> atom.toUpperCase().startsWith(possibleLexeme.toUpperCase()));
    }

    private void generateLexicalReport() {
        String baseName = inputFile.substring(0, inputFile.lastIndexOf('.'));
        String lexReportFile = baseName + ".LEX";

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(lexReportFile));
            writer.println("Código da Equipe: 067");
            writer.println("Componentes:");
            writer.println("Eduardo Wanderley; eduardobraz.junior@ucsal.edu.br; (75)98207-4248");
            writer.println("RELATÓRIO DA ANÁLISE LÉXICA. Texto fonte analisado: " + inputFile);

            for (int i = 0; i < lexemes.size(); i++) {
                writer.println("Lexeme: " + lexemes.get(i) +
                        ", Código: " + codes.get(i) +
                        ", ÍndiceTabSimb: " + indices.get(i) +
                        ", Linha: " + lines.get(i) + ".");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateSymbolTableReport() {
        String baseName = inputFile.substring(0, inputFile.lastIndexOf('.'));
        String tabReportFile = baseName + ".TAB";

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(tabReportFile));
            writer.println("Código da Equipe: 067");
            writer.println("Componentes:");
            writer.println("Eduardo Wanderley; eduardobraz.junior@ucsal.edu.br; (75)98207-4248");
            writer.println("RELATÓRIO DA TABELA DE SÍMBOLOS. Texto fonte analisado: " + inputFile);

            for (int i = 0; i < symbolsTable.size(); i++) {
                String symbol = symbolsTable.get(i);
                writer.println("Entrada: " + (i + 1) +
                        ", Código: " + codes.get(i) +
                        ", Lexeme: " + symbol +
                        ", QtdCharAntesTrunc: " + symbol.length() +
                        ", QtdCharDepoisTrunc: " + symbol.length() +
                        ", TipoSimb: -" +
                        ", Linhas: (" + lines.get(i) + ").");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.out.println("Erro: você deve fornecer o arquivo de entrada como argumento");
//            return;
//        }
//        String input = args[0];

        new LexicalAnalyzer("/home/eduardo/Workspace/ucsal/statickChecker/Teste.231").analyze();
    }
}
