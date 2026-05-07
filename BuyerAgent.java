import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;

public class BookBuyerAgent extends Agent {
    private String targetBookTitle;
    private AID[] sellerAgents;
 
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            targetBookTitle = (String) args[0];
            
 
            addBehaviour(new TickerBehaviour(this, 60000) { 
                protected void onTick() {
                    // Пошук продавців у жовтих сторінках (DF)
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("book-selling");
                    template.addServices(sd);
                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        sellerAgents = new AID[result.length];
                        for (int i = 0; i < result.length; ++i) {
                            sellerAgents[i] = result[i].getName();
                        }
                    } catch (FIPAException fe) { fe.printStackTrace(); }

                   
                    myAgent.addBehaviour(new RequestPerformer());
                }
            });
        }
    }

  
    private class RequestPerformer extends Behaviour {
        private int step = 0;
        private MessageTemplate mt;

        public void action() {
            switch (step) {
                case 0:  (Call For Proposal)
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (AID seller : sellerAgents) cfp.addReceiver(seller);
                    cfp.setContent(targetBookTitle);
                    cfp.setConversationId("book-trade");
                    myAgent.send(cfp);
                    mt = MessageTemplate.MatchConversationId("book-trade");
                    step = 1;
                    break;
                case 1: 
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            
                        }
                        step = 2; 
                    } else block();
                    break;
            }
        }
        public boolean done() { return step == 2; }
    }

    protected void takeDown() {
        System.out.println("Покупець " + getAID().getName() + " завершив роботу.");
    }
}
