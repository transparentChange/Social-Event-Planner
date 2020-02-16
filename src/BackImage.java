import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JPanel;

class BackImage extends JPanel {
  BufferedImage img;
  int width;
  int height;
  
  /*
   * BackImage
   * This constructor takes a BufferedImage parameter and sets it to the instance BufferedImage variable of the class
   * @param i, a BufferedImage
   */ 
  public BackImage(int width, int height, BufferedImage i) {
    this.img = i;
    this.width = width;
    this.height = height;
  }
  
  /*
   * paintComponent
   * Draws the BufferedImage img such that it takes up the entire JComponent
   * @param g, the Graphics object used to draw
   */
  @Override
  public void paintComponent(Graphics g) {
    g.drawImage(img, 0, 0, width, height, null);
  }
}