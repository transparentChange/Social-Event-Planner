import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

class BackImage extends JComponent {
  BufferedImage img;
  
  /*
   * BackImage
   * This constructor takes a BufferedImage parameter and sets it to the instance BufferedImage variable of the class
   * @param i, a BufferedImage
   */ 
  public BackImage(BufferedImage i) {
    this.img = i;
  }
  
  /*
   * paintComponent
   * Draws the BufferedImage img such that it takes up the entire JComponent
   * @param g, the Graphics object used to draw
   */
  @Override
  public void paintComponent(Graphics g) {
    g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
  }
}