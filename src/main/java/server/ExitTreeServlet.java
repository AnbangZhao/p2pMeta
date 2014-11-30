package server;

import datastore.DataStoreJsonWrapper;
import metadata.Cloudlet;
import metadata.OverlayTree;
import metadata.TreeNode;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by anbang on 11/11/14.
 *
 * Gracefully exit the node from the tree, including in the
 * tree structure and the cloudlet structure
 *
 * Parameters:
 * treename : name
 */
public class ExitTreeServlet extends HttpServlet{
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String treename = req.getParameter(Constants.TREENAME);
        String cloudletname = req.getParameter(Constants.CLOUDLET_NAME);
        if(treename == null || cloudletname == null){
            resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            return;
        }

        DataStoreJsonWrapper<OverlayTree> treeStore = new DataStoreJsonWrapper<>(OverlayTree.class);
        DataStoreJsonWrapper<Cloudlet> cloudletStore = new DataStoreJsonWrapper<>(Cloudlet.class);
        OverlayTree tree = treeStore.get(Constants.TREEINFO, treename);
        Cloudlet exitCloudlet = cloudletStore.get(Constants.CLOUDLET, cloudletname);
        if(tree == null) {
            exitCloudlet.deleteTree(treename);
            cloudletStore.put(Constants.CLOUDLET, cloudletname, exitCloudlet);
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        TreeNode exitNode = tree.findNode(cloudletname);
        // if we cannot find the tree. Just modify the cloudlet data structure
        if (exitNode == null) {
            exitCloudlet.deleteTree(treename);
            cloudletStore.put(Constants.CLOUDLET, cloudletname, exitCloudlet);
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // if the root wants to exit. Destroy the whole tree
        if(tree.isRoot(exitNode)) {
            tree.destroyTree(treename);
        }
        // if a non-root node wants to exit, it should be a leaf node.
        // if it is a leaf node, exit it
        else if(exitNode.isLeaf()) {
            tree.exitLeaf(exitNode, treename);
        }
        // if it is a non-root node. Either a new node just came or something wrong
        // if it has a new come node. don't exit the node. Tell cloudlet not to exit too
        else if(exitNode.hasNewDesc()) {
            resp.sendError(HttpServletResponse.SC_ACCEPTED);
            return;
        }
        // if it does not have a new coming node.
        else if(!exitNode.hasNewDesc()) {
            tree.exitNonLeaf(exitNode, treename);
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        return;

        //TreeNode exitNode = tree.removeNode(cloudletname);
        // need to modify prevNode cloudlet
        // and re-schedule its dependents
//        if(exitNode != null) {
//            TreeNode prev = exitNode.getPrevNode(tree);
//            Cloudlet prevCloudlet = cloudletStore.get(Constants.CLOUDLET, prev.getCloudletName());
//            prevCloudlet.incBand(tree.getConsuption());
//            cloudletStore.put(Constants.CLOUDLET, prev.getCloudletName(), prevCloudlet);
//            TreeScheduler scheduler = new TreeScheduler(tree, treename);
//            for(TreeNode node : exitNode.getDescedents()) {
//                scheduler.addTreeNode(node);
//            }
//        }
    }

    private void decCloudletBand(String name, double delta) {
        DataStoreJsonWrapper<Cloudlet> cloudletStore = new DataStoreJsonWrapper<>(Cloudlet.class);
        cloudletStore.get(Constants.CLOUDLET, name);
    }
}