package hellogradle;

import com.google.appengine.api.datastore.*;
import com.google.gson.Gson;
import datastore.DataStoreJsonWrapper;
import metadata.CloudletInfo;
import metadata.TreeOnCloudletInfo;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class HelloGradleServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
        String kind = "test";
        String key = "test1";
        CloudletInfo info = new CloudletInfo(10);
        TreeOnCloudletInfo treeInfo = new TreeOnCloudletInfo("hey", 1, "leaf");
        info.addTree(treeInfo);
        DataStoreJsonWrapper<CloudletInfo> datastore = new DataStoreJsonWrapper<CloudletInfo>(CloudletInfo.class);
        datastore.put(kind, key, info);
        CloudletInfo info2 = datastore.get(kind, key);
        Gson gson = new Gson();
        String res = gson.toJson(info2);

        resp.getWriter().println(res);
	}
}
