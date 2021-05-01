package com.ntankard.databaseViewer.processor;

import com.ntankard.databaseViewer.dataBase.*;
import com.ntankard.databaseViewer.dataBase.cell.Cell;
import com.ntankard.databaseViewer.dataBase.type.PrimitiveType;
import com.ntankard.databaseViewer.dataBase.type.Type;
import com.ntankard.databaseViewer.dataBase.type.dataObjectType.FieldDataObjectType;
import com.ntankard.javaObjectDatabase.util.set.OneParent_Children_Set;
import com.ntankard.javaObjectDatabase.util.set.TwoParent_Children_Set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveInstanceMapper {

    /**
     * Map 2 save instances together and find out what data is related
     *
     * @param currentSaveInstance The current instance
     * @param pastSaveInstance    The past instance
     * @param fileMap             A map of all loaded rows with there IDs per file
     */
    public static void mapToPast(SaveInstance currentSaveInstance, SaveInstance pastSaveInstance, Map<SaveInstance, Map<Type, Map<Integer, Row>>> fileMap) {
        mapPrimitiveTypes(currentSaveInstance, pastSaveInstance);
        mapFileTypes(currentSaveInstance, pastSaveInstance);
        matchRows(currentSaveInstance, fileMap);
    }

    /**
     * Map the primitive types
     *
     * @param currentSaveInstance The current instance
     * @param pastSaveInstance    The past instance
     */
    public static void mapPrimitiveTypes(SaveInstance currentSaveInstance, SaveInstance pastSaveInstance) {
        Map<String, PrimitiveType> pastPrimitiveMap = new HashMap<>();
        for (PrimitiveType pastPrimitiveType : new OneParent_Children_Set<>(PrimitiveType.class, pastSaveInstance).get()) {
            pastPrimitiveMap.put(pastPrimitiveType.getFullName(), pastPrimitiveType);
        }

        for (PrimitiveType currentPrimitiveType : new OneParent_Children_Set<>(PrimitiveType.class, currentSaveInstance).get()) {
            if (pastPrimitiveMap.containsKey(currentPrimitiveType.getFullName())) {
                PrimitiveType past = pastPrimitiveMap.get(currentPrimitiveType.getFullName());
                currentPrimitiveType.setMatchType(past);
            }
        }
    }

    /**
     * Map the files to each other
     *
     * @param currentSaveInstance The current instance
     * @param pastSaveInstance    The past instance
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public static void mapFileTypes(SaveInstance currentSaveInstance, SaveInstance pastSaveInstance) {

        List<File> currentFile = new OneParent_Children_Set<>(File.class, currentSaveInstance).get();
        List<File> pastFile = new OneParent_Children_Set<>(File.class, pastSaveInstance).get();
        Map<String, File> pastDataObjectMap = new HashMap<>();
        Map<String, Map<String, Column>> pastDataObjectFieldMap = new HashMap<>();
        for (File dataObjectType : pastFile) {
            pastDataObjectMap.put(dataObjectType.getType().getFullName(), dataObjectType);
            Map<String, Column> fieldMap = new HashMap<>();
            for (Column savedField : new OneParent_Children_Set<>(Column.class, dataObjectType).get()) {
                fieldMap.put(savedField.getName(), savedField);
            }
            pastDataObjectFieldMap.put(dataObjectType.getType().getFullName(), fieldMap);
        }

        while (mapIdenticalFiles(currentFile, pastFile, pastDataObjectMap, pastDataObjectFieldMap) != 0) {
        }
    }

    /**
     * Map any files that are a perfect match (ignoring fields types)
     *
     * @param currentFiles   The current files to map
     * @param pastFiles      The past files to map too
     * @param pastFileNames  The pastFiles mapped to there name
     * @param pastFileFields The pastFileFields mapped to the files name
     * @return The number of Files that were matched
     */
    public static int mapIdenticalFiles(List<File> currentFiles, List<File> pastFiles, Map<String, File> pastFileNames, Map<String, Map<String, Column>> pastFileFields) {
        int matched = 0;
        for (File currentFile : new ArrayList<>(currentFiles)) {

            // Check the file name
            if (!pastFileNames.containsKey(currentFile.getType().getFullName())) {
                continue;
            }

            // Check the fields
            List<Column> currentFields = new OneParent_Children_Set<>(Column.class, currentFile).get();
            Map<String, Column> passFields = pastFileFields.get(currentFile.getType().getFullName());
            if (passFields.size() != currentFields.size()) {
                continue;
            }

            // Check each individual field
            boolean allMatch = true;
            for (Column currentField : currentFields) {

                // Do they have a field with the same name?
                if (!passFields.containsKey(currentField.getName())) {
                    allMatch = false;
                    break;
                }
                Column pastField = passFields.get(currentField.getName());

                // Are they of the same type?
                if (!FieldDataObjectType.class.isAssignableFrom(pastField.getType().getClass()) || !FieldDataObjectType.class.isAssignableFrom(currentField.getType().getClass())) {
                    if (!currentField.getType().areRelated(pastField.getType())) {
                        allMatch = false;
                        break;
                    }
                }
            }

            if (!allMatch) {
                continue;
            }

            // Match the Files and Type
            File pastFile = pastFileNames.get(currentFile.getType().getFullName());
            currentFile.getType().setMatchType(pastFile.getType());
            currentFile.setMatchFile(pastFile);

            for (Column currentField : currentFields) {
                Column pastField = passFields.get(currentField.getName());
                if (!currentField.getType().areRelated(pastField.getType())) {
                    if (currentField.getType().getFullName().equals(pastField.getType().getFullName())) {
                        currentField.getType().setMatchType(pastField.getType());
                    } else {
                        currentField.getType().setSimilarType(pastField.getType());
                    }
                }

                if (pastField.getType().equals(currentField.getType().getMatchType())) {
                    currentField.setMatchColumn(pastField);
                } else if (pastField.getType().equals(currentField.getType().getSimilarType())) {
                    currentField.setSimilarColumn(pastField);
                } else {
                    throw new RuntimeException();
                }

            }

            // Remove the matched files
            currentFiles.remove(currentFile);
            pastFiles.remove(pastFile);
            pastFileNames.remove(currentFile.getType().getFullName());
            pastFileFields.remove(currentFile.getType().getFullName());
            matched++;
        }

        return matched;
    }

    /**
     * Match all the rows
     *
     * @param currentSaveInstance The current instance
     * @param fileMap             A map of all loaded rows with there IDs per file
     */
    public static void matchRows(SaveInstance currentSaveInstance, Map<SaveInstance, Map<Type, Map<Integer, Row>>> fileMap) {
        for (File currentFile : new OneParent_Children_Set<>(File.class, currentSaveInstance).get()) {
            File pastFile;
            if (currentFile.getMatchFile() != null) {
                pastFile = currentFile.getMatchFile();
            } else if (currentFile.getSimilarFile() != null) {
                pastFile = currentFile.getSimilarFile();
            } else {
                continue;
            }

            List<Row> currentRows = new OneParent_Children_Set<>(Row.class, currentFile).get();
            List<Row> pastRows = new OneParent_Children_Set<>(Row.class, pastFile).get();
            List<Row> matchedRows = new ArrayList<>();

            List<Row> currentRowsLoop = new ArrayList<>(currentRows);
            for (Row currentRow : currentRowsLoop) {
                Row pastRow = fileMap.get(pastFile.getSaveInstance()).get(pastFile.getType()).get(currentRow.getRowID());
                if (pastRow != null) {
                    matchedRows.add(currentRow);
                    currentRow.setMatchRow(pastRow);
                    currentRows.remove(currentRow);
                    pastRows.remove(pastRow);

                    for (Column column : new OneParent_Children_Set<>(Column.class, currentRow.getFile()).get()) {
                        List<Cell> cells = new TwoParent_Children_Set<>(Cell.class, currentRow, column).get();
                        if (cells.size() != 1) {
                            throw new RuntimeException();
                        }
                        Cell currentCell = cells.get(0);

                        Column pastColumn;
                        if (column.getMatchColumn() != null) {
                            pastColumn = column.getMatchColumn();
                        } else if (column.getSimilarColumn() != null) {
                            pastColumn = column.getSimilarColumn();
                        } else {
                            continue;
                        }

                        if (currentRow.getMatchRow() == null) {
                            throw new RuntimeException();
                        }
                        List<Cell> pastCells = new TwoParent_Children_Set<>(Cell.class, currentRow.getMatchRow(), pastColumn).get();
                        if (pastCells.size() != 1) {
                            throw new RuntimeException();
                        }
                        Cell pastCell = pastCells.get(0);

                        if (currentCell.getValue().equals(pastCell.getValue())) {
                            currentCell.setMatchCell(pastCell);
                        } else {
                            currentCell.setSimilarCell(pastCell);
                        }
                    }
                }
            }

            for (Row currentRow : currentRows) {
                new DeltaRow(currentSaveInstance, currentFile, currentRow, null).add();
            }
            for (Row pastRow : pastRows) {
                new DeltaRow(currentSaveInstance, currentFile, null, pastRow).add();
            }
            for (Row matchedRow : matchedRows) {
                new DeltaRow(currentSaveInstance, currentFile, matchedRow, matchedRow.getMatchRow()).add();
            }
        }
    }
}
