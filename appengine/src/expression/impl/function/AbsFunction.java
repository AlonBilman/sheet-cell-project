
package expression.impl.function;

import expression.api.Expression;
import expression.api.ObjType;
import expression.impl.UnaryExpression;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;
import static java.lang.Math.abs;


public class AbsFunction extends UnaryExpression {
    private final String name;

    public AbsFunction(Expression arg) {
        super(arg);
        name = "ABS";
    }

    public String getName() {
        return name;
    }

    @Override
    public ObjType type() {
        return ObjType.NUMERIC; // this function returns double.
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue o1) {
        if (o1 == null)
            throw new NullPointerException("The parameters cannot be null, you may referred to an uninitiated cell");
        if (o1.getObjType() != ObjType.NUMERIC)
            throw new ArithmeticException("This ABS function only works on a Double (or Integer..)\n" +
                    "Please make sure to provide the correct argument type...");

        double res = abs((double) o1.getValue());
        return new EffectiveValueImpl(res, type());
    }
}