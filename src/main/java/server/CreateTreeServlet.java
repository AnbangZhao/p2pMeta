package server;

import datastore.DataStoreJsonWrapper;
import metadata.CloudletInfo;
import metadata.TreeInfo;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by anbang on 11/9/14.
 *
 * Create an overlay tree for the client.
 * If the tree already exits, return 404
 *
 * Parameter:
 * tree_name : name
 * consume_capacity : capacity
 */
public class CreateTreeServlet extends HttpServlet{
    private static double initLatency = 0;

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String clientName = req.getRemoteAddr();
        String treeName = req.getParameter(Constants.TREENAME);
        double consumeCapacity = Double.valueOf(req.getParameter(Constants.CONSUME_CAPACITY));
        if(clientName == null || treeName == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        DataStoreJsonWrapper<TreeInfo> datastore = new DataStoreJsonWrapper<>(TreeInfo.class);
        DataStoreJsonWrapper<CloudletInfo> cloudletDataStore = new DataStoreJsonWrapper<>(CloudletInfo.class);

        CloudletInfo cloudletInfo = cloudletDataStore.get(Constants.CLOUDLETINFO, clientName);
        if(cloudletInfo == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // tree already exists, return error
        TreeInfo tree = datastore.get(Constants.TREEINFO, treeName);
        if(tree != null) {
            resp.sendError(HttpServletResponse.SC_CONFLICT);
            return;
        }

        tree = new TreeInfo(consumeCapacity);
        double leftCap = cloudletInfo.getCapacity();
        tree.addCloudlet(clientName, initLatency, leftCap);
        cloudletInfo.addTree(treeName, consumeCapacity, "root");
        datastore.put(Constants.TREEINFO, treeName, tree);
        cloudletDataStore.put(Constants.CLOUDLETINFO, clientName, cloudletInfo);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
