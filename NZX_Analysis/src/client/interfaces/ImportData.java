package client.interfaces;

import server.Company;
import server.integration.CSVReader;
import server.integration.HttpDownloadUtility;
import server.integration.JdbcCompanyDAOImpl;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Alex on 31/05/2015.
 */
public class ImportData extends JFrame {
    private java.util.List<Company> comaniesList = (new JdbcCompanyDAOImpl()).findAll();
    private ComboBoxModel cbmCompaniesModel = new DefaultComboBoxModel(getNames(comaniesList));
    private JComboBox jcbCompanies = new JComboBox(cbmCompaniesModel);
    private JLabel jlbSelectCompany = new JLabel("Select Company");

    private JButton jbtImport = new JButton("Import");
    private JButton jbtCancel = new JButton("Cancel");

    private JPanel buttonsPanel = new JPanel();
    private JPanel selectPanel = new JPanel();
    private JPanel sourcePanel = new JPanel();
    private JPanel filePanel = new JPanel();
    private JPanel quotesPanel = new JPanel();
    private JPanel fileQuotesPanel = new JPanel();
    private JPanel fileCompanyPanel = new JPanel();
    private JPanel companyButtonPanel = new JPanel();

    private JRadioButton jrbInternet = new JRadioButton("Internet");
    private JRadioButton jrbCSVFile = new JRadioButton("CSV File");
    private JLabel jlbSource = new JLabel("Source");
    private JLabel jlbQuotesFileName = new JLabel("File Name  ");
    private JTextField jtfQuotesFileName = new JTextField(17);
    private JButton jbtChooseQuotesFile = new JButton("...");
    CSVReader csvReader = new CSVReader();
    private File companyFile = null;
    private File quotesFile = null;


    private JPanel companyImportPanel = new JPanel();
    private JButton jbtImportCompanies = new JButton("Import Companies");
    private JLabel jlbCompanyFileName = new JLabel("File Name  ");
    private JTextField jtfCompanyFileName = new JTextField(17);
    private JButton jbtChooseCompanyFile = new JButton("...");


    public ImportData(){

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle("Import Data");
        this.setLocation(200, 200);


        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(jbtImport);
        buttonsPanel.add(jbtCancel);

        selectPanel.setLayout(new FlowLayout());
        selectPanel.add(jlbSelectCompany);
        jcbCompanies.setPreferredSize(new Dimension(250, 30));
        jcbCompanies.setMinimumSize(new Dimension(250, 30));
        selectPanel.add(jcbCompanies);

        sourcePanel.setLayout(new GridLayout(1, 3, 5, 5));
        sourcePanel.add(jlbSource);
        sourcePanel.add(jrbInternet);
        sourcePanel.add(jrbCSVFile);

        fileQuotesPanel.setLayout(new FlowLayout());
        fileQuotesPanel.add(jlbQuotesFileName);
        fileQuotesPanel.add(jtfQuotesFileName);
        fileQuotesPanel.add(jbtChooseQuotesFile);

        ButtonGroup btgSource = new ButtonGroup();
        btgSource.add(jrbCSVFile);
        btgSource.add(jrbInternet);
        jrbCSVFile.setEnabled(false);
        jrbInternet.setEnabled(false);
        jtfQuotesFileName.setEnabled(false);
        jbtChooseQuotesFile.setEnabled(false);


        quotesPanel.setLayout(new GridLayout(4, 1));
        quotesPanel.add(sourcePanel);
        quotesPanel.add(fileQuotesPanel);
        quotesPanel.add(selectPanel);
        quotesPanel.add(buttonsPanel);

        TitledBorder title;
        title = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                "Import Quotes");
        title.setTitleJustification(TitledBorder.RIGHT);
        quotesPanel.setBorder(title);


        companyImportPanel.setLayout(new GridLayout(2, 1, 5, 5));
        fileCompanyPanel.setLayout(new FlowLayout());
        fileCompanyPanel.add(jlbCompanyFileName);
        fileCompanyPanel.add(jtfCompanyFileName);
        fileCompanyPanel.add(jbtChooseCompanyFile);
        companyImportPanel.add(fileCompanyPanel);
        companyButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        companyButtonPanel.add(jbtImportCompanies);
        companyImportPanel.add(companyButtonPanel);

