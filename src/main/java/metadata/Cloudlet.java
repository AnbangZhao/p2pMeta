package metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anbang on 11/10/14.
 */
public class Cloudlet {
    private double currBand;
    private List<String> inTrees;

    public Cloudlet() {
        inTrees = new ArrayList<>();
    }

    public Cloudlet(double fullBand) {
        currBand = fullBand;
        inTrees = new ArrayList<>();
    }

    public void addIntoTree(String treeName) {
        for(String tree : inTrees){
            if(tree.equals(treeName)){
                return;
            }
        }
        inTrees.add(treeName);
    }

    public double getCurrBand() {
        return currBand;
    }

    public void decBand(double band) {
        currBand -= band;
    }

    public void incBand(double band) {
        currBand += band;
    }

    public void deleteTree(String treeName) {
        for(int i = 0; i < inTrees.size(); i++) {
            if(inTrees.get(i).equals(treeName)) {
                inTrees.remove(i);
            }
        }
    }

}
