package klox.lox;

/*
grammar:

    program     → declaration* EOF ;

    declaration → varDecl
                | statement ;

    varDecl → "var" IDENTIFIER ( "=" expression )? ";" ;

    statement → exprStmt
          | printStmt
          | block ;

    exprStmt  → expression ";" ;
    printStmt → "print" expression ";" ;
    block     → "{" declaration* "}" ;

    expression     → assignment ;
    assignment     → IDENTIFIER "=" assignment
                | equality ;
    equality       → comparison ( ( "!=" | "==" ) comparison )* ;
    comparison     → addition ( ( ">" | ">=" | "<" | "<=" ) addition )* ;
    addition       → multiplication ( ( "-" | "+" ) multiplication )* ;
    multiplication → unary ( ( "/" | "*" ) unary )* ;
    unary          → ( "!" | "-" ) unary
                | primary ;
    primary        → "true" | "false" | "nil"
                | NUMBER | STRING
                | "(" expression ")"
                | IDENTIFIER ;
*/
class Parser(lox: Lox, tokens: List<Token>) {
    private class ParseError: RuntimeException();

    private val lox = lox;
    private val tokens = tokens;
    private var current = 0;

    fun parse(): MutableList<Stmt> {                          
        val statements = mutableListOf<Stmt>(   );
        while (!isAtEnd()) {
            try {
                statements.add(declaration());
            } catch (error: ParseError) {

            }
        }
    
        return statements; 
    }

    private fun expression(): Expr {
        return assignment();
    }

    private fun declaration(): Stmt {                
        try {                                     
            if (match(TokenType.VAR))
                return varDeclaration();

            return statement();
        } catch (error: ParseError) {
            synchronize();
            throw error;
        }
    }

    private fun statement(): Stmt {                  
        if (match(TokenType.PRINT))
            return printStatement();
        if (match(TokenType.LEFT_BRACE))
            return StmtBlock(block());

        return expressionStatement();             
    }

    private fun printStatement(): Stmt {                 
        val value = expression();                    
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return StmtPrint(value);                 
    }

    private fun varDeclaration(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Expect variable name.");
    
        var initializer: Expr? = null;
        if (match(TokenType.EQUAL)) {                                          
            initializer = expression();                                
        }                                                            

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return StmtVar(name, initializer);                      
    }

    private fun expressionStatement(): Stmt {                 
        val expr = expression();                          
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return StmtExpression(expr);                  
    }

    private fun block(): List<Stmt> {                      
        val statements = mutableListOf<Stmt>();
    
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {     
            statements.add(declaration());                
        }                                               
    
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return statements;                              
    }

    private fun assignment(): Expr {
        val expr = equality();
    
        if (match(TokenType.EQUAL)) {
            val equals = previous();
            val value = assignment();
    
            if (expr is ExprVariable) {
                val name = expr.name;
                return ExprAssign(name, value);
            }
    
            error(equals, "Invalid assignment target."); 
        }

        return expr;                                                
      }

    private fun equality(): Expr {
        var expr = comparison();

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = previous();                  
            val right = comparison();                    
            expr = ExprBinary(expr, operator, right);
        }                                               
    
        return expr;                                    
    }

    private fun comparison(): Expr {
        var expr = addition();
    
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val operator = previous();                           
            val right = addition();                               
            expr = ExprBinary(expr, operator, right);         
        }                                                        
    
        return expr;                                             
    }

    private fun addition(): Expr {
        var expr = multiplication();
    
        while (match(TokenType.MINUS, TokenType.PLUS)) {                    
            val operator = previous();
            val right = multiplication();
            expr = ExprBinary(expr, operator, right);
        }                                               
    
        return expr;                                    
      }                                                 
    
      private fun multiplication(): Expr {
        var expr = unary();
    
        while (match(TokenType.SLASH, TokenType.STAR)) {                    
          val operator = previous();
          val right = unary();
          expr = ExprBinary(expr, operator, right);
        }                                               
    
        return expr;                                    
    }

    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
          val operator = previous();
          val right = unary();
          return ExprUnary(operator, right);
        }
    
        return primary();                        
    }

    private fun primary(): Expr {
        if (match(TokenType.FALSE))
            return ExprLiteral(false);      
        if (match(TokenType.TRUE)) 
            return ExprLiteral(true);        
        if (match(TokenType.NIL)) 
            return ExprLiteral(null);
    
        if (match(TokenType.NUMBER, TokenType.STRING)) {                           
            return ExprLiteral(previous().literal);         
        }

        if (match(TokenType.IDENTIFIER)) {                      
            return ExprVariable(previous());
        }
    
        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return ExprGrouping(expr);
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