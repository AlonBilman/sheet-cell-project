package sheet.impl;
import expression.api.Expression;
import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final int row;
    private final int col;
    private final String id;
    private int lastChangeAt;
    private List<Cell> dependsOn;
    private List<Cell> affectsOn;
    private Expression originalValue;
    private String effectiveValue; //string? what should it be?... //bool, int, double, string, they are all printable...

    public Cell(int row, int col) {
        lastChangeAt = 0;
        effectiveValue = " ";
        this.row = row;
        this.col = col;
        this.id = generateId(col,row);
        dependsOn = new ArrayList<>();
        affectsOn = new ArrayList<>();
    }

    public void editCell(Expression value, int version, Cell... depends) {
        originalValue = value;
        effectiveValue = value.eval().toString();
        updateLastChangeAt(version);
        updateCellsThatIAffect();
        updateCellsThatIDependsOn(depends);
    }

    private void updateCellsThatIDependsOn(Cell... depends) {
        //.... update the list. get rid of older that I don't depend on anymore
    }
    public void updateCellsThatIAffect() {
        //
    }

    private String generateId(int col, int row) {
        char letter = (char)('A'+col);
        return letter+String.valueOf(row+1);
    }

    Boolean IdChecker(String id){
        return id.equals(this.id);
    }

    public void removeDependsOn(Cell cell) {
        dependsOn.remove(cell);
    }

    public void updateLastChangeAt(int currVersion) {
        lastChangeAt = currVersion++;
    }

    public  Expression getOriginalValue() {
        return originalValue;
    }

    public int getLastChangeAt() {
        return lastChangeAt;
    }

    public List<Cell> getDependsOn() {
        return dependsOn;
    }

    public List<Cell> getAffectsOn() {
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
