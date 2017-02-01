/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JDialog_GeneraTra.java
 *
 * Created on 2011-4-5, 16:44:24
 */
package levyflight;

import Static.StaticLib;
import TraGen.TC_PolygonRegion;
import TraGen.TC_Population;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import levyflight.Data.MBR;
import levyflight.Data.Node;
import levyflight.Data.TimePrecisionEnum;
import levyflight.Data.TrajectoryPool;
import levyflight.Import.ExpTraGen;
import levyflight.Import.GaussianTraGen;
import levyflight.Import.MethodLib;
import levyflight.Import.PowerLawTraGen;
import levyflight.Transformer.NumericalTransformer;
import levyflight.Transformer.NumericalTransformer_Linear;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.swing.JMapPane;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Administrator
 */
public class JDialog_GaussianTraGen extends javax.swing.JDialog
{

    private final String label_union = "Union of all";
    private final String lable_areaConstraint = "Area constraint";
    private final String label_linearConstraint = "Linear constraint";
    private final String label_field = "Field";
    private EnvironmentParameter ep;
    private CoordinateReferenceSystem CRS;
    private TrajectoryPool resultPool;
    private File trajectoryFile;
    private TC_Population populationConstraint = null;
    private NumericalTransformer transformer = new NumericalTransformer_Linear(1, 0);

    /**
     * 通过对话框获取服从幂律分布的TrajectoryPool
     * @param parent 父容器
     * @param modal 是否在最前显示
     * @param crs 坐标系统
     * @return
     */
    public static File GetTrajectoryPool(java.awt.Frame parent, boolean modal, EnvironmentParameter environmentParameter, CoordinateReferenceSystem crs) throws Exception
    {
        JDialog_GaussianTraGen generator = new JDialog_GaussianTraGen(parent, modal, environmentParameter, crs);
        generator.setVisible(true);
        if (generator != null)
        {
            return MethodLib.TrajectoryPoolToSHPFile(generator.resultPool, generator.trajectoryFile);
        } else
        {
            return null;
        }
    }

