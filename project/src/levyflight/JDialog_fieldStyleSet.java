/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JDialog_fieldStyleSet.java
 *
 * Created on 2011-5-2, 9:18:35
 */
package levyflight;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import levyflight.Data.Field;

/**
 *
 * @author wu
 */
public final class JDialog_fieldStyleSet extends javax.swing.JDialog
{

    /**
     * 用户是否已经选择了新的颜色板
     */
    private boolean isNewColorsSelected = false;
    /**
     * 用户选择的颜色板
     */
    private Color[] renderingColorBand;
    /**
     * 用户选择的nodata渲染色
     */
    private Color noDataColor;

    /**
     * 获取渲染栅格图像所需的颜色
     * @param parent 父控件
     * @param ep 环境变量
     * @return 返回是否需要重新绘制
     */
    public static boolean getFieldColors(java.awt.Frame parent, EnvironmentParameter ep)
    {
        JDialog_fieldStyleSet dialog = new JDialog_fieldStyleSet(parent, true, ep);
        dialog.setVisible(true);
        if (dialog.isNewColorsSelected)
        {
            ep.field_colorBand = dialog.renderingColorBand;
            ep.field_noDataColor = dialog.noDataColor;
            return true;
        } else
        {
            return false;
        }
    }

    /** Creates new form JDialog_fieldStyleSet */
    public JDialog_fieldStyleSet(java.awt.Frame parent, boolean modal, EnvironmentParameter EP)
    {
        super(parent, modal);
        initComponents();
        this.ep = EP;
        this.customColorBand = new ArrayList<Color>();
        this.initalizeColorRampCombox();
        this.checkComponents();
        //this.jLabel_ValueRange.setText("Value Range [" + String.valueOf(minValue) + " , " + String.valueOf(maxValue) + "]");
        this.fieldValueRange = EP.field.getExtremums();
        this.jComboBox_bandSelect.removeAllItems();
        int bandNum = EP.field.getNumBands();
        for (int i = 1; i <= bandNum; i++)
        {
            this.jComboBox_bandSelect.addItem("Band " + String.valueOf(i));
        }
        this.ChangeLabelValueRange();
        this.jComboBox_bandSelect.setEditable(false);

        if (EP.field.getFieldType() == Field.FieldType_GEOTIFF && EP.field.getNumBands() >= 3)
        {
            this.jCheckBox_useRGBStyle.setEnabled(true);
        } else
        {
            this.jCheckBox_useRGBStyle.setEnabled(false);
            this.jCheckBox_useRGBStyle.setSelected(false);
        }
    }

    /**
     * 装载颜色表
     */
    public void initalizeColorRampCombox()
    {
        this.jComboBox_colorRamp.removeAllItems();
        int width = 293;
        int height = 18;
        for (Color[] collorBand : this.colorBandCollection)
        {
            ColorBandIcon icon = new ColorBandIcon(width, height, collorBand);
            this.jComboBox_colorRamp.addItem(icon);
        }
    }

    /**
     *自定义的颜色带
     */
    private class ColorBandIcon implements Icon
    {

        private int width;
        private int height;
        private Color[] colorMap;

        public ColorBandIcon(int Width, int Height, Color[] ColorMap)
        {
            this.width = Width;
            this.height = Height;
            this.colorMap = ColorMap;
        }

        public void paintIcon(Component c, Graphics g, int x, int y)
        {
            Graphics2D g2 = (Graphics2D) g;
            if (this.colorMap == null || this.colorMap.length == 0)
            {
                return;
            }
            if (this.colorMap.length == 1)
            {
                g2.setColor(this.colorMap[0]);
                g2.fillRect(x, y, width, height);
            } else
            {
                for (int i = 0; i < this.colorMap.length - 1; i++)
                {
                    int x0 = (int) ((double) i / (double) (this.colorMap.length - 1) * this.width);
                    int x1 = (int) ((double) (i + 1) / (double) (this.colorMap.length - 1) * this.width);
                    GradientPaint paint = new GradientPaint(x + x0, this.height / 2, this.colorMap[i], x + x1, this.height / 2, this.colorMap[i + 1]);
                    g2.setPaint(paint);
                    g2.fillRect(x + x0, y, x1 - x0, height);
                }
            }
        }

        public int getIconWidth()
        {
            return this.width;
        }

