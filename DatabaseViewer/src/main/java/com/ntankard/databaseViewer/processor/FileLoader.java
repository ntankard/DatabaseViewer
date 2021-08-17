package com.ntankard.databaseViewer.processor;

import com.ntankard.databaseViewer.dataBase.Column;
import com.ntankard.databaseViewer.dataBase.File;
import com.ntankard.databaseViewer.dataBase.Row;
import com.ntankard.databaseViewer.dataBase.SaveInstance;
import com.ntankard.databaseViewer.dataBase.cell.Cell;
import com.ntankard.databaseViewer.dataBase.type.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileLoader {

    /**
     * Load all data from a File
     *
     * @param file      The file to load into
     * @param allLines  The lines of the file to load
     * @param masterMap A map of all loaded rows with there IDs
     * @param fileMap   A map of all loaded rows with there IDs per file
     */
    public static void loadFile(File file, List<String[]> allLines, Map<SaveInstance, Map<Integer, Row>> masterMap, Map<SaveInstance, Map<Type, Map<Integer, Row>>> fileMap) {

        // Load the Columns
        String[] paramList = allLines.get(1);
        List<Column> columns = new ArrayList<>();
        fileMap.get(file.getSaveInstance()).put(file.getType(), new HashMap<>());
        for (int i = 0; i < paramList.length / 2; i++) {
            Type type = Type.getOrCreateType(paramList[i * 2 + 1], file.getSaveInstance(), false);
            Column column = new Column(file, paramList[i * 2], type).add();
            if (column.getName().equals("getId")) {
                if (file.getIDColumn() != null) {
                    throw new RuntimeException();
                }
                file.setIDColumn(column);
            } else if (column.getName().equals("getName")) {
                if (file.getNameColumn() != null) {
                    throw new RuntimeException();
                }
                file.setNameColumn(column);
            }
            columns.add(column);
        }
        if (file.getIDColumn() == null) {
            throw new RuntimeException();
        }

        // Load the Rows and cells
        for (int i = 2; i < allLines.size(); i++) {
            String[] savedLineString = allLines.get(i);
            assert (savedLineString.length == columns.size());

            Row row = new Row(file, i - 2).add();
            for (int j = 0; j < columns.size(); j++) {
                Cell.createCell(row, columns.get(j), savedLineString[j]);
                if (columns.get(j).equals(file.getIDColumn())) {
                    row.setRowID(Integer.decode(savedLineString[j]));
                }
            }
            if (row.getLineNum() == null) {
                throw new RuntimeException();
            }
            masterMap.get(file.getSaveInstance()).put(row.getRowID(), row);
            fileMap.get(file.getSaveInstance()).get(file.getType()).put(row.getRowID(), row);
        }
    }
}
