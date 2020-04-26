package klox.lox;

interface ExprVisitor<R> {
    fun visitAssignExpr(expr: ExprAssign): R
    fun visitBinaryExpr(expr: ExprBinary): R
    fun visitGroupingExpr(expr: ExprGrouping): R
    fun visitLiteralExpr(expr: ExprLiteral): R
    fun visitUnaryExpr(expr: ExprUnary): R
    fun visitVariableExpr(expr: ExprVariable): R
}

abstract class Expr {
   abstract fun <R> accept(visitor: ExprVisitor<R>): R;
}

class ExprAssign(name: Token, value: Expr) : Expr() {
    val name = name;
    val value = value;

    override fun <R> accept(visitor: ExprVisitor<R>): R {
      return visitor.visitAssignExpr(this);
    }
}

class ExprBinary(left: Expr, operator: Token, right: Expr) : Expr() {
    val left = left;
    val operator = operator;
    val right = right;

    override fun <R> accept(visitor: ExprVisitor<R>): R {
      return visitor.visitBinaryExpr(this);
    }
}

class ExprGrouping(expression: Expr) : Expr() {
    val expression = expression;

    override fun <R> accept(visitor: ExprVisitor<R>): R {
      return visitor.visitGroupingExpr(this);
    }
}

class ExprLiteral(value: Any?) : Expr() {
    val value = value;

    override fun <R> accept(visitor: ExprVisitor<R>): R {
      return visitor.visitLiteralExpr(this);
    }
}

class ExprUnary(operator: Token, right: Expr) : Expr() {
    val operator = operator;
    val right = right;

    override fun <R> accept(visitor: ExprVisitor<R>): R {
      return visitor.visitUnaryExpr(this);
    }
}

class ExprVariable(name: Token) : Expr() {
    val name = name;

    override fun <R> accept(visitor: ExprVisitor<R>): R {
      return visitor.visitVariableExpr(this);
    }
}

