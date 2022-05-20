package Entregable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

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
import Entregable.mapaOnt;

public class AgentMove extends SingleCapabilityAgent{
	
	ArrayList<String> PATH = new ArrayList<String>(Arrays.asList("node4","node3"));
		
	protected void init() {
		Capability cap = this.getCapability();
        BeliefBase beliefBase = cap.getBeliefBase();
        
        try {
        	
        	beliefBase.addBelief(new TransientBelief("Trabajo","AgenteRecolector"));
        	
			beliefBase.addBelief(new TransientBelief("posicion","node5"));
        	
        	beliefBase.addBelief(new TransientBelief("PATH",PATH));
        	

    	    Goal recolecta = new GoalRecolecta();
    	    this.addGoal(recolecta);
    	    
    	    this.getCapability().getPlanLibrary().addPlan(new DefaultPlan(GoalRecolecta.class,PlanRecolecta.class));


        } catch (Exception e) {
            doDelete();
            return;
        }
	}
	
	public class GoalRecolecta implements Goal{}

	public class PlanRecolecta extends AbstractPlanBody {
		
	    @Override
	    public void action() {
	    	Capability cap = this.getCapability();
	        BeliefBase beliefBase = cap.getBeliefBase();
	        
	        String position = (String) beliefBase.getBelief("Position").getValue();
	        String Agent = (String) beliefBase.getBelief("Trabajo").getValue();
	        
	        ArrayList<String> rem_path = (ArrayList<String>) beliefBase.getBelief("PATH").getValue();
	        
			String path = "./";
			String owlFile = "myMap.owl";
			String namespace = "myMap";
			mapaOnt mapa = new mapaOnt(path, owlFile, namespace);
	    }
	}
	
}	




