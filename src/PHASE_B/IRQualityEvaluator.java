package PHASE_B;
import gr.uoc.csd.hy463.Topic;
import gr.uoc.csd.hy463.TopicsReader;
import java.util.ArrayList;
import java.util.List;

public class IRQualityEvaluator {

    List<PHASE_B.Topic> topics;

    public IRQualityEvaluator()  throws Exception{
        ArrayList<Topic> topics = TopicsReader.readTopics("topics.xml");
        for (Topic topic : topics) {
            PHASE_B.Topic tmp = new PHASE_B.Topic(topic.getNumber(),topic.getType(),topic.getSummary(),topic.getDescription());
            this.topics = new ArrayList<>();
            this.topics.add(tmp);

//            System.out.println(topic.getNumber());
//            System.out.println(topic.getType());
//            System.out.println(topic.getSummary());
//            System.out.println(topic.getDescription());
//            System.out.println("---------");
        }


    }

}
