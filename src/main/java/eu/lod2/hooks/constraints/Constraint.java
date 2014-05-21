package eu.lod2.hooks.constraints;

/**
 * The Constraint class is a factory for the definition of priorities on hooks.
 *
 * Constraints are generally supplied as a Collection of Priority objects. Each
 * of the Priority objects can be constructed through this factory.
 *
 * Some examples of constraints are:
 *
 * Constraint.after("com.bar.Foo") indicates that we want to run after "com.bar.foo"
 * but not depend on it.
 *
 * Constraint.before(com.bar.Foo) indicates we want to run before the plugin
 * com.bar.Foo and require it to be loaded.
 *
 * Constraint.EARLY indicates a general tendency to be executed earlier on, rather than later.
 */
public abstract class Constraint {

    /** Try to execute this EARLY in the tree */
    public static Priority EARLY = BroadPriority.EARLY;
    /** Try to execute this LATE in the tree */
    public static Priority LATE = BroadPriority.LATE;

    /**
     * Execute this after the given plugin, if the given plugin is to be called.
     *
     * Use this method if you don't want to depend on the plugin being loaded.  Otherwise
     * use {@link Constraint#after(Class)}
     *
     * @see Constraint#after(Class)
     * @param pluginName Canonical name of the plugin which should be called before us.
     * @return Priority object representing the constraint.
     */
    public static Priority after(String pluginName) {
        return new RelativePriority(RelativePriority.Relation.AFTER, pluginName);
    }

    /**
     * Build a constraint so we execute before the supplied plugin, if said plugin is
     * to be called.
     *
     * Use this method if you don't want to depend on the plugin being loaded.  Otherwise
     * use {@link Constraint#before(Class)}.
     *
     * @see Constraint#before(Class)
     * @param pluginName Canonical name of the plugin before which we want to be called.
     * @return Priority object representing the constraint.
     */
    public static Priority before(String pluginName) {
        return new RelativePriority(RelativePriority.Relation.BEFORE, pluginName);
    }

    /**
     * Same as @{Constraint#after(String)} but supplies the plugin as a class instead of as a String.
     * This offers the same functionality, but it guarantees plugin to be loaded.
     *
     * @param plugin Class representing the plugin to be loaded.  See the documentation
     *               of the dependent plugin for the correct class.
     * @see Constraint#after(String)
     */
    public static Priority after(Class plugin) {
        return after(plugin.getCanonicalName());
    }

    /**
     * Same as @{Constraint#before(String)} but supplies the plugin as a class instead of as a String.
     * This offers the same functionality, but it guarantees the plugin to have been loaded.
     *
     * @param plugin Class representing the plugin to be loaded.  See the documentation
     *               of the dependent plugin for the correct class.
     * @see Constraint#before(String)
     */
    public static Priority before(Class plugin) {
        return before(plugin.getCanonicalName());
    }

}
