package org.upc.edu.Behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;


public class Cyclic extends Agent
{

    public class Ciclao extends CyclicBehaviour
    {
        String message;
        int count_chocula;

        public void onStart()
        {
            this.message = "Agent " + getAID().getName() +" with Cyclic in action!! ";
            count_chocula = 0;
        }

        public int onEnd()
        {
            System.out.println("I have done " + count_chocula + " iterations");
            return count_chocula;
        }

        public void action()
        {
            System.out.println(this.message + count_chocula);
            ++count_chocula;

            try{
              Thread.sleep(3000);
            }catch(Exception e){}
        }
        /*
        public boolean done()
        {
          return count_chocula < 10;
        } */

    }
    protected void setup()
    {

     Ciclao b = new Ciclao();
     this.addBehaviour(b);
    }
}
