package br.ucsal;

public class Token {
    private int enterNumber;
    private int atomCode;
    private String lexeme;
    private int qtdBeforeTrunc;
    private int qtdAfterTrunc;
    private String symbolType;
    private String lineOfAppearance;
    private Integer indexInSymbolTable;

    public Token(int enterNumber, int atomCode, String lexeme, int qtdBeforeTrunc, int qtdAfterTrunc, String symbolType, String firstLineOfAppearance) {
        this.enterNumber = enterNumber;
        this.atomCode = atomCode;
        this.lexeme = lexeme;
        this.qtdBeforeTrunc = qtdBeforeTrunc;
        this.qtdAfterTrunc = qtdAfterTrunc;
        this.symbolType = symbolType;
        this.lineOfAppearance = firstLineOfAppearance;
    }

    public int getEnterNumber() {
        return enterNumber;
    }

    public void setEnterNumber(int enterNumber) {
        this.enterNumber = enterNumber;
    }

    public int getAtomCode() {
        return atomCode;
    }

    public void setAtomCode(int atomCode) {
        this.atomCode = atomCode;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public int getQtdBeforeTrunc() {
        return qtdBeforeTrunc;
    }

    public void setQtdBeforeTrunc(int qtdBeforeTrunc) {
        this.qtdBeforeTrunc = qtdBeforeTrunc;
    }

    public int getQtdAfterTrunc() {
        return qtdAfterTrunc;
    }

    public void setQtdAfterTrunc(int qtdAfterTrunc) {
        this.qtdAfterTrunc = qtdAfterTrunc;
    }

    public String getSymbolType() {
        return symbolType;
    }

    public void setSymbolType(String symbolType) {
        this.symbolType = symbolType;
    }

    public String getLineOfAppearance() {
        return lineOfAppearance;
    }

    public void setLineOfAppearance(String lineOfAppearance) {
        this.lineOfAppearance = lineOfAppearance;
    }

    public String getIndexInSymbolTable() {
        return indexInSymbolTable == null ? "-" : indexInSymbolTable.toString();
    }

    public void setIndexInSymbolTable(Integer indexInSymbolTable) {
        this.indexInSymbolTable = indexInSymbolTable;
    }
}
