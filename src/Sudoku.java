/*This is the main class file
- This will contain the matrix and JFrame and perform all main operations


 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Sudoku extends JPanel implements ActionListener, KeyListener{
    private Timer solveSlowTimer = new Timer(1,this);//Delay for slow-solve
    private Timer sliderTimer = new Timer(1,this); //timer for reading slider values
    private Board board; //The object that controls the entire Grid
    JSlider slider = new JSlider(JSlider.VERTICAL, 1, 100, 1);//number slider
    private JLabel numbers = new JLabel(""); //JLabel that displays available numbers
    private String numbersText = "";
    private JLabel tries = new JLabel("Recursion Count"); //displays recursion count
    private int buttonX;
    private int buttonY;
    private int previous = 0;
    private boolean[] taken = new boolean[9];
    private JButton newBoard = new JButton("Create Board");
    private JButton genBoard = new JButton("Gen Board");
    private JButton solve = new JButton("Solve");
    private JButton solveSlowButton = new JButton("Solve Slow");
    private JButton pause = new JButton("Pause");
    private JButton[] nums = new JButton[9];
    private JButton clear = new JButton("Clear");
    private boolean createBoard;
    private boolean play;
    private ArrayList<JComponent> components = new ArrayList<JComponent>();
    private int count = 0;
    private int next = 0;
    private int ticker = 1000;

    public Sudoku() {
        setLayout(null);
        addKeyListener(this);
        setFocusable(true);

        setUpComponents(); //Sets up all components
        drawBorders(); //Sets up all of the borders around the boxes
    }

    public void setUpComponents(){
        board = new Board();
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        tries.setBounds(750,950,500,100); numbers.setBounds(10,10,400,100);
        newBoard.setBounds(10,10,300,100); genBoard.setBounds(10,120,300,100);
        solve.setBounds(1500,10,200,100); slider.setBounds(1500,500,100,500);
        solveSlowButton.setBounds(1500,120,200,100);
        clear.setBounds(10,780,200,100); pause.setBounds(1500,230,200,100);

        clear.addActionListener(this); solve.addActionListener(this); solveSlowButton.addActionListener(this);
        newBoard.addActionListener(this); genBoard.addActionListener(this); pause.addActionListener(this);

        components.add(tries); components.add(numbers); components.add(solve);
        components.add(newBoard); components.add(genBoard); components.add(slider);
        components.add(clear);components.add(solveSlowButton); components.add(pause);


        for (JComponent component : components) {
            component.setVisible(true);
            add(component);
            component.setFont(new Font("Comic Sans", Font.BOLD, 30));
        }

        numbers.setVisible(false);
        pause.setVisible(false);
        slider.setVisible(false);

        for(int i = 0; i < 9; i++){
            nums[i] = new JButton(""+(i+1));
            nums[i].setFont(new Font("Comic Sans", Font.BOLD, 50));
            nums[i].setVisible(true);
            nums[i].setBounds(10+(i%3)*150,300+((i/3)*150),150,150);
            add(nums[i]);
            nums[i].addActionListener(this);
        }
    }

    public void drawBorders(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                if(j%3 == 0){
                    if(i == 8)board.buttons[i][j].setBorder(BorderFactory.createMatteBorder(4, 1, 1, 4, Color.BLACK));
                    else if(i%3 == 0)board.buttons[i][j].setBorder(BorderFactory.createMatteBorder(4, 4, 1, 1, Color.BLACK));
                    else board.buttons[i][j].setBorder(BorderFactory.createMatteBorder(4, 1, 1, 1, Color.BLACK));
                }
                else if(j == 8){
                    if(i == 8)board.buttons[i][j].setBorder(BorderFactory.createMatteBorder(1, 1, 4, 4, Color.BLACK));
                    else if(i%3 == 0)board.buttons[i][j].setBorder(BorderFactory.createMatteBorder(1, 4, 4, 1, Color.BLACK));
                    else board.buttons[i][j].setBorder(BorderFactory.createMatteBorder(1, 1, 4, 1, Color.BLACK));
                }
                else{
                    if(i == 8)board.buttons[i][j].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 4, Color.BLACK));
                    else if(i%3 == 0)board.buttons[i][j].setBorder(BorderFactory.createMatteBorder(1, 4, 1, 1, Color.BLACK));
                    else board.buttons[i][j].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
                }
                add(board.buttons[i][j]);
                board.buttons[i][j].addActionListener(this);
            }
        }
    }

    public void keyTyped(KeyEvent e) {
        //if(createBoard&&look) {
        if (e.getKeyCode() == KeyEvent.VK_NUMPAD1) {
            board.points.add(new int[]{buttonX, buttonY, 1});
            board.buttons[buttonY][buttonX].setText("" + 1);

        }
        if (e.getKeyCode() == KeyEvent.VK_NUMPAD2) {
            board.points.add(new int[]{buttonX, buttonY, 2});
            board.buttons[buttonY][buttonX].setText("" + 2);

        }
        if (e.getKeyCode() == KeyEvent.VK_NUMPAD3) {
            board.points.add(new int[]{buttonX, buttonY, 3});
            board.buttons[buttonY][buttonX].setText("" + 3);

        }
        if (e.getKeyCode() == KeyEvent.VK_NUMPAD4) {
            board.points.add(new int[]{buttonX, buttonY, 4});
            board.buttons[buttonY][buttonX].setText("" + 4);

        }
        if (e.getKeyCode() == KeyEvent.VK_NUMPAD5) {
            board.points.add(new int[]{buttonX, buttonY, 5});
            board.buttons[buttonY][buttonX].setText("" + 5);

        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            board.points.add(new int[]{buttonX, buttonY, 6});
            board.buttons[buttonY][buttonX].setText("" + 6);
            System.out.println("here");
        }
        if (e.getKeyCode() == KeyEvent.VK_NUMPAD7) {
            board.points.add(new int[]{buttonX, buttonY, 7});
            board.buttons[buttonY][buttonX].setText("" + 7);

        }
        if (e.getKeyCode() == KeyEvent.VK_9) {
            System.out.println("here");
            board.points.add(new int[]{buttonX, buttonY, 8});
            board.buttons[buttonY][buttonX].setText("" + 8);

        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            System.out.println("here");
            board.clearSelected();
            buttonY -=1;
            board.selected[buttonY][buttonX] = true;
            board.buttons[buttonY][buttonX].setBackground(Color.CYAN);
            //if(slider.getValue() !=9)
            //slider.setValue(slider.getValue() + 1);
        }
    }

    public void keyPressed(KeyEvent e){
        if (e.getKeyCode() == KeyEvent.VK_S) {
            System.out.println("HHEERE");
            System.out.println(board.solveBruteForce(0,0));
        }
        if (e.getKeyCode() == KeyEvent.VK_T) {
            System.out.println(board.calculateAvailable(buttonY,buttonX));
        }
        if(createBoard) {
            if (e.getKeyCode() == KeyEvent.VK_NUMPAD1) {
                board.points.add(new int[]{buttonX, buttonY, 1});
                board.buttons[buttonY][buttonX].setText("" + 1);

            }
            if (e.getKeyCode() == KeyEvent.VK_NUMPAD2) {
                board.points.add(new int[]{buttonX, buttonY, 2});
                board.buttons[buttonY][buttonX].setText("" + 2);

            }
            if (e.getKeyCode() == KeyEvent.VK_NUMPAD3) {
                board.points.add(new int[]{buttonX, buttonY, 3});
                board.buttons[buttonY][buttonX].setText("" + 3);

            }
            if (e.getKeyCode() == KeyEvent.VK_NUMPAD4) {
                board.points.add(new int[]{buttonX, buttonY, 4});
                board.buttons[buttonY][buttonX].setText("" + 4);

            }
            if (e.getKeyCode() == KeyEvent.VK_NUMPAD5) {
                board.points.add(new int[]{buttonX, buttonY, 5});
                board.buttons[buttonY][buttonX].setText("" + 5);

            }
            if (e.getKeyCode() == KeyEvent.VK_NUMPAD6) {
                board.points.add(new int[]{buttonX, buttonY, 6});
                board.buttons[buttonY][buttonX].setText("" + 6);

                System.out.println("here");
            }
            if (e.getKeyCode() == KeyEvent.VK_NUMPAD7) {
                board.points.add(new int[]{buttonX, buttonY, 7});
                board.buttons[buttonY][buttonX].setText("" + 7);

            }
            if (e.getKeyCode() == KeyEvent.VK_NUMPAD8) {
                board.points.add(new int[]{buttonX, buttonY, 8});
                board.buttons[buttonY][buttonX].setText("" + 8);
            }
            if (e.getKeyCode() == KeyEvent.VK_NUMPAD9) {
                board.points.add(new int[]{buttonX, buttonY, 9});
                board.buttons[buttonY][buttonX].setText("" + 9);

            }
        }
    }
    public void keyReleased(KeyEvent e) { }

    public void actionPerformed(ActionEvent e) {

        if(e.getSource().equals(newBoard)) {
            if(!createBoard) {
                createBoard = true;
                newBoard.setText("Done");
                remove(genBoard);
                genBoard.setVisible(false);
            }
            else if(createBoard){
                for(int i= 0; i < 9;i++){
                    for(int j = 0; j < 9; j++)
                        board.buttons[i][j].setText("");
                }
                createBoard = false;
                board.addPoints();
                remove(newBoard);
                play = true;
            }
        }
        if(e.getSource().equals(genBoard)){
           //board.boardNumber = (int)(Math.random()*4)+1;
            board.boardNumber = 4;
            board.addPoints();
            //board.generateBoard();
            play=true;
            remove(genBoard);
            remove(newBoard);
        }

        if(e.getSource().equals(solve)){
            solveSlowTimer.stop();
            board.clearBoard();
            for(int i = 0; i < 9; i++){
                taken[i] = false;
            }
            System.out.println(board.solveBruteForce(0,0));
            //while((board.solveLogicRow()||board.solveLogicGrid()) && !board.fullBoard()){
              //  System.out.println("hehe");
            //}
            //board.generateBoard();
            //if(board.solveBruteForce(0,0)) System.out.println("Needed Brute Force");
            if(!board.fullBoard()){
                //if(board.solveBruteForce(0,0)) System.out.println("Needed Brute Force");
                tries.setText("Guesses: " + board.tryC);
            }
            else {
                tries.setText("Only Used Logic");
            }


        }

        if(e.getSource().equals(solveSlowButton)){
            solve.setText("Finish");
           // while((board.solveLogicRow()||board.solveLogicGrid()) && !board.fullBoard()){
              //  System.out.println("hehe");
            //}
            if(!board.fullBoard()){
                //board.changeEditable();
                if(board.solveBruteForce(0,0)) System.out.println("Needed Brute Force");
                tries.setText("Guesses: " + board.tryC);
            }
            else {
                tries.setText("Only Used Logic");
            }
            board.clearBoard();
            pause.setVisible(true);
            solveSlowTimer.start();
        }

        if(e.getSource().equals(solveSlowTimer)){

            count++;
            if(count%ticker == 0 && next < board.solution.size()){
                board.editBox(board.solution.get(next)[0],board.solution.get(next)[1],board.solution.get(next)[2]);
                next++;
            }
            if(next == board.solution.size()) solveSlowTimer.stop();
        }

        if(e.getSource().equals(sliderTimer)){
            ticker = 100/slider.getValue();
        }

        if(e.getSource().equals(pause)){
            if(pause.getText().equals("Pause")){
                pause.setText("Continue");
                sliderTimer.start();
                slider.setVisible(true);
                solveSlowTimer.stop();
            }
            else{
                pause.setText("Pause");
                solveSlowTimer.start();
                sliderTimer.stop();
                slider.setVisible(false);
            }
        }

        if(createBoard){
            if(e.getSource().equals(clear)){
                board.points.remove(board.points.size()-1);
                board.buttons[buttonY][buttonX].setText("");
            }
            for(int i = 0; i < 9; i++)
                if(e.getSource().equals(nums[i])) {
                    board.points.add(new int[]{buttonX, buttonY, (i+1)});
                    board.buttons[buttonY][buttonX].setText("" + (i+1));
                }
        }
        if(play){
            for(int i = 0; i < 9; i++)
                if(e.getSource().equals(nums[i])) {
                    if(taken[i] && board.editable[buttonY][buttonX])
                        board.editBox(buttonY, buttonX, i+1);
                }
            if(e.getSource().equals(clear)){
                if(board.values[buttonY][buttonX] != 0 && board.editable[buttonY][buttonX]) board.editBox(buttonY,buttonX,0);
            }
        }

        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                if(e.getSource().equals(board.buttons[i][j])) {
                    if(createBoard){
                        board.clearSelected();
                        buttonY = i;
                        buttonX = j;
                        board.selected[i][j] = true;
                        board.buttons[i][j].setBackground(Color.CYAN);
                        System.out.println("Looking");

                    }
                    if(play) {
                        //board.buttons[i][j].setVisible(false);
                        taken = board.calculateAvailable(i, j);
                        numbersText = "";
                        for (int n = 0; n < 9; n++)
                            if (taken[n]) {
                                numbersText = numbersText + "  " + (n + 1);
                            }
                        numbersText = numbersText + "" + board.editable[i][j] + board.calculateAvailableNum(i, j);
                        numbers.setText(numbersText);
                        board.clearSelected();
                        board.selected[i][j] = true;
                        board.buttons[i][j].setBackground(Color.CYAN);
                        buttonY = i;
                        buttonX = j;
                        previous = board.values[buttonY][buttonX];
                        System.out.println(board.calculateAvailableNum(buttonY, buttonX));
                    }
                }
        }

    public static void main(String[] args) {
        JFrame w = new JFrame("Sudoku");
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //w.setSize(1200,1200);
        w.setExtendedState(w.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        w.getContentPane().add(new Sudoku());
        w.setVisible(true);
    }

}