//package function;
//
//public class AbsFunction extends MathExpression {
//
//    private final String name;
//
//    public AbsFunction(Expression<Double> arg) {
//        super(arg);
//        name = "ABS";
//    }
//
//    @Override
//    protected int numberOfArguments() {
//        return 1;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    protected double execute(Expression<Double>[] argument) {
//        return Math.abs(argument[0].eval());
//    }
//}
