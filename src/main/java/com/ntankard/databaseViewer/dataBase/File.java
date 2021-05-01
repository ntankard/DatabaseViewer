package com.ntankard.databaseViewer.dataBase;

import com.ntankard.databaseViewer.dataBase.DeltaRow.DeltaRowList;
import com.ntankard.databaseViewer.dataBase.type.dataObjectType.DataObjectType;
import com.ntankard.databaseViewer.dataBase.type.dataObjectType.FileDataObjectType;
import com.ntankard.dynamicGUI.javaObjectDatabase.Display_Properties;
import com.ntankard.dynamicGUI.javaObjectDatabase.Displayable_DataObject;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.ListDataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Factory;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;

import java.util.List;

import static com.ntankard.databaseViewer.dataBase.DeltaRow.DeltaRow_ChangeType;
import static com.ntankard.dynamicGUI.javaObjectDatabase.Display_Properties.*;
import static com.ntankard.dynamicGUI.javaObjectDatabase.Display_Properties.DataContext.NOT_FALSE;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.DataCore_Factory.createSelfParentList;

public class File extends DataObject {

    public interface FileList extends List<File> {
    }

    private static final String File_Prefix = "file_";

    public static final String File_SaveInstance = File_Prefix + "SaveInstance";
    public static final String File_Type = File_Prefix + "Type";
    public static final String File_MatchFile = File_Prefix + "MatchFile";
    public static final String File_SimilarFile = File_Prefix + "SimilarFile";
    public static final String File_IDColumn = File_Prefix + "IDColumn";
    public static final String File_NameColumn = File_Prefix + "NameColumn";
    public static final String File_DeltaRows = File_Prefix + "DeltaRows";
    public static final String File_NotChanged = File_Prefix + "NotChanged";

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = Displayable_DataObject.getDataObjectSchema();

        // ID
        dataObjectSchema.add(new DataField_Schema<>(File_SaveInstance, SaveInstance.class));
        dataObjectSchema.add(new DataField_Schema<>(File_Type, FileDataObjectType.class));
        dataObjectSchema.add(new DataField_Schema<>(File_MatchFile, File.class, true));
        dataObjectSchema.add(new DataField_Schema<>(File_SimilarFile, File.class, true));
        dataObjectSchema.add(new DataField_Schema<>(File_IDColumn, Column.class, true));
        dataObjectSchema.add(new DataField_Schema<>(File_NameColumn, Column.class, true));
        dataObjectSchema.add(new ListDataField_Schema<>(File_DeltaRows, DeltaRowList.class));
        dataObjectSchema.add(new DataField_Schema<>(File_NotChanged, Boolean.class));
        // Children

        // File_SaveInstance ===========================================================================================
        dataObjectSchema.get(File_SaveInstance).getProperty(Display_Properties.class).setVerbosityLevel(INFO_DISPLAY);
        // File_MatchFile ==============================================================================================
        dataObjectSchema.get(File_MatchFile).getProperty(Display_Properties.class).setVerbosityLevel(DEBUG_DISPLAY);
        dataObjectSchema.get(File_MatchFile).setManualCanEdit(true);
        // File_SimilarFile ============================================================================================
        dataObjectSchema.get(File_SimilarFile).getProperty(Display_Properties.class).setVerbosityLevel(DEBUG_DISPLAY);
        dataObjectSchema.get(File_SimilarFile).setManualCanEdit(true);
        // File_IDColumn ===============================================================================================
        dataObjectSchema.get(File_IDColumn).getProperty(Display_Properties.class).setVerbosityLevel(INFO_DISPLAY);
        dataObjectSchema.get(File_IDColumn).setManualCanEdit(true);
        // File_NameColumn =============================================================================================
        dataObjectSchema.get(File_NameColumn).getProperty(Display_Properties.class).setVerbosityLevel(INFO_DISPLAY);
        dataObjectSchema.get(File_NameColumn).setManualCanEdit(true);
        // File_DeltaRows ==============================================================================================
        dataObjectSchema.get(File_DeltaRows).getProperty(Display_Properties.class).setVerbosityLevel(TRACE_DISPLAY);
        dataObjectSchema.<List<DeltaRow>>get(File_DeltaRows).setDataCore_schema(createSelfParentList(DeltaRow.class, null));
        // File_NotChanged =============================================================================================
        dataObjectSchema.get(File_NotChanged).getProperty(Display_Properties.class).setDataContext(NOT_FALSE);
        dataObjectSchema.<Boolean>get(File_NotChanged).setDataCore_schema(
                new Derived_DataCore_Schema<Boolean, File>(
                        container -> {
                            for (DeltaRow deltaRow : container.getDeltaRows()) {
                                if (deltaRow.getChangeType() != 0) {
                                    return false;
                                }
                            }
                            return true;
                        }
                        , Source_Factory.makeSharedStepSourceChain(File_DeltaRows, DeltaRow_ChangeType)));

        return dataObjectSchema.finaliseContainer(File.class);
    }

    /**
     * Constructor
     */
    public File(SaveInstance saveInstance, FileDataObjectType type) {
        super(saveInstance.getTrackingDatabase());
        setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                , File_SaveInstance, saveInstance
                , File_Type, type
                , File_MatchFile, null
                , File_SimilarFile, null
                , File_IDColumn, null
                , File_NameColumn, null
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
        return getType().toString();
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public SaveInstance getSaveInstance() {
        return get(File_SaveInstance);
    }

    public DataObjectType getType() {
        return get(File_Type);
    }

    public File getMatchFile() {
        return get(File_MatchFile);
    }

    public File getSimilarFile() {
        return get(File_SimilarFile);
    }

    public Column getIDColumn() {
        return get(File_IDColumn);
    }

    public Column getNameColumn() {
        return get(File_NameColumn);
    }

    public List<DeltaRow> getDeltaRows() {
        return get(File_DeltaRows);
    }

    public Boolean getNotChanged() {
        return get(File_NotChanged);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Setters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public void setMatchFile(File file) {
        set(File_MatchFile, file);
    }

    public void setSimilarFile(File file) {
        set(File_SimilarFile, file);
    }

    public void setIDColumn(Column column) {
        set(File_IDColumn, column);
    }

    public void setNameColumn(Column column) {
        set(File_NameColumn, column);
    }
}
