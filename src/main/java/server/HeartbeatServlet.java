package server;

import datastore.DataStoreJsonWrapper;
import metadata.OverlayTree;
import metadata.TreeNode;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by anbang on 12/1/14.
 */
public class HeartbeatServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String treename = req.getParameter(Constants.TREENAME);
        String cloudletname = req.getParameter(Constants.CLOUDLET_NAME);

        DataStoreJsonWrapper<OverlayTree> treeStore = new DataStoreJsonWrapper<>(OverlayTree.class);
        OverlayTree tree = treeStore.get(Constants.TREEINFO, treename);
        TreeNode currNode = tree.findNode(cloudletname);
        if(currNode != null) {
            currNode.updateTimestamp();
        }
        treeStore.put(Constants.TREEINFO, treename, tree);
    }
}
