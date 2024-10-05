package expression.impl.function;

import expression.api.ErrorValues;
import expression.api.Expression;
import expression.api.ObjType;
import expression.impl.simple.expression.BinaryExpression;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class PercentFunction extends BinaryExpression {
    private final String name;

    public PercentFunction(Expression arg1, Expression arg2) {
        super(arg1, arg2);
        name = "PERCENT";
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
        if (o1.getObjType() == ObjType.NUMERIC && o2.getObjType() == ObjType.NUMERIC) {
            double part = (double) o1.getValue();
            double whole = (double) o2.getValue();
            if (part >= 0 && whole > 0) {
                double res = part * (whole / 100);
                return new EffectiveValueImpl(res, type());
                //need to ask Aviad about that
            }
        }
        return new EffectiveValueImpl(ErrorValues.NUMERIC_ERROR.getErrorMessage(), ObjType.UNKNOWN);
    }

}