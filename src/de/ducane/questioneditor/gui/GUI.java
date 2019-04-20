package de.ducane.questioneditor.gui;

import de.androbin.json.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.*;
import org.json.simple.*;

public final class GUI extends JPanel {
  private static final long serialVersionUID = 1L;
  
  private final List<QuestionComponent> questions = new ArrayList<QuestionComponent>();
  
  private JPanel list = new JPanel();
  
  public GUI() {
    init();
  }
  
  private void init() {
    initLayout();
    initList();
    initButtons();
    initMenu();
  }
  
  private void initList() {
    list = new JPanel();
    list.setLayout( null );
    list.setBackground( Color.WHITE );
    
    final JScrollPane pane = new JScrollPane( list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
    
    add( pane, BorderLayout.CENTER );
  }
  
  private void initButtons() {
    final JPanel button_panel = new JPanel();
    button_panel.setLayout( new FlowLayout() );
    
    final JButton add = new JButton( "+" );
    add.setFont( new Font( "Calibri Light", 0, 20 ) );
    add.setPreferredSize( new Dimension( 200, 30 ) );
    add.addActionListener( ( final ActionEvent event ) -> {
      final QuestionComponent component = new QuestionComponent( this );
      questions.add( component );
      list.add( component );
      updateBounds();
    } );
    button_panel.add( add );
    
    final JButton delete = new JButton( "-" );
    delete.setFont( new Font( "Calibri Light", 0, 20 ) );
    delete.setPreferredSize( new Dimension( 200, 30 ) );
    delete.addActionListener( ( final ActionEvent event ) -> {
      final List<QuestionComponent> toRemove = new LinkedList<QuestionComponent>();
      
      if ( !questions.isEmpty() ) {
        for ( final QuestionComponent component : questions ) {
          if ( component.isExpanded() ) {
            list.remove( component );
            toRemove.add( component );
          }
        }
        questions.removeAll( toRemove );
        list.repaint();
        updateBounds();
      }
    } );
    button_panel.add( delete );
    
    add( button_panel, BorderLayout.SOUTH );
  }
  
  private void initLayout() {
    setLayout( new BorderLayout() );
  }
  
  private void initMenu() {
    final JMenuBar menubar = new JMenuBar();
    
    {
      final JMenu menuFile = new JMenu( "File..." );
      menuFile.setMnemonic( 'D' );
      
      final int ctrlShortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
      
      {
        final JMenuItem menuItemSave = new JMenuItem( "Save..." );
        menuItemSave.addActionListener( ( final ActionEvent event ) -> {
          if ( saveConditions() ) {
            final JFileChooser chooser = new JFileChooser();
            
            final FileNameExtensionFilter filter = new FileNameExtensionFilter( "json (*.json)",
                "json" );
            chooser.setFileFilter( filter );
            
            final int option = chooser.showSaveDialog( null );
            
            if ( option == JFileChooser.APPROVE_OPTION ) {
              final List<JSONObject> questions = new ArrayList<>();
              
              for ( final QuestionComponent comp : this.questions ) {
                final JSONObject object = parseToJSON( comp );
                questions.add( object );
              }
              
              final File file = chooser.getSelectedFile();
              final String fileName = file.getName();
              
              try {
                final FileWriter writer = new FileWriter(
                    fileName.endsWith( "json" ) ? file.getPath() : file + ".json" );
                writer.write( XArray.toString( questions, true ) );
                writer.close();
              } catch ( final IOException e ) {
                e.printStackTrace();
              }
            }
          } else {
            JOptionPane.showMessageDialog( null,
                "Difficulty and right answer are not correctly filled.", "Error",
                JOptionPane.WARNING_MESSAGE );
          }
        } );
        menuItemSave.setMnemonic( 'K' );
        menuItemSave.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, ctrlShortcut ) );
        menuFile.add( menuItemSave );
      }
      
      {
        final JMenuItem menuItemLoad = new JMenuItem( "Load..." );
        menuItemLoad.addActionListener( ( final ActionEvent event ) -> {
          final JFileChooser chooser = new JFileChooser();
          
          final FileNameExtensionFilter filter = new FileNameExtensionFilter( "json (*.json)",
              "json" );
          chooser.setFileFilter( filter );
          
          final int option = chooser.showOpenDialog( null );
          
          if ( option == JFileChooser.APPROVE_OPTION ) {
            final File selectedFile = chooser.getSelectedFile();
            final String name = selectedFile.getName();
            
            if ( name.endsWith( ".json" ) ) {
              final XArray array = XUtil.readJSON( selectedFile.toPath() ).get().asArray();
              
              array.forEach( o -> {
                final XObject x = o.asObject();
                final QuestionComponent comp = parseToQuestionComponent( x );
                
                questions.add( comp );
                list.add( comp );
                updateBounds();
              } );
            } else {
              JOptionPane.showMessageDialog( null, "Only .json files are allowed!",
                  "Error", JOptionPane.WARNING_MESSAGE );
            }
          }
          
        } );
        menuItemLoad.setMnemonic( 'Y' );
        menuItemLoad.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_L, ctrlShortcut ) );
        menuFile.add( menuItemLoad );
      }
      
      menuFile.addSeparator();
      
      {
        final JMenuItem menuItemClear = new JMenuItem( "Delete" );
        menuItemClear.addActionListener( ( final ActionEvent event ) -> {
          questions.clear();
          list.removeAll();
          list.repaint();
        } );
        menuItemClear.setMnemonic( 'S' );
        menuItemClear.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C, ctrlShortcut ) );
        menuFile.add( menuItemClear );
      }
      
      menubar.add( menuFile );
    }
    
    add( menubar, BorderLayout.NORTH );
  }
  
  private boolean saveConditions() {
    for ( final QuestionComponent component : questions ) {
      if ( component.getDifficultyField().getText().equals( " " )
          || component.getCorrectAnswerField().getText().equals( " " ) ) {
        return false;
      }
    }
    
    return true;
  }
  
  @ SuppressWarnings( "unchecked" )
  private JSONObject parseToJSON( final QuestionComponent comp ) {
    final JSONObject object = new JSONObject();
    
    object.put( "question", comp.getQuestion() );
    
    final JSONArray array = new JSONArray();
    
    final String[] answers = comp.getAnswers();
    for ( int i = 0; i < answers.length; i++ ) {
      array.add( answers[ i ] );
    }
    
    object.put( "answers", array );
    
    final char correctAnswer = comp.getCorrectAnswer();
    object.put( "correctAnswer", correctAnswer - 65 );
    object.put( "difficulty", comp.getDifficulty() );
    return object;
  }
  
  private QuestionComponent parseToQuestionComponent( final XObject o ) {
    final String questionText = o.get( "question" ).asString();
    final String[] answers = o.get( "answers" ).asStringArray();
    final int correctAnswer = o.get( "correctAnswer" ).asInt();
    final int difficulty = o.get( "difficulty" ).asInt();
    
    final QuestionComponent comp = new QuestionComponent( this );
    
    comp.getQuestionField().setText( questionText );
    
    final JTextField[] answerFields = comp.getAnswerFields();
    for ( int i = 0; i < answerFields.length; i++ ) {
      final JTextField field = answerFields[ i ];
      field.setText( answers[ i ] );
    }
    
    comp.getCorrectAnswerField().setText( String.valueOf( (char) ( correctAnswer + 65 ) ) );
    comp.getDifficultyField().setText( String.valueOf( difficulty ) );
    
    return comp;
  }
  
  public List<QuestionComponent> getQuestions() {
    return questions;
  }
  
  public JPanel getList() {
    return list;
  }
  
  protected void updateBounds() {
    int y = 0;
    
    for ( int i = 0; i < questions.size(); i++ ) {
      final QuestionComponent component = questions.get( i );
      component.setBounds( 0, y, list.getWidth(), component.getHeight() );
      component.setNumber( i + 1 );
      
      y += component.getHeight() - 1;
    }
    
    list.setPreferredSize( new Dimension( list.getWidth(), y ) );
    list.revalidate();
  }
}