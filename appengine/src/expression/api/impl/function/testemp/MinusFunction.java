//package expression.api.impl.function.testemp;
//
//import expression.api.Expression;
//import expression.api.impl.BinaryExpression;
//
//public class MinusFunction extends BinaryExpression {
//
//    private final String name;
//
//    public MinusFunction(Expression arg1 , Expression arg2) {
//        super(arg1,arg2);
//        if(arg1.type()!=1 || arg2.type()!=1)
//            throw new ArithmeticException("This function only works on Doubles! (or Integers..), Please make sure to provide the correct argument type...");
//        name = "MINUS";
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    public int type() {
//        return 1;
//    }
//
//    @Override
//    protected Object evaluate(Object o1, Object o2) {
//        return  (double)o1 - (double)o2;
//    }
//}
