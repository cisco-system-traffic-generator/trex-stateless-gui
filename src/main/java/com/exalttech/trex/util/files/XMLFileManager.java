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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;

/**
 * Load/Save data as XML utility class
 *
 * @author GeorgeKh
 */
public class XMLFileManager {

    private static final Logger LOG = Logger.getLogger(XMLFileManager.class.getName());

    /**
     * Load XML file and return it as object
     *
     * @param fileName
     * @param classType
     * @return
     */
    public static Object loadXML(String fileName, Class classType) {
        try {
            Path path = Paths.get(FileManager.getLocalFilePath() + fileName);
            if (Files.exists(path)) {
                JAXBContext context = JAXBContext.newInstance(classType);
                Unmarshaller um = context.createUnmarshaller();
                return um.unmarshal(new File(path.toUri()));
            }
        } catch (JAXBException ex) {
            LOG.error("Error loading file " + fileName, ex);
        }
        return null;
    }

    /**
     * Save data object as XML file
     *
     * @param fileName
     * @param dataToSave
     * @param classType
     */
    public static void saveXML(String fileName, Object dataToSave, Class classType) {

        try {
            File file = new File(FileManager.createDirectoryIfNotExists(FileManager.getLocalFilePath()) + fileName);
            JAXBContext context = JAXBContext.newInstance(classType);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(dataToSave, file);
        } catch (JAXBException ex) {
            LOG.error("Error saving file " + fileName, ex);
        }

    }

    /**
     * Private constructor
     */
    private XMLFileManager() {
        // private constructor
    }

}
