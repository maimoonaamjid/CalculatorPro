package calculatorpro;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CalculatorPro extends Application {

    private TextField display = new TextField();
    private boolean darkMode = true;
    private VBox root;

    @Override
    public void start(Stage stage) {

        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setPrefHeight(70);
        display.setText("0");
        display.setPadding(new Insets(10));
        display.setStyle("-fx-font-size: 26px; -fx-background-radius: 12;");

        ToggleButton themeBtn = new ToggleButton("☀ Light Mode");
        themeBtn.setOnAction(e -> {
            darkMode = !darkMode;
            themeBtn.setText(darkMode ? "☀ Light Mode" : "🌙 Dark Mode");
            applyTheme();
        });

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        String[][] buttons = {
            {"sin", "cos", "tan", "√"},
            {"ln", "log", "1/x", "%"},
            {"7", "8", "9", "/"},
            {"4", "5", "6", "*"},
            {"1", "2", "3", "-"},
            {"±", "0", ".", "+"},
            {"π", "x²", "C", "="}
        };

        for (int r = 0; r < buttons.length; r++) {
            for (int c = 0; c < buttons[r].length; c++) {
                Button btn = createButton(buttons[r][c]);
                grid.add(btn, c, r);
            }
        }

        root = new VBox(20, themeBtn, display, grid);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        applyTheme();

        Scene scene = new Scene(root, 380, 550);
        stage.setTitle("Calculator Pro");
        stage.setScene(scene);
        stage.show();
    }

    // ---------- BUTTON FACTORY ----------

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(78, 58);
        btn.setStyle("""
            -fx-font-size: 14px;
            -fx-background-radius: 14;
            -fx-cursor: hand;
        """);
        
        if (text.equals("=")) {
    btn.setStyle(btn.getStyle() + "-fx-font-weight: bold;");
}


        DropShadow shadow = new DropShadow(8, Color.rgb(0, 0, 0, 0.25));
        btn.setEffect(shadow);

        ScaleTransition press = new ScaleTransition(Duration.millis(90), btn);
        press.setToX(0.93);
        press.setToY(0.93);

        ScaleTransition release = new ScaleTransition(Duration.millis(90), btn);
        release.setToX(1);
        release.setToY(1);

        btn.setOnMousePressed(e -> press.playFromStart());
        btn.setOnMouseReleased(e -> release.playFromStart());

        btn.setOnAction(e -> handle(text));
        return btn;
        
    }

    // ---------- LOGIC ----------

    private void handle(String t) {
        try {
            switch (t) {
                case "C" -> display.setText("0");
                case "=" -> display.setText(String.valueOf(eval(display.getText())));
                case "√" -> display.setText(String.valueOf(Math.sqrt(val())));
                case "x²" -> display.setText(String.valueOf(Math.pow(val(), 2)));
                case "%" -> display.setText(String.valueOf(val() / 100));
                case "π" -> append(Math.PI);
                case "sin" -> display.setText(String.valueOf(Math.sin(Math.toRadians(val()))));
                case "cos" -> display.setText(String.valueOf(Math.cos(Math.toRadians(val()))));
                case "tan" -> display.setText(String.valueOf(Math.tan(Math.toRadians(val()))));
                case "ln" -> display.setText(String.valueOf(Math.log(val())));
                case "log" -> display.setText(String.valueOf(Math.log10(val())));
                case "1/x" -> display.setText(String.valueOf(1 / val()));
                case "±" -> display.setText(String.valueOf(-val()));
                default -> append(t);
            }
        } catch (Exception e) {
            display.setText("Error");
        }
    }

   private void append(Object v) {
    String t = display.getText();
    if (t.equals("0")) display.setText("");

    if ("+-*/".contains(v.toString()) &&
        "+-*/".contains(t.substring(t.length()-1))) {
        return;
    }
    display.appendText(v.toString());
}


    private double val() {
        return Double.parseDouble(display.getText());
    }

    // ---------- EXPRESSION PARSER ----------

    private double eval(String expr) {
        return new Object() {
            int pos = -1, ch;

            void next() { ch = (++pos < expr.length()) ? expr.charAt(pos) : -1; }

            double parse() {
                next();
                return expr();
            }

            double expr() {
                double x = term();
                while (true) {
                    if (ch == '+') { next(); x += term(); }
                    else if (ch == '-') { next(); x -= term(); }
                    else return x;
                }
            }

            double term() {
                double x = factor();
                while (true) {
                    if (ch == '*') { next(); x *= factor(); }
                    else if (ch == '/') { next(); x /= factor(); }
                    else return x;
                }
            }

            double factor() {
                if (ch == '+') { next(); return factor(); }
                if (ch == '-') { next(); return -factor(); }
                int start = pos;
                while ((ch >= '0' && ch <= '9') || ch == '.') next();
                return Double.parseDouble(expr.substring(start, pos));
            }
        }.parse();
    }

    // ---------- THEMES ----------

    private void applyTheme() {
        if (darkMode) {
            root.setStyle("-fx-background-color: #121212;");
            display.setStyle("-fx-background-color: #1f1f1f; -fx-text-fill: white; -fx-font-size: 26px;");
        } else {
            root.setStyle("-fx-background-color: #f5f5f5;");
            display.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 26px;");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
