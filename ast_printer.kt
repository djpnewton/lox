package klox.lox;

import kotlin.text.StringBuilder;

//TODO: get rid of visitor pattern and just use when statement?

// Creates an unambiguous, if ugly, string representation of AST nodes.
class AstPrinter : ExprVisitor<String> {                     
    fun print(expr: Expr): String {
        return expr.accept(this);
    }
                                                         
    override fun visitBinaryExpr(expr: ExprBinary): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    override fun visitGroupingExpr(expr: ExprGrouping): String {
        return parenthesize("group", expr.expression);
    }                                                                  

    override fun visitLiteralExpr(expr: ExprLiteral): String {
        if (expr.value == null)
            return "nil";
        return expr.value.toString();
    }                                                                  

    override fun visitUnaryExpr(expr: ExprUnary): String {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    override fun visitVariableExpr(expr: ExprVariable): String {
        throw NotImplementedError();                 
    }

    override fun visitAssignExpr(expr: ExprAssign): String {
        throw NotImplementedError();                 
    }

    private fun parenthesize(name: String, vararg exprs: Expr): String {
        val builder = StringBuilder();
    
        builder.append("(").append(name);                      
        for (expr in exprs) {                              
          builder.append(" ");
          builder.append(expr.accept(this));   
        }
        builder.append(")");

        return builder.toString();
      }
}