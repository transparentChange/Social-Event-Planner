import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.border.EmptyBorder;

/*
 * DesignConstants
 * This final class holds a number of constants relating to visual aspect of
 * the program, mainly Font and Color objects
 */
public final class DesignConstants {
	public static final Color BACK_COLOUR = new Color((float) (130 / 255.0), (float) (235 / 255.0), 
			(float) (33 / 255.0), (float) 0.3);
    public static final Color MAIN_COLOUR = new Color(75, 112, 68);
    
    public static final Font LARGE_BOLD_FONT = new Font("Segoe UI", Font.BOLD, 25);
    public static final Font LARGE_FONT = new Font("Segoe UI", Font.PLAIN, 20);
    public static final Font MEDIUM_BOLD_FONT = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font GENERAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_BOLD_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font SMALL_BOLD_ITALICS_FONT = new Font("Segoe UI", Font.BOLD + Font.ITALIC, 13);
    public static final Canvas C = new Canvas();
    public static final FontMetrics metricsSmall = C.getFontMetrics(GENERAL_FONT);
    
    public static final EmptyBorder INFO_LABEL_BORDER = new EmptyBorder(0, 0, 10, 0);
    
    DesignConstants() {
    	throw new AssertionError();
    }
}