package org.upc.edu.Behaviours;

import jade.core.Agent;
import jade.core.behaviours.*;


public class COMBO extends Agent
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
     OneShotEj a = new OneShotEj();
     Ciclao b = new Ciclao();
     WakeyWakey c = new WakeyWakey(this,3000);
     Tickero d = new Tickero(this,3000);

     //SequentialBehaviour sub = new SequentialBehaviour();
     ParallelBehaviour sub = new ParallelBehaviour();
     sub.addSubBehaviour(a);
     sub.addSubBehaviour(b);
     sub.addSubBehaviour(c);
     sub.addSubBehaviour(d);

     this.addBehaviour(sub);
    }
}
