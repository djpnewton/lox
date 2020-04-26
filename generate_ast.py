import sys

def kotlin_field_from_java(field):
    type_, name = field.split(' ')
    if type_ == 'Object':
        type_ = 'Any?'
    return '%s: %s' % (name, type_)

def field_name(field):
    name, _ = field.split(': ')
    return name

def define_type(f, base_name, class_name, field_list):
    fields = field_list.split(', ')
    field_list_kotlin = ', '.join(kotlin_field_from_java(field) for field in fields)
    fields_kotlin = field_list_kotlin.split(', ')
    f.write('class ' + base_name + class_name + '(' + field_list_kotlin + ') : ' + base_name + '() {\n')

    # Fields.                                           
    for field in fields_kotlin:
        name = field_name(field)
        f.write('    val ' + name + ' = ' + name + ';\n')

    # Visitor pattern.                                      
    f.write('\n')
    f.write('    override fun <R> accept(visitor: ' + base_name + 'Visitor<R>): R {\n')
    f.write('      return visitor.visit' + class_name + base_name + '(this);\n')
    f.write('    }\n')

    f.write('}\n')

def define_visitor(f, base_name, types):
    f.write('interface ' + base_name + 'Visitor<R> {\n') # out R?

    for t in types:
        type_name = t.split(':')[0].strip()
        f.write('    fun visit' + type_name + base_name + '(' + base_name.lower() + ': ' + base_name + type_name + '): R\n')

    f.write('}\n')

def define_ast(out_dir, base_name, types):                  
    path = out_dir + '/' + base_name.lower() + '.kt'
    with open(path, 'w') as f:
        f.write('package klox.lox;\n')
        f.write('\n')

        define_visitor(f, base_name, types)
        f.write('\n')

        f.write('abstract class ' + base_name + ' {\n')

        # The base accept() method.
        f.write('   abstract fun <R> accept(visitor: ' + base_name + 'Visitor<R>): R;\n')

        f.write('}\n')
        f.write('\n')

        # The AST classes.                                     
        for t in types:
            class_name = t.split(':')[0].strip()
            fields = t.split(':')[1].strip()
            define_type(f, base_name, class_name, fields)
            f.write('\n')

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print('Usage: generate_ast <output directory>')
        sys.exit(1)

    out_dir = sys.argv[1]
    define_ast(out_dir, 'Expr', [
        'Assign   : Token name, Expr value',
        'Binary   : Expr left, Token operator, Expr right',
        'Grouping : Expr expression',                      
        'Literal  : Object value',                         
        'Unary    : Token operator, Expr right',
        'Variable : Token name'
    ])
    define_ast(out_dir, 'Stmt', [
        'Block      : List<Stmt> statements',
        'Expression : Expr expression',
        'Print      : Expr expression',
        'Var        : Token name, Expr? initializer'
    ])