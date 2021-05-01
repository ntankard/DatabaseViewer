package com.ntankard.databaseViewer.dataBase;

import com.ntankard.databaseViewer.dataBase.cell.Cell;
import com.ntankard.databaseViewer.dataBase.cell.Cell.CellList;
import com.ntankard.dynamicGUI.javaObjectDatabase.Displayable_DataObject;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.ListDataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Factory;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;

import java.util.List;

import static com.ntankard.databaseViewer.dataBase.cell.Cell.Cell_Changed;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.DataCore_Factory.createSelfParentList;

public class Row extends DataObject {

    public interface RowList extends List<Row> {
    }

    private static final String Row_Prefix = "row_";

    public static final String Row_File = Row_Prefix + "File";
    public static final String Row_LineNum = Row_Prefix + "LineNum";
    public static final String Row_RowID = Row_Prefix + "RowID";
    public static final String Row_MatchRow = Row_Prefix + "MatchRow";
    public static final String Row_Cells = Row_Prefix + "Cells";
    public static final String Row_Changed = Row_Prefix + "Changed";

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = Displayable_DataObject.getDataObjectSchema();

        // ID
        dataObjectSchema.add(new DataField_Schema<>(Row_File, File.class));
        dataObjectSchema.add(new DataField_Schema<>(Row_LineNum, Integer.class));
        dataObjectSchema.add(new DataField_Schema<>(Row_RowID, Integer.class, true));
        dataObjectSchema.add(new DataField_Schema<>(Row_MatchRow, Row.class, true));
        dataObjectSchema.add(new ListDataField_Schema<>(Row_Cells, CellList.class));
        dataObjectSchema.add(new DataField_Schema<>(Row_Changed, Boolean.class));
        // Children

        // Row_RowID ===================================================================================================
        dataObjectSchema.get(Row_RowID).setManualCanEdit(true);
        // Row_MatchRow ================================================================================================
        dataObjectSchema.get(Row_MatchRow).setManualCanEdit(true);
        // Row_Cells ===================================================================================================
        dataObjectSchema.<List<Cell>>get(Row_Cells).setDataCore_schema(createSelfParentList(Cell.class, null));
        // Row_Changed =================================================================================================
        dataObjectSchema.<Boolean>get(Row_Changed).setDataCore_schema(
                new Derived_DataCore_Schema<Boolean, Row>(
                        container -> {
                            for (Cell cell : container.getCells()) {
                                if (cell.getChanged()) {
                                    return true;
                                }
                            }
                            return false;
                        }
                        , Source_Factory.makeSharedStepSourceChain(Row_Cells, Cell_Changed)));

        return dataObjectSchema.finaliseContainer(Row.class);
    }

    /**
     * Constructor
     */
    public Row(File file, Integer lineNum) {
        super(file.getTrackingDatabase());
        setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                , Row_File, file
                , Row_LineNum, lineNum
                , Row_RowID, null
                , Row_MatchRow, null
        );
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### General #####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return getFile().toString() + ":" + getLineNum().toString();
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public File getFile() {
        return get(Row_File);
    }

    public Integer getRowID() {
        return get(Row_RowID);
    }

    public Integer getLineNum() {
        return get(Row_LineNum);
    }

    public Row getMatchRow() {
        return get(Row_MatchRow);
    }

    public List<Cell> getCells() {
        return get(Row_Cells);
    }

    public Boolean getChanged() {
        return get(Row_Changed);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Setters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public void setRowID(Integer id) {
        set(Row_RowID, id);
    }

    public void setMatchRow(Row row) {
        set(Row_MatchRow, row);
    }
}
