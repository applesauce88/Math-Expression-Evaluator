package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;

	/**
	 * Scalar symbols in the expression
	 */
	ArrayList<ScalarSymbol> scalars;

	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;

	/**
	 * String containing all delimiters (characters other than variables and
	 * constants), to be used with StringTokenizer
	 */
	public static final String delims = " \t*+-/()[]";

	/**
	 * Initializes this Expression object with an input expression. Sets all
	 * other fields to null.
	 * 
	 * @param expr
	 *            Expression
	 */
	public Expression(String expr) {
		this.expr = expr;
	}

	/**
	 * Populates the scalars and arrays lists with symbols for scalar and array
	 * variables in the expression. For every variable, a SINGLE symbol is
	 * created and stored, even if it appears more than once in the expression.
	 * At this time, values for all variables are set to zero - they will be
	 * loaded from a file in the loadSymbolValues method.
	 * 
	 */

	public void buildSymbols() {
		/** COMPLETE THIS METHOD **/

		String a = expr;
		int counter = 0;

		arrays = new ArrayList<ArraySymbol>();
		ArrayList<String> ArrayVariables = new ArrayList<String>();
		for (int i = 0; i < a.length(); i++) {
			// System.out.println(i);
			if (a.charAt(i) == '[') {
				counter = i;
				counter--;
				while (counter != -1 && Character.isLetter(a.charAt(counter))) {
					counter--;
				}
				counter++;
				// System.out.println(counter);
				ArrayVariables.add(a.substring(counter, i));
				ArraySymbol d = new ArraySymbol(a.substring(counter, i));
				arrays.add(d);
			}
		}
		for (String b : ArrayVariables) {
//			System.out.println("array:" + b + ":");
			a = a.substring(0, a.indexOf(b)) + a.substring(a.indexOf(b) + b.length(), a.length());
//			System.out.println("detail: " + a);
		} // method to print out these: ArrayVariables.forEach(e ->
			// System.out.print(e));

		// System.out.println(":" + a);

		// take out operators
		a = a.replaceAll("[*/+-]", " ");
		a = a.replaceAll("\\(", " ");
		a = a.replaceAll("\\)", " ");
		a = a.replaceAll("\\[", " ");
		a = a.replaceAll("\\]", " ");
		// System.out.println(a);

		// removes numbers
		for (int i = 0; i < a.length(); i++) {
			if (Character.isDigit(a.charAt(i))) {
				a = a.substring(0, i) + a.substring(i + 1, a.length());
				i--;
				// System.out.println(a);
			}
		}
		// System.out.println("result:" + a);

		scalars = new ArrayList<ScalarSymbol>();

		a = a + " ";
		/*
		 * for(String b:a.split(" ")){ if(b.length()>0) scalars.add(new
		 * ScalarSymbol("b")); }
		 */
		while (a.length() > 0) {
			if (a.charAt(0) == ' ') {
				a = a.substring(1);
			} else {

				ScalarSymbol e = new ScalarSymbol(a.substring(0, a.indexOf(' ')));
				scalars.add(e);
				a = a.substring(a.indexOf(' ') + 1);
//				System.out.println(e.name);
			}
		}
//		System.out.println("input: " + expr);
//		arrays.forEach(e -> System.out.println(e.name));
//		scalars.forEach(e -> System.out.print(e.name + ": "));

	}

	/**
	 * Loads values for symbols in the expression
	 * 
	 * @param sc
	 *            Scanner for values input
	 * @throws IOException
	 *             If there is a problem with the input
	 */
	public void loadSymbolValues(Scanner sc) throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String sym = st.nextToken();
			ScalarSymbol ssymbol = new ScalarSymbol(sym);
			ArraySymbol asymbol = new ArraySymbol(sym);
			int ssi = scalars.indexOf(ssymbol);
			int asi = arrays.indexOf(asymbol);
			if (ssi == -1 && asi == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { // scalar symbol
				scalars.get(ssi).value = num;
			} else { // array symbol
				asymbol = arrays.get(asi);
				asymbol.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					String tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok, " (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					asymbol.values[index] = val;
				}
			}
		}
	}

	/**
	 * Evaluates the expression, using RECURSION to evaluate subexpressions and
	 * to evaluate array subscript expressions.
	 * 
	 * @return Result of evaluation
	 * 
	 */

	public float evaluate() {
		String expressionUsed = expr.replaceAll("-", "#");

		return eval(expressionUsed);

	}

	private float eval(String expressionUsed) {
		if (expressionUsed.contains("(")) {
			// implement recursive cases
			int level = 0;
			int start = 0;
			int end = 0;
			for (int i = 0; i < expressionUsed.length(); i++) {
				// System.out.println("i" + i + " " + expressionUsed.charAt(i));
				if (expressionUsed.charAt(i) == '(') {
					level++;
					start = i + 1;
					int subcounter = 1;
					while (i + subcounter < expressionUsed.length()) {
						// System.out.println(i + subcounter + "" +
						// expressionUsed.charAt(i + subcounter));
						if (expressionUsed.charAt(i + subcounter) == '(') {
							level++;
						}
						if (expressionUsed.charAt(i + subcounter) == ')') {
							level--;
						}
						if (level == 0) {
							end = i + subcounter;
//							System.out.print("BEF: " + expressionUsed.substring(0, start - 1));
//							System.out.print("EVthis: " + expressionUsed.substring(start, end));
//							System.out.println("AFT: " + expressionUsed.substring(end + 1));
							expressionUsed = expressionUsed.substring(0, start - 1) + ""
									+ eval(expressionUsed.substring(start, end)) + expressionUsed.substring(end + 1);
//							System.out.println("returned: " + expressionUsed);
							i--;
							break;
							// subcounter=subcounter-start+end-1;
						}
						subcounter++;
					}
				}
			}
		}
		if (expressionUsed.contains("[")) {
			// implement recursive cases
			int level = 0;
			int start = 0;
			int end = 0;
			for (int i = 0; i < expressionUsed.length(); i++) {
				// System.out.println("i" + i + " " + expressionUsed.charAt(i));
				if (expressionUsed.charAt(i) == '[') {
					level++;
					start = i + 1;
					int subcounter = 1;
					while (i + subcounter < expressionUsed.length()) {
						// System.out.println(i + subcounter + "" +
						// expressionUsed.charAt(i + subcounter));
						if (expressionUsed.charAt(i + subcounter) == '[') {
							level++;
						}
						if (expressionUsed.charAt(i + subcounter) == ']') {
							level--;
						}
						if (level == 0) {
							end = i + subcounter;
//							System.out.print("BEF: " + expressionUsed.substring(0, start - 1));
//							System.out.print("EVthis: " + expressionUsed.substring(start, end));
//							System.out.println("AFT: " + expressionUsed.substring(end + 1));
							String temp = "" + eval(expressionUsed.substring(start, end));
							expressionUsed = expressionUsed.substring(0, start) + temp.substring(0, temp.indexOf("."))
									+ expressionUsed.substring(end);
//							System.out.println("returned: " + expressionUsed);

							break;
							// subcounter=subcounter-start+end-1;
						}
						subcounter++;
					}
				}
			}
		}
		/////////////////////////////////////////////////////////////////
		Stack<String> terms = new Stack<String>();
		Stack<String> operators = new Stack<String>();

		Pattern p = Pattern.compile("[-`a-zA-Z0-9\\.\\[\\]]+");
		Matcher m = p.matcher(expressionUsed);
		Pattern op = Pattern.compile("[\\+\\*#/]");
		Matcher m2 = op.matcher(expressionUsed.replaceAll("\\.", "1"));
//		System.out.println("cant find this:" + expressionUsed);

		/*
		 * while (m.find()){ System.out.println("m: "+m.group()); } while
		 * (m2.find()){ System.out.println("m2: "+m2.group()); }
		 */

		// printing method
		/*
		 * System.out.println("terms"); while (m.find()){
		 * System.out.print(m.group()); } System.out.println("----");
		 * System.out.println("operators");
		 * 
		 * while (m2.find()){ System.out.print(m2.group()); }
		 * System.out.println();
		 */

		String x;
		String y = "";
		String z;

		// to calculate all the * and /
		m.find();
		x = m.group();

		// System.out.println("but what about here" + x);
		// if x is a scalar turn into literal number
		if (determineType(x) == 1) {
			// System.out.println("please dont go here");
			x = scalarGet(x) + "";
		} else if (determineType(x) == 2) { // if x is an array process into
											// literal number
			// assume here that the recursive part of the program turns
			// arguments into integers
			x = arraysGet(x.substring(0, x.indexOf('[')),
					Integer.parseInt(x.substring(x.indexOf('[') + 1, x.indexOf(']', x.indexOf('[') + 1)))) + "";
		}
		terms.push(x);
		while (m.find() && m2.find()) {
			y = m2.group();
			z = m.group();
			if (determineType(z) == 1) {
				z = scalarGet(z) + "";
			} else if (determineType(z) == 2) {
				z = arraysGet(z.substring(0, z.indexOf('[')),
						Integer.parseInt(z.substring(z.indexOf('[') + 1, z.indexOf(']', z.indexOf('[') + 1)))) + "";
			}

//			System.out.print("term1:" + terms.peek() + "  ");
//			System.out.print("op:" + y + "  ");
//			System.out.println("term2:" + z);

			if (y.equals("*")) {
//				System.out.print("multiply");
				String temp = (Float.valueOf(terms.pop()) * Float.valueOf(z)) + "";
				terms.push(temp);
//				System.out.println("result " + temp);
			} else if (y.equals("/")) {
//				System.out.println("divide");
				terms.push((Float.valueOf(terms.pop()) / Float.valueOf(z)) + "");
			} else {
				operators.push(y);
				terms.push(z);
			}
			// System.out.println("peek: " + terms.peek());
		}

//		System.out.println("-_-_-_-_-_-_-_-_-_-_-_-_-");
		// to calculate the expression with only + and -
		Stack<String> terms2 = new Stack<String>();
		Stack<String> operators2 = new Stack<String>();
		if (terms.size() > 1) {
			// System.out.println("A");
			while (terms.size() > 0) {
				terms2.push(terms.pop());
			}
			while (operators.size() > 0) {
				operators2.push(operators.pop());
			}
			x = terms2.pop();
			while (terms2.size() > 0) {
				// System.out.println("B");
				y = operators2.pop();
				z = terms2.pop();
				if (y.equals("+")) {
					x = ((Float.valueOf(x) + Float.valueOf(z)) + "");
				} else if (y.equals("#")) {
					x = ((Float.valueOf(x) - Float.valueOf(z)) + "");
				}
			}
			terms.push(x);
		}

		String ans = "";
		while (!terms.isEmpty()) {
			ans = terms.pop();
//			System.out.println("only one answer:" + ans);
		}
//		System.out.println("return:" + ans);
		return Float.valueOf(ans);

	}

	// return the index of the first target
	private int nextOperatorAt(char target, String expr) {
		int j;

		for (j = 0; j < expr.length(); j++) {
			if (expr.charAt(j) == target) {
				return j;
			}
		}
		return -1;

	}

	// 0 means literal number//1 means letter//2 means scalar//-1 means there is
	// a case i'm missing
	private int determineType(String x) {
		int g = -1;
		// System.out.println("method dT:" + x.substring(0,x.indexOf('[')));
		if (Character.isLetter(x.charAt(x.length() - 1)) == true) {
			// scalar
			g = 1;
		} else if (x.contains("[") == true) {
			// array
			g = 2;
		} else if (Character.isDigit((Float.valueOf(x) + "").charAt(x.length() - 1))) {
			g = 0;
		}
		// System.out.println("this is what g is: " + g + " for this x: " + x);
		return g;
	}

	private float scalarGet(String x) {
		return scalars.get(scalars.indexOf(new ScalarSymbol(x))).value;
	}

	private float arraysGet(String x, int a) {
		//System.out.println("index of a: " + a);
		// new ArraySymbol(x)
		return arrays.get(arrays.indexOf(new ArraySymbol(x))).values[a];
	}

	/**
	 * Utility method, prints the symbols in the scalars list
	 */
	public void printScalars() {
		for (ScalarSymbol ss : scalars) {
			System.out.println(ss);
		}
	}

	/**
	 * Utility method, prints the symbols in the arrays list
	 */
	public void printArrays() {
		for (ArraySymbol as : arrays) {
			System.out.println(as);
		}
	}

}
