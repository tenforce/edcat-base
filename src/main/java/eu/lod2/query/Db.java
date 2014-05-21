package eu.lod2.query;

import eu.lod2.edcat.utils.NotFoundException;
import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.edcat.utils.SparqlEngine;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a pooled connection to the database.  It allows you to query/insert the database
 * directly, and allows you to fetch and return SparqlEngine objects if necessary.
 */
public class Db {

  // --- POOL OPERATIONS

  /**
   * @see eu.lod2.query.Pool#retrieve()
   */
  public static SparqlEngine retrieve() {
    return singleton.retrieve();
  }

  /**
   * @see Pool#release(Object)
   */
  public static void release( SparqlEngine engine ) {
    singleton.release( engine );
  }


  // --- DATABASE OPERATIONS
  //
  // if you can't find the necessary operation, get hold of a SparqlEngine through #retrieve() and
  // #release(Object) if you need more specific custom functionality of a SparqlEngine.

  /**
   * Executes a query on an Engine.
   * <p/>
   * The query is built using {@link Sparql#query(String, Object...)}.
   * The built query is executed using {@link SparqlEngine#sparqlSelect(String)}.
   *
   * @param query Query as sent to Sparql#query.
   * @param args  Arguments supplied to Sparql#query.
   * @return QueryResult from executing the supplied query.
   */
  public static QueryResult query( String query, Object... args ) {
    SparqlEngine engine = singleton.retrieve();
    try {
      return engine.sparqlSelect(Sparql.query(query, args));
    } finally {
      singleton.release( engine );
    }
  }

  /**
   * Performs a SPARQL query on the engine and returns the raw BindingSets.
   * <p/>
   * The query is built using {@link Sparql#query(String, Object...)}.
   * The built query is executed using {@link SparqlEngine#sparqlRawSelect(String)}.
   *
   * @param query SPARQL query template.
   * @param args  SPARQL query template parameters.
   * @return List containing one BindingSet per match.
   * @see eu.lod2.edcat.utils.SparqlEngine#sparqlRawSelect(String)
   */
  public static List<BindingSet> rawQuery( String query, Object... args ) {
    SparqlEngine engine = singleton.retrieve();
    try {
      return engine.sparqlRawSelect(Sparql.query(query, args));
    } finally {
      singleton.release( engine );
    }
  }

  /**
   * Performs a SPARQL construct query on the engine and returns the resulting statements.
   * @param query SPARQL query template.
   * @param args  SPARQL query template parameters.
   * @return Model statements
   */
  public static Model graphQuery(String query,Object... args) {
    SparqlEngine engine = singleton.retrieve();
    try {
      return engine.sparqlGraphQuery(Sparql.query(query, args));
    } finally {
      singleton.release( engine );
    }
  }

  public static boolean hasStatement(Resource subject,URI predicate,Value value,Resource...contexts) {
    SparqlEngine engine = singleton.retrieve();
    try{
      return engine.hasStatement(subject,predicate,value,false,contexts);
    } finally {
      singleton.release(engine);
    }
  }


  /**
   * Performs a SPARQL construct on the engine and returns a Model containing the statements.
   * <p/>
   * The query is built using {@link Sparql#query(String, Object...)}.
   * The built query is executed using {@link SparqlEngine#sparqlModelConstruct(String)}.
   *
   * @param query SPARQL query template.
   * @param args  SPARQL query template parameters.
   * @return Model containing the resulting triples.
   */
  public static Model construct( String query, Object... args ) {
    SparqlEngine engine = singleton.retrieve();
    try {
      return engine.sparqlModelConstruct(Sparql.query(query, args));
    } finally {
      singleton.release( engine );
    }
  }

  /**
   * Executes a Sparql update query.
   * <p/>
   * The query is built using {@link Sparql#query(String, Object...)}.
   * The built query is executed using {@link SparqlEngine#sparqlSelect(String)}.
   *
   * @param query Query as sent to Sparql#query.
   * @param args  Arguments supplied to Sparql#query.
   */
  public static void update( String query, Object... args ) {
    SparqlEngine engine = singleton.retrieve();
    try {
      engine.sparqlUpdate(Sparql.query(query, args));
    } finally {
      singleton.release( engine );
    }
  }

  /**
   * Adds a set of statements to the triple store.
   * <p/>
   * Dispatches to {@link SparqlEngine#addStatements(org.openrdf.model.Model,
   * org.openrdf.model.Resource...)}
   */
  public static void add( Model statements, Resource... contexts ) {
    SparqlEngine engine = singleton.retrieve();
    try {
      engine.addStatements(statements, contexts);
    } finally {
      singleton.release( engine );
    }
  }

  /**
   * Clears a graph in the store.
   * <p/>
   * Dispatches to {@link SparqlEngine#clearGraph(org.openrdf.model.URI)}.
   *
   * @param graph
   */
  public static void clearGraph( URI graph ) {
    SparqlEngine engine = singleton.retrieve();
    try {
      engine.clearGraph( graph );
    } finally {
      singleton.release( engine );
    }
  }

  /**
   * Retrieves statements from the database.
   * <p/>
   * Dispatches to {@link eu.lod2.edcat.utils.SparqlEngine#getStatements()}.
   */
  public static Map<String, Map<String, Set<Value>>> getStatements() {
    SparqlEngine engine = singleton.retrieve();
    try {
      return engine.getStatements();
    } finally {
      singleton.release( engine );
    }
  }

  /**
   * Retrieves the statements from the database.
   * <p/>
   * Dispatches to {@link eu.lod2.edcat.utils.SparqlEngine#getStatements(org.openrdf.model.Resource,
   * org.openrdf.model.URI, org.openrdf.model.Value, boolean, org.openrdf.model.Resource...)}.
   */
  public static Model getStatements( Resource subject, URI predicate, Value value, boolean includeInferred, Resource... contexts ) {
    SparqlEngine engine = singleton.retrieve();
    try {
      return engine.getStatements( subject, predicate, value, includeInferred, contexts );
    } finally {
      singleton.release( engine );
    }
  }

  /**
   * Retrieves statements from the database.
   * <p/>
   * Dispatches to {@link eu.lod2.edcat.utils.SparqlEngine#getStatements(org.openrdf.model.Resource...)}.
   * @throws NotFoundException if not statements are found in specified contexts
   */
  public static Model getStatements( Resource resource ) throws NotFoundException {
    SparqlEngine engine = singleton.retrieve();
    try {
      Model statements = engine.getStatements( resource );
      if (statements.size() == 0)
        throw new NotFoundException();
      return statements;
    } finally {
      singleton.release( engine );
    }
  }

  // --- POOL SUPPORT IMPLEMENTATION

  /** Singleton database on which requests can be made. */
  public static Pool<SparqlEngine> singleton = new Pool<SparqlEngine>() {
    @Override
    SparqlEngine buildPooledObject() {
      return new SparqlEngine();
    }
  };

  /** Empty constructor */
  private Db() { super(); }
}
