package bigdata.entity;

import lombok.Data;

import java.util.Map;
import java.util.Set;
@Data
public class CascadeGraph {
    private Map<String, CascadeNode> nodeSet;
    private Map<Integer, Set<CascadeNode>> nodeByTime;
    private int t;
    public CascadeGraph(Map<String, CascadeNode> nodeSet, Map<Integer, Set<CascadeNode>> nodeByTime, int t){
        this.nodeSet=nodeSet;
        this.nodeByTime=nodeByTime;
        this.t=t;
    }
}
