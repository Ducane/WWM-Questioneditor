package de.ducane.questioneditor;

import static de.ducane.questioneditor.Configuration.window_.*;
import de.ducane.questioneditor.gui.*;
import java.awt.*;
import javax.swing.*;

public final class Main {
  private Main() {
  }
  
  public static void main( final String[] args ) {
    // SystemGraphics.setSystemLookAndFeel();
    
    final JFrame window = new JFrame();
    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    window.setUndecorated( UNDECORATED );
    window.setResizable( RESIZABLE );
    window.setTitle( TITLE );
    window.setSize( calcWindowSize() );
    window.setLocationRelativeTo( null );
    window.setContentPane( new GUI() );
    
    window.setVisible( true );
  }
  
  private static Dimension calcWindowSize() {
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    
    final int width = (int) ( screenSize.getWidth() * SCALE );
    final int height = (int) ( screenSize.getHeight() * SCALE );
    
    return new Dimension( width, height );
  }
}
