
package expression.impl.function;

import expression.api.Expression;
import expression.api.ObjType;
import expression.api.ErrorValues;
import expression.impl.simple.expression.UnaryExpression;
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
        if (o1.getObjType() == ObjType.STRING)
            return new EffectiveValueImpl(abs((double) o1.getValue()), type());
        return new EffectiveValueImpl(ErrorValues.NUMERIC_ERROR.getErrorMessage(), ObjType.UNKNOWN);
    }
}