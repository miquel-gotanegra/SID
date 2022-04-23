package Entregable;

import jade.core.*;
import jade.core.Agent;
import jade.core.behaviours.*;
import java.lang.Math.*;
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

import java.util.Random;

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
                //ADD BELIEFS
                beliefBase.addBelief(new TransientBelief("CC",Integer.parseInt((String) args[0])));
                beliefBase.addBelief(new TransientBelief("CD",Integer.parseInt((String) args[1])));
                beliefBase.addBelief(new TransientBelief("DC",Integer.parseInt((String) args[2])));
                beliefBase.addBelief(new TransientBelief("DD",Integer.parseInt((String) args[3])));

                beliefBase.addBelief(new TransientBeliefSet("historial",new HashSet()));

                beliefBase.addBelief(new TransientBelief("score",0));

                beliefBase.addBelief(new TransientBelief("jugadores_pendientes",new HashSet()));

                beliefBase.addBelief(new TransientBelief("jugadores_hechos",new HashSet()));
                beliefBase.addBelief(new TransientBelief("jugadores_encontrados",new HashSet()));

                System.out.println();
                System.out.println("\033[0;35m"+"Prisionero " + getAID().getLocalName() +" ha empezado a jugar"+"\u001B[0m");
                System.out.println("OPCIONES PRISIONERO");
                System.out.println("    C    D");
                System.out.println("C   "+Integer.parseInt((String) args[0])+ "    " +Integer.parseInt((String) args[1]));
                System.out.println("D   "+Integer.parseInt((String) args[2])+ "    " +Integer.parseInt((String) args[3]));
                System.out.println();

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
        Goal[] goals = new Goal[2];
        goals[0]= new GoalRegister();
        goals[1]= new GoalFindAvailablePlayers();
        SequentialGoal secuentialGoal = new SequentialGoal(goals);
        this.addGoal(secuentialGoal);
        Goal[] goals2 = new Goal[2];
        goals2[0]= new GoalOfferPlay();
        goals2[1]= new GoalReceivePlay();
        ParallelGoal parallelGoal = new ParallelGoal(goals2);
        this.addGoal(parallelGoal);

        getCapability().getPlanLibrary().addPlan(new DefaultPlan(GoalRegister.class, PlanRegister.class));
        getCapability().getPlanLibrary().addPlan(new DefaultPlan(GoalFindAvailablePlayers.class, PlanFindAvailablePlayers.class));
        getCapability().getPlanLibrary().addPlan(new DefaultPlan(GoalOfferPlay.class, PlanOfferPlay.class));
        getCapability().getPlanLibrary().addPlan(new DefaultPlan(GoalReceivePlay.class, PlanReceivePlay.class));

    }

    public class GoalRegister implements Goal{}
    public class GoalFindAvailablePlayers implements Goal{}
    public class GoalOfferPlay implements Goal{}
    public class GoalReceivePlay implements Goal{}

    public class PlanRegister extends AbstractPlanBody {
        @Override
        public void action(){
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("player");
            sd.setName(getLocalName());
            dfd.setName(getAID());
            dfd.addServices(sd);
            try {
              DFService.register((Agent)this.getCapability().getMyAgent(),dfd);
            } catch (FIPAException e){
              final String ANSI_RED = "\u001B[31m";
              final String ANSI_RESET = "\u001B[0m";

              System.out.println(ANSI_RED+"Error al registarse."+ANSI_RESET);
            }
            setEndState(Plan.EndState.SUCCESSFUL);
        }
    }
    public class PlanFindAvailablePlayers extends AbstractPlanBody {

        private void printNewPendent(String agN, String agFN){
            final String ANSI_RED = "\033[0;33m";
            final String ANSI_RESET = "\u001B[0m";

            System.out.println(ANSI_RED + "Ahora " + agN + " tiene a " + agFN + " como jugador pendiente."+ANSI_RESET);
        }

        @Override
        public void action(){
          try{
            DFAgentDescription dfa = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("player");
            dfa.addServices(sd);

            DFAgentDescription[] results = DFService.search(myAgent, dfa);
            for(DFAgentDescription result:results){
              String agName = myAgent.getLocalName();
              String agFName = result.getName().getLocalName();
              AID agN = myAgent.getAID();
              AID agFN = result.getName();

              if(!(agName.equals(agFName))){
                Capability cap = this.getCapability();
                BeliefBase beliefBase = cap.getBeliefBase();
                Object pD = beliefBase.getBelief("jugadores_hechos").getValue();
                Object pP = beliefBase.getBelief("jugadores_pendientes").getValue();
                Object pF = beliefBase.getBelief("jugadores_encontrados").getValue();

                HashSet pDone = (HashSet) pD;
                HashSet pPendent = (HashSet) pP;
                HashSet pFound = (HashSet) pF;
                if(!pFound.contains(agFN)){
                    pFound.add(agFN);
                    beliefBase.updateBelief("jugadores_encontrados", (Object)pFound);
                    pPendent.add(agFN);
                    beliefBase.updateBelief("jugadores_pendientes", (Object)pPendent);
                    printNewPendent(agName,agFName);
                }

              }

            }

          } catch (FIPAException e) {
              e.printStackTrace();
          }
        }
    }
    public class PlanOfferPlay extends AbstractPlanBody {
        @Override
        public void action(){
            Capability cap = this.getCapability();
            BeliefBase beliefBase = cap.getBeliefBase();

            Object vPlayers = beliefBase.getBelief("jugadores_encontrados").getValue();
            HashSet players = (HashSet) vPlayers;

            Random rand = new Random();
            HashSet jugPend = (HashSet) beliefBase.getBelief("jugadores_pendientes").getValue();

            if(players.size() > 0) {
                int ind_rand = rand.nextInt(players.size());

                AID playerObj = (AID) players.toArray()[ind_rand];

                if(jugPend.contains(playerObj)) {
                    System.out.println(myAgent.getLocalName() + ": I'm playing with " + playerObj.getLocalName());

                    int CC = (int) beliefBase.getBelief("CC").getValue();
                    int CD = (int) beliefBase.getBelief("CD").getValue();
                    int DC = (int) beliefBase.getBelief("DC").getValue();
                    int DD = (int) beliefBase.getBelief("DD").getValue();

                    String selectedPlay = "";

                    if (Math.min(CC, CD) <= Math.min(DC, DD)) {
                        selectedPlay = "C";
                    } else {
                        selectedPlay = "D";
                    }


                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(playerObj);
                    msg.setOntology("play");
                    msg.setContent(selectedPlay);

                    Object vH = beliefBase.getBelief("historial").getValue();
                    ArrayList hist = (ArrayList) vH;

                    hist.add("Sent play: " + selectedPlay);
                    send(msg);

                    Object vS = beliefBase.getBelief("score").getValue();
                    int sc = (int) vS;

                    System.out.println(myAgent.getLocalName() + " sent play: " + selectedPlay + " to " + playerObj.getLocalName());
                    beliefBase.updateBelief("historial", (Object) hist);

                    System.out.println(myAgent.getLocalName()+ ": decided to play "+ selectedPlay +" versus " + playerObj.getLocalName()+"\n"+myAgent.getLocalName()+"\n"+ "Historial : " + hist +"\n"+ "Score " + sc);

                    jugPend.remove(playerObj);
                    beliefBase.updateBelief("jugadores_pendientes", (Object) jugPend);
                    HashSet jugComp = (HashSet) beliefBase.getBelief("jugadores_hechos").getValue();
                    jugComp.add(playerObj);
                    beliefBase.updateBelief("jugadores_hechos", (Object) jugComp);
                }

            }
        }
    }
    public class PlanReceivePlay extends AbstractPlanBody {
        @Override
        public void action(){
            ACLMessage msg = myAgent.receive();
            Capability cap = this.getCapability();
            BeliefBase beliefBase = cap.getBeliefBase();


            HashSet jugPend = (HashSet) beliefBase.getBelief("jugadores_pendientes").getValue();
            if (msg != null){
                AID sender = msg.getSender();
                if ((msg.getPerformative() == ACLMessage.INFORM) && msg.getOntology().equals("play")){
                    if(msg.getContent().equals("C")||msg.getContent().equals("D")){
                        int VALUE = 0;
                        String movement = "";



                        System.out.println(myAgent.getLocalName()+ ": I'm playing with " + sender.getLocalName() + ", that decided to play " + msg.getContent());
                        if(msg.getContent().equals("C")){

                            Object vCC = beliefBase.getBelief("CC").getValue();
                            Object vDC = beliefBase.getBelief("DC").getValue();

                            int CC = (int) vCC;
                            int DC = (int) vDC;

                            VALUE = Math.min(CC,DC);
                            if(VALUE == CC){
                                movement = "CC";
                            } else {
                                movement = "DC";
                            }
                        }
                        else if (msg.getContent().equals("D")){

                            Object vCD = beliefBase.getBelief("CD").getValue();
                            Object vDD = beliefBase.getBelief("DD").getValue();

                            int CD = (int) vCD;
                            int DD = (int) vDD;

                            VALUE = Math.min(CD,DD);
                            if(VALUE == CD){
                                movement = "CD";
                            } else {
                                movement = "DD";
                            }

                        }
                        Object vH = beliefBase.getBelief("historial").getValue();
                        Object vS = beliefBase.getBelief("score").getValue();

                        ArrayList hist = (ArrayList) vH;
                        int sc = (int) vS;
                        sc += VALUE;

                        hist.add("Played: " + movement);
                        System.out.println(myAgent.getLocalName()+ ": decided to play "+ movement +" versus " + sender.getLocalName()+"\n"+myAgent.getLocalName()+"\n"+ "Historial : " + hist +"\n"+ "Score " + sc);

                        beliefBase.updateBelief("historial",(Object) hist);
                        beliefBase.updateBelief("score", (Object) sc);

                        jugPend.remove(sender);
                        beliefBase.updateBelief("jugadores_pendientes", (Object) jugPend);
                        HashSet jugComp = (HashSet) beliefBase.getBelief("jugadores_hechos").getValue();
                        jugComp.add(sender);
                        beliefBase.updateBelief("jugadores_hechos",(Object) jugComp);

                    }
                }
            }
        }
    }
}
