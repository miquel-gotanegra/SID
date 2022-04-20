package org.upc.edu.Behaviours;

import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;

public class Waker extends Agent
{

    public class WakeyWakey extends WakerBehaviour
    {
        String message;
        int count_chocula;

        public WakeyWakey(Agent a, long timeout)
        {
            super(a,timeout);
        }

        public void onStart()
        {
            this.message = "Agent " + getAID().getName() +" with Waker in action!!";
            count_chocula = 0;
        }

        public int onEnd()
        {
            System.out.println("I have done " + count_chocula + " iterations");
            return count_chocula;
        }

        public void onWake()
        {
            System.out.println(this.message + count_chocula);
            ++count_chocula;
        }

    }

     protected void setup()
    {

     WakeyWakey b = new WakeyWakey(this, 3000);
     this.addBehaviour(b);
    }

}
