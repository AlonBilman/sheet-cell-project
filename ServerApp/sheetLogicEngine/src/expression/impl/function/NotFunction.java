
package expression.impl.function;

import expression.api.Expression;
import expression.api.ObjType;
import expression.api.ErrorValues;
import expression.impl.simple.expression.UnaryExpression;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class NotFunction extends UnaryExpression {
    private final String name;

    public NotFunction(Expression arg) {
        super(arg);
        name = "NOT";
    }

    public String getName() {
        return name;
    }

    @Override
    public ObjType type() {
        return ObjType.BOOLEAN; // this function returns double.
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue o1) {
        if (o1 == null)
            throw new NullPointerException("The parameters cannot be null, you may referred to an uninitiated cell");
        if (o1.getObjType() == ObjType.BOOLEAN)
            return new EffectiveValueImpl(!((boolean) o1.getValue()), type());
        return new EffectiveValueImpl(ErrorValues.BOOLEAN_ERROR.getErrorMessage(), ObjType.UNKNOWN);
    }
}