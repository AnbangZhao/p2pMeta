package datastore;

import com.google.appengine.api.datastore.*;

import java.util.List;

/**
 * Created by anbang on 11/8/14.
 */

public class DataStoreWrapper implements DataStoreInterface{
    private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    private String propertyName = "myvalue";

    @Override
    public Object get(String kind, String key) {
        Key testKey = KeyFactory.createKey(kind, key);
        Query query = new Query(testKey);
        List<Entity> greetings = datastoreService.prepare(query).asList(FetchOptions.Builder.withLimit(1));
        if(greetings == null || greetings.size() < 1){
            return null;
        }
        return greetings.get(0).getProperty(propertyName);
    }

    @Override
    public void put(String kind, String key, Object value) {
        Key testKey = KeyFactory.createKey(kind, key);
        Entity greeting = new Entity(testKey);
        greeting.setProperty(propertyName, value);
        datastoreService.put(greeting);
    }
}
