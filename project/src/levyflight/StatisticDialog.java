/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StatisticDialog.java
 *
 * Created on 2011-4-21, 0:00:57
 */
package levyflight;

import java.util.ArrayList;
import Static.StaticLib;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.Double;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import levyflight.Data.Trajectory;
import levyflight.Export.StatisticGraphGen;
import levyflight.Statistic.FrequencyCal;

/**
 *
 * @author wu
 */
public class StatisticDialog extends javax.swing.JDialog
{

    EnvironmentParameter ep=null;
    /** Creates new form StatisticDialog */
    public StatisticDialog(java.awt.Frame parent, boolean modal, EnvironmentParameter EP)
    {
        super(parent, modal);
        initComponents();
        this.TextPane_DeclareInfo(jTextPane1, "Welcome!", Color.blue);
        if (EP.trajectoryPool != null)
        {
            JumpLengthList = new ArrayList<Double>();
            ROGList = new ArrayList<Double>();
            EntropyList = new ArrayList<Double>();
            for (int i = 0; i < EP.trajectoryPool.TrajectorySet.size(); i++)
            {
                Trajectory trajectory = EP.trajectoryPool.TrajectorySet.get(i);
                for (double lngth : trajectory.GetStepLengthList())
                {
                    JumpLengthList.add(lngth);
                }
                ROGList.add(trajectory.GetRadiusOfGyration());
                EntropyList.add(trajectory.GetEntropyByDistance(1.0));
            }
        }

        String message = "";
        Color color = Color.BLACK;
        this.TextPane_DeclareInfo(jTextPane1, "Jump Length", color, true);
        FrequencyCal frequencyCal = new FrequencyCal(JumpLengthList,EP.statisticBinningStrategy);
        message = "min: " + frequencyCal.getMinValue();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        message = "max: " + frequencyCal.getMaxValue();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        message = "mean: " + frequencyCal.getAverage();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        message = "sample variance: " + frequencyCal.getSampleVariance();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        message = "sample size: " + frequencyCal.getTotalSampleSize();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        this.TextPane_DeclareInfo(jTextPane1, "", color);
//
        color = StaticLib.fontGreenColor;
        this.TextPane_DeclareInfo(jTextPane1, "ROG", color, true);
        frequencyCal = new FrequencyCal(ROGList,EP.statisticBinningStrategy);
        message = "min: " + frequencyCal.getMinValue();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        message = "max: " + frequencyCal.getMaxValue();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        message = "mean: " + frequencyCal.getAverage();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        message = "sample variance: " + frequencyCal.getSampleVariance();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        message = "sample size: " + frequencyCal.getTotalSampleSize();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        this.TextPane_DeclareInfo(jTextPane1, "", color);

        color = Color.BLUE;
        this.TextPane_DeclareInfo(jTextPane1, "Entropy", color, true);
        frequencyCal = new FrequencyCal(EntropyList,EP.statisticBinningStrategy);
        message = "min: " + frequencyCal.getMinValue();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        message = "max: " + frequencyCal.getMaxValue();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        message = "mean: " + frequencyCal.getAverage();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        message = "sample variance: " + frequencyCal.getSampleVariance();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        message = "sample size: " + frequencyCal.getTotalSampleSize();
        this.TextPane_DeclareInfo(jTextPane1, message, color);
        this.TextPane_DeclareInfo(jTextPane1, "", color);
        this.ep=EP;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        jSplitPane1.setDividerLocation(520);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jSplitPane2.setDividerLocation(300);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setName("jSplitPane2"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 517, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 299, Short.MAX_VALUE)
        );

