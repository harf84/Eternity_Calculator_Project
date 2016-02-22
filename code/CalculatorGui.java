/**
 * Contributions: Fadi Hariri [Full front end design]
 * 
 */

package iteration1;

/**
 * Dependencies
 */
import static javafx.geometry.HPos.RIGHT;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * 
 * @author fadihariri
 * Main class for front end Calculator User Interface
 *
 */
public class CalculatorGui extends Application{

	public static void main(String[] args) {
		launch (args);
	}

	/**
	 * Gui Attributes
	 */
	protected Calculator compute = new Calculator ();//to compute stuff!
	protected String expr ="";//expression to be evaluated
	protected Queue <String> tokens = new LinkedList ();
	protected final int ALLOC =22;
	protected boolean evaluated=false;
	protected String result ="0";
	
	protected TextField input = new TextField("");
	protected GridPane grid = new GridPane();
	protected Button [] mainPad = new Button [30];
	protected String [] labelsmain = {
			"log\u2081\u2080X", "0", ".", "C", "+","=",
			"10\u02e3", "1","2","3","-","Ans",
			"X\u02b8","4","5","6","*","%",
			"√x","7","8","9","÷","1/x",
			"Sin","π","ln2","(",")", "X!"};
	
	protected HashMap <String, String> func = new HashMap ();
	protected HashMap <String, String> func2 = new HashMap ();
	
