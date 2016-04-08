package iteration1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * @author Fadi Hariri
 * @date January 12, 2016
 * @Description: This class pipelines the queue of tokens fed by CalculatorGui through a series of methods that converts 
 * the infix tokens queue into a postfix queue that is then evaluated by the evalTokens () function call. 
 * This class contains the compute attribute, an instance of the Compute class, to calculate transcendental functions.
 *
 */
public class Calculator {

	//==+==+==+==+==+==+==+Class definition goes here+==+==+==+==+==+==+==+
	private static HashMap <String, Integer> precedence = new HashMap();
	private Queue <String>postfix;
	Compute compute = new Compute ();//will compute all functions

	public Calculator (){initialize();}

	///set expression and evaluate 
	/**
	 * @param tokens queue 
	 * @return String output of evaluated expression
	 * @Description Converts tokens infix queue to postfix queue then evaluates the expression.
	 */
	public String evalTokens (Queue <String> tokens){
		postfix = infixToPostfix(tokens);
		for (String s : postfix)System.out.print(s);System.out.println();
		double d = evaluate();
		return d+"";	
	}

	//infix to postfix
	//convert an infix queue to a postfix queue
	private Queue infixToPostfix(Queue <String> infixTokenQueue){
		Queue <String> postfixQueue = new LinkedList ();//to store postfix tokens
		Stack <String> opStack = new Stack();//to store intermediate operators
		boolean isNegative = false;
		int i=1;//track first element
		while (!infixTokenQueue.isEmpty()){
			String token = infixTokenQueue.peek();
			//first token is - sign 
			if (i++ == 1 && token.charAt(0) == '-'){
				infixTokenQueue.poll();
				postfixQueue.add('-'+infixTokenQueue.poll());
				continue;
			}

			//if (token is an operand)
			if (Character.isDigit(token.charAt(0))){
				if (isNegative){
					postfixQueue.add("-"+token);
					isNegative=false;
				}
				else { 
					postfixQueue.add(token);}
			}

			//(token is a left parenthesis)
			else if (token.charAt(0) == '('){
				opStack.push(token);
				infixTokenQueue.poll();
				if (infixTokenQueue.peek().charAt(0) == '-'){
					infixTokenQueue.poll();
					postfixQueue.add('-'+infixTokenQueue.poll());
				}
				continue;
			}
			//(token is a right parenthesis)
			else if (token.charAt(0) == ')'){
				String op = opStack.pop();
				while (!opStack.isEmpty() && op.charAt(0) != '('){
					postfixQueue.add(op);
					op = opStack.pop();
				}
			}
			
			//else token must be an operator
			else{
				while (!opStack.isEmpty() && opStack.peek().charAt(0) != '('
						&& precedence.get(token) >= precedence.get(opStack.peek())){
					//System.out.println (token+"; "+opStack.peek().charAt(0));
					postfixQueue.add(opStack.peek());
					opStack.pop();

				}
				opStack.push(token);
			}
			infixTokenQueue.poll();
			
			if (token.charAt(0) == '^' && infixTokenQueue.peek().charAt(0) == '-'){
				infixTokenQueue.poll();
				isNegative=true;
			}
		}
		while (!opStack.isEmpty()){
			postfixQueue.add(opStack.peek());
			opStack.pop();
		}
		return postfixQueue;
	}

	//evaluate
	/**
	 * 
	 * @return Value of expression after evaluating infixQueue
	 * @Description The expression is computed based on operator priority. Special functions are computed using an instance of
	 * the Compute class.
	 * @throws IllegalExpressionException if the expression is erroneuosly constructed.
	 */
	public double evaluate (){
		double val = 0;
		Stack <Double> operands = new Stack ();
		if (postfix.size() == 1)operands.push(Double.parseDouble(postfix.poll()));
		while (!postfix.isEmpty()){
			String op = postfix.poll();
			try {
				operands.push(Double.parseDouble(op));
				continue;
			}catch (NumberFormatException e){}

			if (op.equals("!")){//factorial
				try {val =compute.factorial(operands.pop());}
				catch(java.lang.StackOverflowError e){
					Alert alert = new Alert (AlertType.ERROR);
					alert.setTitle("Error!");alert.setHeaderText("Error!");
					alert.setContentText("Error encountered in expression...");
					alert.showAndWait();
				}
			}
			else if (op.equals("sqrt")){
				val=compute.squareRoot(operands.pop());
			}
			else if (op.equals("sin")){
				//System.out.print ("sin"+operands.peek()+"=");
				val=compute.sin(operands.pop());
				//System.out.println (val);
			}

			else if (op.equals("log")){
				val=compute.log10(operands.pop());
			}

			else val=calculate (op.charAt(0), operands.pop(), operands.pop());
			operands.push(val);

		}
		return operands.pop();
	}


	private double calculate (char op, double a, double b){
		//System.out.println(b+""+op+" "+a);
		double val=0;
		if (op == '+')val+= b+a;
		else if (op == '-')val+= b-a;
		else if (op == '*')val+= b*a;
		else if (op == '/')val+= b/a;
		else if (op == '^')val+= compute.powerOfX(b, a);
		else if (op == '%')val+= b%a;
		return val;
	}

	private static void initialize (){
		//1 is highest --> 8 is lowest
		precedence.put("!",1);precedence.put("sqrt", 1);
		precedence.put("sin", 1);precedence.put("log", 1);
		precedence.put("^", 2);precedence.put("*",3);
		precedence.put("/",3 );precedence.put("%",3 );
		precedence.put("+", 4);precedence.put("-",4);
	}
}
