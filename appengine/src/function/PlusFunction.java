package function;

public class PlusFunction extends MathExpression {

private final String name;

public PlusFunction(Expression<Double> arg1 , Expression<Double> arg2) {
    super(arg1,arg2);
    name = "PLUS";
}

    @Override
    protected int numberOfArguments() {
        return 2;
    }

    public String getName() {
        return name;
    }

    @Override
    protected double execute(Expression<Double>[] argument) {
        return argument[0].eval()+argument[1].eval();
    }
}
