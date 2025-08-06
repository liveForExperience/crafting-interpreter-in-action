package cn.lfe.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static cn.lfe.lox.TokenType.*;

/**
 * Comprehensive unit tests for Parser class
 */
public class ParserTest {

    @Test
    @DisplayName("Should parse number literals")
    void testNumberLiterals() {
        List<Token> tokens = Arrays.asList(
            new Token(NUMBER, "42", 42.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        );
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        
        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.Expression.class, statements.getFirst());
        Stmt.Expression exprStmt = (Stmt.Expression) statements.getFirst();
        assertInstanceOf(Expr.Literal.class, exprStmt.expression);
        assertEquals(42.0, ((Expr.Literal) exprStmt.expression).value);
    }

    @Test
    @DisplayName("Should parse binary expressions")
    void testBinaryExpressions() {
        List<Token> tokens = Arrays.asList(
            new Token(NUMBER, "1", 1.0, 1),
            new Token(PLUS, "+", null, 1),
            new Token(NUMBER, "2", 2.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        );
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        
        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.Expression.class, statements.getFirst());
        Stmt.Expression exprStmt = (Stmt.Expression) statements.getFirst();
        assertInstanceOf(Expr.Binary.class, exprStmt.expression);
        
        Expr.Binary binary = (Expr.Binary) exprStmt.expression;
        assertEquals(PLUS, binary.operator.type);
        assertEquals(1.0, ((Expr.Literal) binary.left).value);
        assertEquals(2.0, ((Expr.Literal) binary.right).value);
    }

    @Test
    @DisplayName("Should parse unary expressions")
    void testUnaryExpressions() {
        List<Token> tokens = Arrays.asList(
            new Token(MINUS, "-", null, 1),
            new Token(NUMBER, "5", 5.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        );
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        
        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.Expression.class, statements.getFirst());
        Stmt.Expression exprStmt = (Stmt.Expression) statements.getFirst();
        assertInstanceOf(Expr.Unary.class, exprStmt.expression);
        
        Expr.Unary unary = (Expr.Unary) exprStmt.expression;
        assertEquals(MINUS, unary.operator.type);
        assertEquals(5.0, ((Expr.Literal) unary.right).value);
    }

    @Test
    @DisplayName("Should parse variable declarations")
    void testVariableDeclarations() {
        List<Token> tokens = Arrays.asList(
            new Token(VAR, "var", null, 1),
            new Token(IDENTIFIER, "x", null, 1),
            new Token(EQUAL, "=", null, 1),
            new Token(NUMBER, "5", 5.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        );
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        
        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.Var.class, statements.getFirst());
        
        Stmt.Var varStmt = (Stmt.Var) statements.getFirst();
        assertEquals("x", varStmt.name.lexeme);
        assertInstanceOf(Expr.Literal.class, varStmt.initializer);
        assertEquals(5.0, ((Expr.Literal) varStmt.initializer).value);
    }

    @Test
    @DisplayName("Should parse function declarations")
    void testFunctionDeclarations() {
        Parser parser = getParser();
        List<Stmt> statements = parser.parse();
        
        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.Function.class, statements.getFirst());
        
