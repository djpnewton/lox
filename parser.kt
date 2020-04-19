package klox.lox;

/*
grammar:

    expression     → equality ;
    equality       → comparison ( ( "!=" | "==" ) comparison )* ;
    comparison     → addition ( ( ">" | ">=" | "<" | "<=" ) addition )* ;
    addition       → multiplication ( ( "-" | "+" ) multiplication )* ;
    multiplication → unary ( ( "/" | "*" ) unary )* ;
    unary          → ( "!" | "-" ) unary
                | primary ;
    primary        → NUMBER | STRING | "false" | "true" | "nil"
                | "(" expression ")" ;
*/
class Parser(lox: Lox, tokens: List<Token>) {
    private class ParseError: RuntimeException();

    private val lox = lox;
    private val tokens = tokens;
    private var current = 0;

    fun parse(): Expr? {
        try {
            return expression();
        } catch (error: ParseError) {
            return null;
        }
    }

    private fun expression(): Expr {
        return equality();
    }

    private fun equality(): Expr {
        var expr = comparison();

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = previous();                  
            val right = comparison();                    
            expr = Binary(expr, operator, right);
        }                                               
    
        return expr;                                    
    }

    private fun comparison(): Expr {
        var expr = addition();
    
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val operator = previous();                           
            val right = addition();                               
            expr = Binary(expr, operator, right);         
        }                                                        
    
        return expr;                                             
    }

    private fun addition(): Expr {
        var expr = multiplication();
    
        while (match(TokenType.MINUS, TokenType.PLUS)) {                    
            val operator = previous();
            val right = multiplication();
            expr = Binary(expr, operator, right);
        }                                               
    
        return expr;                                    
      }                                                 
    
      private fun multiplication(): Expr {
        var expr = unary();
    
        while (match(TokenType.SLASH, TokenType.STAR)) {                    
          val operator = previous();
          val right = unary();
          expr = Binary(expr, operator, right);
        }                                               
    
        return expr;                                    
    }

    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
          val operator = previous();
          val right = unary();
          return Unary(operator, right);
        }
    
        return primary();                        
    }

    private fun primary(): Expr {
        if (match(TokenType.FALSE))
            return Literal(false);      
        if (match(TokenType.TRUE)) 
            return Literal(true);        
        if (match(TokenType.NIL)) 
            return Literal(null);
    
        if (match(TokenType.NUMBER, TokenType.STRING)) {                           
          return Literal(previous().literal);         
        }                                                      
    
        if (match(TokenType.LEFT_PAREN)) {                               
          val expr = expression();                            
          consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
          return Grouping(expr);                      
        }

        throw error(peek(), "Expect expression.");
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
          if (check(type)) {                     
            advance();                           
            return true;                         
          }                                      
        }
    
        return false;                            
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type))
            return advance();
    
        throw error(peek(), message);                        
    }

    private fun check(type: TokenType): Boolean {
        if (isAtEnd())
            return false;
        return peek().type == type;
    }

    private fun advance(): Token {
        if (!isAtEnd())
            current++;
        return previous();        
    }

    private fun isAtEnd(): Boolean {
        return peek().type == TokenType.EOF;
    }
    
    private fun peek(): Token {
        return tokens.get(current);
    }                                
    
    private fun previous(): Token {
        return tokens.get(current - 1);
    }

    private fun error(token: Token, message: String): ParseError {
        lox.error(token, message);                           
        return ParseError();                             
    }

    private fun synchronize() {
        advance();
    
        while (!isAtEnd()) {                       
            if (previous().type == TokenType.SEMICOLON)
                return;
    
            when (peek().type) {
                TokenType.CLASS,
                TokenType.FUN,
                TokenType.VAR,
                TokenType.FOR,
                TokenType.IF,
                TokenType.WHILE,
                TokenType.PRINT,
                TokenType.RETURN -> return
                else -> {}                              
            }

            advance();
        }                                          
    }
}