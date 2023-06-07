package br.ucsal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LexicalAnalyzer2 {
    private static final int MAX_LEXEME_LENGTH = 30;

    private static final Map<String, Integer> tableOfReservedWordsAndSymbols = new HashMap<>();

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

    /*
    regexProgramName = "^[a-z]+[a-z0-9]*$"
    regexVariable = "^[_a-z]+[_a-z0-9]*$"
    regexFunctionName = "^[a-z]+[a-z0-9]*$
    regexInteger = "^[0-9]+$"
    regexReal = "^([0-9]+[.][0-9]+([eE][+-]?[0-9]+)?)$"
    regexString = "^\"[a-z0-9$_.\\s]*\"$"
    regexCharacter = "^'[a-z]'$"
 */
    private List<String> patternsOfAtomsFormation;

    {
        patternsOfAtomsFormation = new ArrayList<>();
        patternsOfAtomsFormation.add("^[a-z]+[a-z0-9]*$");
        patternsOfAtomsFormation.add("^[_a-z]+[_a-z0-9]*$");
        patternsOfAtomsFormation.add("^[a-z]+[a-z0-9]*$");
        patternsOfAtomsFormation.add("^[0-9]+$");
        patternsOfAtomsFormation.add("^([0-9]+[.][0-9]+([eE][+-]?[0-9]+)?)$");
        patternsOfAtomsFormation.add("^\"[a-z0-9$_.\\s]*\"$");
        patternsOfAtomsFormation.add("^'[a-z]'$");
    }

    private final String inputFile;
    private List<String> symbolsTable;
    private List<Token> tokens;

    public LexicalAnalyzer2(String inputFile) {
        if (!inputFile.endsWith(".231")) {
            throw new IllegalArgumentException("O arquivo de entrada deve ter a extensão .231");
        }

        initializeAtomsWithCodes();

        this.inputFile = inputFile;
        this.symbolsTable = new ArrayList<>();
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

    private boolean insideBlockComment = false;

    private void readAndProcessLine(String line, int lineNumber) {
        int index = 0;
        int lineLength = line.length();

        while (index < lineLength) {
            char currentChar = line.charAt(index);

            if (insideBlockComment) {
                // Inside a block comment, so ignore until finding the closing of the block comment
                int closingIndex = line.indexOf("*/", index);
                if (closingIndex != -1) {
                    index = closingIndex + 2;
                    insideBlockComment = false;
                } else {
                    // If there's no closing block, consider the rest of the line as a comment
                    return;
                }
                continue;
            } else if (currentChar == '/' && index + 1 < lineLength && line.charAt(index + 1) == '*') {
                // Enter into a block comment
                insideBlockComment = true;
                index += 2;
                continue;
            } else if (currentChar == '/' && index + 1 < lineLength && line.charAt(index + 1) == '/') {
                // Ignore the rest of the line, as it's a single-line comment
                break;
            }

            // Form the atom from the current character and subsequent valid characters
            StringBuilder lexemeBuilder = new StringBuilder();
            boolean previousWasSpace = false;

            while (index < lineLength && lexemeBuilder.length() < MAX_LEXEME_LENGTH) {
                currentChar = line.charAt(index);

                if (Character.isWhitespace(currentChar)) {
                    if (!previousWasSpace && isValidCharacterForAtom(lexemeBuilder.toString() + currentChar)) {
                        lexemeBuilder.append(currentChar);
                        previousWasSpace = true;
                    }
                    index++;
                    continue;
                } else if (isValidCharacter(currentChar) && isValidCharacterForAtom(lexemeBuilder.toString() + currentChar)) {
                    lexemeBuilder.append(currentChar);
                    previousWasSpace = false;
                } else if (lexemeBuilder.length() > 0) {
                    if (!isValidCharacter(currentChar)) {
                        index++;
                        continue;
                    }
                    break;
                }

                index++;
            }

            // Continue reading past the 30 character limit until we find a delimiter
            while (index < lineLength && isValidCharacter(line.charAt(index)) && !Character.isWhitespace(line.charAt(index))) {
                index++;
            }

            // Check if the atom is a valid identifier
            String lexeme = lexemeBuilder.toString().trim().toUpperCase(); // Trim any trailing/leading whitespace
            if (!lexeme.isEmpty()) {
                int code = getOrDefault(lexeme, -1, tableOfReservedWordsAndSymbols);
                if (code != -1) {
                    symbolsTable.add(lexeme);
                    tokens.add(new Token(lexeme, code, symbolsTable.size(), lineNumber));
                }
            }

            if (index < lineLength && !isValidCharacter(line.charAt(index)) && !Character.isWhitespace(line.charAt(index))) {
                index++;
            }
        }
    }

    private int getOrDefault(Object key, int defaultValue, Map<String, Integer> atomCodes) {
        return atomCodes
                .keySet()
                .stream()
                .filter(atom -> atom.equalsIgnoreCase((String) key))
                .findFirst()
                .map(atomCodes::get)
                .orElse(defaultValue);
    }

    private boolean isValidCharacter(Character c) {
        int asciiValue = c;
        return (asciiValue <= 127) && (Character.isLetterOrDigit(asciiValue) || c == '_' || c == '$' || c == '.');
    }

    private boolean isValidCharacterForAtom(String possibleLexeme) {
        return tableOfReservedWordsAndSymbols.keySet().stream().anyMatch(atom -> atom.toUpperCase().startsWith(possibleLexeme.toUpperCase()));
    }

    public String getInputFile() {
        return inputFile;
    }
}