    /** Creates new form JDialog_GeneraTra */
    private JDialog_GaussianTraGen(java.awt.Frame parent, boolean modal, EnvironmentParameter environmentParameter, CoordinateReferenceSystem crs)
    {
        super(parent, modal);
        initComponents();
        this.ep = environmentParameter;
        this.CRS = crs;
        this.trajectoryFile = null;
        this.setTitle("Gaussian/Normally distribution trajectory generating parameters set");
        this.jComboBox_layers.removeAllItems();
        this.jComboBox_layers.addItem(this.label_union);
        this.jComboBox_layers.addItem(this.lable_areaConstraint);
        this.jComboBox_layers.addItem(this.label_linearConstraint);
        this.jComboBox_layers.addItem(this.label_field);
        this.jLabel_notice.setText("<html>Notice: The length unit used in the generating process is according to the current coordinate reference system (you can check the Option->show current  coordinate reference system to see it if you like). If current CRS is null, then, the simulation dosen't use specific unit.");
        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
        this.jFormattedTextField_date.setText(dateFormat.format(time));
        this.jFormattedTextField_time.setText(timeFormat.format(time));
        this.jFormattedTextField_top.setEnabled(this.jCheckBox_setBoundry.isSelected());
        this.jFormattedTextField_bottom.setEnabled(this.jCheckBox_setBoundry.isSelected());
        this.jFormattedTextField_left.setEnabled(this.jCheckBox_setBoundry.isSelected());
        this.jFormattedTextField_right.setEnabled(this.jCheckBox_setBoundry.isSelected());
        this.jRadioButton_sameLocation.setSelected(true);
        if (StaticLib.InitialFileDirectory != null)
        {
            String path = StaticLib.InitialFileDirectory.getAbsolutePath();
            path = path.substring(0, path.lastIndexOf("\\")) + "\\TrajectoryFile.shp";
            this.jTextField_filePath.setText(path);
        }
        this.CheckStartLocationComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup_startLocationGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        SpinnerNumberModel mode=new SpinnerNumberModel();
        mode.setMaximum(1000);
        mode.setMinimum(1);
        mode.setStepSize(1);
        mode.setValue(1);
        jSpinner_trajectoryNum = new JSpinner(mode);
        SpinnerNumberModel mode2=new SpinnerNumberModel();
        mode2.setMaximum(1000000);
        mode2.setMinimum(2);
        mode2.setStepSize(1);
        mode2.setValue(100);
        jSpinner_nodeNum = new JSpinner(mode2);
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jFormattedTextField_date = new javax.swing.JFormattedTextField();
        jFormattedTextField_time = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField_velocity = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jFormattedTextField_locationX = new javax.swing.JFormattedTextField();
        jFormattedTextField_locationY = new javax.swing.JFormattedTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jFormattedTextField_variance = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jFormattedTextField_average = new javax.swing.JFormattedTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        jComboBox_layers = new javax.swing.JComboBox();
        jLabel_top = new javax.swing.JLabel();
        jLabel_left = new javax.swing.JLabel();
        jLabel_right = new javax.swing.JLabel();
        jLabel_bottom = new javax.swing.JLabel();
        jCheckBox_setBoundry = new javax.swing.JCheckBox();
        jFormattedTextField_top = new javax.swing.JFormattedTextField();
        jFormattedTextField_left = new javax.swing.JFormattedTextField();
        jFormattedTextField_right = new javax.swing.JFormattedTextField();
        jFormattedTextField_bottom = new javax.swing.JFormattedTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel_notice = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jButton_cancel = new javax.swing.JButton();
        jButton_ok = new javax.swing.JButton();
        jLabel_spanLat = new javax.swing.JLabel();
        jLabel_spanLon = new javax.swing.JLabel();
        jRadioButton_sameLocation = new javax.swing.JRadioButton();
        jRadioButton_randomLocation = new javax.swing.JRadioButton();
        jFormattedTextField_pointTop = new javax.swing.JFormattedTextField();
        jFormattedTextField_pointLeft = new javax.swing.JFormattedTextField();
        jFormattedTextField_pointRight = new javax.swing.JFormattedTextField();
        jFormattedTextField_pointBottom = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField_filePath = new javax.swing.JTextField();
        jButton_browse = new javax.swing.JButton();
        jCheckBox_populationConstraint = new javax.swing.JCheckBox();
        jSeparator5 = new javax.swing.JSeparator();
        jCheckBox_polygonConstraint = new javax.swing.JCheckBox();
        jButton_setConstraintDetails = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(levyflight.LevyFlightApp.class).getContext().getResourceMap(JDialog_GaussianTraGen.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(575, 770));
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 13, -1, -1));

        jSpinner_trajectoryNum.setName("jSpinner_trajectoryNum"); // NOI18N
        getContentPane().add(jSpinner_trajectoryNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(122, 10, 68, -1));

        jSpinner_nodeNum.setName("jSpinner_nodeNum"); // NOI18N
        getContentPane().add(jSpinner_nodeNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(378, 10, 67, -1));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(224, 13, -1, -1));

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, 20));

        jFormattedTextField_date.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd"))));
        jFormattedTextField_date.setName("jFormattedTextField_date"); // NOI18N
        getContentPane().add(jFormattedTextField_date, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, 79, -1));

        jFormattedTextField_time.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("HH:mm:ss"))));
        jFormattedTextField_time.setName("jFormattedTextField_time"); // NOI18N
        getContentPane().add(jFormattedTextField_time, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 50, 79, -1));

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        jFormattedTextField_velocity.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0###############"))));
        jFormattedTextField_velocity.setText(resourceMap.getString("jFormattedTextField_velocity.text")); // NOI18N
        jFormattedTextField_velocity.setName("jFormattedTextField_velocity"); // NOI18N
        getContentPane().add(jFormattedTextField_velocity, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 89, 92, -1));

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 90, -1, -1));

        jLabel6.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 330, -1, -1));

        jFormattedTextField_locationX.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jFormattedTextField_locationX.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0###############"))));
        jFormattedTextField_locationX.setText(resourceMap.getString("jFormattedTextField_locationX.text")); // NOI18N
        jFormattedTextField_locationX.setName("jFormattedTextField_locationX"); // NOI18N
        getContentPane().add(jFormattedTextField_locationX, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 380, 108, -1));

        jFormattedTextField_locationY.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jFormattedTextField_locationY.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0###############"))));
        jFormattedTextField_locationY.setText(resourceMap.getString("jFormattedTextField_locationY.text")); // NOI18N
        jFormattedTextField_locationY.setName("jFormattedTextField_locationY"); // NOI18N
        getContentPane().add(jFormattedTextField_locationY, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 410, 108, -1));

        jSeparator1.setName("jSeparator1"); // NOI18N
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 122, 565, 0));

        jLabel7.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 382, -1, -1));

        jLabel8.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 412, -1, -1));

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 130, -1, -1));

        jFormattedTextField_variance.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0###############"))));
        jFormattedTextField_variance.setText(resourceMap.getString("jFormattedTextField_variance.text")); // NOI18N
        jFormattedTextField_variance.setName("jFormattedTextField_variance"); // NOI18N
        getContentPane().add(jFormattedTextField_variance, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 130, 79, -1));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, -1));

        jFormattedTextField_average.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0###############"))));
        jFormattedTextField_average.setText(resourceMap.getString("jFormattedTextField_average.text")); // NOI18N
        jFormattedTextField_average.setName("jFormattedTextField_average"); // NOI18N
        getContentPane().add(jFormattedTextField_average, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 130, 79, -1));

        jSeparator2.setName("jSeparator2"); // NOI18N
        getContentPane().add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 159, 565, 10));

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, -1, -1));

        jComboBox_layers.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox_layers.setName("jComboBox_layers"); // NOI18N
        jComboBox_layers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_layersActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBox_layers, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 200, 125, -1));

        jLabel_top.setText(resourceMap.getString("jLabel_top.text")); // NOI18N
        jLabel_top.setName("jLabel_top"); // NOI18N
        getContentPane().add(jLabel_top, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 240, 150, -1));

        jLabel_left.setText(resourceMap.getString("jLabel_left.text")); // NOI18N
        jLabel_left.setName("jLabel_left"); // NOI18N
        getContentPane().add(jLabel_left, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, 120, -1));

        jLabel_right.setText(resourceMap.getString("jLabel_right.text")); // NOI18N
        jLabel_right.setName("jLabel_right"); // NOI18N
        getContentPane().add(jLabel_right, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 260, 100, -1));

        jLabel_bottom.setText(resourceMap.getString("jLabel_bottom.text")); // NOI18N
        jLabel_bottom.setName("jLabel_bottom"); // NOI18N
        getContentPane().add(jLabel_bottom, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 290, 140, -1));

        jCheckBox_setBoundry.setForeground(resourceMap.getColor("jFormattedTextField_bottom.foreground")); // NOI18N
        jCheckBox_setBoundry.setText(resourceMap.getString("jCheckBox_setBoundry.text")); // NOI18N
        jCheckBox_setBoundry.setName("jCheckBox_setBoundry"); // NOI18N
        jCheckBox_setBoundry.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCheckBox_setBoundryMouseClicked(evt);
            }
        });
        getContentPane().add(jCheckBox_setBoundry, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 200, -1, 21));

        jFormattedTextField_top.setForeground(resourceMap.getColor("jFormattedTextField_bottom.foreground")); // NOI18N
        jFormattedTextField_top.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0###############"))));
        jFormattedTextField_top.setName("jFormattedTextField_top"); // NOI18N
        getContentPane().add(jFormattedTextField_top, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 230, 110, -1));

        jFormattedTextField_left.setForeground(resourceMap.getColor("jFormattedTextField_bottom.foreground")); // NOI18N
        jFormattedTextField_left.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0###############"))));
        jFormattedTextField_left.setName("jFormattedTextField_left"); // NOI18N
        getContentPane().add(jFormattedTextField_left, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 260, 100, -1));

        jFormattedTextField_right.setForeground(resourceMap.getColor("jFormattedTextField_bottom.foreground")); // NOI18N
        jFormattedTextField_right.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0###############"))));
        jFormattedTextField_right.setName("jFormattedTextField_right"); // NOI18N
        getContentPane().add(jFormattedTextField_right, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 260, 110, -1));

        jFormattedTextField_bottom.setForeground(resourceMap.getColor("jFormattedTextField_bottom.foreground")); // NOI18N
        jFormattedTextField_bottom.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0###############"))));
        jFormattedTextField_bottom.setName("jFormattedTextField_bottom"); // NOI18N
        getContentPane().add(jFormattedTextField_bottom, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 290, 110, -1));

        jSeparator3.setName("jSeparator3"); // NOI18N
        getContentPane().add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 580, 565, 10));

        jLabel_notice.setForeground(resourceMap.getColor("jLabel_notice.foreground")); // NOI18N
        jLabel_notice.setText(resourceMap.getString("jLabel_notice.text")); // NOI18N
        jLabel_notice.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel_notice.setName("jLabel_notice"); // NOI18N
        getContentPane().add(jLabel_notice, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 590, 555, 49));

        jSeparator4.setName("jSeparator4"); // NOI18N
        getContentPane().add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 650, 565, 12));

        jButton_cancel.setText(resourceMap.getString("jButton_cancel.text")); // NOI18N
        jButton_cancel.setName("jButton_cancel"); // NOI18N
        jButton_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButton_cancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 710, -1, -1));

        jButton_ok.setText(resourceMap.getString("jButton_ok.text")); // NOI18N
        jButton_ok.setName("jButton_ok"); // NOI18N
        jButton_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_okActionPerformed(evt);
            }
        });
        getContentPane().add(jButton_ok, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 710, 72, -1));

        jLabel_spanLat.setText(resourceMap.getString("jLabel_spanLat.text")); // NOI18N
        jLabel_spanLat.setName("jLabel_spanLat"); // NOI18N
        getContentPane().add(jLabel_spanLat, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 480, 250, -1));

        jLabel_spanLon.setText(resourceMap.getString("jLabel_spanLon.text")); // NOI18N
        jLabel_spanLon.setName("jLabel_spanLon"); // NOI18N
        getContentPane().add(jLabel_spanLon, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 480, 270, -1));

        buttonGroup_startLocationGroup.add(jRadioButton_sameLocation);
        jRadioButton_sameLocation.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jRadioButton_sameLocation.setText(resourceMap.getString("jRadioButton_sameLocation.text")); // NOI18N
        jRadioButton_sameLocation.setName("jRadioButton_sameLocation"); // NOI18N
        jRadioButton_sameLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadioButton_sameLocationMouseClicked(evt);
            }
        });
        getContentPane().add(jRadioButton_sameLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 350, -1, -1));

        buttonGroup_startLocationGroup.add(jRadioButton_randomLocation);
        jRadioButton_randomLocation.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jRadioButton_randomLocation.setText(resourceMap.getString("jRadioButton_randomLocation.text")); // NOI18N
        jRadioButton_randomLocation.setName("jRadioButton_randomLocation"); // NOI18N
        jRadioButton_randomLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadioButton_randomLocationMouseClicked(evt);
            }
        });
        getContentPane().add(jRadioButton_randomLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 350, 325, -1));

        jFormattedTextField_pointTop.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jFormattedTextField_pointTop.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0###############"))));
        jFormattedTextField_pointTop.setName("jFormattedTextField_pointTop"); // NOI18N
        getContentPane().add(jFormattedTextField_pointTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 380, 110, -1));

        jFormattedTextField_pointLeft.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jFormattedTextField_pointLeft.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0###############"))));
        jFormattedTextField_pointLeft.setName("jFormattedTextField_pointLeft"); // NOI18N
        getContentPane().add(jFormattedTextField_pointLeft, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 410, 100, -1));

        jFormattedTextField_pointRight.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jFormattedTextField_pointRight.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0###############"))));
        jFormattedTextField_pointRight.setName("jFormattedTextField_pointRight"); // NOI18N
        getContentPane().add(jFormattedTextField_pointRight, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 410, 110, -1));

        jFormattedTextField_pointBottom.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jFormattedTextField_pointBottom.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0###############"))));
        jFormattedTextField_pointBottom.setName("jFormattedTextField_pointBottom"); // NOI18N
        getContentPane().add(jFormattedTextField_pointBottom, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 440, 110, -1));

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 670, -1, 21));

        jTextField_filePath.setText(resourceMap.getString("jTextField_filePath.text")); // NOI18N
        jTextField_filePath.setName("jTextField_filePath"); // NOI18N
        getContentPane().add(jTextField_filePath, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 670, 282, -1));

        jButton_browse.setText(resourceMap.getString("jButton_browse.text")); // NOI18N
        jButton_browse.setName("jButton_browse"); // NOI18N
        jButton_browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_browseActionPerformed(evt);
            }
        });
        getContentPane().add(jButton_browse, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 670, -1, -1));

        jCheckBox_populationConstraint.setText(resourceMap.getString("jCheckBox_populationConstraint.text")); // NOI18N
        jCheckBox_populationConstraint.setEnabled(false);
        jCheckBox_populationConstraint.setName("jCheckBox_populationConstraint"); // NOI18N
        getContentPane().add(jCheckBox_populationConstraint, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 550, -1, -1));

        jSeparator5.setName("jSeparator5"); // NOI18N
        getContentPane().add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 540, 565, 10));

        jCheckBox_polygonConstraint.setText(resourceMap.getString("jCheckBox_polygonConstraint.text")); // NOI18N
        jCheckBox_polygonConstraint.setEnabled(false);
        jCheckBox_polygonConstraint.setName("jCheckBox_polygonConstraint"); // NOI18N
        getContentPane().add(jCheckBox_polygonConstraint, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 510, -1, -1));

        jButton_setConstraintDetails.setText(resourceMap.getString("jButton_setConstraintDetails.text")); // NOI18N
        jButton_setConstraintDetails.setEnabled(false);
        jButton_setConstraintDetails.setName("jButton_setConstraintDetails"); // NOI18N
        jButton_setConstraintDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_setConstraintDetailsActionPerformed(evt);
            }
        });
        getContentPane().add(jButton_setConstraintDetails, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 550, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox_setBoundryMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jCheckBox_setBoundryMouseClicked
    {//GEN-HEADEREND:event_jCheckBox_setBoundryMouseClicked
        this.jFormattedTextField_top.setEnabled(this.jCheckBox_setBoundry.isSelected());
        this.jFormattedTextField_bottom.setEnabled(this.jCheckBox_setBoundry.isSelected());
        this.jFormattedTextField_left.setEnabled(this.jCheckBox_setBoundry.isSelected());
        this.jFormattedTextField_right.setEnabled(this.jCheckBox_setBoundry.isSelected());
    }//GEN-LAST:event_jCheckBox_setBoundryMouseClicked

    private void jComboBox_layersActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBox_layersActionPerformed
    {//GEN-HEADEREND:event_jComboBox_layersActionPerformed
        if (this.jComboBox_layers.getItemCount() == 0)
        {
            return;
        }
        MBR boundry = null;
        if ((this.jComboBox_layers.getSelectedItem().toString()).equals(this.label_union))
        {
            if (this.ep.field != null)
            {
                boundry = MBR.Clone(this.ep.field.Boundary);
            }
            if (this.ep.road != null)
            {
                if (boundry != null)
                {
                    boundry.Union(this.ep.road.Boundary);
                } else
                {
                    boundry = MBR.Clone(this.ep.road.Boundary);
                }
            }
            if (this.ep.polygonImporter != null)
            {
                if (boundry != null)
                {
                    boundry.Union(this.ep.polygonImporter.getMBR());
                } else
                {
                    boundry = MBR.Clone(this.ep.polygonImporter.getMBR());
                }
            }
        } else if (((String) this.jComboBox_layers.getSelectedItem()).equals(this.label_field))
        {
            if (this.ep.field != null)
            {
                boundry = MBR.Clone(this.ep.field.Boundary);
            }
        } else if (((String) this.jComboBox_layers.getSelectedItem()).equals(this.label_linearConstraint))
        {
            if (this.ep.road != null)
            {
                boundry = MBR.Clone(this.ep.road.Boundary);
            }
        } else if (((String) this.jComboBox_layers.getSelectedItem()).equals(this.lable_areaConstraint))
        {
            boundry = MBR.Clone(ep.polygonImporter.getMBR());
        }


        if (boundry != null)
        {
            this.jLabel_top.setText(String.valueOf(boundry.Top));
            this.jLabel_bottom.setText(String.valueOf(boundry.Bottom));
            this.jLabel_left.setText(String.valueOf(boundry.Left));
            this.jLabel_right.setText(String.valueOf(boundry.Right));
            this.jFormattedTextField_top.setText(String.valueOf(boundry.Top));
            this.jFormattedTextField_bottom.setText(String.valueOf(boundry.Bottom));
            this.jFormattedTextField_left.setText(String.valueOf(boundry.Left));
            this.jFormattedTextField_right.setText(String.valueOf(boundry.Right));
            this.jLabel_spanLat.setText("Horizonal span:" + String.valueOf(boundry.GetLatitudinalSpan()));
            this.jLabel_spanLon.setText("Vertical span:" + String.valueOf(boundry.GetLongitudinalSpan()));
            this.jFormattedTextField_locationX.setText(String.valueOf((boundry.Left + boundry.Right) / 2));
            this.jFormattedTextField_locationY.setText(String.valueOf((boundry.Top + boundry.Bottom) / 2));
            this.jFormattedTextField_pointTop.setText(String.valueOf(boundry.Top));
            this.jFormattedTextField_pointBottom.setText(String.valueOf(boundry.Bottom));
            this.jFormattedTextField_pointLeft.setText(String.valueOf(boundry.Left));
            this.jFormattedTextField_pointRight.setText(String.valueOf(boundry.Right));
        } else
        {
            this.jLabel_top.setText("null");
            this.jLabel_bottom.setText("null");
            this.jLabel_left.setText("null");
            this.jLabel_right.setText("null");
            this.jFormattedTextField_top.setText("");
            this.jFormattedTextField_bottom.setText("");
            this.jFormattedTextField_left.setText("");
            this.jFormattedTextField_right.setText("");
            this.jLabel_spanLat.setText("Horizonal span:null");
            this.jLabel_spanLon.setText("Vertical span:null");
            this.jFormattedTextField_locationX.setText("0");
            this.jFormattedTextField_locationY.setText("0");
            this.jFormattedTextField_pointTop.setText("");
            this.jFormattedTextField_pointBottom.setText("");
            this.jFormattedTextField_pointLeft.setText("");
            this.jFormattedTextField_pointRight.setText("");
        }
    }//GEN-LAST:event_jComboBox_layersActionPerformed

    private void jButton_cancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_cancelActionPerformed
    {//GEN-HEADEREND:event_jButton_cancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton_cancelActionPerformed

    private void jButton_okActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_okActionPerformed
    {//GEN-HEADEREND:event_jButton_okActionPerformed
        try
        {
            int traNum = Integer.parseInt(this.jSpinner_trajectoryNum.getValue().toString());
            int nodeNum = Integer.parseInt(this.jSpinner_nodeNum.getValue().toString());
            String time_s = this.jFormattedTextField_date.getText() + " " + this.jFormattedTextField_time.getText();
            Date beginTime = StaticLib.LevyFlightDateFormat.parse(time_s);
            Calendar startTime = Calendar.getInstance();
            startTime.setTime(beginTime);
            double velocity = Double.parseDouble(this.jFormattedTextField_velocity.getText());
            if (velocity <= 0)
            {
                JOptionPane.showMessageDialog(this, "velocity should be greater than 0", "input error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean useSameStartPoint = this.jRadioButton_sameLocation.isSelected();
            double startX = 0;
            double startY = 0;
            MBR startPointLimit = null;
            if (useSameStartPoint)
            {
                startX = Double.parseDouble(this.jFormattedTextField_locationX.getText());
                startY = Double.parseDouble(this.jFormattedTextField_locationY.getText());
            } else
            {
                startPointLimit = new MBR();
                startPointLimit.RefreshBoundary(Double.parseDouble(this.jFormattedTextField_pointLeft.getText()), Double.parseDouble(this.jFormattedTextField_pointBottom.getText()));
                startPointLimit.RefreshBoundary(Double.parseDouble(this.jFormattedTextField_pointRight.getText()), Double.parseDouble(this.jFormattedTextField_pointTop.getText()));
            }
            double average = Double.parseDouble(this.jFormattedTextField_average.getText());
            if (average <= 0)
            {
                JOptionPane.showMessageDialog(this, "x_bottom should be greater than 0", "input error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double variance = Double.parseDouble(this.jFormattedTextField_variance.getText());
            if (variance <= 0)
            {
                JOptionPane.showMessageDialog(this, "exponent should be greater than 0", "input error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean useRegionLimit = this.jCheckBox_setBoundry.isSelected();
            MBR regionLimit = null;
            if (useRegionLimit)
            {
                regionLimit = new MBR();
                regionLimit.RefreshBoundary(Double.parseDouble(this.jFormattedTextField_left.getText()), Double.parseDouble(this.jFormattedTextField_bottom.getText()));
                regionLimit.RefreshBoundary(Double.parseDouble(this.jFormattedTextField_right.getText()), Double.parseDouble(this.jFormattedTextField_top.getText()));
                if (useSameStartPoint && !regionLimit.IsPointIn(startX, startY))
                {
                    JOptionPane.showMessageDialog(this, "trajectory start point is not in the boundry limit!", "input error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            String path = this.jTextField_filePath.getText();
            if (path.length() < 3)
            {
                JOptionPane.showMessageDialog(this, "Trajectory file save path incorrect, please check again", "input error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (path.length() < 5)
            {
                path += ".shp";
            } else
            {
                String extention = path.substring(path.length() - 4);
                if (!extention.equals(".shp"))
                {
                    path += ".shp";
                }
            }
            this.trajectoryFile = new File(path);
            if (this.trajectoryFile == null)
            {
                JOptionPane.showMessageDialog(this, "Trajectory file save path incorrect, please check again", "input error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (this.jCheckBox_populationConstraint.isSelected())
            {
                this.populationConstraint = new TC_Population(this.ep.field, this.transformer);
            }

            TC_PolygonRegion polygonRegionConstraint = null;//多边形位置约束
            if (this.jCheckBox_polygonConstraint.isEnabled() && this.jCheckBox_polygonConstraint.isSelected())
            {
                polygonRegionConstraint = new TC_PolygonRegion(ep.polygonImporter.getPolygonUnion());
            }

            GaussianTraGen generator;
            if (useRegionLimit && useSameStartPoint)
            {
                Node startNode = new Node(startX, startY, startTime);
                generator = new GaussianTraGen(average, variance, startNode, velocity, traNum, nodeNum, regionLimit, this.populationConstraint, polygonRegionConstraint);
            } else if (!useRegionLimit && useSameStartPoint)
            {
                Node startNode = new Node(startX, startY, startTime);
                generator = new GaussianTraGen(average, variance, startNode, velocity, traNum, nodeNum, this.populationConstraint, polygonRegionConstraint);
            } else if (useRegionLimit && !useSameStartPoint)
            {
                if (!startPointLimit.IsHasOverlapWith(regionLimit))
                {
                    JOptionPane.showMessageDialog(this, "Start location region has no overlap with boundry limit region.\nGeneration failed.", "input error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                generator = new GaussianTraGen(average, variance, startPointLimit, startTime, velocity, traNum, nodeNum, regionLimit, this.populationConstraint, polygonRegionConstraint);
            } else
            {
                generator = new GaussianTraGen(average, variance, startPointLimit, startTime, velocity, traNum, nodeNum, this.populationConstraint, polygonRegionConstraint);
            }
            //JOptionPane.showMessageDialog(null, "hello");
            generator.CoordinateReference = this.CRS;
            //ep.trajectoryPool = new TrajectoryPool(ep.generalCRS, TimePrecisionEnum.SECOND);
            TrajectoryPool trajectoryPool = new TrajectoryPool(generator.CoordinateReference, TimePrecisionEnum.SECOND);
            //ep.trajectoryPool.Fill(generator);
            trajectoryPool.Fill(generator);
            this.resultPool = trajectoryPool;
            //File shpFile = MethodLib.TrajectoryPoolToSHPFile(ep.trajectoryPool);
            //ep.generalCRS = null;
            //ControlLib.MapPane_addSHPTrajectoryAction(mapPane, ep, shpFile);

            this.dispose();


        } catch (Exception ex)
        {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
            return;
        }


    }//GEN-LAST:event_jButton_okActionPerformed

    private void jRadioButton_sameLocationMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jRadioButton_sameLocationMouseClicked
    {//GEN-HEADEREND:event_jRadioButton_sameLocationMouseClicked
        this.CheckStartLocationComponents();
    }//GEN-LAST:event_jRadioButton_sameLocationMouseClicked

    private void jRadioButton_randomLocationMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jRadioButton_randomLocationMouseClicked
    {//GEN-HEADEREND:event_jRadioButton_randomLocationMouseClicked
        this.CheckStartLocationComponents();
    }//GEN-LAST:event_jRadioButton_randomLocationMouseClicked

    private void jButton_browseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_browseActionPerformed
    {//GEN-HEADEREND:event_jButton_browseActionPerformed
        JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
        chooser.setDialogTitle("Save generated trajectorypool as shapefile");
        if (StaticLib.InitialFileDirectory != null && StaticLib.InitialFileDirectory.exists())
        {
            chooser.setSaveFile(StaticLib.InitialFileDirectory);
        }
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileDataStoreChooser.APPROVE_OPTION)
        {
            this.jTextField_filePath.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_jButton_browseActionPerformed

    private void jButton_setConstraintDetailsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_setConstraintDetailsActionPerformed
    {//GEN-HEADEREND:event_jButton_setConstraintDetailsActionPerformed
        this.transformer = JDialog_numericalTransformSet.getNumericalTransformer(null, ep);
}//GEN-LAST:event_jButton_setConstraintDetailsActionPerformed

    /**
     * 根据单选按钮的变化情况来调整关联控件是否可用
     */
    private void CheckStartLocationComponents()
    {
        boolean flag = this.jRadioButton_sameLocation.isSelected();
        this.jFormattedTextField_locationX.setEnabled(flag);
        this.jFormattedTextField_locationY.setEnabled(flag);
        flag = this.jRadioButton_randomLocation.isSelected();
        this.jFormattedTextField_pointTop.setEnabled(flag);
        this.jFormattedTextField_pointBottom.setEnabled(flag);
        this.jFormattedTextField_pointLeft.setEnabled(flag);
        this.jFormattedTextField_pointRight.setEnabled(flag);
        if (this.ep.field != null)
        {
            this.jCheckBox_populationConstraint.setEnabled(true);
            this.jButton_setConstraintDetails.setEnabled(true);
        }
        if (this.ep.polygonImporter != null)
        {
            this.jCheckBox_polygonConstraint.setEnabled(true);
        }
    }
    /**
     * @param args the command line arguments
     */
//    public static void main(String args[])
//    {
//        java.awt.EventQueue.invokeLater(new Runnable()
//        {
//
//            public void run()
//            {
//                JDialog_PowerLawTraGen dialog = new JDialog_PowerLawTraGen(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter()
//                {
//
//                    public void windowClosing(java.awt.event.WindowEvent e)
//                    {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup_startLocationGroup;
    private javax.swing.JButton jButton_browse;
    private javax.swing.JButton jButton_cancel;
    private javax.swing.JButton jButton_ok;
    private javax.swing.JButton jButton_setConstraintDetails;
    private javax.swing.JCheckBox jCheckBox_polygonConstraint;
    private javax.swing.JCheckBox jCheckBox_populationConstraint;
    private javax.swing.JCheckBox jCheckBox_setBoundry;
    private javax.swing.JComboBox jComboBox_layers;
    private javax.swing.JFormattedTextField jFormattedTextField_average;
    private javax.swing.JFormattedTextField jFormattedTextField_bottom;
    private javax.swing.JFormattedTextField jFormattedTextField_date;
    private javax.swing.JFormattedTextField jFormattedTextField_left;
    private javax.swing.JFormattedTextField jFormattedTextField_locationX;
    private javax.swing.JFormattedTextField jFormattedTextField_locationY;
    private javax.swing.JFormattedTextField jFormattedTextField_pointBottom;
    private javax.swing.JFormattedTextField jFormattedTextField_pointLeft;
    private javax.swing.JFormattedTextField jFormattedTextField_pointRight;
    private javax.swing.JFormattedTextField jFormattedTextField_pointTop;
    private javax.swing.JFormattedTextField jFormattedTextField_right;
    private javax.swing.JFormattedTextField jFormattedTextField_time;
    private javax.swing.JFormattedTextField jFormattedTextField_top;
    private javax.swing.JFormattedTextField jFormattedTextField_variance;
    private javax.swing.JFormattedTextField jFormattedTextField_velocity;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_bottom;
    private javax.swing.JLabel jLabel_left;
    private javax.swing.JLabel jLabel_notice;
    private javax.swing.JLabel jLabel_right;
    private javax.swing.JLabel jLabel_spanLat;
    private javax.swing.JLabel jLabel_spanLon;
    private javax.swing.JLabel jLabel_top;
    private javax.swing.JRadioButton jRadioButton_randomLocation;
    private javax.swing.JRadioButton jRadioButton_sameLocation;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSpinner jSpinner_nodeNum;
    private javax.swing.JSpinner jSpinner_trajectoryNum;
    private javax.swing.JTextField jTextField_filePath;
    // End of variables declaration//GEN-END:variables
    public int DialogState = 0;
}
