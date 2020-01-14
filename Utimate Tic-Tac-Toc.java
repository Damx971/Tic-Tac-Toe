import java.util.*;
import java.io.*;
import java.math.*;

class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        boolean start = true;
        Noeud best_arbre = null;
        int prof1 = 6;
        int prof2 = 4; 
        int turn = 0;


        // game loop
        while (true) {
            int opponentRow = in.nextInt();
            int opponentCol = in.nextInt();
            int validActionCount = in.nextInt();
            int best_score = -10000000;
            Noeud arbre_tmp = null;
            Noeud new_arbre = null;
            
            
            if(start){
                if( opponentRow == -1 && opponentCol == -1 ){
                    turn = 1;
                }else{
                    turn = 2;
                }
            }else{
                turn+=2;
            }
            
            
            if(!start && turn >= ( prof1-( prof2 / 2 ) ) ){
                    best_arbre.setMaxProf(prof2);
                    best_arbre.create();
            }
            
            if(!start && best_arbre != null){
                arbre_tmp = best_arbre.get(opponentRow,opponentCol);
                best_arbre = arbre_tmp;
            }


            for (int i = 0; i < validActionCount; i++) {
                int row = in.nextInt();
                int col = in.nextInt();
                
                if(start && opponentCol==-1){

                    arbre_tmp = new Noeud(null,row,col,true,turn,prof1,-1,-1);
                    if(arbre_tmp.getScore() > best_score){
                        new_arbre = arbre_tmp;
                        best_score = arbre_tmp.getScore();
                    }

                }else if(start && opponentCol!=-1){

                    arbre_tmp = new Noeud(null,row,col,true,turn,prof1,opponentRow,opponentCol);
                    if(arbre_tmp.getScore() > best_score){
                        new_arbre = arbre_tmp;
                        best_score = arbre_tmp.getScore();
                    }

                }else if(!start){

                    arbre_tmp = best_arbre.get(row,col);
                    
                    if(arbre_tmp == null){
                        arbre_tmp = best_arbre.get(row,col);
                    }

                    System.err.println("turn : "+turn+", Noeud :"+i+": x_"+row+" y_"+col+", score arbre_tmp: "+ arbre_tmp.getScore()+", best score :"+best_score);

                    if(arbre_tmp.getScore() > best_score){
                        new_arbre = arbre_tmp;
                        best_score = arbre_tmp.getScore();
                    }

                }
            }

            start = false;
            best_arbre = new_arbre;

            System.err.println("best score final : "+best_score+", choosen_x : "+best_arbre.getX()+", choosen_y : "+best_arbre.getY());

            
            System.out.println(best_arbre.getX() + " " + best_arbre.getY());
            
        }
    }
}

class Case{
    private boolean played1;
    private boolean played2;
    
    public Case(boolean played1, boolean played2){
        this.played1 = played1;
        this.played2 = played2;
    }
    
    public void play(boolean played2){
        this.played1 = true;
        this.played2 = played2;
    }
    
    public boolean getplayed1(){
        return this.played1;
    }
    
    public boolean getplayed1ByMe(){
        return this.played2;
    }
    
}

class Noeud{
    private boolean my_turn;
    private Case grid[][];
    private Noeud kids[];
    private Noeud previous_move;
    private int turn;
    private int x_move;
    private int y_move;
    private int score;
    private int max_prof;
    
    public Noeud(Noeud previous_move,int x_move,int y_move,boolean my_turn,int turn,int max_prof,int start_opponent_x,int start_opponent_y){ 
        this.previous_move = previous_move;
        this.x_move = x_move;
        this.y_move = y_move;
        this.my_turn = my_turn;
        this.turn = turn;
        this.kids = new Noeud[(9-this.turn)];
        this.max_prof = max_prof;

        if(this.previous_move == null){

            this.grid = new Case[3][3];
            for(int j = 0; j < 3 ; j++){
                for(int k = 0; k < 3 ; k++){
                    this.grid[j][k] = new Case(false, false);
                }
            }

            if(my_turn && turn == 2 && start_opponent_x != -1 && start_opponent_y != -1){ 
                this.grid[start_opponent_x][start_opponent_y].play(false);
            }

        }else{

            this.grid = new Case[3][3];
            for(int j = 0; j < 3 ; j++){
                for(int k = 0; k < 3 ; k++){
                    this.grid[j][k] = new Case(this.previous_move.getCase(j,k).getplayed1(),this.previous_move.getCase(j,k).getplayed1ByMe());
                }
            }

        }

        this.grid[x_move][y_move].play(my_turn); 

        updateScore(calculScore());

        if((this.score > -1000 && this.score < 1000) && this.max_prof > 1){
            create();
        }

    }

