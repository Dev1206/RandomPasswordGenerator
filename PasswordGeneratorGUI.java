import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PasswordGeneratorGUI extends JFrame {
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]|,./?><";

    private static final SecureRandom random = new SecureRandom();

    private JTextField lengthField;
    private JCheckBox lowerCaseCheckBox;
    private JCheckBox upperCaseCheckBox;
    private JCheckBox digitsCheckBox;
    private JCheckBox specialCheckBox;
    private JTextArea passwordArea;
    private JLabel strengthLabel;
    private JLabel errorLabel;
    private JComboBox<String> complexityComboBox;
    private List<String> passwordHistory;

    public PasswordGeneratorGUI() {
        setTitle("Random Password Generator");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(10, 2, 5, 5));


        panel.add(new JLabel("Length:"));
        lengthField = new JTextField();

        panel.add(lengthField);

        panel.add(new JLabel("Include lowercase letters?"));
        lowerCaseCheckBox = new JCheckBox();
        panel.add(lowerCaseCheckBox);

        panel.add(new JLabel("Include uppercase letters?"));
        upperCaseCheckBox = new JCheckBox();
        panel.add(upperCaseCheckBox);

        panel.add(new JLabel("Include digits?"));
        digitsCheckBox = new JCheckBox();
        panel.add(digitsCheckBox);

        panel.add(new JLabel("Include special characters?"));
        specialCheckBox = new JCheckBox();
        panel.add(specialCheckBox);

        panel.add(new JLabel("Complexity Level:"));
        String[] complexityLevels = {"Low", "Medium", "High"};
        complexityComboBox = new JComboBox<>(complexityLevels);
        panel.add(complexityComboBox);

        JButton generateButton = new JButton("Generate Password");
        generateButton.setToolTipText("Generate Password");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generatePassword();
            }
        });
        panel.add(generateButton);

        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.setToolTipText("Copy to Clipboard");
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard();
            }
        });
        panel.add(copyButton);

        JButton clearButton = new JButton("Clear");
        clearButton.setToolTipText("Clear");

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
        panel.add(clearButton);

        passwordArea = new JTextArea();
        passwordArea.setEditable(false);

        panel.add(passwordArea);

        strengthLabel = new JLabel();

        panel.add(strengthLabel);

        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        panel.add(errorLabel);

        passwordHistory = new ArrayList<>();

        add(panel);
    }

    private void generatePassword() {
        int length;
        try {
            length = Integer.parseInt(lengthField.getText());
            if (length <= 0) {
                showError("Length should be a positive integer.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for length.");
            return;
        }

        boolean includeLower = lowerCaseCheckBox.isSelected();
        boolean includeUpper = upperCaseCheckBox.isSelected();
        boolean includeDigits = digitsCheckBox.isSelected();
        boolean includeSpecial = specialCheckBox.isSelected();

        if (!includeLower && !includeUpper && !includeDigits && !includeSpecial) {
            showError("Please select at least one character type.");
            return;
        }

        String generatedPassword = generatePassword(length, includeLower, includeUpper, includeDigits, includeSpecial);
        passwordArea.setText(generatedPassword);
        updatePasswordStrength(generatedPassword);
        addToHistory(generatedPassword);
        clearError();
    }

    private void copyToClipboard() {
        String password = passwordArea.getText();
        if (!password.isEmpty()) {
            StringSelection selection = new StringSelection(password);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            JOptionPane.showMessageDialog(this, "Password copied to clipboard.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearFields() {
        lengthField.setText("");
        lowerCaseCheckBox.setSelected(false);
        upperCaseCheckBox.setSelected(false);
        digitsCheckBox.setSelected(false);
        specialCheckBox.setSelected(false);
        passwordArea.setText("");
        strengthLabel.setText("");
        clearError();
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void clearError() {
        errorLabel.setText("");
    }

    private void updatePasswordStrength(String password) {
        int length = password.length();
        int strength = 0;
        if (length >= 8) {
            strength++;
        }
        if (password.matches(".*[a-z].*")) {
            strength++;
        }
        if (password.matches(".*[A-Z].*")) {
            strength++;
        }
        if (password.matches(".*\\d.*")) {
            strength++;
        }
        if (password.matches(".*[!@#$%^&*()_+\\-=[\\]{}|:;,.<>?].*")) {
            strength++;
        }
        switch (strength) {
            case 0:
            case 1:
                strengthLabel.setText("Weak");
                strengthLabel.setForeground(Color.RED);
                break;
            case 2:
            case 3:
                strengthLabel.setText("Moderate");
                strengthLabel.setForeground(Color.ORANGE);
                break;
            case 4:
            case 5:
                strengthLabel.setText("Strong");
                strengthLabel.setForeground(Color.GREEN);
                break;
        }
    }

    private void addToHistory(String password) {
        passwordHistory.add(password);
    }

    private static String generatePassword(int length, boolean includeLower, boolean includeUpper, boolean includeDigits, boolean includeSpecial) {
        StringBuilder password = new StringBuilder(length);
        String chars = "";
        if (includeLower) chars += LOWER;
        if (includeUpper) chars += UPPER;
        if (includeDigits) chars += DIGITS;
        if (includeSpecial) chars += SPECIAL;

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            password.append(chars.charAt(randomIndex));
        }

        return password.toString();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PasswordGeneratorGUI().setVisible(true);
            }
        });
    }
}