        jSplitPane2.setTopComponent(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 517, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 275, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(jPanel2);

        jSplitPane1.setLeftComponent(jSplitPane2);

        jPanel3.setName("jPanel3"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(levyflight.LevyFlightApp.class).getContext().getResourceMap(StatisticDialog.class);
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextPane1.setName("jTextPane1"); // NOI18N
        jScrollPane1.setViewportView(jTextPane1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap(78, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //jump length
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
    {//GEN-HEADEREND:event_jButton1ActionPerformed
//        FrequencyCal frequencyCal = new FrequencyCal(JumpLengthList);
//        StatisticGraphGen graphGenerator = StatisticGraphGen.getFrequencyGraphGen(frequencyCal.getBottom(), frequencyCal.getInterval(), frequencyCal.getFrequencyList(), frequencyCal.getTotalSampleSize());
//        BufferedImage image = graphGenerator.generateGraph(jPanel1.getWidth(), jPanel1.getHeight(), false, false);
//        Graphics g = this.jPanel1.getGraphics();
//        g.drawImage(image, 0, 0, null);

//        frequencyCal = new FrequencyCal(JumpLengthList);
//        graphGenerator = StatisticGraphGen.getFrequencyGraphGen(frequencyCal.getBottom(), frequencyCal.getInterval(), frequencyCal.getFrequencyList(), frequencyCal.getTotalSampleSize());
//        image = graphGenerator.generateGraph(jPanel2.getWidth(), jPanel2.getHeight(), false, true);
//        g = this.jPanel2.getGraphics();
//        g.drawImage(image, 0, 0, null);


        FrequencyCal frequencyCal = new FrequencyCal(JumpLengthList,ep.statisticBinningStrategy);
        ArrayList<Double> valueList = new ArrayList<Double>();
        ArrayList<Double> probList =new ArrayList<Double>();
        //frequencyCal.getCDFBySample(valueList, probList);
        double[] frequencyList=frequencyCal.getFrequencyList();
//        for (int i = 0; i < probList.size(); i++)
//        {
//            if(probList.get(i)>0.5)
//            {
//                JOptionPane.showMessageDialog(null, i);
//                while(valueList.size()>i)
//                {
//                    probList.remove(probList.size()-1);
//                    valueList.remove(valueList.size()-1);
//                }
//                break;
//            }
//        }
        //StatisticGraphGen graphGenerator = StatisticGraphGen.getFrequencyGraphGen(frequencyCal.getBottom(), frequencyCal.getInterval(), frequencyCal.getFrequencyList(), frequencyCal.getTotalSampleSize());
        //StatisticGraphGen graphGenerator = StatisticGraphGen.getCumulativeFrenquencyGraphGen(valueList, probList);
        StatisticGraphGen graphGenerator = new StatisticGraphGen(frequencyCal.getMinValue(), frequencyCal.getInterval(), frequencyList);
        BufferedImage image = graphGenerator.generateGraphAsLineChart(jPanel1.getWidth(), jPanel1.getHeight(), true, true);
        Graphics g = this.jPanel1.getGraphics();
        g.drawImage(image, 0, 0, null);

        frequencyCal = new FrequencyCal(JumpLengthList,ep.statisticBinningStrategy);
        graphGenerator = new StatisticGraphGen(frequencyCal.getMinValue(), frequencyCal.getInterval(), frequencyCal.getFrequencyList());
       // image = graphGenerator.generateGraphAsLineChart(jPanel2.getWidth(), jPanel2.getHeight(), true, true);
        image = graphGenerator.generateGraphAsHistogram(jPanel2.getWidth(), jPanel2.getHeight(), false);
        g = this.jPanel2.getGraphics();
        g.drawImage(image, 0, 0, null);

    }//GEN-LAST:event_jButton1ActionPerformed

    //ROG
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton2ActionPerformed
    {//GEN-HEADEREND:event_jButton2ActionPerformed
//        FrequencyCal frequencyCal = new FrequencyCal(ROGList);
//        StatisticGraphGen graphGenerator = StatisticGraphGen.getFrequencyGraphGen(frequencyCal.getBottom(), frequencyCal.getInterval(), frequencyCal.getFrequencyList(), frequencyCal.getTotalSampleSize());
//        BufferedImage image = graphGenerator.generateGraph(jPanel1.getWidth(), jPanel1.getHeight(), false, false);
//        Graphics g = this.jPanel1.getGraphics();
//        g.drawImage(image, 0, 0, null);

//        frequencyCal = new FrequencyCal(ROGList);
//        graphGenerator = StatisticGraphGen.getFrequencyGraphGen(frequencyCal.getBottom(), frequencyCal.getInterval(), frequencyCal.getFrequencyList(), frequencyCal.getTotalSampleSize());
//        image = graphGenerator.generateGraph(jPanel2.getWidth(), jPanel2.getHeight(), false, true);
//        g = this.jPanel2.getGraphics();
//        g.drawImage(image, 0, 0, null);

//        frequencyCal = new FrequencyCal(ROGList, ROGList.size() / 2);
//        graphGenerator = StatisticGraphGen.getCumulativeFrenquencyGraphGen(frequencyCal.getBottom(), frequencyCal.getInterval(), frequencyCal.getCDFByGroup());
//        image = graphGenerator.generateGraph(jPanel2.getWidth(), jPanel2.getHeight(), false, false);
//        g = this.jPanel2.getGraphics();
//        g.drawImage(image, 0, 0, null);

        FrequencyCal frequencyCal = new FrequencyCal(ROGList,ep.statisticBinningStrategy);
        ArrayList<Double> valueList = new ArrayList<Double>();
        ArrayList<Double> probList = new ArrayList<Double>();
        frequencyCal.getCCDFBySample(valueList, probList);
        //StatisticGraphGen graphGenerator = StatisticGraphGen.getFrequencyGraphGen(frequencyCal.getBottom(), frequencyCal.getInterval(), frequencyCal.getFrequencyList(), frequencyCal.getTotalSampleSize());
        StatisticGraphGen graphGenerator = new StatisticGraphGen (valueList, probList);
        BufferedImage image = graphGenerator.generateGraphAsLineChart(jPanel1.getWidth(), jPanel1.getHeight(), false, false);
        Graphics g = this.jPanel1.getGraphics();
        g.drawImage(image, 0, 0, null);

//        frequencyCal = new FrequencyCal(JumpLengthList, JumpLengthList.size() / 2);
//        graphGenerator = StatisticGraphGen.getCumulativeFrenquencyGraphGen(frequencyCal.getBottom(), frequencyCal.getInterval(), frequencyCal.getCDFByGroup());
        image = graphGenerator.generateGraphAsLineChart(jPanel2.getWidth(), jPanel2.getHeight(), true, true);
        g = this.jPanel2.getGraphics();
        g.drawImage(image, 0, 0, null);

    }//GEN-LAST:event_jButton2ActionPerformed

    //entropy
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton3ActionPerformed
    {//GEN-HEADEREND:event_jButton3ActionPerformed
//        FrequencyCal frequencyCal = new FrequencyCal(EntropyList);
//        StatisticGraphGen graphGenerator = StatisticGraphGen.getFrequencyGraphGen(frequencyCal.getBottom(), frequencyCal.getInterval(), frequencyCal.getFrequencyList(), frequencyCal.getTotalSampleSize());
//        BufferedImage image = graphGenerator.generateGraph(jPanel1.getWidth(), jPanel1.getHeight(), false, false);
//        Graphics g = this.jPanel1.getGraphics();
//        g.drawImage(image, 0, 0, null);

//        frequencyCal = new FrequencyCal(EntropyList);
//        graphGenerator = StatisticGraphGen.getFrequencyGraphGen(frequencyCal.getBottom(), frequencyCal.getInterval(), frequencyCal.getFrequencyList(), frequencyCal.getTotalSampleSize());
//        image = graphGenerator.generateGraph(jPanel2.getWidth(), jPanel2.getHeight(), false, true);
//        g = this.jPanel2.getGraphics();
//        g.drawImage(image, 0, 0, null);

//        frequencyCal = new FrequencyCal(EntropyList, EntropyList.size() / 2);
//        graphGenerator = StatisticGraphGen.getCumulativeFrenquencyGraphGen(frequencyCal.getBottom(), frequencyCal.getInterval(), frequencyCal.getCDFByGroup());
//        image = graphGenerator.generateGraph(jPanel2.getWidth(), jPanel2.getHeight(), false, false);
//        g = this.jPanel2.getGraphics();
//        g.drawImage(image, 0, 0, null);

        FrequencyCal frequencyCal = new FrequencyCal(EntropyList,ep.statisticBinningStrategy);
        ArrayList<Double> valueList = new ArrayList<Double>();
        ArrayList<Double> probList = new ArrayList<Double>();
        frequencyCal.getCCDFBySample(valueList, probList);
        //StatisticGraphGen graphGenerator = StatisticGraphGen.getFrequencyGraphGen(frequencyCal.getBottom(), frequencyCal.getInterval(), frequencyCal.getFrequencyList(), frequencyCal.getTotalSampleSize());
        StatisticGraphGen graphGenerator =new StatisticGraphGen(valueList, probList);
        BufferedImage image = graphGenerator.generateGraphAsLineChart(jPanel1.getWidth(), jPanel1.getHeight(), false, false);
        Graphics g = this.jPanel1.getGraphics();
        g.drawImage(image, 0, 0, null);

//        frequencyCal = new FrequencyCal(JumpLengthList, JumpLengthList.size() / 2);
//        graphGenerator = StatisticGraphGen.getCumulativeFrenquencyGraphGen(frequencyCal.getBottom(), frequencyCal.getInterval(), frequencyCal.getCDFByGroup());
        image = graphGenerator.generateGraphAsLineChart(jPanel2.getWidth(), jPanel2.getHeight(), true, true);
        g = this.jPanel2.getGraphics();
        g.drawImage(image, 0, 0, null);

    }//GEN-LAST:event_jButton3ActionPerformed
    private ArrayList<Double> ROGList = null;
    private ArrayList<Double> JumpLengthList = null;
    private ArrayList<Double> EntropyList = null;

    /**
     * 向TextPane中添加相应的信息列
     * @param component TextPane组件
     * @param info 信息
     * @param infoColor 字的颜色
     */
    private void TextPane_DeclareInfo(JTextPane component, String info, Color infoColor)
    {
        SimpleAttributeSet attset = new SimpleAttributeSet();
        StyleConstants.setForeground(attset, infoColor);
        StyleConstants.setBold(attset, false);

        Document docs = component.getDocument();
        info += "\n";
        try
        {
            docs.insertString(docs.getLength(), info, attset);
        } catch (Exception error)
        {
            System.out.println(error.getMessage());
        } finally
        {
            //System.out.println(component.getAlignmentY());
            component.setAlignmentY(1.0f);
        }
    }

    /**
     * 向TextPane中添加相应的信息列
     * @param component TextPane组件
     * @param info 信息
     * @param infoColor 字的颜色
     * @param isBold 是否为粗体
     */
    private void TextPane_DeclareInfo(JTextPane component, String info, Color infoColor, Boolean isBold)
    {
        SimpleAttributeSet attset = new SimpleAttributeSet();
        StyleConstants.setForeground(attset, infoColor);
        StyleConstants.setBold(attset, isBold);

        Document docs = component.getDocument();
        info += "\n";
        try
        {
            docs.insertString(docs.getLength(), info, attset);
        } catch (Exception error)
        {
            System.out.println(error.getMessage());
        } finally
        {
            //System.out.println(component.getAlignmentY());
            component.setAlignmentY(1.0f);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
