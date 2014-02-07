package eu.lod2.edcat.format;

import org.openrdf.model.Model;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class CompactedListFormatter extends CompactedJsonLDFormatter {
  public CompactedListFormatter(URL context){
    this.context = context;
  }

  public List format(Model statements) throws FormatException {
     return (List<Map<String,Object>>) super.format(statements);
  }
}
