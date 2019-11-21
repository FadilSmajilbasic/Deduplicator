
package deduplicatorGUI.layouts;

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

        typeButtonGroup = new javax.swing.ButtonGroup();
        dayButtonGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dateTextField = new javax.swing.JFormattedTextField();
        timeSpinner = new javax.swing.JSpinner();
        oneOffRadioButton = new javax.swing.JRadioButton();
        dailyRadioButton = new javax.swing.JRadioButton();
        weeklyRadioButton = new javax.swing.JRadioButton();
        monthlyradioButton = new javax.swing.JRadioButton();
        mondayRadioButton = new javax.swing.JRadioButton();
        tuesdayRadioButton = new javax.swing.JRadioButton();
        wednesdayRadioButton = new javax.swing.JRadioButton();
        thursdayRadioButton = new javax.swing.JRadioButton();
        fridayRadioButton = new javax.swing.JRadioButton();
        saturdayRadioButton = new javax.swing.JRadioButton();
        sundayRadioButton = new javax.swing.JRadioButton();

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




        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2)
                            .addComponent(dateTextField))
                        .addGap(18, 18, 18)
                        .addComponent(timeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mondayRadioButton)
                            .addComponent(tuesdayRadioButton)
                            .addComponent(wednesdayRadioButton))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(thursdayRadioButton)
                                .addComponent(saturdayRadioButton)
                                .addComponent(fridayRadioButton)
                                .addComponent(sundayRadioButton))
                            .addGap(16, 16, 16))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(monthlyradioButton)
                    .addComponent(weeklyRadioButton)
                    .addComponent(dailyRadioButton)
                    .addComponent(oneOffRadioButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(oneOffRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(dailyRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(weeklyRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(monthlyradioButton)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(mondayRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tuesdayRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wednesdayRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(thursdayRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fridayRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saturdayRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(sundayRadioButton))))
        );
    }

    private void dateTextFieldActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void oneOffRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void mondayRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void fridayRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }


    
    private javax.swing.JRadioButton dailyRadioButton;
    private javax.swing.JFormattedTextField dateTextField;
    private javax.swing.ButtonGroup dayButtonGroup;
    private javax.swing.JRadioButton fridayRadioButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JRadioButton mondayRadioButton;
    private javax.swing.JRadioButton monthlyradioButton;
    private javax.swing.JRadioButton oneOffRadioButton;
    private javax.swing.JRadioButton saturdayRadioButton;
    private javax.swing.JRadioButton sundayRadioButton;
    private javax.swing.JRadioButton thursdayRadioButton;
    private javax.swing.JSpinner timeSpinner;
    private javax.swing.JRadioButton tuesdayRadioButton;
    private javax.swing.ButtonGroup typeButtonGroup;
    private javax.swing.JRadioButton wednesdayRadioButton;
    private javax.swing.JRadioButton weeklyRadioButton;
    
}
