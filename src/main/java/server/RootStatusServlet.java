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
public class RootStatusServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String treename = req.getParameter(Constants.TREENAME);
        DataStoreJsonWrapper<OverlayTree> treeStore = new DataStoreJsonWrapper<>(OverlayTree.class);
        OverlayTree tree = treeStore.get(Constants.TREEINFO, treename);
        if(tree == null) {
            resp.getWriter().write("down");
            return;
        }
        TreeNode root = tree.getRoot();
        if(root.isHealthy()){
            resp.getWriter().write("up");
        }
        else{
            resp.getWriter().write("down");
        }
        return;
    }
}
