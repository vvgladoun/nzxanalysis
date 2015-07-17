package client.interfaces;

import server.Company;
import server.integration.JdbcCompanyDAOImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Window for companies management
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public class CompanyDescription extends JFrame{

    //list of companies
    private java.util.List<Company> companiesList = (new JdbcCompanyDAOImpl()).findAll();
    private DefaultListModel listModel = new DefaultListModel();
    private JList jlstCompanies = new JList(listModel);
    private JScrollPane jspList = new JScrollPane(jlstCompanies);

    // area for description
    private JTextArea jtxDescription = new JTextArea();

    //control buttons
    private JButton jbtSave = new JButton("Save");
    private JButton jbtReload = new JButton("Reload");
    private JButton jbtCreate = new JButton("Create");
    private JButton jbtDelete = new JButton("Delete");

    // code and name
    private JLabel jlbCode = new JLabel("Company Code");
    private JLabel jlbName = new JLabel("Company Name");
    private JTextField jtxCode = new JTextField(30);
    private JTextField jtxName = new JTextField(30);

    //panels to arrange components
    private JPanel companyPanel2 = new JPanel();
    private JPanel companyPanel1 = new JPanel();
    private JPanel content = new JPanel();
    private JPanel controlButtons = new JPanel();

    /**
     * Default constructor
     */
    public CompanyDescription(){

        // add buttons
        controlButtons.setLayout(new FlowLayout(FlowLayout.CENTER));
        controlButtons.add(jbtSave);
        controlButtons.add(jbtReload);
        controlButtons.add(jbtCreate);
        controlButtons.add(jbtDelete);

        // add panels and fields
        companyPanel1.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        companyPanel1.add(jlbName, c);
        c.gridx = 1;
        c.gridy = 0;
        companyPanel1.add(jtxName, c);
        c.gridx = 0;
        c.gridy = 1;
        companyPanel1.add(jlbCode, c);
        c.gridx = 1;
        c.gridy = 1;
        companyPanel1.add(jtxCode, c);

        //set frame properties
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle("Companies Description");
        this.setLocation(200, 200);
        this.setLayout(new BorderLayout());
        this.add(controlButtons, BorderLayout.SOUTH);

        // arrange components
        jtxDescription.setPreferredSize(new Dimension(400, 300));
        jtxDescription.setWrapStyleWord(true);
        jtxDescription.setLineWrap(true);

        companyPanel2.setLayout(new BorderLayout());
        companyPanel2.add(companyPanel1, BorderLayout.NORTH);
        companyPanel2.add(new JScrollPane(jtxDescription), BorderLayout.CENTER);
        content.setLayout(new BorderLayout());
        content.add(companyPanel2, BorderLayout.CENTER);
        content.add(jspList, BorderLayout.WEST);
        // add on frame
        this.add(content, BorderLayout.CENTER);
        updateList();

        this.pack();
        this.setVisible(true);

        // declare listeners
        jbtReload.addActionListener(e -> {
            showDescription();
        });
        jbtSave.addActionListener(e -> {
            int index = jlstCompanies.getSelectedIndex();
            saveDescription(index);
        });
        jlstCompanies.addListSelectionListener(e -> {
            showDescription();
        });
        jbtCreate.addActionListener(e -> createCompany());
        jbtDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = jlstCompanies.getSelectedIndex();
                deleteCompany(index);
            }
        });
    }

    /**
     * Get array of companies' names
     *
     * @param companies list of companies
     * @return array of names
     */
    private String[] getNames(java.util.List<Company> companies)
    {
        // for each company from list get name and input to array
        String[] companiesNames = new String[companies.size()];
        int i = 0;
        for(Company company: companies){
            companiesNames[i] = company.getName();
            i++;
        }
        return companiesNames;
    }

    /**
     * Save company
     * Updates company in data store
     *
     * @param index - company's index in list
     */
    private void saveDescription(int index){
        //if no company is selected, show message
        if (index == -1) {
            JOptionPane.showMessageDialog(new JFrame(), "Please select Company first",
                    "Select company", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // find company in a list
        Company company = companiesList.get(index);
        // set company's info
        company.setDescription(jtxDescription.getText());
        company.setCode(jtxCode.getText());
        company.setName(jtxName.getText());
        // update in data store
        (new JdbcCompanyDAOImpl()).updateCompany(company);
    }

    /**
     * Show company's info on frame
     */
    private void showDescription(){
        int index = jlstCompanies.getSelectedIndex();
        //if no company is selected
        if (index == -1) {
            jtxCode.setText("");
            jtxName.setText("");
            jtxDescription.setText("");
            return;
        }

        // find company in a list
        Company company = companiesList.get(index);
        // show info
        jtxCode.setText(company.getCode());
        jtxName.setText(company.getName());
        this.jtxDescription.setText(company.getDescription());
    }

    /**
     * Delete company
     * Removes company from data store
     *
     * @param index - company's index in list
     */
    private void deleteCompany(int index){
        //if no company is selected, show message
        if (index == -1) {
            JOptionPane.showMessageDialog(new JFrame(), "Please select Company first",
                    "Select company", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Company company = companiesList.get(index);
        // delete from data store
        boolean deleted = (new JdbcCompanyDAOImpl()).deleteCompany(company);
        // update companies' list on frame
        updateList();

        //jlstCompanies.setSelectedIndex(0);
        showDescription();
    }

    /**
     * Create company
     * Adds company to data store
     *
     */
    private void createCompany(){
        Company company = new Company();
        company.setName(jtxName.getText());
        company.setCode(jtxCode.getText());
        company.setDescription(jtxDescription.getText());
        (new JdbcCompanyDAOImpl()).insertCompany(company);
        listModel.addElement(company.getName());

        updateList();
        //jlstCompanies.setSelectedIndex(0);
        showDescription();
    }

    /**
     * update list of companies
     */
    private void updateList() {

        jlstCompanies.setSelectionModel(new DefaultListSelectionModel());
        listModel.clear();

        //get companies from data store
        companiesList = (new JdbcCompanyDAOImpl()).findAll();
        // if list on frame is empty, add elements
        if(listModel.getSize() == 0) {
            for (Company company : companiesList) {
                listModel.addElement(company.getName());
            }
        }

        jlstCompanies.setModel(listModel);



    }
}
