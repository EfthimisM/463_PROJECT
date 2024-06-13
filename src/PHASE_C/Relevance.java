package PHASE_C;

import java.util.ArrayList;

/**
 * Every topic will have one of those
 */
public class Relevance {

    private int id;
    private ArrayList<Integer> high;
    private ArrayList<Integer> medium;
    private ArrayList<Integer> low;

    /**
     *
     * @param id The id of the topic.
     */
    public Relevance(int id){
        this.id = id;
        high = new ArrayList<>();
        medium = new ArrayList<>();
        low = new ArrayList<>();
    }

    public int getId() {return id;}
    public ArrayList<Integer> getHigh() {return high;}
    public ArrayList<Integer> getMedium() {return medium;}
    public ArrayList<Integer> getLow() {return low;}

    public double getHighRation(){
        int sum = (high.size() + medium.size() + low.size());
        return (double) high.size() / sum;
    }

    public double getMediumRation(){
        int sum = (high.size() + medium.size() + low.size());
        return (double) medium.size() / sum;
    }

    public double getLowRatio(){
        int sum = (high.size() + medium.size() + low.size());
        return (double) low.size() / sum;
    }

    public int getRelevant(){
        return high.size() + medium.size();
    }

    public void addHigh(int val){high.add(val);}
    public void addMedium(int val){medium.add(val);}
    public void addLow(int val){low.add(val);}

    public void print(){
        System.out.println();
        System.out.println("ID: " + id );
        System.out.println();
        System.out.println("High:");
        for(Integer i : high){
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("TOTAL NUMBER: "+ high.size());
        System.out.println("Medium:");
        for(Integer i : medium){
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("TOTAL NUMBER: "+ medium.size());
        System.out.println("Low:");
        for(Integer i : low){
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("TOTAL NUMBER: "+ low.size());
        System.out.println();
        int sum = (high.size() + medium.size() + low.size());
        System.out.println("SUM: " + sum);
        System.out.println();
        System.out.println("HIGH RATION: "+ (double)high.size()/sum +"\tMEDIUM RATIO: "+(double)medium.size()/sum +"\tLOW RATION: "+(double)low.size()/sum);
    }

}
