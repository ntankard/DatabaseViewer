package com.ntankard.databaseViewer.processor;

import com.ntankard.databaseViewer.dataBase.Row;
import com.ntankard.databaseViewer.dataBase.SaveInstance;
import com.ntankard.databaseViewer.dataBase.type.Type;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.database.Database_Schema;
import com.ntankard.javaObjectDatabase.database.io.Database_IO_Reader;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ntankard.javaObjectDatabase.database.io.Database_IO_Util.ROOT_DATA_PATH;
import static com.ntankard.javaObjectDatabase.util.FileUtil.findFoldersInDirectory;

public class DatabaseLoader {

    /**
     * Load all the stored data into a new database instance
     *
     * @param databasePath The path to the DataObjects to use
     * @param savePath     The path of the database to inspect
     * @return A new database
     */
    public static Database loadDatabase(String databasePath, String savePath) {

        Map<SaveInstance, Map<Integer, Row>> masterMap = new HashMap<>();
        Map<SaveInstance, Map<Type, Map<Integer, Row>>> fileMap = new HashMap<>();

        // Create the core database
        Database_Schema databaseSchema = Database_Schema.getSchemaFromPackage(databasePath);
        Database_IO_Reader reader = new Database_IO_Reader();
        Database database = new Database(databaseSchema, reader);
        database.setIDFloor(0);

        // Find all the save instances
        String corePath = savePath + ROOT_DATA_PATH;
        List<String> folders = findFoldersInDirectory(corePath);
        folders.sort(Comparator.comparingInt(Integer::parseInt));
        
        // Load each from oldest to latest
        SaveInstance pastSaveInstance = null;
        int i = 0;
        for (String s : folders) {
            if(i++ < folders.size() - 10){
                continue;
            }
            if (!s.equals("0")) {
                pastSaveInstance = SaveInstanceLoader.loadSaveInstance(database, savePath, ROOT_DATA_PATH + "\\", s, pastSaveInstance, masterMap, fileMap);
            }
        }

        return database;
    }
}
