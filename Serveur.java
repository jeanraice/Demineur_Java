import java.net.* ; // Sockets
import java.io.* ; // Streams
import javax.swing.*;
import java.util.*;

/**
Class Server for create the field in mode multi-players
and manage the connexion between players and the server
 */
public class Serveur extends JFrame implements Runnable {
    private ServerSocket gestSock;


    private Champ champ;

    private int champDimen;

    private int arraysize = 0;


    private static int numero_joueur;


    private int position_X;
    private int position_Y;

    private String client;

    private int joueur;

   private int currentPlayerNumber ;

    private String typeClick;

    private DataOutputStream currentOutput;
    private DataOutputStream current;

    private final List<DataOutputStream> clients = new ArrayList<>();
    private  final ArrayList<String> currentPlayer = new ArrayList<String>();
    private  final List<String> clientsName = new ArrayList<String>();



    /**
     * This function is a constructor of server
     */


    Serveur() {
        System.out.println("Démarrage du serveur");
        numero_joueur = 0;

        //createChamp();
        try {
            // gestionnaire de socket, port

            gestSock = new ServerSocket(30);
            Thread canal = new Thread(this);
            canal.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Serveur();
    }

    /**
     * The function has the function to realize the  connexion server and clients
     */
    public  void run() {

        System.out.println("serveur en attente");
        try {
            Socket socket = gestSock.accept(); //attente
            // ouverture des streams
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String nomJoueur = in.readUTF();
            clientsName.add(nomJoueur);
            //System.out.println(nomJoueur + " connected");
            currentPlayer.add(nomJoueur + " connected");
            out.writeUTF(nomJoueur + " connected");
            //notifyAllTheClients(connexionNotifcation);
            clients.add(out);
            System.out.println(nomJoueur + " connected");
            out.writeInt(numero_joueur);

            //currentPlayerNumber = numero_joueur +1;
            numero_joueur++;



            Thread t = new Thread(this);
            t.start();

            sendFieldToClients(/*out,*/ nomJoueur);
            //currentOutput =out;

            updateConnectedClientToAll();


            String message = "";
            while (!message.equals("fin")) {
                try {
                    message = in.readUTF();
                    switch (message) {
                        case "sendMeField":
                            sendFieldToClients(/*out,*/ nomJoueur);
                            out.writeInt(currentPlayerNumber);
                            break;

                        case "right click" :

                            position_X = in.readInt();
                            position_Y = in.readInt();
                            typeClick = "right click";
                            sendUpdateToAll();
                            break;

                        case "left click" :

                            position_X = in.readInt();
                            position_Y = in.readInt();
                            typeClick = "left click";
                            sendUpdateToAll();
                            break;
                        case "he lose":
                            //out.writeUTF("you lose");
                            String player = in.readUTF();
                            System.out.println("the player name is "+ player);
                            int playerOutput = clientsName.indexOf(player);
                            System.out.println("the index is " + playerOutput);
                            currentOutput=clients.get(playerOutput);
                            notifyTheLoser(currentOutput);
                            break;
                        case  "send connected to":
                            //String connectedPeople= in.readUTF();
                            // notifyAllTheClients();
                            updateConnectedClientToAll();
                            break;
                        default:
                            System.out.println(nomJoueur + " : " + message);
                            sendMessageToAll(message);
                            break;
                    }

                } catch (Exception e) {
                    System.out.println(nomJoueur + " disconnected");
                    message = "fin";
                }


            }

            in.close(); // fermeture Stream
            out.close();
            socket.close(); // fermeture Socket
            System.out.println(nomJoueur + " disconnected");
            clients.remove(out);
            clientsName.remove(nomJoueur);


            updateConnectedClientToAll();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * create a new champ to send to the player
     */
    public void createChamp() {

        champ = new Champ(Levels.Medium);
        champ.placeMines();
        champDimen = champ.getDim();

    }

    /**
     * Send message to all the client
     * @param message
     */
    public void sendMessageToAll(String message){
        clients.forEach(element -> {
            try {
                element.writeUTF(message);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    /**
     * Send to all the client the data of the case to update a the click type
     */
    public void sendUpdateToAll() {
        clients.forEach(element -> {
            try {
                if(!element.equals(currentOutput)){
                element.writeUTF(typeClick);
                element.writeInt(position_X);
                element.writeInt(position_Y);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }
    public void notifyAllTheClients(){
        clients.forEach(element ->{
            try {
               /*if(currentPlayer.size()>arraysize)*/for(int i=arraysize;i<currentPlayer.size();i++){
                     element.writeUTF("connected people");
                     element.writeUTF(currentPlayer.get(i));
                     arraysize=currentPlayer.size();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void notifyTheLoser(DataOutputStream out) {


            try {

                out.writeUTF("you lose");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
    public void sendFieldToClients(/*DataOutputStream out,*/ String nomJoueur) {
        createChamp();
        clients.forEach(elements -> {
            try {
                elements.writeUTF("returnedField");
                elements.writeInt(champDimen);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            for (int i = 0; i < champDimen; i++) {
                for (int j = 0; j < champDimen; j++) {

                    try {
                        elements.writeUTF(champ.getElementFromXY(i, j));
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Failed to writeUTF " + nomJoueur);
                    }
                }
            }
        });
    }





    private void updateConnectedClientToAll() {
        clients.forEach(o -> {
            try {
                o.writeUTF("-1:connectedClients");
                o.writeInt(clientsName.size());
                clientsName.forEach(pseudo -> {
                    try {
                        o.writeUTF(pseudo);
                    } catch (IOException e) {
                        System.out.println("error writing message : updateConnectedClientToAll");
                    }
                });
            } catch (IOException e) {
                System.out.println("error writing message : updateConnectedClientToAll");
            }
        });
    }

}





