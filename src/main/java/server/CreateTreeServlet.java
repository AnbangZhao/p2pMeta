package server;

import datastore.DataStoreJsonWrapper;
import metadata.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by anbang on 11/9/14.
 *
 * Create an overlay tree for the client.
 * If the tree already exits, delete it and create
 * a new one
 *
 * Parameter:
 * tree_name : name
 * consume_capacity : capacity
 */
public class CreateTreeServlet extends HttpServlet{
    private static double initLatency = 0;

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String clientName = req.getParameter(Constants.CLOUDLET_NAME);
        String treeName = req.getParameter(Constants.TREENAME);
        double consumeCapacity = Double.valueOf(req.getParameter(Constants.STREAM_CAPACITY));
        if(clientName == null || treeName == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        DataStoreJsonWrapper<OverlayTree> datastore = new DataStoreJsonWrapper<>(OverlayTree.class);
        DataStoreJsonWrapper<Cloudlet> cloudletDataStore = new DataStoreJsonWrapper<>(Cloudlet.class);

        Cloudlet cloudlet = cloudletDataStore.get(Constants.CLOUDLET, clientName);
        if(cloudlet == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        TreeNode root = new TreeNode(clientName, initLatency);

        // tree already exists, destroy it
        OverlayTree tree = datastore.get(Constants.TREEINFO, treeName);
        if(tree != null) {
            tree.destroyTree(treeName);
        }

        tree = new OverlayTree(root, consumeCapacity);
        cloudlet.addIntoTree(treeName);
        datastore.put(Constants.TREEINFO, treeName, tree);
        cloudletDataStore.put(Constants.CLOUDLET, clientName, cloudlet);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
