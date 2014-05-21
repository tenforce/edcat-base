package eu.lod2.edcat.utils;

/**
 * Represents a simple Tuple containing two objects.
 *
 * note: this is highly opinionated Java which publishes the Tuple's variables directly.
 */
public class Tuple<Left,Right> {

  /** Contains the left of the tuple */
  public Left left;

  /** Contains the right of the tuple */
  public Right right;

  /**
   * Constructs a new Tuple.
   *
   * @param left  The left node of the tuple.
   * @param right The right node of the tuple.
   */
  public Tuple(Left left, Right right){
    this.left = left;
    this.right = right;
  }
}
