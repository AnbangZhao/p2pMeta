package metadata;

/**
 * Created by anbang on 11/8/14.
 */
public class TreeOnCloudletInfo {
    String treeName;
    double consumedCapacity;
    String status;

    public TreeOnCloudletInfo() {

    }

    public TreeOnCloudletInfo(String name, double cap, String st) {
        treeName = name;
        consumedCapacity = cap;
        status = st;
    }
}
