import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import java.util.Hashtable;

public class BookSellerAgent extends Agent {
    private Hashtable catalogue;
 
    protected void setup() {
        catalogue = new Hashtable();
 
        catalogue.put("Градієнтний бетон", 500);

 
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("book-selling");
        sd.setName("JADE-book-trading");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) { fe.printStackTrace(); }
 
        addBehaviour(new OfferRequestsServer());
    }

 
    private class OfferRequestsServer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String title = msg.getContent();
                ACLMessage reply = msg.createReply();
                Integer price = (Integer) catalogue.get(title);
                
                if (price != null) {
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(String.valueOf(price.intValue()));
                } else {
                    reply.setPerformative(ACLMessage.REFUSE);
                }
                myAgent.send(reply);
            } else block();
        }
    }
 
    protected void takeDown() {
        try {
 
            DFService.deregister(this);
        } catch (FIPAException fe) { fe.printStackTrace(); }
        System.out.println("Продавець " + getAID().getName() + " вимкнувся.");
    }
}
