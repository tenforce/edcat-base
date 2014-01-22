package eu.lod2.query;

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
   * Varmap containing the class variables and their corresponding values
   */
  private static Map<String, Object> classVariableMap = constructLocalVariableMap(
    "PREFIX", "PREFIX : <http://lod2.tenforce.com/edcat/example/config/> \n" +
      "PREFIX dct: <http://purl.org/dc/terms/> \n" +
      "PREFIX dcat: <http://www.w3.org/ns/dcat#> \n" +
      "PREFIX edcat: <http://lod2.tenforce.com/edcat/terms/> \n" +
      "PREFIX cterms: <http://lod2.tenforce.com/edcat/terms/config/> \n" +
      "PREFIX catalogs: <http://lod2.tenforce.com/edcat/catalogs/> \n",
    "DEFAULT_CATALOG", new URIImpl("http://lod2.tenforce.com/edcat/catalogs/example"),
    "CONFIG_GRAPH", new URIImpl("http://lod2.tenforce.com/edcat/example/config/"));

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
   * Constructs a local variable map for the supplied variables.
   *
   * @return Map in which the keys are the values in the string as they should be replaced and
   * the values are the values which belong to the key.
   */
  private static Map<String, Object> constructLocalVariableMap( Object... variables ) {
    Map<String, Object> varmap = new HashMap<String, Object>();
    if ( !(variables.length % 2 == 0) )
      throw new IllegalArgumentException( "Variable map did not contain an even amount of key/values.  Key " + variables[variables.length - 1].toString() + " did not get a value assigned." );
    for ( int i = 0; i < variables.length; i = i + 2 )
      varmap.put( (String) variables[i], variables[i + 1] );
    return varmap;
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
   *   - Object: o.toString() which represents the object inside a Sparql query.
   *   - URI: "<#{o.stringValue()}>".
   */
  private static String queryInjectionConversion( Object o ) {
    if( o instanceof URI )
      return "<" + ((URI) o).stringValue() + ">";
    else
      return o.toString();
  }

  /**
   * Retrieves the substring at index {@code i} which matches {@code regex} in {@code source}.
   *
   * @param regex Regex to match.
   * @param source String in which the match is searched.
   * @param groupIndex Index of the group in the regex (0 for complete match).
   * @return String containing the (sub)matched portion of the String.
   */
  private static String subString(String regex, String source, int groupIndex){
    Matcher matcher = Pattern.compile(regex).matcher( source );
    if( !matcher.find() ) return null;
    return matcher.group( groupIndex );
  }

  /**
   * Same as {@link #subString(String, String, int)} but with a default groupIndex of 0.
   * @see #subString(String, String, int)
   */
  private static String subString(String regex, String source){
    return subString( regex, source, 0 );
  }
}