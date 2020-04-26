package klox.lox

import java.nio.file.Files;           
import java.nio.file.Paths;
import java.nio.charset.Charset;

class Lox() {
    val interpreter = Interpreter(this);
    var hadError = false;
    var hadRuntimeError = false;

    fun runFile(path: String) {
        val bytes = Files.readAllBytes(Paths.get(path));
        run(String(bytes, Charset.defaultCharset()));
        
        // Indicate an error in the exit code.           
        if (hadError)
            System.exit(65);
        if (hadRuntimeError)
            System.exit(70);
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
        val parser = Parser(this, tokens);                    
        val statements = parser.parse();

        // Stop if there was a syntax error.                   
        if (hadError)
            return;                                  

        interpreter.interpret(statements);
    }
    
    fun error(line: Int, message: String) {                       
        report(line, "", message);                                        
    }
    
    fun report(line: Int, where: String, message: String) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);        
        hadError = true;                                                  
    }

    fun error(token: Token, message: String) {              
        if (token.type == TokenType.EOF) {                          
          report(token.line, " at end", message);                   
        } else {                                                    
          report(token.line, " at '" + token.lexeme + "'", message);
        }                                                           
    }

    fun runtimeError(error: RuntimeError) {
        System.err.println(error.message +     
            "\n[line " + error.token.line + "]");   
        hadRuntimeError = true;                     
      }
}

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