package eu.lod2.edcat.utils;

import com.github.jsonldjava.core.DocumentLoader;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.sesame.SesameTripleCallback;
import com.github.jsonldjava.utils.JSONUtils;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class DcatJsonParser {
  public static Model jsonLDToStatements(InputStream inputStream, String contextUri, URI id) throws IOException, JsonLdError {
    Map json = convertToJsonMap(inputStream);
    json.put("@context",contextUri);
    json.put("@id",id.stringValue());

    DcatRDFHandler rdfHandler = new DcatRDFHandler();
    final SesameTripleCallback callback = new SesameTripleCallback(rdfHandler, ValueFactoryImpl.getInstance(), new ParserConfig(), null);
    JsonLdOptions options = new JsonLdOptions("uri-base");
    options.documentLoader = new DocumentLoader(); // TODO: this is ugly
    JsonLdProcessor.toRDF(json, callback,options);
    return new LinkedHashModel(rdfHandler.getStatements());
  }

  public static Map convertToJsonMap(InputStream inputStream) throws IOException {
    Object json = JSONUtils.fromInputStream(inputStream);
    if  (!(json instanceof Map))
      throw new IllegalArgumentException("could not convert json to object");

    Map jsonMap = (Map) json;
    return jsonMap;
  }

  public static Object statementsToJsonLD(Model statements, URL context)  throws RDFHandlerException, IOException, JsonLdError {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Rio.write(statements, outputStream, RDFFormat.JSONLD);
    String jsonLDString = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
    Object jsonLD = JSONUtils.fromString(jsonLDString);
    Object jsonContext = JSONUtils.fromURL(context);
    return JsonLdProcessor.compact(jsonLD, jsonContext, new JsonLdOptions());
  }
}
