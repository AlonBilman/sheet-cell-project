package sheet.impl;

import expression.api.ObjType;
import sheet.api.EffectiveValue;

public class EffectiveValueImpl implements EffectiveValue {
    private Object value;
    private ObjType type;

    public EffectiveValueImpl(Object value, ObjType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public ObjType getObjType() {
        return type;
    }

    @Override
    public Object getValue() {
        return value;
    }
}