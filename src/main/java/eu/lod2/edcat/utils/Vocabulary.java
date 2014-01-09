package eu.lod2.edcat.utils;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import java.io.IOException;
import java.util.Properties;

public class Vocabulary {
  public static Properties properties;

  public static URI get(String key) {
    if (properties == null) {
      properties = reloadProperties();
    }
    return new URIImpl(properties.getProperty(key));
  }

  private static Properties reloadProperties() {
    properties = new Properties();
    try {
      //load a properties file from class path, inside static method
      properties.load(Vocabulary.class.getClassLoader().getResourceAsStream("mappings.properties"));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return properties;
  }
}
