package metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anbang on 11/8/14.
 */
public class CloudletInfo {
    double bandCap;
    List<TreeOnCloudletInfo> treesinfo;
    List<String> descendents;

    public CloudletInfo() {

    }

    public CloudletInfo(double cap) {
        bandCap = cap;
        treesinfo = new ArrayList<>();
        descendents = new ArrayList<>();
    }

    public void addTree(TreeOnCloudletInfo treeInfo) {
        treesinfo.add(treeInfo);
    }

    public void addTree(String treeName, double consumeCap, String status) {
        TreeOnCloudletInfo treeInfo = new TreeOnCloudletInfo(treeName, consumeCap, status);
        addTree(treeInfo);
    }

    public void addDescedent(String descName, double consume) {
        descendents.add(descName);
        bandCap -= consume;
    }

    public double getCapacity() {
        return bandCap;
    }
}