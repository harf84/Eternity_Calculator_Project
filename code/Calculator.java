import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * @author Fadi Hariri
 * This class pipelines the queue of tokens fed by CalculatorGui through a series of methods that converts
 * the infix tokens queue into a postfix queue that is then evaluated by the evalTokens () function call. 
 * This class contains the compute attribute, an instance of the Compute class, to calculate transcendental functions.
 *
 */
public class Calculator {

	private static HashMap<String, Integer> precedence = new HashMap<>();
	private Queue postfix;
	Compute compute = new Compute ();//will compute all functions

	public Calculator (){initialize();}

	///set expression and evaluate 
	/**
	 * @param tokens queue 
	 * @return String output of evaluated expression
	 * Converts tokens infix queue to postfix queue then evaluates the expression.
	 */
	public String evalTokens (Queue <String> tokens){
		postfix = infixToPostfix(tokens);
		postfix.forEach(System.out::print);
		System.out.println();
		double d = evaluate();
		return d+"";	
	}

	//infix to postfix
	//convert an infix queue to a postfix queue
	private Queue infixToPostfix(Queue <String> infixTokenQueue){
		Queue <String> postfixQueue = new LinkedList<>();//to store postfix tokens
		Stack <String> opStack = new Stack<>();//to store intermediate operators
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

	/**
	 * @return Value of expression after evaluating infixQueue
	 * The expression is computed based on operator priority. Special functions are computed using an instance of
	 * the Compute class.
	 */
	public double evaluate (){
		double val = 0;
		Stack <Double> operands = new Stack<>();
		if (postfix.size() == 1)operands.push(Double.parseDouble((String) postfix.poll()));
		while (!postfix.isEmpty()){
			String op = (String) postfix.poll();
			try {
				operands.push(Double.parseDouble(op));
				continue;
			}catch (NumberFormatException e){
                System.out.println(e.getMessage());
            }

            switch (op) {
                case "!": //factorial
                    try {
                        val = compute.factorial(operands.pop());
                    } catch (StackOverflowError e) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Error!");
                        alert.setHeaderText("Error!");
                        alert.setContentText("Error encountered in expression...");
                        alert.showAndWait();
                    }
                    break;
                case "sqrt":
                    val = compute.squareRoot(operands.pop());
                    break;
                case "sin":
                    val = compute.sin(operands.pop());
                    break;
                case "log":
                    val = compute.log10(operands.pop());
                    break;
                default:
                    val = calculate(op.charAt(0), operands.pop(), operands.pop());
                    break;
            }
			operands.push(val);

		}
		return operands.pop();
	}

	private double calculate (char op, double a, double b){
		double val = 0;
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