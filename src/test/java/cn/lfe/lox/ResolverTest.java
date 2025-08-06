package cn.lfe.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static cn.lfe.lox.TokenType.*;

/**
 * Comprehensive unit tests for Resolver class
 */
public class ResolverTest {

    private Resolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new Resolver(new Interpreter());
    }

    @Test
    @DisplayName("Should resolve simple variable declaration and usage")
    void testSimpleVariableResolution() {
        // var x = 5; print x;
        List<Stmt> statements = Arrays.asList(
            new Stmt.Var(new Token(IDENTIFIER, "x", null, 1), 
                         new Expr.Literal(5.0)),
            new Stmt.Print(new Expr.Variable(new Token(IDENTIFIER, "x", null, 1)))
        );
        
        // Should not throw any errors
        assertDoesNotThrow(() -> resolver.resolve(statements));
    }

    @Test
    @DisplayName("Should resolve block scopes")
    void testBlockScopes() {
        // { var x = 1; { var x = 2; print x; } print x; }
        List<Stmt> innerBlock = Arrays.asList(
            new Stmt.Var(new Token(IDENTIFIER, "x", null, 1), 
                         new Expr.Literal(2.0)),
            new Stmt.Print(new Expr.Variable(new Token(IDENTIFIER, "x", null, 1)))
        );
        
        List<Stmt> outerBlock = Arrays.asList(
            new Stmt.Var(new Token(IDENTIFIER, "x", null, 1), 
                         new Expr.Literal(1.0)),
            new Stmt.Block(innerBlock),
            new Stmt.Print(new Expr.Variable(new Token(IDENTIFIER, "x", null, 1)))
        );
        
        List<Stmt> statements = List.of(new Stmt.Block(outerBlock));
        
        assertDoesNotThrow(() -> resolver.resolve(statements));
    }

    @Test
    @DisplayName("Should resolve function declarations")
    void testFunctionDeclarations() {
        // fun greet(name) { print "Hello, " + name; }
        List<Token> params = List.of(new Token(IDENTIFIER, "name", null, 1));
        List<Stmt> body = List.of(
                new Stmt.Print(
                        new Expr.Binary(
                                new Expr.Literal("Hello, "),
                                new Token(PLUS, "+", null, 1),
                                new Expr.Variable(new Token(IDENTIFIER, "name", null, 1))
                        )
                )
        );
        
        List<Stmt> statements = List.of(
                new Stmt.Function(new Token(IDENTIFIER, "greet", null, 1), params, body)
        );
        
        assertDoesNotThrow(() -> resolver.resolve(statements));
    }

    @Test
    @DisplayName("Should resolve function calls")
    void testFunctionCalls() {
        // fun add(a, b) { return a + b; } print add(1, 2);
        List<Token> params = Arrays.asList(
            new Token(IDENTIFIER, "a", null, 1),
            new Token(IDENTIFIER, "b", null, 1)
        );
        List<Stmt> body = List.of(
                new Stmt.Return(
                        new Token(RETURN, "return", null, 1),
                        new Expr.Binary(
                                new Expr.Variable(new Token(IDENTIFIER, "a", null, 1)),
                                new Token(PLUS, "+", null, 1),
                                new Expr.Variable(new Token(IDENTIFIER, "b", null, 1))
                        )
                )
        );
        
        List<Expr> args = Arrays.asList(
            new Expr.Literal(1.0),
            new Expr.Literal(2.0)
        );
        
        List<Stmt> statements = Arrays.asList(
            new Stmt.Function(new Token(IDENTIFIER, "add", null, 1), params, body),
            new Stmt.Print(
                new Expr.Call(
                    new Expr.Variable(new Token(IDENTIFIER, "add", null, 1)),
                    new Token(RIGHT_PAREN, ")", null, 1),
                    args
                )
            )
        );
        
        assertDoesNotThrow(() -> resolver.resolve(statements));
    }

    @Test
    @DisplayName("Should resolve assignment expressions")
    void testAssignmentExpressions() {
        // var x = 1; x = 2;
        List<Stmt> statements = Arrays.asList(
            new Stmt.Var(new Token(IDENTIFIER, "x", null, 1), 
                         new Expr.Literal(1.0)),
            new Stmt.Expression(
                new Expr.Assign(
                    new Token(IDENTIFIER, "x", null, 1),
                    new Expr.Literal(2.0)
                )
            )
        );
        
        assertDoesNotThrow(() -> resolver.resolve(statements));
    }

    @Test
    @DisplayName("Should resolve if statements")
    void testIfStatements() {
        // var x = 1; if (x > 0) print "positive";
        List<Stmt> statements = Arrays.asList(
            new Stmt.Var(new Token(IDENTIFIER, "x", null, 1), 
                         new Expr.Literal(1.0)),
            new Stmt.If(
                new Expr.Binary(
                    new Expr.Variable(new Token(IDENTIFIER, "x", null, 1)),
                    new Token(GREATER, ">", null, 1),
                    new Expr.Literal(0.0)
                ),
                new Stmt.Print(new Expr.Literal("positive")),
                null
            )
        );
        
        assertDoesNotThrow(() -> resolver.resolve(statements));
    }

    @Test
    @DisplayName("Should resolve while statements")
    void testWhileStatements() {
        // var i = 0; while (i < 5) { print i; i = i + 1; }
        List<Stmt> whileBody = Arrays.asList(
            new Stmt.Print(new Expr.Variable(new Token(IDENTIFIER, "i", null, 1))),
            new Stmt.Expression(
                new Expr.Assign(
                    new Token(IDENTIFIER, "i", null, 1),
                    new Expr.Binary(
                        new Expr.Variable(new Token(IDENTIFIER, "i", null, 1)),
                        new Token(PLUS, "+", null, 1),
                        new Expr.Literal(1.0)
                    )
                )
            )
        );
        
        List<Stmt> statements = Arrays.asList(
            new Stmt.Var(new Token(IDENTIFIER, "i", null, 1), 
                         new Expr.Literal(0.0)),
            new Stmt.While(
                new Expr.Binary(
                    new Expr.Variable(new Token(IDENTIFIER, "i", null, 1)),
                    new Token(LESS, "<", null, 1),
                    new Expr.Literal(5.0)
                ),
                new Stmt.Block(whileBody)
            )
        );
        
        assertDoesNotThrow(() -> resolver.resolve(statements));
    }

    @Test
    @DisplayName("Should resolve logical expressions")
    void testLogicalExpressions() {
        // var a = true; var b = false; print a and b;
        List<Stmt> statements = Arrays.asList(
            new Stmt.Var(new Token(IDENTIFIER, "a", null, 1), 
                         new Expr.Literal(true)),
            new Stmt.Var(new Token(IDENTIFIER, "b", null, 1), 
                         new Expr.Literal(false)),
            new Stmt.Print(
                new Expr.Logical(
                    new Expr.Variable(new Token(IDENTIFIER, "a", null, 1)),
                    new Token(AND, "and", null, 1),
                    new Expr.Variable(new Token(IDENTIFIER, "b", null, 1))
                )
            )
        );
        
        assertDoesNotThrow(() -> resolver.resolve(statements));
    }

    @Test
    @DisplayName("Should resolve nested function scopes")
    void testNestedFunctionScopes() {
        // fun outer() { fun inner() { return 42; } return inner(); }
        List<Stmt> innerBody = List.of(
                new Stmt.Return(
                        new Token(RETURN, "return", null, 1),
                        new Expr.Literal(42.0)
                )
        );
        
        List<Stmt> outerBody = Arrays.asList(
            new Stmt.Function(
                new Token(IDENTIFIER, "inner", null, 1),
                    List.of(),
                innerBody
            ),
            new Stmt.Return(
                new Token(RETURN, "return", null, 1),
                new Expr.Call(
                    new Expr.Variable(new Token(IDENTIFIER, "inner", null, 1)),
                    new Token(RIGHT_PAREN, ")", null, 1),
                        List.of()
                )
            )
        );
        
        List<Stmt> statements = List.of(
                new Stmt.Function(
                        new Token(IDENTIFIER, "outer", null, 1),
                        List.of(),
                        outerBody
                )
        );
        
        assertDoesNotThrow(() -> resolver.resolve(statements));
    }

    @Test
    @DisplayName("Should resolve return statements in functions")
    void testReturnStatements() {
        // fun getValue() { return 42; }
        List<Stmt> body = List.of(
                new Stmt.Return(
                        new Token(RETURN, "return", null, 1),
                        new Expr.Literal(42.0)
                )
        );
        
        List<Stmt> statements = List.of(
                new Stmt.Function(
                        new Token(IDENTIFIER, "getValue", null, 1),
                        List.of(),
                        body
                )
        );
        
        assertDoesNotThrow(() -> resolver.resolve(statements));
    }

    @Test
    @DisplayName("Should resolve complex nested scopes")
    void testComplexNestedScopes() {
        // var global = "global";
        // fun outer() {
        //   var outer_var = "outer";
        //   fun inner() {
        //     var inner_var = "inner";
        //     print global + outer_var + inner_var;
        //   }
        //   inner();
        // }
        List<Stmt> innerBody = Arrays.asList(
            new Stmt.Var(new Token(IDENTIFIER, "inner_var", null, 1), 
                         new Expr.Literal("inner")),
            new Stmt.Print(
                new Expr.Binary(
                    new Expr.Binary(
                        new Expr.Variable(new Token(IDENTIFIER, "global", null, 1)),
                        new Token(PLUS, "+", null, 1),
                        new Expr.Variable(new Token(IDENTIFIER, "outer_var", null, 1))
                    ),
                    new Token(PLUS, "+", null, 1),
                    new Expr.Variable(new Token(IDENTIFIER, "inner_var", null, 1))
                )
            )
        );

        List<Stmt> statements = getStmts(innerBody);

        assertDoesNotThrow(() -> resolver.resolve(statements));
    }

    private static List<Stmt> getStmts(List<Stmt> innerBody) {
        List<Stmt> outerBody = Arrays.asList(
            new Stmt.Var(new Token(IDENTIFIER, "outer_var", null, 1),
                         new Expr.Literal("outer")),
            new Stmt.Function(
                new Token(IDENTIFIER, "inner", null, 1),
                    List.of(),
                    innerBody
            ),
            new Stmt.Expression(
                new Expr.Call(
                    new Expr.Variable(new Token(IDENTIFIER, "inner", null, 1)),
                    new Token(RIGHT_PAREN, ")", null, 1),
                        List.of()
                )
            )
        );

        return Arrays.asList(
            new Stmt.Var(new Token(IDENTIFIER, "global", null, 1),
                         new Expr.Literal("global")),
            new Stmt.Function(
                new Token(IDENTIFIER, "outer", null, 1),
                    List.of(),
                outerBody
            )
        );
    }

    @Test
    @DisplayName("Should handle empty statements list")
    void testEmptyStatements() {
        List<Stmt> statements = List.of();
        assertDoesNotThrow(() -> resolver.resolve(statements));
    }

    @Test
    @DisplayName("Should resolve variable with no initializer")
    void testVariableWithNoInitializer() {
        // var x; print x;
        List<Stmt> statements = Arrays.asList(
            new Stmt.Var(new Token(IDENTIFIER, "x", null, 1), null),
            new Stmt.Print(new Expr.Variable(new Token(IDENTIFIER, "x", null, 1)))
        );
        
        assertDoesNotThrow(() -> resolver.resolve(statements));
    }
}
