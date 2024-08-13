package function;

public class ConcatFunction extends StringExpression {

    private final String name;

    public ConcatFunction(Expression<String> str1, Expression<String> str2) {
        super(str1, str2);
        name = "CONCAT";
    }

    @Override
    protected int numberOfArguments() {
        return 2;
    }

    @Override
    protected String execute(Expression<String>[] arguments) {
        return  arguments[0].eval() + arguments[1].eval();
    }

    @Override
    public String getName() {
        return name;
    }
}
