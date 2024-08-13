package function;

public class DivideFunction extends MathExpression {

    private final String name;

    public DivideFunction(Expression<Double> arg1 , Expression<Double> arg2) {
        super(arg1,arg2);
        name = "DIVIDE";
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
        double num = argument[1].eval();
        if(num==0)
            throw new ArithmeticException("Divide by zero!");
            //not a number? - NaN
        else
            return argument[0].eval()/num;

    }
}
