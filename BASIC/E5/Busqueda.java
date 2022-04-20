package org.upc.edu.Behaviours;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.*;

public class Busqueda extends Agent
{

    public class Busca extends OneShotBehaviour
    {
        String message;
        int count_chocula;


        public void onStart()
        {
          // Build the description used as template for the search
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription templateSd = new ServiceDescription();
        templateSd.setType("PingAgent");
        template.addServices(templateSd);
        SearchConstraints sc = new SearchConstraints();
        // We want to receive 10 results at most
        sc.setMaxResults(new Long(10));
        DFAgentDescription[] results = DFService.search(this, template, sc);
        if (results.length > 0) {
          DFAgentDescription dfd = results[0];
        AID provider = dfd.getName();
}
        }

        public int onEnd()
        {

        }

        public void action()
        {
            System.out.println("alo");
            try{
              Thread.sleep(3000);
            }catch(Exception e){}
        }

    }
    protected void setup()
    {

    DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Busqueda");
		sd.setName(getName());
		sd.setOwnership("TILAB");
		dfd.setName(getAID());
		dfd.addServices(sd);

     Busca b = new Busca();
     this.addBehaviour(b);
    }
}
