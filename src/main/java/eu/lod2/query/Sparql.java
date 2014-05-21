package eu.lod2.query;

import eu.lod2.edcat.utils.DcatURI;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple hackish system which allows you to define Sparql queries in a clear and concise way.
 * <p/>
 * <code>
 * Sparql.query( "
 * \@prefix
 * SELECT ?foo, ?bar
 * FROM $graph
 * WHERE {
 * $graph a @graph_class;
 * terms:Catalog ?foo.
 * ?foo a $bar.
 * }",
 * "graph", new URImpl("http://lod2.tenforce.com/wp8/graphs/exampleGraph"),
 * "<bar>", "http://lod2.tenforce.com/wp8/objects/exampleObject");
 * </code>
 * <p/>
 * which would yield the following query:
 * <code>
 * PREFIX terms: <http://lod2.tenforce.com/wp8/terms#>
 * SELECT ?foo , ?bar
 * FROM <http://lod2.tenforce.com/wp8/graphs/exampleGraph>
 * WHERE {
 * $graph a <http://lod2.tenforce.com/wp8/graphs/exampleGraph>;
 * terms:Catalog ?foo.
 * ?foo a <http://lod2.tenforce.com/wp8/objects/exampleObject>.
 * }
 * <p/>
 * </code>
 */
public class Sparql {

  /**
   * Pattern which recognizes class variables
   */
  private static Pattern constantPattern = Pattern.compile( "@([A-Za-z\\-_]+)" );
  /**
   * Pattern which recognizes local variables
   */
  private static Pattern variablePattern = Pattern.compile( "\\$([A-Za-z\\-_]+)" );

  /**
   * Contains all namespaces used in our Sparql queries.
   */
  private static Map<String, URI> namespaces = constructUriMap(
    "", "http://lod2.tenforce.com/edcat/example/config/", // default config graph
    "config", "http://lod2.tenforce.com/edcat/example/config/", // default config graph
    "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
    "dct", "http://purl.org/dc/terms/",
    "dcat", "http://www.w3.org/ns/dcat#",
    "edcat", "http://lod2.tenforce.com/edcat/terms/",
    "cterms", "http://lod2.tenforce.com/edcat/terms/config/",
    "skos", "http://www.w3.org/2004/02/skos/core#",
    "foaf", "http://xmlns.com/foaf/0.1/",
    "catalogs", DcatURI.CATALOG_NAMESPACE); // namespace for all the catalogs

  /**
   * Varmap containing the class variables and their corresponding values
   */
  private static Map<String, Object> classVariableMap = constructLocalVariableMap(
    "PREFIX", constructSparqlPrefixes( namespaces ),
    "CONFIG_GRAPH", new URIImpl( "http://lod2.tenforce.com/edcat/example/config/" ) );

  /**
   * Constructs a new sparql query in a short format.
   * <p/>
   * The first element is a string containing the query with variable replacements.  The other
   * elements consist of a plist of key-value pairs for the local variables.  The key of the
   * local variable is the name which will be replaced (ie: "foo" for variable "$foo") and the
   * value is its replacement.
   *
   * @param pattern   Sparql query in which the variables will be substituted.
   * @param variables Plist containing the local key-value pairs.
   * @return String representing an executable sparql query.
   */
  public static String query( String pattern, Object... variables ) {
    Map<String, Object> localVariables = constructLocalVariableMap( variables );
    return replaceClassVariables( replaceLocalVariables( pattern, localVariables ) );
  }

  /**
   * Retrieves the value of a variable from the classVariableMap.  This map contains the @ variables
   * which may be used in a SparqlQuery.
   *
   * @param name Name of the variable (this is the name without the @ prefix)
   * @return Object containing the value in the map.  This value may need to be converted to a
   * String for easy use inside a query.
   */
  public static Object getClassMapVariable( String name ) {
    return classVariableMap.get( name );
  }

  /**
   * Returns a URI for the supplied namespace.
   *
   * @param name Shorthand name of the namespace (without the colon prefix).
   * @return URI representation of the required namespace.
   * @throws java.lang.IllegalArgumentException Thrown when there was no record of the namespace.
   */
  public static URI getNamespace( String name ) throws IllegalArgumentException {
    URI namespace = namespaces.get( name );
    if ( namespace == null )
      throw new IllegalArgumentException( "Tried to fetch inexistant namespace" );
    return namespace;
  }

  /**
   * Retrieves the URI for an element with name {@code name} in namespace {@code namespace}.
   * <p/>
   * In its simplicity, this constructs a URI of the form "#{getNameSpace}#{name}".
   *
   * @param namespace Shorthand form of the namespace as specified in this class.
   * @param name      Name of the URI.
   * @return URI which represents the identified object.
   */
  public static URI namespaced( String namespace, String name ) {
    return new URIImpl( getNamespace( namespace ).stringValue() + name );
  }

