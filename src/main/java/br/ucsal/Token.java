package br.ucsal;

public class Token {
    private String lexeme;
    private Integer code;
    private Integer index;
    private Integer line;

    public Token(String lexeme, Integer code, Integer index, Integer line) {
        this.lexeme = lexeme;
        this.code = code;
        this.index = index;
        this.line = line;
    }

    public String getLexeme() {
        return lexeme;
    }

    public Integer getCode() {
        return code;
    }

    public Integer getIndex() {
        return index;
    }

    public Integer getLine() {
        return line;
    }
}
