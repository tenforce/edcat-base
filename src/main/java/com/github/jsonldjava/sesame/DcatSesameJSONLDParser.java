package com.github.jsonldjava.sesame;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdProcessor;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DcatSesameJSONLDParser extends SesameJSONLDParser {

  public DcatSesameJSONLDParser() {
    super();
  }

  @Override
  public RDFFormat getRDFFormat() {
    return DcatSesameJSONLDParserFactory.DCATJSONLD;
  }

  private void dcatParse(final Object o, final String baseURI) throws IOException,
          RDFParseException, RDFHandlerException {
    final SesameTripleCallback callback = new SesameTripleCallback(getRDFHandler(),
            valueFactory, getParserConfig(), getParseErrorListener());

    try {
      JsonLdProcessor.toRDF(o, callback);
    } catch (final JsonLdError e) {
      throw new RDFParseException("Could not parse JSONLD", e);
    } catch (final RuntimeException e) {
      if (e.getCause() != null && e.getCause() instanceof RDFParseException) {
        throw (RDFParseException) e.getCause();
      }
      throw e;
    }
  }

  public void parse(final List o, final String baseURI) throws IOException,
          RDFParseException, RDFHandlerException {
    dcatParse(o,baseURI);
  }
  public void parse(final Map o, final String baseURI) throws IOException,
          RDFParseException, RDFHandlerException {
    dcatParse(o,baseURI);
  }

}