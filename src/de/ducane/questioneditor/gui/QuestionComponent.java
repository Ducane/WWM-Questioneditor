package de.ducane.questioneditor.gui;

import static de.androbin.math.util.floats.FloatMathUtil.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.text.*;

public class QuestionComponent extends JPanel {
  private static final long serialVersionUID = 1L;
  
  private static final float COLLAPSED_HEIGHT = 0.08f;
  private static final float EXPANDED_HEIGHT = 0.24f;
  
  private static final ExecutorService animator = Executors.newCachedThreadPool();
  
  private GUI gui;
  
  private JTextField[] answers;
  private JTextField question;
  private JFormattedTextField correctAnswer;
  private JFormattedTextField difficulty;
  
  private boolean expanded;
  
  private boolean animationRunning;
  
  private int number;
  
  public QuestionComponent( final GUI gui ) {
    this.gui = gui;
    
    init();
  }
  
  private void init() {
    updateBounds();
    
    addMouseListener( new MouseAdapter() {
      final int collapsedHeight = (int) ( COLLAPSED_HEIGHT * gui.getHeight() );
      final int expandedHeight = (int) ( EXPANDED_HEIGHT * gui.getHeight() );
      
      @ Override
      public void mouseClicked( final MouseEvent event ) {
        if ( event.getY() < collapsedHeight ) {
          expanded ^= true;
          
          if ( !animationRunning ) {
            animationRunning = true;
            
            animator.execute( () -> {
              float progress = expanded ? 0f : 1f;
              boolean finished = false;
              
              while ( !finished ) {
                try {
                  Thread.sleep( 40L );
                } catch ( final InterruptedException ignore ) {
                }
                
                if ( getHeight() < ( expanded ? expandedHeight : collapsedHeight ) ) {
                  progress += 0.1f;
                  
                  if ( progress >= 1f ) {
                    finished = true;
                  }
                } else {
                  progress -= 0.1f;
                  
                  if ( progress <= 0f ) {
                    finished = true;
                  }
                }
                
                final int iheight = (int) inter( collapsedHeight, progress, expandedHeight );
                updateBounds( iheight );
              }
              
              animationRunning = false;
            } );
          }
        }
      };
    } );
    
    initLayout();
    initTextFields();
  }
  
  public boolean isExpanded() {
    return expanded;
  }
  
  public JTextField[] getAnswerFields() {
    return answers;
  }
  
  public JTextField getQuestionField() {
    return question;
  }
  
  public JFormattedTextField getCorrectAnswerField() {
    return correctAnswer;
  }
  
  public JFormattedTextField getDifficultyField() {
    return difficulty;
  }
  
  public String getQuestion() {
    String question = this.question.getText();
    
    for ( int i = 0; i < question.length(); i++ ) {
      final char c = question.charAt( i );
      
      if ( c > 127 ) {
        question = question.replace( String.valueOf( c ),
            "\\u" + Integer.toHexString( c | 0x10000 ).substring( 1 ) );
      }
    }
    
    return question;
  }
  
  public char getCorrectAnswer() {
    return correctAnswer.getText().charAt( 0 );
  }
  
  public int getDifficulty() {
    return Integer.parseInt( difficulty.getText() );
  }
  
  public String[] getAnswers() {
    final String[] answers = new String[ this.answers.length ];
    
    for ( int i = 0; i < answers.length; i++ ) {
      final JTextField field = this.answers[ i ];
      answers[ i ] = field.getText();
      
      final String answer = answers[ i ];
      
      for ( int j = 0; j < answer.length(); j++ ) {
        final char c = answer.charAt( j );
        
        if ( c > 127 ) {
          answers[ i ] = answers[ i ].replace( String.valueOf( c ),
              "\\u" + Integer.toHexString( c | 0x10000 ).substring( 1 ) );
        }
      }
    }
    
    return answers;
  }
  
  private void initLayout() {
    setLayout( null );
  }
  
