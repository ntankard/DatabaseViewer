package com.ntankard.databaseViewer.dataBase.cell;

import com.ntankard.databaseViewer.dataBase.Column;
import com.ntankard.databaseViewer.dataBase.Row;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;

public class Primitive_Cell extends Cell {

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = Cell.getDataObjectSchema();

        // ID
        // -Row
        // -Column
        // -Value

        return dataObjectSchema.finaliseContainer(Primitive_Cell.class);
    }

    /**
     * Constructor
     */
    public Primitive_Cell(Row row, Column column, String value) {
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
