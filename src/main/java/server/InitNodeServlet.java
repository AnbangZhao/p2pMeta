package server;

import datastore.DataStoreJsonWrapper;
import metadata.CloudletInfo;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by anbang on 11/8/14.
 * Init the cloudlet in the internal data structure.
 * If the name of the cloudlet alread exists, override the content
 * If not, put this cloudlet info into datastore.
 *
 * Parameters:
 * bandwidthCapacity : capacity
 */
public class InitNodeServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String cloudletName = req.getRemoteAddr();
        double capacity = Double.valueOf(req.getParameter(Constants.BANDWIDTH_CAPACITY));
        CloudletInfo cloudlet = new CloudletInfo(capacity);
        DataStoreJsonWrapper<CloudletInfo> datastore = new DataStoreJsonWrapper<>(CloudletInfo.class);
        datastore.put(Constants.CLOUDLETINFO, cloudletName, cloudlet);
    }
}
