package eu.lod2.edcat.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonDatePreProcessor {
  private static final List<String> dateTimeFields = Arrays.asList("modified", "issued");

  public static void preProcess(Map json) {
    setTypeForDateOrTime(json);
  }

  private static void setTypeForDateOrTime(Map json) {
    if (json.containsKey("modified")) {
      json.put("modified", dateTest(json.get("modified")));
    }
    if (json.containsKey("issued")) {
      json.put("issued", dateTest(json.get("issued")));
    }
  }

  private static Object dateTest(Object value) {
    Map<String, String> typedDate = new HashMap<String, String>();
    if (value instanceof String) {
      String date = (String) value;
      if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
        typedDate.put("@value", date);
        typedDate.put("@type", "xsd:date");
        return typedDate;
      } else if (date.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?([+-][0-2]\\d:[0-5]\\d|Z)?")) {
        typedDate.put("@value", date);
        typedDate.put("@type", "xsd:dateTime");
        return typedDate;
      }
    }
    return value;
  }


}
