package eu.lod2.edcat.format;

import eu.lod2.edcat.utils.DcatJsonCompacter;
import eu.lod2.edcat.utils.JsonLdContext;
import org.openrdf.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DcatJsonFormatter implements ResponseFormatter {

  /** Context by the triples will be encoded */
  protected JsonLdContext context;

  /**
   * Simple constructor
   *
   * @param context Context with respect to which the triples should be encoded.
   */
  public DcatJsonFormatter(JsonLdContext context){
    this.context = context;
  }

  @Override
  public Map<String,Object> format(Model statements) throws FormatException {
    Map<String, Object> graph = new HashMap<String, Object>();
    for (Resource topNode : getTopNodes(statements)) {
      graph.put(topNode.stringValue(), buildGraph(topNode, "", statements));
    }
    if( context != null ) {
      DcatJsonCompacter compacter = new DcatJsonCompacter( context );
      graph = compacter.compact( graph );
    }

    return graph;
  }

  private HashMap<String, Object> buildGraph(Resource topNode, String parentShortName, Model statements) {
    DcatJsonHashMap graph = new DcatJsonHashMap();
    Model description = statements.filter(topNode, null, null);
    for (Statement s : description) {
      Value val = s.getObject();
      if (resourceIsDefinedInStatements(statements, val)) {
        Map<String, Object> subNode = buildGraph((Resource) val, shortNameFor(s.getPredicate(), parentShortName), statements);
        subNode.put("uri",val.stringValue()) ; // TODO: hardcoded value
        graph.put(shortNameFor(s.getPredicate()), subNode);
      } else
        graph.put(shortNameFor(s.getPredicate()), val);
    }
    return graph;
  }

  private boolean resourceIsDefinedInStatements(Model statements, Value val) {
    return val instanceof Resource && statements.filter((Resource) val, null, null).size() > 0;
  }

  private String shortNameFor(URI predicate, String parent) {
    return predicate.stringValue();
  }

  private String shortNameFor(URI predicate) {
    return predicate.stringValue();
  }

  public List<Resource> getTopNodes(Model statements) {
    List<Resource> topNodes = new ArrayList<Resource>();
    for (Resource subj : statements.subjects())
      if (statements.filter(null, null, subj).size() == 0)
        topNodes.add(subj);
    return topNodes;
  }

}