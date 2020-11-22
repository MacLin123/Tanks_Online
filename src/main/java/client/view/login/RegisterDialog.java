package client.view.login;

import client.model.BClient;
import client.model.Client;
import client.model.IClient;
import client.model.Login;
import client.view.BClientGuiF;
import client.view.ClientGuiF;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class RegisterDialog extends JDialog {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JLabel lbUsername;
    private JLabel lbPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private boolean succeeded;

    public RegisterDialog(Frame parent) {
        super(parent, "Register", true);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;

        lbUsername = new JLabel("Username: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);


        tfUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        lbPassword = new JLabel("Password: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        pfPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);
        panel.setBorder(new LineBorder(Color.GRAY));

        btnRegister = new JButton("Register");

        btnRegister.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                IClient iClient = BClient.getInstance();
                ClientGuiF cGF = BClientGuiF.getInstance();
                try {

                    if (iClient.register(cGF.getIp(), cGF.getPort(), getUsername(), getPassword())) {
                        JOptionPane.showMessageDialog(RegisterDialog.this,
                                "Hi " + getUsername() + "! You have successfully registered.",
                                "Register",
                                JOptionPane.INFORMATION_MESSAGE);
                        succeeded = true;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(RegisterDialog.this,
                                "user with this name already exists",
                                "Register",
                                JOptionPane.ERROR_MESSAGE);
                        // reset username and password
                        tfUsername.setText("");
                        pfPassword.setText("");
                        succeeded = false;

                    }
                } catch (IOException exc) {
                    System.out.println(exc.getMessage());
                }
            }
        });
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JPanel bp = new JPanel();
        bp.add(btnRegister);
        bp.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public String getUsername() {
        return tfUsername.getText().trim();
    }

    public String getPassword() {
        return new String(pfPassword.getPassword());
    }
}
