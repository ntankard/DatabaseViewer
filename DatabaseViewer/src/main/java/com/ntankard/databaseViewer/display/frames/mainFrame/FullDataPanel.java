package com.ntankard.databaseViewer.display.frames.mainFrame;

import com.ntankard.databaseViewer.dataBase.SaveInstance;
import com.ntankard.databaseViewer.dataBase.File;
import com.ntankard.databaseViewer.dataBase.Row;
import com.ntankard.databaseViewer.dataBase.cell.Cell;
import com.ntankard.dynamicGUI.gui.containers.DynamicGUI_SetDisplayList;
import com.ntankard.dynamicGUI.gui.util.update.Updatable;
import com.ntankard.dynamicGUI.gui.util.update.UpdatableJPanel;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.util.set.Full_Set;
import com.ntankard.javaObjectDatabase.util.set.OneParent_Children_Set;

import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

public class FullDataPanel extends UpdatableJPanel {

    // Core database
    private final Database database;

    private ListSelectionListener selectionListener;

    private DynamicGUI_SetDisplayList<SaveInstance> saveInstance_panel;
    private DynamicGUI_SetDisplayList<File> savedDataObject_panel;
    private DynamicGUI_SetDisplayList<Row> savedLine_panel;
    private DynamicGUI_SetDisplayList<Cell> savedField_panel;

    private OneParent_Children_Set<File, SaveInstance> savedDataObject_set;
    private OneParent_Children_Set<Row, File> savedLine_set;
    private OneParent_Children_Set<Cell, Row> savedField_set;

    private SaveInstance saveInstance_selected = null;
    private File file_selected = null;

    /**
     * Constructor
     */
    public FullDataPanel(Database database, Updatable master) {
        super(master);
        this.database = database;
        createUIComponents();
    }

    /**
     * Create the GUI components
     */
    private void createUIComponents() {
        this.removeAll();
        this.setLayout(new GridBagLayout());

        selectionListener = e -> update();

        saveInstance_panel = new DynamicGUI_SetDisplayList<>(database.getSchema(), SaveInstance.class, new Full_Set<>(database, SaveInstance.class), false, this);
        saveInstance_panel.getMainPanel().getListSelectionModel().addListSelectionListener(selectionListener);

        savedDataObject_set = new OneParent_Children_Set<>(File.class, null);
        savedDataObject_panel = new DynamicGUI_SetDisplayList<>(database.getSchema(), File.class, savedDataObject_set, false, this);
        savedDataObject_panel.getMainPanel().getListSelectionModel().addListSelectionListener(selectionListener);

        savedLine_set = new OneParent_Children_Set<>(Row.class, null);
        savedLine_panel = new DynamicGUI_SetDisplayList<>(database.getSchema(), Row.class, savedLine_set, false, this);
        savedLine_panel.getMainPanel().getListSelectionModel().addListSelectionListener(selectionListener);

        savedField_set = new OneParent_Children_Set<>(Cell.class, null);
        savedField_panel = new DynamicGUI_SetDisplayList<>(database.getSchema(), Cell.class, savedField_set, false, this);

        GridBagConstraints summaryContainer_C = new GridBagConstraints();
        summaryContainer_C.fill = GridBagConstraints.BOTH;
        summaryContainer_C.weightx = 1;
        summaryContainer_C.weighty = 1;

        summaryContainer_C.gridx = 0;
        this.add(saveInstance_panel, summaryContainer_C);

        summaryContainer_C.gridx = 1;
        this.add(savedDataObject_panel, summaryContainer_C);

        summaryContainer_C.gridx = 2;
        this.add(savedLine_panel, summaryContainer_C);

        summaryContainer_C.gridx = 3;
        this.add(savedField_panel, summaryContainer_C);
    }

    @Override
    public void update() {
        // Turn off the listeners to prevent a infinite loop
        saveInstance_panel.getMainPanel().getListSelectionModel().removeListSelectionListener(selectionListener);
        savedDataObject_panel.getMainPanel().getListSelectionModel().removeListSelectionListener(selectionListener);
        savedLine_panel.getMainPanel().getListSelectionModel().removeListSelectionListener(selectionListener);

        // Find out the current status of the lists
        int saveInstance_panel_max = saveInstance_panel.getMainPanel().getListSelectionModel().getMaxSelectionIndex();
        int saveInstance_panel_min = saveInstance_panel.getMainPanel().getListSelectionModel().getMaxSelectionIndex();
        int savedDataObject_panel_max = savedDataObject_panel.getMainPanel().getListSelectionModel().getMaxSelectionIndex();
        int savedDataObject_panel_min = savedDataObject_panel.getMainPanel().getListSelectionModel().getMaxSelectionIndex();
        int savedLine_panel_max = savedLine_panel.getMainPanel().getListSelectionModel().getMaxSelectionIndex();
        int savedLine_panel_min = savedLine_panel.getMainPanel().getListSelectionModel().getMaxSelectionIndex();

        // Update the SaveInstance panel and select the same element as before
        saveInstance_panel.update();
        saveInstance_panel.getMainPanel().getListSelectionModel().setSelectionInterval(saveInstance_panel_max, saveInstance_panel_min);

        // Find the selected SaveInstance
        SaveInstance newSaveInstance_selected = null;
        List<?> selected = saveInstance_panel.getMainPanel().getSelectedItems();
        if (selected.size() == 1) {
            newSaveInstance_selected = ((SaveInstance) selected.get(0));
        }

        // If the selected SaveInstance was changed clear everything below
        if (newSaveInstance_selected != saveInstance_selected) {
            saveInstance_selected = newSaveInstance_selected;
            savedDataObject_set.setParent(saveInstance_selected);
            savedDataObject_panel_max = -1;
            savedDataObject_panel_min = -1;
            savedLine_panel_max = -1;
            savedLine_panel_min = -1;
        }

        // Update the SavedDataObject panel and select the same element as before
        savedDataObject_panel.update();
        savedDataObject_panel.getMainPanel().getListSelectionModel().setSelectionInterval(savedDataObject_panel_max, savedDataObject_panel_min);

        // Find the selected SavedDataObject
        File newFile_selected = null;
        selected = savedDataObject_panel.getMainPanel().getSelectedItems();
        if (selected.size() == 1) {
            newFile_selected = ((File) selected.get(0));
        }

        // If the selected SavedDataObject was changed clear everything below
        if (newFile_selected != file_selected) {
            file_selected = newFile_selected;
            savedLine_set.setParent(file_selected);
            savedLine_panel_max = -1;
            savedLine_panel_min = -1;
        }

        // Update the SavedLine panel and select the same element as before
        savedLine_panel.update();
        savedLine_panel.getMainPanel().getListSelectionModel().setSelectionInterval(savedLine_panel_max, savedLine_panel_min);

        // Find the selected SavedLine
        Row row_selected = null;
        selected = savedLine_panel.getMainPanel().getSelectedItems();
        if (selected.size() == 1) {
            row_selected = ((Row) selected.get(0));
        }

        // Update the SavedLine panel
        savedField_set.setParent(row_selected);
        savedField_panel.update();

        // Turn on the listeners
        saveInstance_panel.getMainPanel().getListSelectionModel().addListSelectionListener(selectionListener);
        savedDataObject_panel.getMainPanel().getListSelectionModel().addListSelectionListener(selectionListener);
        savedLine_panel.getMainPanel().getListSelectionModel().addListSelectionListener(selectionListener);
    }
}
