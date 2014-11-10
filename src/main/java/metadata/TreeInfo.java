package metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anbang on 11/8/14.
 */
public class TreeInfo {
    public List<CloudletLatency> cloudletsLatency;
    // Bandwidth needed for this tree.
    double bandConsumption;

    public TreeInfo() {
        cloudletsLatency = new ArrayList<>();
    }

    public TreeInfo(double consume) {
        this();
        bandConsumption = consume;
    }

    public void addCloudlet(String name, double latency, double leftCap) {
        update(name, latency, leftCap);
    }

    public void update(String name, double latency, double leftCap) {
        for(CloudletLatency cloudlet : cloudletsLatency) {
            if(cloudlet.getName().equals(name)){
                cloudlet.setLatency(latency);
                return;
            }
        }

        CloudletLatency cloudlet = new CloudletLatency(name, latency, leftCap);
        cloudletsLatency.add(cloudlet);
    }

    public double getConsuption() {
        return bandConsumption;
    }
}
