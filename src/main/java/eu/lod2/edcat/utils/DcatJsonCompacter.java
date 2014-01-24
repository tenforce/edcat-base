package eu.lod2.edcat.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DcatJsonCompacter {
  private Map<String,Object> context;
  private Map<String, String> reverseContext;

  public DcatJsonCompacter(Map<String,Object> context) {
    this.context = context;
  }

  public Map<String,Object> compact(Map<String, Object> map) {
    Map<String,Object> reversedMap = new LinkedHashMap<String, Object>(map.size());
    for (String key: map.keySet()) {
      Object o = map.get(key);
      if (o instanceof Map)
        o = compact((Map<String, Object>) o);
      if (o instanceof List)
        for (Object i : (List) o)
          if  (i instanceof Map)
            o = compact((Map<String, Object>) o);
      if (getReverseContext().containsKey(key))
        reversedMap.put(getReverseContext().get(key),o);
      else
        reversedMap.put(key,o);
    }
    return reversedMap;
  }

  public Map<String, String> getReverseContext() {
    if (this.reverseContext == null)
      this.reverseContext = loadReverseContext();
    return this.reverseContext;
  }

  private Map<String, String> loadReverseContext() {
    Map<String, String> reverseContext = new LinkedHashMap<String, String>();
    for (String key : context.keySet()) {
      Object o = context.get(key);
      if (o instanceof Map && ((Map) o).containsKey("@id")) {
        String url = ((Map) o).get("@id").toString();
        reverseContext.put(expandUrl(url), key);
      } else if (o instanceof String) {
        reverseContext.put(expandUrl(o.toString()), key);
      }
    }
    return reverseContext;
  }

  private String expandUrl(String key) {
    String[] split = key.split(":");
    if (split.length == 2 && this.context.containsKey(split[0])){
      String prefix = split[0];
      String term = split[1];
      return this.context.get(prefix) + term;
    }
    else
      return key;
  }
}
