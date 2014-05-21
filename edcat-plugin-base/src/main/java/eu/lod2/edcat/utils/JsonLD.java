package eu.lod2.edcat.utils;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.sesame.SesameTripleCallback;
import com.github.jsonldjava.utils.JsonUtils;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.ParserConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Represents content encoded in a JSON-LD document.
 */
public class JsonLD {

  /**
   * The context of this JSON-LD document.
   */
  protected JsonLdContext context;

  /**
   * Contains a parsed form of the encoded document.
   */
  protected Map<String, Object> document;

  /**
   * Simple constructor for a JsonLD document.
   *
   * @param json JSON describing this JsonLD object.
   */
  public JsonLD( Map<String,Object> json ) {
    this.document = json;
    preProcess();
  }

  /**
   * Constructs a new JsonLD document from the encoded JSON document.
   *
   * @param content String-encoded json document.
   * @return JsonLD object containing the description in the parsed content.
   * @throws IOException Thrown if the format couldn't be read by {@link com.github.jsonldjava.utils.JsonUtils#fromString(String)}
   */
  public static JsonLD parse( String content ) throws IOException {
    return new JsonLD( ( Map ) JsonUtils.fromString( content ) );
  }

  /**
   * Constructs a new JsonLD document from the encoded input stream.
   *
   * @param in Stream containing the json document.
   * @return JsonLD object containing the description in the parsed content.
   * @throws IOException Thrown if the format couldn't be read by {@link com.github.jsonldjava.utils.JsonUtils#fromInputStream(java.io.InputStream)}.
   */
  public static JsonLD parse( InputStream in ) throws IOException {
    Object json = JsonUtils.fromInputStream( in );
    if ( !(json instanceof Map) )
      throw new IllegalArgumentException( "could not convert json to object" );

    return new JsonLD( ( Map<String,Object> ) json );
  }

  /**
   * Run preprocessors on the document when it is being inserted.
   */
  public void preProcess(){
    JsonDatePreProcessor.preProcess( document ); // todo: this should be extensible
  }


  /**
   * Sets the ID of the JsonLD document.
   *
   * @param id URI to set the ID to.
   */
  public void setId( String id ) {
    document.put( "@id", id );
  }

  /**
   * Sets the Id of the JsonLD document.
   *
   * @param id URI to set the ID to.
   */
  public void setId( URI id ) {
    setId( id.stringValue() );
  }

  /**
   * Sets the context of the JsonLD document.
   *
   * @param context New context.
   */
  public void setContext( JsonLdContext context ) {
    this.context = context;
    document.put("@context", context.getMarshaledJsonLdContext());
  }

  /**
   * Converts this JsonLD document to a set of statements.
   *
   * @return Model containing the statements.
   * @throws JsonLdError Thrown if the conversion of the document failed.
   */
  public Model getStatements() throws JsonLdError {
    DcatRDFHandler rdfHandler = new DcatRDFHandler();
    final SesameTripleCallback callback =
        new SesameTripleCallback( rdfHandler, ValueFactoryImpl.getInstance(), new ParserConfig(), null );
    JsonLdOptions options = new JsonLdOptions( "uri-base" );
    JsonLdProcessor.toRDF( document, callback, options );
    return new LinkedHashModel( rdfHandler.getStatements() );
  }

}