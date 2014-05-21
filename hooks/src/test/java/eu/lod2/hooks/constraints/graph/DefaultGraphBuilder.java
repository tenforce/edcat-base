package eu.lod2.hooks.constraints.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is a helper for constructing graphs.
 *
 * Most of its functionality corresponds to the factory pattern.
 */
public class DefaultGraphBuilder {

    /**
     * Three-element cycled set using AFTER
     */
    public static StringNodeSet buildCycledSetA() throws InvalidNodeStringException {
        StringNodeSet set = new StringNodeSet();
        set.add("q->r->");
        set.add("r->t->");
        set.add("t->q->");
        return set;
    }

    /**
     * Three-element cycled set using BEFORE
     */
    public static StringNodeSet buildCycledSetB() throws InvalidNodeStringException {
        StringNodeSet set = new StringNodeSet();
        set.add("->r->t");
        set.add("->t->q");
        set.add("->q->r");
        return set;
    }

    /**
     * Three-element cycled set using mixed BEFORE/AFTER
     */
    public static StringNodeSet buildCycledSetC() throws InvalidNodeStringException {
        StringNodeSet set = new StringNodeSet();
        set.add("t->q->r");
        set.add("r->t-> ");
        set.add(" ->r-> ");
        return set;
    }

    /**
     * All cycled set examples
     */
    public static Collection<StringNodeSet> buildCycledSets() throws InvalidNodeStringException {
        List<StringNodeSet> stringNodeSets = new ArrayList<StringNodeSet>();
        stringNodeSets.add(buildCycledSetA());
        stringNodeSets.add(buildCycledSetB());
        stringNodeSets.add(buildCycledSetC());

        return stringNodeSets;
    }

    /**
     * A bigger non-cycled set
     */
    public static StringNodeSet buildLongSet() throws InvalidNodeStringException {
        StringNodeSet set = new StringNodeSet();
        injectLongSet(set);
        return set;
    }

    /**
     * Injects the long set in the StringNodeSet
     *
     * @param set StringNodeSet to inject the long set into
     */
    public static void injectLongSet(StringNodeSet set) throws InvalidNodeStringException {
        set.add("b,q->a->");
        set.add("   ->b->");
        set.add("   ->c->a");
        set.add("  b->d->");
        set.add("   ->e->b");
        set.add("   ->f->c");
        set.add("  c->q->");
    }

    /**
     * A set for checking the references
     */
    public static StringNodeSet buildReferencingSet() throws InvalidNodeStringException {
        StringNodeSet set = new StringNodeSet();
        injectReferencingSet(set);
        return set;
    }

    /**
     * Inject the referencing set into the supplied set
     *
     * @param set StringNodeSet to inject the referencing set into
     */
    public static void injectReferencingSet(StringNodeSet set) throws InvalidNodeStringException {
        set.add("r,s->t->u,v");
        set.add("->r->s");
        set.add("->+s->");
        set.add("v->u->");
        set.add("->v->");
    }
}
