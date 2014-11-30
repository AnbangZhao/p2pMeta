package datastore;

import java.util.Objects;

/**
 * Created by anbang on 11/8/14.
 */
public interface DataStoreInterface {
    public Object get(String kind, String key);
    public void put(String kind, String key, Object value);
    public void delete(String kind, String key);
}
