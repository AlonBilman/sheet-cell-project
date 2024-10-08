package expression.impl.function;

import expression.api.ErrorValues;
import expression.api.Expression;
import expression.api.ObjType;
import expression.impl.simple.expression.TrinaryExpression;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class SubFunction extends TrinaryExpression {
    private final String name;

    public SubFunction(Expression arg1, Expression arg2, Expression arg3) {
        super(arg1, arg2, arg3);
        name = "SUB";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ObjType type() {
        return ObjType.STRING; // this function returns a expString.
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue sourceValue, EffectiveValue startValue, EffectiveValue endValue) {
        if (sourceValue == null || startValue == null || endValue == null)
            throw new NullPointerException("The parameters cannot be null; you may have referred to an uninitiated cell");
        if (sourceValue.getObjType() == ObjType.STRING && startValue.getObjType() == ObjType.NUMERIC && endValue.getObjType() == ObjType.NUMERIC) {
            String source = (String) sourceValue.getValue();
            int start = ((Double) startValue.getValue()).intValue();
            int end = ((Double) endValue.getValue()).intValue();
            if (!(start < 0 || end >= source.length() || start > end))
                return new EffectiveValueImpl(source.substring(start, end + 1), type());
        }
        //if one of them is not numeric or the if inside was false (There's a not there!)
        return new EffectiveValueImpl(ErrorValues.STRING_ERROR.getErrorMessage(), ObjType.UNKNOWN);
    }
}
