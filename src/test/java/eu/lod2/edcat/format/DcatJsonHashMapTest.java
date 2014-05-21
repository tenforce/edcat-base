package eu.lod2.edcat.format;

import junit.framework.Assert;
import org.openrdf.model.impl.LiteralImpl;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DcatJsonHashMapTest {

  @Test
  public void itShouldMergeMultipleLiteralsIntoAnArray() {
    DcatJsonHashMap map = new DcatJsonHashMap();
    map.put("my_key","key");
    map.put("my_key","key");
    Object o = map.get("my_key");
    Assert.assertTrue("two literals should be merged into a list", o instanceof List);
    Assert.assertTrue("both literals need to be present", ((List) o).size() == 2);
  }

  @Test
  public void itShouldMergeMultipleMapsIntoAList() {
    Map<String,Object> map1 = new LinkedHashMap<String,Object>();
    map1.put("test", "test");
    Map<String,Object> map2 = new LinkedHashMap<String,Object>();
    map2.put("test", "test");
    DcatJsonHashMap map = new DcatJsonHashMap();
    map.put("my_key",map1);
    map.put("my_key",map2);
    Object o = map.get("my_key");
    Assert.assertTrue("two maps should be merged into a list", o instanceof List);
    Assert.assertTrue("both maps need to be present", ((List) o).size() == 2);
  }

  @Test
  public void itShouldCreateALanguageContainerForLanguageLiterals() {
     DcatJsonHashMap map = new DcatJsonHashMap();
    map.put("title",new LiteralImpl("test","en"));
    map.put("title",new LiteralImpl("test","fr"));
    Object o = map.get("title");
    Assert.assertTrue("two language literals should be merged into a Map", o instanceof Map);
    Assert.assertTrue("it should have the language as keys",((Map) o).containsKey("en"));
  }
}
