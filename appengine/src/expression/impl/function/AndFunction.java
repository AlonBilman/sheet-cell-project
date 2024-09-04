package expression.impl.function;

import expression.api.ErrorValues;
import expression.api.Expression;
import expression.api.ObjType;
import expression.impl.simple.expression.BinaryExpression;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class AndFunction extends BinaryExpression {
    private final String name;

    public AndFunction(Expression arg1, Expression arg2) {
        super(arg1, arg2);
        name = "AND";
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
        if (o1.getObjType() == ObjType.BOOLEAN && o2.getObjType() == ObjType.BOOLEAN) {
            Boolean res = ((Boolean) o1.getValue() && (Boolean) o2.getValue());
            return new EffectiveValueImpl(res, type());
        }
        return new EffectiveValueImpl(ErrorValues.BOOLEAN_ERROR.getErrorMessage(), ObjType.UNKNOWN);
    }
}