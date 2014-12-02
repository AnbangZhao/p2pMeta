package metadata;

import server.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anbang on 11/10/14.
 */
public class TreeNode {
    String cloudletName;
    double latency;
    List<TreeNode> descedents;
    String prevNodeName;
    long timestamp;
    //TreeNode prevNode;

    public TreeNode() {
        descedents = new ArrayList<>();
        prevNodeName = null;
        timestamp = currTimestamp();
    }

    public TreeNode(String name) {
        this();
        cloudletName = name;
        latency = 0;
    }

    public TreeNode (String name, double la) {
        this();
        cloudletName = name;
        latency = la;
    }

    public void addDescendent(TreeNode node) {
        descedents.add(node);
    }

    public List<TreeNode> getDescedents() {
        return descedents;
    }

    public String getCloudletName() {
        return cloudletName;
    }

    public double getLatency() {
        return latency;
    }

    public TreeNode getPrevNode(OverlayTree root) {

        return root.findNode(prevNodeName);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setPrevNode(TreeNode prev) {
        prevNodeName = prev.getCloudletName();
    }

    public void delDesc(String toDelName) {
        for(int i = 0; i < descedents.size(); i++) {
            if(descedents.get(i).getCloudletName().equals(toDelName)) {
                descedents.remove(i);
                break;
            }
        }
    }

    public void setLatency(double newLatency) {
        latency = newLatency;
    }

    public void updateTimestamp() {
        timestamp = currTimestamp();
    }

    public boolean isLeaf() {
        return descedents.size() == 0;
    }

    public boolean hasNewDesc() {
        long currTime = currTimestamp();
        for(TreeNode node : descedents) {
            if(currTimestamp() - node.getTimestamp() < Constants.CHECK_DURATION) {
                return true;
            }
        }
        return false;
    }

    public void changeToDummy() {
        cloudletName = "dummyNode";
    }

    public boolean isDummy() {
        return cloudletName == "dummyNode";
    }

    public boolean isHealthy() {
        return !isDummy() && currTimestamp() - timestamp < Constants.MAX_DURATION;
    }

    private long currTimestamp() {
        return System.currentTimeMillis()/1000l;
    }
}
