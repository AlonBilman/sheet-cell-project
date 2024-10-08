package expression.impl.simple.expression;

import expression.api.Expression;
import expression.api.ObjType;
import sheet.api.EffectiveValue;

public abstract class UnaryExpression implements Expression {
    private final Expression expression;

    public UnaryExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public EffectiveValue eval() {
        return evaluate(expression.eval());
    }

    @Override
    public abstract ObjType type();

    abstract protected EffectiveValue evaluate(EffectiveValue object);
}