  private void initTextFields() {
    final int collapsedHeight = (int) ( COLLAPSED_HEIGHT * gui.getHeight() );
    final int expandedHeight = (int) ( EXPANDED_HEIGHT * gui.getHeight() );
    
    question = new JTextField( "Question" );
    question.setFont( new Font( "Arial", 0, (int) ( collapsedHeight * 0.25f ) ) );
    question.setBackground( Color.decode( "#C9D3D5" ) );
    
    final int width = (int) ( gui.getWidth() * 0.9f );
    final int height = (int) ( collapsedHeight * 0.5f );
    final int x = ( gui.getWidth() - width ) / 2;
    final int y = ( collapsedHeight - height ) / 2;
    
    question.setBounds( x, y, width, height );
    
    add( question );
    
    final int downHeight = ( expandedHeight - collapsedHeight );
    
    answers = new JTextField[ 4 ];
    final JLabel[] answers_labels = new JLabel[ 4 ];
    
    for ( int i = 0; i < answers.length; i++ ) {
      answers[ i ] = new JTextField();
      answers[ i ].setFont( new Font( "Arial", 0, (int) ( collapsedHeight * 0.25f ) ) );
      
      final int answersWidth = (int) ( gui.getWidth() * 0.2f );
      final int answersHeight = (int) ( downHeight * 0.2f );
      final int answersX = (int) ( gui.getWidth() * 0.1f
          + i % 2 * ( answersWidth + 0.1f * gui.getWidth() ) );
      final int answersY = (int) ( collapsedHeight + downHeight * 0.2f
          + i / 2 * ( answersHeight + 0.2f * downHeight ) );
      
      answers[ i ].setBounds( answersX, answersY, answersWidth, answersHeight );
      add( answers[ i ] );
      
      answers_labels[ i ] = new JLabel( "Answer " + (char) ( 65 + i ) + ":" );
      answers_labels[ i ].setFont(
          new Font( "Arial", 0, (int) ( downHeight * 0.125f ) ) );
      
      final JLabel current = answers_labels[ i ];
      final FontMetrics fm = current.getFontMetrics( current.getFont() );
      answers_labels[ i ].setBounds(
          answersX - fm.stringWidth( current.getText() ) - (int) ( 0.025f * gui.getWidth() ),
          answersY, answersWidth, answersHeight );
      
      add( answers_labels[ i ] );
    }
    
    MaskFormatter mfCorrectAnswer = null;
    try {
      mfCorrectAnswer = new MaskFormatter( "*" );
    } catch ( final ParseException e ) {
      e.printStackTrace();
    }
    mfCorrectAnswer.setValidCharacters( "ABCD" );
    
    correctAnswer = new JFormattedTextField( mfCorrectAnswer );
    correctAnswer.setFont( new Font( "Arial", 0, (int) ( collapsedHeight * 0.25f ) ) );
    
    final int correctAnswerWidth = (int) ( gui.getWidth() * 0.2f );
    final int correctAnswerHeight = (int) ( downHeight * 0.2f );
    final int correctAnswerX = (int) ( gui.getWidth() * 0.7f );
    final int correctAnswerY = (int) ( collapsedHeight + downHeight * 0.2f );
    
    correctAnswer.setBounds( correctAnswerX, correctAnswerY, correctAnswerWidth,
        correctAnswerHeight );
    
    add( correctAnswer );
    
    final JLabel correctAnswerLabel = new JLabel( "Right answer:" );
    correctAnswerLabel
        .setFont( new Font( "Arial", 0, (int) ( ( expandedHeight - collapsedHeight ) * 0.125f ) ) );
    final FontMetrics fmCorrectAnswer = correctAnswerLabel
        .getFontMetrics( correctAnswerLabel.getFont() );
    correctAnswerLabel.setBounds(
        correctAnswerX - fmCorrectAnswer.stringWidth( correctAnswerLabel.getText() )
            - (int) ( 0.025f * gui.getWidth() ),
        correctAnswerY, correctAnswerWidth, correctAnswerHeight );
    
    add( correctAnswerLabel );
    
    MaskFormatter mfDifficulty = null;
    try {
      mfDifficulty = new MaskFormatter( "*" );
    } catch ( final ParseException e ) {
      e.printStackTrace();
    }
    mfDifficulty.setValidCharacters( "1234" );
    
    difficulty = new JFormattedTextField( mfDifficulty );
    difficulty.setFont( new Font( "Arial", 0, (int) ( collapsedHeight * 0.25f ) ) );
    
    final int difficultyWidth = (int) ( gui.getWidth() * 0.2f );
    final int difficultyHeight = (int) ( downHeight * 0.2f );
    final int difficultyX = (int) ( gui.getWidth() * 0.7f );
    final int difficultyY = (int) ( collapsedHeight + downHeight * 0.6f );
    
    difficulty.setBounds( difficultyX, difficultyY, difficultyWidth, difficultyHeight );
    
    add( difficulty );
    
    final JLabel difficultyLabel = new JLabel( "Difficulty:" );
    difficultyLabel
        .setFont( new Font( "Arial", 0, (int) ( ( expandedHeight - collapsedHeight ) * 0.125f ) ) );
    final FontMetrics fmDifficulty = difficultyLabel.getFontMetrics( difficultyLabel.getFont() );
    difficultyLabel.setBounds( difficultyX - fmDifficulty.stringWidth( difficultyLabel.getText() )
        - (int) ( 0.025f * gui.getWidth() ), difficultyY, difficultyWidth, difficultyHeight );
    add( difficultyLabel );
  }
  
