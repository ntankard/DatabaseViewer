package com.ntankard.databaseViewer.dataBase.type;

import com.ntankard.databaseViewer.dataBase.SaveInstance;
import com.ntankard.databaseViewer.dataBase.type.dataObjectType.FieldDataObjectType;
import com.ntankard.databaseViewer.dataBase.type.dataObjectType.FileDataObjectType;
import com.ntankard.dynamicGUI.javaObjectDatabase.Displayable_DataObject;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.util.set.OneParent_Children_Set;

public abstract class Type extends DataObject {

    private static final String Type_Prefix = "type_";

    public static final String Type_Name = Type_Prefix + "Name";
    public static final String Type_FullName = Type_Prefix + "FullName";
    public static final String Type_SaveInstance = Type_Prefix + "SaveInstance";
    public static final String Type_MatchType = Type_Prefix + "MatchType";
    public static final String Type_SimilarType = Type_Prefix + "SimilarType";
    public static final String Type_TypeClass = Type_Prefix + "TypeClass";

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = Displayable_DataObject.getDataObjectSchema();

        // ID
        dataObjectSchema.add(new DataField_Schema<>(Type_Name, String.class));
        dataObjectSchema.add(new DataField_Schema<>(Type_FullName, String.class));
        dataObjectSchema.add(new DataField_Schema<>(Type_SaveInstance, SaveInstance.class));
        dataObjectSchema.add(new DataField_Schema<>(Type_MatchType, Type.class, true));
        dataObjectSchema.add(new DataField_Schema<>(Type_SimilarType, Type.class, true));
        // -TypeClass

        // MatchType ===============================================================================================
        dataObjectSchema.get(Type_MatchType).setManualCanEdit(true);
        // SimilarType =============================================================================================
        dataObjectSchema.get(Type_SimilarType).setManualCanEdit(true);

        return dataObjectSchema.endLayer(Type.class);
    }

    /**
     * Constructor
     */
    public Type(Database database) {
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
        return getSaveInstance().toString() + " - " + getName();
    }

    //------------------------------------------------------------------------------------------------------------------
    //###################################################### Util ######################################################
    //------------------------------------------------------------------------------------------------------------------

    public static Type getOrCreateType(String fullName, SaveInstance saveInstance, boolean isFile) {

        // Search for an existing type
        for (Type type : new OneParent_Children_Set<>(Type.class, saveInstance).get()) {
            if (type.getFullName().equals(fullName)) {
                return type;
            }
        }

        // Create a new type
        Type newType;
        try {
            Class<?> aClass = Class.forName(fullName);
            if (DataObject.class.isAssignableFrom(aClass)) {
                if (isFile) {
                    newType = new FileDataObjectType(aClass, saveInstance);
                } else {
                    newType = new FieldDataObjectType(saveInstance, aClass);
                }
            } else {
                newType = new PrimitiveType(saveInstance, aClass);
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            if (isFile) {
                newType = new FileDataObjectType(fullName, saveInstance);
            } else {
                newType = new FieldDataObjectType(fullName, saveInstance);
            }
        }

        newType.add();
        return newType;
    }


    public boolean areRelated(Type pastType){
        return pastType.equals(getMatchType()) || pastType.equals(getSimilarType());
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public String getName() {
        return get(Type_Name);
    }

    public String getFullName() {
        return get(Type_FullName);
    }

    public SaveInstance getSaveInstance() {
        return get(Type_SaveInstance);
    }

    public Type getMatchType() {
        return get(Type_MatchType);
    }

    public Type getSimilarType() {
        return get(Type_SimilarType);
    }

    public Class<?> getTypeClass() {
        return get(Type_TypeClass);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Setters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public void setMatchType(Type type) {
        set(Type_MatchType, type);
    }

    public void setSimilarType(Type type) {
        set(Type_SimilarType, type);
    }
}
