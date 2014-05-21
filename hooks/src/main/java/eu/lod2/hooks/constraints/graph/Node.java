package eu.lod2.hooks.constraints.graph;

import java.util.*;

/**
 * A node abstracts out information regarding the ordering of HookHandlers.  It is used
 * by the NodeSet to cleanly access the ordering information.
 */
public class Node<HookHandler> {

    /** The hook we are abstracting */
    private HookHandler handler;

    /** Hooks have a broad scheduling preference which is indicated by these keywords */
    public enum SchedulingPreference {
        EARLY, LATE
    }

    /** Contains the current schedulingPreference, defaulting to LATE */
    private SchedulingPreference schedulingPreference = SchedulingPreference.LATE;

    /** Contains the nodes of which I specified that I would run before them */
    private Set<Node<HookHandler>> thisExplicitlyRunsBefore = new HashSet<Node<HookHandler>>();

    /** Contains the nodes of which I specified that I would run after them */
    private Set<Node<HookHandler>> thisExplicitlyRunsAfter = new HashSet<Node<HookHandler>>();

    /** Nodes which have implicitly been specified to be executed after this node (in a single step) */
    private Set<Node<HookHandler>> singleStepAfterSelf = new HashSet<Node<HookHandler>>();

    /** Nodes which have implicitly been specified to be executed before this node (in a single step) */
    private Set<Node<HookHandler>> singleStepBeforeSelf = new HashSet<Node<HookHandler>>();

    /** Nodes which have implicitly been specified to be executed after this node (any amount of steps) */
    private Set<Node<HookHandler>> implicitAfterSelf = new HashSet<Node<HookHandler>>();

    /** Nodes which have implicitly been specified to be executed before this node (any amount of steps) */
    private Set<Node<HookHandler>> implicitBeforeSelf = new HashSet<Node<HookHandler>>();


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
     * Sets the broad scheduling preference of this Node.
     * <p/>
     * If a Node is the first node in a graph to be executed, setting its scheduling preference to
     * EARLY will put it in front of the list of executed preference.  Choosing LATE will plate the node more
     * near the back of the execution stack.
     * <p/>
     * The priority should be seen as a solid hint towards the scheduling system.  If a node has both dependencies
     * and a priority, the dependencies MAY overrule the scheduling preference.
     *
     * @param preference The new preference
     */
    public void setSchedulingPreference(SchedulingPreference preference) {
        this.schedulingPreference = preference;
    }

    /**
     * Returns the scheduling preference of self.
     *
     * @return The scheduling preference of this Node
     */
    public SchedulingPreference getSchedulingPreference() {
        return this.schedulingPreference;
    }

    /**
     * Return the nodes of which we have explicitly said that we must run after them (no inference).
     *
     * @return nodes of which we have explicitly said that we must run after them.
     */
    public Set<Node> explicitlyRunsAfter() {
        return new HashSet<Node>(thisExplicitlyRunsAfter);
    }

    /**
     * Return the nodes of which we have explicitly been that we must run before them (without inference).
     *
     * @return nodes of which we have explicitly said that we must run before them.
     */
    public Set<Node> explicitlyRunsBefore() {
        return new HashSet<Node>(thisExplicitlyRunsBefore);
    }

    /**
     * Returns all nodes which are accessible by taking a single step from the current node
     *
     * @return Directly accessible nodes from this node.
     */
    @SuppressWarnings( "UnusedDeclaration" )
    public Set<Node<HookHandler>> getSingleStepAfterSelf() {
        return singleStepAfterSelf;
    }

    /**
     * Returns all nodes which are accessible by taking a single step from the current node
     *
     * @return Directly accessible nodes from this node.
     */
    public Set<Node<HookHandler>> getSingleStepBeforeSelf() {
        return singleStepBeforeSelf;
    }

    /**
     * Retrieves all accessible nodes from this node (including the current node)
     */
    public Set<Node<HookHandler>> getAccessibleNodes() {
        Set<Node<HookHandler>> accessibleNodes = new HashSet<Node<HookHandler>>();
        Set<Node<HookHandler>> newNodes = new HashSet<Node<HookHandler>>();
        newNodes.add(this);

        while(! newNodes.isEmpty()){
            accessibleNodes.addAll(newNodes);
            Set<Node<HookHandler>> nextNewNodes = new HashSet<Node<HookHandler>>();
            for(Node<HookHandler> newNode : newNodes){
                nextNewNodes.addAll(newNode.getAllImplicitAfterMe());
                nextNewNodes.addAll(newNode.getAllImplicitBeforeMe());
            }
            newNodes = nextNewNodes;
            newNodes.removeAll(accessibleNodes);
        }
        return accessibleNodes;
    }

