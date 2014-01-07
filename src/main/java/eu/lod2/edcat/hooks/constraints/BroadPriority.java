package eu.lod2.edcat.hooks.constraints;

public class BroadPriority extends Priority {
  public static final boolean EARLY = true;
  public static final boolean LATE = false;

  private boolean earlyP;

  public BroadPriority(boolean type) {
    this.earlyP = type;
  }

  public boolean isEarly() {
    return earlyP == EARLY;
  }

  public boolean isLate() {
    return earlyP == LATE;
  }
}
