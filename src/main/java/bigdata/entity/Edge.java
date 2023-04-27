package bigdata.entity;

import lombok.Data;

@Data
public class Edge {
    private final String out;   //发出的点
    private final String in;  //指向的点
    private double p;  //概率
    public Edge(String out,String in){
        this.out=out;
        this.in=in;
    }
}
