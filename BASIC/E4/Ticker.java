package org.upc.edu.Behaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class Ticker extends Agent
{
    public class Tickero extends TickerBehaviour
    {
        String message;
        int count_chocula;

        public Tickero(Agent a, long period)
        {
            super(a,period);
        }

         public void onStart()
        {
            this.message = "Agent " + getAID().getName() +" with Ticker in action!!";
            count_chocula = 0;
        }

        public int onEnd()
        {
            System.out.println("I have done " + count_chocula + " iterations");
            return count_chocula;
        }

        public void onTick()
        {
            System.out.println(this.message + count_chocula);
            ++count_chocula;
        }

    }

    protected void setup()
    {

     Tickero b = new Tickero(this, 3000);;
     this.addBehaviour(b);
    }


}
