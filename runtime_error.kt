package klox.lox;

class RuntimeError(token: Token, message: String) : RuntimeException(message) {
  final var token = token;
}