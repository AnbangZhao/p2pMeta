package metadata;

import server.common.TreeManager;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by anbang on 11/10/14.
 */
public class OverlayTree {
    TreeNode root;
    double bandConsump;
    int nodeCounter;

    public OverlayTree() {

    }

    public OverlayTree(double consume) {
        nodeCounter = 0;
        bandConsump = consume;
        root = null;
    }

    public OverlayTree(TreeNode node, double consume) {
        nodeCounter = 1;
        root = node;
        bandConsump = consume;
    }

    public TreeNode getRoot() {
        return root;
    }

    public double getConsuption() {
        return bandConsump;
    }

    public int getCounter() {
        int cnt = 0;
        Queue<TreeNode> bfsQueue = new ArrayDeque<>();
        bfsQueue.add(root);
        while (bfsQueue.size() > 0) {
            TreeNode curr = bfsQueue.poll();
            cnt++;
            for(TreeNode node : curr.getDescedents()) {
                bfsQueue.add(node);
            }
        }
        return cnt;
    }

    public void addNode(TreeNode currNode, TreeNode prevNode) {
        nodeCounter++;
        prevNode.addDescendent(currNode);
        currNode.setPrevNode(prevNode);
        currNode.setLatency(prevNode.getLatency() + 1);
    }

    public TreeNode removeNode(String name) {
        TreeNode toDel = findNode(name);
        if(toDel == null) {
            return null;
        }

        TreeNode prev = toDel.getPrevNode(this);
        if(prev == null) {
            destroyTree(name);
        }
        else {
            prev.delDesc(toDel.getCloudletName());
        }
        return toDel;
    }

    public void destroyTree(String name) {
        TreeManager manager = new TreeManager();
        manager.destroyTree(this, name);
    }

    public void exitLeaf(TreeNode node, String treeName) {
        TreeManager manager = new TreeManager();
        manager.deleteLeaf(this, node, treeName);

    }

    public void exitNonLeaf(TreeNode node, String treeName) {
        TreeManager manager = new TreeManager();
        manager.deleteNonLeaf(this, node, treeName);
    }

    public TreeNode findNode(String name) {
        Stack<TreeNode> st = new Stack<>();
        st.push(root);
        while (!st.empty()) {
            TreeNode curr = st.pop();
            if(curr.getCloudletName().equals(name)) {
                return curr;
            }

            for(TreeNode node : curr.getDescedents()) {
                st.push(node);
            }

        }
        return null;
    }

    public boolean isRoot(TreeNode node) {
        return node.getCloudletName().equals(root.cloudletName);
    }
}
