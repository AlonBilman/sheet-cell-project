package FileCheck;

import jakarta.xml.bind.annotation.*;

import java.util.List;

public class XMLTypes {
    @XmlRootElement(name = "STL-Sheet")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class STLSheet {

        @XmlElement(name = "STL-Layout")
        private CheckForXMLFile.STLLayout layout;

        @XmlElementWrapper(name = "STL-Cells")
        @XmlElement(name = "STL-Cell")
        private List<CheckForXMLFile.STLCell> cells;

        public CheckForXMLFile.STLLayout getLayout() {
            return layout;
        }
        public List<CheckForXMLFile.STLCell> getCells() {
            return cells;
        }

        public void setCells(List<CheckForXMLFile.STLCell> cells) {
            this.cells = cells;
        }

        public void setLayout(CheckForXMLFile.STLLayout layout) {
            this.layout = layout;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class STLLayout {

        @XmlAttribute(name = "rows")
        private int rows;

        @XmlAttribute(name = "columns")
        private int columns;

        public int getRows() {
            return rows;
        }

        public void setRows(int rows) {
            this.rows = rows;
        }

        public int getColumns() {
            return columns;
        }

        public void setColumns(int columns) {
            this.columns = columns;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class STLCell {
        @XmlAttribute(name = "row")
        private int row;

        @XmlAttribute(name = "column")
        private String column;

        @XmlElement(name = "STL-Original-Value")
        private String originalValue;

        // Getters and setters
        public int getRow() {
            return row;
        }

//        public void setRow(int row) {
//            this.row = row;
//        }

        public String getColumn() {
            return column;
        }

//        public void setColumn(String column) {
//            this.column = column;
//        }

        public String getOriginalValue() {
            return originalValue;
        }

//        public void setOriginalValue(String originalValue) {
//            this.originalValue = originalValue;
//        }
    }
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class STLSize {
        @XmlAttribute(name = "rows-height-units")
        private int rowHeightUnits;

        @XmlAttribute(name = "column-width-units")
        private int columnWidthUnits;

        public int getRowHeightUnits() {
            return rowHeightUnits;
        }
        public void setRowHeightUnits(int rowHeightUnits) {
            this.rowHeightUnits = rowHeightUnits;
        }
        public int getColumnWidthUnits() {
            return columnWidthUnits;
        }
        public void setColumnWidthUnits(int columnWidthUnits) {
            this.columnWidthUnits = columnWidthUnits;
        }
    }
}

