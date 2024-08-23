package expression.impl.simple.expression;

import expression.api.Expression;
import expression.api.ObjType;
import sheet.api.EffectiveValue;

public abstract class TrinaryExpression implements Expression {
    private final Expression expression1, expression2, expression3;

    public TrinaryExpression(Expression expression1, Expression expression2, Expression expression3) {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.expression3 = expression3;
    }

    @Override
    public EffectiveValue eval() {
        return evaluate(expression1.eval(), expression2.eval(), expression3.eval());
    }

    public abstract String getName();

    @Override
    public abstract ObjType type();

    abstract protected EffectiveValue evaluate(EffectiveValue o1, EffectiveValue o2, EffectiveValue o3);
}
