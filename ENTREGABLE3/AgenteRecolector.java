package Entregable;

import jade.core.Agent;
import jade.core.behaviours.*;

import bdi4jade.core.*;
import bdi4jade.plan.*;
import bdi4jade.plan.planbody.*;
import bdi4jade.goal.*;
import bdi4jade.belief.*;

import static java.lang.Math.*;
import java.util.*;

public class AgenteRecolector extends SingleCapabilityAgent {
  static int cont = 0;

  
  protected void init() {
    System.out.println("Bienvenido Player: " + getAID().getLocalName());
    Capability cap = this.getCapability();
    BeliefBase beliefBase = cap.getBeliefBase();

    Object[] args = getArguments();

    Set objects = new HashSet();
    objects.add(args[0]);
    objects.add(args[1]);

    Set objects2 = new HashSet();
    objects2.add(args[2]);
    objects2.add(args[3]);

    Belief C = new TransientBeliefSet("C",objects);
    Belief D = new TransientBeliefSet("D",objects2);
    Belief history = new TransientBeliefSet("history",new HashSet());

    beliefBase.addBelief(C);
    beliefBase.addBelief(D);
    beliefBase.addBelief(history);


    Plan plan = new DefaultPlan(minimizePlayGoal.class, PlayerPlanBody.class);
    addGoal(new minimizePlayGoal("minimizeGoal"));
    Plan plan2 = new DefaultPlan(minimizePlayGoal.class, HelloBody.class);

    getCapability().getPlanLibrary().addPlan(plan2);
    getCapability().getPlanLibrary().addPlan(plan);
  }

    public class minimizePlayGoal implements Goal
    {
      private final String text;

      public minimizePlayGoal(String text){
        this.text = text;
      }

      public String getText(){
        return text;
      }
    } 
  public static class HelloBody extends AbstractPlanBody {
        @Override
        public void action() {
            minimizePlayGoal  goal = (minimizePlayGoal)getGoal();
            System.out.println("Hello " + goal.getText());
            //if(cont ==1) 
              //setEndState(Plan.EndState.SUCCESSFUL);
            //else 
              setEndState(Plan.EndState.FAILED);
        }
    }

  public class PlayerPlanBody extends AbstractPlanBody {

    @Override
    public void action(){
      BeliefBase bBase = getBeliefBase();

      Object c = bBase.getBelief("C").getValue();
      Set cValue = (Set) c;

      Object d = bBase.getBelief("D").getValue();
      Set dValue = (Set) d;

      Object hist = bBase.getBelief("history").getValue();
      Set hValue = (Set) hist;

      String vCC = String.valueOf(cValue.toArray()[0]);
      String vCD = String.valueOf(cValue.toArray()[1]);
      String vDC = String.valueOf(dValue.toArray()[0]);
      String vDD = String.valueOf(dValue.toArray()[1]);
      int valCC = Integer.parseInt(vCC);
      int valCD = Integer.parseInt(vCD);
      int valDC = Integer.parseInt(vDC);
      int valDD = Integer.parseInt(vDD);

      int minC = Math.min(valCC,valCD);
      int minD = Math.min(valDC,valDD);

      System.out.println("C value: " + cValue);
      System.out.println("D value: " + dValue);

      if(minC < minD) {
        System.out.println("Decided value: " + cValue);
        hValue.add(cValue);
        bBase.updateBelief("history",hValue);
        System.out.println("History of decitions:" + hValue);
      }
      else {
        System.out.println("Decided value: " + dValue);
        hValue.add(dValue);
        bBase.updateBelief("history",hValue);
        System.out.println("History of decitions:" + hValue);
      }
      ++cont;
      setEndState(Plan.EndState.FAILED);

    }
  }


}

