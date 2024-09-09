package expression.impl.function;


import expression.api.ErrorValues;
import expression.api.Expression;
import expression.api.ObjType;
import sheet.api.EffectiveValue;
import sheet.impl.CellImpl;
import sheet.impl.EffectiveValueImpl;
import sheet.impl.Range;

import java.util.Set;

public class AverageFunction implements Expression {
    private final Range range;
    private final String name;

    public AverageFunction(Range range) {
        this.range = range;
        this.name = "AVERAGE";
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
        double count = 0;
        double avg;
        Set<CellImpl> cellsInRange = range.getRangeCells();
        for (CellImpl cell : cellsInRange) {
            EffectiveValue val = cell.getEffectiveValue();
            if (val.getObjType() == ObjType.UNKNOWN && val.getValue() == ErrorValues.NUMERIC_ERROR.getErrorMessage())
                return new EffectiveValueImpl(ErrorValues.NUMERIC_ERROR.getErrorMessage(), ObjType.UNKNOWN);
            if (val.getObjType() == ObjType.NUMERIC) {
                ++count;
                sum += (double) val.getValue();
            }
        }
        if (count == 0)
            throw new RuntimeException("Average function requires at least one numeric cell.\n" +
                    "The range that was given did not include any.");

        else avg = sum / count;
        return new EffectiveValueImpl(avg, type());
    }
}