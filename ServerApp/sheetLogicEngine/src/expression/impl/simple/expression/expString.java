package expression.impl.simple.expression;

import expression.api.Expression;
import expression.api.ObjType;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class expString implements Expression {
    private final String value;

    public expString(String value) {
        this.value = value;
    }

    public ObjType type() {
        return ObjType.STRING;
    }

    @Override
    public EffectiveValue eval() {
        return new EffectiveValueImpl(value, type());
    }
}

