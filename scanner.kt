package klox.lox                                           

class Scanner(lox: Lox, source: String) {

    companion object {
        val keywords = mapOf(
            "and" to TokenType.AND,
            "class" to TokenType.CLASS,                 
            "else" to TokenType.ELSE,                    
            "false" to TokenType.FALSE,                     
            "for" to TokenType.FOR,                 
            "fun" to TokenType.FUN,                       
            "if" to TokenType.IF,                      
            "nil" to TokenType.NIL,                       
            "or" to TokenType.OR,                      
            "print" to TokenType.PRINT,                     
            "return" to TokenType.RETURN,                   
            "super" to TokenType.SUPER,                  
            "this" to TokenType.THIS,                    
            "true" to TokenType.TRUE,                      
            "var" to TokenType.VAR,                     
            "while" to TokenType.WHILE
        );
    }

    private val lox = lox;
    private val source = source;
    private val tokens = mutableListOf<Token>();
    private var start = 0;                               
    private var current = 0;                             
    private var line = 1; 

    init {

    }

    fun scanTokens(): MutableList<Token> {
        while (!isAtEnd()) {                            
          // We are at the beginning of the next lexeme.
          start = current;                              
          scanToken();                                  
        }
    
        tokens.add(Token(TokenType.EOF, "", null, line));     
        return tokens;                                  
    }

    private fun scanToken() {                     
        var c = advance();                          
        when (c) {                                 
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)    
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)          
            '.' -> addToken(TokenType.DOT)           
            '-' -> addToken(TokenType.MINUS)          
            '+' -> addToken(TokenType.PLUS)           
            ';' -> addToken(TokenType.SEMICOLON)      
            '*' -> addToken(TokenType.STAR)
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '/' -> {                                                       
                if (match('/')) {                                             
                    // A comment goes until the end of the line.                
                    while (peek() != '\n' && !isAtEnd())
                        advance();             
                } else {                                                      
                    addToken(TokenType.SLASH);                                            
                }
            }

            ' ', '\r', '\t' -> {} // Ignore whitespace.

            '\n' -> line++

            '"' -> string()

            else -> {
                if (isDigit(c)) {                          
                    number();
                } else if (isAlpha(c)) {                   
                    identifier();
                } else {
                    lox.error(line, "Unexpected character.");
                }
            }
        }                                            
    }

    private fun identifier() {                
        while (isAlphaNumeric(peek()))
            advance();

        // See if the identifier is a reserved word.   
        val text = source.substring(start, current);

        var type = keywords.get(text);           
        if (type == null)
            type = TokenType.IDENTIFIER;           
        addToken(type);
    }

    private fun number() {                                     
        while (isDigit(peek()))
            advance();
    
        // Look for a fractional part.                            
        if (peek() == '.' && isDigit(peekNext())) {               
          // Consume the "."                                      
          advance();                                              
    
          while (isDigit(peek()))
            advance();                      
        }                                                         
    
        addToken(TokenType.NUMBER, source.substring(start, current).toDouble());
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {                   
          if (peek() == '\n')
            line++;                           
          advance();                                            
        }
    
        // Unterminated string.                                 
        if (isAtEnd()) {                                        
          lox.error(line, "Unterminated string.");              
          return;                                               
        }                                                       
    
        // The closing ".                                       
        advance();                                              
    
        // Trim the surrounding quotes.                         
        val value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);                                
    }

    private fun match(expected: Char): Boolean {                 
        if (isAtEnd())
            return false;                         
        if (source[current] != expected)
            return false;
    
        current++;                                           
        return true;                                         
    }

    private fun peek(): Char {
        if (isAtEnd())
            return '\u0000';   
        return source[current];
    }

    private fun peekNext(): Char {                         
        if (current + 1 >= source.length)
            return '\u0000';
        return source[current + 1];              
    }

    private fun isAlpha(c: Char): Boolean {       
        return (c >= 'a' && c <= 'z') ||      
               (c >= 'A' && c <= 'Z') ||      
                c == '_';                     
    }
    
    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || isDigit(c);      
    }
    
    private fun isDigit(c: Char): Boolean {
        return c >= '0' && c <= '9';   
    }

    private fun isAtEnd(): Boolean {         
        return current >= source.length;
    }

    private fun advance(): Char {                               
        current++;                                           
        return source[current - 1];                   
    }
    
    private fun addToken(type: TokenType) {
        addToken(type, null);
    }                                                      
    
    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current);      
        tokens.add(Token(type, text, literal, line));    
    }
}