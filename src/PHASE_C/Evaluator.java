package PHASE_C;
import gr.uoc.csd.hy463.Topic;
import gr.uoc.csd.hy463.TopicsReader;
import java.util.ArrayList;
import java.util.List;

public class Evaluator {

    List<Reader> topics;

    public Evaluator()  throws Exception{
        ArrayList<Topic> topics = TopicsReader.readTopics("topics.xml");
        this.topics = new ArrayList<>();
        for (Topic topic : topics) {
            Reader tmp = new Reader(topic.getNumber(),topic.getType(),topic.getSummary(),topic.getDescription());
            this.topics.add(tmp);

        }

    }

}
