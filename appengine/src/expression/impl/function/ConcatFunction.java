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

    @Override
    public ObjType type() {
        return ObjType.STRING; // this function returns string.
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue o1, EffectiveValue o2) {
        if (o1.getObjType() == ObjType.NUMERIC || o2.getObjType() == ObjType.NUMERIC_ERROR ||
                o1.getObjType() == ObjType.NUMERIC_ERROR || o2.getObjType() == ObjType.NUMERIC) {
            throw new ArithmeticException("The CONCAT function only works on Strings! (or Expressions that returns String..)\nPlease make sure to provide the correct argument type...");
        } else if (o1.getObjType() == ObjType.STRING && o2.getObjType() == ObjType.STRING) {
            String res = (String) o1.getValue() + (String) o2.getValue();
            return new EffectiveValueImpl(res, type());
        }
        return new EffectiveValueImpl("!UNDEFINED!", ObjType.STRING_ERROR);
    }

    public String getName() {
        return name;
    }
}
