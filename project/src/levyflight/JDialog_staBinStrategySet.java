/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JDialog_staBinStrategySet.java
 *
 * Created on 2011-5-24, 10:22:40
 */
package levyflight;

import levyflight.Statistic.StatisticBinningStrategy;

/**
 *
 * @author wu
 */
public class JDialog_staBinStrategySet extends javax.swing.JDialog
{

    private EnvironmentParameter ep;
    private boolean isReseted = false;

    /**
     * 设置默认统计策略
     * @param parent
     * @param EP
     * @return 用户是否已经重设了参数（是否需要刷新）
     */
    static public boolean setStrategy(java.awt.Frame parent, EnvironmentParameter EP)
    {
        JDialog_staBinStrategySet dialog = new JDialog_staBinStrategySet(parent, true, EP);
        dialog.setVisible(true);
        return dialog.isReseted;
    }

    /** Creates new form JDialog_staBinStrategySet */
    public JDialog_staBinStrategySet(java.awt.Frame parent, boolean modal, EnvironmentParameter EP)
    {
        super(parent, modal);
        initComponents();
        this.ep = EP;
        if (ep.statisticBinningStrategy.getStrategy() == StatisticBinningStrategy.strategy_optimal)
        {
            this.jRadioButton_optimal.setSelected(true);
        } else if (ep.statisticBinningStrategy.getStrategy() == StatisticBinningStrategy.strategy_fixedInterval)
        {
            this.jRadioButton_fixedInterval.setSelected(true);
        } else if (ep.statisticBinningStrategy.getStrategy() == StatisticBinningStrategy.strategy_fixedSegment)
        {
            this.jRadioButton_fixedSegment.setSelected(true);
        } else if (ep.statisticBinningStrategy.getStrategy() == StatisticBinningStrategy.strategy_percentageOfSampleSize)
        {
            this.jRadioButton_percentageSeg.setSelected(true);
        }

        this.jCheckBox_calEverySamplyInCDF.setSelected(ep.statisticBinningStrategy.calEverySamplyInCDF);

        this.checkButtons();
    }

    private void checkButtons()
    {
        if (this.jRadioButton_optimal.isSelected())
        {
            this.jFormattedTextField_interval.setEnabled(false);
            this.jFormattedTextField_percentage.setEnabled(false);
            this.jFormattedTextField_segment.setEnabled(false);
        } else if (this.jRadioButton_fixedInterval.isSelected())
        {
            this.jFormattedTextField_interval.setEnabled(true);
            this.jFormattedTextField_percentage.setEnabled(false);
            this.jFormattedTextField_segment.setEnabled(false);
        } else if (this.jRadioButton_fixedSegment.isSelected())
        {
            this.jFormattedTextField_interval.setEnabled(false);
            this.jFormattedTextField_percentage.setEnabled(false);
            this.jFormattedTextField_segment.setEnabled(true);
        } else if (this.jRadioButton_percentageSeg.isSelected())
        {
            this.jFormattedTextField_interval.setEnabled(false);
            this.jFormattedTextField_percentage.setEnabled(true);
            this.jFormattedTextField_segment.setEnabled(false);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup_strategy = new javax.swing.ButtonGroup();
        jRadioButton_percentageSeg = new javax.swing.JRadioButton();
        jRadioButton_optimal = new javax.swing.JRadioButton();
        jFormattedTextField_interval = new javax.swing.JFormattedTextField();
        jRadioButton_fixedInterval = new javax.swing.JRadioButton();
        jRadioButton_fixedSegment = new javax.swing.JRadioButton();
        jFormattedTextField_segment = new javax.swing.JFormattedTextField();
        jFormattedTextField_percentage = new javax.swing.JFormattedTextField();
        jCheckBox_calEverySamplyInCDF = new javax.swing.JCheckBox();
        jButton_cancel = new javax.swing.JButton();
        jButton_ok = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(levyflight.LevyFlightApp.class).getContext().getResourceMap(JDialog_staBinStrategySet.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(440, 337));
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(null);

        buttonGroup_strategy.add(jRadioButton_percentageSeg);
        jRadioButton_percentageSeg.setText(resourceMap.getString("jRadioButton_percentageSeg.text")); // NOI18N
        jRadioButton_percentageSeg.setName("jRadioButton_percentageSeg"); // NOI18N
        jRadioButton_percentageSeg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_percentageSegActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButton_percentageSeg);
        jRadioButton_percentageSeg.setBounds(20, 150, 280, 20);

        buttonGroup_strategy.add(jRadioButton_optimal);
        jRadioButton_optimal.setText(resourceMap.getString("jRadioButton_optimal.text")); // NOI18N
        jRadioButton_optimal.setName("jRadioButton_optimal"); // NOI18N
        jRadioButton_optimal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_optimalActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButton_optimal);
        jRadioButton_optimal.setBounds(20, 30, 240, 23);

