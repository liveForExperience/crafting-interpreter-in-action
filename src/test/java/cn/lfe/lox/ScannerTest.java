package cn.lfe.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static cn.lfe.lox.TokenType.*;

/**
 * Comprehensive unit tests for Scanner class
 * Covers all token types, edge cases, and error conditions
 */
public class ScannerTest {

    private Scanner scanner;

    @BeforeEach
    void setUp() {
        // Reset any static state if needed
    }

    // ========== Basic Single Character Tokens ==========
    
    @Test
    @DisplayName("Should scan single character tokens correctly")
    void testSingleCharacterTokens() {
        scanner = new Scanner("(){},.;+-*/");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(12, tokens.size()); // 10 tokens + EOF
        assertEquals(LEFT_PAREN, tokens.get(0).type);
        assertEquals(RIGHT_PAREN, tokens.get(1).type);
        assertEquals(LEFT_BRACE, tokens.get(2).type);
        assertEquals(RIGHT_BRACE, tokens.get(3).type);
        assertEquals(COMMA, tokens.get(4).type);
        assertEquals(DOT, tokens.get(5).type);
        assertEquals(SEMICOLON, tokens.get(6).type);
        assertEquals(PLUS, tokens.get(7).type);
        assertEquals(MINUS, tokens.get(8).type);
        assertEquals(STAR, tokens.get(9).type);
        assertEquals(SLASH, tokens.get(10).type);
        assertEquals(EOF, tokens.get(11).type);
    }

    // ========== Two Character Tokens ==========
    
    @Test
    @DisplayName("Should scan two character comparison tokens")
    void testTwoCharacterTokens() {
        scanner = new Scanner("!= == <= >=");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(5, tokens.size()); // 4 tokens + EOF
        assertEquals(BANG_EQUAL, tokens.get(0).type);
        assertEquals(EQUAL_EQUAL, tokens.get(1).type);
        assertEquals(LESS_EQUAL, tokens.get(2).type);
        assertEquals(GREATER_EQUAL, tokens.get(3).type);
    }

    @Test
    @DisplayName("Should scan single character when not followed by equals")
    void testSingleCharacterWhenNotEquals() {
        scanner = new Scanner("! = < >");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(5, tokens.size()); // 4 tokens + EOF
        assertEquals(BANG, tokens.get(0).type);
        assertEquals(EQUAL, tokens.get(1).type);
        assertEquals(LESS, tokens.get(2).type);
        assertEquals(GREATER, tokens.get(3).type);
    }

    // ========== Comments ==========
    
    @Test
    @DisplayName("Should ignore single line comments")
    void testSingleLineComments() {
        scanner = new Scanner("// This is a comment\nvar x = 5;");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(6, tokens.size()); // var, x, =, 5, ;, EOF
        assertEquals(VAR, tokens.get(0).type);
        assertEquals(IDENTIFIER, tokens.get(1).type);
        assertEquals("x", tokens.get(1).lexeme);
    }

    @Test
    @DisplayName("Should handle comment at end of file")
    void testCommentAtEndOfFile() {
        scanner = new Scanner("var x; // comment");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(4, tokens.size()); // var, x, ;, EOF
        assertEquals(VAR, tokens.get(0).type);
        assertEquals(IDENTIFIER, tokens.get(1).type);
        assertEquals(SEMICOLON, tokens.get(2).type);
    }

    @Test
    @DisplayName("Should handle slash without comment")
    void testSlashWithoutComment() {
        scanner = new Scanner("10 / 2");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(4, tokens.size()); // 10, /, 2, EOF
        assertEquals(NUMBER, tokens.get(0).type);
        assertEquals(SLASH, tokens.get(1).type);
        assertEquals(NUMBER, tokens.get(2).type);
    }

    // ========== Whitespace Handling ==========
    
