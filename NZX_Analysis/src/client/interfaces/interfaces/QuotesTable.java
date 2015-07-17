package client.interfaces;

import net.sourceforge.jdatepicker.JDatePicker;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import server.Company;
import server.Quote;
import server.integration.JdbcCompanyDAOImpl;
import server.integration.JdbcQuotesDAOImpl;

import javax.swing.table.AbstractTableModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.crypto.Data;
//import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.text.MaskFormatter;


/**
 * Created by Alex on 12/05/2015.
 */
public class QuotesTable extends JApplet {

    private JFrame frame = new JFrame();
    private JPanel jplGraphic = new JPanel();
    private JPanel chooseCompany = new JPanel();

    private JButton jbtDraw = new JButton("Draw");


    private java.util.List<Company> comaniesList = (new JdbcCompanyDAOImpl()).findAll();
    private java.util.List<Quote> quotes;
    private JComboBox jcbCompanies = new JComboBox(getNames(comaniesList));

    private String[] columnNames =
            {"ID", "ID Company", "Date" ,"Open", "High", "Low", "Close", "Volume", "Adj Close"};

    private DefaultTableModel modelQuotes = new DefaultTableModel(0, columnNames.length);;
    private JTable jtbQuotes = new JTable(modelQuotes);
    private JScrollPane jspQuotes = new JScrollPane(jtbQuotes);
    private TableRowSorter<TableModel> sorter;

    private UtilDateModel modelFrom = new UtilDateModel();
    private UtilDateModel modelTo = new UtilDateModel();
    private JDatePanelImpl dateToPanel = new JDatePanelImpl(modelTo);
    private JDatePanelImpl dateFromPanel = new JDatePanelImpl(modelFrom);
    private JDatePickerImpl dateFromPicker = new JDatePickerImpl(dateFromPanel);
    private JDatePickerImpl dateToPicker = new JDatePickerImpl(dateToPanel);

    private JLabel jlbDateFrom = new JLabel("Date From");
    private JLabel jlbDateTo = new JLabel("Date To");


    public QuotesTable() {

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setTitle("Quotes");
        this.init();
        this.start();
        frame.setLocation(200, 200);
        tableDraw(comaniesList.get(0));

        frame.getContentPane().add(this, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        jcbCompanies.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int index = jcbCompanies.getSelectedIndex();
                tableDraw(comaniesList.get(index));
            }
        });

        jbtDraw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawCandlesPlot();
            }
        });
    }

    private void tableDraw(Company company){
        quotes = (new JdbcQuotesDAOImpl()).findAll(company.getId());
        String[] rows = new String[9];
        int rowsNum = modelQuotes.getRowCount();
        if(rowsNum != 0){
            for(int i = rowsNum - 1; i > -1; i--){
                modelQuotes.removeRow(i);
            }
        }

        try {
            modelQuotes.setColumnIdentifiers(columnNames);
            for (Quote quote : quotes) {
                rows[0] = String.valueOf(quote.getId());
                rows[1] = String.valueOf(quote.getId_company());
                rows[2] = String.valueOf(quote.getClose_date());
                rows[3] = String.valueOf(quote.getOpen_price());
                rows[4] = String.valueOf(quote.getHigh_price());
                rows[5] = String.valueOf(quote.getLow_price());
                rows[6] = String.valueOf(quote.getClose_price());
                rows[7] = String.valueOf(quote.getVolume());
                rows[8] = String.valueOf(quote.getAdjusted_close());
                modelQuotes.addRow(rows);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        jtbQuotes.setModel(modelQuotes);
        jtbQuotes.getColumnModel().getColumn(1).setMinWidth(0);
        jtbQuotes.getColumnModel().getColumn(1).setMaxWidth(0);
        jtbQuotes.getColumnModel().getColumn(0).setMinWidth(0);
        jtbQuotes.getColumnModel().getColumn(0).setMaxWidth(0);
        jtbQuotes.getColumnModel().getColumn(2).setMinWidth(100);
        jtbQuotes.getColumnModel().getColumn(2).setMaxWidth(100);
        sorter = new TableRowSorter<TableModel>(jtbQuotes.getModel());
        jtbQuotes.setRowSorter(sorter);


        setLayout(new BorderLayout());
        jplGraphic.setLayout(new FlowLayout());

        jplGraphic.add(jlbDateFrom);
        jplGraphic.add(dateFromPicker);
        jplGraphic.add(jlbDateTo);
        jplGraphic.add(dateToPicker);
        jplGraphic.add(jbtDraw);

        chooseCompany.setLayout(new BorderLayout());
        chooseCompany.add(jcbCompanies, BorderLayout.CENTER);
        chooseCompany.add(jplGraphic, BorderLayout.SOUTH);
        add(chooseCompany, BorderLayout.NORTH);
        add(jspQuotes, BorderLayout.CENTER);
    }
    /**
     * return array of companies names
     */
    private String[] getNames(java.util.List<Company> companies)
    {

        String[] companiesNames = new String[companies.size()];
        int i = 0;
        for(Company company: companies){
            companiesNames[i] = company.getName();
            i++;
        }
        return companiesNames;
    }

    private void drawCandlesPlot(){
        int index = jcbCompanies.getSelectedIndex();
        try {
            java.util.Date dateFrom = (java.util.Date) dateFromPicker.getModel().getValue();
            java.util.Date dateTo = (java.util.Date) dateToPicker.getModel().getValue();
            if(!dateFrom.before(dateTo)){
                JOptionPane.showMessageDialog(new JFrame(), "Date From must be before Date To", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                new CandlestickDemo(comaniesList.get(index), dateFrom, dateTo).setVisible(true);
            }
        }catch (NullPointerException ex){
            JOptionPane.showMessageDialog(new JFrame(), "Dates must not be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
