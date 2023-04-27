package bigdata.entity;

import lombok.Data;

@Data
public class Record {
    String ID;
    int count;
    double threshold;
    double score;
    public Record(String ID,double threshold){
        this.ID=ID;
        this.threshold=threshold;
        this.count=0;
        this.score=0.0;
    }
    public void addScore(double score){
        this.score+=score;
        this.count++;
    }
    public boolean isSatisfied(){
        return this.score>this.threshold;
    }
}