  @ Override
  public void paintComponent( final Graphics g ) {
    final Graphics2D g2d = (Graphics2D) g;
    
    final int collapsedHeight = (int) ( COLLAPSED_HEIGHT * gui.getHeight() );
    
    g.setColor( Color.decode( "#DBE2E3" ) );
    g.fillRect( 0, 0, getWidth() - 1, getHeight() - 1 );
    
    g.setColor( Color.decode( "#C9D3D5" ) );
    g.fillRect( 0, 0, getWidth() - 1, collapsedHeight - 1 );
    
    g.setColor( Color.decode( "#53676C" ) );
    g.drawRect( 0, 0, getWidth() - 1, getHeight() - 1 );
    
    g.setColor( Color.BLACK );
    final JScrollBar bar = ( (JScrollPane) gui.getList().getParent().getParent() )
        .getVerticalScrollBar();
    g.setFont( new Font( "Arial", Font.BOLD, 12 ) );
    
    final FontMetrics fm = g.getFontMetrics();
    final int px = getWidth() - fm.stringWidth( String.valueOf( number ) ) - 5;
    
    g.drawString( String.valueOf( number ), bar.isVisible() ? px - bar.getWidth() : px,
        fm.getHeight() );
    
    final Path2D.Double triangle = new Path2D.Double();
    
    int height = (int) ( collapsedHeight * 0.2f );
    int width = (int) ( getWidth() * 0.0075f );
    final int x = (int) ( getWidth() * 0.01f );
    final int y =  ( collapsedHeight - height ) / 2;
    
    triangle.moveTo( x, y );
    
    if ( expanded ) {
      final int width_ = width;
      width = height;
      height = width_;
      
      triangle.lineTo( x + width / 2, y + height );
      triangle.lineTo( x + width, y );
    } else {
      triangle.lineTo( x + width, y + height / 2 );
      triangle.lineTo( x, y + height );
    }
    
    g.setColor( Color.BLACK );
    g2d.fill( triangle );
  }
  
  public void setNumber( final int number ) {
    this.number = number;
  }
  
  private void updateBounds( final int height ) {
    setSize( getWidth(), height );
    gui.updateBounds();
  }
  
  private void updateBounds() {
    updateBounds( (int) ( expanded ? EXPANDED_HEIGHT * gui.getHeight()
        : COLLAPSED_HEIGHT * gui.getHeight() ) );
  }
}