        public int getIconHeight()
        {
            return this.height;
        }
    }

    //自定义的颜色板绘制panel
    private class ColorBandPane extends JPanel
    {

        @Override
        public void repaint()
        {
            ArrayList<Color> colorBand = customColorBand;
            if (colorBand == null || colorBand.isEmpty())
            {
                return;
            }

            if (colorBand.size() == 1)
            {
                Graphics g = this.getGraphics();
                g.setColor(colorBand.get(0));
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
            } else
            {
                Graphics2D g2 = (Graphics2D) this.getGraphics();
                for (int i = 0; i < colorBand.size() - 1; i++)
                {
                    int x0 = (int) ((double) i / (double) (customColorBand.size() - 1) * this.getWidth());
                    int x1 = (int) ((double) (i + 1) / (double) (customColorBand.size() - 1) * this.getWidth());
                    GradientPaint paint = new GradientPaint(x0, this.getHeight() / 2, colorBand.get(i), x1, this.getHeight() / 2, colorBand.get(i + 1));
                    g2.setPaint(paint);
                    g2.fillRect(x0, 0, x1 - x0, this.getHeight());
                }
            }
        }

        @Override
        public void paint(Graphics g)
        {
            ArrayList<Color> colorBand = customColorBand;
            if (colorBand == null || colorBand.isEmpty())
            {
                return;
            }

            if (colorBand.size() == 1)
            {
                g.setColor(colorBand.get(0));
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
            } else
            {
                Graphics2D g2 = (Graphics2D) g;
                int rectWidth = this.getWidth() / (colorBand.size() - 1);
                for (int i = 0; i < colorBand.size() - 1; i++)
                {
                    GradientPaint paint = new GradientPaint(i * rectWidth, this.getHeight() / 2, colorBand.get(i), (i + 1) * rectWidth, this.getHeight() / 2, colorBand.get(i + 1));
                    g2.setPaint(paint);
                    g2.fillRect(i * rectWidth, 0, rectWidth, this.getHeight());
                }
            }
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

        jLabel1 = new javax.swing.JLabel();
        jComboBox_colorRamp = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jCheckBox_Invert = new javax.swing.JCheckBox();
        jCheckBox_Define = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jButton_deleteColor = new javax.swing.JButton();
        jButton_addColor = new javax.swing.JButton();
        jButton_ok = new javax.swing.JButton();
        jButton_cancel = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel_noDataColor = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel_customColorPanel = new ColorBandPane();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel_ValueRange = new javax.swing.JLabel();
        jCheckBox_useRGBStyle = new javax.swing.JCheckBox();
        jComboBox_bandSelect = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(levyflight.LevyFlightApp.class).getContext().getResourceMap(JDialog_fieldStyleSet.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(500, 364));
        setName("Form"); // NOI18N
        getContentPane().setLayout(null);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(20, 90, 108, 15);

        jComboBox_colorRamp.setName("jComboBox_colorRamp"); // NOI18N
        getContentPane().add(jComboBox_colorRamp);
        jComboBox_colorRamp.setBounds(140, 86, 320, 27);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        getContentPane().add(jLabel5);
        jLabel5.setBounds(20, 10, 130, 20);

        jCheckBox_Invert.setText(resourceMap.getString("jCheckBox_Invert.text")); // NOI18N
        jCheckBox_Invert.setName("jCheckBox_Invert"); // NOI18N
        getContentPane().add(jCheckBox_Invert);
        jCheckBox_Invert.setBounds(20, 120, 130, 23);

        jCheckBox_Define.setText(resourceMap.getString("jCheckBox_Define.text")); // NOI18N
        jCheckBox_Define.setName("jCheckBox_Define"); // NOI18N
        jCheckBox_Define.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCheckBox_DefineMouseClicked(evt);
            }
        });
        getContentPane().add(jCheckBox_Define);
        jCheckBox_Define.setBounds(20, 170, 180, 23);

        jSeparator1.setName("jSeparator1"); // NOI18N
        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(10, 260, 470, 10);

        jButton_deleteColor.setText(resourceMap.getString("jButton_deleteColor.text")); // NOI18N
        jButton_deleteColor.setName("jButton_deleteColor"); // NOI18N
        jButton_deleteColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_deleteColorActionPerformed(evt);
            }
        });
        getContentPane().add(jButton_deleteColor);
        jButton_deleteColor.setBounds(340, 170, 110, 23);

        jButton_addColor.setText(resourceMap.getString("jButton_addColor.text")); // NOI18N
        jButton_addColor.setName("jButton_addColor"); // NOI18N
        jButton_addColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_addColorActionPerformed(evt);
            }
        });
        getContentPane().add(jButton_addColor);
        jButton_addColor.setBounds(220, 170, 110, 23);

        jButton_ok.setText(resourceMap.getString("jButton_ok.text")); // NOI18N
        jButton_ok.setName("jButton_ok"); // NOI18N
        jButton_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_okActionPerformed(evt);
            }
        });
        getContentPane().add(jButton_ok);
        jButton_ok.setBounds(290, 290, 80, 23);

        jButton_cancel.setText(resourceMap.getString("jButton_cancel.text")); // NOI18N
        jButton_cancel.setName("jButton_cancel"); // NOI18N
        jButton_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButton_cancel);
        jButton_cancel.setBounds(380, 290, 80, 23);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2);
        jLabel2.setBounds(20, 270, 130, 15);

        jLabel_noDataColor.setText(resourceMap.getString("jLabel_noDataColor.text")); // NOI18N
        jLabel_noDataColor.setToolTipText(resourceMap.getString("jLabel_noDataColor.toolTipText")); // NOI18N
        jLabel_noDataColor.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel_noDataColor.setMinimumSize(new java.awt.Dimension(20, 8));
        jLabel_noDataColor.setName("jLabel_noDataColor"); // NOI18N
        jLabel_noDataColor.setOpaque(true);
        jLabel_noDataColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_noDataColorMouseClicked(evt);
            }
        });
        getContentPane().add(jLabel_noDataColor);
        jLabel_noDataColor.setBounds(150, 270, 43, 16);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel1.setName("jPanel1"); // NOI18N

        jPanel_customColorPanel.setName("jPanel_customColorPanel"); // NOI18N

        javax.swing.GroupLayout jPanel_customColorPanelLayout = new javax.swing.GroupLayout(jPanel_customColorPanel);
        jPanel_customColorPanel.setLayout(jPanel_customColorPanelLayout);
        jPanel_customColorPanelLayout.setHorizontalGroup(
            jPanel_customColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 426, Short.MAX_VALUE)
        );
        jPanel_customColorPanelLayout.setVerticalGroup(
            jPanel_customColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_customColorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_customColorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1);
        jPanel1.setBounds(20, 210, 430, 40);

        jSeparator2.setName("jSeparator2"); // NOI18N
        getContentPane().add(jSeparator2);
        jSeparator2.setBounds(10, 150, 470, 10);

        jSeparator3.setName("jSeparator3"); // NOI18N
        getContentPane().add(jSeparator3);
        jSeparator3.setBounds(10, 70, 470, 10);

        jLabel_ValueRange.setText(resourceMap.getString("jLabel_ValueRange.text")); // NOI18N
        jLabel_ValueRange.setName("jLabel_ValueRange"); // NOI18N
        getContentPane().add(jLabel_ValueRange);
        jLabel_ValueRange.setBounds(20, 40, 440, 20);

        jCheckBox_useRGBStyle.setText(resourceMap.getString("jCheckBox_useRGBStyle.text")); // NOI18N
        jCheckBox_useRGBStyle.setName("jCheckBox_useRGBStyle"); // NOI18N
        jCheckBox_useRGBStyle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCheckBox_useRGBStyleMouseClicked(evt);
            }
        });
        getContentPane().add(jCheckBox_useRGBStyle);
        jCheckBox_useRGBStyle.setBounds(290, 10, 110, 23);

        jComboBox_bandSelect.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox_bandSelect.setName("jComboBox_bandSelect"); // NOI18N
        jComboBox_bandSelect.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox_bandSelectItemStateChanged(evt);
            }
        });
        getContentPane().add(jComboBox_bandSelect);
        jComboBox_bandSelect.setBounds(160, 10, 80, 21);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox_DefineMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jCheckBox_DefineMouseClicked
    {//GEN-HEADEREND:event_jCheckBox_DefineMouseClicked

        this.checkComponents();
    }//GEN-LAST:event_jCheckBox_DefineMouseClicked

    /**
     * 重新检查并设置控件的可用性
     */
    private void checkComponents()
    {
        boolean flag = true;
        flag = this.jCheckBox_useRGBStyle.isSelected();
        if (flag)//如果用户用RGB绘制栅格，底下全部不用绘制
        {
            this.jComboBox_colorRamp.setEnabled(!flag);
            this.jCheckBox_Invert.setEnabled(!flag);
            this.jButton_addColor.setEnabled(!flag);
            this.jButton_deleteColor.setEnabled(!flag);
            this.jComboBox_bandSelect.setEnabled(!flag);
            this.jLabel_noDataColor.setEnabled(!flag);
            this.jCheckBox_Define.setEnabled(!flag);
        } else
        {
            this.jComboBox_bandSelect.setEnabled(!flag);
            this.jLabel_noDataColor.setEnabled(!flag);
            this.jCheckBox_Define.setEnabled(!flag);

            flag = this.jCheckBox_Define.isSelected();
            this.jComboBox_colorRamp.setEnabled(!flag);
            this.jCheckBox_Invert.setEnabled(!flag);
            this.jButton_addColor.setEnabled(flag);
            this.jButton_deleteColor.setEnabled(flag);
        }
    }

    private void ChangeLabelValueRange()
    {
        int index = this.jComboBox_bandSelect.getSelectedIndex();
        if (index < 0)
        {
            return;
        }
        this.jLabel_ValueRange.setText("Value Range [" + String.valueOf(this.fieldValueRange[index * 2]) + " , " + String.valueOf(this.fieldValueRange[index * 2 + 1]) + "]");
    }

    private void jButton_addColorActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_addColorActionPerformed
    {//GEN-HEADEREND:event_jButton_addColorActionPerformed
        Color newColor = JColorChooser.showDialog(null, "Choose one color to add in your custom color band", Color.BLUE);
        if (newColor != null)
        {
            this.customColorBand.add(newColor);
        }
        this.jPanel_customColorPanel.repaint();
    }//GEN-LAST:event_jButton_addColorActionPerformed

    private void jButton_deleteColorActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_deleteColorActionPerformed
    {//GEN-HEADEREND:event_jButton_deleteColorActionPerformed

        if (!this.customColorBand.isEmpty())
        {
            this.customColorBand.remove(this.customColorBand.size() - 1);
            if (this.customColorBand.isEmpty())
            {
                Graphics g = this.jPanel_customColorPanel.getGraphics();
                g.setColor(this.jPanel_customColorPanel.getBackground());
                g.fillRect(0, 0, this.jPanel_customColorPanel.getWidth(), this.jPanel_customColorPanel.getHeight());
            }
        }
        this.jPanel_customColorPanel.repaint();

    }//GEN-LAST:event_jButton_deleteColorActionPerformed

    private void jLabel_noDataColorMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jLabel_noDataColorMouseClicked
    {//GEN-HEADEREND:event_jLabel_noDataColorMouseClicked
        if (!this.jLabel_noDataColor.isEnabled())
        {
            return;
        }
        Color newColor = JColorChooser.showDialog(null, "Choose a new color to render the nodata value region.", this.getBackground());
        if (newColor != null)
        {
            this.jLabel_noDataColor.setBackground(newColor);
        }

    }//GEN-LAST:event_jLabel_noDataColorMouseClicked

    private void jButton_okActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_okActionPerformed
    {//GEN-HEADEREND:event_jButton_okActionPerformed
        if (this.jCheckBox_useRGBStyle.isSelected())//用RGB样式绘制
        {
            this.ep.field.setBandToRender(0);
            this.isNewColorsSelected = true;
        } else//绘制特定的单个波段
        {
            int bandIndex = this.jComboBox_bandSelect.getSelectedIndex() + 1;
            ep.field.setBandToRender(bandIndex);
            if (this.jCheckBox_Define.isSelected())//用户自定义颜色板
            {
                if (this.customColorBand.size() < 2)
                {
                    JOptionPane.showMessageDialog(null, "Custom colorRamp's colors number can't be less than 2 !", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                this.renderingColorBand = new Color[this.customColorBand.size()];
                for (int i = 0; i < this.renderingColorBand.length; i++)
                {
                    this.renderingColorBand[i] = this.customColorBand.get(i);
                }

            } else//用户选择了现有的颜色板
            {
                int index = this.jComboBox_colorRamp.getSelectedIndex();
                this.renderingColorBand = this.colorBandCollection[index];
                if (this.jCheckBox_Invert.isSelected())
                {
                    Color[] invertColorBand = new Color[this.renderingColorBand.length];
                    for (int i = 0; i < invertColorBand.length; i++)
                    {
                        invertColorBand[i] = this.renderingColorBand[this.renderingColorBand.length - 1 - i];
                    }
                    this.renderingColorBand = invertColorBand;
                }
            }
            this.noDataColor = this.jLabel_noDataColor.getBackground();
            this.isNewColorsSelected = true;
        }
        this.dispose();
    }//GEN-LAST:event_jButton_okActionPerformed

    private void jButton_cancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_cancelActionPerformed
    {//GEN-HEADEREND:event_jButton_cancelActionPerformed
        this.isNewColorsSelected = false;
        this.dispose();
    }//GEN-LAST:event_jButton_cancelActionPerformed

    private void jCheckBox_useRGBStyleMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jCheckBox_useRGBStyleMouseClicked
    {//GEN-HEADEREND:event_jCheckBox_useRGBStyleMouseClicked
        this.checkComponents();
    }//GEN-LAST:event_jCheckBox_useRGBStyleMouseClicked

    private void jComboBox_bandSelectItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_jComboBox_bandSelectItemStateChanged
    {//GEN-HEADEREND:event_jComboBox_bandSelectItemStateChanged
        this.ChangeLabelValueRange();
    }//GEN-LAST:event_jComboBox_bandSelectItemStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_addColor;
    private javax.swing.JButton jButton_cancel;
    private javax.swing.JButton jButton_deleteColor;
    private javax.swing.JButton jButton_ok;
    private javax.swing.JCheckBox jCheckBox_Define;
    private javax.swing.JCheckBox jCheckBox_Invert;
    private javax.swing.JCheckBox jCheckBox_useRGBStyle;
    private javax.swing.JComboBox jComboBox_bandSelect;
    private javax.swing.JComboBox jComboBox_colorRamp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel_ValueRange;
    private javax.swing.JLabel jLabel_noDataColor;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_customColorPanel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    // End of variables declaration//GEN-END:variables
    /**
     * 用户自定义的颜色表带
     */
    private ArrayList<Color> customColorBand;
    /**
     * field各个波段的取值范围
     */
    private double[] fieldValueRange;
    /**
     * 环境变量
     */
    private EnvironmentParameter ep;
    /**
     * 通过硬编码预定义一组颜色表
     */
    private final Color[][] colorBandCollection = new Color[][]
    {
        new Color[]
        {
            Color.BLACK, Color.WHITE
        },
        new Color[]
        {
            new Color(200, 200, 255), Color.BLUE
        },
        new Color[]
        {
            new Color(200, 255, 200), new Color(0, 102, 0)
        },
        new Color[]
        {
            new Color(255, 200, 200), new Color(102, 0, 0)
        },
        new Color[]
        {
            new Color(255, 255, 180), new Color(102, 102, 0)
        },
        new Color[]
        {
            new Color(102, 102, 0), new Color(255, 235, 200), new Color(102, 0, 0)
        },
        new Color[]
        {
            Color.blue, Color.cyan, Color.yellow, Color.red
        },
        new Color[]
        {
            new Color(0, 255, 255), new Color(0, 0, 255)
        },
        new Color[]
        {
            new Color(255, 255, 204), new Color(255, 255, 0), new Color(255, 0, 0), new Color(102, 0, 204), new Color(0, 0, 255)
        },
        new Color[]
        {
            new Color(102, 51, 0), new Color(255, 255, 102), new Color(0, 0, 153)
        },
        new Color[]
        {
            new Color(255, 51, 51), new Color(255, 255, 51), new Color(51, 255, 51)
        },
        new Color[]
        {
            new Color(255, 204, 204), new Color(255, 51, 204), new Color(0, 0, 255), new Color(0, 255, 255), new Color(0, 255, 0), new Color(255, 255, 0), new Color(153, 153, 0), new Color(102, 51, 0)
        },
    };
}
