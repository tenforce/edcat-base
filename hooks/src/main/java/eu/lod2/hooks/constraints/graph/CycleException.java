package eu.lod2.hooks.constraints.graph;

/**
 * This exception indicates that there's a cycle in the supplied NodeSet.
 */
public class CycleException extends Exception {
  public CycleException() {
    super("Cycle found in graph");
  }
}
