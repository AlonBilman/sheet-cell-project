package expression.impl.function;

import expression.api.ErrorValues;
import expression.api.Expression;
import expression.api.ObjType;
import expression.impl.simple.expression.BinaryExpression;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class BiggerFunction extends BinaryExpression {
    private final String name;

    public BiggerFunction(Expression arg1, Expression arg2) {
        super(arg1, arg2);
        name = "BIGGER";
    }

    public String getName() {
        return name;
    }

    @Override
    public ObjType type() {
        return ObjType.BOOLEAN; // this function returns boolean.
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue o1, EffectiveValue o2) {
        if (o1 == null || o2 == null)
            throw new NullPointerException("The parameters cannot be null, you may referred to an uninitiated cell");
        if (o1.getObjType() == ObjType.NUMERIC && o2.getObjType() == ObjType.NUMERIC) {
            Boolean res = ((double) o1.getValue() >= (double) o2.getValue());
            return new EffectiveValueImpl(res, type());
        }
        return new EffectiveValueImpl(ErrorValues.BOOLEAN_ERROR.getErrorMessage(), ObjType.UNKNOWN);
    }
}