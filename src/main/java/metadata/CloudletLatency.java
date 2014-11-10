package metadata;

/**
 * Created by anbang on 11/8/14.
 */
public class CloudletLatency {
    private String cloudletName;
    private double latency;
    private double leftCapacity;

    public CloudletLatency() {

    }

    public CloudletLatency(String name, double la, double cap) {
        cloudletName = name;
        latency = la;
        leftCapacity = cap;
    }

    public String getName() {
        return cloudletName;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double la) {
        latency = la;
    }

    public double getLeftCapacity() {
        return leftCapacity;
    }

    public void decLeftCapacity(double volumn) {
        leftCapacity -= volumn;
    }

    public void incLeftCapacity(double volumn) {
        leftCapacity += volumn;
    }
}
