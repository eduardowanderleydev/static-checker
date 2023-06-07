package br.ucsal;

import java.util.List;

public class Main {
    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.out.println("Erro: você deve fornecer o arquivo de entrada como argumento");
//            return;
//        }
//        String input = args[0];

        LexicalAnalyzer2 analyzer = new LexicalAnalyzer2("/home/eduardo/Workspace/ucsal/statickChecker/Teste.231");
        List<Token> tokens = analyzer.analyze();
        Reporter.generateLexicalReport(analyzer.getInputFile(), tokens);
        Reporter.generateSymbolTableReport(analyzer.getInputFile(), tokens);
    }
}
