package Entregable;

import jade.core.Agent;
import jade.core.behaviours.*;

import bdi4jade.core.*;
import bdi4jade.plan.*;
import bdi4jade.plan.planbody.*;
import bdi4jade.goal.*;
import bdi4jade.belief.*;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;



import java.util.*;
import java.util.ArrayList;

public class Prisionero extends SingleCapabilityAgent {
    int score = 0;
    ArrayList<DFAgentDescription> jugadores_pendientes;
    String[] historial;


    protected void init(){
        Capability cap = this.getCapability();
        BeliefBase beliefBase = cap.getBeliefBase();
        Object[] args = getArguments();
        if (args.length == 4) {
            //Registration
            //Deteccion de error en input
            try {
                beliefBase.addBelief(new TransientBelief("CC",Integer.parseInt((String) args[0])));
                beliefBase.addBelief(new TransientBelief("CD",Integer.parseInt((String) args[1])));
                beliefBase.addBelief(new TransientBelief("DC",Integer.parseInt((String) args[2])));
                beliefBase.addBelief(new TransientBelief("DD",Integer.parseInt((String) args[3])));

                beliefBase.addBelief(new TransientBeliefSet("historial",new HashSet()));

                beliefBase.addBelief(new TransientBelief("score",0));

                System.out.println("Prisionero " + getAID().getLocalName() +" ha empezado a jugar");

            } catch (Exception e) {
                System.out.println("LOS PARAMETROS TIENEN QUE SER NUMEROS ENTEROS");
                doDelete();
                return;
            }
        } else {
            System.out.println("~~~~~~~~~~~~~USAGE~~~~~~~~~~~~~");
            System.out.println("Los parametros necesarios son: CC,CD,DC,DD, donde XY se interpreta como la penalización para el agente cuando  ́este juega X y su adversario juega Y , independientemente de quién haya hecho su jugada antes");
            doDelete();
            return;
        }

        //MEJOR QUE SEA SECUENCIAL YA QUE NO PUEDE BUSCAR SIN REGISTRARSE, NI ENVIAR NADA SIN BUSCAR
        Goal[] goals = new Goal[4];
        goals[0]= new GoalRegister();
        goals[1]= new GoalFindAvailablePlayers();
        goals[2]= new GoalOfferPlay();
        goals[3]= new GoalRecivePlay();
        SequentialGoal secuentialGoal = new SequentialGoal(goals);
        this.addGoal(secuentialGoal);
        
        getCapability().getPlanLibrary().addPlan(new DefaultPlan(GoalRegister.class, PlanRegister.class));
        getCapability().getPlanLibrary().addPlan(new DefaultPlan(GoalFindAvailablePlayers.class, PlanFindAvailablePlayers.class));
        getCapability().getPlanLibrary().addPlan(new DefaultPlan(GoalOfferPlay.class, PlanOfferPlay.class));
        getCapability().getPlanLibrary().addPlan(new DefaultPlan(GoalRecivePlay.class, PlanRecivePlay.class));
        
    }

    public class GoalRegister implements Goal{}
    public class GoalFindAvailablePlayers implements Goal{}
    public class GoalOfferPlay implements Goal{}
    public class GoalRecivePlay implements Goal{}
    
    public class PlanRegister extends AbstractPlanBody {
        @Override
        public void action(){
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("player");
            sd.setName(getLocalName());
            dfd.setName(getAID());
            dfd.addServices(sd);
            DFService.register(this,dfd);

        }
    }
    public class PlanFindAvailablePlayers extends AbstractPlanBody {
        @Override
        public void action(){
            DFAgentDescription dfa = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("player");
            dfa.addServices(sd);

            DFAgentDescription[] results = DFService.search(myAgent, dfa);
            Capability cap = this.getCapability();
            BeliefBase beliefBase = cap.getBeliefBase();
            beliefBase.updateBelief("jugadores_pendientes", new ArrayList<>(Arrays.asList(results)));
        }
    }
    public class PlanOfferPlay extends AbstractPlanBody {
        @Override
        public void action(){

        }
    }
    public class PlanRecivePlay extends AbstractPlanBody {
        @Override
        public void action(){

        }
    }
}