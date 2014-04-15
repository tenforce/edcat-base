package eu.lod2.edcat.controller.distribution;

import eu.lod2.edcat.controller.BaseController;
import eu.lod2.edcat.utils.*;
import eu.lod2.query.Db;
import eu.lod2.query.Sparql;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

public class DistributionController  extends BaseController {
  /** Kind of object used by the JsonLdContext. */
  static JsonLdContext.Kind kind = JsonLdContext.Kind.Dataset;

  /** JsonLdContext on which the controllers operate. */
  JsonLdContext jsonLdContext = new JsonLdContext( kind );

  // --- ROUTING

  /** list datasets route */
  protected static final String LIST_ROUTE = DcatURI.DISTRIBUTION_LIST_PATH;

  /** show dataset route */
  protected static final String OBJECT_ROUTE = DcatURI.DISTRIBUTION_OBJECT_PATH;

  protected void verifyDatasetExists(URI dataset) throws NotFoundException {
    if (!datasetExists(dataset))
      throw new NotFoundException("could not find dataset " + dataset);
  }
  protected boolean datasetExists(URI dataset) {
    // use getStatements for now, hasStatement has a bug and always returns false
    return Db.getStatements(dataset, RDF.TYPE, Sparql.namespaced("dcat","Dataset"),true,dataset).size() == 1;
  }

  protected Model buildModel( HttpServletRequest request, URI distribution ) throws Exception {
    InputStream in = request.getInputStream();
    Model statements = DcatJsonParser.jsonLDToStatements(
        in, new JsonLdContext(kind),
        distribution, Sparql.namespaced("dcat", "Distribution"));
    BlankNodeNuker.nuke(statements, kind);
    in.close();
    return statements;
  }

}
