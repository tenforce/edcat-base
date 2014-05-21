package eu.lod2.hooks.constraints.graph;

import java.util.*;

/**
 * A NodeSet contains an Array of Nodes and can perform operations on them regarding
 * their grouping and ordering.  It does not require all nodes to be linked together.
 * <p/>
 * The NodeSet must perform a series of operations in order to be able to return the results.
 * Namely, the NodeSet constructs nodes from the supplied HookHandlers and retrieves the
 * references between the HookHandlers.  As this step may be expensive, the NodeSet lazily
 * retrieves the references and throws all references away when new hooks are added.
 * <p/>
 * The functionality is explicitly geared towards ordering a set of HookHandlers.
 */
public abstract class NodeSet<HookHandler> {

    //--------------------
    //- instance variables
    //--------------------

    /** Stores all HookHandlers in the current set */
    protected Set<HookHandler> handlers = new HashSet<HookHandler>();

    /** Stores all nodes in the current set */
    protected Set<Node<HookHandler>> nodes = new HashSet<Node<HookHandler>>();


    //-----------------------------
    //- state change administration
    //-----------------------------

    /**
     * State in which the NodeSet currently is.  This is regarding to the lazy constructions of
     * nodes and the references between these nodes.
     * <p/>
     * ADDING: adding new HookHandlers
     * RETRIEVING: retrieving information about the information stored in known nodes
     */
    private enum State {
        ADDING, RETRIEVING
    }

    /** The current state of the NedSet. */
    private State currentState = State.ADDING;

    /**
     * Ensures the current state is ADDING.  If it is not, the state is set up so it is
     * the current state.
     * <p/>
     * IMPLEMENTATION: sets the new State
     */
    protected void inAddingState() {
        // We don't have to do something when starting to add new HookHandlers.
        // Values are cleared when entering the next state.
        currentState = State.ADDING;
    }

    /**
     * Ensures the current state is RETRIEVING.  If it is not, the state is set up so it is
     * the current state.
     */
    protected void inRetrievingState() {
        switch (currentState) {
            case ADDING: // move from ADDING state -> RETRIEVING state
                clearNodes();
                constructNodes();
                constructNodeReferences();
                break;
            case RETRIEVING: // move from RETRIEVING state -> RETRIEVING state
                break;
        }
        currentState = State.RETRIEVING;
    }


    //--------------------------
    //-- public methods
    //--------------------------

    /**
     * Adds *handler* to the set of HookHandlers to manage unless *handler* was already being handled.
     * <p/>
     * Internally, this means a node will be constructed to manage the dependency tree of *hook*.
     * <p/>
     *
     * @param handler Hook to be managed by the current set of nodes.
     */
    public void add(HookHandler handler) {
        inAddingState();
        handlers.add(handler);
    }

    /**
     * Discovers an order in which the handlers can be executed.
     *
     * @return Ordered list of HookHandlers.  The first handler in the list should be executed first.
     * /@pre Graph must not contain cycles
     */
    public List<HookHandler> handlersExecutionList() {
        inRetrievingState();
        Set<Node<HookHandler>> clonedNodes = new HashSet<Node<HookHandler>>(nodes);
        Set<ConnectedGraph<HookHandler>> nodeSets = ConnectedGraph.discoverNodeSets(clonedNodes);
        return unpackNodes(orderedExecutionPath(nodeSets));
    }

    /**
     * Checks if there is a cycle in the current set of hooks.
     *
     * @return true if a cycle could be found, false otherwise.
     */
    public boolean hasCycleP() {
        inRetrievingState();
        for (ConnectedGraph<HookHandler> graph : ConnectedGraph.discoverNodeSets(nodes))
            if (graph.cycleP())
                return true;
        return false;
    }


    //------------------------------------
    //-- override (to support generic use)
    //------------------------------------

    /**
     * We want *hook* to be executed before each of the returned results.
     * <p/>
     * /@atStateChange: used during State change
     *
     *
     * @param hook The hook which should indicate the hooks that must execute before itself.
     * @return Hooks before which *hook* should execute.
     */
    public abstract Collection<HookHandler> hookExecutesBefore(HookHandler hook);

    /**
     * We want *hook* to be executed only after each of the returned results have been executed.
     * As if *hook* depends on the functionality executed by each of the returned hooks.
     * <p/>
     * /@atStateChange: used during State change
     *
     * @param hook The hook which should indicate the hooks that must execute after itself.
     * @return Hooks after which *hook* should execute.
     */
    public abstract Collection<HookHandler> hookExecutesAfter(HookHandler hook);

    /**
     * Returns the scheduling preference for the supplied hook.  This is the EARLY/LATE distinction
     * inside three of solutions.
     *
     * Returning LATE indicates that we prefer this HookHandler to be executed later within the
     * constraints of its execution.  Returning EARLY indicates that we want this HookHandler to be
     * executed earlier on.
     *
     * @param hook Hook of which we want to know the scheduling preference.
     * @return Indication of the broad scheduling preference of the supplied HookHandler.
     */
    public abstract Node.SchedulingPreference hookSchedulingPreference(HookHandler hook);


