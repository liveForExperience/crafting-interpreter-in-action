package cn.lfe.lox;

import lombok.Getter;

public class RuntimeError extends RuntimeException {
    @Getter
    private final Token token;
    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
