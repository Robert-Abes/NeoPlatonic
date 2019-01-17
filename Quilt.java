// The game Quilt and this implementation code (c) Robert Abes, January 2019
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

/*          SCHEMATIC X & Y COORDINATES, CELLS AND CELL NUMBERS
                                 1  1  1  1  1  1  1  1  1  1  2  2  2  2  2  2  2
   0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6
 0                      :                       :                       :
                     :     :                 :     :                 :     :
 1    :           :     18    :           :     19    :          :      20    :
     :   :     :   :         :   :     :   :         :   :     :   :         :
 2  :       :       :       :       :       :       :       :       :       :
 3 :   0    :   36   : : : :    1   :    37  : : : :    2   :   38   : : : :
 4  :       :       :       :       :       :       :       :       :       :
     :   :     :   :    54   :   :     :   :   55    :   :     :   :         :
 5    :     21    :           :     22    :           :    23     :     56    :
       :         :   :     :   :         :   :     :   :         :   :     :   :
 6      :       :       :       :       :       :       :       :       :       :
 7       : : : :    3   :   39   : : : :   4    :   40   : : : :    5   :   41   :
 8      :       :       :       :       :       :       :       :       :       :
       :         :   :     :   :         :   :     :   :         :   :     :   :
 9    :    57     :    24     :     58    :     25    :     59    :     26    :
     :   :     :   :         :   :     :   :         :   :     :   :         :
10  :       :       :       :       :       :       :       :       :       :
11 :  6     :   42   : : : :    7   :   43   : : : :   8    :   44   : : : :     
12  :       :       :       :       :       :       :       :       :       :
     :   :     :   :         :   :     :   :         :   :     :   :         :
13    :    27     :    60     :     26    :     61    :     29    :     62    :
       :         :   :     :   :         :   :     :   :         :   :     :   :
14      :       :       :       :       :       :       :       :       :       :
15       : : : :    9   :   45   : : : :   10   :   46   : : : :   11   :   41   :
16      :       :       :       :       :       :       :       :       :       :
       :         :   :    :    :         :   :     :   :         :   :     :   : 
17    :    63     :           :     64    :           :     65    :           :
     :   :     :   :   30    :   :     :   :    31   :   :     :   :    32   :
18  :       :       :       :       :       :       :       :       :       :
19 :   12   :   48   : : : :   13   :   49   : : : :   14   :   50   : : : :
20  :       :       :       :       :       :       :       :       :       :
     :   :     :   :         :   :     :   :         :   :     :   :         :
21    :     33    :     66    :     34    :     67    :     35    :     68    :
       :         :   :     :   :         :   :     :   :         :   :     :   :
22      :       :       :       :       :       :       :       :       :       :
23       : : : :   15   :   51   : : : :   16   :    52  : : : :   17   :   53   :
24      :       :       :       :       :       :       :       :       :       :
       :         :   :     :   :         :   :     :   :         :   :     :   :
25    :     69    :           :     70    :           :     71    :           :
         :     :                 :     :                 :     :
26          :                       :                       :                    */

class Quilt extends JFrame implements MouseListener, Runnable 
{ public void run() { /* Timer */ }
  static char STATE;      // 'I' ---> Initial state
                          // 'B' ---> Before/Between games
                          // 'C' ---> Choose Colors
                          // 'Q' ---> Fix the quilt
                          // 'P' ---> Pause
                          // '1' ---> Instruction screen 1
                          // '2' ---> Instruction screen 2
             	          // '3' ---> Instruction screen 3
             	          // '4' ---> Instruction screen 4
  static boolean over;    // true ---> quilt fixed
  static boolean dots;    // true ---> circle all borderline swaps
  boolean tick;           // true ---> clock() maintains TUSF
  long TQFS;              // Time quilt fixing started (ms)
  static long TUSF;       // Time used so far (ms)
  static int SUSF;        // Swaps used so far
  float X, Y;             // User click coordinates
  static int[] Z = new int[3];        // Color numbers
  static int[] array = new int[72];   // Quilt pattern array of color numbers
  // Lists of cell numbers that all have the same color if a quilt is fixed
  static int[] L1 = { 2, 5, 6, 9,13,16,20,21,24,28,31,35,  // List 1 part 1
	             40,44,47,48,51,54,37,58,61,65,68,69};  
  static int[] L2 = { 0, 3, 7,10,14,17,18,22,25,29,32,33,  // List 1 part 2
	             38,41,42,45,49,52,55,59,62,63,66,70};
  static int[] L3 = { 0, 5, 8,10,13,15,19,22,24,27,32,35,  // List 2 part 1
	             38,40,43,45,48,53,54,57,62,65,67,70};
  static int[] L4 = { 1, 3, 6,11,14,16,20,23,25,28,30,33,  // List 2 part 2
	             36,41,44,46,49,51,55,58,60,63,68,71};

