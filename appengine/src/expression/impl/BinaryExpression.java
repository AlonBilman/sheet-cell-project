package expression.impl;
import expression.api.Expression;
import expression.api.ObjType;
import sheet.api.EffectiveValue;

public abstract class BinaryExpression implements Expression {
    private Expression expression1, expression2;
    public BinaryExpression(Expression expression1, Expression expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }
    @Override
    public EffectiveValue eval() {
        return evaluate(expression1.eval(), expression2.eval());
    }
    @Override
    public abstract ObjType type();
    abstract protected EffectiveValue evaluate(EffectiveValue o1, EffectiveValue o2);
}
