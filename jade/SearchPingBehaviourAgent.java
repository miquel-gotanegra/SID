package org.upc.edu.Behaviours;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class SearchPingBehaviourAgent extends Agent
{
	
	String message="Nobody is pingAgent";
    DFAgentDescription template = new DFAgentDescription();
    ServiceDescription templateSd = new ServiceDescription();
     public class SearchPingOneShotBehaviour extends OneShotBehaviour
    {
        public SearchPingOneShotBehaviour()
        {
            
        }

        public void onStart()
        {
            
        }

        public int onEnd()
        {
            
            return 1;
        }

        public void action()
        {
        	SearchConstraints sc = new SearchConstraints();
			DFAgentDescription[] results = null;
			try {
				results = DFService.search(myAgent, template, sc );
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     	if (results.length > 0) {
     		DFAgentDescription dfd = results[0];
     		AID provider = dfd.getName();
     		message = "PingAgent is "+provider;
     	}
     		System.out.println(message);
        }

    }

     protected void setup()
    {
    	 
     	
    	 SearchPingOneShotBehaviour b = new SearchPingOneShotBehaviour();
    	 this.addBehaviour(b);
    	 
	
      
    }
}
