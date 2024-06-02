package PHASE_C;

import gr.uoc.csd.hy463.TopicType;

public class Reader {

    private int number;
    private TopicType type;
    private String summary;
    private String description;

    public Reader(int number, TopicType type, String summary, String description) {
        this.number = number;
        this.type = type;
        this.summary = summary;
        this.description = description;
    }

    public int getNumber() {return number;}
    public TopicType getType() {return type;}
    public String getSummary() {return summary;}
    public String getDescription() {return description;}

}
