package bigdata.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Node implements Comparable{
    private final String ID;
    List<Edge> outEdgeList;  //出边
    List<Edge> inEdgeList;  //入边

    boolean activated;   //是否激活
    boolean isInfluencer; //是否是起始节点
    int timestamp;     //激活的时间戳

    int inDegree;
    int outDegree;
    double threshold;
    Set<String> influencers;
    public Node(String ID){
        this.ID=ID;
        this.outEdgeList=new ArrayList<>();
        this.inEdgeList=new ArrayList<>();
        this.outDegree=0;
        this.inDegree=0;
        this.activated=false;
        this.influencers=new HashSet<>();
        this.timestamp=999;
    }
    public void addOutEdge(Edge outEdge){
        this.outEdgeList.add(outEdge);
        this.outDegree++;
    }
    public void addInEdge(Edge inEdge){
        this.inEdgeList.add(inEdge);
        this.inDegree++;
    }

    @Override
    public int compareTo(Object o) {
        Node other=(Node)o;
        return other.getOutDegree()-this.outDegree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return ID.equals(node.ID);
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }
    @Override
    public String toString(){
        return this.ID;
    }
    public void addInfluencer(String ID){
        this.influencers.add(ID);
    }
    public void unionInfluencers(Set<String> set){
        this.influencers.addAll(set);
    }
}
