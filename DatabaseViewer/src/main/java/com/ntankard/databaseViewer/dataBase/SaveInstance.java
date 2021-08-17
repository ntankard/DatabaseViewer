package com.ntankard.databaseViewer.dataBase;

import com.ntankard.dynamicGUI.javaObjectDatabase.Display_Properties;
import com.ntankard.dynamicGUI.javaObjectDatabase.Displayable_DataObject;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.ListDataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Factory;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;

import java.util.List;

import static com.ntankard.databaseViewer.dataBase.File.File_NotChanged;
import static com.ntankard.dynamicGUI.javaObjectDatabase.Display_Properties.*;
import static com.ntankard.dynamicGUI.javaObjectDatabase.Display_Properties.DataContext.*;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.DataCore_Factory.createSelfParentList;

public class SaveInstance extends DataObject {

    private static final String SaveInstance_Prefix = "saveInstance_";

    public static final String SaveInstance_SaveID = SaveInstance_Prefix + "SaveID";
    public static final String SaveInstance_Path = SaveInstance_Prefix + "Path";
    public static final String SaveInstance_Past = SaveInstance_Prefix + "Past";
    public static final String SaveInstance_Files = SaveInstance_Prefix + "Files";
    public static final String SaveInstance_NotChanged = SaveInstance_Prefix + "NotChanged";

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = Displayable_DataObject.getDataObjectSchema();

        // ID
        dataObjectSchema.add(new DataField_Schema<>(SaveInstance_SaveID, Integer.class));
        dataObjectSchema.add(new DataField_Schema<>(SaveInstance_Path, String.class));
        dataObjectSchema.add(new DataField_Schema<>(SaveInstance_Past, SaveInstance.class, true));
        dataObjectSchema.add(new ListDataField_Schema<>(SaveInstance_Files, DeltaRow.DeltaRowList.class));
        dataObjectSchema.add(new DataField_Schema<>(SaveInstance_NotChanged, Boolean.class));
        // Children

        // SaveInstance_Path ===========================================================================================
        dataObjectSchema.get(SaveInstance_Path).getProperty(Display_Properties.class).setVerbosityLevel(INFO_DISPLAY);
        // SaveInstance_Past ===========================================================================================
        dataObjectSchema.get(SaveInstance_Past).getProperty(Display_Properties.class).setVerbosityLevel(INFO_DISPLAY);
        // SaveInstance_Files ==========================================================================================
        dataObjectSchema.<List<File>>get(SaveInstance_Files).setDataCore_schema(createSelfParentList(File.class, null));
        dataObjectSchema.get(SaveInstance_Files).getProperty(Display_Properties.class).setVerbosityLevel(TRACE_DISPLAY);
        // SaveInstance_NotChanged =====================================================================================
        dataObjectSchema.get(SaveInstance_NotChanged).getProperty(Display_Properties.class).setDataContext(NOT_FALSE);
        dataObjectSchema.<Boolean>get(SaveInstance_NotChanged).setDataCore_schema(
                new Derived_DataCore_Schema<Boolean, SaveInstance>(
                        container -> {
                            for (File file : container.getFiles()) {
                                if (!file.getNotChanged()) {
                                    return false;
                                }
                            }
                            return true;
                        }
                        , Source_Factory.makeSharedStepSourceChain(SaveInstance_Files, File_NotChanged)));

        return dataObjectSchema.finaliseContainer(SaveInstance.class);
    }

    /**
     * Constructor
     */
    public SaveInstance(Database database, Integer saveID, String path, SaveInstance pastSaveInstance) {
        super(database);
        setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                , SaveInstance_SaveID, saveID
                , SaveInstance_Path, path
                , SaveInstance_Past, pastSaveInstance
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
        return getSaveID().toString();
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public Integer getSaveID() {
        return get(SaveInstance_SaveID);
    }

    public String getPath() {
        return get(SaveInstance_Path);
    }

    public SaveInstance getPast() {
        return get(SaveInstance_Past);
    }

    public List<File> getFiles() {
        return get(SaveInstance_Files);
    }
}
