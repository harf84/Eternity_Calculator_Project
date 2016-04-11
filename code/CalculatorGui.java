import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author Fadi Hariri
 * Main class for front end Calculator User Interface.
 * This class represents the main entry point to the Eternity application with the launch(args) function call in the public main
 * method.  CalculatorGui encompasses a set of user interface JavaFx attributes [buttons, input field, window etc..] integral to
 * properly launch the graphic user interface. Furthermore, the input field collects user input in the format of a mathematical
 * expression; this current version of Eternity supports input via button clicks. Inputted expressions are then parsed to generate
 * a queue of tokens via the tokenize() function call and fed to a Calculator object.
 */

public class CalculatorGui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Gui Attributes
     */
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();

    final Menu optionsMenu = new Menu("Options");//skin and copy to clipboard
    final Menu helpMenu = new Menu("Help");// show a descriptive about section
    final Menu historyMenu = new Menu("History"); // show history of expressions executed in current session
    final Menu loadFile = new Menu("Load");
    protected MenuBar menuBar = new MenuBar();

    protected Calculator calculate = new Calculator();//to calculate stuff!
    protected String expr = "";//expression to be evaluated
    protected LinkedList<String> tokens = new LinkedList<>();
    protected final int ALLOC = 22;
    protected boolean evaluated = false;
    protected String result = "0";

    protected TextField input = new TextField("");
    protected GridPane grid = new GridPane();
    protected Button[] mainPad = new Button[30];
    protected String[] mainLabel = {
            "log\u2081\u2080X", "0", ".", "C", "+", "=",
            "10\u02e3", "1", "2", "3", "-", "Ans",
            "X\u02b8", "4", "5", "6", "*", "%",
            "√x", "7", "8", "9", "÷", "1/x",
            "Sin", "π", "ln2", "(", ")", "X!"};

    protected HashMap<String, String> func = new HashMap<>();
    protected LinkedHashMap<String, String> historyMap = new LinkedHashMap<>();

    /* (non-Javadoc)
     * Main logic goes in start method which launches a javafx application with a non-editable input field and a grid of buttons.
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set up Menu bar
        Menu skin = new Menu("Browse Skin");
        // sub skin menus
        MenuItem skin1 = new MenuItem("Calm", new ImageView(new Image("images/calmModule.jpg", 40, 40, false, false)));
        MenuItem skin2 = new MenuItem("Red Hot", new ImageView(new Image("images/redModule.jpg", 40, 40, false, false)));
        MenuItem skin3 = new MenuItem("Phantom Blue", new ImageView(new Image("images/phantomModule.jpg", 40, 40, false, false)));
        skin.getItems().addAll(skin1, skin2, skin3);

        MenuItem clipButton = new MenuItem("Copy to Clipboard");
        clipButton.setOnAction(e -> {
            content.putString(input.getText());
            clipboard.setContent(content);
        });

        MenuItem guide = new MenuItem("Quick Guide");
        guide.setOnAction(e -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.getDialogPane().setPrefSize(600, 400);
            alert.setTitle("User Manual");
            alert.setHeaderText("Tips and Tricks");
            String message = "Congratulations for purchasing our app!\nHere are a few tips for a fluid user experience."
                    + "\n\n1) Customize your interface by selecting the appropriate skin.\n"
                    + "2) Copy recently entered expression to clipboard by selecting the copy to clipboard menu.\n"
                    + "3) Show session history to track computed expressions and trace errors.\n"
                    + "4) Trigonometry functions treats all input in degrees.\n"
                    + "5) Avoid syntax errors. Use parenthesis. Avoid compound statements e.g. 3(5)-5\n"
                    + "6) When evaluating expressions from a *.txt file, please note the following accepted function syntax:\n"
                    + "sin, log, ^, !, sqrt\n"
                    + "7) For any problems with this version, please contact our customer support team at:\n"
                    + "   customer_services@calculeternity.ca";
            alert.setContentText(message);
            alert.showAndWait();
        });

        MenuItem showHistory = new MenuItem("Show History");
        showHistory.setOnAction(e -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Session History");
            alert.setHeaderText("History:");
            alert.getDialogPane().setPrefSize(550, 300);
            String message;
            if (historyMap.isEmpty()) {
                message = "New session started. There are no expressions to display.\n\n";
            } else {
                message = "";
                for (Object key : historyMap.keySet()) {
                    message += (String.format("%-40s----->      %s%n", key, historyMap.get(key)));
                }
            }
            alert.setContentText(message);
            alert.showAndWait();
        });

        MenuItem openBrowser = new MenuItem("Open File");
        openBrowser.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile == null) return;
            if (!selectedFile.toString().split("\\.")[1].equals("txt")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Error!");
                alert.setContentText("Illegal File Format...Please input a *.txt File...");
                alert.showAndWait();
            } else {
                Scanner kb = null;
                try {
                    LinkedHashMap<String, String> data = new LinkedHashMap<>();
                    kb = new Scanner(new BufferedReader(new FileReader(selectedFile)));
                    while (kb.hasNextLine()) {
                        expr = kb.nextLine();//load expression
                        String currInput = expr;
                        try {
                            tokenize();// tokenize it
                            String res = calculate.evalTokens(tokens);
                            tokens = new LinkedList<>();
                            expr = "";
                            historyMap.put(currInput, res);
                            data.put(currInput, res);
                        } catch (Exception myException) {
                            historyMap.put(currInput, "Syntax Error!");
                            data.put(currInput, "Syntax Error!");
                            tokens = new LinkedList<>();
                            expr = "";
                        }
                    }

                    //no output a file
                    int ind = selectedFile.toString().lastIndexOf('/');
                    File output = new File(selectedFile.toString().substring(0, ind + 1) + "output.txt");
                    FileWriter wr = null;
                    try {
                        wr = new FileWriter(output);
                        for (String str : data.keySet()) {
                            wr.write(String.format("%-40s----->     %s%n", str, data.get(str)));
                        }
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.getDialogPane().setPrefSize(500, 300);
                        alert.setTitle("Success!");
                        alert.setHeaderText("Computation Complete...");
                        alert.setContentText("Output file written: " + output.toString());
                        alert.showAndWait();
                    } catch (IOException exception) {
                        System.out.println(exception.getMessage());
                    } finally {
                        try {
                            if (wr != null) wr.close();
                        } catch (IOException exception) {
                            System.out.println(exception.getMessage());
                        }
                    }
                } catch (FileNotFoundException exception) {
                    System.out.println(exception.getMessage());
                } finally {
                    if (kb != null) kb.close();
                }
            }
        });

        optionsMenu.getItems().add(skin);
        optionsMenu.getItems().add(clipButton);
        helpMenu.getItems().add(guide);
        historyMenu.getItems().add(showHistory);
        loadFile.getItems().add(openBrowser);
        menuBar.getMenus().addAll(optionsMenu, helpMenu, historyMenu, loadFile);

        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
        }
        else if (os != null && os.startsWith ("Win")){
            menuBar.setTranslateY(-150);
        }

		/*
         * background image from:
		 * source:http://www.freelargeimages.com/black-background-1923/
		 * inputLabel image from:
		 * source: http://www.freelargeimages.com/wp-content/uploads/2014/12/Black_background-8.jpg
		 */

        initializeFuncMap();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        //input label
        setInput("0");
        input.setEditable(false);
        input.setPrefWidth(550);
        input.setPrefHeight(90);
        input.setId("input");

        //set up buttons
        //main Pad
        HBox[] boxes = new HBox[5];
        GridPane grid2 = new GridPane();
        grid2.setHgap(10);
        grid2.setVgap(10);
        int row = 20;
        int index = 0;
        for (int i = 0; i < mainPad.length; i++) {

            if (i % 6 == 0) {
                boxes[index] = new HBox(10);
                grid2.add(boxes[index], 0, row--);
                index++;
            }

            mainPad[i] = new Button(mainLabel[i]);
            mainPad[i].setPrefSize(100, 100);
            mainPad[i].setPrefSize(100, 100);

            Button btn = mainPad[i];
            btn.setOnAction(e -> parseAndEvaluate(e, btn));
            boxes[index - 1].getChildren().add(mainPad[i]);
        }

        //View
        GridPane grid3 = new GridPane();
        grid3.add(input, 1, 0);
        grid.add(grid3, 0, 0);
        grid.add(grid2, 0, 0);
        grid.setMaxHeight(Control.USE_PREF_SIZE);

        Scene scene = new Scene(grid, 600, 600);
        grid.getChildren().add(menuBar);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Eternity Calculator App");

        setSheet(scene, "styles/gui.css");
        skin1.setOnAction(e -> setSheet(scene, "styles/calm.css"));
        skin2.setOnAction(e -> setSheet(scene, "styles/redhot.css"));
        skin3.setOnAction(e -> setSheet(scene, "styles/gui.css"));

        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void setSheet(Scene scene, String resource) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(CalculatorGui.class.getResource(resource).toExternalForm());
    }

    /**
     * @param st, a String input
     * This method will update the input field.
     */
    private void setInput(String st) {
        String str = func.containsKey(st) ? func.get(st) : st;
        String str2 = str;

        if (str.equals("powX") || str.equals("pow10") || str.equals("sqrt") || str.equals("log") || str.equals("/") || str.equals("pi")) {
            str = func.get(str);
        }

        if (!evaluated) {

            String s = (input.getText().equals("0") && (Character.isDigit(str.charAt(0))
                    || str2.equals("sqrt") || str2.equals("sin") || str2.equals("pi") ||
                    str2.equals("log") || str2.equals("ln2") || str2.equals("("))) ? "" : input.getText();
            input.setText(s + str);
            expr = (input.getText().equals("0") && Character.isDigit(str.charAt(0))) ? str2 : expr + str2;

        } else {
            String inputText = input.getText();
            if (Character.isDigit(str.charAt(0)) || str2.equals("sqrt") || str2.equals("sin") || str2.equals("pi") ||
                    str2.equals("log") || str2.equals("ln2") || str2.equals("(")) {
                input.setText(str);
                expr = str2;
            } else {
                this.input.setText(inputText + str);
                expr += inputText + str2;
            }
            evaluated = false;
        }

    }

    /**
     * Initializes special symbol HashMap
     */
    private void initializeFuncMap() {
        //numbers
        for (int i = 0; i < 10; i++) {
            func.put(i + "", i + "");
        }

        //special characters
        func.put("*", "*");
        func.put("-", "-");
        func.put(".", ".");
        func.put("+", "+");
        func.put("÷", "/");
        func.put("%", "%");

        //equal, 1/x, !, (, )
        func.put("X!", "!");
        func.put("(", "(");
        func.put(")", ")");
        func.put("1/x", "1/");
        func.put("=", "=");

        //transcendental functions and special symbols
        func.put("ln2", "ln2");
        func.put("π", "pi");
        func.put("Sin", "sin");
        func.put("log\u2081\u2080X", "log");
        func.put("√x", "sqrt");
        func.put("10\u02e3", "pow10");
        func.put("X\u02b8", "powX");
        func.put("powX", "^");
        func.put("pow10", "10^");
        func.put("sqrt", "√");
        func.put("log", "log\u2081\u2080");
        func.put("/", "÷");
        func.put("pi", "π");
    }

    /**
     * parseAndEvaluate runs on a daemon thread to handle any button pressed and dynamically set input field
     */
    private void parseAndEvaluate(ActionEvent e, Button btn) {
        /**
         * Build Hierarchy of events
         */
        String btnText = btn.getText();
        if (btnText.equals("C")) {
            input.setText("0");
            evaluated = false;
            expr = "";
            tokens = new LinkedList<>();
        }
        else if (btnText.equals("Ans")){
            setInput(result);
        }
        else if (input.getText().length() == ALLOC || btnText.equals("=")) {
            if (evaluated && btnText.equals("=")) {
                input.setText(result);
            }
            else if (btnText.equals("=")){
                String currInput = input.getText();
                try {
                    tokenize();
                    String res = calculate.evalTokens(tokens);
                    input.setText(res);
                    result = res;
                    evaluated = true;
                    expr = "";
                    tokens = new LinkedList<>();
                    historyMap.put(currInput, result);
                } catch(ComputationException ex){
                    historyMap.put(currInput, "Computation Error!");
                    showAlert("Computation Error!", ex.getMessage());
                }
                catch (Exception myException) {
                    historyMap.put(currInput, "Syntax Error!");
                    showAlert("Error!", "The entered expression has a syntax error...");
                }
            }
        } else {
            setInput(btnText);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
        input.setText("0");
        expr = "";
        tokens = new LinkedList<>();
    }

    /**
     * tokenize: converts a string expression to a queue of tokens.
     */
    private void tokenize() {
        char firstChar = expr.charAt(0);
        for (int i = 0; i < this.expr.length(); i++) {
            char currChar = expr.charAt(i);
            if (currChar == ' ') continue;

            String token = "";
            boolean startsWithExpression
                    = firstChar == '.' || firstChar == '*' || firstChar == '/' || expr.startsWith("powX") || firstChar == '%';

            if (startsWithExpression) {
                expr = "0" + expr;
            }

            if (Character.isDigit(currChar)) {
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    token += expr.charAt(i);
                    i++;
                }
                i--;
            } else if (currChar == 'p' || currChar == 's' || currChar == 'l') {
                if (i + 3 < expr.length() && expr.substring(i, i + 4).equals("powX")) {
                    token = "^";
                    i += 3;
                } else if (i + 4 < expr.length() && expr.substring(i, i + 5).equals("pow10")) {
                    tokens.add("10");
                    token = "^";
                    i += 4;
                } else if (i + 1 < expr.length() && expr.substring(i, i + 2).equals("pi")) {
                    token = "" + calculate.compute.pi;
                    i += 1;
                } else if (i + 3 < expr.length() && expr.substring(i, i + 4).equals("sqrt")) {
                    token = "sqrt";
                    i += 3;
                } else if (i + 2 < expr.length() && expr.substring(i, i + 3).equals("sin")) {
                    token = "sin";
                    i += 2;
                } else if (i + 2 < expr.length() && expr.substring(i, i + 3).equals("log")) {
                    token = "log";
                    i += 2;
                } else if (i + 2 < expr.length() && expr.substring(i, i + 3).equals("ln2")) {
                    token = "" + calculate.compute.ln2;
                    i += 2;
                }
            }
            else {
                token += expr.charAt(i);
            }
            tokens.add(token);
        }
    }
}
