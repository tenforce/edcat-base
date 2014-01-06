package eu.lod2.edcat.handler;

import java.util.Map;

public interface OptionalHookHandler {

  public boolean isHandlingHook(String hook);

  public void handle(String hook,Object... args);

  public Map<String,Object> getConstraints(String hook);
}