    @Test
    @DisplayName("Should ignore whitespace characters")
    void testWhitespaceIgnored() {
        scanner = new Scanner("  \t\r  var   x  \t  ");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(3, tokens.size()); // var, x, EOF
        assertEquals(VAR, tokens.get(0).type);
        assertEquals(IDENTIFIER, tokens.get(1).type);
    }

    @Test
    @DisplayName("Should track line numbers correctly")
    void testLineNumberTracking() {
        scanner = new Scanner("var x;\nvar y;\n\nvar z;");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(1, tokens.get(0).line); // var (line 1)
        assertEquals(1, tokens.get(1).line); // x (line 1)
        assertEquals(2, tokens.get(3).line); // var (line 2)
        assertEquals(4, tokens.get(6).line); // var (line 4)
    }

    // ========== String Literals ==========
    
    @Test
    @DisplayName("Should scan string literals correctly")
    void testStringLiterals() {
        scanner = new Scanner("\"hello world\"");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(2, tokens.size()); // string, EOF
        assertEquals(STRING, tokens.get(0).type);
        assertEquals("\"hello world\"", tokens.get(0).lexeme);
        assertEquals("hello world", tokens.get(0).literal);
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyString() {
        scanner = new Scanner("\"\"");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(2, tokens.size());
        assertEquals(STRING, tokens.get(0).type);
        assertEquals("", tokens.get(0).literal);
    }

    @Test
    @DisplayName("Should handle multiline strings")
    void testMultilineString() {
        scanner = new Scanner("\"line 1\nline 2\"");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(2, tokens.size());
        assertEquals(STRING, tokens.get(0).type);
        assertEquals("line 1\nline 2", tokens.get(0).literal);
        assertEquals(2, tokens.get(0).line); // Should track line correctly
    }

    @Test
    @DisplayName("Should handle unterminated strings")
    void testUnterminatedString() {
        // This test assumes Lox.error is called but doesn't throw exception
        scanner = new Scanner("\"unterminated");
        List<Token> tokens = scanner.scanTokens();
        
        // Should still produce EOF token
        assertEquals(1, tokens.size());
        assertEquals(EOF, tokens.get(0).type);
    }

    // ========== Number Literals ==========
    
    @Test
    @DisplayName("Should scan integer numbers")
    void testIntegerNumbers() {
        scanner = new Scanner("123");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(2, tokens.size());
        assertEquals(NUMBER, tokens.get(0).type);
        assertEquals("123", tokens.get(0).lexeme);
        assertEquals(123.0, tokens.get(0).literal);
    }

    @Test
    @DisplayName("Should scan decimal numbers")
    void testDecimalNumbers() {
        scanner = new Scanner("123.456");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(2, tokens.size());
        assertEquals(NUMBER, tokens.get(0).type);
        assertEquals("123.456", tokens.get(0).lexeme);
        assertEquals(123.456, tokens.get(0).literal);
    }

    @Test
    @DisplayName("Should handle numbers starting with decimal")
    void testNumbersStartingWithDecimal() {
        scanner = new Scanner("0.5");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(2, tokens.size());
        assertEquals(NUMBER, tokens.get(0).type);
        assertEquals(0.5, tokens.get(0).literal);
    }

    @Test
    @DisplayName("Should handle dot without following digit as separate token")
    void testDotWithoutDigit() {
        scanner = new Scanner("123.");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(3, tokens.size()); // 123, ., EOF
        assertEquals(NUMBER, tokens.get(0).type);
        assertEquals(123.0, tokens.get(0).literal);
        assertEquals(DOT, tokens.get(1).type);
    }

    // ========== Identifiers and Keywords ==========
    
    @ParameterizedTest
    @ValueSource(strings = {"and", "class", "else", "false", "for", "fun", "if", "nil", "or", "print", "return", "super", "this", "true", "var", "while"})
    @DisplayName("Should recognize keywords correctly")
    void testKeywords(String keyword) {
        scanner = new Scanner(keyword);
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(2, tokens.size());
        assertNotEquals(IDENTIFIER, tokens.get(0).type);
        assertEquals(keyword, tokens.get(0).lexeme);
    }

    @Test
    @DisplayName("Should scan identifiers correctly")
    void testIdentifiers() {
        scanner = new Scanner("variable_name myVar _private var123");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(5, tokens.size()); // 4 identifiers + EOF
        assertEquals(IDENTIFIER, tokens.get(0).type);
        assertEquals("variable_name", tokens.get(0).lexeme);
        assertEquals(IDENTIFIER, tokens.get(1).type);
        assertEquals("myVar", tokens.get(1).lexeme);
        assertEquals(IDENTIFIER, tokens.get(2).type);
        assertEquals("_private", tokens.get(2).lexeme);
        assertEquals(IDENTIFIER, tokens.get(3).type);
        assertEquals("var123", tokens.get(3).lexeme);
    }

    @Test
    @DisplayName("Should distinguish keywords from similar identifiers")
    void testKeywordVsIdentifier() {
        scanner = new Scanner("var variable vars");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(4, tokens.size());
        assertEquals(VAR, tokens.get(0).type);
        assertEquals(IDENTIFIER, tokens.get(1).type);
        assertEquals("variable", tokens.get(1).lexeme);
        assertEquals(IDENTIFIER, tokens.get(2).type);
        assertEquals("vars", tokens.get(2).lexeme);
    }

    // ========== Complex Expressions ==========
    
    @Test
    @DisplayName("Should scan complex arithmetic expression")
    void testComplexArithmetic() {
        scanner = new Scanner("(10 + 5) * 2.5 - 3");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(10, tokens.size()); // (, 10, +, 5, ), *, 2.5, -, 3, EOF
        assertEquals(LEFT_PAREN, tokens.get(0).type);
        assertEquals(NUMBER, tokens.get(1).type);
        assertEquals(10.0, tokens.get(1).literal);
        assertEquals(PLUS, tokens.get(2).type);
        assertEquals(NUMBER, tokens.get(3).type);
        assertEquals(5.0, tokens.get(3).literal);
        assertEquals(RIGHT_PAREN, tokens.get(4).type);
        assertEquals(STAR, tokens.get(5).type);
        assertEquals(NUMBER, tokens.get(6).type);
        assertEquals(2.5, tokens.get(6).literal);
        assertEquals(MINUS, tokens.get(7).type);
        assertEquals(NUMBER, tokens.get(8).type);
        assertEquals(3.0, tokens.get(8).literal);
    }

    @Test
    @DisplayName("Should scan variable assignment")
    void testVariableAssignment() {
        scanner = new Scanner("var x = 42;");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(6, tokens.size()); // var, x, =, 42, ;, EOF
        assertEquals(VAR, tokens.get(0).type);
        assertEquals(IDENTIFIER, tokens.get(1).type);
        assertEquals("x", tokens.get(1).lexeme);
        assertEquals(EQUAL, tokens.get(2).type);
        assertEquals(NUMBER, tokens.get(3).type);
        assertEquals(42.0, tokens.get(3).literal);
        assertEquals(SEMICOLON, tokens.get(4).type);
    }

    @Test
    @DisplayName("Should scan function definition")
    void testFunctionDefinition() {
        scanner = new Scanner("fun greet(name) { print \"Hello, \" + name; }");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(13, tokens.size());
        assertEquals(FUN, tokens.get(0).type);
        assertEquals(IDENTIFIER, tokens.get(1).type);
        assertEquals("greet", tokens.get(1).lexeme);
        assertEquals(LEFT_PAREN, tokens.get(2).type);
        assertEquals(IDENTIFIER, tokens.get(3).type);
        assertEquals("name", tokens.get(3).lexeme);
        assertEquals(RIGHT_PAREN, tokens.get(4).type);
        assertEquals(LEFT_BRACE, tokens.get(5).type);
        assertEquals(PRINT, tokens.get(6).type);
        assertEquals(STRING, tokens.get(7).type);
        assertEquals("Hello, ", tokens.get(7).literal);
        assertEquals(PLUS, tokens.get(8).type);
        assertEquals(IDENTIFIER, tokens.get(9).type);
        assertEquals("name", tokens.get(9).lexeme);
        assertEquals(SEMICOLON, tokens.get(10).type);
        assertEquals(RIGHT_BRACE, tokens.get(11).type);
    }

    // ========== Edge Cases ==========
    
    @Test
    @DisplayName("Should handle empty source")
    void testEmptySource() {
        scanner = new Scanner("");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(1, tokens.size());
        assertEquals(EOF, tokens.get(0).type);
    }

    @Test
    @DisplayName("Should handle source with only whitespace")
    void testOnlyWhitespace() {
        scanner = new Scanner("   \t\n\r  ");
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(1, tokens.size());
        assertEquals(EOF, tokens.get(0).type);
        assertEquals(2, tokens.get(0).line); // Should track newline
    }

    @Test
    @DisplayName("Should handle unexpected characters")
    void testUnexpectedCharacters() {
        // This test assumes Lox.error is called but scanning continues
        scanner = new Scanner("var x @ 5;");
        List<Token> tokens = scanner.scanTokens();
        
        // Should skip the @ character and continue
        assertEquals(5, tokens.size()); // var, x, 5, ;, EOF
        assertEquals(VAR, tokens.get(0).type);
        assertEquals(IDENTIFIER, tokens.get(1).type);
        assertEquals(NUMBER, tokens.get(2).type);
        assertEquals(SEMICOLON, tokens.get(3).type);
    }

    // ========== Integration Tests ==========
    
    @Test
    @DisplayName("Should scan complete Lox program")
    void testCompleteLoxProgram() {
        String program = """
            fun fibonacci(n) {
                if (n <= 1) return n;
                return fibonacci(n - 2) + fibonacci(n - 1);
            }
            
            var result = fibonacci(10);
            print result;
            """;
        
        scanner = new Scanner(program);
        List<Token> tokens = scanner.scanTokens();
        
        // Verify key tokens are present
        assertTrue(tokens.stream().anyMatch(t -> t.type == FUN));
        assertTrue(tokens.stream().anyMatch(t -> t.type == IF));
        assertTrue(tokens.stream().anyMatch(t -> t.type == RETURN));
        assertTrue(tokens.stream().anyMatch(t -> t.type == VAR));
        assertTrue(tokens.stream().anyMatch(t -> t.type == PRINT));
        assertTrue(tokens.stream().anyMatch(t -> t.type == IDENTIFIER && "fibonacci".equals(t.lexeme)));
        assertTrue(tokens.stream().anyMatch(t -> t.type == NUMBER && (Double) t.literal == 10.0));
        
        // Last token should be EOF
        assertEquals(EOF, tokens.get(tokens.size() - 1).type);
    }

    @Test
    @DisplayName("Should maintain correct token positions and line numbers")
    void testTokenPositionsAndLines() {
        String source = "var x = 1;\nvar y = 2;";
        scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        // Check line numbers
        assertEquals(1, tokens.get(0).line); // var
        assertEquals(1, tokens.get(1).line); // x
        assertEquals(1, tokens.get(2).line); // =
        assertEquals(1, tokens.get(3).line); // 1
        assertEquals(1, tokens.get(4).line); // ;
        assertEquals(2, tokens.get(5).line); // var
        assertEquals(2, tokens.get(6).line); // y
        assertEquals(2, tokens.get(7).line); // =
        assertEquals(2, tokens.get(8).line); // 2
        assertEquals(2, tokens.get(9).line); // ;
        
        // Check lexemes
        assertEquals("var", tokens.get(0).lexeme);
        assertEquals("x", tokens.get(1).lexeme);
        assertEquals("=", tokens.get(2).lexeme);
        assertEquals("1", tokens.get(3).lexeme);
        assertEquals(";", tokens.get(4).lexeme);
    }
}
