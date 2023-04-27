package bigdata;

import bigdata.algorithm.Algorithm;
import bigdata.entity.CascadeGraph;
import bigdata.entity.Graph;
import bigdata.utils.CascadeGraphExtractor;
import bigdata.utils.InfluenceContributionCompute;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;

public class Experiment {
    public static void main(String[] args) throws IOException {
        LT_Model("facebook");
        LT_Model("twitter");
        IC_Model("facebook");
        IC_Model("twitter");
    }
    private static void LT_Model(String filename) throws IOException {
        String filepath="src/main/resources/"+filename+".txt";
        String resultPath="src/main/results/LT_"+filename+".csv";
        try(BufferedWriter writer= Files.newBufferedWriter(Paths.get(resultPath), StandardCharsets.UTF_8)){
            writer.write("k,al1,deg1,deg2\n");
            for(int k=10;k<=200;k++){
                Graph graph=new Graph(filepath);
                graph.selectInfluencers(k);
                graph.setModel("LT");
                graph.initialGraph();
                graph.cascade();
                CascadeGraph D=CascadeGraphExtractor.D_Extractor(graph);
                Map<String,Double> realResult=Algorithm.Algorithm1(D);
                Map<String,Double> DEG1= InfluenceContributionCompute.DEG1(graph,graph.getS());
                Map<String,Double> DEG2= InfluenceContributionCompute.DEG2(D);
                Set<String> influencers=graph.getS().keySet();
                double deg1=0;
                double deg2=0;
                int num=graph.getActivatedNum();
                for(String s:influencers){
                    deg1+=Math.pow((realResult.get(s)-DEG1.get(s)),2);
                    deg2+=Math.pow((realResult.get(s)-DEG2.get(s)),2);
                }
                DecimalFormat df = new DecimalFormat( "#.00");
                writer.write(k+","+"0.00"+","+df.format(deg1/k)+","+df.format(deg2/k)+"\n");
            }
        }
    }
    private static void IC_Model(String filename) throws IOException {
        String filepath="src/main/resources/"+filename+".txt";
        String resultPath="src/main/results/IC_"+filename+".csv";
        try(BufferedWriter writer= Files.newBufferedWriter(Paths.get(resultPath), StandardCharsets.UTF_8)){
            writer.write("k,al4,deg1,deg2\n");
            for(int k=10;k<=200;k++){
                Graph graph=new Graph(filepath);
                graph.selectInfluencers(k);
                graph.setModel("IC");
                graph.initialGraph();
                graph.cascade();
                CascadeGraph D=CascadeGraphExtractor.D_Extractor(graph);
                Map<String,Double> realResult=InfluenceContributionCompute.IC_exactContribution(graph,graph.getS());
                Map<String,Double> result=Algorithm.Algorithm4(D,0.5,0.1);
                Map<String,Double> DEG1= InfluenceContributionCompute.DEG1(graph,graph.getS());
                Map<String,Double> DEG2= InfluenceContributionCompute.DEG2(D);
                Set<String> influencers=graph.getS().keySet();
                double al4=0;
                double deg1=0;
                double deg2=0;
                for(String s:influencers){
                    al4+=Math.pow((realResult.get(s)-result.get(s)),2);
                    deg1+=Math.pow((realResult.get(s)-DEG1.get(s)),2);
                    deg2+=Math.pow((realResult.get(s)-DEG2.get(s)),2);
                }
                DecimalFormat df = new DecimalFormat("#.00");
                writer.write(k+","+df.format(al4/k)+","+df.format(deg1/k)+","+df.format(deg2/k)+"\n");
            }
        }
    }
}
