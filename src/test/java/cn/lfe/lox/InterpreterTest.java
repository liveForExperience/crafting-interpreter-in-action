package cn.lfe.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static cn.lfe.lox.TokenType.*;

/**
 * Comprehensive unit tests for Interpreter class
 */
public class InterpreterTest {

    private Interpreter interpreter;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        interpreter = new Interpreter();
        // Capture System.out for testing print statements
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should evaluate literal expressions")
    void testLiteralExpressions() {
        // Test number literal
        Expr.Literal numberLiteral = new Expr.Literal(42.0);
        Object result = interpreter.visitLiteralExpr(numberLiteral);
        assertEquals(42.0, result);

        // Test string literal
        Expr.Literal stringLiteral = new Expr.Literal("hello");
        result = interpreter.visitLiteralExpr(stringLiteral);
        assertEquals("hello", result);

        // Test boolean literal
        Expr.Literal boolLiteral = new Expr.Literal(true);
        result = interpreter.visitLiteralExpr(boolLiteral);
        assertEquals(true, result);

        // Test nil literal
        Expr.Literal nilLiteral = new Expr.Literal(null);
        result = interpreter.visitLiteralExpr(nilLiteral);
        assertNull(result);
    }

    @Test
    @DisplayName("Should evaluate binary arithmetic expressions")
    void testBinaryArithmeticExpressions() {
        // Test addition: 1 + 2
        Expr.Binary addition = new Expr.Binary(
            new Expr.Literal(1.0),
            new Token(PLUS, "+", null, 1),
            new Expr.Literal(2.0)
        );
        Object result = interpreter.visitBinaryExpr(addition);
        assertEquals(3.0, result);

        // Test subtraction: 5 - 3
        Expr.Binary subtraction = new Expr.Binary(
            new Expr.Literal(5.0),
            new Token(MINUS, "-", null, 1),
            new Expr.Literal(3.0)
        );
        result = interpreter.visitBinaryExpr(subtraction);
        assertEquals(2.0, result);

        // Test multiplication: 4 * 3
        Expr.Binary multiplication = new Expr.Binary(
            new Expr.Literal(4.0),
            new Token(STAR, "*", null, 1),
            new Expr.Literal(3.0)
        );
        result = interpreter.visitBinaryExpr(multiplication);
        assertEquals(12.0, result);

        // Test division: 10 / 2
        Expr.Binary division = new Expr.Binary(
            new Expr.Literal(10.0),
            new Token(SLASH, "/", null, 1),
            new Expr.Literal(2.0)
        );
        result = interpreter.visitBinaryExpr(division);
        assertEquals(5.0, result);
    }

    @Test
    @DisplayName("Should evaluate binary comparison expressions")
    void testBinaryComparisonExpressions() {
        // Test greater than: 5 > 3
        Expr.Binary greater = new Expr.Binary(
            new Expr.Literal(5.0),
            new Token(GREATER, ">", null, 1),
            new Expr.Literal(3.0)
        );
        Object result = interpreter.visitBinaryExpr(greater);
        assertEquals(true, result);

        // Test less than: 2 < 5
        Expr.Binary less = new Expr.Binary(
            new Expr.Literal(2.0),
            new Token(LESS, "<", null, 1),
            new Expr.Literal(5.0)
        );
        result = interpreter.visitBinaryExpr(less);
        assertEquals(true, result);

        // Test greater equal: 5 >= 5
        Expr.Binary greaterEqual = new Expr.Binary(
            new Expr.Literal(5.0),
            new Token(GREATER_EQUAL, ">=", null, 1),
            new Expr.Literal(5.0)
        );
        result = interpreter.visitBinaryExpr(greaterEqual);
        assertEquals(true, result);

        // Test less equal: 3 <= 5
        Expr.Binary lessEqual = new Expr.Binary(
            new Expr.Literal(3.0),
            new Token(LESS_EQUAL, "<=", null, 1),
            new Expr.Literal(5.0)
        );
        result = interpreter.visitBinaryExpr(lessEqual);
        assertEquals(true, result);
    }

    @Test
    @DisplayName("Should evaluate equality expressions")
    void testEqualityExpressions() {
        // Test equal: 5 == 5
        Expr.Binary equal = new Expr.Binary(
            new Expr.Literal(5.0),
            new Token(EQUAL_EQUAL, "==", null, 1),
            new Expr.Literal(5.0)
        );
        Object result = interpreter.visitBinaryExpr(equal);
        assertEquals(true, result);

        // Test not equal: 5 != 3
        Expr.Binary notEqual = new Expr.Binary(
            new Expr.Literal(5.0),
            new Token(BANG_EQUAL, "!=", null, 1),
            new Expr.Literal(3.0)
        );
        result = interpreter.visitBinaryExpr(notEqual);
        assertEquals(true, result);
    }

