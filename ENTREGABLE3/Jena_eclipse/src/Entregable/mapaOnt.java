package Entregable;


import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.core.ResultBinding;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;


public class mapaOnt {
	 	OntModel model;
	    String JENAPath;
	    String OntologyFile;
	    String NamingContext;
	    OntDocumentManager dm;

	    private static final String MAPA_BASE_URI = "http://www.semanticweb.org/mgota/ontologies/2022/4/myMap";
	    private static final String MODIFIED_PREFIX = "new_";

	    public mapaOnt(String _JENA_PATH, String _File, String _NamingContext) {
	        this.JENAPath = _JENA_PATH;
	        this.OntologyFile = _File;
	        this.NamingContext = _NamingContext;
	    }

	    public void initialize() throws FileNotFoundException{
	        System.out.println("· Initializing....");
	        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF);
	        dm = model.getDocumentManager();
	        dm.addAltEntry(NamingContext, "file:" + JENAPath + OntologyFile);
	        model.read(NamingContext);
	        if (!model.isClosed()) {
	            model.write(new FileOutputStream(JENAPath + File.separator + MODIFIED_PREFIX + OntologyFile, false));
	            model.close();
	        }
	    }
	    
	    public void testInferencia() {
	    	System.out.println("· Loading Ontology");
	        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
	        dm = model.getDocumentManager();
	        dm.addAltEntry(NamingContext, "file:" + JENAPath + File.separator + MODIFIED_PREFIX + OntologyFile);
	        model.read(NamingContext);
	        Individual instance = model.getIndividual(MAPA_BASE_URI + "#node3");
	        boolean encima = instance.hasOntClass(MAPA_BASE_URI + "#NodeRecolector");
	        System.out.println("node3 tiene a un agente   almacenador encima?: " + encima);
	        
	        boolean n = instance.hasOntClass(MAPA_BASE_URI + "#Node");
	        System.out.println("node3 es de la classe node?: " + n);
	        
	        model.close();
	    }
	    
	    public String whereAmI(String Agent) {
	    	 //System.out.println("· Loading Ontology");
	         model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
	         dm = model.getDocumentManager();
	         dm.addAltEntry(NamingContext, "file:" + JENAPath + File.separator + MODIFIED_PREFIX + OntologyFile);
	         model.read(NamingContext);
	         Individual instance = model.getIndividual(MAPA_BASE_URI + "#" + Agent);
	         if(instance == null) return ("El Agente " + Agent + " no existe"); 
	         Property nameProperty = model.getProperty(MAPA_BASE_URI + "#Located_in");
	         RDFNode nameValue = instance.getPropertyValue(nameProperty);
	         //System.out.println(nameValue);
	         model.close();
	         String[] s = (nameValue.toString()).split("#");
	         return s[1];
	    }
	    
	    public ArrayList<String> adjacentNodes(String Node){
	    	ArrayList<String> s = new ArrayList<String>();
	    	model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
	        dm = model.getDocumentManager();
	        dm.addAltEntry(NamingContext, "file:" + JENAPath + File.separator + MODIFIED_PREFIX + OntologyFile);
	        model.read(NamingContext);
	    	Individual instance = model.getIndividual(MAPA_BASE_URI + "#" + Node);
	        if(instance == null) {
	        	 s.add("El nodo " + Node + " no existe");
	        	 return s; 
	        }
	        Property nameProperty = model.createDatatypeProperty(MAPA_BASE_URI + "#contiguoA");
	    
	        for (StmtIterator it = instance.listProperties(nameProperty); it.hasNext(); ) {
	        	Statement x = it.next();
	        	
	        	//System.out.println(x.getBag());
	        	String[] aux = (x.getBag().toString()).split("#");
	        	s.add(aux[1]);
	        }
	        //System.out.println(instance.getPropertyResourceValue(nameProperty));
	        model.close();
	    	return s;
	    }
	    
	    public boolean isNodeRecolector(String Node) {
	    	model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
	        dm = model.getDocumentManager();
	        dm.addAltEntry(NamingContext, "file:" + JENAPath + File.separator + MODIFIED_PREFIX + OntologyFile);
	        model.read(NamingContext);
	        
	        Individual instance = model.getIndividual(MAPA_BASE_URI + "#" + Node);
	        boolean ret = instance.hasOntClass(MAPA_BASE_URI + "#NodeRecolector");
	        model.close();
	    	return ret;
	    }
	    
	    public void moverAgente(String Agent, String Node) throws FileNotFoundException {
	    	model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
	        dm = model.getDocumentManager();
	        dm.addAltEntry(NamingContext, "file:" + JENAPath + File.separator + MODIFIED_PREFIX + OntologyFile);
	        model.read(NamingContext);
	        
	        Individual agente = model.getIndividual(MAPA_BASE_URI + "#" + Agent);
	        Individual nodo = model.getIndividual(MAPA_BASE_URI + "#" + Node);
	        Property nameProperty = model.getProperty(MAPA_BASE_URI + "#Located_in");
	        RDFNode nameValue = agente.getPropertyValue(nameProperty);
	        agente.removeProperty(nameProperty, nameValue);
	        
	        agente.addProperty(nameProperty, nodo);
	        
	        model.write(new FileOutputStream(JENAPath + File.separator + MODIFIED_PREFIX + OntologyFile, false));
            model.close();
	        
	    }
	    
	    public boolean puedeDescargar(String Agent) {
	    	model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
	        dm = model.getDocumentManager();
	        dm.addAltEntry(NamingContext, "file:" + JENAPath + File.separator + MODIFIED_PREFIX + OntologyFile);
	        model.read(NamingContext);
	        
	        Individual instance = model.getIndividual(MAPA_BASE_URI + "#" + Agent);
	        Property nameProperty = model.getProperty(MAPA_BASE_URI + "#puedeDescargar");
	        boolean ret = instance.hasProperty(nameProperty);
	        model.close();
	    	return ret;
	    }
	    
	    public static void main(String args[]) throws FileNotFoundException {
        System.out.println("Starting tester...");
        
        String path = "./";
        String owlFile = "myMap.owl";
        String namespace = "myMap";
        mapaOnt tester = new mapaOnt(path, owlFile, namespace);
        
        //creamos una copia de la ontologia para no editar la original
        tester.initialize();
        //tester.testInferencia();
        System.out.println(tester.whereAmI("AgenteRecolector"));
        System.out.println(tester.adjacentNodes("node3"));
        
        System.out.println(tester.isNodeRecolector("node5"));
        System.out.println(tester.isNodeRecolector("node3"));
        
        System.out.println(tester.puedeDescargar("AgenteRecolector"));
        tester.moverAgente("AgenteRecolector","node3");
        
        System.out.println(tester.whereAmI("AgenteRecolector"));
        System.out.println(tester.isNodeRecolector(tester.whereAmI("AgenteRecolector")));
        System.out.println(tester.puedeDescargar("AgenteRecolector"));
        
	    }
}