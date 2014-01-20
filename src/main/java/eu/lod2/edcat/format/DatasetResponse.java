package eu.lod2.edcat.format;

public class DatasetResponse {
  private String self;
  private Object dataset;
  private Object record;


  public String getSelf() {
    return self;
  }

  public void setSelf(String self) {
    this.self = self;
  }

  public Object getDataset() {
    return dataset;
  }

  public void setDataset(Object dataset) {
    this.dataset = dataset;
  }

  public Object getRecord() {
    return record;
  }

  public void setRecord(Object record) {
    this.record = record;
  }
}
