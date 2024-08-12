
public class MinusFunction extends MathExpression {

    private final String name;

    public MinusFunction(Expression<Double> arg1 , Expression<Double> arg2) {
        super(arg1,arg2);
        name = "MINUS";
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
        return argument[0].eval()-argument[1].eval();
    }
}
