package klox.lox;

interface StmtVisitor<R> {
    fun visitBlockStmt(stmt: StmtBlock): R
    fun visitExpressionStmt(stmt: StmtExpression): R
    fun visitPrintStmt(stmt: StmtPrint): R
    fun visitVarStmt(stmt: StmtVar): R
}

abstract class Stmt {
   abstract fun <R> accept(visitor: StmtVisitor<R>): R;
}

class StmtBlock(statements: List<Stmt>) : Stmt() {
    val statements = statements;

    override fun <R> accept(visitor: StmtVisitor<R>): R {
      return visitor.visitBlockStmt(this);
    }
}

class StmtExpression(expression: Expr) : Stmt() {
    val expression = expression;

    override fun <R> accept(visitor: StmtVisitor<R>): R {
      return visitor.visitExpressionStmt(this);
    }
}

class StmtPrint(expression: Expr) : Stmt() {
    val expression = expression;

    override fun <R> accept(visitor: StmtVisitor<R>): R {
      return visitor.visitPrintStmt(this);
    }
}

class StmtVar(name: Token, initializer: Expr?) : Stmt() {
    val name = name;
    val initializer = initializer;

    override fun <R> accept(visitor: StmtVisitor<R>): R {
      return visitor.visitVarStmt(this);
    }
}

