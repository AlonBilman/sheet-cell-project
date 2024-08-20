package expression.impl;
import expression.api.Expression;
import expression.api.ObjType;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class Mystring implements Expression {
    private final String value;
    public Mystring(String value) {
        this.value = value;
    }
    public ObjType type() {
        return ObjType.STRING;
    }
    @Override
    public EffectiveValue eval() {
        return new EffectiveValueImpl(value,type());
    }
}

