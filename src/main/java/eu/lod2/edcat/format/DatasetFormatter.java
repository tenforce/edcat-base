package eu.lod2.edcat.format;

import eu.lod2.edcat.utils.DatasetResponseBuilder;
import eu.lod2.edcat.utils.JsonLdContext;
import org.openrdf.model.Model;

import java.util.Map;

public class DatasetFormatter extends DcatJsonFormatter {

  public DatasetFormatter( JsonLdContext context ){
    super( context );
  }

  @Override
  public Map<String,Object> format(Model statements) throws FormatException {
    Map<String,Object> compactedJson = super.format( statements );

    return DatasetResponseBuilder.build( compactedJson );
  }
}
