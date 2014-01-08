package eu.lod2.hooks.constraints;

public class RelativePriority extends Priority {
  public static final boolean BEFORE = true;
  public static final boolean AFTER = false;

  private boolean beforeP;
  private String name;

  public RelativePriority(boolean type,String name) {
    beforeP = type;
    this.name = name;
  }

  public boolean getType() {
    return beforeP;
  }

  public String getName() {
    return name;
  }
}
