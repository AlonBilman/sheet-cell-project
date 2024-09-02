package expression.impl.simple.expression;

import expression.api.Expression;
import expression.api.ObjType;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class Bool implements Expression {
    private final Boolean value;

    public Bool(Boolean value) {
        this.value = value;
    }

    public ObjType type() {
        return ObjType.BOOLEAN;
    }

    @Override
    public EffectiveValue eval() {
        return new EffectiveValueImpl(value, type());
    }
}




