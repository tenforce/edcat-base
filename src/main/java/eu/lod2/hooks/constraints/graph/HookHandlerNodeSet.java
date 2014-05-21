package eu.lod2.hooks.constraints.graph;

import eu.lod2.hooks.constraints.Priority;
import eu.lod2.hooks.constraints.BroadPriority;
import eu.lod2.hooks.constraints.RelativePriority;
import eu.lod2.hooks.handlers.HookHandler;

import java.util.*;

/**
 * NodeSet implementation for the execution order of HookHandler instances.
 */
public class HookHandlerNodeSet extends NodeSet<HookHandler>{

  /** Canonical name of the hook we are finding the execution order for. */
  private String hookName;

  /**
   * Constructs a new HookHandlerNodeSet.
   *
   * @param hookHandlers Collection of all HookHandler instances in the graph.
   * @param hookName Name of the hook which will be called.
   */
  public HookHandlerNodeSet( Collection<HookHandler> hookHandlers, String hookName ){
    for(HookHandler handler : hookHandlers )
      add(handler);
    this.hookName = hookName;
  }

  @Override
  public Collection<HookHandler> hookExecutesBefore(HookHandler hookHandler) {
    return hookExecutesRelativeTo(hookHandler, RelativePriority.Relation.BEFORE);
  }

  @Override
  public Collection<HookHandler> hookExecutesAfter(HookHandler hookHandler) {
    return hookExecutesRelativeTo(hookHandler, RelativePriority.Relation.AFTER);
  }

  @Override
  public Node.SchedulingPreference hookSchedulingPreference(HookHandler hook) {
    Collection<Priority> constraints = hook.getConstraints(hookName);
    if( constraints == null )
      return Node.SchedulingPreference.LATE;

    // for loop is the normal case, the rest is returning LATE by default
    for(Priority priority : constraints)
      if( priority instanceof BroadPriority) {
        if( ((BroadPriority) priority).isEarly())
          return Node.SchedulingPreference.EARLY;
        else
          return Node.SchedulingPreference.LATE;
      }

    return Node.SchedulingPreference.LATE;
  }

  /**
   * Abstraction of hookExecutesBefore and hookExecutesAfter.
   *
   * @param hookHandler HookHandler for which we want to find the relative priorities
   * @param relation Type of relation for which to find the relative priorities
   * @return Collection of HookHandler instances which may directly be returned by
   *         hookExecutesBefore and hookExecutesAfter.
   */
  private Collection<HookHandler> hookExecutesRelativeTo(HookHandler hookHandler, RelativePriority.Relation relation){
    // for each BEFORE relative priority
    //   return each hook which responds to the relative priority's target
    Collection<HookHandler> beforeHooks = new ArrayList<HookHandler>();
    for(RelativePriority p : relativePrioritiesFor(hookHandler))
      if(p.getPriority() == relation)
        for(HookHandler beforeMe : hookHandlersByCanonicalName(p.getTarget()))
          beforeHooks.add(beforeMe);
    return beforeHooks;
  }

  /**
   * Returns the relative priorities for this.hookName which are attached to hookHandler
   *
   * @param hookHandler hookHandler for which we want to find the relative priorities
   * @return Collection of relative priorities
   */
  private Collection<RelativePriority> relativePrioritiesFor(HookHandler hookHandler){
    List<RelativePriority> priorities = new ArrayList<RelativePriority>();

    Collection<Priority> priorityObjects = hookHandler.getConstraints(hookName);
    if(priorityObjects != null)
      for(Priority p : hookHandler.getConstraints(hookName))
        if(p instanceof RelativePriority)
          priorities.add((RelativePriority) p);

    return priorities;
  }


  /**
   * Retrieves all HookHandler instances which have *name* a their canonical class name.
   * This is used to dereference the RelativePriority constraints.
   *
   * @param name Canonical class name of the referred HookHandlers
   * @return Collection of HookHandlers which have the canonical name *name*.
   */
  private Collection<HookHandler> hookHandlersByCanonicalName(String name){
    Set<HookHandler> foundHandlers = new HashSet<HookHandler>();
    for(HookHandler handler : handlers)
      if( handler.getClass().getCanonicalName().equals(name))
        foundHandlers.add(handler);
    return foundHandlers;
  }
}
