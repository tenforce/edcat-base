package eu.lod2.hooks.constraints.graph;

import eu.lod2.hooks.handlers.HookHandler;

import java.util.Collection;
import java.util.List;

/**
 * NodeSet implementation for the execution order of HookHandler instances.
 */
public class HookHandlerNodeSet extends NodeSet<HookHandler>{

    /**
     * Constructs a new HookHandlerNodeSet.
     *
     * @param hookHandlers Collection of all HookHandler instances in the graph.
     * @param hookName Name of the hook which will be called.
     */
    public HookHandlerNodeSet( Collection<HookHandler> hookHandlers, String hookName ){
        for(HookHandler handler : hookHandlers )
            add(handler);
    }

    @Override
    public List<HookHandler> hookExecutesBefore(HookHandler hookHandler) {
        // Todo: implement me!
        return null;
    }

    @Override
    public List<HookHandler> hookExecutesAfter(HookHandler hookHandler) {
        // Todo: implement me!
        return null;
    }

    @Override
    public Node.SchedulingPreference hookSchedulingPreference(HookHandler hook) {
        // Todo: implement me!
        return null;
    }
}
