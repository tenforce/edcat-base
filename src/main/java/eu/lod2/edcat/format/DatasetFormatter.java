package eu.lod2.edcat.format;

import eu.lod2.edcat.utils.DatasetResponseBuilder;
import org.openrdf.model.Model;

import java.net.URL;
import java.util.Map;

public class DatasetFormatter extends DcatJsonFormatter {

  public DatasetFormatter( URL url ){
    super( url );
  }

  @Override
  public Map<String,Object> format(Model statements) throws FormatException {
    Map<String,Object> compactedJson = super.format( statements );

    return DatasetResponseBuilder.build( (Map<String, Object>) compactedJson );
  }
}
