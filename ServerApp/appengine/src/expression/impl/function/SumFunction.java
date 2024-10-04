package expression.impl.function;


import expression.api.ErrorValues;
import expression.api.Expression;
import expression.api.ObjType;
import sheet.api.EffectiveValue;
import sheet.impl.CellImpl;
import sheet.impl.EffectiveValueImpl;
import sheet.impl.Range;

import java.util.Set;

public class SumFunction implements Expression {
    private final Range range;
    private final String name;

    public SumFunction(Range range) {
        this.range = range;
        this.name = "SUM";
    }

    public String getName() {
        return name;
    }

    @Override
    public ObjType type() {
        return ObjType.NUMERIC;
    }

    @Override
    public EffectiveValue eval() {
        if(range==null)
            return new EffectiveValueImpl(ErrorValues.NUMERIC_ERROR.getErrorMessage(), ObjType.UNKNOWN);
        double sum = 0;
        Set<CellImpl> cellsInRange = range.getRangeCells();
        for (CellImpl cell : cellsInRange) {
            EffectiveValue val = cell.getEffectiveValue();
            if (val.getObjType() == ObjType.UNKNOWN && val.getValue() == ErrorValues.NUMERIC_ERROR.getErrorMessage())
                return new EffectiveValueImpl(ErrorValues.NUMERIC_ERROR.getErrorMessage(), ObjType.UNKNOWN);
            if (val.getObjType() == ObjType.NUMERIC) {
                sum += (double) val.getValue();
            }
        }
        return new EffectiveValueImpl(sum, type());
    }
}