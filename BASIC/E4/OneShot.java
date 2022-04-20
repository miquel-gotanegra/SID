package org.upc.edu.Behaviours;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class OneShot extends Agent
{

     public class OneShotEj extends OneShotBehaviour
    {
        String message;
        int count_chocula;

        public void onStart()
        {
            this.message = "Agent " + getAID().getName() +" with OneShotEj in action!!";
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
        }

    }

     protected void setup()
    {

      OneShotEj b = new OneShotEj();
      this.addBehaviour(b);
    }
}
