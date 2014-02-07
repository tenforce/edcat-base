package eu.lod2.edcat.format;

import eu.lod2.edcat.utils.DcatJsonCompacter;
import org.openrdf.model.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CompactedJsonLDFormatter implements ResponseFormatter {

  protected URL context;
  public static final String URI_KEY = "uri";

  public CompactedJsonLDFormatter(URL context){
    this.context = context;
  }

  public CompactedJsonLDFormatter(){
  }

  @Override
  public Object format(Model statements) throws FormatException {
    List<Map<String,Object>> graph = new ArrayList<Map<String,Object>>();
    for (Resource topNode : getTopNodes(statements)) {
      Map<String,Object> resource = buildGraph(topNode, "", statements);
      resource.put(URI_KEY,topNode);
      graph.add(resource);
    }
    if( context != null ) {
      DcatJsonCompacter compacter = new DcatJsonCompacter( );
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
        subNode.put(URI_KEY,val.stringValue()) ;
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
