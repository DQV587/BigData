package bigdata.utils;

import bigdata.entity.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public  class CascadeGraphExtractor {
    public static CascadeGraph D_Extractor(Graph originGraph){
        if(originGraph.getModel().equals("IC")){
            int t=originGraph.getT();
            Map<String,Node> nodeSet=originGraph.getNodeSet();
            Map<Integer, Set<CascadeNode>> nodeByTime=new HashMap<>(t+1);
            Map<String, CascadeNode> CNodeSet=new HashMap<>();
            for(;t>=0;t--){
                Set<Node> tmpNodeSet=originGraph.getActivatedNode().get(t);
                Set<CascadeNode> tmpCNodeSet=new HashSet<>(tmpNodeSet.size());
                for(Node node:tmpNodeSet){
                    CascadeNode tmpNode=new CascadeNode(node.getID(),node.getTimestamp());
                    tmpCNodeSet.add(tmpNode);
                    CNodeSet.put(tmpNode.getID(),tmpNode);
                    for(Edge edge:node.getInEdgeList()){
                        Node inverseNeighbor=nodeSet.get(edge.getOut());
                        if(inverseNeighbor.isActivated()&&inverseNeighbor.getTimestamp()==t-1){
                            tmpNode.addInEdge(edge);
                        }
                    }
                }
                nodeByTime.put(t,tmpCNodeSet);
            }
            for(CascadeNode node:CNodeSet.values()){
                double totalP=1;
                for(Edge edge: node.getInEdgeList()){
                    CNodeSet.get(edge.getOut()).addOutEdge(edge);
                    totalP=totalP*(1- edge.getP());
                }
                totalP=1-totalP;
                node.setPLeastOneIncoming(totalP);
            }
            return new CascadeGraph(CNodeSet,nodeByTime,originGraph.getT());
        }
        else if(originGraph.getModel().equals("LT")){
            int t=originGraph.getT();
            Map<String,Node> nodeSet=originGraph.getNodeSet();
            Map<Integer, Set<CascadeNode>> nodeByTime=new HashMap<>(t+1);
            Map<String, CascadeNode> CNodeSet=new HashMap<>();
            for(;t>=0;t--){
                Set<Node> tmpNodeSet=originGraph.getActivatedNode().get(t);
                Set<CascadeNode> tmpCNodeSet=new HashSet<>(tmpNodeSet.size());
                for(Node node:tmpNodeSet){
                    CascadeNode tmpNode=new CascadeNode(node.getID(),node.getTimestamp());
                    tmpCNodeSet.add(tmpNode);
                    CNodeSet.put(tmpNode.getID(),tmpNode);
                    for(Edge edge:node.getInEdgeList()){
                        Node inverseNeighbor=nodeSet.get(edge.getOut());
                        if(inverseNeighbor.isActivated()&&inverseNeighbor.getTimestamp()<t){
                            tmpNode.addInEdge(edge);
                        }
                    }
                }
                nodeByTime.put(t,tmpCNodeSet);
            }
            for(CascadeNode node:CNodeSet.values()){
                double totalP=0;
                for(Edge edge: node.getInEdgeList()){
                    CNodeSet.get(edge.getOut()).addOutEdge(edge);
                    totalP+= edge.getP();
                }
                node.setPLeastOneIncoming(totalP);
            }
            return new CascadeGraph(CNodeSet,nodeByTime,originGraph.getT());
        }
        return null;
    }
}
