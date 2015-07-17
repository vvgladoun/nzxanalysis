package client.interfaces;

import server.Company;
import server.User;
import server.integration.JdbcCompanyDAOImpl;
import server.integration.JdbcUserDAOImpl;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.CubicCurve2D;

/**
 * Window for user management
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public class UsersDescription extends JFrame {

    private java.util.List<User> userList = (new JdbcUserDAOImpl()).findAll();
    private DefaultListModel listModel = new DefaultListModel();
    private JList jlstUsers = new JList(listModel);
    private JScrollPane jspList = new JScrollPane(jlstUsers);

    private JLabel jlbFirstName = new JLabel("First Name");
    private JLabel jlbLastName = new JLabel("Last Name");
    private JLabel jlbRole = new JLabel("User Role");
    private JLabel jlbLogin = new JLabel("Login");

    private JTextField jtxFirstName = new JTextField(25);
    private JTextField jtxLastName = new JTextField(25);
    private JTextField jtxLogin = new JTextField(25);
    private JComboBox jcbRole = new JComboBox(new String[]{"Administrator", "Public"});

    private JPanel companyPanel2 = new JPanel();
    private JPanel userPanel1 = new JPanel();
    private JPanel content = new JPanel();
    private JPanel controlButtons = new JPanel();

    private JButton jbtUserAdd = new JButton();
    private JButton jbtUserRemove = new JButton();
    private JButton jbtEditUser = new JButton();
    private JButton jbtEditPass = new JButton();

    private Icon icoUser_add = new ImageIcon("src/images/user_add.png");
    private Icon icoUser_remove = new ImageIcon("src/images/user_remove.png");
    private Icon icoEdit_user = new ImageIcon("src/images/edit_user.png");
    private Icon icoEdit_pass = new ImageIcon("src/images/edit_pass.png");

    private JLabel jlbPass = new JLabel("Current Password");
    private JLabel jlbNewPass = new JLabel("New Password");
    private JLabel jlbConfNewPass = new JLabel("Confirm New Password");
    private JPasswordField jpfPass = new JPasswordField(12);
    private JPasswordField jpfNewPass = new JPasswordField(12);
    private JPasswordField jpfConfNewPass = new JPasswordField(12);

    // current user
    private User currentUser;

    /**
     * constructor method
     *
     * @param currentUser
     */
    public UsersDescription(User currentUser){

        this.currentUser = currentUser;

        // set icons and alignments for buttons
        controlButtons.setLayout(new FlowLayout(FlowLayout.CENTER));
        jbtUserAdd.setIcon(icoUser_add);
        jbtUserAdd.setText("Add User");
        jbtUserAdd.setHorizontalAlignment(SwingConstants.LEFT);
        jbtUserAdd.setHorizontalTextPosition(SwingConstants.RIGHT);
        jbtUserRemove.setIcon(icoUser_remove);
        jbtUserRemove.setText("Remove User");
        jbtUserRemove.setHorizontalAlignment(SwingConstants.LEFT);
        jbtUserRemove.setHorizontalTextPosition(SwingConstants.RIGHT);
        jbtEditUser.setIcon(icoEdit_user);
        jbtEditUser.setText("Edit User");
        jbtEditUser.setHorizontalAlignment(SwingConstants.LEFT);
        jbtEditUser.setHorizontalTextPosition(SwingConstants.RIGHT);
        jbtEditPass.setIcon(icoEdit_pass);
        jbtEditPass.setText("Edit Password");
        jbtEditPass.setHorizontalAlignment(SwingConstants.LEFT);
        jbtEditPass.setHorizontalTextPosition(SwingConstants.RIGHT);

        // check user rights (2 - public)
        if (currentUser.getId_user_role() == 2){
            jbtUserAdd.setEnabled(false);
            jbtUserRemove.setEnabled(false);
        }

        // add buttons on panel
        controlButtons.add(jbtEditUser);
        controlButtons.add(jbtEditPass);
        controlButtons.add(jbtUserAdd);
        controlButtons.add(jbtUserRemove);

        // arrange panels
        userPanel1.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        userPanel1.add(jlbFirstName, c);
        c.gridx = 1;
        c.gridy = 0;
        userPanel1.add(jtxFirstName, c);
        c.gridx = 0;
        c.gridy = 1;
        userPanel1.add(jlbLastName, c);
        c.gridx = 1;
        c.gridy = 1;
        userPanel1.add(jtxLastName, c);
        c.gridx = 0;
        c.gridy = 2;
        userPanel1.add(jlbLogin, c);
        c.gridx = 1;
        c.gridy = 2;
        userPanel1.add(jtxLogin, c);
        c.gridx = 0;
        c.gridy = 3;
        userPanel1.add(jlbRole, c);
        c.gridx = 1;
        c.gridy = 3;
        userPanel1.add(jcbRole, c);

        c.gridx = 0;
        c.gridy = 4;
        userPanel1.add(jlbPass, c);
        c.gridx = 1;
        c.gridy = 4;
        userPanel1.add(jpfPass, c);
        c.gridx = 0;
        c.gridy = 5;
        userPanel1.add(jlbNewPass, c);
        c.gridx = 1;
        c.gridy = 5;
        userPanel1.add(jpfNewPass, c);
        c.gridx = 0;
        c.gridy = 6;
        userPanel1.add(jlbConfNewPass, c);
        c.gridx = 1;
        c.gridy = 6;
        userPanel1.add(jpfConfNewPass, c);

        // set defaults for frame
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle("User Manager");
        this.setLocation(200, 200);
        this.setLayout(new BorderLayout());
        this.add(controlButtons, BorderLayout.SOUTH);

        // update list of users
        updateList();

        companyPanel2.setLayout(new BorderLayout());
        companyPanel2.add(userPanel1, BorderLayout.CENTER);
        content.setLayout(new BorderLayout());
        jspList.setPreferredSize(new Dimension(150, 150));
        content.add(companyPanel2, BorderLayout.CENTER);
        content.add(jspList, BorderLayout.WEST);
        this.add(content, BorderLayout.CENTER);

        this.pack();
        this.setVisible(true);

        //set listeners
        jlstUsers.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                showDescription();
            }
        });

        jbtUserAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createUser();
            }
        });
        jbtUserRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeUser();
            }
        });
        jbtEditUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editUser();
            }
        });
        jbtEditPass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editPassword();
            }
        });

    }

    /**
     * edit selected user
     */
    private void editUser(){
        //get selected user (check if any user is selected)
        int index = jlstUsers.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(new JFrame(), "Please select User first",
                    "Select user", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User user = userList.get(index);
        user.setLogin(jtxLogin.getText());
        user.setFirstname(jtxFirstName.getText());
        user.setLastname(jtxLastName.getText());
        user.setId_user_role(jcbRole.getSelectedIndex() + 1);
        //add user to data store
        (new JdbcUserDAOImpl()).updateUser(user);

        //try to change password too
        editPassword();

    }

    /**
     * Change password for selected user
     */
    private void editPassword(){
        int index = jlstUsers.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(new JFrame(), "Please select User first",
                    "Select user", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!String.valueOf(jpfNewPass.getPassword()).equals(String.valueOf(jpfConfNewPass.getPassword())) ||
                String.valueOf(jpfNewPass.getPassword()).length() == 0) {
            JOptionPane.showMessageDialog(new JFrame(), "Please enter correct password",
                    "Incorrect password", JOptionPane.INFORMATION_MESSAGE);
            jpfNewPass.setText("");
            jpfConfNewPass.setText("");
            return;
        }

        User user = userList.get(index);
        user.setPassword(String.valueOf(jpfNewPass.getPassword()));
        // update user in data store
        (new JdbcUserDAOImpl()).updateUser(user);
    }

    /**
     * Show user description
     */
    private void showDescription(){
        //get selected user
        int index = jlstUsers.getSelectedIndex();
        // check if user is selected
        if (index == -1) {
            //no user
            jtxFirstName.setText("");
            jtxLastName.setText("");
            jtxLogin.setText("");
            jcbRole.setSelectedIndex(1);
            jpfPass.setText("");
            jpfNewPass.setText("");
            jpfConfNewPass.setText("");
            return;
        } else {
            User user = userList.get(index);
            jtxFirstName.setText(user.getFirstname());
            jtxLastName.setText(user.getLastname());
            jtxLogin.setText(user.getLogin());
            jcbRole.setSelectedIndex(user.getId_user_role() - 1);
            jpfPass.setText(user.getPassword());
            jpfNewPass.setText("");
            jpfConfNewPass.setText("");
        }
    }

    /**
     * remove selected user from list
     */
    private void removeUser(){
        //get selected user
        int index = jlstUsers.getSelectedIndex();
        //check if selection is empty
        if (index == -1) {
            JOptionPane.showMessageDialog(new JFrame(), "Please select User first",
                    "Select user", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User user = userList.get(index);
        // check if user is equal to current
        if (user.getId() == currentUser.getId()) {
            JOptionPane.showMessageDialog(new JFrame(), "Current user cannot be deleted!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        (new JdbcUserDAOImpl()).deleteUser(user);
        //update list of users
        updateList();
    }

    /**
     * create new user
     */
    private void createUser() {

        //System.out.println(String.valueOf(jpfNewPass.getPassword()).length());

        if(!String.valueOf(jpfNewPass.getPassword()).equals(String.valueOf(jpfConfNewPass.getPassword())) ||
                String.valueOf(jpfNewPass.getPassword()).length() == 0) {
            JOptionPane.showMessageDialog(new JFrame(), "Please enter correct password",
                    "Incorrect password", JOptionPane.INFORMATION_MESSAGE);
            jpfNewPass.setText("");
            jpfConfNewPass.setText("");
        }
        else{
            boolean uniqueUser = checkLogin(jtxLogin.getText());
            //if user login unique, create new user
            if (uniqueUser){
                User user = new User();
                user.setFirstname(jtxFirstName.getText());
                user.setLastname(jtxLastName.getText());
                user.setLogin(jtxLogin.getText());
                user.setId_user_role(jcbRole.getSelectedIndex() + 1);
                user.setPassword(String.valueOf(jpfNewPass.getPassword()));
                // insert to data store
                (new JdbcUserDAOImpl()).insertUser(user);

                updateList();
            }
        }
    }

    /**
     * Check if new login is unique
     *
     * @param login new login
     * @return true if login unique
     */
    private boolean checkLogin(String login){
        for(User users: userList){
            if (users.getLogin().equals(login)){
                JOptionPane.showMessageDialog(new JFrame(), "User with such login is already exist",
                        "Incorrect login", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }
        return true;
    }


    /**
     * update list of users
     */
    private void updateList(){

        jlstUsers.setSelectionModel(new DefaultListSelectionModel());
        listModel.clear();

        if(currentUser.getId_user_role() == 1) {
            userList = (new JdbcUserDAOImpl()).findAll();

            if (listModel.getSize() == 0) {
                for (User user : userList) {
                    listModel.addElement(user.getLogin());
                }
            }
        }
        else {
            userList.clear();
            userList.add(currentUser);
            listModel.addElement(currentUser.getLogin());
        }

        jlstUsers.setModel(listModel);
        showDescription();
    }
}
