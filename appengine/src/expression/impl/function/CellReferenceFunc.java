package expression.impl.function;

import expression.api.Expression;
import expression.api.ObjType;
import sheet.api.EffectiveValue;
import sheet.impl.CellImpl;
import sheet.impl.SpreadSheetImpl;

import java.util.Map;

public class CellReferenceFunc implements Expression {
    Expression cellId;
    SpreadSheetImpl currSheet;
    public CellReferenceFunc(Expression cellId, SpreadSheetImpl currSheet) {
        this.cellId = cellId;
        this.currSheet = currSheet;
    }

    @Override
    public EffectiveValue eval() {
       return currSheet.ref(cellId.eval());
    }

    @Override
    public ObjType type() {
        return ObjType.SYSTEM;
    }
}