    @Test
    @DisplayName("Should evaluate unary expressions")
    void testUnaryExpressions() {
        // Test unary minus: -5
        Expr.Unary unaryMinus = new Expr.Unary(
            new Token(MINUS, "-", null, 1),
            new Expr.Literal(5.0)
        );
        Object result = interpreter.visitUnaryExpr(unaryMinus);
        assertEquals(-5.0, result);

        // Test logical not: !true
        Expr.Unary logicalNot = new Expr.Unary(
            new Token(BANG, "!", null, 1),
            new Expr.Literal(true)
        );
        result = interpreter.visitUnaryExpr(logicalNot);
        assertEquals(false, result);

        // Test logical not with falsy value: !nil
        Expr.Unary logicalNotNil = new Expr.Unary(
            new Token(BANG, "!", null, 1),
            new Expr.Literal(null)
        );
        result = interpreter.visitUnaryExpr(logicalNotNil);
        assertEquals(true, result);
    }

    @Test
    @DisplayName("Should evaluate grouping expressions")
    void testGroupingExpressions() {
        // Test: (1 + 2) * 3
        Expr.Grouping grouping = new Expr.Grouping(
            new Expr.Binary(
                new Expr.Literal(1.0),
                new Token(PLUS, "+", null, 1),
                new Expr.Literal(2.0)
            )
        );
        
        Expr.Binary multiplication = new Expr.Binary(
            grouping,
            new Token(STAR, "*", null, 1),
            new Expr.Literal(3.0)
        );
        
        Object result = interpreter.visitBinaryExpr(multiplication);
        assertEquals(9.0, result);
    }

    @Test
    @DisplayName("Should execute print statements")
    void testPrintStatements() {
        // Test: print "Hello, World!";
        Stmt.Print printStmt = new Stmt.Print(new Expr.Literal("Hello, World!"));
        
        interpreter.visitPrintStmt(printStmt);
        
        String output = outputStream.toString().trim();
        assertEquals("Hello, World!", output);
        
        tearDown();
    }

    @Test
    @DisplayName("Should execute variable declarations and access")
    void testVariableDeclarationsAndAccess() {
        // Test: var x = 5; print x;
        Stmt.Var varDecl = new Stmt.Var(
            new Token(IDENTIFIER, "x", null, 1),
            new Expr.Literal(5.0)
        );
        
        interpreter.visitVarStmt(varDecl);
        
        Expr.Variable varExpr = new Expr.Variable(new Token(IDENTIFIER, "x", null, 1));
        Object result = interpreter.visitVariableExpr(varExpr);
        assertEquals(5.0, result);
    }

    @Test
    @DisplayName("Should execute assignment expressions")
    void testAssignmentExpressions() {
        // Test: var x = 1; x = 5;
        Stmt.Var varDecl = new Stmt.Var(
            new Token(IDENTIFIER, "x", null, 1),
            new Expr.Literal(1.0)
        );
        interpreter.visitVarStmt(varDecl);
        
        Expr.Assign assignment = new Expr.Assign(
            new Token(IDENTIFIER, "x", null, 1),
            new Expr.Literal(5.0)
        );
        
        Object result = interpreter.visitAssignExpr(assignment);
        assertEquals(5.0, result);
        
        // Verify the variable was updated
        Expr.Variable varExpr = new Expr.Variable(new Token(IDENTIFIER, "x", null, 1));
        result = interpreter.visitVariableExpr(varExpr);
        assertEquals(5.0, result);
    }

    @Test
    @DisplayName("Should execute block statements")
    void testBlockStatements() {
        // Test: { var x = 1; print x; }
        List<Stmt> statements = List.of(
            new Stmt.Block(List.of(
                new Stmt.Var(new Token(IDENTIFIER, "x", null, 1), new Expr.Literal(1.0)),
                new Stmt.Print(new Expr.Variable(new Token(IDENTIFIER, "x", null, 1)))
            ))
        );
        
        // Need to run resolver first for proper variable scope resolution
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
        
        interpreter.interpret(statements);
        
        String output = outputStream.toString().trim();
        assertEquals("1", output);
        
        tearDown();
    }

    @Test
    @DisplayName("Should execute if statements")
    void testIfStatements() {
        // Test: if (true) print "yes";
        Stmt.If ifStmt = new Stmt.If(
            new Expr.Literal(true),
            new Stmt.Print(new Expr.Literal("yes")),
            null
        );
        
        interpreter.visitIfStmt(ifStmt);
        
        String output = outputStream.toString().trim();
        assertEquals("yes", output);
        
        tearDown();
    }

    @Test
    @DisplayName("Should execute if-else statements")
    void testIfElseStatements() {
        // Test: if (false) print "no"; else print "yes";
        Stmt.If ifElseStmt = new Stmt.If(
            new Expr.Literal(false),
            new Stmt.Print(new Expr.Literal("no")),
            new Stmt.Print(new Expr.Literal("yes"))
        );
        
        interpreter.visitIfStmt(ifElseStmt);
        
        String output = outputStream.toString().trim();
        assertEquals("yes", output);
        
        tearDown();
    }

