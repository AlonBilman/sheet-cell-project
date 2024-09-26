package expression.impl.function;

import expression.api.ErrorValues;
import expression.api.Expression;
import expression.api.ObjType;
import expression.impl.simple.expression.BinaryExpression;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

import java.util.Arrays;


public class EqualFunction extends BinaryExpression {

    private final String name;

    public EqualFunction(Expression arg1, Expression arg2) {
        super(arg1, arg2);
        name = "EQUAL";
    }

    public String getName() {
        return name;
    }

    @Override
    public ObjType type() {
        return ObjType.BOOLEAN;
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue o1, EffectiveValue o2) {
        if (o1 == null || o2 == null)
            throw new NullPointerException("The parameters cannot be null, you may referred to an uninitiated cell");
        if (o1.getObjType() == ObjType.UNKNOWN || o2.getObjType() == ObjType.UNKNOWN)
            return new EffectiveValueImpl(ErrorValues.BOOLEAN_ERROR.getErrorMessage(), ObjType.UNKNOWN);
        if (o1.getObjType() == o2.getObjType()) {
            Boolean res = o1.getValue().equals(o2.getValue());
            return new EffectiveValueImpl(res, type());
        }
        return new EffectiveValueImpl(false, ObjType.BOOLEAN);
    }
}

