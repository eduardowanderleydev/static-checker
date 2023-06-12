package br.ucsal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class LexicalAnalyzer {

    private static final int MAX_LEXEME_LENGTH = 30;
    private static final Map<String, Integer> tableOfReservedWordsAndSymbols = new HashMap<>();
    private static int counter = 0;
    private final String inputFile;
    private List<Token> tokens;
    private SymbolTable symbolTable = new SymbolTable();
    private boolean insideBlockComment = false;

    public static void initializeAtomsWithCodes() {
        tableOfReservedWordsAndSymbols.put("cadeia", 101);
        tableOfReservedWordsAndSymbols.put("inteiro", 108);
        tableOfReservedWordsAndSymbols.put("%", 301);
        tableOfReservedWordsAndSymbols.put("-", 401);
        tableOfReservedWordsAndSymbols.put("cons-cadeia", 501);
        tableOfReservedWordsAndSymbols.put("caracter", 102);
        tableOfReservedWordsAndSymbols.put("logico", 109);
        tableOfReservedWordsAndSymbols.put("(", 302);
        tableOfReservedWordsAndSymbols.put("*", 402);
        tableOfReservedWordsAndSymbols.put("cons-caracter", 502);
        tableOfReservedWordsAndSymbols.put("declaracoes", 103);
        tableOfReservedWordsAndSymbols.put("pausa", 110);
        tableOfReservedWordsAndSymbols.put(")", 303);
        tableOfReservedWordsAndSymbols.put("/", 403);
        tableOfReservedWordsAndSymbols.put("cons-inteiro", 503);
        tableOfReservedWordsAndSymbols.put("enquanto", 104);
        tableOfReservedWordsAndSymbols.put("programa", 111);
        tableOfReservedWordsAndSymbols.put(",", 304);
        tableOfReservedWordsAndSymbols.put("+", 404);
        tableOfReservedWordsAndSymbols.put("cons-real", 504);
        tableOfReservedWordsAndSymbols.put("...", 605);
        tableOfReservedWordsAndSymbols.put("false", 201);
        tableOfReservedWordsAndSymbols.put("real", 112);
        tableOfReservedWordsAndSymbols.put(":", 305);
        tableOfReservedWordsAndSymbols.put("!=", 411);
        tableOfReservedWordsAndSymbols.put("nom-funcao", 511);
        tableOfReservedWordsAndSymbols.put("fim-declaracoes", 202);
        tableOfReservedWordsAndSymbols.put("retorna", 113);
        tableOfReservedWordsAndSymbols.put(":=", 306);
        tableOfReservedWordsAndSymbols.put("#", 411);
        tableOfReservedWordsAndSymbols.put("nom-programa", 512);
        tableOfReservedWordsAndSymbols.put("fim-enquanto", 203);
        tableOfReservedWordsAndSymbols.put("se", 114);
        tableOfReservedWordsAndSymbols.put(";", 307);
        tableOfReservedWordsAndSymbols.put("<", 412);
        tableOfReservedWordsAndSymbols.put("variavel", 513);
        tableOfReservedWordsAndSymbols.put("fim-func", 204);
        tableOfReservedWordsAndSymbols.put("senao", 115);
        tableOfReservedWordsAndSymbols.put("?", 308);
        tableOfReservedWordsAndSymbols.put("<=", 413);
        tableOfReservedWordsAndSymbols.put("fim-funcoes", 205);
        tableOfReservedWordsAndSymbols.put("tipo-func", 116);
        tableOfReservedWordsAndSymbols.put("[", 309);
        tableOfReservedWordsAndSymbols.put("==", 414);
        tableOfReservedWordsAndSymbols.put("fim-programa", 206);
        tableOfReservedWordsAndSymbols.put("tipo-param", 117);
        tableOfReservedWordsAndSymbols.put("]", 310);
        tableOfReservedWordsAndSymbols.put(">", 415);
        tableOfReservedWordsAndSymbols.put("fim-se", 105);
        tableOfReservedWordsAndSymbols.put("tipo-var", 118);
        tableOfReservedWordsAndSymbols.put("{", 311);
        tableOfReservedWordsAndSymbols.put(">=", 416);
        tableOfReservedWordsAndSymbols.put("funcoes", 106);
        tableOfReservedWordsAndSymbols.put("true", 119);
        tableOfReservedWordsAndSymbols.put("}", 312);
        tableOfReservedWordsAndSymbols.put("imprime", 107);
        tableOfReservedWordsAndSymbols.put("vazio", 120);
        tableOfReservedWordsAndSymbols.put("atomo teste", 120);
    }

    public LexicalAnalyzer(String inputFile) {
        if (!inputFile.endsWith(".231")) {
            throw new IllegalArgumentException("O arquivo de entrada deve ter a extensão .231");
        }

        initializeAtomsWithCodes();

        this.inputFile = inputFile;
        this.tokens = new ArrayList<>();
    }

    public List<Token> analyze() {
        tokens = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                readAndProcessLine(line, lineNumber);
                lineNumber++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tokens;
    }

    private void readAndProcessLine(String line, int lineNumber) {
        int index = 0;
        int lineLength = line.length();
        int lexemeOriginalLength = 0;

        while (index < lineLength) {
            char currentChar = line.charAt(index);

            if (insideBlockComment) {
                int closingIndex = line.indexOf("*/", index);
                if (closingIndex != -1) {
                    index = closingIndex + 2;
                    insideBlockComment = false;
                } else {
                    return;
                }
                continue;
            } else if (currentChar == '/' && index + 1 < lineLength && line.charAt(index + 1) == '*') {
                insideBlockComment = true;
                index += 2;
                continue;
            } else if (currentChar == '/' && index + 1 < lineLength && line.charAt(index + 1) == '/') {
                break;
            }

            StringBuilder lexemeBuilder = new StringBuilder();

            while (index < lineLength) {
                currentChar = line.charAt(index);

                if (Character.isWhitespace(currentChar)) {
                    lexemeBuilder.append(currentChar);
                    if (lexemeBuilder.length() > 0 && !isValidCharacterForAtom(lexemeBuilder.toString())) {
                        break;
                    }
                    index++;
                    continue;
                } else if (isValidCharacter(currentChar) && isValidCharacterForAtom(lexemeBuilder.toString() + currentChar)) {
                    lexemeBuilder.append(currentChar);
                } else if (lexemeBuilder.length() > 0) {
                    if (!isValidCharacter(currentChar)) {
                        index++;
                        continue;
                    }
                    break;
                }

                index++;
            }

            if (lexemeBuilder.length() > MAX_LEXEME_LENGTH) {
                lexemeOriginalLength = lexemeBuilder.length();
                lexemeBuilder.setLength(MAX_LEXEME_LENGTH);
            }

            while (index < lineLength && isValidCharacter(line.charAt(index)) && !Character.isWhitespace(line.charAt(index))) {
                index++;
            }

            String lexeme = lexemeBuilder.toString().trim().toUpperCase();
            if (!lexeme.isEmpty()) {
                processLexeme(lexeme, lineNumber, lexemeOriginalLength);
            }

            if (index < lineLength && !isValidCharacter(line.charAt(index)) && !Character.isWhitespace(line.charAt(index))) {
                index++;
            }
            if (index < lineLength && Character.isWhitespace(line.charAt(index))) {
                index++;
            }
        }
    }

    private void processLexeme(String lexeme, int lineNumber, int lexemeOriginalLength) {
        int code = getOrDefault(lexeme, -1, tableOfReservedWordsAndSymbols);
        if (code != -1) {
            Optional<Map.Entry<String, Integer>> possibleMap = getTypeOfLexeme(lexeme, tableOfReservedWordsAndSymbols).entrySet().stream().findFirst();
            String typeOfLexeme = possibleMap.isPresent() ? possibleMap.get().getKey() : "-";
            Token token = new Token(counter++, code, lexeme, lexemeOriginalLength, lexeme.length(), typeOfLexeme, String.valueOf(lineNumber));
            if (isAIdentifierAtom(lexeme)) {
                token.setIndexInSymbolTable(symbolTable.getTokens().size());
                symbolTable.addToken(token);
            }
            tokens.add(token);
        }
    }

    private boolean isValidCharacterForAtom(String possibleLexeme) {
        return tableOfReservedWordsAndSymbols.keySet().stream().anyMatch(atom -> atom.toUpperCase().startsWith(possibleLexeme.toUpperCase())) ||
                (!getTypeOfLexeme(possibleLexeme, tableOfReservedWordsAndSymbols).isEmpty());
    }

    private boolean isAIdentifierAtom(String lexeme) {
        return !getTypeOfLexeme(lexeme, tableOfReservedWordsAndSymbols).isEmpty() && !existsInTableOfReservedWordsAndSymbols(lexeme);
    }

    private boolean existsInTableOfReservedWordsAndSymbols(String lexeme) {
        return tableOfReservedWordsAndSymbols.keySet().stream().anyMatch(atom -> atom.equalsIgnoreCase(lexeme));
    }

    private int getOrDefault(Object key, int defaultValue, Map<String, Integer> atomCodes) {
        Integer integer = atomCodes
                .keySet()
                .stream()
                .filter(atom -> atom.equalsIgnoreCase((String) key))
                .findFirst()
                .map(atomCodes::get)
                .orElse(defaultValue);

        if (integer == defaultValue) {
            Map<String, Integer> typeOfLexeme = getTypeOfLexeme(key.toString(), atomCodes);
            if (!typeOfLexeme.isEmpty()) {
                integer = typeOfLexeme.entrySet().stream().findFirst().get().getValue();
            }
        }

        return integer;
    }

    private Map<String, Integer> getTypeOfLexeme(String lexeme, Map<String, Integer> atomCodes) {
        String nomProgramaPattern = "^[a-z]+[a-z0-9]*$";
        String variavelPattern = "^[_a-z]+[a-z0-9_]*$";
        String nomFuncaoPattern = "^[a-z]+[a-z0-9]*$";
        String consInteiroPattern = "^[0-9]+$";
        String consRealPattern = "^[0-9]+(\\.[0-9]*)?(e[+-]?[0-9]+)?$";
        String consCadeiaPattern = "^\".*\"$";
        String consCaracterPattern = "^'[a-z]'$";

        Pattern nomPrograma = Pattern.compile(nomProgramaPattern, CASE_INSENSITIVE);
        Pattern variavel = Pattern.compile(variavelPattern, CASE_INSENSITIVE);
        Pattern nomFuncao = Pattern.compile(nomFuncaoPattern, CASE_INSENSITIVE);
        Pattern consInteiro = Pattern.compile(consInteiroPattern, CASE_INSENSITIVE);
        Pattern consReal = Pattern.compile(consRealPattern, CASE_INSENSITIVE);
        Pattern consCadeia = Pattern.compile(consCadeiaPattern, CASE_INSENSITIVE);
        Pattern consCaracter = Pattern.compile(consCaracterPattern, CASE_INSENSITIVE);


        Map <String, Integer> map = new HashMap<>();

        if(nomPrograma.matcher(lexeme).matches()) {
            map.put("nom-programa", atomCodes.get("nom-programa"));
        } else if(variavel.matcher(lexeme).matches()) {
            map.put("variavel", atomCodes.get("variavel"));
        } else if(nomFuncao.matcher(lexeme).matches()) {
            map.put("nom-funcao", atomCodes.get("nom-funcao"));
        } else if(consInteiro.matcher(lexeme).matches()) {
            map.put("cons-inteiro", atomCodes.get("cons-inteiro"));
        } else if(consReal.matcher(lexeme).matches()) {
            map.put("cons-real", atomCodes.get("cons-real"));
        } else if(consCadeia.matcher(lexeme).matches()) {
            map.put("cons-cadeia", atomCodes.get("cons-cadeia"));
        } else if(consCaracter.matcher(lexeme).matches()) {
            map.put("cons-caracter", atomCodes.get("cons-caracter"));
        }

        return map;
    }

    private boolean isValidCharacter(Character c) {
        int asciiValue = c;
        return (asciiValue <= 127) && (Character.isLetterOrDigit(asciiValue) || c == '_' || c == '$' || c == '.');
    }

    public String getInputFile() {
        return inputFile;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}