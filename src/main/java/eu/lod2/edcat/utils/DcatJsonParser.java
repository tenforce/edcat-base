package eu.lod2.edcat.utils;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.sesame.DcatRDFHandler;
import com.github.jsonldjava.sesame.SesameTripleCallback;
import com.github.jsonldjava.utils.JSONUtils;
import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.ParserConfig;

import java.io.IOException;
import java.io.InputStream;

public class DcatJsonParser {
  public static Model parse(InputStream inputStream,String contextUri) throws IOException, JsonLdError {
    Object json = JSONUtils.fromInputStream(inputStream);
    DcatRDFHandler rdfHandler = new DcatRDFHandler();
    final SesameTripleCallback callback = new SesameTripleCallback(rdfHandler, ValueFactoryImpl.getInstance(), new ParserConfig(), null);
    JsonLdProcessor.toRDF(json, callback);
    return new LinkedHashModel(rdfHandler.getStatements());
  }
}
