package com.ntankard.databaseViewer.dataBase;

import com.ntankard.dynamicGUI.javaObjectDatabase.Displayable_DataObject;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Factory;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;

import java.util.List;

import static com.ntankard.databaseViewer.dataBase.Row.Row_Changed;

public class DeltaRow extends DataObject {

    public interface DeltaRowList extends List<DeltaRow> {
    }

    private static final String DeltaRow_Prefix = "deltaRow_";

    public static final String DeltaRow_SaveInstance = DeltaRow_Prefix + "SaveInstance";
    public static final String DeltaRow_File = DeltaRow_Prefix + "File";
    public static final String DeltaRow_CurrentRow = DeltaRow_Prefix + "CurrentRow";
    public static final String DeltaRow_PastRow = DeltaRow_Prefix + "PastRow";
    public static final String DeltaRow_ChangeType = DeltaRow_Prefix + "ChangeType";

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = Displayable_DataObject.getDataObjectSchema();

        // ID
        dataObjectSchema.add(new DataField_Schema<>(DeltaRow_SaveInstance, SaveInstance.class));
        dataObjectSchema.add(new DataField_Schema<>(DeltaRow_File, File.class));
        dataObjectSchema.add(new DataField_Schema<>(DeltaRow_CurrentRow, Row.class, true));
        dataObjectSchema.add(new DataField_Schema<>(DeltaRow_PastRow, Row.class, true));
        dataObjectSchema.add(new DataField_Schema<>(DeltaRow_ChangeType, Integer.class));
        // Children

        // DeltaRow_ChangeType =========================================================================================
        dataObjectSchema.<Integer>get(DeltaRow_ChangeType).setDataCore_schema(
                new Derived_DataCore_Schema<Integer, DeltaRow>(
                        container -> {
                            if (container.getCurrentRow() == null) {
                                return 3;
                            } else {
                                if (container.getPastRow() == null) {
                                    return 2;
                                }
                                return container.getCurrentRow().getChanged() ? 1 : 0;
                            }
                        }
                        , Source_Factory.makeSourceChain(DeltaRow_CurrentRow, Row_Changed)
                        , Source_Factory.makeSourceChain(DeltaRow_PastRow)));

        return dataObjectSchema.finaliseContainer(DeltaRow.class);
    }

    /**
     * Constructor
     */
    public DeltaRow(SaveInstance saveInstance, File file, Row currentRow, Row pastRow) {
        super(saveInstance.getTrackingDatabase());
        setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                , DeltaRow_SaveInstance, saveInstance
                , DeltaRow_File, file
                , DeltaRow_CurrentRow, currentRow
                , DeltaRow_PastRow, pastRow
        );
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### General #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public SaveInstance getSaveInstance() {
        return get(DeltaRow_SaveInstance);
    }

    public File getFile() {
        return get(DeltaRow_File);
    }

    public Row getCurrentRow() {
        return get(DeltaRow_CurrentRow);
    }

    public Row getPastRow() {
        return get(DeltaRow_PastRow);
    }

    public Integer getChangeType() {
        return get(DeltaRow_ChangeType);
    }
}
