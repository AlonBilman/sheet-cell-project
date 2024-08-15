package expression.impl.function;
import expression.api.Expression;
import expression.api.ObjType;
import expression.impl.BinaryExpression;
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
        if(o1.getObjType()!=ObjType.STRING || o2.getObjType()!=ObjType.STRING)
            throw new ArithmeticException("This function only works on Strings! (or Expressions that returns String..), Please make sure to provide the correct argument type...");
        String res = (String)o1.getValue() + (String)o2.getValue();
        return new EffectiveValueImpl(res,type());
    }

    public String getName() {
        return name;
    }
}
