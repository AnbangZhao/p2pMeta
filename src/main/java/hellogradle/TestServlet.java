package hellogradle;
import com.google.appengine.api.datastore.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by anbang on 11/2/14.
 */
public class TestServlet extends HttpServlet{
    	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key testKey = KeyFactory.createKey("test", "testkey");
        //Entity greeting = new Entity(testKey);
        //greeting.setProperty("value1", "valuuuu1");
        //datastoreService.put(greeting);

        Query query = new Query(testKey);
        List<Entity> greetings = datastoreService.prepare(query).asList(FetchOptions.Builder.withLimit(1));
        String response = "111";
        for(Entity a : greetings) {
            if(a.getProperty("value1") != null) {
                response = (String)a.getProperty("value1");
            }
        }
		resp.setContentType("text/plain");
		resp.getWriter().println(response);
	}
}
