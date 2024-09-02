package expression.impl.function;

import expression.api.Expression;
import expression.api.ObjType;
import expression.impl.simple.expression.BinaryExpression;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class ConcatFunction extends BinaryExpression {
    private final String name;

    public ConcatFunction(Expression str1, Expression str2) {
        super(str1, str2);
        name = "CONCAT";
    }

    public String getName() {
        return name;
    }

    @Override
    public ObjType type() {
        return ObjType.STRING; // this function returns expString.
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue o1, EffectiveValue o2) {
        if (o1 == null || o2 == null)
            throw new NullPointerException("The parameters cannot be null, you may referred to an uninitiated cell");
        if (o1.getObjType() == ObjType.STRING && o2.getObjType() == ObjType.STRING) {
            return new EffectiveValueImpl((String) o1.getValue() + (String) o2.getValue(), type());
        }
        return new EffectiveValueImpl("!UNDEFINED!", ObjType.UNKNOWN);
    }
}
