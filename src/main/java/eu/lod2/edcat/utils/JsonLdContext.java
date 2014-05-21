package eu.lod2.edcat.utils;

import com.github.jsonldjava.utils.JsonUtils;

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
 * <li>{@link eu.lod2.edcat.utils.DcatJsonCompacter}</li>
 * <li>{@link eu.lod2.edcat.utils.DcatJsonParser}</li>
 * <li>{@link eu.lod2.edcat.utils.BlankNodeNuker}</li>
 * </ul>
 * <p/>
 * The following responsibilities have been assigned to this class:
 * <ul>
 * <li>Context configuration</li>
 * <li>Retrieving the context</li>
 * <li>Parsing of the context</li>
 * </ul>
 */
public class JsonLdContext {

  /** Contains mapping from the JSON keyword to a string representation of the predicate. */
  private Map<String, String> keywordMap;

  /** Contains mapping from a string representation of the predicate, to the JSON keyword. */
  private Map<String, String> reverseKeywordMap;

  /** Location where the context can be found */
  private URL contextLocation;

  /**
   * Retrieves the URL at which the JSON-LD context can be fetched.
   * <p/>
   * todo: this should be retrieved from a configurable location
   *
   * @param kind Kind for which the JsonLdContext should be retrieved.  The following kinds are
   *             supported: [ "dataset" , "catalog" ]
   * @return location of the JSON-LD context.
   */
  private static URL getContextLocation( Kind kind ) {
    return JsonLdContext.class.getResource( "/eu/lod2/edcat/jsonld/" + kind.kindName + ".jsonld" );
  }

  /**
   * Dispatches to {@link #getContextLocation(Kind)} by finding the kind by the supplied string
   * {@code kind}.
   *
   * @see #getContextLocation(eu.lod2.edcat.utils.JsonLdContext.Kind)
   * @see Kind#find(String)
   */
  public static URL getContextLocation( String kind ) {
    return getContextLocation( Kind.find( kind ) );
  }

  /** Enum containing the kinds which this JsonLdContext class can interpret. */
  public enum Kind {
    Dataset( "dataset" ), Catalog( "catalog" );

    /** Textual representation of the kind */
    public String kindName;

    Kind( String kind ) {
      this.kindName = kind;
    }

    /**
     * Retrieves the Kind with name {@code name}
     *
     * @param name kindName for which to retrieve the kind.
     * @return Kind with kindName {@code name} or null if said kind did not exist.
     */
    public static Kind find( String name ) {
      for ( Kind k : Kind.values() )
        if ( k.kindName.equals( name ) )
          return k;
      return null;
    }
  }

  /**
   * Simple constructor for a JsonLdContext.
   * <p/>
   * Many operations on the JsonLdContext are instance methods for performance and threading
   * reasons.
   *
   * @param kind The kind for which we want to manage the JsonLdContext.
   */
  public JsonLdContext( Kind kind ) {
    this.contextLocation = getContextLocation( kind );
  }

  /**
   * Simple constructor for a JsonLdContext.
   * <p/>
   * Operates by {@link Kind#find(String)}ing the correct Kind and using the simple
   * constructor with that kind.
   *
   * @param kind String representation of the kind for which we want to construct a JsonLdConstext.
   */
  public JsonLdContext( String kind ) {
    this( Kind.find( kind ) );
  }

  /**
   * Simple constructor for the JsonLdContext.
   *
   * @param contextLocation Location where the context handled by this JsonLdContext can be found.
   */
  public JsonLdContext( URL contextLocation ) {
    this.contextLocation = contextLocation;
  }

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
   * of the predicate.
   * <p/>
   * The retrieved map must not be altered.
   *
   * @return Mapping from JSON keywords to the URI of the predicate matching it.
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
  public Map<String, Object> getMarshaledJsonLdContext() {
    try {
      Map<String, Object> json = ( Map<String, Object> ) JsonUtils.fromURL( contextLocation );
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
    Map<String, Object> marshaledJsonLdContext = getMarshaledJsonLdContext();

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
