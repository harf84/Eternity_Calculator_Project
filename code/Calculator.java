/**
 *@author:fadihariri
 *@description: arithmetic expression pipelining backbone
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Calculator {
	public static void main(String[] args) {
			// TODO Auto-generated method stub
            /**
             * Add as many expressions as you can to expr.txt to quality assure the outputs
             * Supported functions: +-/*! == != < > <= >=
             */
			Calculator c = new Calculator ();
			Scanner kb = null;
			try {
				kb = new Scanner (new BufferedReader(new FileReader(new File("expr.txt"))));
				while (kb.hasNextLine()){
					c.setExpression(kb.nextLine().trim());
				}
			}catch (FileNotFoundException e){
				e.printStackTrace();
			}finally {if (kb != null)kb.close();}
			/**
		c.setExpression("-1+2!=3+3!");
		c.setExpression("1*2-3/5");
		c.setExpression("(1+3)*3!");
			 */
		}
		//==+==+==+==+==+==+==+Class definition goes here+==+==+==+==+==+==+==+
		private static HashMap <String, Integer> precedence = new HashMap();
		private String expression;//store the expression
		private double result;//store the result
		private Queue <String>postfix;
		private boolean isBooleanExpression =false;
		private boolean booleanResult;

		public Calculator (){initialize();}

		///set expression
		public void setExpression (String expr){
			this.expression=expr;
			postfix = infixToPostfix(tokenize());

			for (String s: postfix)System.out.print(s);
			double d = evaluate();
			if(isBooleanExpression)System.out.println("\nEvaluated: "+booleanResult);
			else System.out.println("\nEvaluated: "+d);
			isBooleanExpression =false;
		}

		//tokenize
		private Queue <String> tokenize (){
			Queue <String> st = new LinkedList ();
			for (int i= 0; i < this.expression.length(); i++){
				if (this.expression.charAt(i) == ' ')continue;
				String str="";
				if (Character.isDigit(this.expression.charAt(i))){
					while (i < this.expression.length() && (Character.isDigit(this.expression.charAt(i))
							|| this.expression.charAt(i) == '.')){
						str+= this.expression.charAt(i);i++;
					}
					i--;
				}
				else if (this.expression.charAt(i) == '<' || this.expression.charAt(i) == '>'
						|| this.expression.charAt(i) == '=' || this.expression.charAt(i) == '!'){
					str+= this.expression.charAt(i);
					if (i+1 < this.expression.length() &&
							!Character.isDigit(this.expression.charAt(i+1))){i++;str+= this.expression.charAt(i);}

				}
				else str+= this.expression.charAt(i);
				st.add(str);
			}
			return st;
		}

		//infix to postfix
		//convert an infix queue to a postfix queue
		private Queue infixToPostfix(Queue <String> infixTokenQueue){
			Queue <String> postfixQueue = new LinkedList ();//to store postfix tokens
			Stack <String> opStack = new Stack();//to store intermediate operators

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
					postfixQueue.add(token);
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
					char op = opStack.pop().charAt(0);
					while (!opStack.isEmpty() && op != '('){
						postfixQueue.add(op+"");
						op = opStack.pop().charAt(0);
					}
				}

				//else token must be an operator
				else{
					while (!opStack.isEmpty() && opStack.peek().charAt(0) != '(' && !token.equals("(")
							&& precedence.get(token) >= precedence.get(opStack.peek())){
						//System.out.println (token+"; "+opStack.peek().charAt(0));
						postfixQueue.add(opStack.peek());
						opStack.pop();

					}
					opStack.push(token);
				}
				infixTokenQueue.poll();
			}
			while (!opStack.isEmpty()){
				postfixQueue.add(opStack.peek());
				opStack.pop();
			}
			return postfixQueue;
		}

		//evaluate
		public double evaluate (){
			double val = 0;
			Stack <Double> operands = new Stack ();
			while (!postfix.isEmpty()){
				String op = postfix.poll();
				try {
					operands.push(Double.parseDouble(op));
					//System.out.println (operands.peek());
					continue;
				}catch (NumberFormatException e){}

				if (op.equals("!")){//factorial
					val =factorial(operands.pop());
				}
				else if (op.equals("<")||op.equals("<=")||op.equals(">")
						||op.equals(">=")||op.equals("==")||op.equals("!=")){
					evaluateBoolean(op, operands.pop(), operands.pop());
				}
				else val=calculate (op.charAt(0), operands.pop(), operands.pop());
				operands.push(val);

			}
			return operands.pop();
		}

		private void evaluateBoolean (String op, double a, double b){
			isBooleanExpression =true;
			if (op.equals("<")){
				this.booleanResult =b < a;
			}
			else if (op.equals(">")){
				this.booleanResult =b > a;
			}
			else if (op.equals("<=")){
				this.booleanResult =b <= a;
			}
			else if (op.equals(">=")){
				this.booleanResult =b >= a;
			}
			else if (op.equals("==")){
				this.booleanResult =(b == a);
			}
			else if (op.equals("!=")){
				this.booleanResult =(b != a);
			}

		}
		private double factorial (double val){
			if (val == 1)return 1;
			return val *factorial(val-1);
		}
		private double calculate (char op, double a, double b){
			//System.out.println(b+""+op+" "+a);
			double val=0;
			if (op == '+')val+= b+a;
			else if (op == '-')val+= b-a;
			else if (op == '*')val+= b*a;
			else if (op == '/')val+= b/a;
			else if (op == '^')val+= Math.pow(b, a);//here we will call our function that evaluates power
			return val;
		}

		private static void initialize (){
			//1 is highest --> 8 is lowest
            //we will add precedence to trascendental function evaluation
			precedence.put("!",2 );
			precedence.put("^", 4);precedence.put("*",5 );
			precedence.put("/",5 );precedence.put("+", 6);
			precedence.put("-",6 );
			precedence.put("<",7 );precedence.put(">",7 );
			precedence.put(">=",7 );precedence.put("<=",7 );
			precedence.put("==",8 );precedence.put("!=",8 );
		}
	}
