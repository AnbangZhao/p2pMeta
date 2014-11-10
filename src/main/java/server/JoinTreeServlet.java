package server;

import datastore.DataStoreJsonWrapper;
import metadata.CloudletInfo;
import metadata.CloudletLatency;
import metadata.TreeInfo;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        String cloudletname = req.getRemoteAddr();
        if(treename == null || cloudletname == null){
            resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            return;
        }

        DataStoreJsonWrapper<TreeInfo> treeStore = new DataStoreJsonWrapper<>(TreeInfo.class);
        DataStoreJsonWrapper<CloudletInfo> cloudletStore = new DataStoreJsonWrapper<>(CloudletInfo.class);

        TreeInfo tree = treeStore.get(Constants.TREEINFO, treename);
        CloudletInfo cloudlet = cloudletStore.get(Constants.CLOUDLETINFO, cloudletname);
        // schedCloudlet is the one this cloudlet will connect to
        CloudletLatency schedCloudlet = schedule(tree);
        CloudletInfo prevNode = cloudletStore.get(Constants.CLOUDLETINFO, schedCloudlet.getName());
        editNode(schedCloudlet, tree, cloudletname, prevNode);
        addIntoTree(cloudletname, tree, schedCloudlet, cloudlet);

        treeStore.put(Constants.TREEINFO, treename, tree);
        cloudletStore.put(Constants.CLOUDLETINFO, cloudletname, cloudlet);
        cloudletStore.put(Constants.CLOUDLETINFO, schedCloudlet.getName(), prevNode);
    }

    private CloudletLatency schedule(TreeInfo tree) {
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
