package communication;

import com.unibo.s3.main_system.communication.FileActor;
import com.unibo.s3.main_system.communication.GraphActor;
import com.unibo.s3.main_system.communication.Messages;
import com.unibo.s3.main_system.communication.SystemManager;
import org.jgrapht.graph.DefaultEdge;

import akka.actor.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;

import static javax.script.ScriptEngine.FILENAME;

public class TestSameTerminal {

    private UndirectedGraph<String, DefaultEdge> graph;

    @Before
    public void initialization() {
        this.graph = new SimpleGraph<>(DefaultEdge.class);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";

        this.graph.addVertex(v1);
        this.graph.addVertex(v2);
        this.graph.addVertex(v3);
        this.graph.addVertex(v4);

        this.graph.addEdge(v1, v2);
        this.graph.addEdge(v2, v3);
        this.graph.addEdge(v3, v4);
        this.graph.addEdge(v4, v1);
    }

    @Test
    public void testCommunicationBetweenSystems() {
        System.out.println("initial graph: " + this.graph.toString());
        try {
            String confText =
                    "{\"akka\":{\"actor\":{\"provider\":\"akka.remote.RemoteActorRefProvider\"}," +
                            "\"loglevel\":\"INFO\",\"remote\":{\"enabled-transports\":[\"akka.remote.netty.tcp\"]" +
                            ",\"log-received-messages\":\"on\",\"log-sent-messages\":\"on\"" +
                            ",\"netty\":{\"tcp\":{\"hostname\":\""+ Inet4Address.getLocalHost().getHostAddress()+"\",\"port\":2727}}}}}";
            Config customConf = ConfigFactory.parseString(confText);
            SystemManager.getInstance().createSystem("RemoteSystem", customConf);
            ActorRef remote = SystemManager.getInstance().createActor(GraphActor.props("Remote"), "remote");
            customConf = ConfigFactory.parseString(confText.replace("2727", "5050"));
            ActorSystem LocalSystem = ActorSystem.create("LocalSystem", customConf);
            ActorRef local = LocalSystem.actorOf
                    (GraphActor.props("Local"), "local");
            remote.tell(this.graph, local);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCommunicationAndSelectionOnLocalSystem() {
        SystemManager.getInstance().createSystem("System", null);
        SystemManager.getInstance().createActor(FileActor.props("First Local"), "firstLocal");
        SystemManager.getInstance().createActor(FileActor.props("Second Local"), "secondLocal");
        ActorRef first = SystemManager.getInstance().getLocalActor("firstLocal");
        ActorRef second = SystemManager.getInstance().getLocalActor("secondLocal");

        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader("assets/Trial");
            br = new BufferedReader(fr);
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                second.tell(new Messages.FileMsg(sCurrentLine), first);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
