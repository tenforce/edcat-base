package eu.lod2.edcat.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DcatJsonCompacter {

  /** JsonLdContext used for compacting */
  private JsonLdContext context;

  public DcatJsonCompacter( JsonLdContext.Kind kind ) {
    this.context = new JsonLdContext( kind );
  }

  public DcatJsonCompacter( JsonLdContext context ) {
    this.context = context;
  }

  @SuppressWarnings( "UnusedDeclaration" )
  public DcatJsonCompacter( URL context ){
    this.context = new JsonLdContext( context );
  }

  public Map<String, Object> compact( Map<String, Object> map ) {
    Map<String, Object> reversedMap = new LinkedHashMap<String, Object>( map.size() );
    Map<String, String> reverseContext = context.getReverseKeywordMap();
    for ( String key : map.keySet() ) {
      Object o = map.get( key );
      if ( o instanceof Map )
        o = compact( ( Map<String, Object> ) o );
      if ( o instanceof List ) {
        o = compactList( ( List ) o );
      }
      if ( reverseContext.containsKey( key ) )
        reversedMap.put( reverseContext.get( key ), o );
      else
        reversedMap.put( key, o );
    }
    return reversedMap;
  }

  private List compactList( List list ) {
    List compactedList = new ArrayList( list.size() );
    for ( Object i : list ) {
      if ( i instanceof Map )
        compactedList.add( compact( ( Map<String, Object> ) i ) );
      else
        compactedList.add( i );
    }
    return compactedList;
  }

}