        jFormattedTextField_interval.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("####0.00##########"))));
        jFormattedTextField_interval.setText(resourceMap.getString("jFormattedTextField_interval.text")); // NOI18N
        jFormattedTextField_interval.setName("jFormattedTextField_interval"); // NOI18N
        getContentPane().add(jFormattedTextField_interval);
        jFormattedTextField_interval.setBounds(170, 70, 120, 21);

        buttonGroup_strategy.add(jRadioButton_fixedInterval);
        jRadioButton_fixedInterval.setText(resourceMap.getString("jRadioButton_fixedInterval.text")); // NOI18N
        jRadioButton_fixedInterval.setName("jRadioButton_fixedInterval"); // NOI18N
        jRadioButton_fixedInterval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_fixedIntervalActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButton_fixedInterval);
        jRadioButton_fixedInterval.setBounds(20, 70, 140, 20);

        buttonGroup_strategy.add(jRadioButton_fixedSegment);
        jRadioButton_fixedSegment.setText(resourceMap.getString("jRadioButton_fixedSegment.text")); // NOI18N
        jRadioButton_fixedSegment.setName("jRadioButton_fixedSegment"); // NOI18N
        jRadioButton_fixedSegment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_fixedSegmentActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButton_fixedSegment);
        jRadioButton_fixedSegment.setBounds(20, 110, 140, 20);

        jFormattedTextField_segment.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        jFormattedTextField_segment.setName("jFormattedTextField_segment"); // NOI18N
        getContentPane().add(jFormattedTextField_segment);
        jFormattedTextField_segment.setBounds(170, 110, 120, 21);

        jFormattedTextField_percentage.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("####0.00##########"))));
        jFormattedTextField_percentage.setName("jFormattedTextField_percentage"); // NOI18N
        getContentPane().add(jFormattedTextField_percentage);
        jFormattedTextField_percentage.setBounds(300, 150, 100, 21);

        jCheckBox_calEverySamplyInCDF.setForeground(resourceMap.getColor("jCheckBox_calEverySamplyInCDF.foreground")); // NOI18N
        jCheckBox_calEverySamplyInCDF.setText(resourceMap.getString("jCheckBox_calEverySamplyInCDF.text")); // NOI18N
        jCheckBox_calEverySamplyInCDF.setName("jCheckBox_calEverySamplyInCDF"); // NOI18N
        getContentPane().add(jCheckBox_calEverySamplyInCDF);
        jCheckBox_calEverySamplyInCDF.setBounds(20, 200, 360, 23);

        jButton_cancel.setText(resourceMap.getString("jButton_cancel.text")); // NOI18N
        jButton_cancel.setName("jButton_cancel"); // NOI18N
        jButton_cancel.setPreferredSize(null);
        jButton_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButton_cancel);
        jButton_cancel.setBounds(320, 260, 81, 23);

        jButton_ok.setText(resourceMap.getString("jButton_ok.text")); // NOI18N
        jButton_ok.setName("jButton_ok"); // NOI18N
        jButton_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_okActionPerformed(evt);
            }
        });
        getContentPane().add(jButton_ok);
        jButton_ok.setBounds(230, 260, 81, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton_optimalActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButton_optimalActionPerformed
    {//GEN-HEADEREND:event_jRadioButton_optimalActionPerformed
        this.checkButtons();
    }//GEN-LAST:event_jRadioButton_optimalActionPerformed

    private void jRadioButton_fixedIntervalActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButton_fixedIntervalActionPerformed
    {//GEN-HEADEREND:event_jRadioButton_fixedIntervalActionPerformed
        this.checkButtons();
    }//GEN-LAST:event_jRadioButton_fixedIntervalActionPerformed

    private void jRadioButton_fixedSegmentActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButton_fixedSegmentActionPerformed
    {//GEN-HEADEREND:event_jRadioButton_fixedSegmentActionPerformed
        this.checkButtons();
    }//GEN-LAST:event_jRadioButton_fixedSegmentActionPerformed

    private void jRadioButton_percentageSegActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButton_percentageSegActionPerformed
    {//GEN-HEADEREND:event_jRadioButton_percentageSegActionPerformed
        this.checkButtons();
    }//GEN-LAST:event_jRadioButton_percentageSegActionPerformed

    private void jButton_okActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_okActionPerformed
    {//GEN-HEADEREND:event_jButton_okActionPerformed
        if (this.jRadioButton_optimal.isSelected())
        {
            ep.statisticBinningStrategy.setStrategy(StatisticBinningStrategy.strategy_optimal);
        } else if (this.jRadioButton_fixedInterval.isSelected())
        {
            double value = 1;
            try
            {
                value = Double.parseDouble(this.jFormattedTextField_interval.getText());
            } catch (Exception e)
            {
                javax.swing.JOptionPane.showMessageDialog(null, e.getMessage(), "input error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (value <= 0)
            {
                javax.swing.JOptionPane.showMessageDialog(null, "interval should be greater than 0!", "input error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            ep.statisticBinningStrategy.setStrategy(StatisticBinningStrategy.strategy_fixedInterval);
            ep.statisticBinningStrategy.setValue(value);

        } else if (this.jRadioButton_fixedSegment.isSelected())
        {
            int value = 1;
            try
            {
                value = Integer.parseInt(this.jFormattedTextField_segment.getText());
            } catch (Exception e)
            {
                javax.swing.JOptionPane.showMessageDialog(null, e.getMessage(), "input error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (value <= 0)
            {
                javax.swing.JOptionPane.showMessageDialog(null, "segment should be greater than 0!", "input error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            ep.statisticBinningStrategy.setStrategy(StatisticBinningStrategy.strategy_fixedSegment);
            ep.statisticBinningStrategy.setValue(value);

        } else if (this.jRadioButton_percentageSeg.isSelected())
        {
            double value = 1;
            try
            {
                value = Double.parseDouble(this.jFormattedTextField_percentage.getText());
            } catch (Exception e)
            {
                javax.swing.JOptionPane.showMessageDialog(null, e.getMessage(), "input error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (value <= 0 || value >= 1)
            {
                javax.swing.JOptionPane.showMessageDialog(null, "percentage should be within (0, 1)!", "input error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            ep.statisticBinningStrategy.setStrategy(StatisticBinningStrategy.strategy_percentageOfSampleSize);
            ep.statisticBinningStrategy.setValue(value);
        }
        ep.statisticBinningStrategy.calEverySamplyInCDF = this.jCheckBox_calEverySamplyInCDF.isSelected();
        this.isReseted = true;
        this.dispose();
    }//GEN-LAST:event_jButton_okActionPerformed

    private void jButton_cancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_cancelActionPerformed
    {//GEN-HEADEREND:event_jButton_cancelActionPerformed

        this.isReseted = false;
        this.dispose();
    }//GEN-LAST:event_jButton_cancelActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup_strategy;
    private javax.swing.JButton jButton_cancel;
    private javax.swing.JButton jButton_ok;
    private javax.swing.JCheckBox jCheckBox_calEverySamplyInCDF;
    private javax.swing.JFormattedTextField jFormattedTextField_interval;
    private javax.swing.JFormattedTextField jFormattedTextField_percentage;
    private javax.swing.JFormattedTextField jFormattedTextField_segment;
    private javax.swing.JRadioButton jRadioButton_fixedInterval;
    private javax.swing.JRadioButton jRadioButton_fixedSegment;
    private javax.swing.JRadioButton jRadioButton_optimal;
    private javax.swing.JRadioButton jRadioButton_percentageSeg;
    // End of variables declaration//GEN-END:variables
}
