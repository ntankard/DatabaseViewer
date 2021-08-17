package com.ntankard.databaseViewer.processor;

import com.ntankard.databaseViewer.dataBase.File;
import com.ntankard.databaseViewer.dataBase.Row;
import com.ntankard.databaseViewer.dataBase.SaveInstance;
import com.ntankard.databaseViewer.dataBase.type.Type;
import com.ntankard.databaseViewer.dataBase.type.dataObjectType.FileDataObjectType;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.util.FileUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ntankard.javaObjectDatabase.database.io.Database_IO_Util.INSTANCE_CLASSES_PATH;

public class SaveInstanceLoader {

    /**
     * Load a full saved instance of the database
     *
     * @param database         The place to store the data
     * @param basePath         The path to the data
     * @param sectionPath      The path to the instance
     * @param folderPath       The path to the folder
     * @param pastSaveInstance The previous saved instance
     * @param masterMap        A map of all loaded rows with there IDs
     * @param fileMap          A map of all loaded rows with there IDs per file
     * @return The loaded instance
     */
    public static SaveInstance loadSaveInstance(Database database, String basePath, String sectionPath, String folderPath, SaveInstance pastSaveInstance, Map<SaveInstance, Map<Integer, Row>> masterMap, Map<SaveInstance, Map<Type, Map<Integer, Row>>> fileMap) {
        // Create the SaveInstance
        String qualifiedPath = basePath + sectionPath + folderPath;
        SaveInstance saveInstance = new SaveInstance(database, Integer.parseInt(folderPath), qualifiedPath, pastSaveInstance).add();
        masterMap.put(saveInstance, new HashMap<>());
        fileMap.put(saveInstance, new HashMap<>());

        // Create each File and its Type as well as preload the data
        List<String> files = FileUtil.findFilesInDirectory(qualifiedPath + INSTANCE_CLASSES_PATH);
        Map<File, List<String[]>> fileLines = new HashMap<>();
        for (String filePath : files) {
            List<String[]> allLines = FileUtil.readLines(qualifiedPath + INSTANCE_CLASSES_PATH + filePath);
            Type type = Type.getOrCreateType(allLines.get(0)[0], saveInstance, true);
            File file = new File(saveInstance, (FileDataObjectType) type).add();
            fileLines.put(file, allLines);
        }

        // Process each File
        for (Map.Entry<File, List<String[]>> fileData : fileLines.entrySet()) {
            FileLoader.loadFile(fileData.getKey(), fileData.getValue(), masterMap, fileMap);
        }

        // Map to the past save instance
        if (pastSaveInstance != null) {
            SaveInstanceMapper.mapToPast(saveInstance, pastSaveInstance, fileMap);
        }

        return saveInstance;
    }
}
