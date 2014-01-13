package eu.lod2.edcat.utils;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

import java.util.Date;

/**
 * Service class to handle catalog provenance
 */
public class Catalog {
  private SparqlEngine engine;
  private URI catalogUri;


  public Catalog(SparqlEngine engine, String catalogUri) {
    this.engine = engine;
    this.catalogUri = new URIImpl(catalogUri);
  }

  public URI generateDatasetUri(String datasetId) {
    return buildURI(catalogUri.stringValue(),"dataset",datasetId);
  }

  public URI generateRecordUri(String datasetId) {
    return buildURI(catalogUri.stringValue(),"record",datasetId);  }

  public Model insertDataset(String datasetId) {
    Model statements = new LinkedHashModel();
    URI dataset = generateDatasetUri(datasetId);
    URI record = generateRecordUri(datasetId);
    Literal now = ValueFactoryImpl.getInstance().createLiteral(new Date());
    statements.add(record, DCTERMS.MODIFIED, now);
    statements.add(record, DCTERMS.ISSUED, now);
    statements.add(record, RDF.TYPE, Vocabulary.get("Record"));
    statements.add(record, Vocabulary.get("record.primaryTopic"), dataset);
    statements.add(catalogUri, Vocabulary.get("catalog.dataset"), dataset);
    statements.add(catalogUri, Vocabulary.get("catalog.record"), record);
    engine.addStatements(statements, catalogUri);
    return statements;
  }

  public Model getRecord(String datasetId) {
    return engine.getStatements(generateRecordUri(datasetId), null, null, true, catalogUri);
  }

  public Model updateDataset(String datasetId) {
    URI record = generateRecordUri(datasetId);
    engine.sparqlUpdate("DELETE WHERE {GRAPH <" + catalogUri + "> { <" + record + "> <" + DCTERMS.MODIFIED + "> ?modified }}");
    Model statements = new LinkedHashModel();
    Literal now = ValueFactoryImpl.getInstance().createLiteral(new Date());
    statements.add(record, DCTERMS.MODIFIED, now, catalogUri);
    engine.addStatements(statements, catalogUri);
    return engine.getStatements(record, null, null, true, catalogUri);
  }

  public void removeDataset(String datasetId) {
    StringBuilder builder = new StringBuilder();
    builder.append("DELETE WHERE {GRAPH <" + catalogUri + ">");
    builder.append("{<" + generateRecordUri(datasetId) + "> ?p ?o. OPTIONAL{?o ?op ?oo}}");
    builder.append("}");
    engine.sparqlUpdate(builder.toString());
  }

  private URI buildURI(String base, String sub,String id) {
    return new URIImpl(concatWithSlash(concatWithSlash(base,sub),id));
  }

  private String concatWithSlash(String pref,String end) {
    if (pref.endsWith("/"))
            return pref + end;
    else
      return pref + '/' + end;
  }


}