  /**
   * Constructs a local variable map for the supplied variables.
   *
   * @return Map in which the keys are the values in the string as they should be replaced and
   * the values are the values which belong to the key.
   */
  private static Map<String, Object> constructLocalVariableMap( Object... variables ) {
    return constructMapFromList( variables );
  }

  /**
   * Constructs a map between String and URI from a list of values.
   *
   * @param variables PLIST containing the keys and the values.
   * @return Map which maps a string to a Uri.
   */
  private static Map<String, URI> constructUriMap( String... variables ) {
    Map<String, String> varmap = constructMapFromList( variables );
    Map<String, URI> resultMap = new HashMap<String, URI>();
    for ( String key : varmap.keySet() )
      resultMap.put( key, new URIImpl( varmap.get( key ) ) );
    return resultMap;
  }

  /**
   * Constructs a Map from an array of values.
   *
   * @param plist Plist containing the local key-value pairs.
   * @param <K>   Type of the keys in the plist.  The keys will be typecast to this type.
   * @param <V>   Type of the values in the plist.  They values will be typecast to this type.
   * @return Supplied PLIST in map version
   */
  @SuppressWarnings("unchecked")
  private static <K, V> Map<K, V> constructMapFromList( Object... plist ) {
    Map<K, V> varmap = new HashMap<K, V>();
    if ( !(plist.length % 2 == 0) )
      throw new IllegalArgumentException( "Map did not contain an even amount of key/values.  Key " + plist[plist.length - 1].toString() + " did not get a value assigned." );
    for ( int i = 0; i < plist.length; i = i + 2 )
      varmap.put( (K) plist[i], (V) plist[i + 1] );
    return varmap;
  }

  /**
   * Constructs a set of SPARQL prefixes from a supplied namespace -> URI mapping
   *
   * @param varmap Map between the namespace name and the URI.
   * @return String containing the prefixes for the namespaces which could be used in a SPARQL query.
   */
  private static String constructSparqlPrefixes( Map<String, URI> varmap ) {
    StringBuilder sb = new StringBuilder();
    for ( String s : varmap.keySet() )
      sb.append( " PREFIX " + s + ": " + queryInjectionConversion( varmap.get( s ) ) + " \n" );
    return sb.toString();
  }

  /**
   * Replaces the local variables in the pattern and returns a new String containing {@code pattern}
   * with the variables replaced.
   *
   * @param pattern Pattern in which we will replace the variables.
   * @param varmap  key/value pair containing the variables to replace as keywords and their
   *                corresponding value as the value.
   * @return New String representing the pattern with the variables replaced.
   */
  private static String replaceLocalVariables( String pattern, Map<String, Object> varmap ) {
    // see http://www.regular-expressions.info/java.html
    StringBuffer buffer = new StringBuffer();
    Matcher matcher = variablePattern.matcher( pattern );
    while ( matcher.find() ) {
      String matched = matcher.group( 1 );
      if ( varmap.containsKey( matched ) )
        matcher.appendReplacement( buffer, queryInjectionConversion( varmap.get( matched ) ) );
    }
    matcher.appendTail( buffer );
    return buffer.toString();
  }

  /**
   * Replaces the class variables in pattern.
   *
   * @param pattern Pattern in which the variables will be replaced.
   * @return New String containing consisting of pattern with the values replaced.
   */
  private static String replaceClassVariables( String pattern ) {
    // see http://ww.regular-expressions.info/java.html
    StringBuffer buffer = new StringBuffer();
    Matcher matcher = constantPattern.matcher( pattern );
    while ( matcher.find() ) {
      String matched = matcher.group( 1 );
      if ( classVariableMap.containsKey( matched ) )
        matcher.appendReplacement( buffer, queryInjectionConversion( classVariableMap.get( matched ) ) );
    }
    matcher.appendTail( buffer );

    return buffer.toString();
  }

  /**
   * Converts an object to something that may be used inside a query.  This is specialized for all
   * objects which we may want to serialize.
   *
   * @param o Object to convert to a String for the query.
   * @return String representing the object inside a Sparql query.
   * - Object: o.toString() which represents the object inside a Sparql query.
   * - URI: "<#{o.stringValue()}>".
   */
  private static String queryInjectionConversion( Object o ) {
    if ( o instanceof URI )
      return "<" + ((URI) o).stringValue() + ">";
    else
      return o.toString();
  }

  /**
   * Retrieves the substring at index {@code i} which matches {@code regex} in {@code source}.
   *
   * @param regex      Regex to match.
   * @param source     String in which the match is searched.
   * @param groupIndex Index of the group in the regex (0 for complete match).
   * @return String containing the (sub)matched portion of the String.
   */
  private static String subString( String regex, String source, int groupIndex ) {
    Matcher matcher = Pattern.compile( regex ).matcher( source );
    if ( !matcher.find() ) return null;
    return matcher.group( groupIndex );
  }

  /**
   * Same as {@link #subString(String, String, int)} but with a default groupIndex of 0.
   *
   * @see #subString(String, String, int)
   */
  private static String subString( String regex, String source ) {
    return subString( regex, source, 0 );
  }
}