package expression.impl;

import expression.api.Expression;
import expression.api.ObjType;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class SimpleExpression implements Expression {
    private final Object value;
    private final ObjType type;

    // Constructor to handle numeric and string types
    public SimpleExpression(Object value) {
        ObjType type1;
        this.value = value;
        ObjType temp = null;
        if (value instanceof String) {
            try {
                // Attempt to parse the string as a number
                Double.parseDouble((String)value);
                type1 = ObjType.NUMERIC;
            } catch (NumberFormatException e) {
                // If parsing fails, treat it as a string
                temp= ObjType.STRING;
            }
        } else if (value instanceof Double) {
            temp = ObjType.NUMERIC;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass());
        }
        type1 =temp;
        this.type = type1;
    }


    @Override
    public ObjType type() {
        return type;
    }

    @Override
    public EffectiveValue eval() {
        return new EffectiveValueImpl(value, type);
    }
}
