package server.common;

import datastore.DataStoreJsonWrapper;
import metadata.Cloudlet;
import metadata.OverlayTree;
import metadata.TreeNode;
import server.Constants;

import java.util.Stack;

/**
 * Created by anbang on 11/12/14.
 */
public class TreeManager {

    public void destroyTree(OverlayTree tree, String treeName) {
        DataStoreJsonWrapper<Cloudlet> cloudletStore = new DataStoreJsonWrapper<>(Cloudlet.class);
        DataStoreJsonWrapper<OverlayTree> treeStore = new DataStoreJsonWrapper<>(OverlayTree.class);

        TreeNode root = tree.getRoot();
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.empty()) {
            TreeNode curr = stack.pop();
            Cloudlet cloudlet = cloudletStore.get(Constants.CLOUDLET, curr.getCloudletName());
            cloudlet.incBand(tree.getConsuption() * curr.getDescedents().size());
            cloudlet.deleteTree(treeName);
            cloudletStore.put(Constants.CLOUDLET, curr.getCloudletName(), cloudlet);

            for(TreeNode node : curr.getDescedents()) {
                stack.push(node);
            }
        }
        treeStore.delete(Constants.TREEINFO, treeName);
    }

    public void deleteLeaf(OverlayTree tree, TreeNode node, String treeName) {
        DataStoreJsonWrapper<Cloudlet> cloudletStore = new DataStoreJsonWrapper<>(Cloudlet.class);
        DataStoreJsonWrapper<OverlayTree> treeStore = new DataStoreJsonWrapper<>(OverlayTree.class);
        TreeNode prevNode = node.getPrevNode(tree);
        if(prevNode != null) {
            // modify prev node
            prevNode.delDesc(node.getCloudletName());
            Cloudlet prevCloudlet = cloudletStore.get(Constants.CLOUDLET, prevNode.getCloudletName());
            prevCloudlet.incBand(tree.getConsuption());

            // persist changes
            cloudletStore.put(Constants.CLOUDLET, prevNode.getCloudletName(), prevCloudlet);
            treeStore.put(Constants.TREEINFO, treeName, tree);
        }

        // modify leaf node
        Cloudlet currCloudlet = cloudletStore.get(Constants.CLOUDLET, node.getCloudletName());
        currCloudlet.deleteTree(treeName);

        // persist changes
        cloudletStore.put(Constants.CLOUDLET, node.getCloudletName(), currCloudlet);
    }

    public void deleteNonLeaf(OverlayTree tree, TreeNode node, String treeName) {
        DataStoreJsonWrapper<Cloudlet> cloudletStore = new DataStoreJsonWrapper<>(Cloudlet.class);
        DataStoreJsonWrapper<OverlayTree> treeStore = new DataStoreJsonWrapper<>(OverlayTree.class);
        TreeNode prevNode = node.getPrevNode(tree);
        if(prevNode != null) {
            // modify prev cloudlet and tree structure
            // just change the current node to dummy node
            node.changeToDummy();
            Cloudlet prevCloudlet = cloudletStore.get(Constants.CLOUDLET, prevNode.getCloudletName());
            prevCloudlet.incBand(tree.getConsuption());

            // persist changes
            cloudletStore.put(Constants.CLOUDLET, prevNode.getCloudletName(), prevCloudlet);
            treeStore.put(Constants.TREEINFO, treeName, tree);
        }

        // modify leaf node
        Cloudlet currCloudlet = cloudletStore.get(Constants.CLOUDLET, node.getCloudletName());
        currCloudlet.deleteTree(treeName);

        // persist changes
        cloudletStore.put(Constants.CLOUDLET, node.getCloudletName(), currCloudlet);

    }
}
