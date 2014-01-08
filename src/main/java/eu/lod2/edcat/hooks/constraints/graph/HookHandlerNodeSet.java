package eu.lod2.edcat.hooks.constraints.graph;

import eu.lod2.edcat.hooks.handlers.HookHandler;

import java.util.List;

public class HookHandlerNodeSet extends NodeSet<HookHandler>{

    @Override
    public List<HookHandler> hookExecutesBefore(HookHandler hookHandler) {
        return null;
    }

    @Override
    public List<HookHandler> hookExecutesAfter(HookHandler hookHandler) {
        return null;
    }
}
