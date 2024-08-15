package expression.impl.function;
import expression.api.Expression;
import expression.api.ObjType;
import expression.impl.BinaryExpression;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class PlusFunction extends BinaryExpression {

private final String name;

public PlusFunction(Expression arg1 , Expression arg2) {
    super(arg1,arg2);
    name = "PLUS";
}
    public String getName() {
        return name;
    }

    @Override
    public ObjType type() {
        return ObjType.NUMERIC; // this function returns double.
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue o1, EffectiveValue o2) {

        if(o1.getObjType()!=ObjType.NUMERIC || o2.getObjType()!=ObjType.NUMERIC)
                throw new ArithmeticException("This PLUS function only works on Doubles! (or Integers..), Please make sure to provide the correct argument type...");

        double res = (double)o1.getValue() + (double)o2.getValue();
        return new EffectiveValueImpl(res,type());
    }

}
