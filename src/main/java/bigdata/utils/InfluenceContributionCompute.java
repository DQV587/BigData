package bigdata.utils;

import bigdata.entity.CascadeGraph;
import bigdata.entity.CascadeNode;
import bigdata.entity.Graph;
import bigdata.entity.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InfluenceContributionCompute {
    public static Map<String,Double> DEG1(Graph g,Map<String, Node> S){
        Map<String,Double> results=new HashMap<>();
        for(Node node:S.values()){
            results.put(node.getID(), (double) node.getOutDegree());
        }
        return results;
    }
    public static Map<String,Double> DEG2(CascadeGraph g){
        Map<String,Double> results=new HashMap<>();
        for(CascadeNode node:g.getNodeByTime().get(0)){
            results.put(node.getID(), (double) node.getOutEdgeList().size());
        }
        return results;
    }
    public static Map<String,Double> IC_exactContribution(Graph g,Map<String,Node> S){
        Map<String,Double> result=new HashMap<>();
        for(Node node:S.values()){
            node.addInfluencer(node.getID());
            result.put(node.getID(),-1.0);
        }
        for(Set<Node> nodeSet: g.getActivatedNode().values()){
            for(Node node:nodeSet){
                double num=node.getInfluencers().size();
                for(String s: node.getInfluencers()){
                    result.put(s,result.get(s)+1.0/num);
                }
            }
        }
        return result;
    }

}
