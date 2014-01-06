package com.github.jsonldjava.sesame;

import org.apache.commons.io.Charsets;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParserFactory;

public class DcatSesameJSONLDParserFactory implements RDFParserFactory {
  public final static RDFFormat DCATJSONLD = new RDFFormat("dcatjsonld", "application/json", Charsets.UTF_8, ".jsonld", true, true);

  @Override
  public RDFFormat getRDFFormat() {
    return DCATJSONLD;
  }

  @Override
  public RDFParser getParser() {
    return new DcatSesameJSONLDParser();
  }

}
