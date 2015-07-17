package client.interfaces;

import server.integration.HttpDownloadUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ProgressBarForm extends Thread {

    static private int BOR = 10;
    private HttpDownloadUtility httpDownload = new HttpDownloadUtility();
    private JLabel jlbCurrent = new JLabel("Start");
    private JProgressBar progressBar = new JProgressBar();
    private JFrame frame = new JFrame();

    public ProgressBarForm(int max){

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(BOR, BOR, BOR, BOR));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalGlue());


        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        progressBar.setMaximum(max);
        panel.add(jlbCurrent);
        panel.add(progressBar);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel, BorderLayout.CENTER);


        frame.setPreferredSize(new Dimension(260, 100));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Thread t = new Thread(new HttpDownloadUtility());
        t.start();
    }

    public void run(){
        while (httpDownload.getDownloaded() < progressBar.getMaximum()) {
            progressBar.setValue(httpDownload.getDownloaded());
            jlbCurrent.setText(httpDownload.getCurrentCompany());
        }
        JOptionPane.showMessageDialog(frame, "Import is finished", "Import", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    }

}