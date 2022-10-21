import javax.swing.*;
import java.awt.*;
import java.awt.event.* ;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


/**
 * class GUI for the Display.
 */
public class GUI extends JPanel implements ActionListener, Runnable {

    final Main main;
    private int minesDiscovered;

    private int nombreDrapeau;

    private boolean chronoIsStarted;

    private boolean chronoIsStartedMulti;

    private int dimensionCustom = 0;

    private int nombreMinesCustom = 0;


    boolean connected;


    private String player_name;
    private final JPanel panelNorth;
    private final JPanel panelNorthWest;

    private JPanel panelPlayerConnected;
    private JScrollPane onlineUsersPanel;
    private JLabel niveauGame;


    private final JPanel panelSouth;

    private final JPanel borderG;
    private final JPanel borderD;
    private final JPanel borderC;
    private final JPanel panelNorthEast;
    private final JButton quit;
    private final JButton restart;

    private final JButton start;
    private Timer chrono;
    private Case[][] cases;

    private JLabel[][] tabMines;

    JPanel timePanel = new JPanel(new FlowLayout());
    JPanel panelCenter;
    JPanel panelCase;

    JLabel timeLabel = new JLabel();

    int elapsedTime = 0;
    int secondes = 0;
    int minutes = 0;

    String seconds_string = String.format("% 02d", secondes);

    String minutes_string = String.format("% 02d", minutes);

    JLabel lab3 = new JLabel("Flags : ");

    JLabel lab4 = new JLabel("Level : ");

    private DataOutputStream outClient;
    private DataInputStream inClient;
    private int numberPlayer;

    private boolean isLose;

    private String knowPlayer ="";

    private int arraySize=0;
    private int casesOuvertes;

    public void setOutputClient(DataOutputStream out) {
        outClient = out;
    }

    int num_joueur;
    JTextArea taUserList;


