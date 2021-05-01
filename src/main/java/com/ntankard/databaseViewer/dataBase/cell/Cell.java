package com.ntankard.databaseViewer.dataBase.cell;

import com.ntankard.databaseViewer.dataBase.Column;
import com.ntankard.databaseViewer.dataBase.Row;
import com.ntankard.databaseViewer.dataBase.type.dataObjectType.DataObjectType;
import com.ntankard.dynamicGUI.javaObjectDatabase.Displayable_DataObject;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Factory;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;

import java.util.List;

public abstract class Cell extends DataObject {

    public interface CellList extends List<Cell> {
    }

    private static final String Cell_Prefix = "cell_";

    public static final String Cell_Row = Cell_Prefix + "Row";
    public static final String Cell_Column = Cell_Prefix + "Column";
    public static final String Cell_Value = Cell_Prefix + "Value";
    public static final String Cell_MatchCell = Cell_Prefix + "MatchCell";
    public static final String Cell_SimilarCell = Cell_Prefix + "SimilarCell";
    public static final String Cell_Changed = Cell_Prefix + "Changed";

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = Displayable_DataObject.getDataObjectSchema();

        // ID
        dataObjectSchema.add(new DataField_Schema<>(Cell_Row, Row.class));
        dataObjectSchema.add(new DataField_Schema<>(Cell_Column, Column.class));
        dataObjectSchema.add(new DataField_Schema<>(Cell_Value, String.class));
        dataObjectSchema.add(new DataField_Schema<>(Cell_MatchCell, Cell.class, true));
        dataObjectSchema.add(new DataField_Schema<>(Cell_SimilarCell, Cell.class, true));
        dataObjectSchema.add(new DataField_Schema<>(Cell_Changed, Boolean.class));
        // Children

        // Cell_MatchCell ==============================================================================================
        dataObjectSchema.get(Cell_MatchCell).setManualCanEdit(true);
        // Cell_SimilarCell ============================================================================================
        dataObjectSchema.get(Cell_SimilarCell).setManualCanEdit(true);
        // Cell_Changed ================================================================================================
        dataObjectSchema.<Boolean>get(Cell_Changed).setDataCore_schema(
                new Derived_DataCore_Schema<Boolean, Cell>(
                        container -> {
                            if (container.getMatchCell() != null) {
                                if (container.getValue().equals(container.getMatchCell().getValue())) {
                                    return false;
                                } else {
                                    throw new RuntimeException();
                                }
                            }
                            if (container.getSimilarCell() != null) {
                                if (container.getValue().equals(container.getSimilarCell().getValue())) {
                                    throw new RuntimeException();
                                } else {
                                    return true;
                                }
                            }
                            return true;
                        }
                        , Source_Factory.makeSourceChain(Cell_SimilarCell, Cell_Value)
                        , Source_Factory.makeSourceChain(Cell_MatchCell, Cell_Value)
                        , Source_Factory.makeSourceChain(Cell_Value)));

        return dataObjectSchema.endLayer(Cell.class);
    }

    /**
     * Constructor
     */
    public Cell(Database database) {
        super(database);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### General #####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return getRow().toString() + " - " + getColumn().toString() + " - " + getValue();
    }

    //------------------------------------------------------------------------------------------------------------------
    //###################################################### Util ######################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Make a new cell of the correct type based on its value
     *
     * @param row    The Row of the cell
     * @param column The Column of the cell
     * @param value  The value of the cell
     * @return The new Cell
     */
    public static Cell createCell(Row row, Column column, String value) {
        if (DataObjectType.class.isAssignableFrom(column.getType().getClass())) {
            return new DataObject_Cell(row, column, value).add();
        } else {
            return new Primitive_Cell(row, column, value).add();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public Row getRow() {
        return get(Cell_Row);
    }

    public Column getColumn() {
        return get(Cell_Column);
    }

    public String getValue() {
        return get(Cell_Value);
    }

    public Cell getMatchCell() {
        return get(Cell_MatchCell);
    }

    public Cell getSimilarCell() {
        return get(Cell_SimilarCell);
    }

    public Boolean getChanged() {
        return get(Cell_Changed);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Setters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public void setMatchCell(Cell cell) {
        set(Cell_MatchCell, cell);
    }

    public void setSimilarCell(Cell cell) {
        set(Cell_SimilarCell, cell);
    }
}
