package klox.lox;

class Environment(enclosing: Environment? = null) {
    final val enclosing: Environment? = enclosing;
    private final val values = hashMapOf<String, Any?>();

    fun define(name: String, value: Any?) {
        values.put(name, value);
    }

    fun get(name: Token): Any? {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null)
            return enclosing.get(name);
    
        throw RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");        
    }

    fun assign(name: Token, value: Any?) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {         
            enclosing.assign(name, value); 
            return;                        
        }
    
        throw RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
}