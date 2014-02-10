package eu.lod2.edcat.format;

import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.query.Sparql;
import org.openrdf.model.Model;
import org.openrdf.model.vocabulary.RDF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatasetFormatter extends CompactedObjectFormatter {

  public DatasetFormatter( JsonLdContext context ){
    super( context );
  }

  @Override
  public DatasetResponse format(Model statements) throws FormatException {
       return postProcess(super.format(statements));
  }

  public static DatasetResponse postProcess(Map<String, Object> compactJson) {
    DatasetResponse response = new DatasetResponse();
    Map<String,Object> dataset = searchMapForUri(compactJson, Sparql.namespaced("dcat", "Dataset").stringValue());
    if (dataset != null && dataset.containsKey("uri")) {
      response.setUri(dataset.get("uri").toString());
      dataset.remove("uri");
      dataset.remove(RDF.TYPE.stringValue());
    }
    response.setDataset(dataset);
    Map<String,Object> fullRecord = searchMapForUri(compactJson,Sparql.namespaced("dcat","Record").stringValue());
    if (fullRecord != null) {
      Map<String,Object> simpleRecord = new HashMap<String, Object>();
      simpleRecord.put("issued",fullRecord.get("issued"));
      simpleRecord.put("modified",fullRecord.get("modified"));
      response.setRecord(simpleRecord);
    }
    return response;
  }

  private static Map<String, Object> searchMapForUri(Map<String, Object> map, String uri) {
    if (map.containsKey(RDF.TYPE.stringValue()) && map.get(RDF.TYPE.stringValue()).equals(uri))
      return map;
    else  {
      for (Object o: map.values()) {
        Map result = deepSearch(o,uri);
        if (result!= null)
          return result;
      }
    }
    return null;
  }

  private static Map<String, Object> deepSearch(Object o,String uri) {
    if (o instanceof Map) {
      Map result = searchMapForUri((Map<String, Object>) o,uri);
      if (result!=null)
        return result;
    }
    else if (o instanceof List) {
      for (Object i : (List) o) {
        return deepSearch(i,uri);
      }
    }
    return null;
  }
}
