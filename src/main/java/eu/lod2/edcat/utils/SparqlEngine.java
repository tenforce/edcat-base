package eu.lod2.edcat.utils;

import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JSONUtils;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import virtuoso.sesame2.driver.VirtuosoRepository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Class to take care of interactions with the sparql endpoint.
 * FIXME Currently does not reuse connections across sessions. We may have to look into this if application is not fast enough -> how to get session independent? Is this done automatically? Check with dominique
 * NOTE: (niels) I believe we can use something like http://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html if necessary
 */
public class SparqlEngine {
  private static final Logger log = LoggerFactory.getLogger(SparqlEngine.class);

  protected Repository repository;
  protected RepositoryConnection connection;


  public SparqlEngine() {
    this.readConfiguration();
  }

  public SparqlEngine(Repository repos) {
    try {
      this.repository = repos;
      if (!repos.isInitialized()) {
        repos.initialize();
      }
      this.connection = this.repository.getConnection();
    } catch (Exception e) {
      log.error("Could not establish connection to repository, error message: {}", e);
    }

  }

  public void readConfiguration() {
    Properties properties = new Properties();
    try {
      properties.load(SparqlEngine.class.getClassLoader().getResourceAsStream("sparql.properties"));
      String memoryStore = properties.getProperty("memoryStore");
      if ("1".equals(memoryStore)) {
        this.repository = new SailRepository(new MemoryStore());
        this.repository.initialize();
        this.connection = this.repository.getConnection();
      } else {
        String jDBCconnectionstring = properties.getProperty("JDBCconnection");
        String jDBCuser = properties.getProperty("JDBCuser");
        String jDBCpassword = properties.getProperty("JDBCpassword");

        this.repository = new VirtuosoRepository(jDBCconnectionstring, jDBCuser, jDBCpassword);
        this.repository.initialize();

        this.connection = this.repository.getConnection();
      }
    } catch (Exception e) {
      log.error("Could not establish connection to repository, error message: {}", e);
    }
  }

  public void addStatements(Model statements) {
      try {
          connection.add(statements);
      } catch (RepositoryException e) {
          throw new IllegalArgumentException(e);
      }
  }

  public QueryResult sparqlSelect(String query) throws IllegalArgumentException, IllegalStateException {
    try {
      TupleQueryResult result = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate();

      QueryResult results = new QueryResult();

      while (result.hasNext()) {
        BindingSet set = result.next();
        Map<String, String> currentRow = new HashMap<String, String>();
        for (String name : set.getBindingNames()) {
          currentRow.put(name, set.getValue(name).stringValue());
        }
        results.add(currentRow);
      }

      return results;
    } catch (RepositoryException e) {
      throw new IllegalStateException(e);
    } catch (MalformedQueryException e) {
      throw new IllegalArgumentException(e);
    } catch (QueryEvaluationException e) {
      throw new IllegalArgumentException(e);
    }

  }

  public void sparqlUpdate(String query) throws IllegalArgumentException, IllegalStateException {
    try {
      this.connection.prepareUpdate(QueryLanguage.SPARQL, query).execute();
    } catch (RepositoryException e) {
      throw new IllegalStateException(e);
    } catch (UpdateExecutionException e) {
      throw new IllegalStateException(e);
    } catch (MalformedQueryException e) {
      throw new IllegalArgumentException(e);
    }

  }

