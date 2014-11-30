package server;

import datastore.DataStoreJsonWrapper;
import metadata.*;
import server.common.NodeManager;
import server.common.TreeScheduler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

/**
 * Created by anbang on 11/8/14.
 * The node will join the tree if the tree exists.
 * If not, return 404.
 *
 * Parameters:
 * treename : 'treename'
 */
public class JoinTreeServlet extends HttpServlet{
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
        Cloudlet cloudlet = cloudletStore.get(Constants.CLOUDLET, cloudletname);

        // tree is null or cloudlet is null. Tell the cloudlet to do nothing
        if(tree == null || cloudlet == null) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }


        TreeNode currNode = tree.findNode(cloudletname);
        TreeScheduler scheduler = new TreeScheduler(tree, treename);
        // if node exits. re-schedule it to a node that is not in the subtree of its prev
        // if the prev node is a root, just reschedule it
        TreeNode schedNode = null;
        if(currNode != null) {
            TreeNode prevNode = currNode.getPrevNode(tree);
            if(prevNode == null) {
                // schedule to a node
                schedNode = scheduler.addTreeNode(currNode, null, null);
            }
            else {
                // delete currNode from prevNode
                NodeManager nodeManager = new NodeManager();
                nodeManager.deleteDesc(prevNode, currNode, tree.getConsuption());
                if (tree.isRoot(prevNode)) {
                    // schedule to a node that is not root
                    schedNode = scheduler.addTreeNode(currNode, prevNode, null);
                } else {
                    // schedule to a node that is not in the subtree of its prev
                    schedNode = scheduler.addTreeNode(currNode, null, prevNode);
                }
            }
        }
        else {
            currNode = new TreeNode(cloudletname);
            schedNode = scheduler.addTreeNode(currNode, null, null);
            // schedule to a node
        }
        resp.getWriter().write(schedNode.getCloudletName());
//
//        TreeNode schedNode = schedule(tree);
//        TreeNode newNode = new TreeNode(cloudletname, schedNode.getLatency() + 1);
//        tree.addNode(newNode, schedNode);
//        Cloudlet prevNodeCLoudlet = cloudletStore.get(Constants.CLOUDLET, schedNode.getCloudletName());
//        prevNodeCLoudlet.decBand(tree.getConsuption());
//        cloudlet.addIntoTree(treename);
//
//        treeStore.put(Constants.TREEINFO, treename, tree);
//        cloudletStore.put(Constants.CLOUDLET, cloudletname, cloudlet);
//        cloudletStore.put(Constants.CLOUDLET, schedNode.getCloudletName(), prevNodeCLoudlet);
    }

    private TreeNode schedule(OverlayTree tree) {
        TreeNode treeRoot = tree.getRoot();
        double cost = tree.getConsuption();
        TreeNode schedNode = bfs(treeRoot, cost);
        if(schedNode == null) {
            schedNode = randomPick(tree);
        }
        return schedNode;
    }

    private TreeNode bfs(TreeNode root, double capCost) {
        Queue<TreeNode> bfsQueue = new ArrayDeque<>();
        TreeNode ret = null;
        bfsQueue.add(root);
        while (bfsQueue.size() > 0) {
            TreeNode curr = bfsQueue.poll();
            if(isGoodNode(curr, capCost)) {
                ret = curr;
                break;
            }
            for(TreeNode node : curr.getDescedents()) {
                bfsQueue.add(node);
            }
        }
        return ret;
    }

    private TreeNode randomPick(OverlayTree tree) {
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
        return (cloudlet.getCurrBand() - cost >= 0);
    }

    private CloudletLatency schedule1(TreeInfo tree) {
        CloudletLatency minWithCap = null;
        CloudletLatency minWithoutCap = null;

        CloudletLatency tmpMin = new CloudletLatency("tmp", 99999, 1);

        for(CloudletLatency cloudlet : tree.cloudletsLatency) {
            if(cloudlet.getLatency() < tmpMin.getLatency()) {
                tmpMin = cloudlet;
                minWithoutCap = cloudlet;
                if(cloudlet.getLeftCapacity() - tree.getConsuption() >= 0) {
                    minWithCap = cloudlet;
                }
            }
        }

        CloudletLatency ret = minWithCap != null ? minWithCap : minWithoutCap;
        return ret;
    }

    private void editNode(CloudletLatency p, TreeInfo tree, String currName, CloudletInfo prev) {
        p.decLeftCapacity(tree.getConsuption());
        prev.addDescedent(currName, tree.getConsuption());
    }

    private void addIntoTree(String cloudletName, TreeInfo tree, CloudletLatency prevNode, CloudletInfo currNode) {
        double latency = prevNode.getLatency() + 1;
        double leftCap = currNode.getCapacity();
        tree.addCloudlet(cloudletName, latency, leftCap);
    }
}
