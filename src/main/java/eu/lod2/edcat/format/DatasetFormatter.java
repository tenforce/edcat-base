package eu.lod2.edcat.format;

import com.github.jsonldjava.utils.JSONUtils;
import eu.lod2.edcat.utils.DatasetResponseBuilder;
import eu.lod2.edcat.utils.DcatJsonCompacter;
import org.openrdf.model.Model;

import java.util.HashMap;
import java.util.Map;

public class DatasetFormatter extends DcatJsonFormatter {

  @Override
  public Object format(Model statements) throws FormatException {
    try {
      HashMap<String, Object> graph = (HashMap<String, Object>) super.format(statements);
      DcatJsonCompacter compacter = new DcatJsonCompacter(getContext());
      Map<String,Object> compactJson = compacter.compact(graph);
      return DatasetResponseBuilder.build(compactJson);
    } catch (Exception e) {
      throw new FormatException(e);
    }
  }


  // TODO: provide context uri on initiate
  private Map<String, Object> getContext() {
    try {
      Object jsonContext = JSONUtils.fromURL(this.getClass().getResource("/eu/lod2/edcat/jsonld/dataset.jsonld"));
      Map<String, Object> json = (Map<String, Object>) jsonContext;
      return (Map<String, Object>) json.get("@context");
    } catch (Exception e) {
      throw new IllegalStateException("illegal context");
    }

  }
}
