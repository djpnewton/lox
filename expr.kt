package klox.lox;

interface Visitor<R> {
    fun visitBinaryExpr(expr: Binary): R
    fun visitGroupingExpr(expr: Grouping): R
    fun visitLiteralExpr(expr: Literal): R
    fun visitUnaryExpr(expr: Unary): R
}

abstract class Expr {
   abstract fun <R> accept(visitor: Visitor<R>): R;
}

class Binary(left: Expr, operator: Token, right: Expr) : Expr() {
    val left = left;
    val operator = operator;
    val right = right;

    override fun <R> accept(visitor: Visitor<R>): R {
      return visitor.visitBinaryExpr(this);
    }
}

class Grouping(expression: Expr) : Expr() {
    val expression = expression;

    override fun <R> accept(visitor: Visitor<R>): R {
      return visitor.visitGroupingExpr(this);
    }
}

class Literal(value: Any?) : Expr() {
    val value = value;

    override fun <R> accept(visitor: Visitor<R>): R {
      return visitor.visitLiteralExpr(this);
    }
}

class Unary(operator: Token, right: Expr) : Expr() {
    val operator = operator;
    val right = right;

    override fun <R> accept(visitor: Visitor<R>): R {
      return visitor.visitUnaryExpr(this);
    }
}

