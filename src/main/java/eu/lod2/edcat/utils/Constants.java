package eu.lod2.edcat.utils;

import org.apache.commons.io.IOUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//* list of constants used throughout the program
public class Constants {
  public static final String register_uri = "/register";
  public static final String datasets_uri = "/datasets";
  public static final int randomIdLength = 32;

  public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

  public final static String defaultUriProp = "defaults.uriBase";

  public final static String defaultGraphProp = "defaults.graph";

  public final static String defaultPageSize = "100";

  public final static String sparqlTargetVar = "?subject";

  public static Properties properties;
  public static final String datasetType = "http://www.w3.org/ns/dcat#Dataset";
  public static final String version_uri = "/version";
  public static final String apiVersionProp = "api.version";
  public static final String defaultLangProp = "defaults.language";
  private static final String targetURIProp = "defaults.targetURI";
  public static final String datasetURIType = "datasets";



  //* returns the bas uri that is being used in the application
  public static String getURIBase() {
    Properties properties = ensureProperties();
    return properties.getProperty(defaultUriProp);
  }

  public static Properties ensureProperties() {
    if (properties == null) {
      properties = reloadProperties();
    }
    return properties;
  }

  public static Map<String, String> invertedProperties;

  public static Map<String, String> ensureInvertedProperties() {
    if (invertedProperties != null) {
      return invertedProperties;
    }
    Properties properties = ensureProperties();
    Map<String, String> result = new HashMap<String, String>();
    for (String key : properties.stringPropertyNames()) {
      result.put(properties.getProperty(key), key);
    }
    invertedProperties = result;
    return invertedProperties;
  }

  public static Properties reloadProperties() {
    properties = new Properties();
    try {
      //load a properties file from class path, inside static method
      properties.load(Constants.class.getClassLoader().getResourceAsStream("mappings.properties"));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return properties;
  }

  public static String getDefaultGraph() {
    Properties properties = ensureProperties();
    return properties.getProperty(defaultGraphProp);
  }

  public static String getVersion() {
    Properties properties = ensureProperties();
    return properties.getProperty(apiVersionProp);
  }

  /**
   * Formats date according to xsd:dateTime
   * You're not going to believe this: https://www.ibm.com/developerworks/community/blogs/HermannSW/entry/java_simpledateformat_vs_xs_datetime26?lang=enhttps://www.ibm.com/developerworks/community/blogs/HermannSW/entry/java_simpledateformat_vs_xs_datetime26?lang=en
   */
  public static String formatDate(Date date) {
    if (date == null) {
      return null;
    }
    SimpleDateFormat format = Constants.dateFormat;

    String dateTime = format.format(date);

    if (!dateTime.contains("Z")) {
      dateTime = new StringBuilder(dateTime).insert(dateTime.length() - 2, ':').toString();
    }
    return dateTime;
  }

  public static Date parseDate(String date) throws ParseException {
    if (date == null) {
      return null;
    }
    Calendar cal = DatatypeConverter.parseDate(date);
    return cal.getTime();
  }


  public static String getDefaultLanguage() {
    Properties properties = ensureProperties();
    return properties.getProperty(defaultLangProp);
  }

  public static String getTargetURI() {
    Properties properties = ensureProperties();
    return properties.getProperty(targetURIProp);
  }

  public static String fetchValueFromSearchResult(String fieldName, Map<String, String> variableNameMapping, Map<String, String> resultRow) {
    String varName;
    if (variableNameMapping == null) {
      varName = fieldName;
    } else {
      varName = variableNameMapping.get(fieldName);
    }
    return resultRow.get(varName);
  }

  public static Integer parseInt(String bytesize) {
    if (bytesize == null) {
      return null;
    }
    return Integer.parseInt(bytesize);
  }
  private static String jsonLdContext;
  public static String getJsonLdContext() {
    if(jsonLdContext == null){
      try{
        jsonLdContext = IOUtils.toString(Constants.class.getClassLoader().getResourceAsStream("jsonld-context.json-ld"));
      }catch (Exception e){
        e.printStackTrace();
      }
    }
    return jsonLdContext;
  }
}
