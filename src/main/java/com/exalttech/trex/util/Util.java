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
package com.exalttech.trex.util;

import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.remote.models.params.Params;
import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.ui.MultiplierType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class that contains general method and functionality
 *
 * @author GeorgeKh
 */
public class Util {

    private static final String IP_REG_EXP = "^((?:(?:^|\\.)(?:\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){4})$";
    private static final String HOST_NAME_EXP = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";
    private final static String IP_ADDRESS_REG = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
    private static final String DIGITS_REG_EXP = "[0-9]+";
    private static final String DECIMAL_REG_EXP = "[0-9]*+(\\.[0-9][0-9]?)?";
    private static final String HEX_REG_EXP = "[0-9a-fA-F]+";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final Logger LOG = Logger.getLogger(Util.class.getName());
    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("0.0");
    private static final DecimalFormat FRACTION_FORMATTER = new DecimalFormat("#0.############");
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final int UNIT_VALUE = 1000;
    private static final int SHORT_LENGTH = 16;
    private static final int ALERT_X_POSITION = 300;
    private static final int ALERT_Y_POSITION = 150;
    private static final String APPLICATION_EXECUTABLE = "trex-stateless-gui.exe";
    private static final String VERSION_PROPERTIES_FILE = "version.properties";
    private static final String VERSION_KEY = "version";
    private static final int numOfCharacterLimit = 10000;

    /**
     * Convert JSON string to object
     *
     * @param jsonString
     * @param domainClass
     * @return
     */
    public static Object fromJSONString(final String jsonString, Class<?> domainClass) {
        try {
            return new ObjectMapper().readValue(jsonString, domainClass);
        } catch (IOException ex) {
            LOG.error("Error parsing string", ex);
            return null;
        }
    }

