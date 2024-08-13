package function;

public abstract class MathExpression implements Expression<Double> {

    protected Expression<Double>[] arguments;
    protected abstract int numberOfArguments();
    protected abstract double execute(Expression<Double>[] argument);

    protected MathExpression(Expression<Double>... arguments) {
        this.arguments = arguments;
        checkArguments();
    }

    private void checkArguments() {
        if(arguments.length != numberOfArguments()) {
            throw new IllegalArgumentException("function.Number of arguments does not match the required number of arguments : "+numberOfArguments());
        }
    }

    public abstract String getName();
    @Override
    public Double eval(){
        return execute(arguments);
    }
}


