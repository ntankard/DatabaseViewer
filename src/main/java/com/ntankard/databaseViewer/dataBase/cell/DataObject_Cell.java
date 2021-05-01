package com.ntankard.databaseViewer.dataBase.cell;

import com.ntankard.databaseViewer.dataBase.Column;
import com.ntankard.databaseViewer.dataBase.Row;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;

public class DataObject_Cell extends Cell {

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = Cell.getDataObjectSchema();

        // ID
        // -Row
        // -Column
        // -Value

        return dataObjectSchema.finaliseContainer(DataObject_Cell.class);
    }

    /**
     * Constructor
     */
    public DataObject_Cell(Row row, Column column, String value) {
        super(column.getTrackingDatabase());
        setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                , Cell_Row, row
                , Cell_Column, column
                , Cell_Value, value
                , Cell_MatchCell, null
                , Cell_SimilarCell, null
        );
    }
}
