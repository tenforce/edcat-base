package eu.lod2.query;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.testng.Assert;

/**
 * Simple test case for the Sparql builder.
 */
public class SparqlTest {
  @org.testng.annotations.Test
  public void testQuery() throws Exception {
    URI graphUri = new URIImpl( "http://lod2.tenforce.com/sparqlTest/graph" );
    String generated =
      Sparql.query( "" +
        "@PREFIX " +
        "SELECT ?foo, ?bar " +
        "FROM $graph " +
        "WHERE {" +
        "  $graph a dcat:Catalog; " +
        "         dcterms:Catalog ?foo. " +
        "  ?foo a $bar. " +
        "} ",
        "graph", graphUri,
        "bar" , "<http://lod2.tenforce.com/sparqlTest/bar>" );
    String manuallyBuilt =
      Sparql.getClassMapVariable( "PREFIX" ) +
        " SELECT ?foo, ?bar" +
        " FROM <" + graphUri.stringValue() + "> " +
        " WHERE {" +
        "   <" + graphUri.stringValue() + "> a dcat:Catalog;" +
        "          dcterms:Catalog ?foo." +
        "   ?foo a <http://lod2.tenforce.com/sparqlTest/bar>." +
        " }";
    Assert.assertEquals(
      generated.replaceAll( "\\s" , "" ),
      manuallyBuilt.replaceAll( "\\s", "" ));
  }
}