        title = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                "Import Companies");
        title.setTitleJustification(TitledBorder.RIGHT);
        companyImportPanel.setBorder(title);
        this.setLayout(new BorderLayout(5, 10));
        this.add(quotesPanel, BorderLayout.CENTER);
        this.add(companyImportPanel, BorderLayout.NORTH);
        this.pack();
        this.setVisible(true);

        jbtChooseCompanyFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile(1);
                if (companyFile != null) {
                    jtfCompanyFileName.setText(companyFile.getAbsolutePath());
                }
            }
        });
        jbtImportCompanies.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadCompanies();
            }
        });
        jcbCompanies.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (jcbCompanies.getSelectedIndex() < 2) {
                    jrbCSVFile.setEnabled(false);
                    jrbInternet.setEnabled(false);
                } else {
                    jrbCSVFile.setEnabled(true);
                    jrbInternet.setEnabled(true);
                }
            }
        });
        jrbCSVFile.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (jrbCSVFile.isSelected()){
                    jtfQuotesFileName.setEnabled(true);
                    jbtChooseQuotesFile.setEnabled(true);
                } else {
                    jtfQuotesFileName.setEnabled(false);
                    jbtChooseQuotesFile.setEnabled(false);
                }
            }
        });
        jbtCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        jbtImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadData(jcbCompanies.getSelectedIndex());
            }
        });
        jbtChooseQuotesFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile(2);
                if(quotesFile != null){
                    jtfQuotesFileName.setText(quotesFile.getAbsolutePath());
                }
            }
        });

    }

    private String[] getNames(java.util.List<Company> companies)
    {

        String[] companiesNames = new String[companies.size() + 2];
        companiesNames[0] = "";
        companiesNames[1] = "Import All";
        int i = 2;
        for(Company company: companies){
            companiesNames[i] = company.getName();
            i++;
        }
        return companiesNames;
    }


    private void downloadData(int index){
        boolean status = false;
        if(index == 0){
            JOptionPane.showMessageDialog(new JFrame(),"Please Select Company",
                    "Incorrect Selection", JOptionPane.INFORMATION_MESSAGE);
        }
        else if (index == 1) {
            (new Thread(new ProgressBarForm(jcbCompanies.getItemCount() - 2))).start();

        } else {
            Company company = comaniesList.get(index - 2);
            if(jrbCSVFile.isSelected()){
                if(quotesFile != null) {
                    csvReader.setFileName(quotesFile.getAbsolutePath());
                    csvReader.setId_company(company.getId());
                    csvReader.setHeaderRows(1);
                    status = csvReader.importQuote();
                    quotesFile = null;
                    jtfQuotesFileName.setText("");
                }
                else {
                    JOptionPane.showMessageDialog(new JFrame(), "File is not selected",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            else{
                status = HttpDownloadUtility.downloadQuotesByCompany(company);
            }

            if(status) {
                JOptionPane.showMessageDialog(new JFrame(), "Data Imported Successfully",
                        "Finished", JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(new JFrame(), "Data is not Imported",
                        "Finished", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void chooseFile(int type){
        JFileChooser dialog = new JFileChooser();
        dialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        dialog.setDialogTitle("Choose file");// ??????? ????????
        dialog.setDialogType(JFileChooser.OPEN_DIALOG);// ??????? ??? ??????? Open ??? Save
        dialog.setMultiSelectionEnabled(false); // ????????? ????? ????????? ??????
        dialog.showOpenDialog(this);
        if(type == 2)
            quotesFile = dialog.getSelectedFile();
        else
            companyFile = dialog.getSelectedFile();
    }

    private void downloadCompanies(){
        boolean status = false;
        if(companyFile != null) {
            csvReader.setFileName(companyFile.getAbsolutePath());
            csvReader.setHeaderRows(1);
            status = csvReader.importCompany();
            companyFile = null;
            jtfCompanyFileName.setText("");
            comaniesList = (new JdbcCompanyDAOImpl().findAll());

            cbmCompaniesModel = new DefaultComboBoxModel<>(getNames(comaniesList));
            jcbCompanies.setModel(cbmCompaniesModel);
        }
        else {
            JOptionPane.showMessageDialog(new JFrame(), "File is not selected",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        if(status) {
            JOptionPane.showMessageDialog(new JFrame(), "Data Imported Successfully",
                    "Finished", JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            JOptionPane.showMessageDialog(new JFrame(), "Data is not Imported",
                    "Finished", JOptionPane.ERROR_MESSAGE);
        }
    }
}

