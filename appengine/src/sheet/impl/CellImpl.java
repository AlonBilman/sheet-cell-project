package sheet.impl;
import expression.api.Expression;
import sheet.api.EffectiveValue;

import java.util.ArrayList;
import java.util.List;

public class CellImpl {
    private final int row;
    private final int col;
    private final String id;
    private int lastChangeAt;
    private List<CellImpl> dependsOn;
    private List<CellImpl> affectsOn;
    private String originalValue;
    private EffectiveValue effectiveValue;


    public CellImpl(int row, int col) {
        lastChangeAt = 0;

        this.row = row;
        this.col = col;
        this.id = generateId(col,row);
        dependsOn = new ArrayList<>();
        affectsOn = new ArrayList<>();
    }
    //maybe I get a string? and then edit the cell? {Bla Bla}?

    public void editCell(Expression value, int version, CellImpl... depends) {
        //originalValue = value;
      //  effectiveValue = value.eval().toString();
        updateLastChangeAt(version);
        updateCellsThatIAffect(); //maham
        updateCellsThatIDependsOn(depends);
    }

    private void updateCellsThatIDependsOn(CellImpl... depends) {
        //.... update the list. get rid of older that I don't depend on anymore
    }
    public void updateCellsThatIAffect() {
        //
    }

    public void calculateEffectiveValue() {
        // build the expression object out of the original value...
        // it can be {PLUS, 4, 5} OR {CONCAT, "hello", "world"}

        // first question: what is the generic type of Expression ?

        // second question: what is the return type of eval() ?

    }


    private String generateId(int col, int row) {
        char letter = (char)('A'+col);
        return letter+String.valueOf(row+1);
    }

    Boolean IdChecker(String id){
        return id.equals(this.id);
    }

    public void removeDependsOn(CellImpl cellImpl) {
        dependsOn.remove(cellImpl);
    }

    public void updateLastChangeAt(int currVersion) {
        lastChangeAt = currVersion++;
    }

    public  String getOriginalValue() {
        return originalValue;
    }

    public int getLastChangeAt() {
        return lastChangeAt;
    }

    public List<CellImpl> getDependsOn() {
        return dependsOn;
    }

    public List<CellImpl> getAffectsOn() {
        return affectsOn;
    }

    public String getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
