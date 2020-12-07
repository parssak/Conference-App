package ui.view;

import javax.swing.*;
import java.awt.*;

/**
 * View for creating a new user account
 */
public class SignupView extends JPanel implements View{
    private JTextField usernameField = new JTextField();
    private JTextField nameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JPasswordField confirmPasswordField = new JPasswordField();
    private JButton signUpButton = new JButton("Sign Up");

    public SignupView() {
        setLayout(new GridBagLayout());
        GridBagConstraints cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 0;
        cst.fill = GridBagConstraints.BOTH;
        cst.insets = new Insets(7, 7, 7, 7);

        signUpButton.addActionListener(e -> signUp());

        add(new JLabel("Username: "),cst);
        cst.gridx = 1;
        add(usernameField,cst);
        cst.gridx=0;
        cst.gridy++;
        add(new JLabel("Name: "),cst);
        cst.gridx = 1;
        add(nameField,cst);
        cst.gridx=0;
        cst.gridy++;
        add(new JLabel("Password"), cst);
        cst.gridx = 1;
        add(passwordField, cst);
        cst.gridx=0;
        cst.gridy++;
        add(new JLabel("Confirm Password: "),cst);
        cst.gridx = 1;
        add(confirmPasswordField, cst);
        cst.gridx=0;
        cst.gridy++;
        cst.gridwidth=2;
        add(signUpButton, cst);

    }


    /**
     * Triggered when user presses sign up button.
     */
    private void signUp(){
        String username = usernameField.getText();
        String name = nameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // TODO SEND THIS STUFF TO PRESENTER; CHECK IF VALID INFO AND CREATE ACCOUNT
    }

    @Override
    public String getViewName() {
        return "Sign Up";
    }
}
