import java.awt.event.MouseListener;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.* ;
import java.io.IOException;

/**
 Class Case which will allow to create the pixels which will shelter the mines and the numbers which surround them
 */
public class Case extends JPanel implements MouseListener{

    private String txt;



    private boolean clicDroit;

    private boolean clockIsStarted;

    private boolean clicGauche = false ;

    private static boolean canPlay = true;

    private static boolean isMultiPlayer;

    private final int pos_X;
    private final int  pos_Y;

    private final GUI gui;
    private final ImageIcon drapeau = new ImageIcon("drapeau.jpg");
    private int flagPlaced = 0 ;

    private boolean isOpened;



    Case(int pos_X,int pos_Y,GUI gui) {
        this.gui = gui;
        int dimension = 50;
        setPreferredSize(new Dimension(dimension, dimension));
        clicDroit= false;
        this.pos_X = pos_X;
        this.pos_Y = pos_Y;
        addMouseListener(this);


    }

    /**
     * method which permit to update the display
     */


    public void paintComponent(Graphics gc) {

            if(!clicDroit){
                if(!clicGauche) {

                    super.paintComponent(gc); // appel méthode mère (efface le dessin précedent)
                    setBorder(BorderFactory.createLineBorder(Color.black));
                    gc.setColor(Color.cyan); // cyan
                    gc.setColor(new Color(128, 128, 128)); // grey
                    gc.fillRect(0, 0, getWidth(), getHeight());

                }
                else{

                    if(flagPlaced==1){

                        gc.drawImage(drapeau.getImage(),0,0, getWidth(), getHeight(), this);
                        if(gui.main.getChamp().isMine(pos_X, pos_Y)){

                        }
                    }

                    if(flagPlaced==0){
                        super.paintComponent(gc); // appel méthode mère (efface le dessin précedent)
                        setBorder(BorderFactory.createLineBorder(Color.black));
                        gc.setColor(Color.cyan); // cyan
                        gc.setColor(new Color(128, 128, 128)); // grey
                        gc.fillRect(0, 0, getWidth(), getHeight());
                        if(gui.main.getChamp().isMine(pos_X, pos_Y)){

                        }
                    }

                }
            }
            else{
                 super.paintComponent(gc);
                 if(!txt.equals("x")){
                     if(txt.equals("1")){
                         gc.setColor(Color.BLUE);
                         gc.drawString(txt,-3 + (getWidth() + 1) / 2, 3 + (getHeight() + 1) / 2);
                     }else if(txt.equals("2")){
                         gc.setColor(Color.green);
                         gc.drawString(txt,-3 + (getWidth() + 1) / 2, 3 + (getHeight() + 1) / 2);
                     }else if(txt.equals("3")){
                         gc.setColor(Color.red);
                         gc.drawString(txt,-3 + (getWidth() + 1) / 2, 3 + (getHeight() + 1) / 2);
                     }else if(txt.equals("4")){
                         gc.setColor(new Color(250, 135, 250));
                         gc.drawString(txt,-3 + (getWidth() + 1) / 2, 3 + (getHeight() + 1) / 2);
                     }
                 }else{
                     try {
                         Image mine = ImageIO.read(getClass().getResource("Minesweeper_Icon.png"));
                         gc.drawImage(mine,0,0,getWidth(),getHeight(),this);
                     } catch (IOException ex) {
                         throw new RuntimeException(ex);
                     }
                 }
                //gc.fillRect(0, 0, getWidth()/2, getHeight()/2);
                 if(!isOpened) {
                    gui.incrementCasesOuvertes();
                    gui.checkYouWin();
                    isOpened = true;
                }


            }

        }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!isMultiPlayer) {

            if (SwingUtilities.isRightMouseButton(e)) {
                clicDroit = true;
                boolean touche = gui.main.getChamp().isMine(pos_X, pos_Y);
                if (touche) {
                    txt = "x";
                    repaint();
                    gui.youLost();
                } else{
                    gui.repaintAround(pos_X, pos_Y);

                }


            } else if (SwingUtilities.isLeftMouseButton(e)) {
                clicGauche = true;
                repaint();
                gui.setVisible(false);
                gui.updatePanelNorth();
                gui.setVisible(true);
                flagPlaced = 1 - flagPlaced;

                if(gui.main.getChamp().isMine(pos_X, pos_Y)){
                    if(flagPlaced == 0){
                        gui.decrementMinesDiscovered();

                    }
                    else if (flagPlaced == 1){
                        gui.incrementMinesDiscovered();


                    }
                }
                gui.checkYouWin();
            }

        } else if(isMultiPlayer && canPlay) {
            if (SwingUtilities.isRightMouseButton(e)) {
                clicDroit = true;
                boolean touche = gui.main.getChamp().isMine(pos_X, pos_Y);
                if (touche) {
                    txt = "x";
                    repaint();
                    gui.youLostMulti();
                }
                gui.sendClickDataToServer(pos_X,pos_Y,"right click");



            } else if (SwingUtilities.isLeftMouseButton(e)) {
                gui.sendClickDataToServer(pos_X,pos_Y,"left click");
                if(gui.main.getChamp().isMine(pos_X, pos_Y)){
                    if(flagPlaced == 0){
                        gui.decrementMinesDiscovered();

                    }
                    else if (flagPlaced == 1){
                        gui.incrementMinesDiscovered();


                    }
                }
                gui.checkYouWin();
            }
        }else if(isMultiPlayer && !canPlay){
            System.out.println("coucou");
           int  answer = JOptionPane.showConfirmDialog(gui.main, "You can't play ! Do you want to quit the game ? ");
           if(answer == JOptionPane.YES_OPTION){
               System.exit(0);
           }

        }

    }


        @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e){

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

  /**
   * function used when you clic on a case for discover what is back the case.
   */
    public void discoverYourself() {

        if(gui.main.getChamp().getElementFromXY(pos_X,pos_Y).equals("0")){
            txt = " ";
        }else{
            txt = gui.main.getChamp().getElementFromXY(pos_X,pos_Y);
        }
            clicDroit = true;
            repaint();
    }

    /**
     * Function used in multi Player mode for put a Flag on a  case
     */

    public void putFlag(){
         clicGauche = true;
         repaint();
    }

    public void  discoverCase(int i, int j){
        txt = gui.main.getChamp().getElementFromXY(i,j);
        clicDroit = true;
        repaint();
    }

    /**
     * This function is used in multi-player to uptade the multi-Player flag
     * @param multiPlayer
     */
    public static void setMultiPlayer(boolean multiPlayer) {

        isMultiPlayer = multiPlayer;
    }
    /**
     * This function modify CanPlay. It's will block or let a player the permission to play
     */

    public static void setCanPlay(boolean can){
      canPlay =can;
    }

}
