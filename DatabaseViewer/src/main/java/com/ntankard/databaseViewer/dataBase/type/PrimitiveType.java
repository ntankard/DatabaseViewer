package com.ntankard.databaseViewer.dataBase.type;

import com.ntankard.databaseViewer.dataBase.SaveInstance;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;

public class PrimitiveType extends Type {

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
        dataObjectSchema.add(new DataField_Schema<>(Type_TypeClass, Class.class));

        return dataObjectSchema.finaliseContainer(PrimitiveType.class);
    }

    /**
     * Constructor
     */
    public PrimitiveType(SaveInstance saveInstance, Class<?> aClass) {
        super(saveInstance.getTrackingDatabase());
        setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                , Type_Name, aClass.getSimpleName()
                , Type_FullName, aClass.getName()
                , Type_SaveInstance, saveInstance
                , Type_MatchType, null
                , Type_SimilarType, null
                , Type_TypeClass, aClass
        );
    }
}
