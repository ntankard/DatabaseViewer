package com.ntankard.databaseViewer.dataBase;

import com.ntankard.databaseViewer.dataBase.type.Type;
import com.ntankard.dynamicGUI.javaObjectDatabase.Displayable_DataObject;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;

public class Column extends DataObject {

    private static final String Column_Prefix = "column_";

    public static final String Column_File = Column_Prefix + "File";
    public static final String Column_Name = Column_Prefix + "Name";
    public static final String Column_Type = Column_Prefix + "Type";
    public static final String Column_MatchColumn = Column_Prefix + "MatchColumn";
    public static final String Column_SimilarColumn = Column_Prefix + "SimilarColumn";

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = Displayable_DataObject.getDataObjectSchema();

        // ID
        dataObjectSchema.add(new DataField_Schema<>(Column_File, File.class));
        dataObjectSchema.add(new DataField_Schema<>(Column_Name, String.class));
        dataObjectSchema.add(new DataField_Schema<>(Column_Type, Type.class));
        dataObjectSchema.add(new DataField_Schema<>(Column_MatchColumn, Type.class, true));
        dataObjectSchema.add(new DataField_Schema<>(Column_SimilarColumn, Type.class, true));

        // MatchColumn ===============================================================================================
        dataObjectSchema.get(Column_MatchColumn).setManualCanEdit(true);
        // SimilarColumn =============================================================================================
        dataObjectSchema.get(Column_SimilarColumn).setManualCanEdit(true);

        return dataObjectSchema.finaliseContainer(Column.class);
    }

    /**
     * Constructor
     */
    public Column(File file, String name, Type type) {
        super(file.getTrackingDatabase());
        setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                , Column_File, file
                , Column_Name, name
                , Column_Type, type
                , Column_MatchColumn, null
                , Column_SimilarColumn, null
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
        return getFile().toString() + " - " + getName() + " - " + getType().getName();
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public File getFile() {
        return get(Column_File);
    }

    public String getName() {
        return get(Column_Name);
    }

    public Type getType() {
        return get(Column_Type);
    }

    public Column getMatchColumn() {
        return get(Column_MatchColumn);
    }

    public Column getSimilarColumn() {
        return get(Column_SimilarColumn);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Setters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public void setMatchColumn(Column column) {
        set(Column_MatchColumn, column);
    }

    public void setSimilarColumn(Column column) {
        set(Column_SimilarColumn, column);
    }
}
