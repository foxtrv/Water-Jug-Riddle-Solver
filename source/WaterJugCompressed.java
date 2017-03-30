import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class WaterJugCompressed extends PApplet {

// Library - creates text box and grabs submitted input

ControlP5 cp5;
String url1, url2, url3;
String OUTPUT = ""; // Made it global just so I can call it easier from other classes
PGraphics textBox = new PGraphics();
PVector textBoxPos = new PVector(240, 30); // XY coords of answer text box
float scroll = 0;

// Creates window
public void settings() {
  size(350, 300, P2D);
}

// In processing, the setup fcn is the main fcn
public void setup() {
  background(0);
  textBox = createGraphics(90, 250, P2D); // Dimensions of answers text box (width, height)
  cp5 = new ControlP5(this);
  cp5.addTextfield("Size of bucket A").setPosition(20, 30).setSize(200, 40).setAutoClear(false);
  cp5.addTextfield("Size of bucket B").setPosition(20, 100).setSize(200, 40).setAutoClear(false);
  cp5.addTextfield("Desired volume").setPosition(20, 170).setSize(200, 40).setAutoClear(false);  
  cp5.addBang("Submit").setPosition(20, 240).setSize(200, 40);
}

// Submit button
public void Submit() {
  // Grab text from textbox
  url1 = cp5.get(Textfield.class,"Size of bucket A").getText();
  url2 = cp5.get(Textfield.class,"Size of bucket B").getText();
  url3 = cp5.get(Textfield.class,"Desired volume").getText(); 
  // Compute solution with user input
  WJ w = new WJ(Integer.parseInt(url1), Integer.parseInt(url2), Integer.parseInt(url3));
  w.checkGoal();
}

// Main waterjug class that solves the problem
class WJ {
  int a_max, b_max, goal;
  int a, b = 0;

  // Class constructor
  WJ(int bucketA, int bucketB, int desiredVol) {
    OUTPUT = ""; // reset answer output
    a_max = bucketA;
    b_max = bucketB;
    goal = desiredVol;
  }

  public void checkGoal() {
    // if the greatest common denominator is 1 then a solution exists. If not, cancel execution
    int ans = gcd(a_max, b_max);
    if (ans != 1 || (goal > a_max && goal > b_max)) {
      OUTPUT = "No sln";
      return;
    }

    // Stores first set of buckets {0, 0}
    OUTPUT += "{" + this.a + "," + this.b + "}";

    while (!(this.a == this.goal || this.b == this.goal)) {
      if (this.a == 0) 
        fillA();
      else if ((this.a > 0) && (this.b != this.b_max))
        transferAtoB();
      else if ((this.a > 0) && (this.b == this.b_max))
        emptyB();
    }
  }

  // Computes greatest common denominator
  public int gcd(int a, int b) {
    if (a == 0 || b == 0) 
      return a + b;
    return gcd(b, a%b);
  }

  // Fills bucket A
  public void fillA() {
    this.a = this.a_max;
    OUTPUT += "\n{" + this.a + "," + this.b + "}";
  }

  // Fills bucket B
  public void fillB() {
    this.b = this.b_max;
    OUTPUT += "\n{" + this.a + "," + this.b + "}";
  }

  // Transfer content of A into B
  public void transferAtoB() {
    while (!(this.b == this.b_max || this.a == 0)) {
      this.b += 1;
      this.a -= 1;
    }
    OUTPUT += "\n{" + this.a + "," + this.b + "}";
  }

  // Empty bucket A
  public void emptyA() {
    this.a = 0;
    OUTPUT += "\n{" + this.a + "," + this.b + "}";
  }

  // Empty bucket B
  public void emptyB() {
    this.b = 0;
    OUTPUT += "\n{" + this.a + "," + this.b + "}";
  }
}

public void draw() {
  background(0); // black background
  textBox.beginDraw();
  textBox.background(255, 255, 255, 255); // White background inside the answer output box
  textBox.stroke(0, 0, 0);
  textBox.fill(0, 0, 0, 0);
  textBox.rect(0, 0, textBox.width-1, textBox.height-1);  // Black rectangle around the outside.
  textBox.textSize(24);
  textBox.fill(0); // Color of text
  textBox.textAlign(RIGHT, TOP);
  int count = countLines(OUTPUT); // useful for modifying height value of scrolling text (as seen below)
  textBox.text(OUTPUT, 0, scroll, textBox.width, textBox.height*count);
  textBox.endDraw();
  image(textBox, textBoxPos.x, textBoxPos.y);
}

// Scrolling answer text field (if output extends past window)
public void mouseWheel(MouseEvent event) {
  scroll -= event.getCount()*10;
}

public int countLines(String s) {
  return (s + " ").split("\r?\n").length;
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "WaterJugCompressed" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
