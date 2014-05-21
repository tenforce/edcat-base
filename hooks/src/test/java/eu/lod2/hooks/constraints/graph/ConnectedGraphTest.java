package eu.lod2.hooks.constraints.graph;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Tests for the ConnectedGraph
 */
public class ConnectedGraphTest {

    @Test
    public void testDiscoverNodeSets() throws Exception {
        StringNodeSet set = DefaultGraphBuilder.buildLongSet();
        Collection<ConnectedGraph<NodeString>> graphs = ConnectedGraph.discoverNodeSets(set.getNodes());
        Assert.assertEquals(graphs.size(), 1);
    }

    @Test
    public void testCycleP() throws Exception {

    }

    @Test
    public void testFirstNode() throws Exception {
        StringNodeSet set = DefaultGraphBuilder.buildLongSet();
        Set<ConnectedGraph<NodeString>> graphs = ConnectedGraph.discoverNodeSets(set.getNodes());
        String firstNodeName = graphs.iterator().next().firstNode().getHandler().getName();
        Assert.assertTrue(firstNodeName.equals("e") || firstNodeName.equals("f"));
    }

    @Test
    public void testLastNode() throws Exception {
        StringNodeSet set = DefaultGraphBuilder.buildLongSet();
        Set<ConnectedGraph<NodeString>> graphs = ConnectedGraph.discoverNodeSets(set.getNodes());
        String lastNodeName = graphs.iterator().next().lastNode().getHandler().getName();
        Assert.assertTrue(lastNodeName.equals("a") || lastNodeName.equals("d"));
    }

    @Test
    public void testExecutionOrder() throws Exception {
        StringNodeSet set;
        List<String> order;

        // check the order in the long set
        set = DefaultGraphBuilder.buildLongSet();
        order = set.nodeStringsToNodeNames(set.handlersExecutionList());

        // first is e or f
        Assert.assertTrue("e".equals(order.get(0)) || "f".equals(order.get(0)));

        // check that each constraint is met in the execution order (which implies the global order is "ok")
        Assert.assertTrue(order.indexOf("b") > order.indexOf("e"));
        Assert.assertTrue(order.indexOf("b") < order.indexOf("d"));
        Assert.assertTrue(order.indexOf("a") > order.indexOf("b"));
        Assert.assertTrue(order.indexOf("c") > order.indexOf("f"));
        Assert.assertTrue(order.indexOf("c") < order.indexOf("q"));
        Assert.assertTrue(order.indexOf("c") < order.indexOf("a"));
        Assert.assertTrue(order.indexOf("q") < order.indexOf("a"));


        // check the order in the referencing set
        set = DefaultGraphBuilder.buildReferencingSet();
        order = set.nodeStringsToNodeNames(set.handlersExecutionList());

        // order is r,s,t,u,v
        Assert.assertEquals(order.get(0), "r");
        Assert.assertEquals(order.get(1), "s");
        Assert.assertEquals(order.get(2), "t");
        Assert.assertEquals(order.get(3), "v");
        Assert.assertEquals(order.get(4), "u");
    }

    @Test
    public void testEarliestUnconstrainedNode() throws Exception {
        StringNodeSet set;
        ConnectedGraph<NodeString> graph;
        String unconstrainedNodeName;

        // check the earliest unconstrained node in the long set
        set = DefaultGraphBuilder.buildLongSet();
        graph = ConnectedGraph.discoverNodeSets(set.getNodes()).iterator().next();
        unconstrainedNodeName = graph.earliestUnconstrainedNode().getHandler().getName();
        Assert.assertTrue(unconstrainedNodeName.equals("b") || unconstrainedNodeName.equals("q"));

        // check the earliest unconstrained node in the referencing set
        set = DefaultGraphBuilder.buildReferencingSet();
        graph = ConnectedGraph.discoverNodeSets(set.getNodes()).iterator().next();
        unconstrainedNodeName = graph.earliestUnconstrainedNode().getHandler().getName();
        Assert.assertEquals(unconstrainedNodeName,"s");
    }

    @Test
    public void testLastUnconstrainedNode() throws Exception {
        StringNodeSet set;
        ConnectedGraph<NodeString> graph;
        String unconstrainedNodeName;

        // check the earliest unconstrained node in the long set
        set = DefaultGraphBuilder.buildLongSet();
        graph = ConnectedGraph.discoverNodeSets(set.getNodes()).iterator().next();
        unconstrainedNodeName = graph.lastUnconstrainedNode().getHandler().getName();
        Assert.assertTrue(unconstrainedNodeName.equals("b") || unconstrainedNodeName.equals("c"))
        ;

        // check the earliest unconstrained node in the referencing set
        set = DefaultGraphBuilder.buildReferencingSet();
        graph = ConnectedGraph.discoverNodeSets(set.getNodes()).iterator().next();
        unconstrainedNodeName = graph.lastUnconstrainedNode().getHandler().getName();
        Assert.assertEquals(unconstrainedNodeName,"v");
    }
}
