package Entregable;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.lang.acl.MessageTemplate;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class Termostato extends Agent {
    private int a; // valor mínimo aceptable de temperatura
    private int b; // valor máximo aceptable de temperatura

    private int temperaturaActual = 0;
    private int sum = 0;
    private int counter = 0;

    private final Map<String, Integer> invalidTempCounter = new HashMap<>();

    protected void setup() {
        Object[] args = getArguments();

        if (args.length == 2) {
            try {
                a = Integer.parseInt((String) args[0]);
                b = Integer.parseInt((String) args[1]);
                printHeader("NUEVO TERMOSTATO " + getAID().getLocalName() + " con los valores a=" + a + " b=" + b);

            } catch (Exception e) {
                System.out.println("LOS PARAMETROS TIENEN QUE SER NUMEROS ENTEROS");
                doDelete();
                return;
            }
            //Registration
            try {
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("Termostato");
                sd.setName(getLocalName());
                dfd.setName(getAID());
                dfd.addServices(sd);
                DFService.register(this, dfd);

                receiveMsgBehaviour b = new receiveMsgBehaviour(this);
                this.addBehaviour(b);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Behaviours


        } else {
            System.out.println("~~~~~~~~~~~~~USAGE~~~~~~~~~~~~~");
            System.out.println("Los parametros necesarios son: ");
            System.out.println("a = valor mínimo que acepta el termostato");
            System.out.println("b = valor máximo que acepta el termostato");

            doDelete();
        }

    }

    protected void takeDown() {
        // Printout a dismissal message
        System.out.println("Termostato " + getAID().getLocalName() + " terminating.");
        try {
            DFService.deregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class receiveMsgBehaviour extends CyclicBehaviour {
        public receiveMsgBehaviour(Agent a) {
            super(a);
        }

        public void action() {
            ACLMessage msg = myAgent.receive();
            String id = getAID().getLocalName();
            if (msg != null) {
                int temp;
                try {
                    temp = Integer.parseInt(msg.getContent());
                    String senderId = msg.getSender().getLocalName();

                    if (!invalidTempCounter.containsKey(senderId) || invalidTempCounter.get(senderId) < 5) {

                        if (temp < a || temp > b) {
                            DFAgentDescription dfa = new DFAgentDescription();
                            ServiceDescription sd = new ServiceDescription();

                            ACLMessage msg_send = new ACLMessage(ACLMessage.INFORM);

                            sd.setType("alarm-management");
                            dfa.addServices(sd);


                            String text = "TERMOSTATO " + id + ": ha recibido la temperatura " + temp
                                    + "ºC de " + senderId + " --> NO es correcta [" + a + ", " + b + "]        ";
                            if (counter > 0) text += "Temperatura actual: " + temperaturaActual+ "ºC";;

                            msg_send.setContent("text");
                            send(msg_send);
                            printHeader(text);

                            int count = invalidTempCounter.getOrDefault(senderId, 0);
                            invalidTempCounter.put(senderId, count + 1);


                        } else {

                            sum += temp;
                            counter++;
                            temperaturaActual = sum / counter;

                            String text = "TERMOSTATO " + id + ": ha recibido la temperatura " + temp
                                    + "ºC de " + senderId + " --> SI es correcta [" + a + " < " + temp + " < " + b + "]        " +
                                    "Temperatura actual: " + temperaturaActual + "ºC";
                            printHeader(text);


                            if (invalidTempCounter.containsKey(senderId)) {
                                invalidTempCounter.put(senderId, 0);
                            }


                        }
                    }
                    // En el caso de que haya fallado más de 5 veces consecutivas
                    // -> Termometro bloqueado permanentemente
                    else {
                        printAlert("El termómetro " + senderId + " esta bloqueado permanentemente por el termostato " + id);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                block();
            }
        }
    }

    private void printHeader(String text) {
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RESET = "\u001B[0m";

        System.out.println(ANSI_GREEN + text + ANSI_RESET);
    }

    private void printAlert(String text) {
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_RESET = "\u001B[0m";

        System.out.println(ANSI_RED + text + ANSI_RESET);
    }

}
