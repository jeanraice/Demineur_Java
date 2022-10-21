import javax.swing.*;

/**
 * Class Main for lunch the programm
 */
public class Main extends JFrame {
    private Champ champ;
    public Champ getChamp(){
        return champ;
    }
    Main(){


        champ = new Champ(Levels.Easy);
        champ.placeMines();
        GUI gui = new GUI(this);
        setContentPane(gui);
        pack();
        setVisible(true) ;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Xmines");
        JOptionPane.showConfirmDialog(this, "Appuyer sur le bouton 'Start' Pour commencer le jeu","" ,JOptionPane.OK_CANCEL_OPTION);


    }
    public static void main(String[] args) {
        new Main();
       // System.out.println("Hello world!");
    }

    public void setTitleFrame(String titleFrame) {
        setTitle(titleFrame);
    }
    public void setChamp(Champ champToDisplay) {
        this.champ=champToDisplay;
    }


}