    public boolean getMyTurn(){
        return this.my_turn;
    }

    public Case getCase(int x, int y){
        return this.grid[x][y];
    }  

    public int getturn(){
        return this.turn;
    }

    public Noeud get(int x, int y){
        if(this.kids[0] != null){
            for(int i = 0 ; i < this.kids.length ; i++){
                if(this.kids[i].getX() == x && this.kids[i].getY() == y){
                    return this.kids[i];
                }
            }
        }
        return null;
    }

    public void setMaxProf(int max_prof){
        this.max_prof = max_prof;
    }

    public int getX(){
        return this.x_move;
    }

    public int getY(){
        return this.y_move;
    }

    public int getScore(){
        return this.score;
    }

    public void updatScore(int score){
        this.score = score + this.score;
        if(previous_move != null){
            previous_move.updateScore(this.score/2);
        }
    }

    public void create(){
        int i = 0;
        for(int j = 0; j < 3 ; j++){
            for(int k = 0; k < 3 ; k++){
                if(!this.grid[j][k].getplayed1()){
                    kids[i] = new Noeud(this,j,k,!my_turn,turn+1,max_prof-1,-1,-1);
                    i++;
                }

            }
        }

    }

    private int calculScore() { 
        int score = 0;

        for (int i = 0; i < 3; i++) {

		//-------------------------------------------------------------------------------------------------------------------------------
            if(grid[i][0].getplayed1() && grid[i][1].getplayed1() && grid[i][2].getplayed1()){ 

                if(grid[i][0].getplayed1ByMe() && grid[i][1].getplayed1ByMe() && grid[i][2].getplayed1ByMe()){
                    score += 10000;
                }else if(!grid[i][0].getplayed1ByMe() && !grid[i][1].getplayed1ByMe() && !grid[i][2].getplayed1ByMe()){
                    score += -10000;
                }

            }else if(grid[i][0].getplayed1() && grid[i][1].getplayed1() && !grid[i][2].getplayed1()){ 
                if(grid[i][0].getplayed1ByMe() && grid[i][1].getplayed1ByMe()){
                    score += 100;
                }else if(!grid[i][0].getplayed1ByMe() && !grid[i][1].getplayed1ByMe()){
                    score += -100;
                }
            }else if(grid[i][0].getplayed1() && grid[i][2].getplayed1() && !grid[i][1].getplayed1()){ 
                if(grid[i][0].getplayed1ByMe() && grid[i][2].getplayed1ByMe()){
                    score += 100;
                }else if(!grid[i][0].getplayed1ByMe() && !grid[i][2].getplayed1ByMe()){
                    score += -100;
                }
            }else if(grid[i][1].getplayed1() && grid[i][2].getplayed1() && !grid[i][0].getplayed1()){
                if(grid[i][1].getplayed1ByMe() && grid[i][2].getplayed1ByMe()){
                    score += 100;
                }else if(!grid[i][1].getplayed1ByMe() && !grid[i][2].getplayed1ByMe()){
                    score += -100;
                }
            }

		//-------------------------------------------------------------------------------------------------------------------------------
            if(grid[0][i].getplayed1() && grid[1][i].getplayed1() && grid[2][i].getplayed1()){

                if(grid[0][i].getplayed1ByMe() && grid[1][i].getplayed1ByMe() && grid[2][i].getplayed1ByMe()){
                    score += 10000;
                }else if(!grid[0][i].getplayed1ByMe() && !grid[1][i].getplayed1ByMe() && !grid[2][i].getplayed1ByMe()){
                    score += -10000;
                }

            }else if(grid[0][i].getplayed1() && grid[1][i].getplayed1() && !grid[2][i].getplayed1()){
                if(grid[0][i].getplayed1ByMe() && grid[1][i].getplayed1ByMe()){
                    score += 100;
                }else if(!grid[0][i].getplayed1ByMe() && !grid[1][i].getplayed1ByMe()){
                    score += -100;
                }
            }else if(grid[0][i].getplayed1() && grid[2][i].getplayed1() && !grid[1][i].getplayed1()){ 
                if(grid[0][i].getplayed1ByMe() && grid[2][i].getplayed1ByMe()){
                    score += 100;
                }else if(!grid[0][i].getplayed1ByMe() && !grid[2][i].getplayed1ByMe()){
                    score += -100;
                }
            }else if(grid[1][i].getplayed1() && grid[2][i].getplayed1() && !grid[0][i].getplayed1()){ 
                if(grid[1][i].getplayed1ByMe() && grid[2][i].getplayed1ByMe()){
                    score += 100;
                }else if(!grid[1][i].getplayed1ByMe() && !grid[2][i].getplayed1ByMe()){
                    score += -100;
                }
            }
        }

		//-------------------------------------------------------------------------------------------------------------------------------
        if(grid[0][0].getplayed1() && grid[1][1].getplayed1() && grid[2][2].getplayed1()){

            if (grid[0][0].getplayed1ByMe() && grid[1][1].getplayed1ByMe() && grid[2][2].getplayed1ByMe()) {
                score += 10000;
            }else if(!grid[0][0].getplayed1ByMe() && !grid[1][1].getplayed1ByMe() && !grid[2][2].getplayed1ByMe()){
                score += -10000;
            }

        }else if(grid[0][0].getplayed1() && grid[1][1].getplayed1() && !grid[2][2].getplayed1()){
            if (grid[0][0].getplayed1ByMe() && grid[1][1].getplayed1ByMe()) {
                score += 100;
            }else if(!grid[0][0].getplayed1ByMe() && !grid[1][1].getplayed1ByMe()){
                score += -100;
            }
        }else if(grid[0][0].getplayed1() && grid[2][2].getplayed1() && !grid[1][1].getplayed1()){
            if (grid[0][0].getplayed1ByMe() && grid[2][2].getplayed1ByMe()) {
                score += 100;
            }else if(!grid[0][0].getplayed1ByMe() && !grid[2][2].getplayed1ByMe()){
                score += -100;
            }
        }else if(grid[1][1].getplayed1() && grid[2][2].getplayed1() && !grid[0][0].getplayed1()){
            if (grid[1][1].getplayed1ByMe() && grid[2][2].getplayed1ByMe()) {
                score += 100;
            }else if(!grid[1][1].getplayed1ByMe() && !grid[2][2].getplayed1ByMe()){
                score += -100;
            }
        }

		//-------------------------------------------------------------------------------------------------------------------------------
        if(grid[2][0].getplayed1() && grid[1][1].getplayed1() && grid[0][2].getplayed1()){

            if (grid[2][0].getplayed1ByMe() && grid[1][1].getplayed1ByMe() && grid[0][2].getplayed1ByMe()) {
                score += 10000;
            }else if(!grid[2][0].getplayed1ByMe() && !grid[1][1].getplayed1ByMe() && !grid[0][2].getplayed1ByMe()){
                score += -10000;
            }

        }else if(grid[2][0].getplayed1() && grid[1][1].getplayed1() && !grid[0][2].getplayed1()){
            if (grid[2][0].getplayed1ByMe() && grid[1][1].getplayed1ByMe()) {
                score += 100;
            }else if(!grid[2][0].getplayed1ByMe() && !grid[1][1].getplayed1ByMe()){
                score += -100;
            }
        }else if(grid[2][0].getplayed1() && grid[0][2].getplayed1() && !grid[1][1].getplayed1()){
            if (grid[2][0].getplayed1ByMe() && grid[0][2].getplayed1ByMe()) {
                score += 100;
            }else if(!grid[2][0].getplayed1ByMe() && !grid[0][2].getplayed1ByMe()){
                score += -100;
            }
        }else if(grid[1][1].getplayed1() && grid[0][2].getplayed1() && !grid[2][0].getplayed1()){ 
            if (grid[1][1].getplayed1ByMe() && grid[0][2].getplayed1ByMe()) {
                score += 100;
            }else if(!grid[1][1].getplayed1ByMe() && !grid[0][2].getplayed1ByMe()){
                score += -100;
            }
        }

        return score;
    }
}