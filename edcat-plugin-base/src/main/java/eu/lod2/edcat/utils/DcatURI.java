package eu.lod2.edcat.utils;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DcatURI {
  private static Properties properties;
  public static final String CATALOG_NAMESPACE = concatenateWithSlash(
          getProperties().get("baseUri").toString(),
          getProperties().get("catalogPath").toString()
  );
  public static final String CATALOG_LIST_PATH = "/catalogs";
  public static final String CATALOG_OBJECT_PATH = "/catalogs/{catalogId}";
  public static final String DATASET_LIST_PATH = "/catalogs/{catalogId}/datasets";
  public static final String DATASET_OBJECT_PATH = "/catalogs/{catalogId}/datasets/{datasetId}";
  public static final String DISTRIBUTION_LIST_PATH = "/catalogs/{catalogId}/datasets/{datasetId}/distributions";
  public static final String DISTRIBUTION_OBJECT_PATH = "/catalogs/{catalogId}/datasets/{datasetId}/distributions/{distributionId}";

  /**
   * Constructs a catalog URI using the configured pattern
   * @param id the unique identifier for the catalog to be used
   * @return the URI of the catalog
   */
  public static URI catalogUri(String id)  {
    String uri = concatenateWithSlash(CATALOG_NAMESPACE, id);
    return new URIImpl(uri);
  }


  /**
   * Constructs a dataset URI using the configured pattern
   * @param catalogId the unique identifier for the catalog to be used
   * @param id the unique identifier for the dataset to be used
   * @return the URI of the dataset
   */
  public static URI datasetURI(String catalogId, String id)  {
    String uri = concatenateWithSlash(
            catalogUri(catalogId).stringValue(),
            getProperties().get("datasetPath").toString(),
            id
    );
    return new URIImpl(uri);
  }

  /**
   * Constructs a record URI using the configured pattern
   * @param catalogId the unique identifier for the catalog to be used
   * @param id the unique identifier for the record to be used
   * @return the URI of the catalog record
   */
  public static URI recordURI(String catalogId, String id)  {
    String uri = concatenateWithSlash(
            catalogUri(catalogId).stringValue(),
            getProperties().get("recordPath").toString(),
            id
    );
    return new URIImpl(uri);
  }

  /**
   * Constructs a distribution URI using the configured pattern
   * @param datasetUri the uri of the dataset the distribution belongs to
   * @param id the unique identifier for the distribution to be used
   * @return the URI of the distribution
   */
  public static URI distributionURI(URI datasetUri, String id)  {
    String uri = concatenateWithSlash(
        datasetUri.stringValue(),
        getProperties().get("distributionPath").toString(),
        id
    );
    return new URIImpl(uri);
  }

  private static Properties getProperties() {
    if (properties == null){
      properties = new Properties();
      try {
        if (System.getProperty("ext.properties.dir") == null) throw new IllegalStateException("System property 'ext.properties.dir' is not set.");
        InputStream file = new FileInputStream(new File(System.getProperty("ext.properties.dir"), "uriPattern.properties"));
        properties.load( file  );
        file.close();
      } catch (IOException e) {
        throw new IllegalStateException("uriPattern.properties could not be loaded");
      }
    }
    return properties;
  }

  /**
   * Concatenates multiple strings together, adding a / in between if a / would not be in between
   * after joining.
   *
   * @param strings Strings to concatenate.
   * @return Concatenation of all strings, with an added / in between if no / existed in between.
   */
  private static String concatenateWithSlash( String... strings ) {
    if ( strings.length == 0 ) return "";

    StringBuilder b = new StringBuilder( strings[0] );
    for ( int i = 1; i < strings.length; i++ )
      if ( strings[i - 1].endsWith( "/" ) || strings[i].startsWith( "/" ) )
        b.append( strings[i] );
      else
        b.append( "/" ).append( strings[i] );

    return b.toString();
  }

}
