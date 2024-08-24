
package expression.impl.function;

import expression.api.Expression;
import expression.api.ObjType;
import expression.impl.simple.expression.BinaryExpression;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

import static java.lang.Math.pow;

public class PowFunction extends BinaryExpression {
    private final String name;

    public PowFunction(Expression arg1, Expression arg2) {
        super(arg1, arg2);
        name = "POW";
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
        if (o1 == null || o2 == null)
            throw new NullPointerException("The parameters cannot be null, you may referred to an uninitiated cell");
        if (o1.getObjType() == ObjType.STRING || o1.getObjType() == ObjType.STRING_ERROR
                || o2.getObjType() == ObjType.STRING_ERROR || o2.getObjType() == ObjType.STRING) {
            throw new ArithmeticException("The POW function only works on Doubles! (or Integers..)\n" +
                    "Please make sure to provide the correct argument type...");
        } else if (o1.getObjType() == ObjType.NUMERIC && o2.getObjType() == ObjType.NUMERIC) {
            double res = pow((double) o1.getValue(), (double) o2.getValue());
            return new EffectiveValueImpl(res, type());
        }
        //else - numeric error.
        return new EffectiveValueImpl("NaN", ObjType.NUMERIC_ERROR);
    }
}