  public static void main(String[] args)
  { Quilt whatever = new Quilt();}     // Load main class

  Quilt()  // Main class constructor
  { super ("QUILT -- A Colorful Pastime");
    addMouseListener(this);
    setSize(1030, 690);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    QuiltG rug = new QuiltG();  // Load graphics subclass
    add(rug);  STATE = 'I';  prep();
    Thread timer = new Thread (this);
    timer.start();  setVisible(true);
    clock();}  // Never returns; must be called after setVisible(true)

  public void mouseEntered  (MouseEvent event) { /* Ignore */ }
  public void mouseExited   (MouseEvent event) { /* Ignore */ }
  public void mousePressed  (MouseEvent event) { /* Ignore */ }
  public void mouseReleased (MouseEvent event) { /* Ignore */ }
  public void mouseClicked  (MouseEvent event)
  { X = (float)event.getX() - 3F;  Y = (float)event.getY() - 30F;
    /*   Why X-3F and Y-30F?  Because:  If you calibrate your mouse arrow by
    clicking its sharp tip on a precisely marked known point (X, Y) of the
    screen, you will find that Java mouse event methods return its position
    as (X+3F, Y+30F).  I think this human factors fudge is abominable.   */	  
    if (X > 804F && X < 1016F && Y > 4F && Y < 71F)  // Click on QUILT ellipse
    { if (STATE == 'C')  // User says color choice complete
      { choose(true);  return;}  
      if (STATE == 'Q')  // Pause the app
      { tick = false;  STATE = 'P';  return;}
      if (STATE == 'P')  // Resume the app
      { TQFS = System.currentTimeMillis() - TUSF;
	STATE = 'Q';  tick = true;  return;}
      if (STATE == '1')
      { STATE = '2';  prep();  return;}
      if (STATE == '2')
      { STATE = '3';  prep();  return;}
      if (STATE == '3')
      { STATE = 'I';  prep();  return;}
      return;}  // Sloppy click
    if (STATE == 'B') // Before or between games
    { if (X < 750F || X > 950F || Y < 285F || Y > 510F) return;
      if (Y < 386F) {STATE = '1';  return;}  // Get instructions 	    
      if (Y > 409F)  // Choose colors
      { Z[0] = Z[1] = Z[2] = -1;  STATE = 'C';  return;}
      return;}  // Sloppy click
    if (STATE == 'C') {choose(false);  return;}  // Choose colors
    if (STATE == 'Q' || STATE == '3')  // Fix the quilt or practice using tool
    {swap();  return;}}

  void clock()  // Maintain Time Used So Far and issue all repaint() calls
  { while (true)
    { try {Thread.sleep(333);}  // Sleep for 333 millisec
      catch (InterruptedException ie) { /* In case the app has closed */ }
      repaint();
      if (tick) TUSF = System.currentTimeMillis() - TQFS;}}

  void choose(boolean done)  // Choose colors
  { int i, j;  float q;
    if (done)  // User says colors have been chosen
    { if (Z[0] > -1 && Z[1] > -1 && Z[2] > -1) prep();  return;}
    if (Y < 590F) return;  // Sloppy click
    for (i = 0; i < 10; i++)  // Find which color square the user clicked
    { q = 100F * (float)i;
      if (X > 14F + q && X < 106F + q) break;}  // User clicked color i
    if (i == 10) return;  // Sloppy click
    for (j = 0; j < 3; j++)  // Does the user want to undo a choice?
      if (Z[j] == i) {Z[j] = -1; return;}
    for (j = 0; j < 3; j++)  // Save the choice if there is room for it
      if (Z[j] == -1) {Z[j] = i; return;}}

