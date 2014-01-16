package eu.lod2.hooks.constraints;

/**
 * Indicates a general tendency to be called early or late in the execution
 * order.
 * <p/>
 * Construction of priorities should be done through the Constraint class.
 *
 * @see eu.lod2.hooks.constraints.Constraint
 */
public class BroadPriority extends Priority {

    /** Handy pointer to an EARLY priority */
    public static BroadPriority EARLY = new BroadPriority(BroadPriority.Priority.EARLY);
    /** Handy pointer to an EARLY priority */
    public static BroadPriority LATE = new BroadPriority(BroadPriority.Priority.LATE);

    /**
     * Possible broad priority specifications.
     */
    public enum Priority {
        EARLY, LATE
    }

    /**
     * My priority.
     */
    public Priority priority = Priority.LATE;

    /**
     * Constructs a new priority.
     *
     * @param priority Value indicating whether we should execute EARLY or LATE.
     */
    public BroadPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Returns true iff we want to execute early.
     *
     * @return true on early priority.  false otherwise.
     */
    public boolean isEarly() {
        return priority == Priority.EARLY;
    }

    /**
     * Returns true iff we want to execute late.
     *
     * @return true on late priority, false otherwise.
     */
    @SuppressWarnings( {"UnusedDeclaration"} )
    public boolean isLate() {
        return priority == Priority.LATE;
    }
}
