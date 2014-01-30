package eu.lod2.edcat.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DcatJsonCompacter {

  /** JsonLdContext used for compacting */
  private JsonLdContext context;

  public DcatJsonCompacter() {
    this.context = new JsonLdContext();
  }

  public Map<String,Object> compact(Map<String, Object> map) {
    Map<String,Object> reversedMap = new LinkedHashMap<String, Object>(map.size());
    Map<String,String> reverseContext = context.getReverseKeywordMap();
    for (String key: map.keySet()) {
      Object o = map.get(key);
      if (o instanceof Map)
        o = compact((Map<String, Object>) o);
      if (o instanceof List)
        for (Object i : (List) o)
          if  (i instanceof Map)
            o = compact((Map<String, Object>) o);
      if (reverseContext.containsKey(key))
        reversedMap.put(reverseContext.get(key),o);
      else
        reversedMap.put(key,o);
    }
    return reversedMap;
  }

}
