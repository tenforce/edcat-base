package eu.lod2.edcat.utils;

import org.openrdf.model.*;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

import java.util.UUID;

/**
 * This class helps in converting blank nodes in a Model to URIs.
 */
public class BlankNodeNuker {

  /** Contains the model in which we will nuke the blank nodes. */
  private Model model;

  /** JsonLdContext for translating predicates */
  private JsonLdContext ldContext;

  /**
   * Constructs a new BlankNodeNuker
   *
   * @param model Contains all statements in which we will try to nuke the blank nodes.
   * @param kind  Contextual kind based on which the nodes will be nuked.  This calculates the
   *              paths.
   */
  private BlankNodeNuker( Model model, JsonLdContext.Kind kind ) {
    this.model = model;
    this.ldContext = new JsonLdContext( kind );
  }

  /**
   * Nukes the recognized blank nodes in {@code model}, converting them to resources instead.
   *
   * @param model Model of which we will nuke the blank nodes.
   */
  public static void nuke( Model model , JsonLdContext.Kind kind ) {
    (new BlankNodeNuker( model , kind )).nukeBlankNodes();
  }

  /**
   * Nukes the understood blank nodes in {@link BlankNodeNuker#model} and translates them to URIs.
   */
  private void nukeBlankNodes() {
    // until we have changes
    int changes;
    do {
      changes = 0;
      // walk over each triple
      Model walkingModel = new LinkedHashModel( model );
      for ( Statement s : walkingModel ) {
        walkingModel = new LinkedHashModel( model ); // reinitialize model, some triples may have changed during this step
        Resource target = s.getSubject();
        // if the triple is a blank node
        if ( target instanceof BNode
            && model.contains( s.getSubject(), s.getPredicate(), s.getObject() ) ) {
          // find a connection which refers to our blank node through a predicate known by the jsonLdContext
          for ( Statement nameConnection : walkingModel.filter( null, null, s.getSubject() ) ) {
            Resource connectingSubject = nameConnection.getSubject();
            URI connectingPredicate = nameConnection.getPredicate();
            if ( ldContext.getReverseKeywordMap().containsKey( connectingPredicate.stringValue() )
                && connectingSubject instanceof URI ) {
              // build the new URI
              URI newTarget = new URIImpl( ""
                  + connectingSubject.stringValue() + "/"
                  + ldContext.getReverseKeywordMap().get( connectingPredicate.stringValue() ) + "/"
                  + UUID.randomUUID() );
              // replace the triples which have the blank node as subject or as object.
              for ( Statement changeMySubject : walkingModel.filter( target, null, null ) ) {
                model.remove( changeMySubject );
                model.add( new StatementImpl( newTarget, changeMySubject.getPredicate(), changeMySubject.getObject() ) );
              }
              for ( Statement changeMyObject : walkingModel.filter( null, null, target ) ) {
                model.remove( changeMyObject );
                model.add( new StatementImpl( changeMyObject.getSubject(), changeMyObject.getPredicate(), newTarget ) );
              }
              // indicate something changed
              changes++;
            }
          }
        }
      }
    } while ( changes > 0 );
  }
}