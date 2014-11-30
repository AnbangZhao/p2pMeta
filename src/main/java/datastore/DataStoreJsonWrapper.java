package datastore;

import com.google.gson.Gson;

/**
 * Created by anbang on 11/9/14.
 *
 * transform an object into a json string and store
 * get string from datastore and transform it back
 * to an object
 *
 */
public class DataStoreJsonWrapper<T> {
    private Class<T> classtype;
    private DataStoreWrapper datastore;
    Gson gson = new Gson();

    public DataStoreJsonWrapper(Class<T> type){
        classtype = type;
        datastore = new DataStoreWrapper();
    }

    public T get(String kind, String key) {
        String jsonStr = (String)datastore.get(kind, key);
        T obj = gson.fromJson(jsonStr, classtype);
        return obj;
    }

    public void put(String kind, String key, T obj) {
        String jsonStr = gson.toJson(obj);
        datastore.put(kind, key, jsonStr);
    }

    public void delete(String kind, String key) {
        datastore.delete(kind, key);
    }
}