  boolean fixed()  // Returns true if the quilt is fixed and false otherwise
  { int i; boolean OK = true;
    for (i = 1; i < 24; i++)
    { if (array[L1[0]] != array[L1[i]] || array[L2[0]] != array[L2[i]])
      { OK = false;  break;}}    
    if (OK == true) return true;
    for (i = 1; i < 24; i++)
    { if (array[L3[0]] != array[L3[i]] || array[L4[0]] != array[L4[i]])
        return false;}
    return true;}

  void swap()  // If edge click is good and cell colors differ, swap colors 
  { int g, h, i, j, k;  float DX, DY;
    for (i = 0; i < 156; i++)
    { DX = X - QuiltG.edge[i][0];  DY = Y - QuiltG.edge[i][1];	    
      if (DX * DX + DY * DY < 144F) break;}
    if (i == 156) return;  // Sloppy click
    j = QuiltG.pair[i][0];  k = QuiltG.pair[i][1];  // Get cell numbers
    if((g = array[j]) ==  (h = array[k])) return;  // Cell colors the same
    array[j] = h;  array[k] = g;  // Cell colors different;  Swap colors.
    if (STATE =='3') return;  // Instruction screen 3
    SUSF++;  // Bump swap count
    if (fixed())  // Quilt fixed?
    { over = true;  STATE = 'B';  dots = tick = false;}}

  void prep()  // Prepare for various screens and states 
  { int h, i, j, k;
    if (STATE == 'I')
    { k = ((int)System.currentTimeMillis()) & 0X7FFF;	  
      Z[0] = Z[1] = Z[2] = k % 10;
      i = 1 + k % 3;  //  Impose a random color scheme	    
      while (Z[1] == Z[0]) Z[1] = (Z[1] + i) % 10; 
      while (Z[2] == Z[0] || Z[2] == Z[1]) Z[2] = (Z[2] + i) % 10;
      for (i = 0; i < 72; i++) array[i] = Z[0];
      for (i = 0; i < 24; i++) array[L1[i]] = Z[1];
      for (i = 0; i < 24; i++) array[L2[i]] = Z[2];
      tick = dots = over = false;  STATE = 'B';  return;}
    if (STATE == 'C' || STATE == '3')  // Colors have already been chosen
    { for (i =  0; i < 24; i++) array[i] = Z[0];
      for (i = 24; i < 48; i++) array[i] = Z[1];
      for (i = 48; i < 72; i++) array[i] = Z[2];
      dots = true;  over = false;  SUSF = 0;
      TQFS = System.currentTimeMillis();
      k = ((int)TQFS) & 0X7FFF;
      for (i = 71;  i > 0;  i--)  // Shuffle array[]
      { j = k % i;  // Now swap array[i] and array[j]
	h = array[i];  array[i] = array[j];  array[j] = h;}
      if (STATE == '3') return;
      STATE = 'Q';  TUSF = 0L;  tick = true;  return;}
    if (STATE == '2')
    { for (i = 0; i < 72; i++) array[i]     = Z[0];
      for (i = 0; i < 24; i++) array[L3[i]] = Z[1];
      for (i = 0; i < 24; i++) array[L4[i]] = Z[2];
      return;}}}   

