package client.interfaces;

import server.User;
import server.integration.JdbcUserDAOImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Alex on 24/05/2015.
 */
public class MainMenu extends JApplet{

    private User currentUser;

    private JButton jbtCompany = new JButton();
    private JButton jbtImport = new JButton();
    private JButton jbtMethods = new JButton();
    private JButton jbtPortfolio = new JButton();
    private JButton jbtQuotes = new JButton();
    private JButton jbtUsers = new JButton();

    private Icon icoCompany = new ImageIcon("src/images/company.png");
    private Icon icoImport = new ImageIcon("src/images/import.png");
    private Icon icoMethods = new ImageIcon("src/images/methods.png");
    private Icon icoPortfolio = new ImageIcon("src/images/portfolio.png");
    private Icon icoQuotes = new ImageIcon("src/images/quotes.png");
    private Icon icoUsers = new ImageIcon("src/images/user.png");

    private JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu = new JMenu("File");
    private JMenu helpMenu = new JMenu("Help");
    private JMenu toolsMenu = new JMenu("Tools");
    private JMenuItem closeItem = new JMenuItem("Close");
    private JMenuItem changeUserItem = new JMenuItem("Change User");
    private JMenuItem importItem = new JMenuItem("Import");
    private JMenuItem methodsItem = new JMenuItem("Methods");
    private JMenuItem portfolioItem = new JMenuItem("Portfolio");
    private JMenuItem quotesItem = new JMenuItem("Quotes");
    private JMenuItem usersItem = new JMenuItem("Users");
    private JMenuItem companiesItem = new JMenuItem("Companies");
    private JMenuItem aboutItem = new JMenuItem("About");
    private JMenuItem helpItem = new JMenuItem("Help");




    public MainMenu(){
//        makeAllUnavailable(false);
//        LoggIn logg = new LoggIn(this, new JFrame(), "Logg In to the system");
        setCurrentUser((new JdbcUserDAOImpl().findById(2)));
        //buttons creating
        jbtCompany.setIcon(icoCompany);
        jbtCompany.setText("Companies");
        jbtCompany.setHorizontalAlignment(SwingConstants.LEFT);
        jbtCompany.setHorizontalTextPosition(SwingConstants.RIGHT);
        jbtPortfolio.setIcon(icoPortfolio);
        jbtPortfolio.setText("Portfolio");
        jbtPortfolio.setHorizontalAlignment(SwingConstants.LEFT);
        jbtPortfolio.setHorizontalTextPosition(SwingConstants.RIGHT);
        jbtQuotes.setIcon(icoQuotes);
        jbtQuotes.setText("Quotes");
        jbtQuotes.setHorizontalAlignment(SwingConstants.LEFT);
        jbtQuotes.setHorizontalTextPosition(SwingConstants.RIGHT);
        jbtMethods.setIcon(icoMethods);
        jbtMethods.setText("Methods");
        jbtMethods.setHorizontalAlignment(SwingConstants.LEFT);
        jbtMethods.setHorizontalTextPosition(SwingConstants.RIGHT);

        jbtImport.setIcon(icoImport);
        jbtImport.setText("Import");
        jbtImport.setHorizontalAlignment(SwingConstants.LEFT);
        jbtImport.setHorizontalTextPosition(SwingConstants.RIGHT);
        jbtUsers.setIcon(icoUsers);
        jbtUsers.setText("Users");
        jbtUsers.setHorizontalAlignment(SwingConstants.LEFT);
        jbtUsers.setHorizontalTextPosition(SwingConstants.RIGHT);

//        jbtUserAdd.setIcon(icoUser_add);
//        jbtUserAdd.setText("Add User");
//        jbtUserAdd.setHorizontalAlignment(SwingConstants.LEFT);
//        jbtUserAdd.setHorizontalTextPosition(SwingConstants.RIGHT);
//        jbtUserRemove.setIcon(icoUser_remove);
//        jbtUserRemove.setText("Remove User");
//        jbtUserRemove.setHorizontalAlignment(SwingConstants.LEFT);
//        jbtUserRemove.setHorizontalTextPosition(SwingConstants.RIGHT);
//        jbtEditUser.setIcon(icoEdit_user);
//        jbtEditUser.setText("Edit User");
//        jbtEditUser.setHorizontalAlignment(SwingConstants.LEFT);
//        jbtEditUser.setHorizontalTextPosition(SwingConstants.RIGHT);
//        jbtEditPass.setIcon(icoEdit_pass);
//        jbtEditPass.setText("Edit Password");
//        jbtEditPass.setHorizontalAlignment(SwingConstants.LEFT);
//        jbtEditPass.setHorizontalTextPosition(SwingConstants.RIGHT);

        //first column panel
        JPanel workspace = new JPanel();
        workspace.setLayout(new GridLayout(3, 2, 5, 5));
        workspace.add(jbtCompany);
        workspace.add(jbtPortfolio);
        workspace.add(jbtQuotes);
        workspace.add(jbtMethods);
        workspace.add(jbtImport);
        workspace.add(jbtUsers);

        //second column panel
//        JPanel administration = new JPanel();
//        administration.setLayout(new GridLayout(4, 1, 5, 5));
//        administration.add(jbtImport);
//        administration.add(jbtUsers);
//        administration.add(jbtUserAdd);
//        administration.add(jbtUserRemove);
//        administration.add(jbtEditUser);
//        administration.add(jbtEditPass);

//        java.awt.EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                logg.toFront();
//                logg.repaint();
//            }
//        });

        fileMenu.add(changeUserItem);
        fileMenu.addSeparator();
        fileMenu.add(closeItem);
        toolsMenu.add(companiesItem);
        toolsMenu.add(quotesItem);
        toolsMenu.add(methodsItem);
        toolsMenu.addSeparator();
        toolsMenu.add(portfolioItem);
        toolsMenu.add(usersItem);
        toolsMenu.add(importItem);
        helpMenu.add(helpItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        setLayout(new BorderLayout());
        add(workspace, BorderLayout.CENTER);
//        add(administration);

        jbtQuotes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                QuotesTable quotes = new QuotesTable();
            }
        });

        jbtCompany.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CompanyDescription description = new CompanyDescription();
            }
        });

        jbtImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImportData importData = new ImportData();
            }
        });
        jbtUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UsersDescription userDescription = new UsersDescription(currentUser);
            }
        });
        jbtPortfolio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PortfolioManager portfolioManager = new PortfolioManager(currentUser);
            }
        });
        closeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        changeUserItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoggIn change = new LoggIn(null, new JFrame(), "Logg In to the system");

            }
        });
        companiesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CompanyDescription description = new CompanyDescription();
            }
        });
        quotesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                QuotesTable quotes = new QuotesTable();
            }
        });
        usersItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UsersDescription userDescription = new UsersDescription(currentUser);
            }
        });
        importItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImportData importData = new ImportData();
            }
        });
        methodsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        portfolioItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PortfolioManager portfolioManager = new PortfolioManager(currentUser);
            }
        });
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        helpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

    }

    public static void main(String[] args) {
        MainMenu applet = new MainMenu();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("NZX-Analytic");
        frame.getContentPane().add(applet, BorderLayout.CENTER);
        applet.init();
        applet.start();
        frame.setSize(380, 400);
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
    }

    protected void makeAllUnavailable(boolean status){
        jbtUsers.setEnabled(status);
        jbtCompany.setEnabled(status);
        jbtImport.setEnabled(status);
        jbtMethods.setEnabled(status);
        jbtPortfolio.setEnabled(status);
        jbtQuotes.setEnabled(status);
    }

    protected void setCurrentUser(User user){
        currentUser = user;
    }

}
