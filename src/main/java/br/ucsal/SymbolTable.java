package br.ucsal;

import java.util.*;

public class SymbolTable {
    private Set<Token> tokens = new TreeSet<>(Comparator.comparing(Token::getLexeme));

    public void addToken(Token token) {
        tokens.add(token);
    }

    public Set<Token> getTokens() {
        return tokens;
    }
}
