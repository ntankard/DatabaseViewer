package com.ntankard.databaseViewer;

import com.ntankard.databaseViewer.display.frames.mainFrame.MasterFrame;
import com.ntankard.javaObjectDatabase.database.Database;

import static com.ntankard.databaseViewer.processor.DatabaseLoader.loadDatabase;

public class Main {

    public static String databasePath = "com.ntankard.databaseViewer.dataBase";
    public static String savePath = "C:\\Users\\Nicholas\\Google Drive\\BudgetTrackingData";

    public static void main(String[] args) {
        Database database = loadDatabase(databasePath, savePath);
        MasterFrame.open(database);
    }
}
