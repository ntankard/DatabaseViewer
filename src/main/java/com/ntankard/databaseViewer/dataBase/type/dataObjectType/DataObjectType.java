package com.ntankard.databaseViewer.dataBase.type.dataObjectType;

import com.ntankard.databaseViewer.dataBase.type.Type;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;

public abstract class DataObjectType extends Type {

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = Type.getDataObjectSchema();

        // ID
        // -Name
        // -FullName
        // -SaveInstance
        // -MatchType
        // -SimilarType
        dataObjectSchema.add(new DataField_Schema<>(Type_TypeClass, Class.class, true));

        return dataObjectSchema.endLayer(DataObjectType.class);
    }

    /**
     * Constructor
     */
    public DataObjectType(Database database) {
        super(database);
    }
}