    @Test
    @DisplayName("Should execute logical expressions")
    void testLogicalExpressions() {
        // Test logical OR: true or false
        Expr.Logical logicalOr = new Expr.Logical(
            new Expr.Literal(true),
            new Token(OR, "or", null, 1),
            new Expr.Literal(false)
        );
        Object result = interpreter.visitLogicalExpr(logicalOr);
        assertEquals(true, result);

        // Test logical AND: true and false
        Expr.Logical logicalAnd = new Expr.Logical(
            new Expr.Literal(true),
            new Token(AND, "and", null, 1),
            new Expr.Literal(false)
        );
        result = interpreter.visitLogicalExpr(logicalAnd);
        assertEquals(false, result);
    }

    @Test
    @DisplayName("Should handle function declarations and calls")
    void testFunctionDeclarationsAndCalls() {
        // Test: fun greet() { print "Hello!"; } greet();
        List<Stmt> body = List.of(
                new Stmt.Print(new Expr.Literal("Hello!"))
        );
        
        Stmt.Function funcDecl = new Stmt.Function(
            new Token(IDENTIFIER, "greet", null, 1),
                List.of(),
            body
        );
        
        interpreter.visitFunctionStmt(funcDecl);
        
        // Call the function
        Expr.Call funcCall = new Expr.Call(
            new Expr.Variable(new Token(IDENTIFIER, "greet", null, 1)),
            new Token(RIGHT_PAREN, ")", null, 1),
                List.of()
        );
        
        interpreter.visitCallExpr(funcCall);
        
        String output = outputStream.toString().trim();
        assertEquals("Hello!", output);
        
        tearDown();
    }

    @Test
    @DisplayName("Should handle string concatenation")
    void testStringConcatenation() {
        // Test: "Hello, " + "World!"
        Expr.Binary concat = new Expr.Binary(
            new Expr.Literal("Hello, "),
            new Token(PLUS, "+", null, 1),
            new Expr.Literal("World!")
        );
        
        Object result = interpreter.visitBinaryExpr(concat);
        assertEquals("Hello, World!", result);
    }

    @Test
    @DisplayName("Should handle truthiness correctly")
    void testTruthiness() {
        // Test various truthy/falsy values
        
        // nil is falsy
        Expr.Unary notNil = new Expr.Unary(
            new Token(BANG, "!", null, 1),
            new Expr.Literal(null)
        );
        Object result = interpreter.visitUnaryExpr(notNil);
        assertEquals(true, result);
        
        // false is falsy
        Expr.Unary notFalse = new Expr.Unary(
            new Token(BANG, "!", null, 1),
            new Expr.Literal(false)
        );
        result = interpreter.visitUnaryExpr(notFalse);
        assertEquals(true, result);
        
        // Everything else is truthy (including 0)
        Expr.Unary notZero = new Expr.Unary(
            new Token(BANG, "!", null, 1),
            new Expr.Literal(0.0)
        );
        result = interpreter.visitUnaryExpr(notZero);
        assertEquals(false, result);
        
        // Empty string is truthy
        Expr.Unary notEmptyString = new Expr.Unary(
            new Token(BANG, "!", null, 1),
            new Expr.Literal("")
        );
        result = interpreter.visitUnaryExpr(notEmptyString);
        assertEquals(false, result);
    }

    @Test
    @DisplayName("Should handle while loops")
    void testWhileLoops() {
        // Test: var i = 0; while (i < 3) { print i; i = i + 1; }
        interpreter.visitVarStmt(new Stmt.Var(
            new Token(IDENTIFIER, "i", null, 1),
            new Expr.Literal(0.0)
        ));
        
        List<Stmt> whileBody = Arrays.asList(
            new Stmt.Print(new Expr.Variable(new Token(IDENTIFIER, "i", null, 1))),
            new Stmt.Expression(new Expr.Assign(
                new Token(IDENTIFIER, "i", null, 1),
                new Expr.Binary(
                    new Expr.Variable(new Token(IDENTIFIER, "i", null, 1)),
                    new Token(PLUS, "+", null, 1),
                    new Expr.Literal(1.0)
                )
            ))
        );
        
        Stmt.While whileStmt = new Stmt.While(
            new Expr.Binary(
                new Expr.Variable(new Token(IDENTIFIER, "i", null, 1)),
                new Token(LESS, "<", null, 1),
                new Expr.Literal(3.0)
            ),
            new Stmt.Block(whileBody)
        );
        
        interpreter.visitWhileStmt(whileStmt);
        
        String output = outputStream.toString().trim();
        assertEquals("0\n1\n2", output);
        
        tearDown();
    }
}