    /**
     * Returns all nodes which should be executed before self.
     *
     * @return All nodes which ought to be executed before this node.
     */
    public Set<Node<HookHandler>> getAllImplicitBeforeMe() {
        return new HashSet<Node<HookHandler>>(implicitBeforeSelf);
    }

    /**
     * Returns all nodes which should be executed after self.
     *
     * @return All nodes which ought to be executed after this node.
     */
    public Set<Node<HookHandler>> getAllImplicitAfterMe() {
        return new HashSet<Node<HookHandler>>(implicitAfterSelf);
    }

    /**
     * Indicate that this node should be executed after attachment.
     * <p/>
     * The syntax node.after(attachment) essentially says that node should be executed *after*
     * the supplied attachment. Hence, internally, it adds itself to the internal variable
     * #explicitBeforeSelf and related variables of attachment.
     *
     * @param attachment Node after which we should execute.
     */
    public void after(Node<HookHandler> attachment) {
        thisExplicitlyRunsAfter(attachment);
        attachment.addSingleStepAfter(this);
        attachment.addImplicitAfter(this);
    }

    /**
     * Indicate that this node should be executed before attachment.
     * <p/>
     * The syntax node.after(attachment) essentially says that node should be executed *before*
     * the supplied attachment. Hence, internally, it adds itself to the internal variable
     * #explicitAfterSelf and related variables of attachment.
     *
     * @param attachment Node before which we should execute.
     */
    public void before(Node<HookHandler> attachment) {
        thisExplicitlyRunsBefore(attachment);
        attachment.addSingleStepBefore(this);
        attachment.addImplicitBefore(this);
    }

    /**
     * Indicates that self explicitly said that it should run after the supplied node.
     *
     * @param node Node which must be ran before we are ran.
     */
    private void thisExplicitlyRunsAfter(Node<HookHandler> node){
        thisExplicitlyRunsAfter.add(node);
    }

    /**
     * Indicates that self explicitly said that it should be ran before the supplied node.
     *
     * @param node Node before which we must have ran.
     */
    private void thisExplicitlyRunsBefore(Node<HookHandler> node){
        thisExplicitlyRunsBefore.add(node);
    }

    /**
     * Indicate that *other* should be executed *after* this.  Also implying that other comes *before* this.
     *
     * @param other Node which should be executed after this.
     */
    private void addSingleStepAfter(Node<HookHandler> other) {
        singleStepAfterSelf.add(other);
        other.singleStepBeforeSelf.add(this);
    }

    /**
     * Indicate that *other* should be executed *before* this.  Also implying that other comes *after* this.
     *
     * @param other Node which should be executed before this.
     */
    private void addSingleStepBefore(Node<HookHandler> other) {
        singleStepBeforeSelf.add(other);
        other.singleStepAfterSelf.add(this);
    }

    /**
     * Indicate that *other* should be executed *after* self, on any distance.
     *
     * @param other Node which should be executed after this.
     */
    private void addImplicitAfter(Node<HookHandler> other) {
        this.addImplicitAfterDirectional(other);
        other.addImplicitBeforeDirectional(this);
    }

    /**
     * Indicate that *other* should be executed *before* self, on any distance.
     *
     * @param other Node which should be executed after this.
     */
    private void addImplicitBefore(Node<HookHandler> other) {
        this.addImplicitBeforeDirectional(other);
        other.addImplicitAfterDirectional(this);
    }

    /**
     * Indicate that *other* should be executed *after* self, on any distance, but only in this direction.
     *
     * @param other Node which should be executed after this.
     */
    private void addImplicitAfterDirectional(Node<HookHandler> other) {
        if(!implicitAfterSelf.contains(other)){
            Set<Node<HookHandler>> newAfterMe = new HashSet<Node<HookHandler>>(other.implicitAfterSelf);
            newAfterMe.remove(this);
            newAfterMe.add(other);
            implicitAfterSelf.addAll(newAfterMe);
            for(Node<HookHandler> beforeSelf : implicitBeforeSelf)
                for(Node<HookHandler> singleNewAfterMe : newAfterMe)
                    beforeSelf.addImplicitAfterDirectional(singleNewAfterMe);
        }
    }

    /**
     * Indicate that *other* should be executed *before* self, on any distance, but only in this direction.
     *
     * @param other Node which should be executed before self.
     */
    private void addImplicitBeforeDirectional(Node<HookHandler> other) {
        if(!implicitBeforeSelf.contains(other)){
            Set<Node<HookHandler>> newBeforeMe = new HashSet<Node<HookHandler>>(other.implicitBeforeSelf);
            newBeforeMe.remove(this);
            newBeforeMe.add(other);
            implicitBeforeSelf.addAll(newBeforeMe);
            for(Node<HookHandler> afterSelf : implicitAfterSelf)
                for(Node<HookHandler> singleNewBeforeMe : newBeforeMe)
                    afterSelf.addImplicitBeforeDirectional(singleNewBeforeMe);
        }
    }
}