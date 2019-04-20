package de.ducane.questioneditor;

import de.androbin.json.*;

public final class Configuration {
  private static final XObject CONFIG = XUtil.readJSON( "config.json" ).get().asObject();
  
  public static final class window_ {
    private static final XObject CONFIG_WINDOW = CONFIG.get( "window" ).asObject();
    
    public static final boolean RESIZABLE = CONFIG_WINDOW.get( "resizable" ).asBoolean();
    public static final boolean UNDECORATED = CONFIG_WINDOW.get( "undecorated" ).asBoolean();
    public static final float SCALE = CONFIG_WINDOW.get( "scale" ).asFloat();
    public static final String TITLE = CONFIG_WINDOW.get( "title" ).asString();
  }
  
  public static final class dialog_ {
    private static final XObject CONFIG_DIALOG = CONFIG.get( "dialog" ).asObject();
    
    public static final boolean RESIZABLE = CONFIG_DIALOG.get( "resizable" ).asBoolean();
    public static final boolean UNDECORATED = CONFIG_DIALOG.get( "undecorated" ).asBoolean();
    public static final float SCALE = CONFIG_DIALOG.get( "scale" ).asFloat();
    public static final String TITLE = CONFIG_DIALOG.get( "title" ).asString();
    
    public static final float QUESTION_TEXTFIELD_X = CONFIG_DIALOG.get( "question_textfield_x" )
        .asFloat();
    public static final float QUESTION_TEXTFIELD_Y = CONFIG_DIALOG.get( "question_textfield_y" )
        .asFloat();
    public static final float QUESTION_TEXTFIELD_WIDTH = CONFIG_DIALOG
        .get( "question_textfield_width" ).asFloat();
    public static final float QUESTION_TEXTFIELD_HEIGHT = CONFIG_DIALOG
        .get( "question_textfield_height" ).asFloat();
    
    public static final float ANSWERS_TEXTFIELD_X = CONFIG_DIALOG.get( "answers_textfield_x" )
        .asFloat();
    public static final float ANSWERS_TEXTFIELD_Y = CONFIG_DIALOG.get( "answers_textfield_y" )
        .asFloat();
    public static final float ANSWERS_TEXTFIELD_DX = CONFIG_DIALOG.get( "answers_textfield_dx" )
        .asFloat();
    public static final float ANSWERS_TEXTFIELD_DY = CONFIG_DIALOG.get( "answers_textfield_dy" )
        .asFloat();
    public static final float ANSWERS_TEXTFIELD_WIDTH = CONFIG_DIALOG
        .get( "answers_textfield_width" ).asFloat();
    public static final float ANSWERS_TEXTFIELD_HEIGHT = CONFIG_DIALOG
        .get( "answers_textfield_height" ).asFloat();
    
    public static final float VALUES_TEXTFIELD_X = CONFIG_DIALOG.get( "values_textfield_x" )
        .asFloat();
    public static final float VALUES_TEXTFIELD_Y = CONFIG_DIALOG.get( "values_textfield_y" )
        .asFloat();
    public static final float VALUES_TEXTFIELD_DX = CONFIG_DIALOG.get( "values_textfield_dx" )
        .asFloat();
    public static final float VALUES_TEXTFIELD_WIDTH = CONFIG_DIALOG.get( "values_textfield_width" )
        .asFloat();
    public static final float VALUES_TEXTFIELD_HEIGHT = CONFIG_DIALOG
        .get( "values_textfield_height" ).asFloat();
  }
}