class QuiltG extends JPanel  // Graphics Subclass
{ /*  Schematic X and Y coordinates of the Pointer Vertex (opposite the short
  horizontal or vertical side) of each of the 72 cells.  Cells 0 - 17 have it
  pointing to the left; cells 18 - 35 have it pointing upwards; cells 36 to 53
  have it pointing to the right; cells 54 to 71 have it pointing downwards.  */
  int[][] SPV = {
  { 0, 3},{ 8, 3},{16, 3},{ 4, 7},{12, 7},{20, 7},{ 0,11},{ 8,11},{16,11},
  { 4,15},{12,15},{20,15},{ 0,19},{ 8,19},{16,19},{ 4,23},{12,23},{20,23},
  { 7, 0},{15, 0},{23, 0},{ 3, 4},{11, 4},{19, 4},{ 7, 8},{15, 8},{23, 8},
  { 3,12},{11,12},{19,12},{ 7,16},{15,16},{23,16},{ 3,20},{11,20},{19,20},
  { 6, 3},{14, 3},{22, 3},{10, 7},{18, 7},{26, 7},{ 6,11},{14,11},{22,11},
  {10,15},{18,15},{26,15},{ 6,19},{14,19},{22,19},{10,23},{18,23},{26,23},
  { 7, 6},{15, 6},{23, 6},{ 3,10},{11,10},{19,10},{ 7,14},{15,14},{23,14},
  { 3,18},{11,18},{19,18},{ 7,22},{15,22},{23,22},{ 3,26},{11,26},{19,26}};

  static float[][][] cell = new float[72][5][2];  // X & Y pixel coordinates
  //  in cyclic order of the five vertices of each cell, pointer vertex first.
  // Parallel arrays:  for each border edge between adjacent cells, ... 
  static float[][] edge = new float[156][2];  // ...the X and Y center coords &
  static int[][] pair = new int[156][2];  // the nbrs. of the two border cells  
 
  QuiltG()  // Subclass Constructor
  { setBackground(Color.gray);
    boolean maybe;  int i, j, n = 0, x, y, p, q;  float sa=0F, sb=0F, w = 0F;       
    for (i = 0; i < 72; i++)   // Initialize cell[][][]
    { x = SPV[i][0];  y = SPV[i][1];
      cell[i][0][0] = 5F+25F*(float)x;
      cell[i][0][1] = 5F+25F*(float)y;
      if (i < 18)       // Cell points left
      { cell[i][1][0] = 5F+25F*(float)(x+1);
        cell[i][1][1] = 5F+25F*(float)(y-2);
	cell[i][2][0] = 5F+25F*(float)(x+3);
        cell[i][2][1] = 5F+25F*(float)(y-1);
	cell[i][3][0] = 5F+25F*(float)(x+3);
        cell[i][3][1] = 5F+25F*(float)(y+1);
	cell[i][4][0] = 5F+25F*(float)(x+1);
        cell[i][4][1] = 5F+25F*(float)(y+2);}
      else if (i < 36)  // Cell points upwards
      { cell[i][1][0] = 5F+25F*(float)(x+2);
        cell[i][1][1] = 5F+25F*(float)(y+1);
	cell[i][2][0] = 5F+25F*(float)(x+1);
        cell[i][2][1] = 5F+25F*(float)(y+3);
	cell[i][3][0] = 5F+25F*(float)(x-1);
        cell[i][3][1] = 5F+25F*(float)(y+3);
	cell[i][4][0] = 5F+25F*(float)(x-2);
        cell[i][4][1] = 5F+25F*(float)(y+1);}
      else if (i < 54)  // Cell points right
      { cell[i][1][0] = 5F+25F*(float)(x-1);
        cell[i][1][1] = 5F+25F*(float)(y+2);
	cell[i][2][0] = 5F+25F*(float)(x-3);
        cell[i][2][1] = 5F+25F*(float)(y+1);
	cell[i][3][0] = 5F+25F*(float)(x-3);
        cell[i][3][1] = 5F+25F*(float)(y-1);
	cell[i][4][0] = 5F+25F*(float)(x-1);
        cell[i][4][1] = 5F+25F*(float)(y-2);}
      else  //  i < 72 and cell points downwards
      { cell[i][1][0] = 5F+25F*(float)(x-2);
        cell[i][1][1] = 5F+25F*(float)(y-1);
	cell[i][2][0] = 5F+25F*(float)(x-1);
        cell[i][2][1] = 5F+25F*(float)(y-3);
	cell[i][3][0] = 5F+25F*(float)(x+1);
        cell[i][3][1] = 5F+25F*(float)(y-3);
	cell[i][4][0] = 5F+25F*(float)(x+2);
        cell[i][4][1] = 5F+25F*(float)(y-1);}}
    for (i = 0; i < 71; i++)  // Initialize edge[][] and swap[][]
    { for (j = i+1; j < 72; j++)
      { maybe = false;  // Do cells i & j share two vertices (i.e. a side)?
        for (p = 0; p < 5; p++)
        { for (q = 0; q < 5; q++) 
          { if (cell[i][p][0]==cell[j][q][0] && cell[i][p][1]==cell[j][q][1])
	    { if (maybe == false)
	      { maybe = true;
		sa = cell[i][p][0];  sb = cell[i][p][1];}
              else 
	      { edge[n][0] = (sa + cell[i][p][0]) / 2F;
		edge[n][1] = (sb + cell[i][p][1]) / 2F;
		pair[n][0] = i;
                pair[n++][1] = j;}}}}}}}
 
