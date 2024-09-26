package expression.impl.function;

import expression.api.ErrorValues;
import expression.api.Expression;
import expression.api.ObjType;
import expression.impl.simple.expression.TrinaryExpression;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class ifCondition extends TrinaryExpression {
    private final String name;

    public ifCondition(Expression condition, Expression _then, Expression _else) {
        super(condition, _then, _else);
        name = "IF";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ObjType type() {
        return ObjType.UNKNOWN;
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue condition, EffectiveValue _then, EffectiveValue _else) {
        if (condition == null || _then == null || _else == null) {
            throw new NullPointerException("The parameters cannot be null; you may have referred to an uninitiated cell.");
        }
        if (_then.getObjType() == _else.getObjType()) {
            if (condition.getObjType() == ObjType.BOOLEAN) {
                boolean conditionValue = (Boolean) condition.getValue();
                EffectiveValue result = conditionValue ? _then : _else;
                return new EffectiveValueImpl(result.getValue(), result.getObjType());
            }
        }
        return new EffectiveValueImpl(ErrorValues.STRING_ERROR.getErrorMessage(), ObjType.UNKNOWN);
    }
}
