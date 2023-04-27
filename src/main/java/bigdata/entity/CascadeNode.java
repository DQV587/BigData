package bigdata.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class CascadeNode {
    private final String ID;
    List<Edge> outEdgeList;  //出边
    List<Edge> inEdgeList;  //入边
    Set<String> influencers;
    int timestamp;     //激活的时间戳
    double pLeastOneIncoming;
    public CascadeNode(String ID, int timestamp){
        this.ID=ID;
        this.timestamp=timestamp;
        this.inEdgeList=new ArrayList<>();
        this.outEdgeList=new ArrayList<>();
        this.influencers=new HashSet<>();
    }
    public void addOutEdge(Edge outEdge){
        this.outEdgeList.add(outEdge);
    }
    public void addInEdge(Edge inEdge){
        this.inEdgeList.add(inEdge);
    }
    public void addInfluencer(String influencerID){
        this.influencers.add(influencerID);
    }
    public void unionInfluencers(Set<String> influencers){
        this.influencers.addAll(influencers);
    }
    public void resetInfluencers(){
        this.influencers.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CascadeNode node = (CascadeNode) o;

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
}
