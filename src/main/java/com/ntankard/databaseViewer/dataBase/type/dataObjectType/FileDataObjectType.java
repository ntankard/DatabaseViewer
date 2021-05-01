package com.ntankard.databaseViewer.dataBase.type.dataObjectType;

import com.ntankard.databaseViewer.dataBase.SaveInstance;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;

public class FileDataObjectType extends DataObjectType {

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = DataObjectType.getDataObjectSchema();

        // ID
        // -Name
        // -FullName
        // -SaveInstance
        // -MatchType
        // -SimilarType
        // -TypeClass

        return dataObjectSchema.finaliseContainer(FileDataObjectType.class);
    }

    /**
     * Constructor
     */
    public FileDataObjectType(Class<?> aClass, SaveInstance saveInstance) {
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

    /**
     * Constructor
     */
    public FileDataObjectType(String fullName, SaveInstance saveInstance) {
        super(saveInstance.getTrackingDatabase());
        String[] words = fullName.split("\\.");
        setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                , Type_Name, words[words.length - 1]
                , Type_FullName, fullName
                , Type_SaveInstance, saveInstance
                , Type_MatchType, null
                , Type_SimilarType, null
                , Type_TypeClass, null
        );
    }
}
