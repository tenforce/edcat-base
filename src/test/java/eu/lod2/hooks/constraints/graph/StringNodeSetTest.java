package eu.lod2.hooks.constraints.graph;

import org.testng.Assert;

import java.util.Collection;
import java.util.List;

/**
 * Tests for the StringNodeSet
 *
 * These tests serve to test both the StringNodeSet and the NodeSet functionality governed by it.
 */
public class StringNodeSetTest {

    @org.testng.annotations.Test
    public void testNodeStringsToNodeNames() throws Exception {
        StringNodeSet set = DefaultGraphBuilder.buildCycledSetA();
        Collection<String> names = set.nodeStringsToNodeNames(set.getNodeStrings());
        Assert.assertEquals(names.size(),3);
        Assert.assertTrue(names.contains("r"));
        Assert.assertTrue(names.contains("t"));
        Assert.assertTrue(names.contains("q"));
    }

    @org.testng.annotations.Test
    public void testHasCycleP() throws Exception {
        for(StringNodeSet set : DefaultGraphBuilder.buildCycledSets())
            Assert.assertTrue(set.hasCycleP());
        Assert.assertFalse(DefaultGraphBuilder.buildLongSet().hasCycleP());
    }

    @org.testng.annotations.Test
    public void testHookExecutesBefore() throws Exception {
        StringNodeSet set = DefaultGraphBuilder.buildReferencingSet();
        // a,b->c->d,e
        Collection<NodeString> references = set.hookExecutesBefore(set.findNodeStringByName("t"));
        Assert.assertEquals(2,references.size());
        Assert.assertTrue(references.contains(set.findNodeStringByName("u")));
        Assert.assertTrue(references.contains(set.findNodeStringByName("v")));
        // ->a->b
        references = set.hookExecutesBefore(set.findNodeStringByName("r"));
        Assert.assertEquals(1,references.size());
        Assert.assertTrue(references.contains(set.findNodeStringByName("s")));
        // e->d->
        Assert.assertEquals(0,set.hookExecutesBefore(set.findNodeStringByName("u")).size());
    }

    @org.testng.annotations.Test
    public void testHookExecutesAfter() throws Exception {
        StringNodeSet set = DefaultGraphBuilder.buildReferencingSet();
        Collection<NodeString> references;
        // a,b->c->d,e
        references = set.hookExecutesAfter(set.findNodeStringByName("t"));
        Assert.assertEquals(references.size(),2);
        Assert.assertTrue(references.contains(set.findNodeStringByName("r")));
        Assert.assertTrue(references.contains(set.findNodeStringByName("s")));
        // ->a->b
        references = set.hookExecutesAfter(set.findNodeStringByName("r"));
        Assert.assertEquals(references.size(),0);
        // ->b->
        references = set.hookExecutesAfter(set.findNodeStringByName("s"));
        Assert.assertEquals(references.size(),0);
        // e->d->
        references = set.hookExecutesAfter(set.findNodeStringByName("u"));
        Assert.assertEquals(references.size(), 1);
        Assert.assertTrue(references.contains(set.findNodeStringByName("v")));
    }

    @org.testng.annotations.Test
    public void testHookSchedulingPreference() throws Exception {
        StringNodeSet set = DefaultGraphBuilder.buildReferencingSet();
        Node.SchedulingPreference preference = set.hookSchedulingPreference(set.findNodeStringByName("s"));
        Assert.assertEquals(preference, Node.SchedulingPreference.EARLY);
        preference = set.hookSchedulingPreference(set.findNodeStringByName("r"));
        Assert.assertEquals(preference, Node.SchedulingPreference.LATE);
        preference = set.hookSchedulingPreference(set.findNodeStringByName("t"));
        Assert.assertEquals(preference, Node.SchedulingPreference.LATE);
        preference = set.hookSchedulingPreference(set.findNodeStringByName("u"));
        Assert.assertEquals(preference, Node.SchedulingPreference.LATE);
        preference = set.hookSchedulingPreference(set.findNodeStringByName("v"));
        Assert.assertEquals(preference, Node.SchedulingPreference.LATE);
        set = DefaultGraphBuilder.buildLongSet();
        preference = set.hookSchedulingPreference(set.findNodeStringByName("a"));
        Assert.assertEquals(preference, Node.SchedulingPreference.LATE);
        preference = set.hookSchedulingPreference(set.findNodeStringByName("b"));
        Assert.assertEquals(preference, Node.SchedulingPreference.LATE);
        preference = set.hookSchedulingPreference(set.findNodeStringByName("c"));
        Assert.assertEquals(preference, Node.SchedulingPreference.LATE);
        preference = set.hookSchedulingPreference(set.findNodeStringByName("d"));
        Assert.assertEquals(preference, Node.SchedulingPreference.LATE);
        preference = set.hookSchedulingPreference(set.findNodeStringByName("e"));
        Assert.assertEquals(preference, Node.SchedulingPreference.LATE);
        preference = set.hookSchedulingPreference(set.findNodeStringByName("f"));
        Assert.assertEquals(preference, Node.SchedulingPreference.LATE);
        preference = set.hookSchedulingPreference(set.findNodeStringByName("q"));
        Assert.assertEquals(preference, Node.SchedulingPreference.LATE);
    }

    @org.testng.annotations.Test
    public void testAdd() throws Exception {
        StringNodeSet set = new StringNodeSet();
        set.add("->a->");
        Assert.assertEquals(1,set.getNodeStrings().size());
        set.add(set.findNodeStringByName("a"));
        Assert.assertEquals(1,set.getNodeStrings().size());
    }

    @org.testng.annotations.Test
    public void testHandlersExecutionList() throws Exception {
        // check the order in a combined set
        StringNodeSet set = DefaultGraphBuilder.buildLongSet();
        DefaultGraphBuilder.injectReferencingSet(set);
        List<String> order = set.nodeStringsToNodeNames(set.handlersExecutionList());

        // we know the graphs are ordered correctly, so we check the first elements of each graph
        // the first graph should be the referencingSet, which starts with name "r"
        Assert.assertEquals(order.get(0),"r");
        Assert.assertTrue(order.get(5).equals("e") || order.get(5).equals("f"));
    }
}
