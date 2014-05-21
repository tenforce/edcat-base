package eu.lod2.hooks.constraints.graph;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class allows you to easily create a new NodeSet for testing the priorities and for playing
 * around with the ordering system.  There are too many visible elements in this class to have a
 * clean separation of concerns, yet they are good for testing.
 * <p/>
 * Each node is identified by a string of characters enclosed in arrow signs.
 * <p/>
 * "->a->" would be a valid node.
 * <p/>
 * "->first->" would be a valid node.
 * <p/>
 * If a node must be executed after a different node, put the node which must be executed first
 * before the arrow:
 * <p/>
 * "a->first->" indicates that first requires a to be executed before it.
 * <p/>
 * The same goes if you want a node to be executed after the current node
 * <p/>
 * "a->first->b" would indicate that first must be executed between a and b.
 * <p/>
 * if you depend on multiple nodes, separate them by commas
 * <p/>
 * "a,b,c->second->d,e" indicates that first a, b and c should be executed (in any order), then
 * second, and then d and e (in any order).
 * <p/>
 * By default, a node has a LATE BroadPriority, you can raise the priority by adding a + sign to the
 * node's name
 * <p/>
 * "a->+first->b
 */
public class StringNodeSet extends NodeSet<NodeString> {

  /**
   * Translates a list of nodeStrings to a list of the String names of the NodeStrings.  This is
   * easier to work with for the end user.
   * <p/>
   * The list is constructed by iterating over the supplied Collection.  As such the ordering of the
   * Collection is maintained if it has an ordering.
   *
   * @param nodeStrings Collection containing the NodeString objects to translate
   * @return List of strings containing the names of the NodeString objects in the order supplied by
   * the iterator.
   */
  public List<String> nodeStringsToNodeNames( Collection<NodeString> nodeStrings ) {
    List<String> strings = new ArrayList<String>( nodeStrings.size() );
    for ( NodeString nodeString : nodeStrings )
      strings.add( nodeString.getName() );
    return strings;
  }

  /**
   * Returns the NodeString objects in the current set.
   *
   * @return Collection containing all NodeStrings in the current set.
   */
  protected Collection<NodeString> getNodeStrings() {
    inRetrievingState();
    HashSet<NodeString> set = new HashSet<NodeString>();
    set.addAll( handlers );
    return set;
  }

  /**
   * Retrieves the nodes which are used internally in this StringNodeSet
   *
   * @return Set containing all nodes available to StringNodeSet
   */
  protected Set<Node<NodeString>> getNodes() {
    inRetrievingState();
    return new HashSet<Node<NodeString>>( nodes );
  }

  public void add( String nodeStringSpecification ) throws InvalidNodeStringException {
    add( new NodeString( nodeStringSpecification ) );
  }

  @Override
  public Collection<NodeString> hookExecutesBefore( NodeString nodeString ) {
    Set<NodeString> nodes = new HashSet<NodeString>();

    for ( String before : nodeString.beforeDependencies() ) {
      NodeString beforeNode = findNodeStringByName( before );
      if ( beforeNode != null )
        nodes.add( beforeNode );
    }

    return nodes;
  }

  @Override
  public Collection<NodeString> hookExecutesAfter( NodeString nodeString ) {
    Set<NodeString> nodes = new HashSet<NodeString>();

    for ( String after : nodeString.afterDependencies() ) {
      NodeString afterNode = findNodeStringByName( after );
      if ( afterNode != null )
        nodes.add( afterNode );
    }

    return nodes;
  }

  @Override
  public Node.SchedulingPreference hookSchedulingPreference( NodeString nodeString ) {
    return nodeString.broadPreference();
  }

  /**
   * Returns the NodeString which is currently contained in this name.
   *
   * @param nodeName Name of the node to find.
   * @return NodeString with name nodeName or null if it couldn't be found.
   */
  protected NodeString findNodeStringByName( String nodeName ) {
    for ( NodeString nodeString : handlers )
      if ( nodeString.getName().equals( nodeName ) )
        return nodeString;
    return null;
  }

}

class NodeString {
  /** String containing the specification */
  String specification;

  /** Pattern used to split the specification in its three main parts */
  private Pattern nodeNamePattern;

  /**
   * Constructs a new NodeString from a specification
   *
   * @param specification As described in the StringNodeSet description
   */
  public NodeString( String specification ) throws InvalidNodeStringException {
    nodeNamePattern = Pattern.compile( "^\\s*(.*)\\s*->\\s*(\\+)?\\s*(.*)\\s*->\\s*(.*)\\s*$" );
    if ( !nodeNamePattern.matcher( specification ).find() )
      throw new InvalidNodeStringException( specification );

    this.specification = specification;
  }

  /**
   * Returns the name of this node
   *
   * @return String denoting the name of this node
   */
  public String getName() {
    return nthSpec( 2 );
  }

  /**
   * Returns the broad scheduling preference of this node
   *
   * @return {@link Node.SchedulingPreference}.EARLY if it should be scheduled early, {@link
   * Node.SchedulingPreference}.LATE otherwise
   */
  public Node.SchedulingPreference broadPreference() {
    if ( "+".equals( nthSpec( 1 ) ) )
      return Node.SchedulingPreference.EARLY;
    else
      return Node.SchedulingPreference.LATE;
  }

  /**
   * Returns the AFTER dependencies of this node. In the specification "a,b -> c -> d", this would
   * be "a" and "b".
   *
   * @return Collection containing the names of the AFTER dependencies of this node.
   */
  public Collection<String> afterDependencies() {
    String afterString = nthSpec( 0 );
    return splitDependencyMatch( afterString );
  }

  /**
   * Returns the BEFORE dependencies of this node. In the specification "a,b -> c -> d,e", this
   * would be "d" and "e"
   *
   * @return Collection containing the names of the BEFORE dependencies of this node.
   */
  public Collection<String> beforeDependencies() {
    String beforeString = nthSpec( 3 );
    return splitDependencyMatch( beforeString );
  }

  /**
   * Splits the string containing the dependency match to a collection of names of nodes.
   * <p/>
   * "a, b ,c" ~> ["a","b","c"]
   *
   * @param match String containing only the dependency match
   * @return Collection containing the strings of each nodes in the dependency
   */
  private Collection<String> splitDependencyMatch( String match ) {
    return Arrays.asList( match.split( " *, *" ) );
  }

  /**
   * Retrieves the n^th portion of the specification string. - 0 are the AFTER dependencies - 1 is
   * the name - 2 are the BEFORE dependencies
   *
   * @return n^th portion of the specification string.
   */
  @SuppressWarnings({"all"}) // i would like a more specific suppression, but couldn't find it.
  private String nthSpec( int number ) {
    Matcher m = nodeNamePattern.matcher( specification );
    m.matches();
    return m.toMatchResult().group( 1 + number ); // 1+ because 0 is the complete match
  }
}

/**
 * Exception which is thrown if an invalid NodeString is supplied.
 */
class InvalidNodeStringException extends Exception {
  public InvalidNodeStringException( String nodeString ) {
    super( "NodeString " + nodeString + " is not a valid nodeString" );
  }
}
