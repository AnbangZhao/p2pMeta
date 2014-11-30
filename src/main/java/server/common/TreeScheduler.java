package server.common;

import datastore.DataStoreJsonWrapper;
import metadata.Cloudlet;
import metadata.OverlayTree;
import metadata.TreeNode;
import server.Constants;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

/**
 * Created by anbang on 11/11/14.
 */
public class TreeScheduler {
    String treeName;
    private OverlayTree tree;

    public TreeScheduler(OverlayTree t, String name) {
        tree = t;
        treeName = name;
    }

    public TreeNode addTreeNode(TreeNode currNode, TreeNode banNode, TreeNode banSubTreeRoot) {
        DataStoreJsonWrapper<OverlayTree> treeStore = new DataStoreJsonWrapper<>(OverlayTree.class);
        DataStoreJsonWrapper<Cloudlet> cloudletStore = new DataStoreJsonWrapper<>(Cloudlet.class);

        TreeNode schedNode = schedule(banNode, banSubTreeRoot);
        tree.addNode(currNode, schedNode);
        Cloudlet prevNodeCLoudlet = cloudletStore.get(Constants.CLOUDLET, schedNode.getCloudletName());
        Cloudlet currNodeCloudlet = cloudletStore.get(Constants.CLOUDLET, currNode.getCloudletName());
        prevNodeCLoudlet.decBand(tree.getConsuption());
        currNodeCloudlet.addIntoTree(treeName);

        treeStore.put(Constants.TREEINFO, treeName, tree);
        cloudletStore.put(Constants.CLOUDLET, currNode.getCloudletName(), currNodeCloudlet);
        cloudletStore.put(Constants.CLOUDLET, schedNode.getCloudletName(), prevNodeCLoudlet);

        return schedNode;
    }

    private TreeNode schedule(TreeNode banNode, TreeNode banSubTreeRoot) {
        TreeNode treeRoot = tree.getRoot();
        double cost = this.tree.getConsuption();
        TreeNode schedNode = bfs(treeRoot, cost, banNode, banSubTreeRoot);
        if(schedNode == null) {
            schedNode = randomPick();
        }
        return schedNode;
    }

        private TreeNode bfs(TreeNode root, double capCost, TreeNode banNode, TreeNode banSubTreeRoot) {
        Queue<TreeNode> bfsQueue = new ArrayDeque<>();
        TreeNode ret = null;
        bfsQueue.add(root);
        while (bfsQueue.size() > 0) {
            TreeNode curr = bfsQueue.poll();
            if(curr == banSubTreeRoot) {
                continue;
            }
            if(isGoodNode(curr, capCost) && curr != banNode) {
                ret = curr;
                break;
            }
            for(TreeNode node : curr.getDescedents()) {
                bfsQueue.add(node);
            }
        }
        return ret;
    }

        private TreeNode randomPick() {
        int counter = tree.getCounter();
        TreeNode root = tree.getRoot();
        Random rand = new Random();
        int randNum = rand.nextInt(counter);
        TreeNode ret = getIthNode(root, randNum);
        return ret;
    }

        TreeNode getIthNode (TreeNode root, int i) {
        Stack<TreeNode> st = new Stack<>();
        st.push(root);
        while (!st.empty()) {
            TreeNode curr = st.pop();
            if(i == 0) {
                return curr;
            }

            for(TreeNode node : curr.getDescedents()) {
                st.push(node);
            }
            i--;
        }
        return null;
    }

        private boolean isGoodNode(TreeNode node, double cost) {
        DataStoreJsonWrapper<Cloudlet> cloudletStore = new DataStoreJsonWrapper<>(Cloudlet.class);
        Cloudlet cloudlet = cloudletStore.get(Constants.CLOUDLET, node.getCloudletName());
        return (cloudlet.getCurrBand() - cost >= 0) && node.isHealthy();
    }
}
