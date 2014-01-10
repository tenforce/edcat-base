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
    return new URIImpl(catalogUri + "/dataset/" + datasetId); // TODO fix this
  }

  public URI generateRecordUri(String datasetId) {
    return new URIImpl(catalogUri + "/record/" + datasetId); // TODO fix this
  }

  public URI insertDataset(String datasetId) {
    Model statements = new LinkedHashModel();
    URI dataset = generateDatasetUri(datasetId);
    URI record = generateRecordUri(datasetId);
    Literal now = ValueFactoryImpl.getInstance().createLiteral(new Date());
    statements.add(record, DCTERMS.MODIFIED, now, catalogUri);
    statements.add(record, DCTERMS.ISSUED, now, catalogUri);
    statements.add(record, RDF.TYPE, Vocabulary.get("Record"));
    statements.add(record, Vocabulary.get("record.primaryTopic"), dataset, catalogUri);
    statements.add(catalogUri, Vocabulary.get("catalog.dataset"), dataset);
    statements.add(catalogUri, Vocabulary.get("catalog.record"), record);
    engine.addStatements(statements);
    return dataset;
  }

  public void updateDataset(String datasetId) {
    // TODO: this is a stub, update record modified in catalog

  }

  public void removeDataset(String datasetId) {
    // TODO: this is a stub, delete record/dataset links from catalog
  }


}
