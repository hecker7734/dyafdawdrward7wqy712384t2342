package calculator;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class JCalc extends JFrame implements ActionListener {

    private JTextField display;
    private JButton buttons[];
    private String operators[] = {"+", "-", "/", "*"};
    private StringBuilder currentExpression = new StringBuilder();

    public JCalc() {
        super("Calculator");

        display = new JTextField(20);
        display.setEditable(false);

        int numberButtons = 10;
        buttons = new JButton[numberButtons + operators.length + 3];

        for (int i = 0; i < numberButtons; i++) {
            buttons[i] = new JButton("" + i);
            buttons[i].addActionListener(this);
        }

        for (int i = 0; i < operators.length; i++) {
            buttons[numberButtons + i] = new JButton(operators[i]);
            buttons[numberButtons + i].addActionListener(this);
        }

        buttons[numberButtons + operators.length] = new JButton("Backspace");
        buttons[numberButtons + operators.length].addActionListener(this);

        buttons[numberButtons + operators.length + 1] = new JButton("=");
        buttons[numberButtons + operators.length + 1].addActionListener(this);
        
        buttons[numberButtons + operators.length + 2] = new JButton("Clear");
        buttons[numberButtons + operators.length + 2].addActionListener(this);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 4));
        for (JButton button : buttons) {
            buttonPanel.add(button);
        }

        getContentPane().add(display, BorderLayout.NORTH);
        getContentPane().add(buttonPanel, BorderLayout.CENTER);

        setSize(300, 300);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String buttonText = e.getActionCommand();

        if (buttonText.matches("[0-9.]")) {
            currentExpression.append(buttonText);
            display.setText(currentExpression.toString());
        } else if (buttonText.equals("Backspace")) {
            if (currentExpression.length() > 0) {
                currentExpression.deleteCharAt(currentExpression.length() - 1);
                display.setText(currentExpression.toString());
            }
        } else if (buttonText.equals("=")) {
            try {
                double result = eval(currentExpression.toString());
                display.setText("" + result);
                currentExpression.setLength(0);
            } catch (Exception ex) {
                display.setText("Error");
                currentExpression.setLength(0);
            }
        } else if (buttonText.equals("Clear")) {
            currentExpression.setLength(0);
            display.setText("");
        } else {
            currentExpression.append(buttonText);
            display.setText(currentExpression.toString());
        }
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());
                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        new JCalc();
    }
}
