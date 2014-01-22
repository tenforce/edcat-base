package eu.lod2.edcat.format;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DcatJsonHashMap extends LinkedHashMap<String, Object> {
  /**
   * Associates the specified newValue with the specified key in this map.
   * If the map previously contained a mapping for the key,
   * the values are merged
   *
   * @param key   key with which the specified newValue is to be associated
   * @param value newValue to be associated with the specified key
   * @return the previous newValue associated with <tt>key</tt>, or
   *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
   *         (A <tt>null</tt> return can also indicate that the map
   *         previously associated <tt>null</tt> with <tt>key</tt>.)
   */
  public Object put(String key, Object value) {
    if (isLanguageString(value))
      return insertLanguageString(key, (Literal) value);
    else if (value instanceof Value)
      return put(key, ((Value) value).stringValue());
    else if (containsKey(key))
      return mergePut(key, value);
    else
      return super.put(key, value);
  }

  /**
   * Associates the specified newValue with the specified key in this map.
   * If the map previously contained a mapping for the key,
   * the values are merged into an Array
   *
   * @param key
   * @param newValue
   * @return the previous newValue associated with <tt>key</tt>, or
   *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
   *         (A <tt>null</tt> return can also indicate that the map
   *         previously associated <tt>null</tt> with <tt>key</tt>.)
   */
  public Object mergePut(String key, Object newValue) {
    Object oldValue = get(key);
    if (isLanguageString(newValue))
      return insertLanguageString(key, (Literal) newValue);
    else if (oldValue instanceof List) {
      ((List) oldValue).add(newValue);
      return super.put(key, oldValue);
    } else if (oldValue instanceof DcatJsonHashMap)
      return ((DcatJsonHashMap) oldValue).put(key, newValue);
    else {
      List list = new ArrayList();
      list.add(newValue);
      list.add(oldValue);
      return super.put(key, list);
    }
  }

  /**
   * @param key
   * @param value
   * @return the previous newValue associated with <tt>key</tt>, or
   *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
   *         (A <tt>null</tt> return can also indicate that the map
   *         previously associated <tt>null</tt> with <tt>key</tt>.)
   */
  private Object insertLanguageString(String key, Literal value) {
    Object oldValue = get(key);
    if (oldValue instanceof LanguageMap) {
      ((LanguageMap) oldValue).put(value.getLanguage(), value.stringValue());
      return super.put(key, oldValue);
    } else {
      LanguageMap lMap = new LanguageMap();
      lMap.put(value.getLanguage(), value.stringValue());
      return put(key, lMap);
    }

  }

  private boolean isLanguageString(Object value) {
    return (value instanceof Literal && ((Literal) value).getLanguage() != null);
  }


  private class LanguageMap extends DcatJsonHashMap {
  }
}
