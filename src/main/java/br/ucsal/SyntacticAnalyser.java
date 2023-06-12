package br.ucsal;

import java.io.File;
import java.util.List;

public class SyntacticAnalyser {
    public static void main(String[] args) {
        String inputFile = args.length > 0 ? args[0] : "";

        if(inputFile.isEmpty()) {
            File dir = new File(".");
            File[] files = dir.listFiles();

            if(files != null) {
                for(File file : files) {
                    if(file.isFile() && file.getName().endsWith(".231")) {
                        inputFile = file.getAbsolutePath();
                        break;
                    }
                }
            }

            if(inputFile.isEmpty()) {
                System.out.println("No .231 file found in current directory");
                return;
            }
        }

        LexicalAnalyzer analyzer = new LexicalAnalyzer(inputFile);
        List<Token> tokens = analyzer.analyze();
        Reporter.generateLexicalReport(analyzer.getInputFile(), tokens);
        Reporter.generateSymbolTableReport(analyzer.getInputFile(), analyzer.getSymbolTable(), tokens);
    }
}