    GUI(Main main) {

        minesDiscovered = main.getChamp().getNombremines();
        nombreDrapeau = main.getChamp().getCurrentNumberFlag();
        this.main = main;
        int dimen = main.getChamp().getDim();
        createPanelcase(dimen);
        niveauGame = new JLabel(main.getChamp().getLevel());

        //declaration of JMenuBar and JMenuItem

        JMenuBar menuBar = new JMenuBar();
        JMenu option = new JMenu("Niveau");
        JMenuItem easy = new JMenuItem("Easy");
        JMenuItem hard = new JMenuItem("Hard");
        JMenuItem medium = new JMenuItem("Medium");
        JMenuItem custom = new JMenuItem("Custom");
        option.setToolTipText("Change the difficulty");
        option.add(easy);
        option.add(medium);
        option.add(hard);
        option.add(custom);

        //Add listenner to the JMenuItem

        easy.addActionListener(this);
        medium.addActionListener(this);
        hard.addActionListener(this);
        custom.addActionListener(this);
        option.addActionListener(this);
        menuBar.add(option);

        // JMenuBar menuBar1= new JMenuBar();
        JMenu mode_serveur = new JMenu("Connexion");
        JMenuItem partieServer = new JMenuItem("Multi-joueur");
        mode_serveur.add(partieServer);
        partieServer.addActionListener(this);
        mode_serveur.setToolTipText("Play in multi-player mode");
        menuBar.add(mode_serveur);
        main.setJMenuBar(menuBar);


        // Panelnorth

        quit = new JButton(new ImageIcon((new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/quitter.jpg"))).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH))));
        quit.addActionListener(this);
        quit.setToolTipText("Quit the Game");
        restart = new JButton(new ImageIcon((new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/restart.png"))).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH))));

        start = new JButton(new ImageIcon((new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/start.png"))).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH))));

        start.addActionListener(this);
        start.setToolTipText("Start the Game");

        restart.addActionListener(this);
        restart.setToolTipText("Restart the Game");
        quit.setPreferredSize(new Dimension(50, 50));
        restart.setPreferredSize(new Dimension(50, 50));
        start.setPreferredSize(new Dimension(50, 50));
        panelNorth = new JPanel(new BorderLayout());

        //JLabel centerFlow = new JLabel("CENTER");


        borderG = new JPanel(new BorderLayout());
        borderC = new JPanel(new FlowLayout());
        borderD = new JPanel(new BorderLayout());

        borderG.add(timePanel, BorderLayout.WEST);
        borderC.add(quit);
        borderC.add(restart);
        borderC.add(start);
        panelNorthEast = new JPanel(new FlowLayout());
        panelNorthWest = new JPanel(new FlowLayout());
        JLabel str = new JLabel(String.valueOf(nombreDrapeau));
        panelNorthEast.add(lab3);
        panelNorthEast.add(str);

        panelNorthWest.add(timePanel);
        borderD.add(panelNorthWest, BorderLayout.WEST);
        borderG.add(panelNorthEast, BorderLayout.EAST);

        panelNorth.add(borderG, BorderLayout.WEST);
        panelNorth.add(borderC);
        panelNorth.add(borderD, BorderLayout.EAST);


        //PanelCenter


        panelCenter = new JPanel(new GridLayout(dimen, dimen));
        panelCase = new JPanel(new GridLayout(dimen, dimen));
        panelCase.setLayout(new GridLayout(dimen, dimen));
        for (int i = 0; i < dimen; i++) {
            for (int j = 0; j < dimen; j++) {
                panelCase.add(cases[i][j]);
            }
        }

        panelCenter.setLayout(new GridLayout(dimen, dimen));
        for (int i = 0; i < dimen; i++) {
            for (int j = 0; j < dimen; j++) {
                panelCenter.add(tabMines[i][j]);
            }

        }

        //PanelSouth
        panelSouth = new JPanel(new FlowLayout());
        panelSouth.add(lab4);
        panelSouth.add(niveauGame);

        setLayout(new BorderLayout());

        add(panelNorth, BorderLayout.NORTH);
        add(panelCase, BorderLayout.CENTER);
        add(panelSouth, BorderLayout.SOUTH);



    }

    public Client getClient(String player_name) {
        return new Client(player_name, this);
    }

    @Override
    public void run() {

        main.setTitleFrame(player_name);
        Client client = new Client(player_name, this);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        setVisible(false);
        if (e.getSource() == quit) {
            quit();
        } else if (e.getSource() == start) {
            if (!chronoIsStarted) {
                clock();
                chrono.start();
                chronoIsStarted = true;
            } else {
                chrono.start();
            }
        } else if (e.getSource() == restart) {
            rejouer();
            reset();

        } else if (e.getSource() instanceof JMenuItem) {
            String choix = e.getActionCommand();
            switch (choix) {
                case "Easy":
                    main.getContentPane().removeAll();
                    main.getChamp().easy();
                    newChampLevel();
                    nombreDrapeau = main.getChamp().getNombreDrapeaux("Easy");
                    updatePanelNorth();
                    updatePanelSouth();
                    if (chronoIsStarted) {
                        chrono.stop();
                        reset();
                    } else {
                        reset();
                    }

                    changePlayMode(false);
                    connected = false;
                    break;
                case "Medium":
                    main.getContentPane().removeAll();
                    main.getChamp().medium();
                    newChampLevel();
                    nombreDrapeau = main.getChamp().getNombreDrapeaux("Medium");
                    updatePanelNorth();
                    updatePanelSouth();
                    if (chronoIsStarted) {
                        chrono.stop();
                        reset();
                    } else {
                        reset();
                    }

                    changePlayMode(false);
                    connected = false;
                    break;
                case "Hard":
                    main.getContentPane().removeAll();
                    main.getChamp().hard();
                    newChampLevel();
                    nombreDrapeau = main.getChamp().getNombreDrapeaux("Hard");
                    updatePanelNorth();
                    updatePanelSouth();
                    if (chronoIsStarted) {
                        chrono.stop();
                        reset();
                    } else {
                        reset();
                    }

                    changePlayMode(false);
                    connected = false;
                    break;

                case "Custom":
                    main.getContentPane().removeAll();
                    JTextField dimCustom = new JTextField();
                    JTextField nbMinesToSelect = new JTextField();
                    Object[] message = {
                            "Field dimension :", dimCustom,
                            "Number of mines:", nbMinesToSelect
                    };
                    while (nombreMinesCustom >= dimensionCustom) {
                        int option = JOptionPane.showConfirmDialog(null, message, "Define the field size",
                                JOptionPane.OK_CANCEL_OPTION);
                        if (option == JOptionPane.OK_OPTION) {
                            nombreMinesCustom = Integer.parseInt(nbMinesToSelect.getText());
                            dimensionCustom = Integer.parseInt(dimCustom.getText());
                        }
                        if (nombreMinesCustom >= dimensionCustom) {
                            JOptionPane.showMessageDialog(null,
                                    "The field size must be larger than the number of mines",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    main.getChamp().custom(dimensionCustom, nombreMinesCustom);

                    newChampLevel();
                    updatePanelSouth();
                    if (chronoIsStarted) {
                        chrono.stop();
                        reset();
                    } else {
                        reset();
                    }

                    changePlayMode(false);
                    dimensionCustom = 0;
                    nombreMinesCustom = 0;
                    connected = false;

                    break;

                //main.getChamp().custom();

                case "Multi-joueur":
                    if (!connected) {
                        player_name = JOptionPane.showInputDialog(null, "Please! Enter  your name ");
                        Thread clientThread = new Thread(this);
                        clientThread.start();
                        changePlayMode(true);
                        connected = true;
                    }

                    break;
            }
        }
        setVisible(true);
    }

    /**
    *Method for reset the Timer
     */
    public void reset() {
        //chrono.stop ();
        elapsedTime = 0;
        secondes = 0;
        minutes = 0;
        seconds_string = String.format("% 02d", secondes);
        minutes_string = String.format("% 02d", minutes);
        timeLabel.setText(minutes_string + ":" + seconds_string);
    }

    /**
     * Used for create a new fied of mines when the game is restarted
     *
     * @see #rejouer()
     */
    public void newPartie() {
        casesOuvertes = 0;
        minesDiscovered = main.getChamp().getNombremines();
        panelCase.removeAll();
        int dimen = main.getChamp().getDim();
        System.out.println(dimen);
        cases = new Case[dimen][dimen];
        for (int i = 0; i < dimen; i++) {
            for (int j = 0; j < dimen; j++) {
                String str = main.getChamp().getElementFromXY(i, j);
                tabMines[i][j].setText(str);
                cases[i][j] = new Case(i, j, this);
                panelCase.add(cases[i][j]);
            }
        }
        add(panelCase, BorderLayout.CENTER);
    }

    /**
     * Create a new champ and add him to the panelCase and panelCenter
     */
    public void newChampLevel() {
        casesOuvertes = 0;
        minesDiscovered = main.getChamp().getNombremines();
        int dimen = main.getChamp().getDim();
        cases = new Case[dimen][dimen];
        tabMines = new JLabel[dimen][dimen];
        setVisible(false);
        panelCenter.removeAll();
        panelCase.removeAll();
        panelCenter.setLayout(new GridLayout(dimen, dimen));
        panelCase.setLayout(new GridLayout(dimen, dimen));
        for (int i = 0; i < dimen; i++) {
            for (int j = 0; j < dimen; j++) {
                String str = main.getChamp().getElementFromXY(i, j);
                tabMines[i][j] = new JLabel(str);
                cases[i][j] = new Case(i, j, this);
                panelCenter.add(tabMines[i][j]);
                panelCase.add(cases[i][j]);
            }
        }

        this.clock();
        this.getChrono().start();
        chronoIsStartedMulti = true;
        this.updatePanelNorth();
        this.updatePanelSouth();
        add(panelCase, BorderLayout.CENTER);
        setVisible(true);
    }

    /**
     * This method allows the discover the case around a specific {@code Case}.
     *
     * @param x :  position of the case according to the line
     * @param y : position of the case according to the columns
     * @see #rejouer
     */
    public void repaintAround(int x, int y) {

        int borneInfX = x == 0 ? 0 : x - 1;
        int borneInfY = y == 0 ? 0 : y - 1;
        int borneSupX = x == main.getChamp().getDim() - 1 ? main.getChamp().getDim() - 1 : x + 1;
        int borneSupY = y == main.getChamp().getDim() - 1 ? main.getChamp().getDim() - 1 : y + 1;

        for (int i = borneInfX; i <= borneSupX; i++) {
            for (int j = borneInfY; j <= borneSupY; j++) {
                if (!main.getChamp().isMine(i, j)) {
                    cases[i][j].discoverYourself();
                }
            }
        }
    }

    public void changePlayMode(boolean bool) {
        Case.setMultiPlayer(bool);
    }

    public void repaintCase(int x, int y, String typeClick) {
        if (typeClick.equals("right click")) {
            cases[x][y].discoverYourself();
        } else if (typeClick.equals("left click")) {
            cases[x][y].putFlag();
        }

    }

    /**
     * For replays a party
     */
    public void rejouer() {
        casesOuvertes=0;

        if (!connected) {
            nombreDrapeau = main.getChamp().getCurrentNumberFlag();
            main.getChamp().resetMines();
            main.getChamp().placeMines();
            newPartie();
        } else {
            try {
                outClient.writeUTF("sendMeField");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * For quit the Game
     */
    public void quit() {
        System.exit(0);
    }


    public int score() {
        nombreDrapeau--;
        return nombreDrapeau;
    }

    public int getNombreDrapeau() {
        return nombreDrapeau;
    }

    /**
     * function have to goal to update the panelSouth this function is used in the function
     * it will be used to update the panelSouth when the level of the game have been changed
     */
    public void updatePanelSouth() {
        if(!connected){
        panelSouth.removeAll();
        niveauGame = new JLabel(main.getChamp().getLevel());
        panelSouth.add(lab4);
        panelSouth.add(niveauGame);
        add(panelNorth, BorderLayout.NORTH);
        add(panelSouth, BorderLayout.SOUTH);
        }else {
            panelSouth.removeAll();
            niveauGame = new JLabel(main.getChamp().getLevel());
            panelSouth.add(lab4);
            JLabel custom = new JLabel("you are online");
            panelSouth.add(custom);
            add(panelNorth, BorderLayout.NORTH);
            add(panelSouth, BorderLayout.SOUTH);
        }

    }

    /**
     * function have to goal to update the panelNorth this function is used in the function
     * it will be used to update the panelNorth when the number of flag remaining decrease
     */
    public void updatePanelNorth() {

        if (getNombreDrapeau() > 0) {
            JLabel str = new JLabel(String.valueOf(score()));
            panelNorthEast.removeAll();
            panelNorthWest.removeAll();
            borderG.removeAll();
            borderD.removeAll();
            panelNorthEast.add(lab3);

            panelNorthEast.add(str);

            panelNorthWest.add(timePanel);
            borderD.add(panelNorthWest, BorderLayout.WEST);
            borderG.add(panelNorthEast, BorderLayout.EAST);
        } else {
            panelNorthEast.removeAll();
            borderG.removeAll();
            JLabel zero = new JLabel("0");
            panelNorthEast.add(lab3);
            panelNorthEast.add(zero);
        }
        borderG.add(panelNorthEast, BorderLayout.WEST);
        panelNorth.add(borderG, BorderLayout.WEST);

        add(panelNorth, BorderLayout.NORTH);
        add(panelSouth, BorderLayout.SOUTH);
    }

    /**
     * When a player discovered a mines the game is over and this function
     * send the message to the player.
     */
    public void youLost() {

        JOptionPane.showMessageDialog(main, "You Lost");

        int response = JOptionPane.showConfirmDialog(main, "Voulez-vous rejouer ?");
        if (response == JOptionPane.YES_OPTION) {
            setVisible(false);
            reset();
            rejouer();
            add(panelCase, BorderLayout.CENTER);
            setVisible(true);
        } else
            System.exit(0);
    }

    /**
     * Function used for
     */
    public void youLostMulti() {
        if (!isLose) {
            numberPlayer--;
            JOptionPane.showMessageDialog(main, "You Lost");
            Case.setCanPlay(false);
            System.out.println("The current number of Player : " + numberPlayer);
            isLose = true;
        } else if (numberPlayer != 1) {
            numberPlayer--;
            JOptionPane.showMessageDialog(main, "You Lost");
            Case.setCanPlay(false);
        } else if (numberPlayer == 1) {
            youLost();

        }
    }

    public void incrementCasesOuvertes(){
        casesOuvertes++;
    }
    /**
     * This function check if a player have won a game or not
     */
    public void checkYouWin() {
        int dimen = main.getChamp().getDim();
        int numCasesTotal = dimen * dimen;
        int nbMines = main.getChamp().getNombremines();

        if (  ((numCasesTotal-casesOuvertes) == nbMines)  && minesDiscovered == 0) {
            JOptionPane.showMessageDialog(main, "You Won");
            int response = JOptionPane.showConfirmDialog(main, "Voulez-vous rejouer ?");
            if (response == JOptionPane.YES_OPTION) {
                setVisible(false);
                rejouer();
                add(panelCase, BorderLayout.CENTER);
                setVisible(true);
            } else
                System.exit(0);
        }

    }

    /**
     * Increment the number of discovered. Because the player can put a fllag on a place and after remove it .
     * So we have to increment the number of mines discovered if he removes a flag.
     */
    public void incrementMinesDiscovered() {
        minesDiscovered--;
    }

    /**
     * Decrement the number of mines found when the player put a flag on a mine
     */
    public void decrementMinesDiscovered() {
        minesDiscovered++;
    }

    public void createPanelcase(int dimen) {
        tabMines = new JLabel[dimen][dimen];
        cases = new Case[dimen][dimen];

        for (int i = 0; i < dimen; i++) {
            for (int j = 0; j < dimen; j++) {
                cases[i][j] = new Case(i, j, this);
                tabMines[i][j] = new JLabel(main.getChamp().getElementFromXY(i, j));
            }

        }
    }

    public void setChamp(Champ champToDisplay) {
        main.setChamp(champToDisplay);
    }


    /**
     * use to linked the Client input the gui input
     * @param in : DataInputStream
     */
    public void setInputClient(DataInputStream in) {
        inClient = in;
    }

    public Case getCase() {
        return new Case(0, 0, this);
    }

    /**
     * Function used for send a client clic to the server in order that
     * the server notify the others
     * @param position_X : the case clicked  X_axis
     * @param  position_Y : the case clicked Y_axis
     * @param  typeClick :the type of click (Right or Left)
     */


    public void sendClickDataToServer(int position_X, int position_Y, String typeClick) {

        try {
            outClient.writeUTF(typeClick);
            outClient.writeInt(position_X);
            outClient.writeInt(position_Y);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void receiveTheNumberOfPlayers() {
        try {
            numberPlayer = inClient.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

/**
 * Initialisation of the Timer and the add to the timePanel in the PanelNorth
 */

    public void clock() {
        chrono = new Timer(1000, e -> {
            elapsedTime = elapsedTime + 1000;
            minutes = (elapsedTime / 60000) % 60;
            secondes = (elapsedTime / 1000) % 60;
            seconds_string = String.format("% 02d", secondes);
            minutes_string = String.format("% 02d", minutes);
            timeLabel.setText(minutes_string + ":" + seconds_string);
            timePanel.add(timeLabel);

        });
    }

    /**
     * Timer getter
     */

    public Timer getChrono() {

        return chrono;
    }

    /**
     * getter of the Timer flag
     */

    public boolean getChronoIsStartedMulti() {
        return chronoIsStartedMulti;
    }
    /**
     * This function is used for notify the server when a gamer have lost a party
     */


    public void notifyTheLoser() {
        try {
            outClient.writeUTF("he lose");
            outClient.writeUTF(player_name);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Used for add the client connected to the Panel for connected clients
     */


    public void setPanelPlayerConnected(ArrayList<String> A){

        panelPlayerConnected = new JPanel(new FlowLayout());
        taUserList = new JTextArea();
        taUserList.setRows(10);
        taUserList.setColumns(10);
        taUserList.setEditable(false);

        A.forEach(pseudo -> taUserList.append(pseudo + " connected"+"\n"));

            onlineUsersPanel = new JScrollPane(taUserList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            panelPlayerConnected.add(onlineUsersPanel);

        add(panelPlayerConnected,BorderLayout.WEST);
    }
}





