package eu.lod2.edcat.utils;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.sesame.SesameRDFParser;
import com.github.jsonldjava.sesame.SesameTripleCallback;
import com.github.jsonldjava.utils.JSONUtils;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFHandlerException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class DcatJsonParser {
  public static Model jsonLDToStatements( InputStream inputStream, String contextUri, URI id, URI type ) throws IOException, JsonLdError {
    Map json = convertToJsonMap( inputStream );
    // TODO: detect existing value and merge (for type) or keep it
    json.put( "@context", contextUri );
    json.put( "@id", id.stringValue() );
    json.put( "@type", type.stringValue() );

    JsonDatePreProcessor.preProcess( json );
    DcatRDFHandler rdfHandler = new DcatRDFHandler();
    final SesameTripleCallback callback = new SesameTripleCallback( rdfHandler, ValueFactoryImpl.getInstance(), new ParserConfig(), null );
    JsonLdOptions options = new JsonLdOptions( "uri-base" );
    JsonLdProcessor.toRDF( json, callback, options );
    return new LinkedHashModel( rdfHandler.getStatements() );
  }

  public static Map convertToJsonMap( InputStream inputStream ) throws IOException {
    Object json = JSONUtils.fromInputStream( inputStream );
    if ( !(json instanceof Map) )
      throw new IllegalArgumentException( "could not convert json to object" );

    return ( Map ) json;
  }

  public static Object statementsToJsonLD( Model statements, URL context ) throws RDFHandlerException, IOException, JsonLdError {
    return JsonLdProcessor.fromRDF( statements, new SesameRDFParser() );
  }
}
