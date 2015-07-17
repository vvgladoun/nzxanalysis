package client.interfaces;

import com.sun.org.apache.bcel.internal.generic.NEW;
import javafx.geometry.VerticalDirection;
import server.Company;
import server.Portfolio;
import server.User;
import server.integration.JdbcCompanyDAOImpl;
import server.integration.JdbcPortfolioDAOImpl;
import server.integration.JdbcUserDAOImpl;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Window for portfolios management
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public class PortfolioManager extends JFrame {

    //list of all companies
    private java.util.List<Company> companiesList = (new JdbcCompanyDAOImpl()).findAll();
    private DefaultListModel listCompanyModel = new DefaultListModel();
    private JList jlstCompanies = new JList(listCompanyModel);
    private JScrollPane jspCompaniesList = new JScrollPane(jlstCompanies);

    //list of portfolio's companies
    private java.util.List<Company> portfolioCompaniesList;
    private DefaultListModel listPortfolioCompanyModel = new DefaultListModel();
    private JList jlstPortfolioCompanies = new JList(listPortfolioCompanyModel);
    private JScrollPane jspPortfolioCompaniesList = new JScrollPane(jlstPortfolioCompanies);

    //list of portfolios
    private java.util.List<Portfolio> portfolioList;
    private DefaultListModel listPortfolioModel = new DefaultListModel();
    private JList jlstPortfolio = new JList(listPortfolioModel);
    private JScrollPane jspPortfolioList = new JScrollPane(jlstPortfolio);

    // labels and buttons
    private JButton jbtAddCompany = new JButton("<==");
    private JButton jbtRemoveCompany = new JButton("==>");
    private JButton jbtCreatePortfolio = new JButton("Create Portfolio");
    private JButton jbtDeletePortfolio = new JButton("Delete Portfolio");
    private JPanel portfolioPanel = new JPanel();
    private JPanel controlButtonsPanel = new JPanel();
    private JPanel createPortfolioPanel = new JPanel();
    private JPanel companyPortfolioPanel = new JPanel();
    private JPanel companiesPanel = new JPanel();
    private JPanel portfolioButtonsPanel = new JPanel();
    private JLabel jlbName = new JLabel("Name");
    private JTextField jtfName = new JTextField(10);
    private JLabel jlbPortfolio = new JLabel("Portfolios");
    private JLabel jlbCompaniesPortfolio = new JLabel("Companies in Portfolio");
    private JLabel jlbCompanies = new JLabel("Companies");

    // current user of the system
    private User currentUser;

    /**
     * Constructor for menu
     *
     * @param currentUser - current user of the system
     */
    public PortfolioManager(User currentUser){

        this.currentUser = currentUser;

        // set frame properties
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle("Portfolio Manager");
        this.setLocation(200, 200);
        updateLists();
        this.pack();
        this.setVisible(true);

        // add listeners
        jlstPortfolio.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updatePortfolioCompaniesList();
            }
        });
        jbtCreatePortfolio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPortfolio();
            }
        });
        jbtDeletePortfolio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePortfolio();
            }
        });
        jbtAddCompany.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCompanyToPortfolio();
            }
        });
        jbtRemoveCompany.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeCompanyFromPortfolio();
            }
        });

    }

    /**
     * update all lists on the menu
     */
    private void updateLists(){

        jlstPortfolio.setSelectionModel(new DefaultListSelectionModel());
        listPortfolioModel.clear();
        jlstPortfolio.setModel(listPortfolioModel);

        // add companies to list if needed
        if(listCompanyModel.getSize() == 0){
            for(Company company: companiesList){
                listCompanyModel.addElement(company.getName());
            }
        }
        // get user's portfolios
        portfolioList = (new JdbcPortfolioDAOImpl()).findByUserId(1);
        if(listPortfolioModel.getSize() == 0){
            for(Portfolio portfolio: portfolioList){
                listPortfolioModel.addElement(portfolio.getDescription());
            }
        }

        // format panels
        controlButtonsPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 5;
        c.weighty = 10;
        c.gridx = 0;
        c.gridy = 0;
        controlButtonsPanel.add(jbtAddCompany, c);
        c.gridy = 1;
        controlButtonsPanel.add(jbtRemoveCompany, c);
        portfolioPanel.setLayout(new BorderLayout(5, 5));
        portfolioPanel.add(jspPortfolioList, BorderLayout.CENTER);
        portfolioButtonsPanel.setLayout(new GridLayout(1, 2, 15, 5));
        portfolioButtonsPanel.add(jbtCreatePortfolio);
        portfolioButtonsPanel.add(jbtDeletePortfolio);
        createPortfolioPanel.setLayout(new GridLayout(3, 1));
        createPortfolioPanel.add(portfolioButtonsPanel);
        createPortfolioPanel.add(jlbName);
        createPortfolioPanel.add(jtfName);
        portfolioPanel.add(createPortfolioPanel, BorderLayout.SOUTH);
        portfolioPanel.add(jlbPortfolio, BorderLayout.NORTH);
        companyPortfolioPanel.setLayout(new BorderLayout(5,5));
        companyPortfolioPanel.add(controlButtonsPanel, BorderLayout.EAST);
        companyPortfolioPanel.add(jspPortfolioCompaniesList, BorderLayout.CENTER);
        companyPortfolioPanel.add(jlbCompaniesPortfolio, BorderLayout.NORTH);
        companiesPanel.setLayout(new BorderLayout(5,5));
        companiesPanel.add(jlbCompanies, BorderLayout.NORTH);
        companiesPanel.add(jspCompaniesList, BorderLayout.CENTER);

        //put panels on frame
        this.setLayout(new GridLayout(1, 3, 5, 5));
        this.add(portfolioPanel);
        this.add(companyPortfolioPanel);
        this.add(companiesPanel);
    }

    /**
     * Update list of companies (on menu form)
     */
    private void updatePortfolioCompaniesList(){

        jlstPortfolioCompanies.setSelectionModel(new DefaultListSelectionModel());
        listPortfolioCompanyModel.clear();


        // if no portfolio selected, return
        if (jlstPortfolio.getSelectedIndex() == -1) {
            return;
        }
        // get selected portfolio
        int index = portfolioList.get(jlstPortfolio.getSelectedIndex()).getId();
        // get list of companies by portfolio's id
        portfolioCompaniesList = (new JdbcCompanyDAOImpl()).findByPortfolioId(index);
        // clear list on form and add companies from data store
        listPortfolioCompanyModel.clear();
        for(Company company: portfolioCompaniesList){
            listPortfolioCompanyModel.addElement(company.getName());
        }
        jlstPortfolioCompanies.setModel(listPortfolioCompanyModel);
    }

    /**
     * create new portfolio
     */
    private void createPortfolio(){
        // if name is empty, show message
        if(jtfName.getText().length() == 0){
            JOptionPane.showMessageDialog(new JFrame(), "Name is not entered",
                    "Incorrect name", JOptionPane.ERROR_MESSAGE);
        }
        else{
            Portfolio portfolio = new Portfolio();
            portfolio.setDescription(jtfName.getText());
            portfolio.setId_user(currentUser.getId());
            (new JdbcPortfolioDAOImpl()).insertPortfolio(portfolio);
            portfolioList.add(portfolio);
            listPortfolioModel.addElement(portfolio.getDescription());
        }
    }

    /**
     * delete portfolio
     */
    private void deletePortfolio(){
        // if no portfolio is selected, return
        if (jlstPortfolio.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(new JFrame(), "Please select Portfolio first",
                    "Select portfolio", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // get selected portfolio
        Portfolio portfolio = portfolioList.get(jlstPortfolio.getSelectedIndex());
        (new JdbcPortfolioDAOImpl()).deletePortfolio(portfolio);

        // update lists
        updateLists();
        updatePortfolioCompaniesList();
    }

    /**
     * add selected company to portfolio's
     * list of companies
     */
    private void addCompanyToPortfolio(){
        // if no company is selected, return
        if (jlstCompanies.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(new JFrame(), "Please select Company first",
                    "Select company", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // get selected company
        Company company = companiesList.get(jlstCompanies.getSelectedIndex());

        // if no portfolio is selected, return
        if (jlstPortfolio.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(new JFrame(), "Please select Portfolio first",
                    "Select portfolio", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // get selected portfolio
        Portfolio portfolio = portfolioList.get(jlstPortfolio.getSelectedIndex());

        //get portfolio's companies
        java.util.List<Company> addCompany = portfolio.getCompanies();
        boolean check = false;
        // check if company is already in the list
        for(Company companies: addCompany){
            if (company.getId() == companies.getId()){
                JOptionPane.showMessageDialog(new JFrame(), "This Company is already in the Portfolio",
                        "Error", JOptionPane.ERROR_MESSAGE);
                check = true;
            }
        }
        // if company is not in the list, add it
        if(!check) {
            addCompany.add(company);
            portfolio.setCompanies(addCompany);
            // update portfolio in data store
            (new JdbcPortfolioDAOImpl()).updatePortfolio(portfolio);
            listPortfolioCompanyModel.addElement(company.getName());
        }
        // update list
        updatePortfolioCompaniesList();
    }

    /**
     * remove selected company from portfolio's
     * list of companies
     */
    private void removeCompanyFromPortfolio(){
        // if no company is selected, return
        if (jlstCompanies.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(new JFrame(), "Please select Company first",
                    "Select company", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // get selected company
        Company company = portfolioCompaniesList.get(jlstPortfolioCompanies.getSelectedIndex());

        // if no portfolio is selected, return
        if (jlstPortfolio.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(new JFrame(), "Please select Portfolio first",
                    "Select portfolio", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // get selected portfolio
        Portfolio portfolio = portfolioList.get(jlstPortfolio.getSelectedIndex());
        //get portfolio's companies
        java.util.List<Company> addCompany = portfolio.getCompanies();
        // remove selected company from the list
        addCompany.remove(jlstPortfolioCompanies.getSelectedIndex());
        addCompany.remove(company);
        portfolio.setCompanies(addCompany);
        // update portfolio in data store
        (new JdbcPortfolioDAOImpl()).updatePortfolio(portfolio);
        // update list
        listPortfolioCompanyModel.removeElement(company.getName());
        updatePortfolioCompaniesList();
    }
}
