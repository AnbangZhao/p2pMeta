package server.common;

import datastore.DataStoreJsonWrapper;
import metadata.Cloudlet;
import metadata.TreeNode;
import server.Constants;

/**
 * Created by anbang on 11/30/14.
 */
public class NodeManager {
    public void deleteDesc(TreeNode node, TreeNode toDelNode, double delNodeBand) {
        node.delDesc(toDelNode.getCloudletName());
        DataStoreJsonWrapper<Cloudlet> cloudletStore = new DataStoreJsonWrapper<>(Cloudlet.class);
        Cloudlet cloudlet = cloudletStore.get(Constants.CLOUDLET, node.getCloudletName());
        cloudlet.incBand(delNodeBand);
        cloudletStore.put(Constants.CLOUDLET, node.getCloudletName(), cloudlet);
    }
}
