package server;

import datastore.DataStoreJsonWrapper;
import metadata.Cloudlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by anbang on 11/8/14.
 * Init the cloudlet in the internal data structure.
 * If the name of the cloudlet alread exists override it
 * If not, put this cloudlet info into datastore.
 *
 * Parameters:
 * bandwidthCapacity : capacity
 */
public class InitNodeServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        //String cloudletName = req.getRemoteAddr();
        String cloudletName = req.getParameter(Constants.CLOUDLET_NAME);
        double capacity = Double.valueOf(req.getParameter(Constants.BANDWIDTH_CAPACITY));
        Cloudlet cloudlet = new Cloudlet(capacity);
        DataStoreJsonWrapper<Cloudlet> datastore = new DataStoreJsonWrapper<>(Cloudlet.class);
        Cloudlet getCloudlet = datastore.get(Constants.CLOUDLET, cloudletName);
        if(getCloudlet != null) {
            datastore.delete(Constants.CLOUDLET, cloudletName);
        }
        datastore.put(Constants.CLOUDLET, cloudletName, cloudlet);
    }
}