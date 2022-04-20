package Entregable;


import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;

import java.util.Random;

// java.Boot -gui Termometro:Entregable.Termometro(1,250,99,5);Termostato:Entregable.Termostato(-50,50)

public class Termometro extends Agent {
    private int m; //MEDIO
    private int r; //RANGO
    private int p; //PROBABILIDAD
    private int s; //FRECUENCIA ACTUALIZACIÓN (Segundos (s))


    int ACT_TEMPERATURE;

    protected void setup() {
        //Lectura argumentos
        Object[] args = getArguments();

        if (args.length == 4) {
            //Registration
            //Deteccion de error en input
            try {
                m = Integer.parseInt((String) args[0]);
                r = Integer.parseInt((String) args[1]);
                p = Integer.parseInt((String) args[2]);
                s = Integer.parseInt((String) args[3]);
                printText("NUEVO TERMOMETRO " +getAID().getLocalName() + " con los valores m="+m + " r=" + r + " p="+p+" s="+ s);

            } catch (Exception e) {
                System.out.println("LOS PARAMETROS TIENEN QUE SER NUMEROS ENTEROS");
                doDelete();
                return;
            }

            if (p > 100 || p < 0) {
                System.out.println("PROBABILIDAD INVALIDA");
                doDelete();
                return; //esto hace falta porque si no sigue ejecutando el codigo
            }
            //Lectura de input
            CambioTemp b = new CambioTemp(this, s * 1000L);
            this.addBehaviour(b);
        } else {
            System.out.println("~~~~~~~~~~~~~USAGE~~~~~~~~~~~~~");
            System.out.println("Los parametros necesarios son: ");
            System.out.println("m = valor medio de las temperaturas que puede leer");
            System.out.println("r = rango (Superior y Inferior) de temperaturas a partir del valor medio");
            System.out.println("p = valor formula probabilidad para calcular temperatura [0,100]");
            System.out.println("s = frecuencia de actualización del Termometro");

            doDelete();
        }
    }

    protected void takeDown() {
        // Printout a dismissal message
        System.out.println("Termometro " + getAID().getLocalName() + " terminating.");
    }

    public class CambioTemp extends TickerBehaviour {

        public CambioTemp(Agent a, long period) {
            super(a, period);
        }

        public void onStart() {
            System.out.println("TERMOMETRO " + getAID().getLocalName() + " encendido");
        }

        public int onEnd() {
            //nunca deberia tener finall
            return -1;
        }

        public void onTick() {
            Random x = new Random();
            int rand = x.nextInt(101);
            // Primera opció: Amb prob 1-(q/100) -> num aleatori entre [m-r, m+r]
            if (rand >= p) {
                int low = m-r;
                int high = m+r;
                ACT_TEMPERATURE = x.nextInt(high-low) + low;
                printText("TERMOMETRO " + getAID().getLocalName() +  " ha obtenido la temperatura "+ ACT_TEMPERATURE
                        + "ºC mediante ProbA=[0, " +  p + ") vs ProbB=[" + p  + " < "+ rand + " < 100] --> 1era opció [m-r, m+r] " +
                        "--> Temp [" + (m-r) + " < " + ACT_TEMPERATURE + " < " + (m+r) + " ]");
            }
            // Segona opció: Amb prob q/100 -> num aleatori fora del rang [m-3*r, m+3*r]
            else {
                int low = m-(3*r);
                int high = m+(3*r);
                ACT_TEMPERATURE = x.nextInt(high-low) + low;
                printText("TERMOMETRO " + getAID().getLocalName() +  " ha obtenido la temperatura " + ACT_TEMPERATURE
                        + "ºC mediante ProbA=[0 < "+ rand + ") < "+ p +" vs ProbB=[" + p  + ", 100] --> 2a opció [m-3*r, m+3*r] " +
                        "--> Temp [" + (m-(3*r)) + " < " + ACT_TEMPERATURE + " < " + (m+(3*r)) + " ]");
            }

            //AQUI FALTA QUE ENVIE UN MENSAJE CON LA TEMPERATURA A TODOS LOS TERMOSTATOS

            try {
                //SEND message
                DFAgentDescription dfa = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("Termostato");
                dfa.addServices(sd);
                SearchConstraints sc = new SearchConstraints();

                DFAgentDescription[] results = DFService.search(myAgent, dfa, sc);
                if (results.length > 0) {
                    for(int i = 0; i < results.length; ++i){
                        ACLMessage msg_send = new ACLMessage(ACLMessage.INFORM);
                        DFAgentDescription dfd = results[i];
                        AID t = dfd.getName();
                        msg_send.addReceiver(t);
                        msg_send.setContent(Integer.toString(ACT_TEMPERATURE));
                        send(msg_send);
                      }
                } else System.out.println("No se han encontrado termostatos..");
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        }

    }


    private void printText(String text) {
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_RESET = "\u001B[0m";

        System.out.println(ANSI_BLUE + text + ANSI_RESET);
    }
}