  Font font1 = new Font("Serif", Font.BOLD, 20);
  Font font2 = new Font("SanSerif", Font.BOLD, 11);
  Font font3 = new Font("SanSerif", Font.BOLD, 14);

  String[] colors = {"Black", "Blue", "Cyan", "Green", "Magenta",
	             "Orange", "Pink", "Red", "White", "Yellow"};
  String[] IC = {"COLOR SELECTION",
  "Choose three colors for your quilt",
  "by clicking on three of the color",
  "squares below.  The colors you pick",
  "will show up as pie slices in a big",
  "circle to the left.",
  "If you change your mind about one of",
  "the colors you chose, get rid of it",
  "by clicking its color square again.", " ",
  "When you're happy with the three",
  "colors you picked, click the QUILT",
  "ellipse above to receive your quilt",
  "kit and start swapping its patches."};    
  String[] I1 = {
  "When you mail order a patchwork quilt",
  "kit it always comes with 72 pentagonal",
  "patches that can be arranged to form",
  "a square.  You pick three colors, and",
  "there are 24 patches of each color.", " ",
  "When you ordered your kit, you assumed", 
  "that it would come with a plan speci-",
  "fying how to arrange the patches so",
  "that two patches of the same color",
  "would never be adjacent.  In other",
  "words, you expected to see something",
  "like what is at left, but in the colors",
  "you chose.", " ",
  "However, that is not what the manufac-",
  "turer had in mind.  There are a number",
  "of ways to arrange the patches so that",
  "whenever two patches touch each other,",
  "their colors differ.  If you click the",
  "QUILT ellipse above, you will see another",
  "such arrangement.", " ",  
  "To gve you the creative freedom to design",
  "your own quilt, the manufacturer basted",
  "the patches together in a random manner",
  "and provided you with a tool to help you",
  "accomplish the rearrangement.", " ",
  "Click the QUILT ellipse again to see",
  "what the manufacturer provided."};
  String[] I3 = {
  "The tool provided puts a circle in the",
  "middle of each edge between adjacent",
  "patches with different colors.  If you",
  "put the very tip of the mouse arrow in",
  "any of these circles and click, those",
  "two patches will swap places.", " ",
  "The tool is in operation, now.  You",
  "should try using it now because it is",
  "all that you have to fix your quilt.", " ",
  "As you actually fix a quilt, a counter",
  "will keep track of the number of swaps",
  "you use and a timer will show you how",
  "long you have been swapping patches.",
  "This gives you an objective measure of",
  "your speed and accuracy so that you",
  "can improve your peformance.", " ",
  "If you want to take a break from fixing",
  "a quilt, click on the QUILT ellipse to",
  "blank the screen and pause the fix.",
  "Another click will enable you resume",
  "the fix with no timer or counter loss.", " ",
  "When you finish separating the patches",
  "with like colors, the tool will detect",
  "your success, freeze timekeeping, stop",
  "counting swaps, and erase all the circles",
  "so that you can enjoy your quilt.", " ",
  "Click on the ellipse above to get started",
  "by choosing the colors for your quilt."};


  String cvt(int n, boolean time)  // Convert positive integer to string
  { String s;  String[] nbr = {"0","1","2","3","4","5","6","7","8","9"}; 	  
    if (time && n < 10) return "0" + nbr[n];  // Leading zeros for time strings
    s = nbr[n % 10];
    while(true)
    { n = n / 10;   	    
      if (n == 0) return s;
      s = nbr[n % 10] + s;}}

