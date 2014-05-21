package eu.lod2.hooks.constraints.graph;

import java.util.*;

/**
 * Stores a series of nodes in which each node is accessible from the other nodes
 * in the graph.
 *
 * Can perform ordering information and cycle detection on these graphs.
 */
public class ConnectedGraph<HookHandler> {

    /** Stores all nodes in this ConnectedGraph */
    private Set<Node<HookHandler>> nodes;

    /** Cache for the {@link ConnectedGraph#executionOrder()} */
    private List<Node<HookHandler>> executionOrderCache;

    /**
     * Constructs a new ConnectedGraph from a set of nodes.
     *
     * This constructor assumes that each of the nodes is accessible through each of the other nodes
     * in *nodes* AND that no other node can be accessed.
     *
     * @see #discoverNodeSets(java.util.Set)
     * @param nodes The nodes of which the new ConnectedGraph consists.
     */
    public ConnectedGraph (Set<Node<HookHandler>> nodes) {
        this.nodes = new HashSet<Node<HookHandler>>(nodes);
    }

    /**
     * Splits a supplied set of nodes into a set of ConnectedGraphs with a
     * maximal amount of nodes in each ConnectedGraph.
     *
     * @param nodes The nodes to split.
     * @return The set of ConnectedGraph instances existing in the supplied nodes.
     */
    public static <HookHandler> Set<ConnectedGraph<HookHandler>> discoverNodeSets(Set<Node<HookHandler>> nodes) {
        HashSet<Node<HookHandler>> walkingNodes = new HashSet<Node<HookHandler>>(nodes);
        Set<ConnectedGraph<HookHandler>> discoveredGraphs = new HashSet<ConnectedGraph<HookHandler>>();

        while(!walkingNodes.isEmpty()) {
            Set<Node<HookHandler>> pickedNodes = walkingNodes.iterator().next().getAccessibleNodes();
            discoveredGraphs.add(new ConnectedGraph<HookHandler>(pickedNodes));
            walkingNodes.removeAll(pickedNodes);
        }

        return discoveredGraphs;
    }

    /**
     * Returns true iff the current ConnectedGraph has a cycle in it.
     *
     * TODO: there might be a simpler and more efficient way to do this check.  if the current node is in the set of nodes accessible from itself, then there must be a cycle.  I should verify that this is allowed in the current implementation of Node.
     *
     * @return true if the graph has a cycle in it, false otherwise.
     */
    public boolean cycleP() {
        // if we can walk to a node, and the other node can walk forward towards us, it must mean we have a cycle.
        // the same check could be done in the other direction, but that check would be redundant.
        for(Node<HookHandler> source : nodes)
            for(Node<HookHandler> target : source.getAllImplicitAfterMe())
                if(target.getAllImplicitAfterMe().contains(source))
                    return true;
        return false;
    }

    /**
     * Returns the first node to be executed in this graph.
     *
     * @return Node which should be executed first in this graph.
     */
    public Node<HookHandler> firstNode() {
        if( executionOrderCache != null )
            return executionOrderCache.get(0);

        ArrayList<Node<HookHandler>> sortedNodes = new ArrayList<Node<HookHandler>>(nodes);
        Collections.sort(sortedNodes,
                new Comparator<Node<HookHandler>>() {
                    @Override
                    public int compare(Node<HookHandler> a, Node<HookHandler> b) {
                        return a.getAllImplicitBeforeMe().size() - b.getAllImplicitBeforeMe().size();
                    }
                });
        return sortedNodes.get(0);
    }

    /**
     * Returns the last node to be executed in this graph.
     *
     * @return Node which should be executed last in this graph.
     */
    public Node<HookHandler> lastNode() {
        if( executionOrder() != null )
            return executionOrderCache.get(executionOrderCache.size() -1 );
        ArrayList<Node<HookHandler>> sortedNodes = new ArrayList<Node<HookHandler>>(nodes);
        Collections.sort(sortedNodes,
                new Comparator<Node<HookHandler>>() {
                    @Override
                    public int compare(Node<HookHandler> a, Node<HookHandler> b) {
                        return a.getAllImplicitAfterMe().size() - b.getAllImplicitAfterMe().size();
                    }
                });
        return sortedNodes.get(0);
    }

    /**
     * Returns a valid order of execution, as specified by the Node's constraints
     *
     * @return Valid order of execution for the HookHandler
     */
    public List<Node<HookHandler>> executionOrder(){
        // In the current approach we take a valid starting node and add it to the list of accessible nodes.
        // (A) For each node which isn't in the accessible list yet
        //     Add to accessible nodes if all required nodes (for a single step) are in accessible nodes.
        // Repeat (A) until all nodes are accessible.

        if(executionOrderCache != null)
            return new ArrayList<Node<HookHandler>>(executionOrderCache);

        Set<Node<HookHandler>> availableNodes = new HashSet<Node<HookHandler>>(nodes);
        List<Node<HookHandler>> executionOrder = new ArrayList<Node<HookHandler>>();

        Node<HookHandler> firstNode = firstNode();
        executionOrder.add(firstNode);
        availableNodes.remove(firstNode);

        // todo: for nodes which neither set a before, nor an after priority, it would make sense to have them injected by their requested priority.
        while(!availableNodes.isEmpty()){
            List<Node<HookHandler>> newlyAvailableNodes = new ArrayList<Node<HookHandler>>();
            for(Node<HookHandler> availableNode : availableNodes )
                if( allDirectNodeRequirementsInCollection(availableNode, executionOrder) )
                    newlyAvailableNodes.add(availableNode);
            executionOrder.addAll(newlyAvailableNodes);
            availableNodes.removeAll(newlyAvailableNodes);
        }

        executionOrderCache = executionOrder;

        return executionOrder;
    }

    /**
     * Returns the first node in the execution order which needn't be explicitly ran before another node.
     *
     * @return Earliest unconstrained node in the ConnectedGraph
     */
    public Node<HookHandler> earliestUnconstrainedNode(){
        for(Node<HookHandler> node : executionOrder())
            if( node.explicitlyRunsBefore().isEmpty() )
                return node;
        return null; // unreachable unless we have a cycle
    }

    /**
     * Returns the last node in the execution order which needn't be explicitly ran after another node.
     *
     * @return Last executed unconstrained node in the ConnectedGraph
     */
    public Node<HookHandler> lastUnconstrainedNode(){
        List<Node<HookHandler>> order = executionOrder();
        Collections.reverse( order );
        for(Node<HookHandler> node : order)
            if( node.explicitlyRunsAfter().isEmpty() )
                return node;
        return null; // unreachable unless we have a cycle
    }

    /**
     * Returns true iff node can be executed, given that all nodes in *set* have been executed beforehand.
     *
     * @param node The node of which we want to know if we can execute it.
     * @param set The nodes which have been executed previously.
     * @return true if node can be executed, false otherwise.
     */
    private boolean allDirectNodeRequirementsInCollection(Node<HookHandler> node, Collection<Node<HookHandler>> set){
        return set.containsAll(node.getSingleStepBeforeSelf());
    }
}
