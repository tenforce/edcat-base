package eu.lod2.edcat.format;

import java.util.HashMap;

@SuppressWarnings( "UnusedDeclaration" )
public class DatasetResponse extends HashMap<String, Object> {

  public String getUri() {
    return (String) get( "uri" );
  }

  public void setUri(String self) {
    put( "uri", self );
  }

  public Object getDataset() {
    return get( "dataset" );
  }

  public void setDataset( Object dataset ) {
    put( "dataset", dataset );
  }

  public Object getRecord() {
    return get( "record" );
  }

  public void setRecord( Object record ) {
    put( "record", record );
  }
}
