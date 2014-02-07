package eu.lod2.edcat.format;

import org.openrdf.model.Model;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class CompactedObjectFormatter extends CompactedJsonLDFormatter {

  public CompactedObjectFormatter(URL context) {
    super(context);
  }

  public Map<String,Object> format(Model statements) throws FormatException {
    List<Map<String,Object>> models = (List) super.format(statements);
    if (models.size() != 1)
      throw new FormatException("expected only one root resource. " + models.size() + " resources found");
    return models.get(0);
  }
}