  String showtime() // Convert Quilt.TUSF  to "hh:mm:ss" format
  { int hh, mm, ss, time;
    time = (int)(Quilt.TUSF / 1000L);  // Convert milliseconds to seconds 	  
    ss = time % 60;  time = time / 60;
    mm = time % 60;  hh = time / 60;
    return cvt(hh, true) + ":" + cvt(mm, true) + ":" + cvt(ss, true);}

  public void paintComponent(Graphics Initialize)
  { super.paintComponent(Initialize);
    Graphics2D comp2D = (Graphics2D)Initialize;
    comp2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON);
    int g, h, i, j, k;  float q;

    comp2D.setColor(Color.white);   // Draw banner
    Ellipse2D.Float e0 = new Ellipse2D.Float(825F,5F,190F,65F);
    comp2D.fill(e0);
    comp2D.setFont(font1);
    comp2D.setColor(Color.red);
    comp2D.drawString("QUILT", 880F, 30F);
    comp2D.setColor(Color.black);
    comp2D.setFont(font2);
    comp2D.drawString("(c) Robert Abes", 870F, 45F);
    comp2D.drawString("January 2019", 870F, 60F);

    if (Quilt.STATE == 'P')  // Pause screen
    { comp2D.setFont(font1);
      comp2D.setColor(Color.black);
      comp2D.drawString("QUILT PAUSED.", 300F, 250F);
      comp2D.drawString("Click the QUILT ellipse to resume.", 300F, 400F);
      return;}

    if (Quilt.STATE == 'C')  // Color choice screen
    { comp2D.setFont(font2);	
      for (i = 0; i < 10; i++)	   
      { switch (i)
        { case 0:  comp2D.setColor(Color.black);   break;
          case 1:  comp2D.setColor(Color.blue);    break;
          case 2:  comp2D.setColor(Color.cyan);    break;
          case 3:  comp2D.setColor(Color.green);   break;
          case 4:  comp2D.setColor(Color.magenta); break;
          case 5:  comp2D.setColor(Color.orange);  break;
          case 6:  comp2D.setColor(Color.pink);    break;
          case 7:  comp2D.setColor(Color.red);     break;
          case 8:  comp2D.setColor(Color.white);   break;
          case 9:  comp2D.setColor(Color.yellow);  break;}
        q = 100F*(float)i + 15F;		   
        Rectangle2D.Float r0 = new Rectangle2D.Float(q, 590F, 90F, 100F);
        comp2D.fill(r0);
        for (j = 0; j < 3; j++)
	{ if (Quilt.Z[j] == i)
          { Arc2D.Float A0 = new Arc2D.Float
              (60F, 40F, 500F, 500F, (float)j * 120F, 120F, Arc2D.PIE);
            comp2D.fill(A0);}}
        comp2D.setColor(Color.gray);   
	comp2D.drawString(colors[i], q + 15F, 610F);}
      comp2D.setColor(Color.black);
      Rectangle2D.Float r1 = new Rectangle2D.Float(630F, 140F, 400F, 400F);
      comp2D.fill(r1);
      comp2D.setColor(Color.yellow);
      for (i = 0; i < IC.length; i++)
        comp2D.drawString(IC[i],705F,210F + 20F * (float)i);
    return;}

    for (i=0; i<72; i++)            // Draw the quilt cells
    { switch (Quilt.array[i])
      { case 0:  comp2D.setColor(Color.black);   break;
        case 1:  comp2D.setColor(Color.blue);    break;
        case 2:  comp2D.setColor(Color.cyan);    break;
        case 3:  comp2D.setColor(Color.green);   break;
        case 4:  comp2D.setColor(Color.magenta); break;
        case 5:  comp2D.setColor(Color.orange);  break;
        case 6:  comp2D.setColor(Color.pink);    break;
        case 7:  comp2D.setColor(Color.red);     break;
        case 8:  comp2D.setColor(Color.white);   break;
        case 9:  comp2D.setColor(Color.yellow);  break;}
      GeneralPath gp = new GeneralPath();
      { gp.moveTo(cell[i][0][0], cell[i][0][1]);
        gp.lineTo(cell[i][1][0], cell[i][1][1]);
        gp.lineTo(cell[i][2][0], cell[i][2][1]);
        gp.lineTo(cell[i][3][0], cell[i][3][1]);
        gp.lineTo(cell[i][4][0], cell[i][4][1]);
        gp.lineTo(cell[i][0][0], cell[i][0][1]);}
      comp2D.fill(gp);}

