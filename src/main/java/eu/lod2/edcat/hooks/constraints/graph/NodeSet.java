package eu.lod2.edcat.hooks.constraints.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<HookHandler> handlers = new HashSet<HookHandler>();

    /** Stores all nodes in the current set */
    private Set<Node<HookHandler>> nodes = new HashSet<Node<HookHandler>>();


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

    ;

    /** The current state of the NedSet. */
    private State currentState = State.ADDING;

    /**
     * Ensures the current state is ADDING.  If it is not, the state is set up so it is
     * the current state.
     * <p/>
     * IMPLEMENTATION: sets the new State
     */
    private void inAddingState() {
        // We don't have to do something when starting to add new HookHandlers.
        // Values are cleared when entering the next state.
        currentState = State.ADDING;
    }

    /**
     * Ensures the current state is RETRIEVING.  If it is not, the state is set up so it is
     * the current state.
     */
    private void inRetrievingState() {
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
     * Adds *hook* to the set of hooks to manage.
     * <p/>
     * Internally, this means a node will be constructed to manage the dependency tree of *hook*.
     * <p/>
     * /@pre Hook must not be part of the currently managed set of hooks.
     *
     * @param handler Hook to be managed by the current set of nodes.
     */
    public void add(HookHandler handler) {
        inAddingState();
        handlers.add(handler);
    }


    //------------------------------------
    //-- override (to support generic use)
    //------------------------------------

    /**
     * We want *hook* to be executed before each of the returned results.
     * <p/>
     * /@atStateChange: used during State change
     *
     * @param hook The hook which should indicate the hooks that must execute before itself.
     * @return Hooks before which *hook* should execute.
     */
    public abstract List<HookHandler> hookExecutesBefore(HookHandler hook);

    /**
     * We want *hook* to be executed only after each of the returned results have been executed.
     * As if *hook* depends on the functionality executed by each of the returned hooks.
     * <p/>
     * /@atStateChange: used during State change
     *
     * @param hook The hook which should indicate the hooks that must execute after itself.
     * @return Hooks after which *hook* should execute.
     */
    public abstract List<HookHandler> hookExecutesAfter(HookHandler hook);


    //--------------------------------------------
    //-- conversion between nodes and HookHandlers
    //--------------------------------------------

    /**
     * Clears the precalculated nodes
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
     * Constructs the node references
     * <p/>
     * /@atStateChange: used during State change
     */
    private void constructNodeReferences() {
        for (Node<HookHandler> node : nodes) {
            for (HookHandler beforeHandler : hookExecutesBefore(node.getHandler()))
                node.before(findNodeByHandler(beforeHandler));
            for (HookHandler afterHandler : hookExecutesAfter(node.getHandler()))
                node.after(findNodeByHandler(afterHandler));
        }
    }

    /**
     * Returns a node, given the HookHandler it describes
     * <p/>
     * /@atStateChange: used during State change
     *
     * @param handler the hookhandler which the Node describes
     * @return Node which has HookHandler
     */
    private Node<HookHandler> findNodeByHandler(HookHandler handler) {
        for (Node<HookHandler> node : nodes)
            if (node.getHandler() == handler)
                return node;
        return null;
    }

}
