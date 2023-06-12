package br.ucsal;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public class Reporter {
    public static void generateLexicalReport(String inputFile, List<Token> tokens) {
        String baseName = inputFile.substring(0, inputFile.lastIndexOf('.'));
        String lexReportFile = baseName + ".LEX";

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(lexReportFile));
            writer.println("Código da Equipe: 067");
            writer.println("Componentes:");
            writer.println("Eduardo Wanderley; eduardobraz.junior@ucsal.edu.br; (75)98207-4248");
            writer.println("RELATÓRIO DA ANÁLISE LÉXICA. Texto fonte analisado: " + inputFile);

            for (Token token : tokens) {
                writer.println("Lexeme: " + token.getLexeme() +
                        ", Código: " + token.getAtomCode() +
                        ", ÍndiceTabSimb: " + token.getIndexInSymbolTable() +
                        ", Linha: " + token.getLineOfAppearance() + ".");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateSymbolTableReport(String inputFile, SymbolTable symbolTable, List<Token> allTokens) {
        String baseName = inputFile.substring(0, inputFile.lastIndexOf('.'));
        String tabReportFile = baseName + ".TAB";

        Set<Token> tokens = symbolTable.getTokens();

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(tabReportFile));
            writer.println("Código da Equipe: 067");
            writer.println("Componentes:");
            writer.println("Eduardo Wanderley; eduardobraz.junior@ucsal.edu.br; (75)98207-4248");
            writer.println("RELATÓRIO DA TABELA DE SÍMBOLOS. Texto fonte analisado: " + inputFile);

            for (Token token : tokens) {
                writer.println("Entrada: " + token.getEnterNumber() +
                        ", Código: " + token.getAtomCode() +
                        ", Lexeme: " + token.getLexeme() +
                        ", QtdCharAntesTrunc: " + token.getQtdBeforeTrunc() +
                        ", QtdCharDepoisTrunc: " + token.getQtdAfterTrunc() +
                        ", TipoSimb: " + token.getSymbolType() +
                        ", Linhas: (" + getAllLinesThatLexemeAppears(token.getLexeme(), allTokens) + ").");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getAllLinesThatLexemeAppears(String lexeme, List<Token> allTokens) {
        String lines = "";
        for (Token token : allTokens) {
            if (token.getLexeme().equals(lexeme)) {
                if (lines.contains(token.getLineOfAppearance())) {
                    continue;
                }
                lines += token.getLineOfAppearance() + ", ";
            }
        }
        return lines.substring(0, lines.length() - 2);
    }
}