        Stmt.Function funcStmt = (Stmt.Function) statements.getFirst();
        assertEquals("greet", funcStmt.name.lexeme);
        assertEquals(0, funcStmt.params.size());
        assertEquals(1, funcStmt.body.size());
    }

    private static Parser getParser() {
        List<Token> tokens = Arrays.asList(
            new Token(FUN, "fun", null, 1),
            new Token(IDENTIFIER, "greet", null, 1),
            new Token(LEFT_PAREN, "(", null, 1),
            new Token(RIGHT_PAREN, ")", null, 1),
            new Token(LEFT_BRACE, "{", null, 1),
            new Token(PRINT, "print", null, 1),
            new Token(STRING, "\"Hello!\"", "Hello!", 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(RIGHT_BRACE, "}", null, 1),
            new Token(EOF, "", null, 1)
        );
        return new Parser(tokens);
    }

    @Test
    @DisplayName("Should parse if statements")
    void testIfStatements() {
        List<Stmt> statements = getStmts();

        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.If.class, statements.getFirst());
        
        Stmt.If ifStmt = (Stmt.If) statements.getFirst();
        assertInstanceOf(Expr.Literal.class, ifStmt.condition);
        assertInstanceOf(Stmt.Print.class, ifStmt.thenBranch);
        assertNull(ifStmt.elseBranch);
    }

    private static List<Stmt> getStmts() {
        List<Token> tokens = Arrays.asList(
            new Token(IF, "if", null, 1),
            new Token(LEFT_PAREN, "(", null, 1),
            new Token(TRUE, "true", null, 1),
            new Token(RIGHT_PAREN, ")", null, 1),
            new Token(PRINT, "print", null, 1),
            new Token(STRING, "\"yes\"", "yes", 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        );
        Parser parser = new Parser(tokens);
        return parser.parse();
    }

    @Test
    @DisplayName("Should parse while statements")
    void testWhileStatements() {
        List<Token> tokens = Arrays.asList(
            new Token(WHILE, "while", null, 1),
            new Token(LEFT_PAREN, "(", null, 1),
            new Token(TRUE, "true", null, 1),
            new Token(RIGHT_PAREN, ")", null, 1),
            new Token(PRINT, "print", null, 1),
            new Token(STRING, "\"loop\"", "loop", 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        );
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        
        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.While.class, statements.getFirst());
        
        Stmt.While whileStmt = (Stmt.While) statements.getFirst();
        assertInstanceOf(Expr.Literal.class, whileStmt.condition);
        assertInstanceOf(Stmt.Print.class, whileStmt.body);
    }

    @Test
    @DisplayName("Should parse assignment expressions")
    void testAssignmentExpressions() {
        List<Token> tokens = Arrays.asList(
            new Token(IDENTIFIER, "x", null, 1),
            new Token(EQUAL, "=", null, 1),
            new Token(NUMBER, "5", 5.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        );
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        
        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.Expression.class, statements.getFirst());
        Stmt.Expression exprStmt = (Stmt.Expression) statements.getFirst();
        assertInstanceOf(Expr.Assign.class, exprStmt.expression);
        
        Expr.Assign assign = (Expr.Assign) exprStmt.expression;
        assertEquals("x", assign.name.lexeme);
        assertEquals(5.0, ((Expr.Literal) assign.value).value);
    }

    @Test
    @DisplayName("Should parse function calls")
    void testFunctionCalls() {
        List<Stmt> statements = getStmtList();

        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.Expression.class, statements.getFirst());
        Stmt.Expression exprStmt = (Stmt.Expression) statements.getFirst();
        assertInstanceOf(Expr.Call.class, exprStmt.expression);
        
        Expr.Call call = (Expr.Call) exprStmt.expression;
        assertEquals(2, call.arguments.size());
    }

    private static List<Stmt> getStmtList() {
        List<Token> tokens = Arrays.asList(
            new Token(IDENTIFIER, "foo", null, 1),
            new Token(LEFT_PAREN, "(", null, 1),
            new Token(NUMBER, "1", 1.0, 1),
            new Token(COMMA, ",", null, 1),
            new Token(NUMBER, "2", 2.0, 1),
            new Token(RIGHT_PAREN, ")", null, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        );
        Parser parser = new Parser(tokens);
        return parser.parse();
    }

    @Test
    @DisplayName("Should parse logical expressions")
    void testLogicalExpressions() {
        List<Token> tokens = Arrays.asList(
            new Token(TRUE, "true", null, 1),
            new Token(OR, "or", null, 1),
            new Token(FALSE, "false", null, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        );
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        
        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.Expression.class, statements.getFirst());
        Stmt.Expression exprStmt = (Stmt.Expression) statements.getFirst();
        assertInstanceOf(Expr.Logical.class, exprStmt.expression);
        
        Expr.Logical logical = (Expr.Logical) exprStmt.expression;
        assertEquals(OR, logical.operator.type);
    }

    @Test
    @DisplayName("Should parse block statements")
    void testBlockStatements() {
        List<Token> tokens = Arrays.asList(
            new Token(LEFT_BRACE, "{", null, 1),
            new Token(VAR, "var", null, 1),
            new Token(IDENTIFIER, "x", null, 1),
            new Token(EQUAL, "=", null, 1),
            new Token(NUMBER, "1", 1.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(RIGHT_BRACE, "}", null, 1),
            new Token(EOF, "", null, 1)
        );
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        
        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.Block.class, statements.getFirst());
        
        Stmt.Block blockStmt = (Stmt.Block) statements.getFirst();
        assertEquals(1, blockStmt.statements.size());
        assertInstanceOf(Stmt.Var.class, blockStmt.statements.getFirst());
    }

    @Test
    @DisplayName("Should parse return statements")
    void testReturnStatements() {
        List<Token> tokens = Arrays.asList(
            new Token(RETURN, "return", null, 1),
            new Token(NUMBER, "42", 42.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        );
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        
        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.Return.class, statements.getFirst());
        
        Stmt.Return returnStmt = (Stmt.Return) statements.getFirst();
        assertInstanceOf(Expr.Literal.class, returnStmt.value);
        assertEquals(42.0, ((Expr.Literal) returnStmt.value).value);
    }

    @Test
    @DisplayName("Should parse complex expressions with precedence")
    void testExpressionPrecedence() {
        // Test: 1 + 2 * 3
        List<Token> tokens = Arrays.asList(
            new Token(NUMBER, "1", 1.0, 1),
            new Token(PLUS, "+", null, 1),
            new Token(NUMBER, "2", 2.0, 1),
            new Token(STAR, "*", null, 1),
            new Token(NUMBER, "3", 3.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        );
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        
        assertEquals(1, statements.size());
        assertInstanceOf(Stmt.Expression.class, statements.getFirst());
        Stmt.Expression exprStmt = (Stmt.Expression) statements.getFirst();
        assertInstanceOf(Expr.Binary.class, exprStmt.expression);
        
        // Should be parsed as 1 + (2 * 3) due to precedence
        Expr.Binary outerBinary = (Expr.Binary) exprStmt.expression;
        assertEquals(PLUS, outerBinary.operator.type);
        assertInstanceOf(Expr.Binary.class, outerBinary.right);
        
        Expr.Binary innerBinary = (Expr.Binary) outerBinary.right;
        assertEquals(STAR, innerBinary.operator.type);
    }
}
