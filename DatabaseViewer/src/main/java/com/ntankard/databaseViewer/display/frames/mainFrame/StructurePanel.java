package com.ntankard.databaseViewer.display.frames.mainFrame;

import com.ntankard.dynamicGUI.gui.util.update.Updatable;
import com.ntankard.dynamicGUI.gui.util.update.UpdatableJPanel;
import com.ntankard.javaObjectDatabase.database.Database;

import javax.swing.*;
import java.awt.*;

public class StructurePanel extends UpdatableJPanel {

    private TypePanel typePanel;

    // Core database
    private final Database database;

    /**
     * Constructor
     *
     * @param master The parent of this object to be notified if data changes
     */
    protected StructurePanel(Database database, Updatable master) {
        super(master);
        this.database = database;
        createUIComponents();
    }

    private void createUIComponents() {
        this.removeAll();
        this.setLayout(new BorderLayout());

        typePanel = new TypePanel(database, this);

        JTabbedPane master_tPanel = new JTabbedPane();
        master_tPanel.addTab("Types", typePanel);

        this.add(master_tPanel, BorderLayout.CENTER);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void update() {
        typePanel.update();
    }
}
