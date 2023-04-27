package bigdata.entity;

import bigdata.algorithm.Algorithm;
import bigdata.utils.CascadeGraphExtractor;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
@Data
public class Graph {
    private Map<String,Node> nodeSet;
    private Map<String,Node> S;
    private Map<Integer, Set<Node>> activatedNode;
    private int nodeNum;
    private int edgeNum;
    private int activatedNum;
    private String model;
    private int t;
    public Graph(String filepath) throws IOException {
        Stream<String> lines = Files.lines(Paths.get(filepath));
        Iterator<String> iterator=lines.iterator();
        int edgeNum=0;
        this.nodeSet=new HashMap<>();
        while(iterator.hasNext()){
            edgeNum++;
            String nextEdge=iterator.next();
            String[] nodes = nextEdge.split(" ");
            String out=nodes[0];
            String in=nodes[1];
            Edge edge=new Edge(out,in);
            Node inNode,outNode;
            if(nodeSet.containsKey(out)){
                outNode=nodeSet.get(out);
            }
            else{
                outNode=new Node(out);
                this.nodeSet.put(out,outNode);
            }
            if(nodeSet.containsKey(in)){
                inNode=nodeSet.get(in);
            }
            else{
                inNode=new Node(in);
                this.nodeSet.put(in,inNode);
            }
            outNode.addOutEdge(edge);
            inNode.addInEdge(edge);
        }
        this.edgeNum=edgeNum;
        this.nodeNum=this.nodeSet.size();
    }

    public void selectInfluencers(int k){
        if(k>=this.nodeNum) throw new RuntimeException("初始节点数量过大");
        this.S=new HashMap<>(k);
        PriorityQueue<Node> h=new PriorityQueue<>(this.nodeSet.values());
        for(int i=0;i<k;i++){
            Node s=h.poll();
            s.setInfluencer(true);
            this.S.put(s.getID(),s);
        }
    }
    public void setModel(String model){
        this.model=model;
    }
    public Map<String,Double> cascade(){
        for(Node node:this.S.values()){
            node.setTimestamp(0);
            node.setActivated(true);
        }
        this.activatedNode=new HashMap<>();
        this.activatedNum=0;
        if(this.model.equals("IC")){
            for(Node s:this.S.values()){
                s.addInfluencer(s.getID());
            }
            int t=0;
            Set<Node> recentActivatedNodeSet;
            recentActivatedNodeSet=new HashSet<>(this.S.values());
            this.activatedNode.put(t,recentActivatedNodeSet);
            do{
                t++;
                Set<Node> currentActivatedNodeSet=new HashSet<>();
                for(Node node:recentActivatedNodeSet){
                    for(Edge edge:node.getOutEdgeList()){
                        Node neighbor=this.nodeSet.get(edge.getIn());
                        if(!neighbor.isActivated()||neighbor.getTimestamp()==t){
                            if(Math.random()<=edge.getP()){
                                neighbor.setActivated(true);
                                neighbor.setTimestamp(t);
                                neighbor.unionInfluencers(node.getInfluencers());
                                currentActivatedNodeSet.add(neighbor);
                            }
                        }
                    }
                }
                if(!currentActivatedNodeSet.isEmpty()){
                    this.activatedNode.put(t,currentActivatedNodeSet);
                    this.activatedNum+=currentActivatedNodeSet.size();
                }
                recentActivatedNodeSet=currentActivatedNodeSet;
            }while(!recentActivatedNodeSet.isEmpty());
            this.t=t-1;
        }
        else if(this.model.equals("LT")){
            int t=0;
            Set<Node> recentActivatedNodeSet;
            recentActivatedNodeSet=new HashSet<>(this.S.values());
            this.activatedNode.put(t,recentActivatedNodeSet);
            do{
                t++;
                Set<Node> checkNodes=new HashSet<>();
                Set<Node> currentActivatedNodeSet=new HashSet<>();
                for(Node node:recentActivatedNodeSet){
                    for(Edge edge:node.getOutEdgeList()){
                        Node neighbor=this.nodeSet.get(edge.getIn());
                        if(!neighbor.isActivated()){
                            checkNodes.add(neighbor);
                        }
                    }
                }
                for(Node node:checkNodes){
                    double weight=0;
                    for(Edge edge: node.getInEdgeList()){
                        Node inverseNeighbor=this.nodeSet.get(edge.getOut());
                        if(inverseNeighbor.isActivated()){
                            weight+=edge.getP();
                        }
                    }
                    if(weight>=node.getThreshold()){
                        node.setActivated(true);
                        node.setTimestamp(t);
                        currentActivatedNodeSet.add(node);
                    }
                }
                if(!currentActivatedNodeSet.isEmpty()){
                    this.activatedNode.put(t,currentActivatedNodeSet);
                    this.activatedNum+=currentActivatedNodeSet.size();
                }
                recentActivatedNodeSet=currentActivatedNodeSet;
            }while (!recentActivatedNodeSet.isEmpty());
            this.t=t-1;
        }
        return null;
    }
    public void initialGraph(){
        initialEdgeProbability();
        if(this.model.equals("LT"))
            initializeNodeThreshold();
    }
    private void initializeNodeThreshold(){
        for(Node node:this.nodeSet.values()){
            node.setThreshold(Math.random());
        }
    }
    private void initialEdgeProbability(){
        for(Node node:this.nodeSet.values()){
            double p=1.0/node.getInDegree();
            for(Edge edge:node.inEdgeList){
                edge.setP(p);
            }
        }
    }

}
