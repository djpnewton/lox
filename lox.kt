package klox.lox

import java.nio.file.Files;           
import java.nio.file.Paths;
import java.nio.charset.Charset;

class Lox() {
    var hadError: Boolean = false;

    fun runFile(path: String) {
        val bytes = Files.readAllBytes(Paths.get(path));
        run(String(bytes, Charset.defaultCharset()));          
    }
    
    fun runPrompt() {         
        while (true) {
          print("> ");
        run(readLine() ?: "");
          hadError = false;
        }                                                          
    }
    
    fun run(source: String) {
        val scanner = Scanner(this, source);
        val tokens = scanner.scanTokens();
    
        // For now, just print the tokens.
        for (token in tokens) {
          println(token);
        }

        // Indicate an error in the exit code.           
        if (hadError) System.exit(65);
    }
    
    fun error(line: Int, message: String) {                       
        report(line, "", message);                                        
    }
    
    fun report(line: Int, where: String, message: String) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);        
        hadError = true;                                                  
    }
}
/*
fun main(args: Array<String>) {
    if (args.size > 1) {                                   
        println("Usage: klox [script]");          
        System.exit(64); 
    } else if (args.size == 1) {                           
        Lox().runFile(args[0]);                                      
    } else {                                                 
        Lox().runPrompt();                                           
    }                                                        
}
*/

// test ast printer
fun main(args: Array<String>) {
    val expression = Binary(                     
        Unary(                                    
            Token(TokenType.MINUS, "-", null, 1),      
            Literal(123)),                        
        Token(TokenType.STAR, "*", null, 1),           
        Grouping(                                 
            Literal(45.67)));

    println(AstPrinter().print(expression));
}