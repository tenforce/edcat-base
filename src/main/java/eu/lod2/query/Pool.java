package eu.lod2.query;

import java.util.ArrayList;

/**
 * Dumb pool which never destructs objects.
 */
public abstract class Pool<T> {

  /** Contains the elements of the pool */
  private ArrayList<T> pool = new ArrayList();

  /**
   * Retrieves an object from the pool or creates one if one didn't exist yet.
   *
   * @return Element from the Pool.
   */
  public synchronized T retrieve(){
    if( pool.isEmpty() )
      return buildPooledObject();
    else {
      T element = pool.get(0);
      pool.remove(0);
      return element;
    }
  }

  /**
   * Returns an element to the pool.
   *
   * @param element Element to return.
   */
  public synchronized void release(T element){
    pool.add( element );
  }

  /**
   * Constructs a new object for the pool.
   *
   * @return Object which may be stored in and retrieved from the Pool.
   */
  abstract T buildPooledObject();
}