    /**
     * Fill string map value with it value from JSON String
     *
     * @param jsonString
     * @param resutlSet
     * @return
     */
    public static Map<String, String> fromJSONResultSet(final String jsonString, Map<String, String> resutlSet) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            for (String fieldName : resutlSet.keySet()) {
                resutlSet.put(fieldName, jsonObject.getString(fieldName));
            }
        } catch (JSONException ex) {
            LOG.error("Error parsing string", ex);
            return null;
        }
        return resutlSet;
    }

    /**
     * Prepare and return list of Stats object from JSON string
     *
     * @param jsonString
     * @return
     */
    public static Map<String, String> getStatsFromJSONString(String jsonString) {

        Map<String, String> statsList = new HashMap<>();
        JSONObject jsonObj = new JSONObject(jsonString);
        for (String key : jsonObj.keySet()) {
            statsList.put(key, String.valueOf(jsonObj.get(key)));
        }
        return statsList;
    }

    /**
     * Return part of JSON String
     *
     * @param jsonString
     * @param tageName
     * @return
     */
    public static String fromJSONResult(final String jsonString, String tageName) {
        String result = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (!"null".equals(jsonObject.get(tageName).toString())) {
                result = ((JSONObject) jsonObject.get(tageName)).toString();
            }
        } catch (JSONException ex) {
            LOG.error("Error parsing async response " + jsonString, ex);
            return null;
        }
        return result;
    }

    /**
     * Validate IP address and host-name
     *
     * @param ip
     * @return
     */
    public static boolean isValidAddress(String ip) {
        return !(isNullOrEmpty(ip) || (!isValidIPAddress(ip) && !ip.matches(HOST_NAME_EXP)));
    }

    public static boolean isValidIPAddress(String ip) {
        return !isNullOrEmpty(ip) && ip.matches(IP_REG_EXP);
    }

    /**
     * Validate digits input
     *
     * @param digits
     * @return
     */
    public static boolean isValidPort(String digits) {
        int port;
        try {
            port = Integer.parseInt(digits);

        } catch (NumberFormatException e) {
            return false;
        }

        return !(isNullOrEmpty(digits) || !digits.matches(DIGITS_REG_EXP) || port < 0 || port > 65536);
    }

    /**
     * Return port value from string
     *
     * @param port
     * @return
     */
    public static int getPortValue(String port) {
        if (isValidPort(port)) {
            return Integer.parseInt(port);
        }
        return 0;
    }

    /**
     * Validate if the input is empty or null
     *
     * @param data
     * @return
     */
    public static boolean isNullOrEmpty(String data) {
        return data == null || "".equals(data) || data.isEmpty() || "null".equals(data);
    }

    /**
     * Generate and return alert message window
     *
     * @param type
     * @return
     */
    public static Alert getAlert(Alert.AlertType type) {
        Alert alert = new Alert(type, "", ButtonType.OK);
        alert.setHeaderText(null);
        alert.setX(TrexApp.getPrimaryStage().getX() + ALERT_X_POSITION);
        alert.setY(TrexApp.getPrimaryStage().getY() + ALERT_Y_POSITION);
        return alert;
    }

    /**
     * Confirm deletion message window
     *
     * @param deleteMsg
     * @return
     */
    public static boolean isConfirmed(String deleteMsg) {
        Alert confirmMsgBox = Util.getAlert(Alert.AlertType.CONFIRMATION);
        confirmMsgBox.getButtonTypes().clear();
        confirmMsgBox.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        confirmMsgBox.setContentText(deleteMsg);
        Optional<ButtonType> result = confirmMsgBox.showAndWait();
        return result.get() == ButtonType.YES;
    }

    /**
     * Convert JSON to pretty format
     *
     * @param jsonString
     * @return
     */
    public static String toPrettyFormat(String jsonString) {
        try {

            JsonParser parser = new JsonParser();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // Check if it is an Array
            if ('[' == jsonString.charAt(0)) {
                JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();
                return getSubString(gson.toJson(jsonArray));
            } else {
                JsonObject json = parser.parse(jsonString).getAsJsonObject();
                return  getSubString(gson.toJson(json));
            }
        } catch (JsonSyntaxException ex) {
            // return the original string in case of exception
            LOG.error("Error formatting string", ex);
            return  getSubString(jsonString);
        }
    }

    /**
     * 
     * @param data
     * @return 
     */
    private static String getSubString(String data) {
        if (data.length() > numOfCharacterLimit) {
            return "... "+data.substring(data.length()-numOfCharacterLimit);
        }
        return data;
    }

    /**
     * Return formatted value
     *
     * @param data
     * @param format
     * @param suffix
     * @return
     */
    public static String getFormatted(String data, boolean format, String suffix) {
        try {
            if (!isNullOrEmpty(data)) {
                long value = (long) Double.parseDouble(data);
                return formattedData(value, format) + suffix;
            }
            return "0" + suffix;
        } catch (NumberFormatException ex) {
            LOG.error("Error formatting string", ex);
            return "0" + suffix;
        }
    }

    /**
     *
     * @param data
     * @return
     */
    public static String getEmptyValue(String data) {
        if (Util.isNullOrEmpty(data)) {
            return "0";
        }
        return data;
    }

    /**
     * Format data
     *
     * @param data
     * @param format
     * @return
     */
    public static String formattedData(long data, boolean format) {
        try {

            if (!format) {
                return String.valueOf(data);
            }
            if (data < UNIT_VALUE) {
                return String.valueOf(data) + " ";
            }
            int exp = (int) (Math.log(data) / Math.log(UNIT_VALUE));
            String pre = ("KMGTPE").charAt(exp - 1) + "";
            return String.format(Locale.ROOT, "%3.2f %s", data / Math.pow(UNIT_VALUE, exp), pre);
        } catch (Exception ex) {
            LOG.error("Error formatting string", ex);
            return "0";
        }
    }

    /**
     * Formate double to decimal format
     *
     * @param numToFormat
     * @return
     */
    public static String formatDecimal(double numToFormat) {
        return new BigDecimal(numToFormat)
                .setScale(DECIMAL_FORMATTER.getMaximumFractionDigits(), RoundingMode.HALF_EVEN)
                    .toPlainString();
    }

    /**
     * Validate if the entered value is digit
     *
     * @param value
     * @return
     */
    public static boolean isDigit(String value) {
        return DIGITS_REG_EXP.matches(value);
    }

    /**
     * Return if the value is float digit
     * <p>
     * true if it is float
     * <p>
     * otherwise return false
     *
     * @param value
     * @return
     */
    public static boolean isDecimal(String value) {
        return value.matches(DECIMAL_REG_EXP);
    }

    /**
     * Return if the value is hex
     * <p>
     * true if it is float
     * <p>
     * otherwise return false
     *
     * @param value
     * @return
     */
    public static boolean isHex(String value) {
        return value.matches(HEX_REG_EXP);
    }

    /**
     * Format fraction number to equivalent format
     *
     * @param num
     * @return
     */
    public static String getFormatedFraction(double num) {

        if (num == 0 || num >= 1) {
            return String.valueOf(num);
        }
        int index = -1;
        double value = num;
        while (value < 1) {
            index++;
            value = value * UNIT_VALUE;
        }
        String unit = "munpfaz";
        return formatDecimal(value) + " " + unit.charAt(index);
    }

    /**
     * Convert and return the fraction value
     *
     * @param value
     * @param unit
     * @return
     */
    public static String convertSmallUnitToValue(String value, String unit) {
        double num = Double.parseDouble(value);
        String unitVal = "mμnpfaz";
        double data = num / (Math.pow(UNIT_VALUE, unitVal.indexOf(unit) + 1));

        return new BigDecimal(data)
                .setScale(FRACTION_FORMATTER.getMaximumFractionDigits(), RoundingMode.HALF_EVEN)
                    .toPlainString();
    }

    /**
     * convert large unit value to equivalent value
     *
     * @param value
     * @param unit
     * @return
     */
    public static double convertLargeUnitToValue(String value, String unit) {
        try {
            String units = "KMG";
            int unitIndex = units.indexOf(unit.toUpperCase());
            return Double.parseDouble(value) * Math.pow(UNIT_VALUE, unitIndex + 1);
        } catch (NumberFormatException ex) {
            LOG.error("Error converting unit to number", ex);
            return 0;
        }
    }

    /**
     * Check whether the OS is windows
     *
     * @return
     */
    public static boolean isWindows() {
        return OS.contains("win");
    }

    /**
     * Check whether the OS is UNIX
     *
     * @return
     */
    public static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    /**
     * Return application path
     *
     * @return
     */
    public static String getApplicationPath() {
        String applicationPath = "";
        if (isWindows()) {
            applicationPath = getAbsoluteApplicationPath() + File.separator + ".." + File.separator + APPLICATION_EXECUTABLE;
        }
        return applicationPath;
    }

    /**
     * Return absolute application path
     *
     * @return
     */
    public static String getAbsoluteApplicationPath() {
        Path currentRelativePath = Paths.get("");
        String applicationPath = currentRelativePath.toAbsolutePath().toString();
        LOG.info("Current relative path is: " + applicationPath);
        return applicationPath;
    }

    /**
     * Remove first bracket
     *
     * @param myString
     * @return
     */
    public static String removeFirstBrackets(String myString) {
        if (myString.startsWith("[")) {
            String removeStartString = myString.trim().replaceFirst("\\[", "");
            int ind = removeStartString.lastIndexOf("]");
            return new StringBuilder(removeStartString).replace(ind, ind + 1, "").toString();
        }
        return myString;
    }

    /**
     * Generate and return random value
     *
     * @param length
     * @return
     */
    public static String getRandomID(int length) {
        return RandomStringUtils.randomAlphanumeric(length).toUpperCase();
    }

    /**
     * Generate and return random numeric value
     *
     * @param length
     * @return
     */
    public static String getRandomNumericID(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    /**
     *
     * @return
     */
    public static int getRandomID() {
        return 123456789;
    }

    /**
     * This method tunes the JSON Params. This method was added as the original
     * requirement was to use the VM and RX_Stat as is but it is not the case.
     *
     * @param jsonString
     * @param params
     * @param apiH
     * @return
     */
    public static String tuneJSONParams(String jsonString, Params params, String apiH) {
        String tunedString = jsonString;
        if (tunedString.contains("\"params\":{")) {
            tunedString = tunedString.replace("\"params\":{", "\"params\":{ \"api_h\": \"" + apiH + "\",");
        }

        if (params instanceof Profile) {

            String vm = "\"vm\": " + "{\n"
                    + "                    \"instructions\": [],\n"
                    + "                    \"split_by_var\": \"\"\n"
                    + "                }";
            String rxStats = "\"rx_stats\": " + "{\n"
                    + "                    \"enabled\": false\n"
                    + "                }";
            if (!tunedString.contains("\"vm\"")) {
                tunedString = tunedString.replace("\"self_start\"", vm + ",\r" + "\"self_start\"");
            }
            if (!tunedString.contains("\"rx_stats\"")) {
                tunedString = tunedString.replace("\"self_start\"", rxStats + ",\r" + "\"self_start\"");
            }
            if (tunedString.contains("\"vm\":[]")) {
                tunedString = tunedString.replace("\"vm\":[]", vm);
            }
            if (tunedString.contains("\"rx_stats\":[]")) {
                tunedString = tunedString.replace("\"rx_stats\":[]", rxStats);
            }

            // Add Split by vars in the VM if missing.
            if (!tunedString.contains("split_by_var")) {
                tunedString = tunedString.replace("\"vm\":{", "\"vm\":{\n"
                        + "                    \"split_by_var\": \"\",\n");
            }

            // Remove quotes from uInt32
            String pattern = "\"0x[^\"]*\"";
            String originalString = tunedString;
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(originalString);
            while (m.find()) {
                String hexString = m.group(0).replace("\"", "").replace("0x", "");
                int hexValue = (int) Integer.valueOf(hexString, SHORT_LENGTH);
                tunedString = tunedString.replace(m.group(0), "" + hexValue);
            }

            return tunedString;

        }
        return tunedString;
    }

    /**
     * Return textChange formatter
     *
     * @param regex
     * @return
     */
    public static UnaryOperator<TextFormatter.Change> getTextChangeFormatter(String regex) {
        return c -> {
            String text = c.getControlNewText();
            if (text.matches(regex)) {
                return c;
            } else {
                return null;
            }
        };
    }

    /**
     * Return Hex regex
     *
     * @param numOfChar
     * @return
     */
    public static String hexRegex(int numOfChar) {
        String partialBlock = "(([0-9a-fA-F])*)";
        if (numOfChar > 0) {
            partialBlock = "(([0-9a-fA-F]{0," + numOfChar + "}))";
        }
        return "^" + partialBlock;
    }

    /**
     * Return hex filter
     *
     * @param numOfChar
     * @return
     */
    public static TextFormatter getHexFilter(int numOfChar) {
        UnaryOperator<TextFormatter.Change> filter = Util.getTextChangeFormatter(hexRegex(numOfChar));
        return new TextFormatter<>(filter);
    }

    /**
     * Return numbers regex
     *
     * @param numOfChar
     * @return
     */
    public static String numberRegex(int numOfChar) {
        String partialBlock = "(([0-9]{0," + numOfChar + "}))";
        return "^" + partialBlock;
    }

    /**
     * Return hex filter
     *
     * @param numOfChar
     * @return
     */
    public static TextFormatter getNumberFilter(int numOfChar) {
        UnaryOperator<TextFormatter.Change> filter = Util.getTextChangeFormatter(numberRegex(numOfChar));
        return new TextFormatter<>(filter);
    }

    /**
     * Clone and return Map
     *
     * @param mapToClone
     * @return
     */
    public static Map<String, Object> getClonedMap(Map<String, Object> mapToClone) {
        Map<String, Object> clonedMap = new HashMap<>();
        clonedMap.putAll(mapToClone);
        return clonedMap;
    }

    /**
     *
     * @param number
     * @return
     */
    public static int getIntFromString(String number) {
        int value = 0;
        try {
            value = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            LOG.warn("Value not integer", e);
            return 0;
        }
        return value;
    }

    /**
     * Return short value from string
     *
     * @param value
     * @param isSixteen
     * @return
     */
    public static short getShortFromString(String value, boolean isSixteen) {
        try {
            if (isSixteen) {
                return (short) Integer.parseInt(value, SHORT_LENGTH);
            }
            return (short) Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            LOG.warn("Value not short", ex);
            return (short) Integer.parseInt("0");
        }
    }

    /**
     * Formate date
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * Serialize object to string
     *
     * @param objectToSer
     * @return
     * @throws IOException
     */
    public static String serializeObjectToString(Serializable objectToSer) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(objectToSer);
            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException ex) {
            LOG.error("Error serializing object", ex);
            return "";
        }
    }

    /**
     * De-serialize string to object
     *
     * @param serializedStriing
     * @return
     */
    public static Object deserializeStringToObject(String serializedStriing) {
        try {
            byte[] data = Base64.getDecoder().decode(serializedStriing);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return o;
        } catch (IOException | ClassNotFoundException ex) {
            LOG.error("Error deserializing string to object", ex);
            return null;
        }
    }

    /**
     * Convert unit to equivalent number
     *
     * @param valueData
     * @return
     */
    public static double convertUnitToNum(String valueData) {
        String lastChar = String.valueOf(valueData.charAt(valueData.length() - 1));
        String data = valueData;
        double convertedValue = 0;
        if (!Util.isDecimal(valueData) && !Util.isDigit(valueData)) {
            String smallUnit = "mun";
            data = valueData.substring(0, valueData.indexOf(lastChar)).replaceAll(" ", "");
            if (smallUnit.contains(lastChar)) {
                convertedValue = Double.parseDouble(Util.convertSmallUnitToValue(data, lastChar));
            } else {
                convertedValue = Util.convertLargeUnitToValue(data, lastChar);
            }
        } else {
            convertedValue = Double.parseDouble(data);
        }
        return convertedValue;
    }

    /**
     * Convert number to it's equivalent unit
     *
     * @param value
     * @return
     */
    public static String convertNumToUnit(double value) {
        double convertedData = value;
        if (convertedData < 1) {
            return Util.getFormatedFraction(convertedData);
        } else {
            return Util.formattedData((long) convertedData, true);
        }
    }

    /**
     * Units regression string
     *
     * @param allowSmall
     * @return
     */
    public static String getUnitRegex(boolean allowSmall) {
        String partialBlock = "(([0-9]{0,10}))(\\.){0,1}[0-9]{0,2}[\\s]{0,1}[K|M|G]";
        if (allowSmall) {
            partialBlock = "(([0-9]{0,10}))(\\.){0,1}[0-9]{0,2}[\\s]{0,1}[K|M|G|m|u\n]";
        }
        String testField = partialBlock + "{0,1}";
        return "^" + testField;
    }

    /**
     *
     * @return
     */
    public static File getCwd() {
        return new File("").getAbsoluteFile();
    }

    /**
     *
     */
    private Util() {
        // private constructor
    }

    /**
     * Return TRex version from version.properties file
     *
     * @return
     */
    public static String getTRexVersion() {
        try {
            Properties prop = new Properties();
            prop.load(Util.class.getClassLoader().getResourceAsStream(VERSION_PROPERTIES_FILE));
            return prop.getProperty(VERSION_KEY);
        } catch (IOException ex) {
            return "";
        }
    }

    /**
     */
    public static void optimizeMemory() {
        System.gc();
    }

}
