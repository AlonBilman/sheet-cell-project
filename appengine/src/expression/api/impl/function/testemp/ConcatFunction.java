//package expression.api.impl.function.testemp;
//
//import expression.api.Expression;
//import expression.api.impl.BinaryExpression;
//
//public class ConcatFunction extends BinaryExpression {
//
//    private final String name;
//
//    public ConcatFunction(Expression str1, Expression str2) {
//        super(str1, str2);
//        if(str1.type()!=1 || str2.type()!=2)
//            throw new ArithmeticException("This function only works on Strings! (or Expressions that returns String..), Please make sure to provide the correct argument type...");
//        name = "CONCAT";
//    }
//
//    @Override
//    public int type() {
//        return 2;
//    }
//
//    @Override
//    protected Object evaluate(Object o1, Object o2) {
//        return (String)o1 + (String)o2;
//    }
//
//    public String getName() {
//        return name;
//    }
//}
