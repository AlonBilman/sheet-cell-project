package expression.impl.function;

import expression.api.Expression;
import expression.api.ObjType;
import sheet.api.EffectiveValue;
import sheet.impl.SpreadSheetImpl;

public class CellReferenceFunc implements Expression {
    Expression cellId;
    SpreadSheetImpl currSheet;
    String calledById;

    public CellReferenceFunc(Expression cellId, SpreadSheetImpl currSheet, String calledById) {
        this.cellId = cellId;
        this.currSheet = currSheet;
        this.calledById = calledById;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue evaluateCell = cellId.eval();
        if (evaluateCell.getObjType() != ObjType.STRING)
            throw new IllegalArgumentException("The REF function is only applicable to Strings! (Cell ID). \n- provided Double");
        return currSheet.ref(cellId.eval(), calledById);
    }

    @Override
    public ObjType type() {
        return ObjType.STRING;
    }
}