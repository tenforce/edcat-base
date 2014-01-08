package eu.lod2.edcat.hooks.constraints.graph;

import eu.lod2.edcat.hooks.handlers.HookHandler;

import java.util.*;

/**
 * A node abstracts out information regarding the ordering of HookHandlers.  It is used
 * by the NodeSet to cleanly access the ordering information.
 */
public class Node<HookHandler> {

    /** The hook we are abstracting */
    private HookHandler handler;

    /** Nodes which have explicitly been specified to be executed after this node */
    private Set<Node> directlyAfterSelf = new HashSet<Node>();

    /** Nodes which have explicitly been specified to be executed before this node */
    private Set<Node> directlyBeforeSelf = new HashSet<Node>();

    /** Nodes which have implicitly been specified to be executed after this node (in a single step) */
    private Set<Node> singleStepAfterSelf = new HashSet<Node>();

    /** Nodes which have implicitly been specified to be executed before this node (in a single step) */
    private Set<Node> singleStepBeforeSelf = new HashSet<Node>();

    /** Nodes which have implicitly been specified to be executed after this node (any amount of steps) */
    private Set<Node> implicitAfterSelf = new HashSet<Node>();

    /** Nodes which have implicitly been specified to be executed before this node (any amount of steps) */
    private Set<Node> implicitBeforeSelf = new HashSet<Node>();


    /**
     * Construct a new Node.
     * <p/>
     * Does not interpret the order of the node.
     *
     * @param handler The handler to fetch the ordering information for
     */
    public Node(HookHandler handler) {
        this.handler = handler;
    }

    /**
     * Retrieves the handler which this Node describes
     */
    public HookHandler getHandler() {
        return this.handler;
    }

    /**
     * Indicate that this node should be executed after attachment.
     * <p/>
     * The syntax node.after(attachment) essentially says that node should be executed *after*
     * the supplied attachment. Hence, internally, it adds itself to the internal variable
     * #directlyBeforeSelf and related variables of attachment.
     *
     * @param attachment Node after which we should execute.
     */
    public void after(Node attachment) {
        attachment.addDirectlyAfter(this);
        attachment.addSingleStepAfter(this);
        attachment.addImplicitAfter(this);
    }

    /**
     * Indicate that this node should be executed before attachment.
     * <p/>
     * The syntax node.after(attachment) essentially says that node should be executed *before*
     * the supplied attachment. Hence, internally, it adds itself to the internal variable
     * #directlyAfterSelf and related variables of attachment.
     *
     * @param attachment Node before which we should execute.
     */
    public void before(Node attachment) {
        attachment.addDirectlyBefore(this);
    }

    /**
     * Indicate that *other* should be executed directly after *this*.
     * <p/>
     * This is the inverse syntax of what #after offers you, however it keeps the external
     * API easier to understand.
     *
     * @param other Node which should be executed after this.
     */
    private void addDirectlyAfter(Node other) {
        directlyAfterSelf.add(other);
    }

    /**
     * Indicate that *other* should be executed directly before *this*.
     * <p/>
     * This is the inverse syntax of what #before offers you, however it keeps the external
     * API easier to understand.
     *
     * @param other Node which should be executed before this.
     */
    private void addDirectlyBefore(Node other) {
        directlyBeforeSelf.add(other);

    }

    /**
     * Indicate that *other* should be executed *after* this.  Also implying that other comes *before* this.
     *
     * @param other Node which should be executed after this.
     */
    private void addSingleStepAfter(Node other) {
        singleStepAfterSelf.add(other);
        other.singleStepBeforeSelf.add(this);
    }

    /**
     * Indicate that *other* should be executed *before* this.  Also implying that other comes *after* this.
     *
     * @param other Node which should be executed before this.
     */
    private void addSingleStepBefore(Node other) {
        singleStepBeforeSelf.add(other);
        other.singleStepAfterSelf.add(this);
    }

    /**
     * Indicate that *other* should be executed *after* self, on any distance.
     *
     * @param other Node which should be executed after this.
     */
    private void addImplicitAfter(Node other) {
        this.addImplicitAfterDirectional(other);
        other.addImplicitBeforeDirectional(this);
    }

    /**
     * Indicate that *other* should be executed *before* self, on any distance.
     *
     * @param other Node which should be executed after this.
     */
    private void addImplicitBefore(Node other) {
        this.addImplicitBeforeDirectional(other);
        other.addImplicitAfterDirectional(this);
    }

    /**
     * Indicate that *other* should be executed *after* self, on any distance, but only in this direction.
     *
     * @param other Node which should be executed after this.
     */
    private void addImplicitAfterDirectional(Node other) {
        implicitAfterSelf.add(other);
        for (Node beforeSelf : implicitBeforeSelf)
            beforeSelf.implicitAfterSelf.add(other);
    }

    /**
     * Indicate that *other* should be executed *before* self, on any distance, but only in this direction.
     *
     * @param other Node which should be executed before self.
     */
    private void addImplicitBeforeDirectional(Node other) {
        implicitBeforeSelf.add(other);
        for (Node afterSelf : implicitAfterSelf)
            afterSelf.implicitBeforeSelf.add(other);
    }
}