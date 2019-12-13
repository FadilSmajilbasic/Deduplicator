
package deduplicatorGUI.layouts;

import javax.swing.*;

/**
 *
 * @author Fadil Smajilbasic
 */
public class ScheduleJPanel extends BaseJPanel {

    private static final long serialVersionUID = 6673400195366894131L;
    /**
     * Creates new form ScheduleJPanel
     */
    public ScheduleJPanel() {
        initComponents();
    }

    private void initComponents() {

        typeButtonGroup = new ButtonGroup();
        dayButtonGroup = new ButtonGroup();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        dateTextField = new JFormattedTextField();
        timeSpinner = new JSpinner();
        oneOffRadioButton = new JRadioButton();
        dailyRadioButton = new JRadioButton();
        weeklyRadioButton = new JRadioButton();
        monthlyradioButton = new JRadioButton();
        mondayRadioButton = new JRadioButton();
        tuesdayRadioButton = new JRadioButton();
        wednesdayRadioButton = new JRadioButton();
        thursdayRadioButton = new JRadioButton();
        fridayRadioButton = new JRadioButton();
        saturdayRadioButton = new JRadioButton();
        sundayRadioButton = new JRadioButton();

        jLabel1.setText("Plan new scan:");

        jLabel2.setText("Scan start:");

        dateTextField.setText("date");
        dateTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateTextFieldActionPerformed(evt);
            }
        });

        oneOffRadioButton.setText("One off");
        oneOffRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneOffRadioButtonActionPerformed(evt);
            }
        });

        dailyRadioButton.setText("Daily");

        weeklyRadioButton.setText("Weekly");

        monthlyradioButton.setText("Monthly");

        mondayRadioButton.setText("Monday");
        mondayRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mondayRadioButtonActionPerformed(evt);
            }
        });

        tuesdayRadioButton.setText("Tuesday");

        wednesdayRadioButton.setText("Wednesday");

        thursdayRadioButton.setText("Thursday");

        fridayRadioButton.setText("Friday");
        fridayRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fridayRadioButtonActionPerformed(evt);
            }
        });

        saturdayRadioButton.setText("Saturday");

        sundayRadioButton.setText("Sunday");


        dayButtonGroup.add(mondayRadioButton);
        dayButtonGroup.add(tuesdayRadioButton);
        dayButtonGroup.add(wednesdayRadioButton);
        dayButtonGroup.add(thursdayRadioButton);
        dayButtonGroup.add(fridayRadioButton);
        dayButtonGroup.add(saturdayRadioButton);
        dayButtonGroup.add(sundayRadioButton);


        typeButtonGroup.add(oneOffRadioButton);
        typeButtonGroup.add(weeklyRadioButton);
        typeButtonGroup.add(monthlyradioButton);




        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2)
                            .addComponent(dateTextField))
                        .addGap(18, 18, 18)
                        .addComponent(timeSpinner, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(mondayRadioButton)
                            .addComponent(tuesdayRadioButton)
                            .addComponent(wednesdayRadioButton))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(thursdayRadioButton)
                                .addComponent(saturdayRadioButton)
                                .addComponent(fridayRadioButton)
                                .addComponent(sundayRadioButton))
                            .addGap(16, 16, 16))))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(monthlyradioButton)
                    .addComponent(weeklyRadioButton)
                    .addComponent(dailyRadioButton)
                    .addComponent(oneOffRadioButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(dateTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(timeSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(oneOffRadioButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(dailyRadioButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(weeklyRadioButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(monthlyradioButton)
                        .addContainerGap())
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(mondayRadioButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tuesdayRadioButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wednesdayRadioButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(thursdayRadioButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fridayRadioButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saturdayRadioButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(sundayRadioButton))))
        );
    }

    private void dateTextFieldActionPerformed(java.awt.event.ActionEvent evt) {
       
    }

    private void oneOffRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
        
    }

    private void mondayRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
        
    }

    private void fridayRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
       
    }


    
    private JRadioButton dailyRadioButton;
    private JFormattedTextField dateTextField;
    private ButtonGroup dayButtonGroup;
    private JRadioButton fridayRadioButton;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JRadioButton mondayRadioButton;
    private JRadioButton monthlyradioButton;
    private JRadioButton oneOffRadioButton;
    private JRadioButton saturdayRadioButton;
    private JRadioButton sundayRadioButton;
    private JRadioButton thursdayRadioButton;
    private JSpinner timeSpinner;
    private JRadioButton tuesdayRadioButton;
    private ButtonGroup typeButtonGroup;
    private JRadioButton wednesdayRadioButton;
    private JRadioButton weeklyRadioButton;
    
}
