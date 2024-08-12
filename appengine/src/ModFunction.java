
public class ModFunction extends MathExpression {

    private final String name;

    public ModFunction(Expression<Double> arg1 , Expression<Double> arg2) {
        super(arg1,arg2);
        name = "MOD";
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
            return argument[0].eval()%argument[1].eval();

    }
}
