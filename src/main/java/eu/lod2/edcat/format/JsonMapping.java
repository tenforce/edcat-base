package eu.lod2.edcat.format;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import java.io.IOException;
import java.util.Properties;

public class JsonMapping {
  public static Properties mapping;

  public static String reverseGet(URI pred) {
    if (mapping == null)
      return null;
    return null;
  }

  public static URI get(String key) {
    if (mapping == null) {
      mapping = reloadmapping();
    }

    return new URIImpl(mapping.getProperty(key));
  }

  private static Properties reloadmapping() {
    mapping = new Properties();
    try {
      //load a mapping file from class path, inside static method
      mapping.load(JsonMapping.class.getClassLoader().getResourceAsStream("jsonMapping.properties"));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return mapping;
  }
}