  /**
   * returns a fresh uri with the given base and the given kind (base/kind/random) that is not in the sparql endpoint of this engine
   */
  public String provideFreshUri(String base, String kind) {
    if (!base.endsWith("/")) {
      base += "/";
    }
    String start = base;
    if (kind != null) {
      if (!kind.endsWith("/")) {
        kind += "/";
      }
      start += kind;
    }

    try {
      TupleQueryResult result = connection.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?id where { BIND(bif:sequence_next(\""+start+"\") as ?id) }").evaluate();
      return start + result.next().getBinding("id").getValue().stringValue();
    } catch (Exception e) {
      try{
        // no bif:sequence_next available (e.g. sesame store used)? go for semi-randomness
        Random randgen = new Random();
        randgen.setSeed(new Date().getTime());

        String currentTry;
        do {
          currentTry = start + UUID.randomUUID();
          // race condition has a low chance to occur !!
        } while (hasUri(currentTry));

        return currentTry;
      } catch (Exception e2) {
        throw new IllegalStateException(e2);
      }
    }
  }

  //* returns a fresh uri with the given base that is not in the sparql endpoint of this engine
  public String provideFreshUri(String kind) {
    return provideFreshUri(Constants.getURIBase(), kind);
  }

  //* returns a fresh uri that is not in the sparql endpoint. The uri has the default base
  public String provideFreshUri() {
    return provideFreshUri(null);
  }

  //* whether or not the given uri is used in the sparql; endpoint
  public boolean hasUri(String uri) {
    try {
      StringBuilder builder = new StringBuilder();

      // this hack seems to circumvent the issue
      builder.append("SELECT * { { <").append(uri).append("> ?p ?o. Bind( <").append(uri).append("> as ?s ). } UNION ");
      builder.append("{ ?s ?p <" + uri + "> . Bind( <" + uri + "> as ?o ) . } UNION ");
      builder.append("{ ?s <" + uri + "> ?o . Bind( <" + uri + "> as ?p ) . } UNION ");
      builder.append("{ GRAPH <" + uri + "> {?s ?p ?o} }} LIMIT 2");

      return this.connection.prepareTupleQuery(QueryLanguage.SPARQL, builder.toString()).evaluate().hasNext();
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  //* frees resources
  public void terminate() {
    try {
      this.connection.close();
      this.repository.shutDown();
    } catch (Exception e) {
      log.error("Could not close repository, error message {}", e);
    }
  }

  public void clearGraph(String graph) {
    try {
      this.connection.clear(new URIImpl(graph));
    } catch (RepositoryException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Creates a temporary location to store the results of the given sparql construct in. Returns a string that can be
   * used as a pointer for the temporary storage. Any items in the temporary storage will always be included in a
   * graph with the string interpreted as a uri. This graph *MUST* be used when querying the engine with this storage.
   *
   * This allows complete separation of the logical split of the storages and their physical split
   *
   *
   * <b>NOTE The implementation of the sparqlengine will have to ensure that different instances of such a temporary
   * storage can coexist at the same time.</b>
   */
  public String createTemporaryStorage(String sparqlConstruct) throws Exception {
    int maxRetry = 10;
    int count = maxRetry;
    while (count > 0) {
      try {
        String graphURI = provideFreshUri("temporary-storage");
        this.connection.prepareGraphQuery(QueryLanguage.SPARQL, "CREATE GRAPH <" + graphURI + ">");
        //FIXME if we want a description that this is a temporary graph, maybe use copy instead.
        this.insertConstruct(sparqlConstruct, graphURI);
        return graphURI;
      } catch (Exception e) {
        log.warn("Could not create temporary storage, error: {}", e);
      }
      count--;
    }
    throw new IllegalStateException("Could not create temporary storage, gave up after " + maxRetry + " times.");
  }

  /**
   * Inserts the given construct query's result into the given graphURI
   */
  public void insertConstruct(String sparqlConstruct, String graphURI) throws Exception {
    sparqlConstruct = sparqlConstruct.trim();
    if (!sparqlConstruct.toLowerCase().startsWith("construct")) {
      throw new IllegalArgumentException("Could not handlePreCreate query, not a construct query: " + sparqlConstruct);
    }
    int firstWhere = sparqlConstruct.toLowerCase().indexOf("where");
    String sparqlInsert = "INSERT { GRAPH <" + graphURI + "> " + sparqlConstruct.substring(9, firstWhere) + " } " + sparqlConstruct.substring(firstWhere);
    this.connection.prepareGraphQuery(QueryLanguage.SPARQL, sparqlInsert).evaluate();
  }

  /**
   * Runs the given query over the normal store in combination with the temporary storage store
   * <p/>
   * This implementation of the sparql engine can simply use the standard sparql select, as the temporary storage is
   * simply a graph in the endpoint
   */
  public QueryResult sparqlSelect(String query, String tempStorage) {
    return sparqlSelect(query);
  }

  /**
   * Completely removes the temporary storage, returns true upon success.
   */
  public boolean removeTemporaryStorage(String tempStorageHandle) {
    try {
      this.connection.prepareGraphQuery(QueryLanguage.SPARQL, "DROP SILENT GRAPH <" + tempStorageHandle + ">").evaluate();
      return true;
    } catch (Exception e) {
      log.error("Could not remove temporary storage {}, error message: {}.", tempStorageHandle, e);
    }
    return false;
  }

  /**
   * Runs the given sparql construct query and returns a sparql engine over a new non-persistent memory store with only the results of the query
   */
  public SparqlEngine sparqlConstruct(String query) {
    try {
      GraphQueryResult result = this.connection.prepareGraphQuery(QueryLanguage.SPARQL, query).evaluate();
      SparqlEngine engine = new SparqlEngine(new SailRepository(new MemoryStore()));

      while (result.hasNext()) {
        Statement statement = result.next();
        try {
          engine.addStatement(statement);
        } catch (Exception e) {
          log.error("Could not add catalog statement {} to memory store for processing. Error message: {}", statement, e);
        }
      }

      return engine;
    } catch (RepositoryException e) {
      throw new IllegalStateException(e);
    } catch (MalformedQueryException e) {
      throw new IllegalArgumentException(e);
    } catch (QueryEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private void addStatement(Statement next) throws Exception {
    this.connection.add(next);
  }

  public String buildLanguageVariable(String datasetLangPredicate) {
    return "_".concat(datasetLangPredicate).concat("_Lang");
  }

  private Map<String, Map<String, Set<Value>>> statements;

  public Map<String, Map<String, Set<Value>>> getStatements() {
    if (this.statements != null) {
      return this.statements;
    }
    try {
      RepositoryResult<Statement> statements = this.connection.getStatements(null, null, null, true);
      Map<String, Map<String, Set<Value>>> result = new HashMap<String, Map<String, Set<Value>>>();
      while (statements.hasNext()) {
        Statement statement = statements.next();
        String subject = statement.getSubject().stringValue();
        Map<String, Set<Value>> predicates = result.get(subject);
        if (predicates == null) {
          predicates = new HashMap<String, Set<Value>>();
          result.put(subject, predicates);
        }
        String predicate = statement.getPredicate().stringValue();
        Set<Value> objects = predicates.get(predicate);
        if (objects == null) {
          objects = new HashSet<Value>();
          predicates.put(predicate, objects);
        }

        Value object = statement.getObject();
        objects.add(object);
      }
      this.statements = result;
      return result;
    } catch (Exception e) {
      log.error("Could not fetch statements from the current endpoint: {}. Error message: {}", this.repository, e);
      return new HashMap<String, Map<String, Set<Value>>>();
    }
  }

  public String getJsonLD(){
    try {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      org.openrdf.rio.RDFWriter writer = Rio.createWriter(RDFFormat.JSONLD, stream);

      connection.export(writer);
      String jsonLDString = IOUtils.toString(stream.toByteArray(), "UTF-8");
      Object jsonLD = JSONUtils.fromString(jsonLDString);
      Object jsonContext = JSONUtils.fromString(Constants.getJsonLdContext());
      Object compactedJsonLD = JsonLdProcessor.compact(jsonLD,jsonContext, new JsonLdOptions());

      return JSONUtils.toPrettyString(compactedJsonLD);
    } catch (Exception e){
      log.error("Could not export triples to JSONLD, error message {}",e);
      return null;
    }
  }

  /**
   * Returns the string value of the statement in the given statements list. Assumes single value
   */
  public static String stringValue(Map<String, Map<String, Set<Value>>> statements, String subject, String predicate) {
    try {
      return statements.get(subject).get(predicate).iterator().next().stringValue();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns the objects for the given subject and predicate as a list of strings
   */
  public static List<String> stringList(Map<String, Map<String, Set<Value>>> statements, String subject, String predicate) {
    ArrayList<String> result = new ArrayList<String>();
    for (Value value : statements.get(subject).get(predicate)) {
      result.add(value.stringValue());
    }
    return result;
  }

  /**
   * Returns the objects for the given subject and predicate as a langstring
   */
//  public static LangString langStringValue(Map<String, Map<String, Set<Value>>> statements, String subject, String predicate) {
//    LangString langString = null;
//    for (Value value : statements.get(subject).get(predicate)) {
//      try {
//        LiteralImpl literal = (LiteralImpl) value;
//        if (langString == null) {
//          langString = new LangString();
//        }
//        langString.translations.put(literal.getLanguage(), literal.getLabel());
//      } catch (Exception e) {
//        log.error("Incompatible type found in triple. Expected (internationalized) string, got: {} {} {}", subject, predicate, value);
//      }
//    }
//    return langString;
//  }

  public static Date dateValue(Map<String, Map<String, Set<Value>>> statements, String subject, String predicate) {
    Iterator<Value> iterator = statements.get(subject).get(predicate).iterator();
    if(!iterator.hasNext()){
      return null;
    }
    String dateString = iterator.next().stringValue();
    try {

      return Constants.parseDate(dateString);
    } catch (Exception e) {
//      log.error("Could not parse date {} from sparql result s: {} p: {} . Error: {}", dateString, subject, predicate, e);
      return null;
    }
  }

  public static Integer intValue(Map<String, Map<String, Set<Value>>> statements, String subject, String predicate) {
    Iterator<Value> iterator = statements.get(subject).get(predicate).iterator();
    if(!iterator.hasNext()){
      return null;
    }
    String intString = iterator.next().stringValue();
    try {
      return Integer.parseInt(intString);
    } catch (Exception e) {
//      log.error("Could not parse date {} from sparql result s: {} p: {} . Error: {}", intString, subject, predicate, e);
      return null;
    }
  }

  public void addJSONLD(InputStream stream, String baseURI, String graphName) throws IllegalArgumentException{
    try {
      // NOTE: cannot use direct upload as stream, need to parse first and then upload, otherwise graph contains no triples
      Model statements = Rio.parse(stream, baseURI, RDFFormat.JSONLD);
      this.connection.add(statements,new URIImpl(graphName));
    }catch (Exception e){
      throw new IllegalArgumentException(e);
    }
  }

  public void addJSONLD(String jsonld, String baseURI, String graphName) throws IllegalArgumentException {
    addJSONLD(IOUtils.toInputStream(jsonld),baseURI,graphName);
  }

  public String toJsonLD(String json) {
    try{
      String jsonAlmostStart = json.substring(json.indexOf("{")+1);
      InputStream stream = SparqlEngine.class.getClassLoader().getResourceAsStream("jsonld-context.json-ld");
      String jsonLD = "{ \"@context\" : ".concat(IOUtils.toString(stream)).concat(", \n").concat(jsonAlmostStart);

      return jsonLD;
    }catch (Exception e){
      log.error("Could not parse the json context, exception: {}",e);
      // cannot continue operation without this!
      throw new RuntimeException(e);
    }
  }

}
