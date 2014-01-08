package eu.lod2.edcat.hooks.constraints;

public class Constraint {

  public static Priority EARLY = new BroadPriority(BroadPriority.EARLY);
  public static Priority LATE = new BroadPriority(BroadPriority.LATE);

  public static Priority after(String pluginName) {
    return new RelativePriority(RelativePriority.AFTER,pluginName);
  }

  public static Priority before(String pluginName) {
    return new RelativePriority(RelativePriority.BEFORE,pluginName);
  }

  public static Priority after(Class plugin) {
    return after(plugin.getCanonicalName());
  }

  public static Priority before(Class plugin) {
    return before(plugin.getCanonicalName());
  }

}
