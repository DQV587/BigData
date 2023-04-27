package bigdata.algorithm;

import bigdata.entity.CascadeGraph;
import bigdata.entity.CascadeNode;
import bigdata.entity.Edge;
import bigdata.entity.Record;

import java.util.*;

public class Algorithm {
    public static Map<String,Double> Algorithm1(CascadeGraph g){
        Map<String,Double> influenceContribution=new HashMap<>();
        Map<String,Double> result=new HashMap<>();
        Map<String,CascadeNode> nodeMap=g.getNodeSet();
        int t=g.getT();
        for(;t>=0;t--){
            for(CascadeNode node:g.getNodeByTime().get(t)){
                double contribution=0;
                for(Edge edge:node.getOutEdgeList()){
                    CascadeNode neighbor=nodeMap.get(edge.getIn());
                    contribution+=(edge.getP()/neighbor.getPLeastOneIncoming())*(1+influenceContribution.get(edge.getIn()));
                }
                influenceContribution.put(node.getID(),contribution);
                if(t==0){
                    result.put(node.getID(),contribution);
                }
            }
        }
        return result;
    }

    public static Map<String,Double> Algorithm2(CascadeGraph g){
        Map<String,CascadeNode> nodeMap=g.getNodeSet();
        for(CascadeNode node:nodeMap.values()){
            node.resetInfluencers();
        }
        Map<String,Double> result=new HashMap<>(g.getNodeByTime().get(0).size());
        for(CascadeNode node:g.getNodeByTime().get(0)){
            node.addInfluencer(node.getID());
            result.put(node.getID(),-1.0);
        }
        int t=g.getT();
        for(int j=1;j<=t;j++){
            for(CascadeNode node:g.getNodeByTime().get(j)){
                boolean flag=false;
                for(Edge edge:node.getInEdgeList()){
                    if(!flag){
                        if(Math.random()<=((edge.getP())/ node.getPLeastOneIncoming())){
                            flag=true;
                            node.unionInfluencers(nodeMap.get(edge.getOut()).getInfluencers());
                        }
                    }
                    else {
                        if(Math.random()<= edge.getP()){
                            node.unionInfluencers(nodeMap.get(edge.getOut()).getInfluencers());
                        }
                    }
                }
            }
        }
        for(CascadeNode node: nodeMap.values()){
            double num=node.getInfluencers().size();
            for(String s: node.getInfluencers()){
                result.put(s,result.get(s)+1.0/num);
            }
        }
        return result;
    }

    public static Map<String, List<Integer>> Algorithm3(CascadeGraph g){
        Map<String,CascadeNode> nodeMap=g.getNodeSet();
        Map<String,List<Integer>> result=new HashMap<>(g.getNodeByTime().get(0).size());
        for(CascadeNode node:g.getNodeByTime().get(0)){
            node.addInfluencer(node.getID());
            int a=-1,b=-1;
            List<Integer> tmp=new ArrayList<>(2);
            tmp.add(a);
            tmp.add(b);
            result.put(node.getID(),tmp);
        }
        int t=g.getT();
        for(int i=1;i<=t;i++){
            for(CascadeNode node:g.getNodeByTime().get(i)){
                for(Edge edge: node.getInEdgeList()){
                    node.unionInfluencers(nodeMap.get(edge.getOut()).getInfluencers());
                }
            }
        }
        for(CascadeNode node:nodeMap.values()){
            for(String ID:node.getInfluencers()){
                result.get(ID).set(1,result.get(ID).get(1)+1);
            }
            if(node.getInfluencers().size()==1){
                for(String ID:node.getInfluencers()) {
                    result.get(ID).set(0,result.get(ID).get(0)+1);
                }
            }
        }
        return result;
    }

    public static Map<String,Double> Algorithm4(CascadeGraph D,double e,double p){
        Map<String,List<Integer>> AsBs=Algorithm3(D);
        Map<String, Record> recordMap=new HashMap<>();
        for(Map.Entry<String,List<Integer>> entry:AsBs.entrySet()){
            double a=entry.getValue().get(0);
            double b=entry.getValue().get(1);
            double tmp=2*(b-a)*(1+e)*((b-a)/b+e/3)*Math.log(2/p)/(e*e);
            recordMap.put(entry.getKey(), new Record(entry.getKey(), tmp));
        }
        boolean flag=true;
        while(flag){
            Map<String,Double> contribution=Algorithm2(D);
            int count=0;
            for(Map.Entry<String ,Double> entry:contribution.entrySet()){
                Record record=recordMap.get(entry.getKey());
                if(!record.isSatisfied())
                    recordMap.get(entry.getKey()).addScore(entry.getValue());
                else
                    count++;
            }
            if(count==recordMap.size())
                flag=false;
        }
        Map<String,Double> result=new HashMap<>(recordMap.size());
        for(Record record: recordMap.values()){
            result.put(record.getID(),record.getScore()/record.getCount());
        }
        return result;
    }
}
