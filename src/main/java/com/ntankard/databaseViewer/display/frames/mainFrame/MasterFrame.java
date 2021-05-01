package com.ntankard.databaseViewer.display.frames.mainFrame;

import com.ntankard.dynamicGUI.gui.util.containers.ButtonPanel;
import com.ntankard.dynamicGUI.gui.util.panels.LimitedDatabasePanel;
import com.ntankard.dynamicGUI.gui.util.update.Updatable;
import com.ntankard.javaObjectDatabase.database.Database;

import javax.swing.*;
import java.awt.*;

public class MasterFrame extends JPanel implements Updatable {

    // The GUI components

    // Core database
    private final Database database;

    private DeltaPanel deltaPanel;
    private LimitedDatabasePanel limitedDatabasePanel;
    private StructurePanel structurePanel;
    private FullDataPanel fullDataPanel;

    /**
     * Create and open the tracking frame
     */
    public static void open(Database database) {
        SwingUtilities.invokeLater(() -> {
            JFrame _frame = new JFrame("Database Viewer");
            _frame.setContentPane(new MasterFrame(database));
            _frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            _frame.pack();
            _frame.setVisible(true);

            _frame.repaint();
        });
    }

    /**
     * Constructor
     */
    private MasterFrame(Database database) {
        this.database = database;
        createUIComponents();
        update();
    }

    /**
     * Create the GUI components
     */
    private void createUIComponents() {
        this.removeAll();
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(1500, 1000));

        ButtonPanel btnPanel = new ButtonPanel();

        this.add(btnPanel, BorderLayout.NORTH);

        deltaPanel = new DeltaPanel(database, this);
        limitedDatabasePanel = new LimitedDatabasePanel(database, this);
        structurePanel = new StructurePanel(database, this);
        fullDataPanel = new FullDataPanel(database, this);

        JTabbedPane master_tPanel = new JTabbedPane();

        master_tPanel.addTab("DeltaPanel", deltaPanel);
        master_tPanel.addTab("FullDataPanel", fullDataPanel);
        master_tPanel.addTab("Database", limitedDatabasePanel);
        master_tPanel.addTab("Structure", structurePanel);

        this.add(master_tPanel, BorderLayout.CENTER);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void notifyUpdate() {
        SwingUtilities.invokeLater(this::update);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void update() {
        deltaPanel.update();
        limitedDatabasePanel.update();
        structurePanel.update();
        fullDataPanel.update();
    }
}
