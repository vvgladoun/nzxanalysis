package client.interfaces;

import server.User;
import server.integration.JdbcUserDAOImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Alex on 19/05/2015.
 */
public class LoggIn extends JDialog {

    private User user;

    private JLabel jlbLogin = new JLabel("Login");
    private JLabel jlbPass = new JLabel("Password");

    private JTextField jtfLogin = new JTextField();
    private JPasswordField jpsPass = new JPasswordField();

    private JButton jbtOK = new JButton("OK");
    private JButton jbtCancel = new JButton("Cancel");

    public LoggIn(MainMenu menu, JFrame frame, String title) {
        super(frame, title);
        setLocationRelativeTo(null);
        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayout(2, 2, 5, 5));
        panel1.add(jlbLogin);
        panel1.add(jtfLogin);
        panel1.add(jlbPass);
        panel1.add(jpsPass);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());
        panel2.add(jbtOK);
        panel2.add(jbtCancel);

        setLayout(new BorderLayout());
        add(panel1, BorderLayout.CENTER);
        add(panel2, BorderLayout.SOUTH);
        pack();
        setSize(300, 130);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(1);
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                System.exit(1);
            }
        });


        jbtOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkEnteredData(menu);
            }
        });

        jbtCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
    }

    private boolean checkEnteredData(MainMenu menu) {
        String login = jtfLogin.getText();
        String pass = String.copyValueOf(jpsPass.getPassword());
        user = (new JdbcUserDAOImpl()).findByLogin(login);
        if (user != null) {
            if (user.getPassword().equals(pass)) {
                JOptionPane.showMessageDialog(new JFrame(), "Enter as " + jtfLogin.getText(),
                            "Enters successfully", JOptionPane.INFORMATION_MESSAGE);
                setVisible(false);
                menu.makeAllUnavailable(true);
                menu.setCurrentUser(user);
                return true;
            }
            JOptionPane.showMessageDialog(new JFrame(), "Incorrect login or password",
                    "Enters unsuccessfully", JOptionPane.ERROR_MESSAGE);
            jpsPass.setText("");
            return false;
        }
        JOptionPane.showMessageDialog(new JFrame(), "Incorrect login or password",
                "Enters unsuccessfully", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}
