/*This class will contain the object for each sudoku square
- will have an invisible Jbutton to know which box is selected
- will have an int from 1-9 to record value
- will have a JLabel to record the value in Gui
 */

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class Board {

    int n = 0;
    int[][] values; //Holds the integer values for all of the boxes
    boolean[][] editable;
    JButton[][] buttons; //matrix of all the buttons in each box
    boolean[][] selected; //boolean values for every box to see if the box is selected
     // 3D array for all of the taken values for every box: A taken value means that the box can't hold that value.
    ArrayList<int[]> points = new ArrayList<int[]>();
    int[][] index;
    boolean[][] tried;
    int tryCounter = 0;
    int skipCounter = 1;
    int boardNumber = 0;
    int tryC = 0;
    int skip = 0;
    int useX;
    int useY;
    boolean solveFast = false;
    //Timer t = new Timer();
    ArrayList<Integer[]> solution = new ArrayList<Integer[]>();

    public Board(){
        //initializes all the variables in the board.
        values = new int[9][9];
        buttons = new JButton[9][9];
        selected = new boolean[9][9];
        index = new int[9][9];
        editable = new boolean[9][9];
        tried = new boolean[9][9];


        //Traverses all the buttons and sets them up
        for(int i = 0; i < 9; i++){
            for(int j = 0; j <9; j++){
                    editable[i][j] = true;
                    buttons[i][j] = new JButton("");
                    buttons[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                    buttons[i][j].setBounds(500 + i * 100, 50 + j * 100, 100, 100);
                    buttons[i][j].setFont(new Font("Comic Sans", Font.PLAIN, 35));
                    buttons[i][j].setBackground(Color.WHITE);
                    buttons[i][j].setForeground(Color.BLUE);
            }
        }

    }

    public boolean solveBruteForce(int y, int x){
        /* This is the solving method. It recursively solves itself
        *  It uses a strategy called backtracing
        *
         */
        //System.out.println("" + y + "," + x);
        n = 0;
        tryCounter++; //counts how many recursions it took
        skipCounter = 1; //resets skip counter;
        if(x==-1 && y == 0) return false;
        if(x == 9) return solveBruteForce(y+1, 0);//if reached the end of a row, move to next row
        if(x <= -1) return solveBruteForce(y-1,9+x);//returning to previous value from the beginning of a row
        if(y == 9) {
            tryC = tryCounter;
            return true; //If all rows are filled then return true.
        }
        if(!editable[y][x]) return solveBruteForce(y,x+1); //skips over non-editable items

        //calculates skips needed(reverses to next editable box)
        if(x!= 0){
            for(int i = 1; i < x+1; i++){
                if(!editable[y][x-i]) skipCounter++;
                else break;
            }
            if(x-skipCounter < 0)
                if(y>0) for(int i = 0; i < 5; i++) {
                    if (!editable[y - 1][8 - i]) skipCounter++;
                    else break;
                }
        }
        if(x==0){
            if(y>0)
                for(int i = 0; i < 5; i++){
                    if(!editable[y-1][8-i]) skipCounter++;
                    else break;
                }
        }
        //System.out.println("try:"+tryCounter+" point:" + y+"," + x + " Index: " + index[y][x]+ " Value: " + values[y][x] + " " + calculateAvailableNum(y,x));

        //If all values have been tried for this box then go back to previous box and try another value!
        int num = calculateAvailableNum(y,x);

        //If already tried all of the values for that spot then delete it and go back
        if(index[y][x] >= num){
            index[y][x] = 0;
            editBox(y,x,0);
            solution.add(new Integer[]{y,x,0});
            return solveBruteForce(y,x-skipCounter);
        }

        //takes guess on proper box
       // findNextEmpty(y,x); doesnt seem to do anything
        editBox(y,x,createAvailableNumArray(y,x)[index[y][x]]+1);
        solution.add(new Integer[]{y,x,createAvailableNumArray(y,x)[index[y][x]]+1});
        index[y][x]++;//notes that it just guessed the next value so next time it will guess a different value.
        if(x<=7 && !editable[y][x+1]) return(solveBruteForce(y,x+2));
        //wait(100);
        return(solveBruteForce(y,x+1));//moves to next box
    }

    public boolean solveLogicGrid(){
        /* THIS METHOD WILL SOLVE THE BOARD BY ELIMINATING USING LOGIC
        *  Step one: select a number 1-9
        *  Look at each grid(3 by 3 box)
        * (For Each Number For Each Grid):
        *       Count how many places in that grid you could place the number.
        *          - If there is only one place it can go in the gridthen place, else move on
        *  These operations will run up to ten times
         */
        int positionsAvailable= 0;
        int placeX = 0;
        boolean productive = true;
        boolean doesSomething = false;
        int placeY = 0;
        int run = 0;
        while(!fullBoard()&&productive) {
            productive = false;
            for (int num = 1; num < 10; num++) //The number you are looking to place
                for (int gridX = 0; gridX < 3; gridX++) //These two for loops traevrse through the different grids in the board
                    for (int gridY = 0; gridY < 3; gridY++) {
                        positionsAvailable = 0; //Count of how many places in a grid can you place a number: ex in grid(0,0) you can place a 1 in 4 positions
                        placeY = 0;
                        placeX = 0;
                        if (!inGrid(num, gridX, gridY)) {
                            //If the number isn't in the grid then there is a potential of placing so search, else just move on
                            for (int y = gridY * 3; y < gridY * 3 + 3; y++) //Traverses through the different boxes in a 3:3 grid
                                for (int x = gridX * 3; x < gridX * 3 + 3; x++) {
                                    if (values[y][x] == 0 && calculateAvailable(y, x)[num - 1]) {
                                        //If the spot is empty and you can place the num then add one to positions available and track the X and Y
                                        positionsAvailable++;
                                        placeY = y;
                                        placeX = x;
                                    }
                                }
                            if (positionsAvailable == 1) {
                                editBox(placeY, placeX, num);
                                solution.add(new Integer[]{placeY,placeX,num});
                                productive = true;
                                doesSomething = true;
                            }
                            //If positionsAvailable is one then there is only one possible place that num could go so put that value in!
                        }
                    }
            run++;
            System.out.println(run);
        }
        return doesSomething;
    }

    public boolean solveLogicRow(){
        /* THIS METHOD WILL SOLVE THE BOARD BY ELIMINATING USING LOGIC
         *  Step one: select a number 1-9
         *  Look at each Row and Column
         * (For Each Number For Each Row/Column):
         *       Count how many places in that grid you could place the number.
         *          - If there is only one place it can go in the  Row/Column then place, else move on
         *  These operations will run up to ten times
         */

        int positionsAvailable= 0;
        int placeX = 0;
        boolean productive = true;
        int placeY = 0;
        int run = 0;
        boolean doesSomething = false;
        while(!fullBoard()&&productive) {
            productive = false;
            for (int num = 1; num < 10; num++) { //The number you are looking to place
                for (int col = 0; col < 9; col++) { //These two for loops traevrse through the different grids in the board
                    positionsAvailable = 0; //Count of how many places in a grid can you place a number: ex in grid(0,0) you can place a 1 in 4 positions
                    placeX = 0;
                    if (!inRow(num, col)) {
                        //If the number isn't in the grid then there is a potential of placing so search, else just move on
                        //Traverses through the different boxes in a 3:3 grid
                        for (int x = 0; x < 9; x++) {
                            if (values[col][x] == 0 && calculateAvailable(col, x)[num - 1]) {
                                //if(row == 0) System.out.println(values[row][x]);
                                //If the spot is empty and you can place the num then add one to positions available and track the X and Y
                                positionsAvailable++;
                                placeX = x;
                            }
                        }
                        if (positionsAvailable == 1) {
                            editBox(col, placeX, num);
                            solution.add(new Integer[]{col,placeX,num});
                            productive = true;
                            doesSomething = true;
                        }
                        //If positionsAvailable is one then there is only one possible place that num could go so put that value in!
                    }
                }

                for (int row = 0; row < 9; row++) { //These two for loops traevrse through the different grids in the board
                    positionsAvailable = 0; //Count of how many places in a grid can you place a number: ex in grid(0,0) you can place a 1 in 4 positions
                    placeY = 0;
                    if (!inColumn(num, row)) {
                        //If the number isn't in the grid then there is a potential of placing so search, else just move on
                        //Traverses through the different boxes in a 3:3 grid
                        for (int y = 0; y < 9; y++) {
                            if (values[y][row] == 0 && calculateAvailable(y, row)[num - 1]) {
                                //if(row == 0) System.out.println(values[row][x]);
                                //If the spot is empty and you can place the num then add one to positions available and track the X and Y
                                positionsAvailable++;
                                placeY = y;
                            }
                        }
                        if (positionsAvailable == 1) {
                            editBox(placeY, row, num);
                            solution.add(new Integer[]{placeY,row,num});
                            productive = true;
                            doesSomething = true;
                        }
                    }
                    run++;
                    System.out.println(run);
                }
            }
        }
        return doesSomething;

    }
    //finds if a number is in a grid
    public boolean inGrid(int num, int numX, int numY){
        for(int y = numY*3; y < numY*3 + 3; y++) {
            for (int x = numX * 3; x < numX * 3 + 3; x++) {
                if(num == values[y][x]) return true;
            }
        }
        return false;
    }

    public boolean inColumn(int num, int col){
        for(int row = 0; row < 9; row++){
            if(num == values[row][col]) return true;
        }
        return false;
    }

    public boolean inRow(int num, int col){
        for(int row = 0; row < 9; row++){
            if(num == values[col][row]) return true;
        }
        return false;
    }

    public void changeEditable(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                if(editable[i][j] && values[i][j] != 0){
                    editable[i][j] = false;
                }
            }
        }
    }

    public boolean fullBoard(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                if(values[i][j] == 0) return false;
            }
        }
        return true;
    }
    //changes the value for a box
    public void editBox(int y, int x, int value){
            values[y][x] = value;
            if(value !=0) buttons[y][x].setText("" + value);
            else buttons[y][x].setText("");
    }


    //Adds all of the points on the board.
    public void addPoints(){
        if(boardNumber == 1) {
            points.add(new int[]{0, 3, 3});
            points.add(new int[]{2, 3, 7});
            points.add(new int[]{4, 3, 1});
            points.add(new int[]{0, 0, 9});
            points.add(new int[]{0, 1, 2});
            points.add(new int[]{0, 4, 5});
            points.add(new int[]{0, 6, 8});
            points.add(new int[]{1, 0, 4});
            points.add(new int[]{1, 4, 1});
            points.add(new int[]{1, 6, 2});
            points.add(new int[]{2, 7, 4});
            points.add(new int[]{3, 0, 6});
            points.add(new int[]{3, 2, 5});
            points.add(new int[]{3, 5, 7});
            points.add(new int[]{3, 6, 1});
            points.add(new int[]{4, 1, 3});
            points.add(new int[]{4, 2, 4});
            points.add(new int[]{4, 4, 2});
            points.add(new int[]{4, 5, 6});
            points.add(new int[]{5, 6, 7});
            points.add(new int[]{6, 0, 3});
            points.add(new int[]{6, 2, 2});
            points.add(new int[]{6, 4, 7});
            points.add(new int[]{6, 7, 1});
            points.add(new int[]{6, 8, 5});
            points.add(new int[]{7, 0, 1});
            points.add(new int[]{7, 4, 4});
            points.add(new int[]{7, 8, 3});
            points.add(new int[]{8, 1, 6});
            points.add(new int[]{8, 7, 2});
        }
        if(boardNumber == 2){
            points.add(new int[]{0, 0, 7});
            points.add(new int[]{0, 2, 4});
            points.add(new int[]{0, 6, 6});
            points.add(new int[]{1, 0, 6});
            points.add(new int[]{1, 4, 9});
            points.add(new int[]{1, 5, 8});
            points.add(new int[]{2, 6, 2});
            points.add(new int[]{2, 8, 9});
            points.add(new int[]{3, 0, 1});
            points.add(new int[]{3, 3, 4});
            points.add(new int[]{3, 4, 2});
            points.add(new int[]{3, 6, 5});
            points.add(new int[]{3, 8, 8});
            points.add(new int[]{4, 1, 7});
            points.add(new int[]{4, 7, 4});
            points.add(new int[]{4, 8, 2});
            points.add(new int[]{5, 2, 2});
            points.add(new int[]{5, 3, 6});
            points.add(new int[]{5, 6, 1});
            points.add(new int[]{6, 0, 4});
            points.add(new int[]{6, 3, 8});
            points.add(new int[]{6, 5, 1});
            points.add(new int[]{6, 7, 2});
            points.add(new int[]{7, 0, 2});
            points.add(new int[]{7, 1, 6});
            points.add(new int[]{7, 3, 3});
            points.add(new int[]{7, 4, 4});
            points.add(new int[]{7, 5, 9});
            points.add(new int[]{7, 7, 5});
            points.add(new int[]{8, 7, 7});
        }
        if(boardNumber == 3){
            points.add(new int[]{0, 0, 3});
            points.add(new int[]{0, 1, 8});
            points.add(new int[]{0, 2, 9});
            points.add(new int[]{0, 3, 1});
            points.add(new int[]{0, 8, 2});
            points.add(new int[]{1, 7, 7});
            points.add(new int[]{2, 1, 7});
            points.add(new int[]{2, 2, 2});
            points.add(new int[]{2, 6, 1});
            points.add(new int[]{2, 8, 8});
            points.add(new int[]{3, 0, 4});
            points.add(new int[]{3, 4, 6});
            points.add(new int[]{3, 5, 2});
            points.add(new int[]{3, 6, 7});
            points.add(new int[]{3, 8, 3});
            points.add(new int[]{5, 0, 9});
            points.add(new int[]{5, 2, 7});
            points.add(new int[]{5, 3, 3});
            points.add(new int[]{5, 4, 5});
            points.add(new int[]{5, 8, 4});
            points.add(new int[]{6, 0, 5});
            points.add(new int[]{6, 2, 4});
            points.add(new int[]{6, 6, 9});
            points.add(new int[]{6, 7, 2});
            points.add(new int[]{7, 1, 1});
            points.add(new int[]{8, 0, 2});
            points.add(new int[]{8, 5, 1});
            points.add(new int[]{8, 6, 8});
            points.add(new int[]{8, 7, 5});
            points.add(new int[]{8, 8, 7});
        }
        if(boardNumber == 4){
            points.add(new int[]{0, 0, 6});
            points.add(new int[]{0, 3, 9});
            points.add(new int[]{0, 6, 1});
            points.add(new int[]{0, 7, 8});
            points.add(new int[]{1, 7, 9});
            points.add(new int[]{1, 8, 4});
            points.add(new int[]{2, 0, 2});
            points.add(new int[]{2, 5, 1});
            points.add(new int[]{2, 7, 7});
            points.add(new int[]{3, 0, 8});
            points.add(new int[]{3, 2, 5});
            points.add(new int[]{3, 3, 1});
            points.add(new int[]{4, 3, 6});
            points.add(new int[]{4, 5, 5});
            points.add(new int[]{5, 5, 2});
            points.add(new int[]{5, 6, 6});
            points.add(new int[]{5, 8, 9});
            points.add(new int[]{6, 1, 9});
            points.add(new int[]{6, 3, 2});
            points.add(new int[]{6, 8, 7});
            points.add(new int[]{7, 0, 7});
            points.add(new int[]{7, 1, 4});
            points.add(new int[]{8, 1, 3});
            points.add(new int[]{8, 2, 6});
            points.add(new int[]{8, 5, 7});
            points.add(new int[]{8, 8, 1});


        }
        for(int i = 0; i < points.size(); i++){
            editBox(points.get(i)[1],points.get(i)[0],points.get(i)[2]);
            editable[points.get(i)[1]][points.get(i)[0]] = false;
            buttons[points.get(i)[1]][points.get(i)[0]].setFont(new Font("Comic Sans", Font.BOLD, 35));
            buttons[points.get(i)[1]][points.get(i)[0]].setForeground(Color.BLACK);
        }
    }

    //Clears selected button
    public void clearSelected(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                selected[i][j] = false;
                buttons[i][j].setBackground(Color.WHITE);
            }
        }
    }


    //this method will create a boolean array and set true to all the values that cant be in the spot
    public boolean[] calculateAvailable(int y, int x){
        boolean[] available = new boolean[9];
        for(int i = 0; i < 9; i++) available[i] = true;
        for(int i = 0; i < 9; i++) if(i != y && values[i][x] != 0) available[values[i][x]-1] = false;
        for(int j = 0; j < 9; j++) if(j != x && values[y][j] !=0) available[values[y][j]-1] = false;
        int boxX = x/3;
        int boxY = y/3;
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                if(i+3*boxY != y && j+3*boxX != x && values[i+3*boxY][j+3*boxX] != 0) available[values[i+3*boxY][j+3*boxX] -1] = false;
        return available;
    }

    //Calculates the amount of available numbers
    public int calculateAvailableNum(int y, int x){
        boolean[] takens = calculateAvailable(y,x);
        int count = 0;
        for(int i = 0; i < 9; i++) if(takens[i]) count++;
        return count;
    }

    //creates an array of all the available numbers
    public int[] createAvailableNumArray(int y,int x){
        boolean[] boolArr = calculateAvailable(y,x);
        int number = calculateAvailableNum(y,x);
        int[] answer = new int[number];
        int counter = 0;
        for(int i = 0; i < 9; i++)
            if(boolArr[i]){
                answer[counter] = i;
                counter++;
            }
        return answer;
    }

    //Finds next empty box
    public void findNextEmpty(int y, int x){
        useX = x+1;
        useY = y;
        while(useX < 8){
            if(!editable[useY][useX]) useX++;
            else break;
        }
        if(useX == 8){
            if(!editable[useY][useX]){
                useY++;
                useX = 0;
                findNextEmpty(useY,useX);
            }
        }
    }


    public void clearBoard(){ //Removes all values that are editable
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++)
                if(editable[i][j]) editBox(i,j,0);
        }
    }


    public void generateBoard(){
        /* THIS METHOD WILL GENERATE A RANDOM BOARD WITH ONE UNIQUE SOLUTION
           -----------------------------------------------------------------
        * Step 1: randomly fill in around 30 boxes
        * Step 2: Solve the board!
        * Step 3: Remove a number from the board
        * Step 4: Check to make sure there is still one solution by solving from both sides
        * Step 5: Repeat steps 3 and 4 until there are X numbers still filled in
           -----------------------------------------------------------------
         */
        int size = 81;
        //STEP 1: Randomly fill in the board

        for(int i = 0; i < 9; i++){
            editBox(0,i,createAvailableNumArray(0,i)[(int)(Math.random()*calculateAvailableNum(0,i))]+1); //edits the box at y,x with the value using one of the available values
            editable[0][i] = false;
        }
        for(int i = 0; i < 3; i++){
            editBox(1,i,createAvailableNumArray(1,i)[(int)(Math.random()*calculateAvailableNum(1,i))]+1); //edits the box at y,x with the value using one of the available values
            editable[1][1] = false;
        }

        //STEP 2: Solve the board
        if(solveBruteForce(0,0)) System.out.println("solved");
        for (int i = 0; i < 15; i++) {
            int randX = (int) (Math.random() * 9);
            int randY = (int) (Math.random() * 9);
            if (values[randX][randY] != 0) {
                editBox(randY, randX, 0); //edits the box at y,x with the value using one of the available values
                editable[randY][randX] = true;
            }
        }
        while(size > 35) {
           int rx = 0;
           int ry = 0;
           int v;
           int x = (int)(Math.random()*9);
           int y = (int)(Math.random()*9);
           if(values[y][x] != 0) {
               rx = x;
               ry = y;
               v = values[y][x];
               editBox(ry, rx, 0);
               editable[ry][rx] = true;
               if (solveBruteForce(0, 0)) {
                   size--;
                   System.out.println("size: " + size);
                   clearBoard();
               }
               else {
                    editBox(y,x,v);
                   editable[ry][rx] = false;
               }
           }
        }




        //STEP 3,4,5
        //while()
    }


}