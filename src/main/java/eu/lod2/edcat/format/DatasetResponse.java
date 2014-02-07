package eu.lod2.edcat.format;

import java.util.HashMap;

@SuppressWarnings( "UnusedDeclaration" )
public class DatasetResponse extends HashMap<String, Object> {

  public String getSelf() {
    return (String) get( "self" );
  }

  public void setSelf( String self ) {
    put( "self", self );
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
