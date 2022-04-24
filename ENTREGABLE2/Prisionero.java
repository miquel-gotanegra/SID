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
import java.util.Random;
import java.util.HashMap;


public class Prisionero extends SingleCapabilityAgent {
    String color;
    final String ANSI_RESET = "\u001B[0m";

    void colorSet(){
        Random ran = new Random(System.currentTimeMillis());
        int x = ran.nextInt(6) +31;
        color = "\u001B[" + String.valueOf(x) + "m";
    };

    private static HashMap<String, String>convertArrayListToHashMap(ArrayList<DFAgentDescription> arrayList)
    {
        HashMap<String, String> hashMap = new HashMap<>();
        for (DFAgentDescription str : arrayList) {
            hashMap.put(str.getName().getLocalName(), "");
        }
        return hashMap;
    }
    protected void init(){

        Capability cap = this.getCapability();
        BeliefBase beliefBase = cap.getBeliefBase();
        Object[] args = getArguments();

        colorSet();

        if (args.length == 4) {
            //Registration
            //Deteccion de error en input
            try {
                beliefBase.addBelief(new TransientBelief("CC",Integer.parseInt((String) args[0])));
                beliefBase.addBelief(new TransientBelief("CD",Integer.parseInt((String) args[1])));
                beliefBase.addBelief(new TransientBelief("DC",Integer.parseInt((String) args[2])));
                beliefBase.addBelief(new TransientBelief("DD",Integer.parseInt((String) args[3])));

                beliefBase.addOrUpdateBelief(new TransientBelief("historial", new HashMap<String, ArrayList<String>>()));

                beliefBase.addBelief(new TransientBelief("score",0));

                System.out.println();
                System.out.println(color+"El prisionero " + getAID().getLocalName() +" ha empezado a jugar");
                System.out.println("OPCIONES PRISIONERO");
                System.out.println("    C    D");
                System.out.println("C   "+Integer.parseInt((String) args[0])+ "    " +Integer.parseInt((String) args[1]));
                System.out.println("D   "+Integer.parseInt((String) args[2])+ "    " +Integer.parseInt((String) args[3]));
                System.out.println("\u001B[0m");

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
        Goal[] sequential = new Goal[3];
        sequential[0]= new GoalRegister();
        sequential[1]= new GoalFindAvailablePlayers();
        Goal[] parallel = new Goal[2];
        parallel[0]= new GoalOfferPlay();
        parallel[1]= new GoalRecivePlay();
        ParallelGoal parallelGoal = new ParallelGoal(parallel);
        sequential[2] = parallelGoal;
        SequentialGoal secuentialGoal = new SequentialGoal(sequential);
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
            try {
              DFService.register((Agent)this.getCapability().getMyAgent(),dfd);
              setEndState(Plan.EndState.SUCCESSFUL);
            } catch (FIPAException e){
              final String ANSI_RED = "\u001B[31m";
              final String ANSI_RESET = "\u001B[0m";

              System.out.println(ANSI_RED+"Error al registarse."+ANSI_RESET);
            }
            //System.out.println("REGISTER");
            
        }
    }
    public class PlanFindAvailablePlayers extends AbstractPlanBody {
        @Override
        public void action(){
            try{
            DFAgentDescription dfa = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("player");
            dfa.addServices(sd);
            DFAgentDescription[] res = DFService.search(myAgent, dfa);
            ArrayList<DFAgentDescription> results = new ArrayList<DFAgentDescription>(Arrays.asList(res));
            for(DFAgentDescription r:results){
                if (r.getName().equals(myAgent.getAID())){
                    results.remove(r);
                    break;
                }
            }

            Capability cap = this.getCapability();
            BeliefBase beliefBase = cap.getBeliefBase();
            beliefBase.addOrUpdateBelief(new TransientBelief("jugadores_pendientes", results));
            
            HashMap<String, String> j_ini = convertArrayListToHashMap(results);

            //solo nos hace falta el movimiento que hemos hecho antes con los jugadores en los que vamos en el primer turno, ya que si vamos segundos hacemos la decision al momento
            beliefBase.addOrUpdateBelief(new TransientBelief("lastMove", j_ini));
            

            setEndState(Plan.EndState.SUCCESSFUL);
            }catch(FIPAException e){System.out.println("error FindPlayers");}
            //System.out.println("FIND PLAYERS");
        }
    }
    public class PlanOfferPlay extends AbstractPlanBody {
        @Override
        public void action(){
            Capability cap = this.getCapability();
            BeliefBase beliefBase = cap.getBeliefBase();
            try{
                Thread.sleep(3000);
              }catch(Exception e){}
            //System.out.println("PlayOffer");
            
            
            ArrayList<DFAgentDescription> jugadores_pendientes = (ArrayList<DFAgentDescription>) beliefBase.getBelief("jugadores_pendientes").getValue();
            Random ran = new Random();

            if(jugadores_pendientes.size()>0){
                int index = ran.nextInt(jugadores_pendientes.size());
                DFAgentDescription j = jugadores_pendientes.get(index);
                jugadores_pendientes.remove(index);
                beliefBase.addOrUpdateBelief(new TransientBelief("jugadores_pendientes", jugadores_pendientes));
                
                System.out.println(color+"El prisionero " + getAID().getLocalName() +" ha empezado a jugar con "+j.getName().getLocalName()+ ANSI_RESET);

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

                HashMap<String,String> lastMove = (HashMap<String,String>)beliefBase.getBelief("lastMove").getValue();
                lastMove.put(j.getName().getLocalName(),selectedPlay);
                beliefBase.addOrUpdateBelief(new TransientBelief("lastMove", lastMove));

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(j.getName());
                msg.setOntology("play");
                msg.setContent(selectedPlay);
                send(msg);


            }
            else setEndState(Plan.EndState.SUCCESSFUL);
        }
    }
    public class PlanRecivePlay extends AbstractPlanBody {
        @Override
        public void action(){
            ACLMessage msg = myAgent.receive();
            Capability cap = this.getCapability();
            BeliefBase beliefBase = cap.getBeliefBase();

            try{
                Thread.sleep(3000);
              }catch(Exception e){}

            if (msg != null){
                if ((msg.getPerformative() == ACLMessage.INFORM) && msg.getOntology().equals("play") && (msg.getContent().equals("C")||msg.getContent().equals("D")))
                {
                    int CC = (int)beliefBase.getBelief("CC").getValue();
                    int DC = (int)beliefBase.getBelief("DC").getValue();
                    int CD = (int) beliefBase.getBelief("CD").getValue();
                    int DD = (int) beliefBase.getBelief("DD").getValue();

                    String selectedPlay = "";
                    if(msg.getContent().equals("C")){
                        if(Math.min(CC,DC) == CC)   selectedPlay = "C";
                        else    selectedPlay = "D";
                    }
                    else if (msg.getContent().equals("D")){
                        if(Math.min(CD,DD)== CD)    selectedPlay = "C";
                        else selectedPlay = "D";
                    } 
                   
                    System.out.print(color+ getAID().getLocalName() +": ha recibido "+msg.getContent() + " de " + msg.getSender().getLocalName() + " y ha respondido "+ selectedPlay +"." + ANSI_RESET);

                    HashMap<String,String> lastMove = (HashMap<String,String>)beliefBase.getBelief("lastMove").getValue();
                    //si contiene la key significa que nosotros vamos primero en los turnos
                    String par;
                    String texto;
                    System.out.print(color);
                    if(lastMove.containsKey(msg.getSender().getLocalName())){
                        //System.out.print("voy primero ");
                        par = lastMove.get(msg.getSender().getLocalName())+msg.getContent();
                        texto = "["+getAID().getLocalName()+": "+ lastMove.get(msg.getSender().getLocalName()) + "," +msg.getSender().getLocalName()+": "+  msg.getContent()+"]";
                        //System.out.println(texto);
                        lastMove.put(msg.getSender().getLocalName(),selectedPlay);
                        beliefBase.addOrUpdateBelief(new TransientBelief("lastMove", lastMove));
                    }
                    //si no, vamos segundos
                    else{
                        //System.out.print("voy segundo");
                        texto = "["+msg.getSender().getLocalName()+": "+ msg.getContent() + "," +getAID().getLocalName()+": "+ selectedPlay +"]";
                        //System.out.println(texto);
                        
                        //nuestro agente siempre es la X de XY
                        par = selectedPlay+msg.getContent();
                       // System.out.print("last couple: "+par);
                    }
                    int score = (int) beliefBase.getBelief("score").getValue();
                    if(par.equals("CC"))score +=CC; 
                    else if(par.equals("CD")) score +=CD; 
                    else if(par.equals("DC")) score +=DC; 
                    else if(par.equals("DD")) score +=DD; 
                    beliefBase.addOrUpdateBelief(new TransientBelief("score", score));
                    System.out.print(" NEW SCORE =");
                    System.out.println(score);

                    //System.out.println("SUSSY");
                    HashMap<String,ArrayList<String>> historial = (HashMap<String,ArrayList<String>>)beliefBase.getBelief("historial").getValue();
                    //System.out.println("ADD");
                    if(!historial.containsKey(msg.getSender().getLocalName())) historial.put(msg.getSender().getLocalName(),new ArrayList<String>());
                    historial.get(msg.getSender().getLocalName()).add(texto);
                    //System.out.println("PRINT");
                    System.out.print("Historial con "+msg.getSender().getLocalName()+": ");
                    System.out.println(historial.get(msg.getSender().getLocalName()));
                    beliefBase.addOrUpdateBelief(new TransientBelief("historial", historial));

                    System.out.println(ANSI_RESET);
    
                    ACLMessage answer = new ACLMessage(ACLMessage.INFORM);
                    answer.addReceiver(msg.getSender());
                    answer.setOntology("play");
                    answer.setContent(selectedPlay);
                    send(answer);
                }
            }
        }
    }
}