package eu.lod2.hooks.constraints;

/**
 * Construction of priorities should be done through the Constraint class.
 *
 * @see eu.lod2.hooks.constraints.Constraint
 */
public class RelativePriority extends Priority {

    /**
     * We may either execute BEFORE or AFTER another plugin.
     */
    public enum Relation {
        BEFORE, AFTER
    }

    /** My priority kind */
    private Relation priority;

    /** The canonical name of the plugin before/after which we should execute */
    private String targetPlugin;

    /**
     * Constructs a new RelativePriority, supplying the kind of constraint and
     * relative to which plugin it is.
     *
     * @param priority Whether we want to run before or after the target plugin.
     * @param targetPlugin Plugin before/after which we want to run.
     */
    public RelativePriority(Relation priority, String targetPlugin) {
        this.priority = priority;
        this.targetPlugin = targetPlugin;
    }

    /**
     * Returns whether or not we want to run before/after the target.
     *
     * @return Value of the {@link eu.lod2.hooks.constraints.RelativePriority.Relation} enum
     */
    public Relation getPriority() {
        return priority;
    }

    /**
     * Returns the target before/after which we want to run, as a string containing the
     * canonical name of its class.
     *
     * @return Canonical name of the target plugin.
     */
    public String getTarget() {
        return targetPlugin;
    }
}
