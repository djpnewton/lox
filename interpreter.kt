package klox.lox;

class Interpreter(lox: Lox) : Visitor<Any?> {
    val lox = lox;
                                          
    override fun visitLiteralExpr(expr: Literal): Any? {
        return expr.value;                               
    }

    override fun visitGroupingExpr(expr: Grouping): Any? {
        return evaluate(expr.expression);                  
    }

    override fun visitUnaryExpr(expr: Unary): Any? {
        var right = evaluate(expr.right);
    
        when (expr.operator.type) {
            TokenType.BANG -> return !isTruthy(right);
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right);
                return -(right as Double);
            }
            // Unreachable.
            else -> return null;
        }
    }

    override fun visitBinaryExpr(expr: Binary): Any? {
        val left = evaluate(expr.left);             
        val right = evaluate(expr.right); 
    
        when (expr.operator.type) {
            TokenType.GREATER -> {
                checkNumberOperands(expr.operator, left, right);
                return left as Double > right as Double; 
            }
            TokenType.GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right);
                return left as Double >= right as Double;
            }
            TokenType.LESS -> {
                checkNumberOperands(expr.operator, left, right);
                return (left as Double) < right as Double;
            }
            TokenType.LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right);
                return left as Double <= right as Double;
            }
            TokenType.MINUS -> {
                checkNumberOperands(expr.operator, left, right);
                return left as Double - right as Double;
            }
            TokenType.PLUS -> {                                                
                if (left is Double && right is Double) {
                    return left as Double + right as Double;                  
                }
                if (left is String && right is String) {
                    return left as String + right as String;                  
                }
                throw RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            }
            TokenType.SLASH -> {
                checkNumberOperands(expr.operator, left, right);
                return left as Double / right as Double;
            }
            TokenType.STAR  -> {
                checkNumberOperands(expr.operator, left, right);
                return left as Double * right as Double;
            }
            TokenType.BANG_EQUAL -> return !isEqual(left, right);
            TokenType.EQUAL_EQUAL -> return isEqual(left, right);
            // Unreachable.
            else -> return null;
        }
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double)
            return;                         
        throw RuntimeError(operator, "Operand must be a number.");
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {   
        if (left is Double && right is Double)
        return;
        throw RuntimeError(operator, "Operands must be numbers.");
    }

    private fun isTruthy(obj: Any?): Boolean {
        if (obj == null)
            return false;
        return obj as? Boolean ?: true;                    
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        // nil is only equal to nil.               
        if (a == null && b == null)
            return true;   
        if (a == null)
            return false;
    
        return a.equals(b);                        
    }

    private fun stringify(obj: Any?): String {                         
        if (obj == null)
            return "nil";
    
        // Hack. Work around Java adding ".0" to integer-valued doubles.
        if (obj is Double) {                                 
            var text = obj.toString();                              
            if (text.endsWith(".0")) {                                    
                text = text.substring(0, text.length - 2);                
            }                                                             
            return text;                                                  
        }                                                               
    
        return obj.toString();                                       
      }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this);         
    }

    fun interpret(expression: Expr) {        
        try {                                  
            val value = evaluate(expression); 
            println(stringify(value));
        } catch (error: RuntimeError) {         
            lox.runtimeError(error);             
        }                                      
      }
}   