    //--------------------------------------------
    //-- conversion between nodes and HookHandlers
    //--------------------------------------------

    /**
     * Clears the pre-calculated nodes
     * <p/>
     * /@atStateChange: used during State change
     */
    private void clearNodes() {
        nodes = new HashSet<Node<HookHandler>>();
    }

    /**
     * Constructs the nodes
     * <p/>
     * /@atStateChange: used during State change
     */
    private void constructNodes() {
        for (HookHandler handler : handlers)
            nodes.add(new Node<HookHandler>(handler));
    }

    /**
     * Constructs the node references and priorities
     * <p/>
     * /@atStateChange: used during State change
     */
    private void constructNodeReferences() {
        for (Node<HookHandler> node : nodes) {
            for (HookHandler beforeHandler : hookExecutesBefore(node.getHandler()))
                node.before(findNodeByHandler(beforeHandler));
            for (HookHandler afterHandler : hookExecutesAfter(node.getHandler()))
                node.after(findNodeByHandler(afterHandler));
            node.setSchedulingPreference(this.hookSchedulingPreference(node.getHandler()));
        }
    }

    /**
     * Returns a node, given the HookHandler it describes
     * <p/>
     * /@atStateChange: used during State change
     *
     * @param handler the HookHandler which the Node describes
     * @return Node which has HookHandler
     */
    private Node<HookHandler> findNodeByHandler(HookHandler handler) {
        for (Node<HookHandler> node : nodes)
            if (node.getHandler() == handler)
                return node;
        return null;
    }

    //-----------------------------
    //-- finding the execution path
    //-----------------------------

    /**
     * Returns the execution path for a set of ConnectedGraphs.
     *
     * @param graphs The ConnectedGraphs which contain all nodes to be executed.
     * @return List of HookHandlers contained in the graphs, in the order in which they should be executed.
     * /@pre graph must not contain cycles
     */
    private List<Node<HookHandler>> orderedExecutionPath(Set<ConnectedGraph<HookHandler>> graphs) {
        inRetrievingState();
        // In order to sort the complete graphs, we request the earliest unconstrained node and
        // the last unconstrained node.  We check their SchedulingPreference and sort as good as we can on that.
        Set<List<Node<HookHandler>>> fromEarlyToEarly = new HashSet<List<Node<HookHandler>>>();
        Set<List<Node<HookHandler>>> fromEarlyToLate = new HashSet<List<Node<HookHandler>>>();
        Set<List<Node<HookHandler>>> fromLateToEarly = new HashSet<List<Node<HookHandler>>>();
        Set<List<Node<HookHandler>>> fromLateToLate = new HashSet<List<Node<HookHandler>>>();

        for (ConnectedGraph<HookHandler> graph : graphs) {
            switch (graph.earliestUnconstrainedNode().getSchedulingPreference()) {
                case EARLY:
                    switch (graph.lastUnconstrainedNode().getSchedulingPreference()) {
                        case EARLY:
                            fromEarlyToEarly.add(graph.executionOrder());
                            break;
                        case LATE:
                            fromEarlyToLate.add(graph.executionOrder());
                            break;
                    }
                    break;
                case LATE:
                    switch (graph.lastUnconstrainedNode().getSchedulingPreference()) {
                        case EARLY:
                            fromLateToEarly.add(graph.executionOrder());
                            break;
                        case LATE:
                            fromLateToLate.add(graph.executionOrder());
                            break;
                    }
            }
        }

        List<Node<HookHandler>> orderedNodes = new ArrayList<Node<HookHandler>>();

        for (List<Node<HookHandler>> nodes : fromEarlyToEarly)
            orderedNodes.addAll(nodes);
        for (List<Node<HookHandler>> nodes : fromEarlyToLate)
            orderedNodes.addAll(nodes);
        for (List<Node<HookHandler>> nodes : fromLateToEarly)
            orderedNodes.addAll(nodes);
        for (List<Node<HookHandler>> nodes : fromLateToLate)
            orderedNodes.addAll(nodes);
        return orderedNodes;
    }

    /**
     * Unpacks the supplied list of nodes and returns a list of their payload.
     *
     * @param nodes Nodes which will be unpacked.
     * @return List containing the payload of the supplied nodes in the same order as their corresponding nodes.
     * /@atStateChange: does not influence state
     */
    private <HookHandler> List<HookHandler> unpackNodes(List<Node<HookHandler>> nodes) {
        List<HookHandler> handlers = new ArrayList<HookHandler>();
        for (Node<HookHandler> node : nodes)
            handlers.add(node.getHandler());
        return handlers;
    }
}
