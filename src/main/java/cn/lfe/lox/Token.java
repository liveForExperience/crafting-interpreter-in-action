package cn.lfe.lox;

import lombok.AllArgsConstructor;

/**
 * @author chen yue
 * @date 2025-06-29 19:53:13
 */
@AllArgsConstructor
public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
