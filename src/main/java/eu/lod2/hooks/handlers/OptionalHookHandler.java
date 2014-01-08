package eu.lod2.hooks.handlers;

import java.util.Map;

public interface OptionalHookHandler extends HookHandler {

  public boolean isHandlingHook(String hook);

  public void handle(String hook, Object... args);

  public Map<String, Object> getConstraints(String hook);
}