	/* 
	 * Main logic goes in start method which launches a javafx application with a noneditable input field and a grid of buttons. 
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		/*
		 * background image from:
		 * source:http://www.freelargeimages.com/black-background-1923/
		 * inputLabel image from:
		 * source: http://www.freelargeimages.com/wp-content/uploads/2014/12/Black_background-8.jpg
		 */
		initialize();//initial func map
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(30, 30, 30, 30));
		
		//input label
		input.setText("0");
		input.setEditable(false);
		input.setPrefWidth(550);
		input.setPrefHeight(90);
		input.setId("input");

		//set up buttons
		//main Pad
		HBox [] boxes = new HBox [5];
		GridPane grid2 = new GridPane();
		grid2.setHgap(10);
		grid2.setVgap(10);
		int row=20;
		int index=0;
		for (int i=0; i<mainPad.length;i++){
			
			if (i%6==0){
				boxes[index]= new HBox(10);
				//boxes[index].setAlignment(Pos.BOTTOM_RIGHT);
				grid2.add(boxes[index],0,row--);
				index++;
			}
			
			mainPad[i] = new Button (labelsmain[i]);
			mainPad[i].setPrefSize(100, 100);
			mainPad[i].setPrefSize(100, 100);
			//Logic goes here when button is clicked
			Button btn = mainPad[i];
			btn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				parseAndEvaluate (e, btn);
			}
		});
			boxes[index-1].getChildren().add(mainPad[i]);
		}
	
		//View
		GridPane grid3 = new GridPane();
		grid3.add(input, 1, 0);
		grid.add(grid3, 0,0);
		grid.add(grid2, 0, 0);
		grid.setMaxHeight(Control.USE_PREF_SIZE);
		
		
		
		Scene scene = new Scene(grid, 600, 600);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Eternity Calculator App");
		scene.getStylesheets().add(
				CalculatorGui.class.getResource("gui.css").toExternalForm());
		primaryStage.setResizable(false);
		primaryStage.show();

	}
	

	private void setInput (String st){
		String str="";
		if (func.containsKey(st)){
			str = func.get(st);}
		else str= st;
		String str2=str;
	
		if (str.equals("powX") || str.equals("pow10") || str.equals("sqrt")
				|| str.equals("log") || str.equals("/") || str.equals("pi")){
			str =func.get(str);
		}
		
		if (!evaluated){
			String s = (this.input.getText().equals("0") && (Character.isDigit(str.charAt(0))
					|| str2.equals("sqrt") || str2.equals("sin") || str2.equals("pi")|| 
					str2.equals("log")|| str2.equals("ln2")))?"":this.input.getText();
			this.input.setText(s+str);
			expr=(this.input.getText().equals("0") && Character.isDigit(str.charAt(0)))?str2:expr+str2;
				
		}
		else {
			String s = this.input.getText();
			if (Character.isDigit(str.charAt(0))){
				this.input.setText(str);
				expr=str2;
			}
			else {
				this.input.setText(s+str);
				expr+= s+str2;
			}
			evaluated=false;	
		}
		
	}
	/**
	 * Initializes special symbol hashmap
	 */
	private void initialize (){
		//numbers
		for (int i=0; i< 10; i++){func.put(i+"", i+"");}
		//special characters 
		func.put("*", "*");func.put("-", "-");func.put(".", ".");func.put("+", "+");func.put("÷", "/");func.put("%", "%");
		//equal, 1/x, !, (, )
		func.put("X!", "!");func.put("(", "(");func.put(")", ")");func.put("1/x", "1/");func.put("=", "=");
		
		//transcendental functions and special symbols
		func.put("ln2", "ln2");func.put("π", "pi");
		func.put("Sin", "sin");func.put("log\u2081\u2080X", "log");
		func.put("√x", "sqrt");func.put("10\u02e3", "pow10");
		func.put("X\u02b8", "powX");
		
		
		func.put("powX","^");func.put("pow10","10^");
		func.put("sqrt","√");func.put("log","log\u2081\u2080");
		func.put("/","÷");func.put("pi", "π");
	}
	
	/**
	 * parseAndEvaluate runs on a daemon thread to handle any button pressed and dynamically set input field
	 * @param Event e
	 * @param Button btn
	 */
	private void parseAndEvaluate (ActionEvent e, Button btn){
		/**
		 * Build Hierarchy of events
		 */
		//clear is pressed
		if (btn.getText().equals("C")){
			input.setText("0");
			evaluated=false;
			expr="";tokens =new LinkedList();
		}
		//if Ans is pressed
		else if (btn.getText().equals("Ans"))setInput(result);
		
		
		//reached end of allocated input
		else if (input.getText().length() == ALLOC || btn.getText().equals("=")){
			if (btn.getText().equals("=")){
				//System.out.println(expr);
				//evalate expression
				try{
					tokenize();
					String res=compute.evalTokens(tokens);
					input.setText(res);
					result=res;
					evaluated=true;
					expr = "";tokens = new LinkedList();
				}catch (Exception myException){
					Alert alert = new Alert (AlertType.ERROR);
					alert.setTitle("Error!");alert.setHeaderText("Error!");
					alert.setContentText("Error encountered in expression...");
					alert.showAndWait();
					myException.printStackTrace();
				}
			}
			else{return;}
		}
		
		
		else {setInput(btn.getText());}
		//System.out.println(expr);
	}
	
	private void tokenize (){
		for (int i= 0; i < this.expr.length(); i++){
			if (this.expr.charAt(i) == ' ')continue;
			String str="";
			if (Character.isDigit(this.expr.charAt(i))){
				while (i < this.expr.length() && (Character.isDigit(this.expr.charAt(i))
						|| this.expr.charAt(i) == '.')){
					str+= this.expr.charAt(i);i++;
				}
				i--;
			}
			
			else if (expr.charAt(i) == 'p' || expr.charAt(i) == 's' || expr.charAt(i) == 'l'){
				if (i+3 < expr.length() && expr.substring(i, i+4).equals("powX")){str="^";i+=3;}
				else if (i+4 < expr.length() && expr.substring(i, i+5).equals("pow10")){tokens.add("10");str="^";i+=4;}
				else if (i+1 < expr.length() && expr.substring(i, i+2).equals("pi")){str=""+compute.compute.pi;i+=1;}
				else if (i+3 < expr.length() && expr.substring(i, i+4).equals("sqrt")){str="sqrt";i+=3;}
				else if (i+2 < expr.length() && expr.substring(i, i+3).equals("sin")){str="sin";i+=2;}
				else if (i+2 < expr.length() && expr.substring(i, i+3).equals("log")){str="log";i+=2;}
				else if (i+2 < expr.length() && expr.substring(i, i+3).equals("ln2")){str=""+compute.compute.ln2;i+=2;}
			}
			else str+= this.expr.charAt(i);
			tokens.add(str);
		}
		
	}
}