    for (i = 0; Quilt.dots && i < 156; i++)  // Draw the edge circles:
    { j = Quilt.array[pair[i][0]];           // Get colors of cells...
      k = Quilt.array[pair[i][1]];           // ...adjacent to the edge
      if (j == k) continue;  // Find the "third color" used in the quilt
      if ((h = Quilt.Z[0]) != j && h != k) g = h;
      else if ((h = Quilt.Z[1]) != j && h != k) g = h;  
      else g = Quilt.Z[2];
      switch (g)
      { case 0:  comp2D.setColor(Color.black);   break;
        case 1:  comp2D.setColor(Color.blue);    break;
        case 2:  comp2D.setColor(Color.cyan);    break;
        case 3:  comp2D.setColor(Color.green);   break;
        case 4:  comp2D.setColor(Color.magenta); break;
        case 5:  comp2D.setColor(Color.orange);  break;
        case 6:  comp2D.setColor(Color.pink);    break;
        case 7:  comp2D.setColor(Color.red);     break;
        case 8:  comp2D.setColor(Color.white);   break;
        case 9:  comp2D.setColor(Color.yellow);  break;}
      Ellipse2D.Float e1 = new Ellipse2D.Float
        (edge[i][0]-5F, edge[i][1]-5F, 10F, 10F);
      comp2D.fill(e1);}

    if (Quilt.STATE == 'Q' || (Quilt.STATE == 'B' && Quilt.over))
    { comp2D.setColor(Color.black);
      Rectangle2D.Float r1 = new Rectangle2D.Float(730F, 100F, 300F, 150F);
      comp2D.fill(r1);
      comp2D.setColor(Color.yellow);
      comp2D.setFont(font3);
      comp2D.drawString("Time Used: " + showtime(), 775F, 145F);
      comp2D.drawString("Swaps Used: " + cvt(Quilt.SUSF, false), 775F, 200F);
      if (Quilt.STATE == 'Q') return;}

    if (Quilt.STATE > '0' && Quilt.STATE < '4')
    {  comp2D.setColor(Color.black);    
       Rectangle2D.Float r2 = new Rectangle2D.Float(675F, 80F, 355F, 610F);
       comp2D.fill(r2);
       comp2D.setFont(font3);
       comp2D.setColor(Color.yellow);}
    if (Quilt.STATE == '1' || Quilt.STATE == '2')
    { for (i = 0; i < I1.length; i++)
        comp2D.drawString(I1[i], 685F, 100F + 17F * (float)i);
      return;}
    if (Quilt.STATE == '3')
    { for (i = 0; i < I3.length; i++)
        comp2D.drawString(I3[i], 685F, 100F + 17F * (float)i);
      return;}

    if (Quilt.STATE == 'B')  // Before or Between Games
    { comp2D.setColor(Color.black);
      Ellipse2D.Float e2 = new Ellipse2D.Float(750F,285F, 200F, 100F);
      comp2D.fill(e2);
      Ellipse2D.Float e3 = new Ellipse2D.Float(750F,410F, 200F, 100F);
      comp2D.fill(e3);     
      comp2D.setColor(Color.yellow);	    
      comp2D.setFont(font3);
      comp2D.drawString("Click Here For", 790F, 325F);
      comp2D.drawString("Instructions", 790F, 350F);
      comp2D.drawString("Click Here To", 790F, 450F);
      comp2D.drawString("Choose Colors", 790F, 475F);
      if(Quilt.over)
      { comp2D.setColor(Color.red);	    
        comp2D.setFont(font1);
        comp2D.drawString("CONGRATULATIONS!", 730F, 560F);
        comp2D.drawString("Enjoy Your Quilt!", 730F, 600F);}}}}
