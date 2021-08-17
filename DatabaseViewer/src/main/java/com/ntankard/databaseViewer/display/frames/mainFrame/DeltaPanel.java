package com.ntankard.databaseViewer.display.frames.mainFrame;

import com.ntankard.databaseViewer.dataBase.DeltaRow;
import com.ntankard.databaseViewer.dataBase.File;
import com.ntankard.databaseViewer.dataBase.SaveInstance;
import com.ntankard.databaseViewer.dataBase.cell.Cell;
import com.ntankard.dynamicGUI.gui.containers.DynamicGUI_SetDisplayList;
import com.ntankard.dynamicGUI.gui.util.update.Updatable;
import com.ntankard.dynamicGUI.gui.util.update.UpdatableJPanel;
import com.ntankard.dynamicGUI.javaObjectDatabase.Display_Properties;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.util.set.Full_Set;
import com.ntankard.javaObjectDatabase.util.set.OneParent_Children_Set;
import com.ntankard.javaObjectDatabase.util.set.TwoParent_Children_Set;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class DeltaPanel extends UpdatableJPanel {

    // Core database
    private final Database database;

    private ListSelectionListener selectionListener;

    private DynamicGUI_SetDisplayList<SaveInstance> saveInstance_panel;
    private DynamicGUI_SetDisplayList<File> savedDataObject_panel;

    private OneParent_Children_Set<File, SaveInstance> savedDataObject_set;
    private OneParent_Children_Set<DeltaRow, File> savedLine_set;

    private SaveInstance saveInstance_selected = null;
    private File file_selected = null;

    private DeltaRowModel deltaRowModel;

    /**
     * Constructor
     */
    public DeltaPanel(Database database, Updatable master) {
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
        savedDataObject_panel.setVerbosity(Display_Properties.ALWAYS_DISPLAY);
        savedDataObject_panel.getMainPanel().getListSelectionModel().addListSelectionListener(selectionListener);

        savedLine_set = new OneParent_Children_Set<>(DeltaRow.class, null);

        GridBagConstraints summaryContainer_C = new GridBagConstraints();
        summaryContainer_C.fill = GridBagConstraints.BOTH;
        summaryContainer_C.weightx = 1;
        summaryContainer_C.weighty = 1;

        summaryContainer_C.gridx = 0;
        this.add(saveInstance_panel, summaryContainer_C);

        summaryContainer_C.gridx = 1;
        summaryContainer_C.weightx = 2;
        this.add(savedDataObject_panel, summaryContainer_C);

        JTable jTable = new JTable();

        deltaRowModel = new DeltaRowModel();
        jTable.setModel(deltaRowModel);
        jTable.setDefaultRenderer(Object.class, new DeltaRowModelCellRenderer());

        JScrollPane panel = new JScrollPane(jTable);

        summaryContainer_C.gridx = 2;
        summaryContainer_C.weightx = 10;

        this.add(panel, summaryContainer_C);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void update() {
        // Turn off the listeners to prevent a infinite loop
        saveInstance_panel.getMainPanel().getListSelectionModel().removeListSelectionListener(selectionListener);
        savedDataObject_panel.getMainPanel().getListSelectionModel().removeListSelectionListener(selectionListener);

        // Find out the current status of the lists
        int saveInstance_panel_max = saveInstance_panel.getMainPanel().getListSelectionModel().getMaxSelectionIndex();
        int saveInstance_panel_min = saveInstance_panel.getMainPanel().getListSelectionModel().getMaxSelectionIndex();
        int savedDataObject_panel_max = savedDataObject_panel.getMainPanel().getListSelectionModel().getMaxSelectionIndex();
        int savedDataObject_panel_min = savedDataObject_panel.getMainPanel().getListSelectionModel().getMaxSelectionIndex();

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
        }
        List<DeltaRow> deltaRows = null;
        if (saveInstance_selected != null && file_selected != null) {
            deltaRows = new TwoParent_Children_Set<>(DeltaRow.class, saveInstance_selected, file_selected).get();
            deltaRows.sort(Comparator.comparing(DeltaRow::getChangeType).reversed());
        }
        deltaRowModel.setDeltaRows(deltaRows);
        deltaRowModel.update();

        // Turn on the listeners
        saveInstance_panel.getMainPanel().getListSelectionModel().addListSelectionListener(selectionListener);
        savedDataObject_panel.getMainPanel().getListSelectionModel().addListSelectionListener(selectionListener);
    }

    public static class DeltaRowModelCellRenderer extends DefaultTableCellRenderer {

        /**
         * @inheritDoc
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            DeltaRowModel deltaRowModel = (DeltaRowModel) table.getModel();
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            DeltaRow deltaRow = deltaRowModel.deltaRows.get(row);

            if (deltaRow.getChangeType() == 1) {
                l.setBackground(Color.YELLOW);
            } else if (deltaRow.getChangeType() == 2) {
                l.setBackground(Color.GREEN);
            } else if (deltaRow.getChangeType() == 3) {
                l.setBackground(Color.RED);
            } else {
                l.setBackground(new Label().getBackground());
            }
            return l;
        }
    }

    public static class DeltaRowModel extends AbstractTableModel {

        /**
         * The data to display
         */
        public List<DeltaRow> deltaRows = null;

        /**
         * Set the data to display
         *
         * @param deltaRows The data to display
         */
        public void setDeltaRows(List<DeltaRow> deltaRows) {
            this.deltaRows = deltaRows;
        }

        /**
         * @inheritDoc
         */
        @Override
        public int getRowCount() {
            if (deltaRows == null) {
                return 0;
            }
            return deltaRows.size();
        }

        /**
         * @inheritDoc
         */
        @Override
        public int getColumnCount() {
            if (deltaRows == null) {
                return 0;
            }
            DeltaRow deltaRow = deltaRows.get(0);
            if (deltaRow.getCurrentRow() != null) {
                return new OneParent_Children_Set<>(Cell.class, deltaRow.getCurrentRow()).get().size() * 2;
            } else {
                return new OneParent_Children_Set<>(Cell.class, deltaRow.getPastRow()).get().size() * 2;
            }
        }

        /**
         * @inheritDoc
         */
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DeltaRow deltaRow = deltaRows.get(rowIndex);
            if (columnIndex % 2 != 0) {
                if (deltaRow.getCurrentRow() == null) {
                    return "";
                }
                return new OneParent_Children_Set<>(Cell.class, deltaRow.getCurrentRow()).get().get(columnIndex / 2).getValue();
            } else {
                if (deltaRow.getPastRow() == null) {
                    return "";
                }
                return new OneParent_Children_Set<>(Cell.class, deltaRow.getPastRow()).get().get(columnIndex / 2).getValue();
            }
        }

        /**
         * @inheritDoc
         */
        public void update() {
            fireTableStructureChanged();
            fireTableDataChanged();
        }
    }
}
