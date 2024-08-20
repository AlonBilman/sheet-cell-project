package expression.impl;
import expression.api.Expression;
import expression.api.ObjType;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class Number implements Expression {
    private final Double value;
    public Number(Double value) {
        this.value = value;
    }
    public Number(Integer value) {
        this.value = Double.valueOf(value);
    }
    public ObjType type(){
        return ObjType.NUMERIC;
    }
    @Override
    public EffectiveValue eval() {
        return new EffectiveValueImpl(value,type());
    }
}

