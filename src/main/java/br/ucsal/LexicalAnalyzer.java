package br.ucsal;

import java.io.*;
import java.util.*;

import static java.lang.Character.isWhitespace;

// TODO Não deverá ser solicitada a extensão do texto fonte na chamada de execução do Static Checker.
// TODO Caso seja fornecido apenas o nome do texto fonte, este deve ser procurado no diretório corrente onde o Static Checker está sendo executado.
//  Caso seja fornecido o caminho completo mais o nome do texto fonte como parâmetro de entrada, o arquivo deve ser procurado neste caminho indicado na entrada.

public class LexicalAnalyzer {
    private static final int MAX_LEXEME_LENGTH = 30;

    private static final Map<String, Integer> atomCodes = new HashMap<>();

    static {
        atomCodes.put("cadeia", 101);
        atomCodes.put("inteiro", 108);
        atomCodes.put("%", 301);
        atomCodes.put("-", 401);
        atomCodes.put("cons-cadeia", 501);
        atomCodes.put("submáquina1", 601);
        atomCodes.put("caracter", 102);
        atomCodes.put("logico", 109);
        atomCodes.put("(", 302);
        atomCodes.put("*", 402);
        atomCodes.put("cons-caracter", 502);
        atomCodes.put("submáquina2", 602);
        atomCodes.put("declaracoes", 103);
        atomCodes.put("pausa", 110);
        atomCodes.put(")", 303);
        atomCodes.put("/", 403);
        atomCodes.put("cons-inteiro", 503);
        atomCodes.put("submáquina3", 603);
        atomCodes.put("enquanto", 104);
        atomCodes.put("programa", 111);
        atomCodes.put(",", 304);
        atomCodes.put("+", 404);
        atomCodes.put("cons-real", 504);
        atomCodes.put("...", 605);
        atomCodes.put("false", 201);
        atomCodes.put("real", 112);
        atomCodes.put(":", 305);
        atomCodes.put("!=", 411);
        atomCodes.put("nom-funcao", 511);
        atomCodes.put("submáquinan", 60);
        atomCodes.put("fim-declaracoes", 202);
        atomCodes.put("retorna", 113);
        atomCodes.put(":=", 306);
        atomCodes.put("#", 411);
        atomCodes.put("nom-programa", 512);
        atomCodes.put("fim-enquanto", 203);
        atomCodes.put("se", 114);
        atomCodes.put(";", 307);
        atomCodes.put("<", 412);
        atomCodes.put("variavel", 513);
        atomCodes.put("fim-func", 204);
        atomCodes.put("senao", 115);
        atomCodes.put("?", 308);
        atomCodes.put("<=", 413);
        atomCodes.put("fim-funcoes", 205);
        atomCodes.put("tipo-func", 116);
        atomCodes.put("[", 309);
        atomCodes.put("==", 414);
        atomCodes.put("fim-programa", 206);
        atomCodes.put("tipo-param", 117);
        atomCodes.put("]", 310);
        atomCodes.put(">", 415);
        atomCodes.put("fim-se", 105);
        atomCodes.put("tipo-var", 118);
        atomCodes.put("{", 311);
        atomCodes.put(">=", 416);
        atomCodes.put("funcoes", 106);
        atomCodes.put("true", 119);
        atomCodes.put("}", 312);
        atomCodes.put("imprime", 107);
        atomCodes.put("vazio", 120);
    }

    private String inputFile;
    private List<String> symbolsTable;
    private List<String> lexemes;
    private List<Integer> codes;
    private List<Integer> indices;
    private List<Integer> lines;

    public LexicalAnalyzer(String inputFile) {
        this.inputFile = inputFile;
        this.symbolsTable = new ArrayList();
        this.lexemes = new ArrayList();
        this.codes = new ArrayList();
        this.indices = new ArrayList();
        this.lines = new ArrayList();
    }

    // esse método lê linha a linha do arquivo, processa e no final chama a geração dos relatórios
    public void analyze() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                processLine(line, lineNumber);
                lineNumber++;
            }
            reader.close();
            generateLexicalReport();
            generateSymbolTableReport();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processLine(String line, int lineNumber) {
        int index = 0;
        int lineLength = line.length();

        while (index < lineLength) {
            char currentChar = line.charAt(index);
            if (isWhitespace(currentChar)) {
                index++;
                continue;
            }

            if (currentChar == '/' && index + 1 < lineLength && line.charAt(index + 1) == '/') {
                // Ignorar o restante da linha, pois é um comentário de linha
                break;
            } else if (currentChar == '/' && index + 1 < lineLength && line.charAt(index + 1) == '*') {
                // Ignorar até encontrar o fechamento do comentário de bloco
                int closingIndex = line.indexOf("*/", index + 2);
                if (closingIndex != -1) {
                    index = closingIndex + 2;
                    continue;
                }
            }

            // Formar o átomo a partir do caractere atual e caracteres subsequentes válidos
            StringBuilder lexemeBuilder = new StringBuilder();

            while (index < lineLength && lexemeBuilder.length() < MAX_LEXEME_LENGTH) {
                if (isValidCharacter(line.charAt(index))) {
                    lexemeBuilder.append(line.charAt(index));
                }

                if (isWhitespace(line.charAt(index))) break;

                index++;
            }

            // Verificar se o átomo é um identificador válido
            String lexeme = lexemeBuilder.toString().toUpperCase();

            if (!lexeme.isEmpty()) {
                int code = getOrDefault(lexeme, -1, atomCodes);
                if (code != -1) {
                    symbolsTable.add(lexeme);
                    lexemes.add(lexeme);
                    codes.add(code);
                    indices.add(symbolsTable.size());
                    lines.add(lineNumber);
                }
            }

            if(index < lineLength && !isValidCharacter(line.charAt(index)) && !isWhitespace(line.charAt(index))) {
                index++; // Avança o índice se o próximo caractere é inválido
            }
        }
    }




    private int getOrDefault(Object key, int defaultValue, Map<String, Integer> atomCodes) {
        return atomCodes.keySet().stream().filter(atom -> atom.equalsIgnoreCase((String) key)).findFirst().map(atomCodes::get).orElse(defaultValue);
    }

    // Verificar se o caractere é válido de acordo com as regras da linguagem ORMPlus2023-1
    private boolean isValidCharacter(Character c) {
        int asciiValue = c;
        return (asciiValue <= 127) && (Character.isLetterOrDigit(asciiValue) || c == '_' || c == '$' || c == '.');
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
        String input = "/home/eduardo/Workspace/ucsal/statickChecker/Teste.231";

        new LexicalAnalyzer(input).analyze();
    }
}