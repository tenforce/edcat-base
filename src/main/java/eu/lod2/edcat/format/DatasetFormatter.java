package eu.lod2.edcat.format;

import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.utils.JSONUtils;
import org.openrdf.model.Model;

import java.util.HashMap;

public class DatasetFormatter extends DcatJsonFormatter {
  @Override
  public Object format(Model statements) throws FormatException {
    try {
      HashMap<String, Object> graph = (HashMap<String,Object>) super.format(statements);
      Object jsonContext = JSONUtils.fromURL(this.getClass().getResource("/eu/lod2/edcat/jsonld/dataset.jsonld"));

      JsonLdOptions opts = new JsonLdOptions();
      return null;
    } catch (Exception e) {
      throw new FormatException(e);
    }
  }
}
