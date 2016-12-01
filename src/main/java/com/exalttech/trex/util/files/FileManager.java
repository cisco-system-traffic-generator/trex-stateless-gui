/**
 * *****************************************************************************
 * Copyright (c) 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************
 */
package com.exalttech.trex.util.files;

import com.exalttech.trex.util.PreferencesManager;
import com.exalttech.trex.util.Util;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.io.FileUtils;

/**
 * File manager utility class
 *
 * @author GeorgeKh
 */
public class FileManager {
    
    private static final String APP_DATA_PATH = File.separator + "TRex" + File.separator + "trex" + File.separator;
    private static final String PROFILES_PATH = "traffic-profiles" + File.separator;
    private static final String TEMPLATES_PATH = "templates" + File.separator;

    /**
     * Return local file path
     *
     * @return
     */
    public static String getLocalFilePath() {
        String path = System.getProperty( "user.home" );
        if (Util.isWindows()) {
            if (!Util.isNullOrEmpty(System.getenv("LOCALAPPDATA"))) {
                path = System.getenv("LOCALAPPDATA") ;
            }
        }
        return path + APP_DATA_PATH;
    }

    /**
     * Return The profiles file path
     *
     * @return
     */
    public static String getProfilesFilePath() {
        createDirectoryIfNotExists(getLocalFilePath() + PROFILES_PATH);
        return getLocalFilePath() + PROFILES_PATH;
    }

    /**
     * Return The templates files path
     *
     * @return
     */
    public static String getTemplatesFilePath() {
        createDirectoryIfNotExists(getLocalFilePath() + TEMPLATES_PATH);
        return getLocalFilePath() + TEMPLATES_PATH;
    }

    public static String getDefaultTemplatesFilePath() {
        return getLocalFilePath() + TEMPLATES_PATH;
    }

    /**
     * Copy the select file to the local directory
     *
     * @param srcFile
     * @return
     * @throws IOException
     */
    public static File copyFile(File srcFile) throws IOException {
        File destFile;
        String srcFileDirectory = "";
        if (srcFile.getParentFile() != null) {
            srcFileDirectory = srcFile.getParentFile().getName();
        }
        destFile = new File(createDirectoryIfNotExists(getLocalFilePath() + srcFileDirectory) + File.separator + srcFile.getName());
        FileUtils.copyFile(srcFile, destFile);
        return destFile;
    }

    /**
     * Create directory if not exists
     *
     * @param directoryPath
     * @return
     */
    public static String createDirectoryIfNotExists(String directoryPath) {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return directoryPath;
    }

    /**
     *
     * @param path
     * @return
     */
    public static boolean isExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * Delete file from local directory
     *
     * @param fileToDelete
     */
    public static void deleteFile(File fileToDelete) {
        if (fileToDelete != null) {
            FileUtils.deleteQuietly(fileToDelete);
        }
    }

    /**
     * Create new file
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File createNewFile(String fileName) throws IOException {
        String path = getProfilesFilePath() + fileName;
        File newFile = new File(path);
        newFile.createNewFile();
        return newFile;
    }

    /**
     * Write String to file
     *
     * @param data
     * @param file
     * @throws IOException
     */
    public static void writeStringToFile(String data, File file) throws IOException {
        FileUtils.writeStringToFile(file, data);
    }

    /**
     * Export file
     *
     * @param windowTitle
     * @param fileName
     * @param fileContent
     * @param window
     * @param type
     * @throws IOException
     */
    public static void exportFile(String windowTitle, String fileName, Object fileContent, Window window, FileType type) throws IOException {
        File savedFile = getSelectedFile(windowTitle, fileName, window, type, PreferencesManager.getInstance().getSaveLocation(), true);
        if (savedFile != null) {
            String filePath = savedFile.getAbsolutePath();
            String pathToSave = !filePath.contains(type.getExtension()) ? filePath + type.getExtension() : filePath;
            File fileToSave = new File(pathToSave);
            if (!fileToSave.exists()) {
                fileToSave.createNewFile();
            }
            if (fileContent instanceof String) {
                FileUtils.writeStringToFile(fileToSave, String.valueOf(fileContent));
            } else {
                FileUtils.copyFile((File) fileContent, savedFile);
            }
        }
    }

    /**
     * Return selected file
     *
     * @param windowTitle
     * @param fileName
     * @param window
     * @param type
     * @param filePath
     * @param isExport
     * @return
     */
    public static File getSelectedFile(String windowTitle, String fileName, Window window, FileType type, String filePath, boolean isExport) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(windowTitle);

        fileChooser.setInitialFileName(fileName);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(type.getFilterDescription(), type.getFilterExtension());
        fileChooser.getExtensionFilters().add(extFilter);
        FileChooser.ExtensionFilter allFilesFilter = new FileChooser.ExtensionFilter("All files ", "*.*");
        fileChooser.getExtensionFilters().add(allFilesFilter);
        
        if (!Util.isNullOrEmpty(filePath) && new File(filePath).exists()) {
            fileChooser.setInitialDirectory(new File(filePath));
        }
        if (isExport) {
            return fileChooser.showSaveDialog(window);
        } else {
            return fileChooser.showOpenDialog(window);
        }
    }

    public static String getFileContent(File fileToRead) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(fileToRead.getPath()));
        return new String(encoded);
    }

    /**
     *
     */
    private FileManager() {
        // private constructor
    }

}
