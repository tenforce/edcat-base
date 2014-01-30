package eu.lod2.edcat.utils;

import com.github.jsonldjava.utils.JSONUtils;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is a supporting class to help in the use of the @marshaledJsonLdContext used in JSON-LD,
 * which is also used for related purposes.
 * <p/>
 * This helps in retrieving the marshaledJsonLdContext, but it also aides in fetching and
 * translating the marshaledJsonLdContext
 * for various other translational use.
 * <p/>
 * We intend this to be used in:
 * <ul>
 *   <li>{@link eu.lod2.edcat.utils.DcatJsonCompacter}</li>
 *   <li>{@link eu.lod2.edcat.utils.DcatJsonParser}</li>
 *   <li>{@link eu.lod2.edcat.utils.BlankNodeNuker}</li>
 * </ul>
 * <p/>
 * The following responsibilities have been assigned to this class:
 * <ul>
 *   <li>Context configuration</li>
 *   <li>Retrieving the context</li>
 *   <li>Parsing of the context</li>
 * </ul>
 */
public class JsonLdContext {

  /** Contains mapping from the JSON keyword to a string representation of the predicate. */
  private Map<String, String> keywordMap;

  /** Contains mapping from a string representation of the predicate, to the JSON keyword */
  private Map<String, String> reverseKeywordMap;

  /**
   * Retrieves the URL at which the JSON-LD context can be fetched.
   * <p/>
   * todo: this should be retrieved from a configurable location
   *
   * @return location of the JSON-LD context.
   */
  public static URL getContextLocation() {
    return JsonLdContext.class.getResource( "/eu/lod2/edcat/jsonld/dataset.jsonld" );
  }

  /**
   * Simple constructor for a JsonLdContext.
   * <p/>
   * Many operations on the JsonLdContext are instance methods for performance and threading
   * reasons.
   */
  public JsonLdContext() { }

  /**
   * Retrieves a reverse keyword map. Contains mapping from a string representation of the
   * predicate, to the JSON keyword.
   * <p/>
   * The retrieved map must not be altered.
   *
   * @return Reverse context which maps from a property URI to the short name in JSON.
   */
  public Map<String, String> getReverseKeywordMap() {
    if ( reverseKeywordMap == null )
      reverseKeywordMap = loadReverseMapping();

    return reverseKeywordMap;
  }

  /**
   * Retrieves the keyword map. Contains mapping from the JSON keyword to a string representation
   * of
   * the predicate.
   * <p/>
   * The retrieved map must not be altered.
   *
   * @return
   */
  public Map<String, String> getKeywordMap() {
    if ( keywordMap == null )
      keywordMap = loadMapping();

    return keywordMap;
  }

  /**
   * Loads the marshaled context, as used by jsonld-java.
   * <p/>
   * The predicate is either an IRI or an expanded term definition.
   * <p/>
   * {@link "http://www.w3.org/TR/json-ld/#dfn-expanded-term-definition"}
   * {@link "http://www.w3.org/TR/json-ld/#h3_context-definitions"}
   *
   * @return Marshaled context.
   */
  private static Map<String, Object> loadMarshaledJsonLdContext() {
    try {
      Map<String, Object> json = ( Map<String, Object> ) JSONUtils.fromURL( getContextLocation() );
      return ( Map<String, Object> ) json.get( "@context" );
    } catch ( Exception e ) {
      throw new IllegalStateException( "illegal context" );
    }
  }

  /**
   * Performs the actual loading of the reverse context.
   *
   * @return New reverse context map.
   */
  private Map<String, String> loadReverseMapping() {
    Map<String, String> regularMap = getKeywordMap();
    Map<String, String> reverseMap = new LinkedHashMap<String, String>();
    for ( String key : regularMap.keySet() )
      reverseMap.put( regularMap.get( key ), key );
    return reverseMap;
  }

  /**
   * Performs the actual loading of the context.
   *
   * @return New context map.
   */
  private Map<String, String> loadMapping() {
    Map<String, String> mapping = new LinkedHashMap<String, String>();
    Map<String, Object> marshaledJsonLdContext = loadMarshaledJsonLdContext();

    for ( String key : marshaledJsonLdContext.keySet() ) {
      Object o = marshaledJsonLdContext.get( key );
      if ( o instanceof Map && (( Map ) o).containsKey( "@id" ) ) {
        String url = (( Map ) o).get( "@id" ).toString();
        mapping.put( key, expandNamespacedUrl( url, marshaledJsonLdContext ) );
      } else if ( o instanceof String ) {
        mapping.put( key, expandNamespacedUrl( o.toString(), marshaledJsonLdContext ) );
      }
    }
    return mapping;
  }

  /**
   * Expands a namespaced identifier so the prefix is stripped.
   * <p/>
   * eg: "rdf:type" could become "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
   *
   * @param key String which should be expanded, if possible.
   * @return Expanded version of the supplied string, or the string itself if expanding failed.
   */
  private String expandNamespacedUrl( String key, Map<String, Object> marshaledJsonLdContext ) {
    String[] split = key.split( ":" );
    if ( split.length == 2 && marshaledJsonLdContext.containsKey( split[0] ) ) {
      String prefix = split[0];
      String term = split[1];
      return marshaledJsonLdContext.get( prefix ) + term;
    } else
      return key;
  }

}
