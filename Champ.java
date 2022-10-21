import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

import javax.swing.*;

/**
 * class Champ for create the champ of mines
 */
public class Champ implements Serializable {


    private boolean[][] mines; //La carte des mines
    private final static int[] dim_tab = {10, 20, 30};
    private final static int[] nombreMine_tab = {6, 12, 18};
    private int dim;

    private int nombreMines;
    private final static int[] nombre_drapeau = {18, 25,30,100};


    /*
    Methode pour placer des methodes
     */
    public Champ(Levels niveau) {

        if (niveau.ordinal() == 3) {
            JTextField dimCustom = new JTextField();
            JTextField nbMinesToSelect = new JTextField();
            Object[] message = {
                    "Field dimension :", dimCustom,
                    "Number of mines:", nbMinesToSelect
            };
            int option = JOptionPane.showConfirmDialog(null, message, "Enter the field parameters",
                    JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) { // Check if OK_OPTION is ok
                nombreMines= Integer.parseInt(nbMinesToSelect.getText());
                dim = Integer.parseInt(dimCustom.getText());

            }


        } else {
            dim = dim_tab[niveau.ordinal()];
            nombreMines = nombreMine_tab[niveau.ordinal()];
        }
        mines = new boolean[dim][dim];
    }
    
    public Champ(int dimension){
        dim=dimension;
        mines = new boolean[dim][dim];
    }

    public int getDim() {
        return dim;
    }

    public int compteNbMines(int x, int y) {
        int nb = 0;
        int borneInfX = x == 0 ? 0 : x - 1;
        int borneInfY = y == 0 ? 0 : y - 1;
        int borneSupX = x == mines.length - 1 ? mines.length - 1 : x + 1;
        int borneSupY = y == mines[0].length - 1 ? mines[0].length - 1 : y + 1;

        for (int i = borneInfX; i <= borneSupX; i++) {
            for (int j = borneInfY; j <= borneSupY; j++) {
                if (mines[i][j])
                    nb++;
            }
        }
        return nb;
    }

    public void placeMines() {
        int minesaPlacer = nombreMines;
        Random gene = new Random();
        while (minesaPlacer != 0) {
            int x = gene.nextInt(dim);
            int y = gene.nextInt(dim);
            if (!mines[x][y]) {
                mines[x][y] = true;
                minesaPlacer--;
            }

        }
    }






    /**
     * Methode permettant d'afficher le champ des mines.

     */
    public String getElementFromXY(int x, int y) {
        if (mines[x][y]) {
            return "x";
        }
        return String.valueOf(compteNbMines(x, y));
    }

    public int getNombremines() {
        return nombreMines;
    }

    public String getLevel() {
        if (this.getDim() == 10 && this.nombreMines==6) {
            return "Easy";
        } else if (this.getDim() == 20 && this.nombreMines==12) {
            return "Medium";
        } else if (this.getDim() == 30 && this.nombreMines==18) {
            return "Hard";
        } else
            return "Custom";
    }

    public void resetMines() {
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                mines[i][j] = false;
            }
        }
    }

    public void easy() {
        dim = dim_tab[0];
        mines = new boolean[dim][dim];
        nombreMines = nombreMine_tab[0];

        placeMines();

    }

    public void medium() {
        dim = dim_tab[1];
        mines = new boolean[dim][dim];
        nombreMines = nombreMine_tab[1];
        placeMines();
    }

    public void hard() {
        dim = dim_tab[2];
        mines = new boolean[dim][dim];
        nombreMines = nombreMine_tab[2];
        placeMines();
    }

    public void custom(int  dimCustom, int nombreDeMines){
        dim = dimCustom;
        nombreMines=nombreDeMines;
        mines = new boolean[dimCustom][dimCustom];
        placeMines();
    }


    public void affText() {
        for (int i = 0; i < mines.length; i++) {
            for (int j = 0; j < mines[0].length; j++) {
                if (mines[i][j]) {
                    System.out.print("x");
                } else
                    System.out.print(compteNbMines(i, j));
            }
            System.out.println();

        }
    }


    public boolean getXY(int x, int y) {
        return mines[x][y];
    }

    public int getCurrentNumberFlag() {
        if(getLevel().equals( "Easy")){
        return nombre_drapeau[0];}
        else if(getLevel().equals("Medium")){
            return nombre_drapeau[1];}
        else if(getLevel().equals("Hard")){
            return nombre_drapeau[2];
        }
     return 0;
    }

    public int getNombreDrapeaux(String level) {
        if(Objects.equals(level, "Easy")){
            return nombre_drapeau[0];}
        else if(Objects.equals(level, "Medium")){
            return nombre_drapeau[1];}
        else if(Objects.equals(level, "Hard")){
            return nombre_drapeau[2];
        }
        return 0;
    }

    public Levels getLevelEnum(){
        if (this.getDim() == 10) {
            return Levels.Easy;
        } else if (this.getDim() == 20) {
            return Levels.Medium;
        } else if (this.getDim() == 30) {
            return Levels.Hard;
        } else
            return Levels.Custom;
    }
    public boolean isMine(int i,int j){
     return mines[i][j];
    }

    public void setChampMine(int x, int y,  boolean valeurChamp){
        mines[x][y]=valeurChamp;
    }
}


