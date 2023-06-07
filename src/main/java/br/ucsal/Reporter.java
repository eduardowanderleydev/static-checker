package br.ucsal;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
                        ", Código: " + token.getCode() +
                        ", ÍndiceTabSimb: " + token.getIndex() +
                        ", Linha: " + token.getLine() + ".");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateSymbolTableReport(String inputFile, List<Token> tokens) {
        String baseName = inputFile.substring(0, inputFile.lastIndexOf('.'));
        String tabReportFile = baseName + ".TAB";

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(tabReportFile));
            writer.println("Código da Equipe: 067");
            writer.println("Componentes:");
            writer.println("Eduardo Wanderley; eduardobraz.junior@ucsal.edu.br; (75)98207-4248");
            writer.println("RELATÓRIO DA TABELA DE SÍMBOLOS. Texto fonte analisado: " + inputFile);

            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                writer.println("Entrada: " + (i + 1) +
                        ", Código: " + token.getCode() +
                        ", Lexeme: " + token.getLexeme() +
                        ", QtdCharAntesTrunc: " + token.getLexeme().length() +
                        ", QtdCharDepoisTrunc: " + token.getLexeme().length() +
                        ", TipoSimb: -" +
                        ", Linhas: (" + token.getLine() + ").");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
