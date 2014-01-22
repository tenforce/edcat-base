package eu.lod2.edcat.format;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DcatJsonFormatter implements ResponseFormatter {
  @Override
  public Object format(Model statements) throws FormatException {
    HashMap<String, Object> graph = new HashMap<String, Object>();
    for (Resource topNode : getTopNodes(statements)) {
      graph.put(topNode.stringValue(), buildGraph(topNode, "", statements));
    }
    return graph;
  }

  private HashMap<String, Object> buildGraph(Resource topNode, String parentShortName, Model statements) {
    DcatJsonHashMap graph = new DcatJsonHashMap();
    Model description = statements.filter(topNode, null, null);
    for (Statement s : description) {
      Value val = s.getObject();
      if (val instanceof Resource && statements.filter((Resource) val, null, null).size() > 0) {
        graph.put(shortNameFor(s.getPredicate()), buildGraph((Resource) val, shortNameFor(s.getPredicate(), parentShortName), statements));
      } else
        graph.put(shortNameFor(s.getPredicate()), val);
    }
    return graph;
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
