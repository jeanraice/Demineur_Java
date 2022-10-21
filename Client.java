import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * class client, give the different information on client
 * and the information about the others clients when you are in mode multi players
 */
public class Client implements Runnable{


    private final GUI gui;

    Socket sock;
    private DataInputStream in;
    private DataOutputStream out;
    private ArrayList<String> updateConnectedPlayer=new ArrayList<>();


    private int champDimen=20;

    List<String> playerConnected = new ArrayList<>();

    private int playerNumber;
    private final Thread readerThread = new Thread(this);

    Client(String nom,GUI gui){
        this.gui=gui;
        connexionSocket(nom);
    }

    public void connexionSocket(String nom){
        Scanner scanner  = new Scanner(System.in);
        try {
            /**
             * ouverture de la socket et des streams
             */
            sock = new Socket("localhost", 30);
            out = new DataOutputStream(sock.getOutputStream());
            in = new DataInputStream(sock.getInputStream());

            gui.setOutputClient(out);
            gui.setInputClient(in);
            out.writeUTF(nom);
            String playername =in.readUTF();
            gui.setPanelPlayerConnected(updateConnectedPlayer);


            int numJoueur = in.readInt();

            /**
            / reception d’un nombre
             */
            System.out.println("Joueur n°:" + numJoueur);
            String bufferUseless = in.readUTF();
            Champ champ = createChampClient();
            gui.setChamp(champ);
            gui.newChampLevel();

            String message = "";

            /**
             * Listener thread for messages received from the server
             */


            readerThread.start();
            while(!message.equals("fin")){

                System.out.print("Message to send: ");
                message = scanner.nextLine();
                out.writeUTF(message);

            }

            in.close(); // fermeture Stream
            out.close();
            sock.close(); // fermeture Socket
        }  
        catch (IOException e) {
                System.out.println("port serveur introuvé");
        } 
    }
    public Champ createChampClient(){
        String received;
        try {
            champDimen=in.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Champ champToReturn = new Champ(champDimen);
        for(int i=0;i<champDimen;i++){
            for(int j=0;j<champDimen;j++){
                
                try {
                    received=in.readUTF();
                    champToReturn.setChampMine(i,j, received.equals("x"));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return champToReturn;
    }

    @Override
    public void run() {
        int position_X;
        int position_Y;
        boolean readMessage= true;
        String message;
        while(readMessage){

            try {
                message = in.readUTF();

                switch (message) {
                    case "returnedField" :

                        gui.setChamp(createChampClient());

                        System.out.println(playerNumber);
                        if(gui.getChronoIsStartedMulti()){
                            gui.reset();
                            gui.getChrono().stop();
                        }
                        gui.newChampLevel();
                        Case.setCanPlay(true);

                        break;
                    case "right click" :
                          position_X = in.readInt();
                          position_Y = in.readInt();
                        System.out.println(message + " position_X : "+ position_X +  " position_Y : " + position_Y);
                        gui.repaintCase(position_X, position_Y, "right click");
                        break;

                    case "left click" :
                         position_X = in.readInt();
                         position_Y = in.readInt();
                        System.out.println(message + " position_X : "+ position_X +  " position_Y : " + position_Y);
                        gui.repaintCase( position_X,position_Y, "left click");
                        break;
                    case "connected people":
                        updateConnectedPlayer.add(in.readUTF());
                        gui.setPanelPlayerConnected(updateConnectedPlayer);

                         break;
                    case "you lose" :
                        JOptionPane.showMessageDialog(gui.main,"You Lost");

                        break;
                    case "-1:connectedClients" :
                        int totalConnected = in.readInt();
                        updateConnectedPlayer.clear();
                        for(int i = 0; i < totalConnected; i++) {
                            message = in.readUTF();
                            updateConnectedPlayer.add(message);
                        }

                        gui.setPanelPlayerConnected(updateConnectedPlayer);
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
                readMessage=false;

            }
        }
            
    }

    public void sendClickToServer(int position_X, int position_Y, String typeClick){
        try {
            out.writeUTF(typeClick);
            out.writeInt(position_X);
            out.writeInt(position_Y);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void filPlayerConnected(String playername){
         playerConnected.add(playername);

    }
    public void receiveConnectedPlayer(){

        String player= null;
        try {
            player = in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}


