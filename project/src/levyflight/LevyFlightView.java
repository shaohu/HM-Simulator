/*
 * LevyFlightView.java
 */
package levyflight;

import Static.StaticLib;
import TraGen.LG_PowerLaw;
import TraGen.UnitDescription;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import org.geotools.coverage.grid.io.GridFormatFactorySpi;
import org.geotools.map.MapContext;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import javax.swing.Timer;

import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JDialog;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import levyflight.Data.Field;
import levyflight.Data.Node;
import levyflight.Data.Roads;
import levyflight.Data.TimePrecisionEnum;
import levyflight.Data.Trajectory;
import levyflight.Data.TrajectoryPool;
import levyflight.Export.MovingDirectionExport;
import levyflight.Export.StatisticGraphGen;
import levyflight.Export.StepLengthExport;
import levyflight.Import.MethodLib;
import levyflight.Import.PowerLawTraGen;
import levyflight.Import.SHPRoadImporter;
import levyflight.Import.SHPTraImporter;
import levyflight.Statistic.EigenvectorCal;
import levyflight.Statistic.FrequencyCal;
import levyflight.socialNetwork.JDialogCheckinToTrajectory;
import org.geotools.coverage.grid.GridCoordinates2D;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.DataSourceException;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapLayer;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.swing.JCRSChooser;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.geotools.swing.tool.InfoTool;
import org.geotools.swing.tool.PanTool;
import org.geotools.swing.tool.ZoomInTool;
import org.geotools.swing.tool.ZoomOutTool;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.ContrastMethod;

/**
 * The application's main frame.
 */
public class LevyFlightView extends FrameView
{

    /**
     * 地图绘制控件 from geotools
     */
    final private JMapPane mapPane = new JMapPane();
    /**
     * 环境变量统一管理类
     */
    final private EnvironmentParameter ep = new EnvironmentParameter();

    public LevyFlightView(SingleFrameApplication app)
    {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                //statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++)
        {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                //statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        // statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {

            public void propertyChange(java.beans.PropertyChangeEvent evt)
            {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName))
                {
                    if (!busyIconTimer.isRunning())
                    {
                        // statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName))
                {
                    busyIconTimer.stop();
                    //statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName))
                {
                    String text = (String) (evt.getNewValue());
                    //statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName))
                {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        this.jSplitPane2.setTopComponent(mapPane);
        ControlLib.TextPane_DeclareInfo(jTextPane_Info, "Welcome!", Color.BLUE, Boolean.FALSE);
        this.mapPane.setRenderer(new StreamingRenderer());
        this.jMenu_ExtractRevisitedPointes.setEnabled(false);
        this.getFrame().setTitle("HM-Simulator");
        this.mapPane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter()
        {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent evt)
            {
                ControlLib.RefershLocationTip(mapPane, jLabel_locationTip, evt.getX(), evt.getY());
            }
        });
        this.mapPane.addMouseListener(new java.awt.event.MouseAdapter()
        {

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                jLabel_locationTip.setText("Location");
            }
        });
        this.mapPane.setCursorTool(
                new CursorTool()
                {

                    @Override
                    public void onMouseClicked(MapMouseEvent evt)
                    {
                        if (evt.isControlDown())//在按下Ctrl键时，选取地理对象并显示属性。
                        {
                            try
                            {
                                ep.selectedTrajectory_freCal = null;
                                ep.selectedTrajectory = ControlLib.MapPane_SelectSingleTrajectory(evt, mapPane, ep);
                                singleAlyItemChange();


                            } catch (IOException ex)
                            {
                                ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
                            }
                        }
                    }
                });


    }

    @Action
    public void showAboutBox()
    {
        if (aboutBox == null)
        {
            JFrame mainFrame = LevyFlightApp.getApplication().getMainFrame();
            aboutBox = new LevyFlightAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        LevyFlightApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane_Info = new javax.swing.JTextPane();
        jTabbedPane_statistic = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jRadioButton_alyItem_stepLength = new javax.swing.JRadioButton();
        jRadioButton_alyItem_ROG = new javax.swing.JRadioButton();
        jRadioButton_alyItem_direction = new javax.swing.JRadioButton();
        jRadioButton_alyItem_entropy = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        jSplitPane_groupAly = new javax.swing.JSplitPane();
        jPanel_groupGra = new StaticGraPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane_groupAly = new javax.swing.JTextPane();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane4 = new javax.swing.JSplitPane();
        jPanel_singleGra = new StaticGraPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPane_singleAly = new javax.swing.JTextPane();
        jToolBar1 = new javax.swing.JToolBar();
        jButton_cr_zoomIn = new javax.swing.JButton();
        jButton_cr_zoomOut = new javax.swing.JButton();
        jButton_cr_pan = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jButton_cr_world = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jButton_cr_info = new javax.swing.JButton();
        jButton_cr_normal = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenuItem_importPolygon = new javax.swing.JMenuItem();
        jMenuItem_importRoads = new javax.swing.JMenuItem();
        jMenu_importField = new javax.swing.JMenu();
        jMenuItem_import_F_ASCII = new javax.swing.JMenuItem();
        jMenuItem_Import_F_geotiff = new javax.swing.JMenuItem();
        jMenu_ImportTraj = new javax.swing.JMenu();
        jMenuItem_Imoprt_T_SHP = new javax.swing.JMenuItem();
        jMenuItem_Import_GPS = new javax.swing.JMenuItem();
        jMenuItem_Import_Custom = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        OptionMenu = new javax.swing.JMenu();
        jMenuItem_chooseCRS = new javax.swing.JMenuItem();
        jMenuItem_showCRS = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenu_SetStyle = new javax.swing.JMenu();
        jMenuItem_setRoadStyle = new javax.swing.JMenuItem();
        jMenuItem_setTrajectoryStyle = new javax.swing.JMenuItem();
        jMenuItem_setFieldStyle = new javax.swing.JMenuItem();
        jMenuItem_setPolygonStyle = new javax.swing.JMenuItem();
        jMenu_ExtractRevisitedPointes = new javax.swing.JMenu();
        jMenuItem_extractRevisitedPointesByAmount = new javax.swing.JMenuItem();
        jMenuItem_extractRevisitedPointesByDistance = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItem_staBinStrategySet = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem_removeRoads = new javax.swing.JMenuItem();
        jMenuItem_removeField = new javax.swing.JMenuItem();
        jMenuItem_removeTra = new javax.swing.JMenuItem();
        jMenuItem_removePolygon = new javax.swing.JMenuItem();
        jMenuItem_removeAll = new javax.swing.JMenuItem();
        TraGenMenu = new javax.swing.JMenu();
        jMenuItem_genTra_powerLaw = new javax.swing.JMenuItem();
        jMenuItem_genTra_exp = new javax.swing.JMenuItem();
        jMenuItem_genTra_gaussian = new javax.swing.JMenuItem();
        AnalysisMenu = new javax.swing.JMenu();
        jMenuItem_traRotationAly = new javax.swing.JMenuItem();
        jMenuItem_extractPointFromTra = new javax.swing.JMenuItem();
        ExportMenu = new javax.swing.JMenu();
        jMenuItem_exportStepLength = new javax.swing.JMenuItem();
        jMenuItem_exportMovingDirection = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        SocialNatworkMenu = new javax.swing.JMenu();
        jMenuItem_checkinsToTrajectory = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        jLabel_locationTip = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jDialog_StyleSet = new javax.swing.JDialog(this.getFrame(), true);
        jRadioButton_origin = new javax.swing.JRadioButton();
        jRadioButton_custom = new javax.swing.JRadioButton();
        jLabel_lineColor = new javax.swing.JLabel();
        SpinnerNumberModel mode=new SpinnerNumberModel();
        mode.setMaximum(5.0);
        mode.setMinimum(0.5);
        mode.setStepSize(0.1);
        mode.setValue(1.0);
        jSpinner_lineWidth = new JSpinner(mode);
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton_SetStyleOK = new javax.swing.JButton();
        jButton_SetStyleCancel = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel_fillColor = new javax.swing.JLabel();
        jCheckBox_setFillColorTransporent = new javax.swing.JCheckBox();
        buttonGroup_mode = new javax.swing.ButtonGroup();
        buttonGroup_alyItemsGroup = new javax.swing.ButtonGroup();
        jPopupMenu_graphStyleSet = new javax.swing.JPopupMenu();
        jMenuItem_graStyleSet = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jCheckBoxMenuItem_showFreGra = new javax.swing.JCheckBoxMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jCheckBoxMenuItem_showcumFreGra = new javax.swing.JCheckBoxMenuItem();
        jRadioButtonMenuItem_CDF = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem_CCDF = new javax.swing.JRadioButtonMenuItem();
        buttonGroup_CDFStyle = new javax.swing.ButtonGroup();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setDividerSize(3);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jSplitPane2.setDividerLocation(321);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setName("jSplitPane2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextPane_Info.setEditable(false);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(levyflight.LevyFlightApp.class).getContext().getResourceMap(LevyFlightView.class);
        jTextPane_Info.setFont(resourceMap.getFont("jTextPane_Info.font")); // NOI18N
        jTextPane_Info.setToolTipText(resourceMap.getString("jTextPane_Info.toolTipText")); // NOI18N
        jTextPane_Info.setName("jTextPane_Info"); // NOI18N
        jScrollPane1.setViewportView(jTextPane_Info);

        jSplitPane2.setBottomComponent(jScrollPane1);

        jSplitPane1.setLeftComponent(jSplitPane2);

        jTabbedPane_statistic.setMinimumSize(new java.awt.Dimension(200, 200));
        jTabbedPane_statistic.setName("jTabbedPane_statistic"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.BorderLayout());

        jSplitPane3.setDividerLocation(60);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setName("jSplitPane3"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel3.border.title"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        buttonGroup_alyItemsGroup.add(jRadioButton_alyItem_stepLength);
        jRadioButton_alyItem_stepLength.setForeground(resourceMap.getColor("jRadioButton_alyItem_stepLength.foreground")); // NOI18N
        jRadioButton_alyItem_stepLength.setText(resourceMap.getString("jRadioButton_alyItem_stepLength.text")); // NOI18N
        jRadioButton_alyItem_stepLength.setName("jRadioButton_alyItem_stepLength"); // NOI18N
        jRadioButton_alyItem_stepLength.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadioButton_alyItem_stepLengthMouseClicked(evt);
            }
        });
        jPanel3.add(jRadioButton_alyItem_stepLength);

        buttonGroup_alyItemsGroup.add(jRadioButton_alyItem_ROG);
        jRadioButton_alyItem_ROG.setForeground(resourceMap.getColor("jRadioButton_alyItem_ROG.foreground")); // NOI18N
        jRadioButton_alyItem_ROG.setText(resourceMap.getString("jRadioButton_alyItem_ROG.text")); // NOI18N
        jRadioButton_alyItem_ROG.setName("jRadioButton_alyItem_ROG"); // NOI18N
        jRadioButton_alyItem_ROG.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadioButton_alyItem_ROGMouseClicked(evt);
            }
        });
        jPanel3.add(jRadioButton_alyItem_ROG);

        buttonGroup_alyItemsGroup.add(jRadioButton_alyItem_direction);
        jRadioButton_alyItem_direction.setForeground(resourceMap.getColor("jRadioButton_alyItem_direction.foreground")); // NOI18N
        jRadioButton_alyItem_direction.setLabel(resourceMap.getString("jRadioButton_alyItem_direction.label")); // NOI18N
        jRadioButton_alyItem_direction.setName("jRadioButton_alyItem_direction"); // NOI18N
        jRadioButton_alyItem_direction.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadioButton_alyItem_directionMouseClicked(evt);
            }
        });
        jPanel3.add(jRadioButton_alyItem_direction);

        buttonGroup_alyItemsGroup.add(jRadioButton_alyItem_entropy);
        jRadioButton_alyItem_entropy.setForeground(resourceMap.getColor("jRadioButton_alyItem_entropy.foreground")); // NOI18N
        jRadioButton_alyItem_entropy.setText(resourceMap.getString("jRadioButton_alyItem_entropy.text")); // NOI18N
        jRadioButton_alyItem_entropy.setName("jRadioButton_alyItem_entropy"); // NOI18N
        jRadioButton_alyItem_entropy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadioButton_alyItem_entropyMouseClicked(evt);
            }
        });
        jPanel3.add(jRadioButton_alyItem_entropy);

        jSplitPane3.setTopComponent(jPanel3);

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.BorderLayout());

        jSplitPane_groupAly.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane_groupAly.setName("jSplitPane_groupAly"); // NOI18N

        jPanel_groupGra.setComponentPopupMenu(jPopupMenu_graphStyleSet);
        jPanel_groupGra.setName("jPanel_groupGra"); // NOI18N

        javax.swing.GroupLayout jPanel_groupGraLayout = new javax.swing.GroupLayout(jPanel_groupGra);
        jPanel_groupGra.setLayout(jPanel_groupGraLayout);
        jPanel_groupGraLayout.setHorizontalGroup(
            jPanel_groupGraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );
        jPanel_groupGraLayout.setVerticalGroup(
            jPanel_groupGraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jSplitPane_groupAly.setTopComponent(jPanel_groupGra);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTextPane_groupAly.setEditable(false);
        jTextPane_groupAly.setName("jTextPane_groupAly"); // NOI18N
        jScrollPane2.setViewportView(jTextPane_groupAly);

        jSplitPane_groupAly.setRightComponent(jScrollPane2);

        jPanel4.add(jSplitPane_groupAly, java.awt.BorderLayout.CENTER);

        jSplitPane3.setRightComponent(jPanel4);

        jPanel1.add(jSplitPane3, java.awt.BorderLayout.CENTER);

        jTabbedPane_statistic.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), resourceMap.getIcon("jPanel1.TabConstraints.tabIcon"), jPanel1); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.BorderLayout());

        jSplitPane4.setDividerLocation(300);
        jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane4.setName("jSplitPane4"); // NOI18N

        jPanel_singleGra.setComponentPopupMenu(jPopupMenu_graphStyleSet);
        jPanel_singleGra.setName("jPanel_singleGra"); // NOI18N

        javax.swing.GroupLayout jPanel_singleGraLayout = new javax.swing.GroupLayout(jPanel_singleGra);
        jPanel_singleGra.setLayout(jPanel_singleGraLayout);
        jPanel_singleGraLayout.setHorizontalGroup(
            jPanel_singleGraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 482, Short.MAX_VALUE)
        );
        jPanel_singleGraLayout.setVerticalGroup(
            jPanel_singleGraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 299, Short.MAX_VALUE)
        );

        jSplitPane4.setTopComponent(jPanel_singleGra);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jTextPane_singleAly.setName("jTextPane_singleAly"); // NOI18N
        jScrollPane3.setViewportView(jTextPane_singleAly);

        jSplitPane4.setRightComponent(jScrollPane3);

        jPanel2.add(jSplitPane4, java.awt.BorderLayout.CENTER);

        jTabbedPane_statistic.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), resourceMap.getIcon("jPanel2.TabConstraints.tabIcon"), jPanel2); // NOI18N

        jSplitPane1.setRightComponent(jTabbedPane_statistic);

        mainPanel.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        jButton_cr_zoomIn.setIcon(resourceMap.getIcon("jButton_cr_zoomIn.icon")); // NOI18N
        jButton_cr_zoomIn.setText(resourceMap.getString("jButton_cr_zoomIn.text")); // NOI18N
        jButton_cr_zoomIn.setToolTipText(resourceMap.getString("jButton_cr_zoomIn.toolTipText")); // NOI18N
        jButton_cr_zoomIn.setFocusable(false);
        jButton_cr_zoomIn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_cr_zoomIn.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton_cr_zoomIn.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton_cr_zoomIn.setName("jButton_cr_zoomIn"); // NOI18N
        jButton_cr_zoomIn.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton_cr_zoomIn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton_cr_zoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cr_zoomInActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton_cr_zoomIn);

        jButton_cr_zoomOut.setIcon(resourceMap.getIcon("jButton_cr_zoomOut.icon")); // NOI18N
        jButton_cr_zoomOut.setToolTipText(resourceMap.getString("jButton_cr_zoomOut.toolTipText")); // NOI18N
        jButton_cr_zoomOut.setFocusable(false);
        jButton_cr_zoomOut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_cr_zoomOut.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton_cr_zoomOut.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton_cr_zoomOut.setName("jButton_cr_zoomOut"); // NOI18N
        jButton_cr_zoomOut.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton_cr_zoomOut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton_cr_zoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cr_zoomOutActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton_cr_zoomOut);

        jButton_cr_pan.setIcon(resourceMap.getIcon("jButton_cr_pan.icon")); // NOI18N
        jButton_cr_pan.setToolTipText(resourceMap.getString("jButton_cr_pan.toolTipText")); // NOI18N
        jButton_cr_pan.setFocusable(false);
        jButton_cr_pan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_cr_pan.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton_cr_pan.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton_cr_pan.setName("jButton_cr_pan"); // NOI18N
        jButton_cr_pan.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton_cr_pan.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton_cr_pan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cr_panActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton_cr_pan);

        jSeparator5.setName("jSeparator5"); // NOI18N
        jToolBar1.add(jSeparator5);

        jButton_cr_world.setIcon(resourceMap.getIcon("jButton_cr_world.icon")); // NOI18N
        jButton_cr_world.setToolTipText(resourceMap.getString("jButton_cr_world.toolTipText")); // NOI18N
        jButton_cr_world.setFocusable(false);
        jButton_cr_world.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_cr_world.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton_cr_world.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton_cr_world.setName("jButton_cr_world"); // NOI18N
        jButton_cr_world.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton_cr_world.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton_cr_world.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cr_worldActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton_cr_world);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jToolBar1.add(jSeparator6);

        jButton_cr_info.setIcon(resourceMap.getIcon("jButton_cr_info.icon")); // NOI18N
        jButton_cr_info.setToolTipText(resourceMap.getString("jButton_cr_info.toolTipText")); // NOI18N
        jButton_cr_info.setFocusable(false);
        jButton_cr_info.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_cr_info.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton_cr_info.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton_cr_info.setName("jButton_cr_info"); // NOI18N
        jButton_cr_info.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton_cr_info.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton_cr_info.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cr_infoActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton_cr_info);

        jButton_cr_normal.setIcon(resourceMap.getIcon("jButton_cr_normal.icon")); // NOI18N
        jButton_cr_normal.setToolTipText(resourceMap.getString("jButton_cr_normal.toolTipText")); // NOI18N
        jButton_cr_normal.setFocusable(false);
        jButton_cr_normal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_cr_normal.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton_cr_normal.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton_cr_normal.setName("jButton_cr_normal"); // NOI18N
        jButton_cr_normal.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton_cr_normal.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton_cr_normal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cr_normalActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton_cr_normal);

        mainPanel.add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        menuBar.setFont(resourceMap.getFont("menuBar.font")); // NOI18N
        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setFont(resourceMap.getFont("AnalysisMenu.font")); // NOI18N

        jMenuItem_importPolygon.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem_importPolygon.setText(resourceMap.getString("jMenuItem_importPolygon.text")); // NOI18N
        jMenuItem_importPolygon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_importPolygonActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem_importPolygon);

        jMenuItem_importRoads.setText(resourceMap.getString("jMenuItem_importRoads.text")); // NOI18N
        jMenuItem_importRoads.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_importRoadsActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem_importRoads);

        jMenu_importField.setText(resourceMap.getString("jMenu_importField.text")); // NOI18N
        jMenu_importField.setName("jMenu_importField"); // NOI18N

        jMenuItem_import_F_ASCII.setText(resourceMap.getString("jMenuItem_import_F_ASCII.text")); // NOI18N
        jMenuItem_import_F_ASCII.setToolTipText(resourceMap.getString("jMenuItem_import_F_ASCII.toolTipText")); // NOI18N
        jMenuItem_import_F_ASCII.setName("jMenuItem_import_F_ASCII"); // NOI18N
        jMenuItem_import_F_ASCII.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_import_F_ASCIIActionPerformed(evt);
            }
        });
        jMenu_importField.add(jMenuItem_import_F_ASCII);

        jMenuItem_Import_F_geotiff.setText(resourceMap.getString("jMenuItem_Import_F_geotiff.text")); // NOI18N
        jMenuItem_Import_F_geotiff.setToolTipText(resourceMap.getString("jMenuItem_Import_F_geotiff.toolTipText")); // NOI18N
        jMenuItem_Import_F_geotiff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Import_F_geotiffActionPerformed(evt);
            }
        });
        jMenu_importField.add(jMenuItem_Import_F_geotiff);

        fileMenu.add(jMenu_importField);

        jMenu_ImportTraj.setText(resourceMap.getString("jMenu_ImportTraj.text")); // NOI18N

        jMenuItem_Imoprt_T_SHP.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem_Imoprt_T_SHP.setText(resourceMap.getString("jMenuItem_Imoprt_T_SHP.text")); // NOI18N
        jMenuItem_Imoprt_T_SHP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Imoprt_T_SHPActionPerformed(evt);
            }
        });
        jMenu_ImportTraj.add(jMenuItem_Imoprt_T_SHP);

        jMenuItem_Import_GPS.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem_Import_GPS.setText(resourceMap.getString("jMenuItem_Import_GPS.text")); // NOI18N
        jMenuItem_Import_GPS.setName("jMenuItem_Import_GPS"); // NOI18N
        jMenu_ImportTraj.add(jMenuItem_Import_GPS);

        jMenuItem_Import_Custom.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem_Import_Custom.setText(resourceMap.getString("jMenuItem_Import_Custom.text")); // NOI18N
        jMenuItem_Import_Custom.setName("jMenuItem_Import_Custom"); // NOI18N
        jMenu_ImportTraj.add(jMenuItem_Import_Custom);

        fileMenu.add(jMenu_ImportTraj);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(levyflight.LevyFlightApp.class).getContext().getActionMap(LevyFlightView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        OptionMenu.setText(resourceMap.getString("OptionMenu.text")); // NOI18N
        OptionMenu.setFont(resourceMap.getFont("AnalysisMenu.font")); // NOI18N
        OptionMenu.setName("OptionMenu"); // NOI18N

        jMenuItem_chooseCRS.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem_chooseCRS.setText(resourceMap.getString("jMenuItem_chooseCRS.text")); // NOI18N
        jMenuItem_chooseCRS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_chooseCRSActionPerformed(evt);
            }
        });
        OptionMenu.add(jMenuItem_chooseCRS);

        jMenuItem_showCRS.setText(resourceMap.getString("jMenuItem_showCRS.text")); // NOI18N
        jMenuItem_showCRS.setName("jMenuItem_showCRS"); // NOI18N
        jMenuItem_showCRS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_showCRSActionPerformed(evt);
            }
        });
        OptionMenu.add(jMenuItem_showCRS);

        jSeparator2.setName("jSeparator2"); // NOI18N
        OptionMenu.add(jSeparator2);

        jMenu_SetStyle.setText(resourceMap.getString("jMenu_SetStyle.text")); // NOI18N

        jMenuItem_setRoadStyle.setText(resourceMap.getString("jMenuItem_setRoadStyle.text")); // NOI18N
        jMenuItem_setRoadStyle.setEnabled(false);
        jMenuItem_setRoadStyle.setName("jMenuItem_setRoadStyle"); // NOI18N
        jMenuItem_setRoadStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_setRoadStyleActionPerformed(evt);
            }
        });
        jMenu_SetStyle.add(jMenuItem_setRoadStyle);

        jMenuItem_setTrajectoryStyle.setText(resourceMap.getString("jMenuItem_setTrajectoryStyle.text")); // NOI18N
        jMenuItem_setTrajectoryStyle.setEnabled(false);
        jMenuItem_setTrajectoryStyle.setName("jMenuItem_setTrajectoryStyle"); // NOI18N
        jMenuItem_setTrajectoryStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_setTrajectoryStyleActionPerformed(evt);
            }
        });
        jMenu_SetStyle.add(jMenuItem_setTrajectoryStyle);

        jMenuItem_setFieldStyle.setText(resourceMap.getString("jMenuItem_setFieldStyle.text")); // NOI18N
        jMenuItem_setFieldStyle.setEnabled(false);
        jMenuItem_setFieldStyle.setName("jMenuItem_setFieldStyle"); // NOI18N
        jMenuItem_setFieldStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_setFieldStyleActionPerformed(evt);
            }
        });
        jMenu_SetStyle.add(jMenuItem_setFieldStyle);

        jMenuItem_setPolygonStyle.setText(resourceMap.getString("jMenuItem_setPolygonStyle.text")); // NOI18N
        jMenuItem_setPolygonStyle.setEnabled(false);
        jMenuItem_setPolygonStyle.setName("jMenuItem_setPolygonStyle"); // NOI18N
        jMenuItem_setPolygonStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_setPolygonStyleActionPerformed(evt);
            }
        });
        jMenu_SetStyle.add(jMenuItem_setPolygonStyle);

        OptionMenu.add(jMenu_SetStyle);

        jMenu_ExtractRevisitedPointes.setText(resourceMap.getString("jMenu_ExtractRevisitedPointes.text")); // NOI18N
        jMenu_ExtractRevisitedPointes.setName("jMenu_ExtractRevisitedPointes"); // NOI18N

        jMenuItem_extractRevisitedPointesByAmount.setText(resourceMap.getString("jMenuItem_extractRevisitedPointesByAmount.text")); // NOI18N
        jMenuItem_extractRevisitedPointesByAmount.setName("jMenuItem_extractRevisitedPointesByAmount"); // NOI18N
        jMenuItem_extractRevisitedPointesByAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_extractRevisitedPointesByAmountActionPerformed(evt);
            }
        });
        jMenu_ExtractRevisitedPointes.add(jMenuItem_extractRevisitedPointesByAmount);

        jMenuItem_extractRevisitedPointesByDistance.setText(resourceMap.getString("jMenuItem_extractRevisitedPointesByDistance.text")); // NOI18N
        jMenuItem_extractRevisitedPointesByDistance.setName("jMenuItem_extractRevisitedPointesByDistance"); // NOI18N
        jMenuItem_extractRevisitedPointesByDistance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_extractRevisitedPointesByDistanceActionPerformed(evt);
            }
        });
        jMenu_ExtractRevisitedPointes.add(jMenuItem_extractRevisitedPointesByDistance);

        OptionMenu.add(jMenu_ExtractRevisitedPointes);

        jSeparator9.setName("jSeparator9"); // NOI18N
        OptionMenu.add(jSeparator9);

        jMenuItem_staBinStrategySet.setText(resourceMap.getString("jMenuItem_staBinStrategySet.text")); // NOI18N
        jMenuItem_staBinStrategySet.setName("jMenuItem_staBinStrategySet"); // NOI18N
        jMenuItem_staBinStrategySet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_staBinStrategySetActionPerformed(evt);
            }
        });
        OptionMenu.add(jMenuItem_staBinStrategySet);

        jSeparator3.setName("jSeparator3"); // NOI18N
        OptionMenu.add(jSeparator3);

        jMenuItem_removeRoads.setText(resourceMap.getString("jMenuItem_removeRoads.text")); // NOI18N
        jMenuItem_removeRoads.setEnabled(false);
        jMenuItem_removeRoads.setName("jMenuItem_removeRoads"); // NOI18N
        jMenuItem_removeRoads.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_removeRoadsActionPerformed(evt);
            }
        });
        OptionMenu.add(jMenuItem_removeRoads);

        jMenuItem_removeField.setText(resourceMap.getString("jMenuItem_removeField.text")); // NOI18N
        jMenuItem_removeField.setEnabled(false);
        jMenuItem_removeField.setName("jMenuItem_removeField"); // NOI18N
        jMenuItem_removeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_removeFieldActionPerformed(evt);
            }
        });
        OptionMenu.add(jMenuItem_removeField);

        jMenuItem_removeTra.setText(resourceMap.getString("jMenuItem_removeTra.text")); // NOI18N
        jMenuItem_removeTra.setEnabled(false);
        jMenuItem_removeTra.setName("jMenuItem_removeTra"); // NOI18N
        jMenuItem_removeTra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_removeTraActionPerformed(evt);
            }
        });
        OptionMenu.add(jMenuItem_removeTra);

        jMenuItem_removePolygon.setText(resourceMap.getString("jMenuItem_removePolygon.text")); // NOI18N
        jMenuItem_removePolygon.setEnabled(false);
        jMenuItem_removePolygon.setName("jMenuItem_removePolygon"); // NOI18N
        jMenuItem_removePolygon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_removePolygonActionPerformed(evt);
            }
        });
        OptionMenu.add(jMenuItem_removePolygon);

        jMenuItem_removeAll.setText(resourceMap.getString("jMenuItem_removeAll.text")); // NOI18N
        jMenuItem_removeAll.setName("jMenuItem_removeAll"); // NOI18N
        jMenuItem_removeAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_removeAllActionPerformed(evt);
            }
        });
        OptionMenu.add(jMenuItem_removeAll);

        menuBar.add(OptionMenu);

        TraGenMenu.setText(resourceMap.getString("TraGenMenu.text")); // NOI18N
        TraGenMenu.setFont(resourceMap.getFont("AnalysisMenu.font")); // NOI18N
        TraGenMenu.setName("TraGenMenu"); // NOI18N

        jMenuItem_genTra_powerLaw.setText(resourceMap.getString("jMenuItem_genTra_powerLaw.text")); // NOI18N
        jMenuItem_genTra_powerLaw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_genTra_powerLawActionPerformed(evt);
            }
        });
        TraGenMenu.add(jMenuItem_genTra_powerLaw);

        jMenuItem_genTra_exp.setText(resourceMap.getString("jMenuItem_genTra_exp.text")); // NOI18N
        jMenuItem_genTra_exp.setName("jMenuItem_genTra_exp"); // NOI18N
        jMenuItem_genTra_exp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_genTra_expActionPerformed(evt);
            }
        });
        TraGenMenu.add(jMenuItem_genTra_exp);

        jMenuItem_genTra_gaussian.setText(resourceMap.getString("jMenuItem_genTra_gaussian.text")); // NOI18N
        jMenuItem_genTra_gaussian.setName("jMenuItem_genTra_gaussian"); // NOI18N
        jMenuItem_genTra_gaussian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_genTra_gaussianActionPerformed(evt);
            }
        });
        TraGenMenu.add(jMenuItem_genTra_gaussian);

        menuBar.add(TraGenMenu);

        AnalysisMenu.setText(resourceMap.getString("AnalysisMenu.text")); // NOI18N
        AnalysisMenu.setFont(resourceMap.getFont("AnalysisMenu.font")); // NOI18N
        AnalysisMenu.setName("AnalysisMenu"); // NOI18N

        jMenuItem_traRotationAly.setText(resourceMap.getString("jMenuItem_traRotationAly.text")); // NOI18N
        jMenuItem_traRotationAly.setEnabled(false);
        jMenuItem_traRotationAly.setName("jMenuItem_traRotationAly"); // NOI18N
        jMenuItem_traRotationAly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_traRotationAlyActionPerformed(evt);
            }
        });
        AnalysisMenu.add(jMenuItem_traRotationAly);

        jMenuItem_extractPointFromTra.setText(resourceMap.getString("jMenuItem_extractPointFromTra.text")); // NOI18N
        jMenuItem_extractPointFromTra.setName("jMenuItem_extractPointFromTra"); // NOI18N
        jMenuItem_extractPointFromTra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_extractPointFromTraActionPerformed(evt);
            }
        });
        AnalysisMenu.add(jMenuItem_extractPointFromTra);

        menuBar.add(AnalysisMenu);

        ExportMenu.setText(resourceMap.getString("ExportMenu.text")); // NOI18N
        ExportMenu.setFont(resourceMap.getFont("AnalysisMenu.font")); // NOI18N
        ExportMenu.setName("ExportMenu"); // NOI18N

        jMenuItem_exportStepLength.setText(resourceMap.getString("jMenuItem_exportStepLength.text")); // NOI18N
        jMenuItem_exportStepLength.setEnabled(false);
        jMenuItem_exportStepLength.setName("jMenuItem_exportStepLength"); // NOI18N
        jMenuItem_exportStepLength.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_exportStepLengthActionPerformed(evt);
            }
        });
        ExportMenu.add(jMenuItem_exportStepLength);

        jMenuItem_exportMovingDirection.setText(resourceMap.getString("jMenuItem_exportMovingDirection.text")); // NOI18N
        jMenuItem_exportMovingDirection.setEnabled(false);
        jMenuItem_exportMovingDirection.setName("jMenuItem_exportMovingDirection"); // NOI18N
        jMenuItem_exportMovingDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_exportMovingDirectionActionPerformed(evt);
            }
        });
        ExportMenu.add(jMenuItem_exportMovingDirection);

        menuBar.add(ExportMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setFont(resourceMap.getFont("AnalysisMenu.font")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        SocialNatworkMenu.setText(resourceMap.getString("SocialNatworkMenu.text")); // NOI18N
        SocialNatworkMenu.setName("SocialNatworkMenu"); // NOI18N

        jMenuItem_checkinsToTrajectory.setText(resourceMap.getString("jMenuItem_checkinsToTrajectory.text")); // NOI18N
        jMenuItem_checkinsToTrajectory.setName("jMenuItem_checkinsToTrajectory"); // NOI18N
        jMenuItem_checkinsToTrajectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_checkinsToTrajectoryActionPerformed(evt);
            }
        });
        SocialNatworkMenu.add(jMenuItem_checkinsToTrajectory);

        menuBar.add(SocialNatworkMenu);

        statusPanel.setMinimumSize(new java.awt.Dimension(608, 15));
        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel_locationTip.setText(resourceMap.getString("jLabel_locationTip.text")); // NOI18N
        jLabel_locationTip.setName("jLabel_locationTip"); // NOI18N
        statusPanel.add(jLabel_locationTip, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 420, -1));

        progressBar.setName("progressBar"); // NOI18N
        statusPanel.add(progressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 0, 152, -1));

        jDialog_StyleSet.setFont(resourceMap.getFont("jDialog_StyleSet.font")); // NOI18N
        jDialog_StyleSet.setLocationByPlatform(true);
        jDialog_StyleSet.setMinimumSize(new java.awt.Dimension(379, 210));
        jDialog_StyleSet.setName("jDialog_StyleSet"); // NOI18N
        jDialog_StyleSet.setResizable(false);
        jDialog_StyleSet.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonGroup_mode.add(jRadioButton_origin);
        jRadioButton_origin.setSelected(true);
        jRadioButton_origin.setText(resourceMap.getString("jRadioButton_origin.text")); // NOI18N
        jRadioButton_origin.setActionCommand(resourceMap.getString("jRadioButton_origin.actionCommand")); // NOI18N
        jRadioButton_origin.setName("jRadioButton_origin"); // NOI18N
        jDialog_StyleSet.getContentPane().add(jRadioButton_origin, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, -1, -1));

        buttonGroup_mode.add(jRadioButton_custom);
        jRadioButton_custom.setText(resourceMap.getString("jRadioButton_custom.text")); // NOI18N
        jRadioButton_custom.setName("jRadioButton_custom"); // NOI18N
        jDialog_StyleSet.getContentPane().add(jRadioButton_custom, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 39, -1, -1));

        jLabel_lineColor.setBackground(resourceMap.getColor("jLabel_lineColor.background")); // NOI18N
        jLabel_lineColor.setText(resourceMap.getString("jLabel_lineColor.text")); // NOI18N
        jLabel_lineColor.setToolTipText(resourceMap.getString("jLabel_lineColor.toolTipText")); // NOI18N
        jLabel_lineColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel_lineColor.setName("jLabel_lineColor"); // NOI18N
        jLabel_lineColor.setOpaque(true);
        jLabel_lineColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_lineColorMouseClicked(evt);
            }
        });
        jDialog_StyleSet.getContentPane().add(jLabel_lineColor, new org.netbeans.lib.awtextra.AbsoluteConstraints(99, 68, 23, 22));

        jSpinner_lineWidth.setToolTipText(resourceMap.getString("jSpinner_lineWidth.toolTipText")); // NOI18N
        jSpinner_lineWidth.setName("jSpinner_lineWidth"); // NOI18N
        jDialog_StyleSet.getContentPane().add(jSpinner_lineWidth, new org.netbeans.lib.awtextra.AbsoluteConstraints(231, 68, 54, -1));

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jDialog_StyleSet.getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(155, 71, -1, -1));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        jDialog_StyleSet.getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 71, -1, -1));

        jButton_SetStyleOK.setText(resourceMap.getString("jButton_SetStyleOK.text")); // NOI18N
        jButton_SetStyleOK.setName("jButton_SetStyleOK"); // NOI18N
        jButton_SetStyleOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_SetStyleOKActionPerformed(evt);
            }
        });
        jDialog_StyleSet.getContentPane().add(jButton_SetStyleOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 150, 67, -1));

        jButton_SetStyleCancel.setText(resourceMap.getString("jButton_SetStyleCancel.text")); // NOI18N
        jButton_SetStyleCancel.setName("jButton_SetStyleCancel"); // NOI18N
        jButton_SetStyleCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_SetStyleCancelActionPerformed(evt);
            }
        });
        jDialog_StyleSet.getContentPane().add(jButton_SetStyleCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 150, -1, -1));

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jDialog_StyleSet.getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 108, -1, -1));

        jLabel_fillColor.setBackground(resourceMap.getColor("jLabel_fillColor.background")); // NOI18N
        jLabel_fillColor.setToolTipText(resourceMap.getString("jLabel_fillColor.toolTipText")); // NOI18N
        jLabel_fillColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel_fillColor.setName("jLabel_fillColor"); // NOI18N
        jLabel_fillColor.setOpaque(true);
        jLabel_fillColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_fillColorMouseClicked(evt);
            }
        });
        jDialog_StyleSet.getContentPane().add(jLabel_fillColor, new org.netbeans.lib.awtextra.AbsoluteConstraints(99, 106, 23, 22));

        jCheckBox_setFillColorTransporent.setSelected(true);
        jCheckBox_setFillColorTransporent.setText(resourceMap.getString("jCheckBox_setFillColorTransporent.text")); // NOI18N
        jCheckBox_setFillColorTransporent.setName("jCheckBox_setFillColorTransporent"); // NOI18N
        jDialog_StyleSet.getContentPane().add(jCheckBox_setFillColorTransporent, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 106, 200, -1));

        jPopupMenu_graphStyleSet.setName("jPopupMenu_graphStyleSet"); // NOI18N

        jMenuItem_graStyleSet.setText(resourceMap.getString("jMenuItem_graStyleSet.text")); // NOI18N
        jMenuItem_graStyleSet.setName("jMenuItem_graStyleSet"); // NOI18N
        jMenuItem_graStyleSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_graStyleSetActionPerformed(evt);
            }
        });
        jPopupMenu_graphStyleSet.add(jMenuItem_graStyleSet);

        jSeparator7.setName("jSeparator7"); // NOI18N
        jPopupMenu_graphStyleSet.add(jSeparator7);

        jCheckBoxMenuItem_showFreGra.setSelected(true);
        jCheckBoxMenuItem_showFreGra.setText(resourceMap.getString("jCheckBoxMenuItem_showFreGra.text")); // NOI18N
        jCheckBoxMenuItem_showFreGra.setName("jCheckBoxMenuItem_showFreGra"); // NOI18N
        jCheckBoxMenuItem_showFreGra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem_showFreGraActionPerformed(evt);
            }
        });
        jPopupMenu_graphStyleSet.add(jCheckBoxMenuItem_showFreGra);

        jSeparator8.setName("jSeparator8"); // NOI18N
        jPopupMenu_graphStyleSet.add(jSeparator8);

        jCheckBoxMenuItem_showcumFreGra.setSelected(true);
        jCheckBoxMenuItem_showcumFreGra.setText(resourceMap.getString("jCheckBoxMenuItem_showcumFreGra.text")); // NOI18N
        jCheckBoxMenuItem_showcumFreGra.setName("jCheckBoxMenuItem_showcumFreGra"); // NOI18N
        jCheckBoxMenuItem_showcumFreGra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem_showcumFreGraActionPerformed(evt);
            }
        });
        jPopupMenu_graphStyleSet.add(jCheckBoxMenuItem_showcumFreGra);

        buttonGroup_CDFStyle.add(jRadioButtonMenuItem_CDF);
        jRadioButtonMenuItem_CDF.setSelected(true);
        jRadioButtonMenuItem_CDF.setText(resourceMap.getString("jRadioButtonMenuItem_CDF.text")); // NOI18N
        jRadioButtonMenuItem_CDF.setName("jRadioButtonMenuItem_CDF"); // NOI18N
        jRadioButtonMenuItem_CDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem_CDFActionPerformed(evt);
            }
        });
        jPopupMenu_graphStyleSet.add(jRadioButtonMenuItem_CDF);

        buttonGroup_CDFStyle.add(jRadioButtonMenuItem_CCDF);
        jRadioButtonMenuItem_CCDF.setText(resourceMap.getString("jRadioButtonMenuItem_CCDF.text")); // NOI18N
        jRadioButtonMenuItem_CCDF.setName("jRadioButtonMenuItem_CCDF"); // NOI18N
        jRadioButtonMenuItem_CCDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem_CCDFActionPerformed(evt);
            }
        });
        jPopupMenu_graphStyleSet.add(jRadioButtonMenuItem_CCDF);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    //加入道路数据
    private void jMenuItem_importRoadsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_importRoadsActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_importRoadsActionPerformed
        try
        {
            String message = ControlLib.MapPane_addSHPRoadsAction(mapPane, ep);
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, message, StaticLib.fontGreenColor, Boolean.FALSE);
        } catch (Exception ex)
        {
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, "error: " + ex.getMessage(), Color.RED, Boolean.FALSE);
        }
        ControlLib.AdjustMapPane(jSplitPane2);
        this.CheckButtons();
    }//GEN-LAST:event_jMenuItem_importRoadsActionPerformed

    //显示当前的CRS
    private void jMenuItem_showCRSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_showCRSActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_showCRSActionPerformed
        String info = "Current coordinate reference system:\n";
        if (ep.generalCRS == null)
        {
            info += "null";
        } else
        {
            info += ep.generalCRS.toWKT();
        }
        ControlLib.TextPane_DeclareInfo(jTextPane_Info, info, Color.BLUE, Boolean.FALSE);
    }//GEN-LAST:event_jMenuItem_showCRSActionPerformed

    //加入shp轨迹数据
    //加入geotiff栅格数据
    private void jMenuItem_Import_F_geotiffActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_Import_F_geotiffActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_Import_F_geotiffActionPerformed
        try
        {
            String message = ControlLib.ImportFieldGeotiff(mapPane, ep, jTextPane_Info);
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, message, StaticLib.fontGreenColor, Boolean.FALSE);
        } catch (Exception ex)
        {
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, "error: " + ex.getMessage(), Color.RED, Boolean.FALSE);
        }
        ControlLib.AdjustMapPane(jSplitPane2);
        this.CheckButtons();
    }//GEN-LAST:event_jMenuItem_Import_F_geotiffActionPerformed

    //删除全部图层
    private void jMenuItem_removeAllActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_removeAllActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_removeAllActionPerformed
        ControlLib.RemoveAllLayers(mapPane, ep);
        ControlLib.TextPane_DeclareInfo(jTextPane_Info, "All layers removed", Color.BLACK, Boolean.FALSE);
        this.jMenuItem_removeField.setEnabled(false);
        this.jMenuItem_removeRoads.setEnabled(false);
        this.jMenuItem_removeTra.setEnabled(false);
        this.jMenuItem_setRoadStyle.setEnabled(false);
        this.jMenuItem_removeField.setEnabled(false);
        this.ep.trajectoryPool_freCal_ROG = null;
        this.ep.trajectoryPool_freCal_entropy = null;
        this.ep.trajectoryPool_freCal_stepLength = null;
        this.ep.polygonImporter=null;
        this.jPanel_groupGra.repaint();
    }//GEN-LAST:event_jMenuItem_removeAllActionPerformed

    //删除道路图层
    private void jMenuItem_removeRoadsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_removeRoadsActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_removeRoadsActionPerformed
        try
        {
            int num = ControlLib.RemoveLayer(mapPane, Roads.className, ep);
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, "Roads layer removed, " + String.valueOf(num) + " Layer(s) left.", Color.BLACK, Boolean.FALSE);
        } catch (Exception ex)
        {
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getLocalizedMessage(), Color.RED, Boolean.FALSE);
        }
        ControlLib.AdjustMapPane(jSplitPane2);
        this.CheckButtons();
    }//GEN-LAST:event_jMenuItem_removeRoadsActionPerformed

    //删除轨迹图层
    private void jMenuItem_removeTraActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_removeTraActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_removeTraActionPerformed
        try
        {
            int num = ControlLib.RemoveLayer(mapPane, TrajectoryPool.className, ep);
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, "Trajectories layer removed, " + String.valueOf(num) + " Layer(s) left.", Color.BLACK, Boolean.FALSE);
            this.ep.trajectoryPool_freCal_ROG = null;
            this.ep.trajectoryPool_freCal_entropy = null;
            this.ep.trajectoryPool_freCal_stepLength = null;
            this.jPanel_groupGra.repaint();
        } catch (Exception ex)
        {
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getLocalizedMessage(), Color.RED, Boolean.FALSE);
        }
        ControlLib.AdjustMapPane(jSplitPane2);
        this.CheckButtons();
    }//GEN-LAST:event_jMenuItem_removeTraActionPerformed

    //删除栅格图层
    private void jMenuItem_removeFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_removeFieldActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_removeFieldActionPerformed
        try
        {
            int num = ControlLib.RemoveLayer(mapPane, Field.className, ep);
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, "Field layer removed, " + String.valueOf(num) + " Layer(s) left.", Color.BLACK, Boolean.FALSE);
        } catch (Exception ex)
        {
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getLocalizedMessage(), Color.RED, Boolean.FALSE);
        }
        ControlLib.AdjustMapPane(jSplitPane2);
        this.CheckButtons();
    }//GEN-LAST:event_jMenuItem_removeFieldActionPerformed

    //弹出颜色显示框
    private void jLabel_lineColorMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jLabel_lineColorMouseClicked
    {//GEN-HEADEREND:event_jLabel_lineColorMouseClicked
        Color color = JColorChooser.showDialog(this.getFrame(), "Choose the line color", this.jLabel_lineColor.getBackground());
        this.jLabel_lineColor.setBackground(color);
    }//GEN-LAST:event_jLabel_lineColorMouseClicked

    //确认显示风格设定
    private void jButton_SetStyleOKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_SetStyleOKActionPerformed
    {//GEN-HEADEREND:event_jButton_SetStyleOKActionPerformed
        this.jDialog_StyleSet_OptionIndex = JOptionPane.OK_OPTION;
        this.jDialog_StyleSet.setVisible(false);
    }//GEN-LAST:event_jButton_SetStyleOKActionPerformed

    //取消显示风格设定
    private void jButton_SetStyleCancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_SetStyleCancelActionPerformed
    {//GEN-HEADEREND:event_jButton_SetStyleCancelActionPerformed
        this.jDialog_StyleSet_OptionIndex = JOptionPane.CANCEL_OPTION;
        this.jDialog_StyleSet.setVisible(false);
    }//GEN-LAST:event_jButton_SetStyleCancelActionPerformed

    //设定道路显示风格
    private void jMenuItem_setRoadStyleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_setRoadStyleActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_setRoadStyleActionPerformed
        this.jDialog_StyleSet.setVisible(true);
        if (this.jDialog_StyleSet_OptionIndex == JOptionPane.OK_OPTION)
        {
            try
            {
                String message = ControlLib.ResetPolylineLayerStyle(mapPane, Roads.className, ep.road.AbsoluteFilePath, this.jRadioButton_origin.isSelected(), this.jLabel_lineColor.getBackground(), Double.parseDouble(this.jSpinner_lineWidth.getValue().toString()));
                ControlLib.TextPane_DeclareInfo(jTextPane_Info, message, StaticLib.fontGreenColor, Boolean.FALSE);
                this.ep.road_lineColor = this.jLabel_lineColor.getBackground();
                this.ep.road_lineWidth = Double.parseDouble(this.jSpinner_lineWidth.getValue().toString());
            } catch (Exception ex)
            {
                ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
            }
        }
    }//GEN-LAST:event_jMenuItem_setRoadStyleActionPerformed

    //设定路径显示风格
    private void jMenuItem_setTrajectoryStyleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_setTrajectoryStyleActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_setTrajectoryStyleActionPerformed
        this.jDialog_StyleSet.setVisible(true);
        if (this.jDialog_StyleSet_OptionIndex == JOptionPane.OK_OPTION)
        {
            try
            {
                String message = ControlLib.ResetPolylineLayerStyle(mapPane, TrajectoryPool.className, ep.trajectoryPool.AbsoluteFilePath, this.jRadioButton_origin.isSelected(), this.jLabel_lineColor.getBackground(), Double.parseDouble(this.jSpinner_lineWidth.getValue().toString()));
                ControlLib.TextPane_DeclareInfo(jTextPane_Info, message, StaticLib.fontGreenColor, Boolean.FALSE);
                this.ep.trajectoryPool_lineColor = this.jLabel_lineColor.getBackground();
                this.ep.trajectoryPool_lineWidth = Double.parseDouble(this.jSpinner_lineWidth.getValue().toString());
            } catch (Exception ex)
            {
                ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
            }
        }
    }//GEN-LAST:event_jMenuItem_setTrajectoryStyleActionPerformed

    //选择当前的CRS
    private void jMenuItem_chooseCRSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_chooseCRSActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_chooseCRSActionPerformed
        if (this.ep.generalCRS != null)
        {
            JOptionPane.showMessageDialog(null, "Current coordinate reference system already exists!", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        this.ep.generalCRS = JCRSChooser.showDialog(this.getFrame(), "choose the current coordinate reference system", null, "4586: New Beijing / Gauss-Kruger CM 117E");
    }//GEN-LAST:event_jMenuItem_chooseCRSActionPerformed

    //生成powerlaw的路径
    //读入ACSII码地栅格文件
    private void jMenuItem_import_F_ASCIIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_import_F_ASCIIActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_import_F_ASCIIActionPerformed
        try
        {
            String message = ControlLib.ImportFieldASCII(mapPane, ep, jTextPane_Info);
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, message, StaticLib.fontGreenColor, Boolean.FALSE);
        } catch (Exception ex)
        {
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
        }
        ControlLib.AdjustMapPane(jSplitPane2);
        this.CheckButtons();
    }//GEN-LAST:event_jMenuItem_import_F_ASCIIActionPerformed

    private void jMenuItem_extractRevisitedPointesByAmountActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_extractRevisitedPointesByAmountActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_extractRevisitedPointesByAmountActionPerformed
        String sValue = JOptionPane.showInputDialog(null, "Please input the percentage of pointes' amount left after the extracting.\nValue should between (0.0, 1.0]. \nIf the value is incorrect, we will use the default value: 0.50", "Levy Flight Parameter Input", JOptionPane.QUESTION_MESSAGE);
        if (sValue == null)
        {
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, "Extracting revisited pointes by amount canceled.", Color.BLACK, Boolean.FALSE);
            return;
        }
        double percentage = 0.5;
        try
        {
            percentage = Double.parseDouble(sValue);
        } catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Value input errror", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<Double> pointList = new ArrayList<Double>();
        for (Trajectory t : ep.trajectoryPool.TrajectorySet)
        {
            pointList.addAll(t.ExtractRevistiedPointByAmount(percentage));
        }
        try
        {
            MethodLib.SaveExtractedRevistiedPointAsSHPFile(pointList, ep.generalCRS);
            JOptionPane.showMessageDialog(null, "Revisited pointes extracting & saving finished");
        } catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Errror", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jMenuItem_extractRevisitedPointesByAmountActionPerformed

    private void jMenuItem_extractRevisitedPointesByDistanceActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_extractRevisitedPointesByDistanceActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_extractRevisitedPointesByDistanceActionPerformed
        String sValue = JOptionPane.showInputDialog(null, "Please input the percentage of average flying distance as extracting threshold.\nThe value should be greater than 0.0 \nIf the value is incorrect, we will use the default value: 1.0", "Levy Flight Parameter Input", JOptionPane.QUESTION_MESSAGE);
        if (sValue == null)
        {
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, "Extracting revisited pointes by distance canceled.", Color.BLACK, Boolean.FALSE);
            return;
        }
        double percentage = 1.0;
        try
        {
            percentage = Double.parseDouble(sValue);
        } catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Value input errror", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<Double> pointList = new ArrayList<Double>();
        for (Trajectory t : ep.trajectoryPool.TrajectorySet)
        {
            pointList.addAll(t.ExtractRevistiedPointByDistance(percentage));
        }
        try
        {
            MethodLib.SaveExtractedRevistiedPointAsSHPFile(pointList, ep.generalCRS);
            JOptionPane.showMessageDialog(null, "Revisited pointes extracting & saving finished");
        } catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Errror", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jMenuItem_extractRevisitedPointesByDistanceActionPerformed

    private void jMenuItem_setFieldStyleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_setFieldStyleActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_setFieldStyleActionPerformed
        boolean isRefershField = JDialog_fieldStyleSet.getFieldColors(this.getFrame(), ep);
        if (isRefershField)
        {
            String message = "";
            try
            {
                if (ep.field.getBandToRender() == 0)//GRB样式
                {
                    message = ControlLib.MapPane_ShowRGBField(mapPane, ep);
                } else
                {
                    message = ControlLib.MapPane_ShowSingleBandField(mapPane, ep);
                }
                ControlLib.TextPane_DeclareInfo(jTextPane_Info, message, StaticLib.fontGreenColor, false);
            } catch (Exception e)
            {
                ControlLib.TextPane_DeclareInfo(jTextPane_Info, e.getMessage(), Color.red, false);
            }
        }
    }//GEN-LAST:event_jMenuItem_setFieldStyleActionPerformed

    //用户开始放大地图
    private void jButton_cr_zoomInActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_cr_zoomInActionPerformed
    {//GEN-HEADEREND:event_jButton_cr_zoomInActionPerformed
        this.mapPane.setCursorTool(new ZoomInTool());
    }//GEN-LAST:event_jButton_cr_zoomInActionPerformed

    //用户开始缩小地图
    private void jButton_cr_zoomOutActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_cr_zoomOutActionPerformed
    {//GEN-HEADEREND:event_jButton_cr_zoomOutActionPerformed
        this.mapPane.setCursorTool(new ZoomOutTool());
    }//GEN-LAST:event_jButton_cr_zoomOutActionPerformed

    //用户开始漫游地图
    private void jButton_cr_panActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_cr_panActionPerformed
    {//GEN-HEADEREND:event_jButton_cr_panActionPerformed
        this.mapPane.setCursorTool(new PanTool());
    }//GEN-LAST:event_jButton_cr_panActionPerformed

    //地图全图显示
    private void jButton_cr_worldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_cr_worldActionPerformed
    {//GEN-HEADEREND:event_jButton_cr_worldActionPerformed
        try
        {
            this.mapPane.setDisplayArea(this.mapPane.getMapContext().getLayerBounds());
        } catch (IOException ex)
        {
            javax.swing.JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }//GEN-LAST:event_jButton_cr_worldActionPerformed

    //查看选中对象信息
    private void jButton_cr_infoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_cr_infoActionPerformed
    {//GEN-HEADEREND:event_jButton_cr_infoActionPerformed
        this.mapPane.setCursorTool(new InfoTool());
    }//GEN-LAST:event_jButton_cr_infoActionPerformed

    //回到正常状态
    private void jButton_cr_normalActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton_cr_normalActionPerformed
    {//GEN-HEADEREND:event_jButton_cr_normalActionPerformed

        this.mapPane.setCursorTool(
                new CursorTool()
                {

                    @Override
                    public void onMouseClicked(MapMouseEvent evt)
                    {
                        if (evt.isControlDown())//在按下Ctrl键时，选取地理对象并显示属性。
                        {
                            try
                            {
                                ep.selectedTrajectory_freCal = null;
                                ep.selectedTrajectory = ControlLib.MapPane_SelectSingleTrajectory(evt, mapPane, ep);
                                singleAlyItemChange();
                            } catch (IOException ex)
                            {
                                ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
                            }
                        }
                    }
                });
    }//GEN-LAST:event_jButton_cr_normalActionPerformed

    //用户选择步长统计
    private void jRadioButton_alyItem_stepLengthMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jRadioButton_alyItem_stepLengthMouseClicked
    {//GEN-HEADEREND:event_jRadioButton_alyItem_stepLengthMouseClicked
        this.groupAlyItemChange();
    }//GEN-LAST:event_jRadioButton_alyItem_stepLengthMouseClicked

    //用户选择ROG统计
    private void jRadioButton_alyItem_ROGMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jRadioButton_alyItem_ROGMouseClicked
    {//GEN-HEADEREND:event_jRadioButton_alyItem_ROGMouseClicked
        this.groupAlyItemChange();
    }//GEN-LAST:event_jRadioButton_alyItem_ROGMouseClicked

    //用户选择熵统计
    private void jRadioButton_alyItem_entropyMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jRadioButton_alyItem_entropyMouseClicked
    {//GEN-HEADEREND:event_jRadioButton_alyItem_entropyMouseClicked
        this.groupAlyItemChange();
    }//GEN-LAST:event_jRadioButton_alyItem_entropyMouseClicked

    //模拟幂律分布轨迹
    private void jMenuItem_genTra_powerLawActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_genTra_powerLawActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_genTra_powerLawActionPerformed
        CoordinateReferenceSystem newCRS = this.ep.generalCRS;
        if (newCRS == null)
        {
            int dialogResult = JOptionPane.showConfirmDialog(null, "Current coordinate reference system dosen't exist, define it now?", "Notice", JOptionPane.OK_CANCEL_OPTION);
            if (dialogResult == JOptionPane.OK_OPTION)
            {
                newCRS = JCRSChooser.showDialog(this.getFrame(), "choose the current coordinate reference system", null, "4586: New Beijing / Gauss-Kruger CM 117E");
                if (newCRS == null)
                {
                    return;
                }
            } else
            {
                return;
            }
        }
        File shpFile = null;
        try
        {
            shpFile = JDialog_PowerLawTraGen.GetTrajectoryPool(this.getFrame(), true, ep, newCRS);
        } catch (Exception ex)
        {
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
            return;
        }
        if (shpFile == null)
        {
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, "TrajectoryPool generating canceled or failed", Color.RED, Boolean.FALSE);
        } else
        {
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, "TrajectoryPool generating finished", StaticLib.fontGreenColor, Boolean.FALSE);
            String message = "";
            try
            {
                if (this.ep.generalCRS == null)
                {
                    message = ControlLib.MapPane_addSHPTrajectoryAction(mapPane, ep, shpFile, true);
                } else
                {
                    message = ControlLib.MapPane_addSHPTrajectoryAction(mapPane, ep, shpFile, false);
                }
                ControlLib.TextPane_DeclareInfo(jTextPane_Info, message, StaticLib.fontGreenColor, Boolean.FALSE);
                ControlLib.AdjustMapPane(jSplitPane2);
                this.ep.trajectoryPool_freCal_ROG = null;
                this.ep.trajectoryPool_freCal_entropy = null;
                this.ep.trajectoryPool_freCal_stepLength = null;
                groupAlyItemChange();
            } catch (Exception ex)
            {
                ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
            }

        }
        this.CheckButtons();
    }//GEN-LAST:event_jMenuItem_genTra_powerLawActionPerformed

    //加载shp格式轨迹
      private void jMenuItem_Imoprt_T_SHPActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_Imoprt_T_SHPActionPerformed
    {//GEN-HEADEREND:event_jMenuItem_Imoprt_T_SHPActionPerformed
        try
        {
            String message = ControlLib.MapPane_addSHPTrajectoryAction(mapPane, ep);
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, message, StaticLib.fontGreenColor, Boolean.FALSE);
            this.ep.trajectoryPool_freCal_ROG = null;
            this.ep.trajectoryPool_freCal_entropy = null;
            this.ep.trajectoryPool_freCal_stepLength = null;
            groupAlyItemChange();
        } catch (Exception ex)
        {
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, "error: " + ex.getMessage(), Color.RED, Boolean.FALSE);
        }
        ControlLib.AdjustMapPane(jSplitPane2);
        this.CheckButtons();
    }//GEN-LAST:event_jMenuItem_Imoprt_T_SHPActionPerformed

      private void jMenuItem_graStyleSetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_graStyleSetActionPerformed
      {//GEN-HEADEREND:event_jMenuItem_graStyleSetActionPerformed
          JDialog_staGraStyleSet.setStaGraStyle(this.getFrame(), ep);
          this.groupAlyItemChange();
          this.singleAlyItemChange();
      }//GEN-LAST:event_jMenuItem_graStyleSetActionPerformed

      private void jCheckBoxMenuItem_showFreGraActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxMenuItem_showFreGraActionPerformed
      {//GEN-HEADEREND:event_jCheckBoxMenuItem_showFreGraActionPerformed
          this.jPanel_groupGra.repaint();
          this.jPanel_singleGra.repaint();
      }//GEN-LAST:event_jCheckBoxMenuItem_showFreGraActionPerformed

      private void jCheckBoxMenuItem_showcumFreGraActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxMenuItem_showcumFreGraActionPerformed
      {//GEN-HEADEREND:event_jCheckBoxMenuItem_showcumFreGraActionPerformed
          this.jPanel_groupGra.repaint();
          this.jPanel_singleGra.repaint();
      }//GEN-LAST:event_jCheckBoxMenuItem_showcumFreGraActionPerformed

      private void jRadioButtonMenuItem_CDFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonMenuItem_CDFActionPerformed
      {//GEN-HEADEREND:event_jRadioButtonMenuItem_CDFActionPerformed
          this.groupAlyItemChange();
          this.singleAlyItemChange();
      }//GEN-LAST:event_jRadioButtonMenuItem_CDFActionPerformed

      private void jRadioButtonMenuItem_CCDFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonMenuItem_CCDFActionPerformed
      {//GEN-HEADEREND:event_jRadioButtonMenuItem_CCDFActionPerformed
          this.groupAlyItemChange();
          this.singleAlyItemChange();
      }//GEN-LAST:event_jRadioButtonMenuItem_CCDFActionPerformed

      private void jMenuItem_genTra_expActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_genTra_expActionPerformed
      {//GEN-HEADEREND:event_jMenuItem_genTra_expActionPerformed
          CoordinateReferenceSystem newCRS = this.ep.generalCRS;
          if (newCRS == null)
          {
              int dialogResult = JOptionPane.showConfirmDialog(null, "Current coordinate reference system dosen't exist, define it now?", "Notice", JOptionPane.OK_CANCEL_OPTION);
              if (dialogResult == JOptionPane.OK_OPTION)
              {
                  newCRS = JCRSChooser.showDialog(this.getFrame(), "choose the current coordinate reference system", null, "4586: New Beijing / Gauss-Kruger CM 117E");
                  if (newCRS == null)
                  {
                      return;
                  }
              } else
              {
                  return;
              }
          }
          File shpFile = null;
          try
          {
              shpFile = JDialog_ExpTraGen.GetTrajectoryPool(this.getFrame(), true, ep, newCRS);
          } catch (Exception ex)
          {
              ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
              return;
          }
          if (shpFile == null)
          {
              ControlLib.TextPane_DeclareInfo(jTextPane_Info, "TrajectoryPool generating canceled or failed", Color.RED, Boolean.FALSE);
          } else
          {
              ControlLib.TextPane_DeclareInfo(jTextPane_Info, "TrajectoryPool generating finished", StaticLib.fontGreenColor, Boolean.FALSE);
              String message = "";
              try
              {
                  if (this.ep.generalCRS == null)
                  {
                      message = ControlLib.MapPane_addSHPTrajectoryAction(mapPane, ep, shpFile, true);
                  } else
                  {
                      message = ControlLib.MapPane_addSHPTrajectoryAction(mapPane, ep, shpFile, false);
                  }
                  ControlLib.TextPane_DeclareInfo(jTextPane_Info, message, StaticLib.fontGreenColor, Boolean.FALSE);
                  ControlLib.AdjustMapPane(jSplitPane2);
                  this.ep.trajectoryPool_freCal_ROG = null;
                  this.ep.trajectoryPool_freCal_entropy = null;
                  this.ep.trajectoryPool_freCal_stepLength = null;
                  groupAlyItemChange();
              } catch (Exception ex)
              {
                  ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
              }

          }
          this.CheckButtons();
      }//GEN-LAST:event_jMenuItem_genTra_expActionPerformed

      private void jMenuItem_genTra_gaussianActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_genTra_gaussianActionPerformed
      {//GEN-HEADEREND:event_jMenuItem_genTra_gaussianActionPerformed
          CoordinateReferenceSystem newCRS = this.ep.generalCRS;
          if (newCRS == null)
          {
              int dialogResult = JOptionPane.showConfirmDialog(null, "Current coordinate reference system dosen't exist, define it now?", "Notice", JOptionPane.OK_CANCEL_OPTION);
              if (dialogResult == JOptionPane.OK_OPTION)
              {
                  newCRS = JCRSChooser.showDialog(this.getFrame(), "choose the current coordinate reference system", null, "4586: New Beijing / Gauss-Kruger CM 117E");
                  if (newCRS == null)
                  {
                      return;
                  }
              } else
              {
                  return;
              }
          }
          File shpFile = null;
          try
          {
              shpFile = JDialog_GaussianTraGen.GetTrajectoryPool(this.getFrame(), true, ep, newCRS);
          } catch (Exception ex)
          {
              ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
              return;
          }
          if (shpFile == null)
          {
              ControlLib.TextPane_DeclareInfo(jTextPane_Info, "TrajectoryPool generating canceled or failed", Color.RED, Boolean.FALSE);
          } else
          {
              ControlLib.TextPane_DeclareInfo(jTextPane_Info, "TrajectoryPool generating finished", StaticLib.fontGreenColor, Boolean.FALSE);
              String message = "";
              try
              {
                  if (this.ep.generalCRS == null)
                  {
                      message = ControlLib.MapPane_addSHPTrajectoryAction(mapPane, ep, shpFile, true);
                  } else
                  {
                      message = ControlLib.MapPane_addSHPTrajectoryAction(mapPane, ep, shpFile, false);
                  }
                  ControlLib.TextPane_DeclareInfo(jTextPane_Info, message, StaticLib.fontGreenColor, Boolean.FALSE);
                  ControlLib.AdjustMapPane(jSplitPane2);
                  this.ep.trajectoryPool_freCal_ROG = null;
                  this.ep.trajectoryPool_freCal_entropy = null;
                  this.ep.trajectoryPool_freCal_stepLength = null;
                  groupAlyItemChange();
              } catch (Exception ex)
              {
                  ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
              }

          }
          this.CheckButtons();
      }//GEN-LAST:event_jMenuItem_genTra_gaussianActionPerformed

      private void jMenuItem_staBinStrategySetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_staBinStrategySetActionPerformed
      {//GEN-HEADEREND:event_jMenuItem_staBinStrategySetActionPerformed
          boolean refersh = JDialog_staBinStrategySet.setStrategy(this.getFrame(), ep);
          if (refersh)
          {
              ep.trajectoryPool_freCal_ROG=null;
              ep.trajectoryPool_freCal_direction=null;
              ep.trajectoryPool_freCal_entropy=null;
              ep.trajectoryPool_freCal_stepLength=null;
              this.groupAlyItemChange();
              this.singleAlyItemChange();
          }
      }//GEN-LAST:event_jMenuItem_staBinStrategySetActionPerformed

    /**
     * 转轴变换生成椭圆
     * @param evt
     */
      private void jMenuItem_traRotationAlyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_traRotationAlyActionPerformed
      {//GEN-HEADEREND:event_jMenuItem_traRotationAlyActionPerformed
          try
          {
              File file = MethodLib.rotationTransfrom(ep);
              if (file == null)
              {
                  ControlLib.TextPane_DeclareInfo(jTextPane_Info, "trajectory rotation analysis abandoned", Color.RED, Boolean.FALSE);
              } else
              {
                  javax.swing.JOptionPane.showMessageDialog(null, "trajectory rotation analysis accomplished and saved in "+file.getPath());
              }
          } catch (Exception ex)
          {
              ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
          }
      }//GEN-LAST:event_jMenuItem_traRotationAlyActionPerformed

      private void jMenuItem_extractPointFromTraActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_extractPointFromTraActionPerformed
      {//GEN-HEADEREND:event_jMenuItem_extractPointFromTraActionPerformed
          try
          {
              File file = MethodLib.extractNodesFromTrajectoryLayer(ep);
              if (file == null)
              {
                  ControlLib.TextPane_DeclareInfo(jTextPane_Info, "extracting pointes abandoned", Color.RED, Boolean.FALSE);
              } else
              {
                  javax.swing.JOptionPane.showMessageDialog(null, "extract pointes from accomplished and saved in "+file.getPath());
              }
          } catch (Exception ex)
          {
              ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
          }
      }//GEN-LAST:event_jMenuItem_extractPointFromTraActionPerformed

      /**
       * 加入多边形数据
       * @param evt
       */
      private void jMenuItem_importPolygonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_importPolygonActionPerformed
      {//GEN-HEADEREND:event_jMenuItem_importPolygonActionPerformed
        try
        {
            String message = ControlLib.MapPane_addSHPPolygonAction(mapPane, ep);
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, message,StaticLib.fontGreenColor , Boolean.FALSE);
        } catch (Exception ex)
        {
             ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
        }
        ControlLib.AdjustMapPane(jSplitPane2);
        CheckButtons();
      }//GEN-LAST:event_jMenuItem_importPolygonActionPerformed

      private void jLabel_fillColorMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jLabel_fillColorMouseClicked
      {//GEN-HEADEREND:event_jLabel_fillColorMouseClicked
        Color color = JColorChooser.showDialog(this.getFrame(), "Choose the fill color", this.jLabel_fillColor.getBackground());
        this.jLabel_fillColor.setBackground(color);
      }//GEN-LAST:event_jLabel_fillColorMouseClicked

      private void jMenuItem_removePolygonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_removePolygonActionPerformed
      {//GEN-HEADEREND:event_jMenuItem_removePolygonActionPerformed
        try
        {
            int num = ControlLib.RemoveLayer(mapPane, StaticLib.PolygonRegionLayerName, ep);
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, "Polygon layer removed, " + String.valueOf(num) + " Layer(s) left.", Color.BLACK, Boolean.FALSE);
            this.ep.polygonImporter=null;
        } catch (Exception ex)
        {
            ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getLocalizedMessage(), Color.RED, Boolean.FALSE);
        }
        ControlLib.AdjustMapPane(jSplitPane2);
        this.CheckButtons();
      }//GEN-LAST:event_jMenuItem_removePolygonActionPerformed

      private void jMenuItem_setPolygonStyleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_setPolygonStyleActionPerformed
      {//GEN-HEADEREND:event_jMenuItem_setPolygonStyleActionPerformed
        this.jDialog_StyleSet.setVisible(true);
        if (this.jDialog_StyleSet_OptionIndex == JOptionPane.OK_OPTION)
        {
            try
            {
                Color fillColor=this.jLabel_fillColor.getBackground();
                if(this.jCheckBox_setFillColorTransporent.isSelected())
                {
                    fillColor=null;
                }
                String message = ControlLib.ResetPolygonLayerStyle(mapPane, StaticLib.PolygonRegionLayerName, ep.polygonImporter.getAbsolutePath(), this.jRadioButton_origin.isSelected(), this.jLabel_lineColor.getBackground(), Double.parseDouble(this.jSpinner_lineWidth.getValue().toString()),fillColor);
                ControlLib.TextPane_DeclareInfo(jTextPane_Info, message, StaticLib.fontGreenColor, Boolean.FALSE);
                this.ep.trajectoryPool_lineColor = this.jLabel_lineColor.getBackground();
                this.ep.trajectoryPool_lineWidth = Double.parseDouble(this.jSpinner_lineWidth.getValue().toString());
            } catch (Exception ex)
            {
                ControlLib.TextPane_DeclareInfo(jTextPane_Info, ex.getMessage(), Color.RED, Boolean.FALSE);
            }
        }
      }//GEN-LAST:event_jMenuItem_setPolygonStyleActionPerformed

      private void jRadioButton_alyItem_directionMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jRadioButton_alyItem_directionMouseClicked
      {//GEN-HEADEREND:event_jRadioButton_alyItem_directionMouseClicked
            this.groupAlyItemChange();
      }//GEN-LAST:event_jRadioButton_alyItem_directionMouseClicked

      //在此输出步长文件
      private void jMenuItem_exportStepLengthActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_exportStepLengthActionPerformed
      {//GEN-HEADEREND:event_jMenuItem_exportStepLengthActionPerformed
            StepLengthExport export=new StepLengthExport(ep);
            export.exportStepLengthFile();
      }//GEN-LAST:event_jMenuItem_exportStepLengthActionPerformed

      private void jMenuItem_exportMovingDirectionActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_exportMovingDirectionActionPerformed
      {//GEN-HEADEREND:event_jMenuItem_exportMovingDirectionActionPerformed
            MovingDirectionExport export=new MovingDirectionExport(ep);
            export.exportMovingDirectionFile();
      }//GEN-LAST:event_jMenuItem_exportMovingDirectionActionPerformed

      private void jMenuItem_checkinsToTrajectoryActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem_checkinsToTrajectoryActionPerformed
      {//GEN-HEADEREND:event_jMenuItem_checkinsToTrajectoryActionPerformed
            JDialogCheckinToTrajectory jdctt=new JDialogCheckinToTrajectory(this.getFrame(), true);
            jdctt.setVisible(true);
      }//GEN-LAST:event_jMenuItem_checkinsToTrajectoryActionPerformed

    //整体分析绘图窗口的统一管理
    private void groupAlyItemChange()
    {
        if (this.ep.trajectoryPool == null)
        {
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_frequency = null;
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_cumfrequency = null;
            this.jPanel_groupGra.repaint();
            return;
        }
        if (this.jRadioButton_alyItem_stepLength.isSelected())//步长统计
        {
            Color red = new Color(255, 240, 240);
            this.jPanel_groupGra.setBackground(red);
            this.jTextPane_groupAly.setBackground(red);

            if (ep.trajectoryPool_freCal_stepLength == null)
            {
                ep.trajectoryPool_freCal_stepLength = new FrequencyCal(ep.trajectoryPool, ep.statisticBinningStrategy);
            }
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_frequency = new StatisticGraphGen(ep.trajectoryPool_freCal_stepLength.getMinValue(), ep.trajectoryPool_freCal_stepLength.getInterval(), ep.trajectoryPool_freCal_stepLength.getFrequencyList());
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_frequency.setColor_backGround(red);
            ep.graSchema_frequency.color_backGround = red;
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_frequency.setSchema(this.ep.graSchema_frequency);

            if (this.ep.statisticBinningStrategy.calEverySamplyInCDF)
            {
                ArrayList<Double> valueList = new ArrayList<Double>();
                ArrayList<Double> proList = new ArrayList<Double>();
                if (this.jRadioButtonMenuItem_CDF.isSelected())
                {
                    ep.trajectoryPool_freCal_stepLength.getCDFBySample(valueList, proList);
                } else
                {
                    ep.trajectoryPool_freCal_stepLength.getCCDFBySample(valueList, proList);
                }
                ((StaticGraPanel) (jPanel_groupGra)).graGenerator_cumfrequency = new StatisticGraphGen(valueList, proList);
            } else
            {
                if (this.jRadioButtonMenuItem_CDF.isSelected())
                {
                    ((StaticGraPanel) (jPanel_groupGra)).graGenerator_cumfrequency = new StatisticGraphGen(ep.trajectoryPool_freCal_stepLength.getMinValue(), ep.trajectoryPool_freCal_stepLength.getInterval(), ep.trajectoryPool_freCal_stepLength.getCDFByGroup());
                } else
                {
                    ((StaticGraPanel) (jPanel_groupGra)).graGenerator_cumfrequency = new StatisticGraphGen(ep.trajectoryPool_freCal_stepLength.getMinValue(), ep.trajectoryPool_freCal_stepLength.getInterval(), ep.trajectoryPool_freCal_stepLength.getCCDFByGroup());
                }

            }

            ep.graSchema_cumFrequency.color_backGround = red;
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_cumfrequency.setSchema(this.ep.graSchema_cumFrequency);

            FrequencyCal cal = ep.trajectoryPool_freCal_stepLength;
            try
            {
                this.jTextPane_groupAly.getDocument().remove(0, this.jTextPane_groupAly.getDocument().getLength());
            } catch (BadLocationException ex)
            {
                Logger.getLogger(LevyFlightView.class.getName()).log(Level.SEVERE, null, ex);
            }
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "Step length statistic:", Color.BLACK, true);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "total sample size: " + cal.getTotalSampleSize(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "mean value: " + cal.getAverage(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "maximum: " + cal.getMaxValue(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "minimum: " + cal.getMinValue(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "sample variance: " + cal.getSampleVariance(), Color.BLACK, false);


        } else if (this.jRadioButton_alyItem_ROG.isSelected())//回转半径统计
        {
            Color green = new Color(240, 255, 240);
            this.jPanel_groupGra.setBackground(green);
            this.jTextPane_groupAly.setBackground(green);
            if (this.ep.trajectoryPool_freCal_ROG == null)
            {
                ArrayList<Double> ROGList = new ArrayList<Double>();
                for (int i = 0; i < this.ep.trajectoryPool.TrajectorySet.size(); i++)
                {
                    Trajectory trajectory = ep.trajectoryPool.TrajectorySet.get(i);
                    ROGList.add(trajectory.GetRadiusOfGyration());
                }
                ep.trajectoryPool_freCal_ROG = new FrequencyCal(ROGList, ep.statisticBinningStrategy);
            }
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_frequency = new StatisticGraphGen(ep.trajectoryPool_freCal_ROG.getMinValue(), ep.trajectoryPool_freCal_ROG.getInterval(), ep.trajectoryPool_freCal_ROG.getFrequencyList());
            ep.graSchema_frequency.color_backGround = green;
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_frequency.setSchema(this.ep.graSchema_frequency);

            if (this.ep.statisticBinningStrategy.calEverySamplyInCDF)
            {
                ArrayList<Double> valueList = new ArrayList<Double>();
                ArrayList<Double> proList = new ArrayList<Double>();
                if (this.jRadioButtonMenuItem_CDF.isSelected())
                {
                    ep.trajectoryPool_freCal_ROG.getCDFBySample(valueList, proList);
                } else
                {
                    ep.trajectoryPool_freCal_ROG.getCCDFBySample(valueList, proList);
                }
                ((StaticGraPanel) (jPanel_groupGra)).graGenerator_cumfrequency = new StatisticGraphGen(valueList, proList);
            } else
            {
                if (this.jRadioButtonMenuItem_CDF.isSelected())
                {
                    ((StaticGraPanel) (jPanel_groupGra)).graGenerator_cumfrequency = new StatisticGraphGen(ep.trajectoryPool_freCal_ROG.getMinValue(), ep.trajectoryPool_freCal_ROG.getInterval(), ep.trajectoryPool_freCal_ROG.getCDFByGroup());
                } else
                {
                    ((StaticGraPanel) (jPanel_groupGra)).graGenerator_cumfrequency = new StatisticGraphGen(ep.trajectoryPool_freCal_ROG.getMinValue(), ep.trajectoryPool_freCal_ROG.getInterval(), ep.trajectoryPool_freCal_ROG.getCCDFByGroup());
                }
            }

            ep.graSchema_cumFrequency.color_backGround = green;
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_cumfrequency.setSchema(this.ep.graSchema_cumFrequency);


            FrequencyCal cal = ep.trajectoryPool_freCal_ROG;
            try
            {
                this.jTextPane_groupAly.getDocument().remove(0, this.jTextPane_groupAly.getDocument().getLength());
            } catch (BadLocationException ex)
            {
                Logger.getLogger(LevyFlightView.class.getName()).log(Level.SEVERE, null, ex);
            }
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "Radius of gyration statistic:", Color.BLACK, true);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "total sample size: " + cal.getTotalSampleSize(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "mean value: " + cal.getAverage(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "maximum: " + cal.getMaxValue(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "minimum: " + cal.getMinValue(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "sample variance: " + cal.getSampleVariance(), Color.BLACK, false);

        } else if (this.jRadioButton_alyItem_entropy.isSelected())//熵统计
        {
            Color blue = new Color(240, 240, 255);
            this.jPanel_groupGra.setBackground(blue);
            this.jTextPane_groupAly.setBackground(blue);
            if (this.ep.trajectoryPool_freCal_entropy == null)
            {
                ArrayList<Double> EntropyList = new ArrayList<Double>();
                for (int i = 0; i < ep.trajectoryPool.TrajectorySet.size(); i++)
                {
                    Trajectory trajectory = ep.trajectoryPool.TrajectorySet.get(i);
                    EntropyList.add(trajectory.GetEntropyByDistance(1.0));
                }
                ep.trajectoryPool_freCal_entropy = new FrequencyCal(EntropyList, this.ep.statisticBinningStrategy);
            }
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_frequency = new StatisticGraphGen(ep.trajectoryPool_freCal_entropy.getMinValue(), ep.trajectoryPool_freCal_entropy.getInterval(), ep.trajectoryPool_freCal_entropy.getFrequencyList());
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_frequency.setColor_backGround(blue);
            ep.graSchema_frequency.color_backGround = blue;
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_frequency.setSchema(this.ep.graSchema_frequency);

            if (this.ep.statisticBinningStrategy.calEverySamplyInCDF)
            {
                ArrayList<Double> valueList = new ArrayList<Double>();
                ArrayList<Double> proList = new ArrayList<Double>();
                if (this.jRadioButtonMenuItem_CDF.isSelected())
                {
                    ep.trajectoryPool_freCal_entropy.getCDFBySample(valueList, proList);
                } else
                {
                    ep.trajectoryPool_freCal_entropy.getCCDFBySample(valueList, proList);
                }
                ((StaticGraPanel) (jPanel_groupGra)).graGenerator_cumfrequency = new StatisticGraphGen(valueList, proList);
            } else
            {
                if (this.jRadioButtonMenuItem_CDF.isSelected())
                {
                    ((StaticGraPanel) (jPanel_groupGra)).graGenerator_cumfrequency = new StatisticGraphGen(ep.trajectoryPool_freCal_entropy.getMinValue(), ep.trajectoryPool_freCal_entropy.getInterval(), ep.trajectoryPool_freCal_entropy.getCDFByGroup());
                } else
                {
                    ((StaticGraPanel) (jPanel_groupGra)).graGenerator_cumfrequency = new StatisticGraphGen(ep.trajectoryPool_freCal_entropy.getMinValue(), ep.trajectoryPool_freCal_entropy.getInterval(), ep.trajectoryPool_freCal_entropy.getCCDFByGroup());
                }
            }

            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_cumfrequency.setColor_backGround(blue);
            ep.graSchema_cumFrequency.color_backGround = blue;
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_cumfrequency.setSchema(this.ep.graSchema_cumFrequency);

            FrequencyCal cal = ep.trajectoryPool_freCal_entropy;
            try
            {
                this.jTextPane_groupAly.getDocument().remove(0, this.jTextPane_groupAly.getDocument().getLength());
            } catch (BadLocationException ex)
            {
                Logger.getLogger(LevyFlightView.class.getName()).log(Level.SEVERE, null, ex);
            }
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "Entropy statistic:", Color.BLACK, true);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "total sample size: " + cal.getTotalSampleSize(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "mean value: " + cal.getAverage(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "maximum: " + cal.getMaxValue(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "minimum: " + cal.getMinValue(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "sample variance: " + cal.getSampleVariance(), Color.BLACK, false);

        }else if (this.jRadioButton_alyItem_direction.isSelected())//移动方向统计
        {
            Color magenta = new Color(255, 240, 255);
            this.jPanel_groupGra.setBackground(magenta);
            this.jTextPane_groupAly.setBackground(magenta);


            if (ep.trajectoryPool_freCal_direction == null)
            {
                ArrayList<Double>directionList=new ArrayList<Double>();
                for (int i = 0; i < this.ep.trajectoryPool.TrajectorySet.size(); i++)
                {
                    Trajectory trajectory = ep.trajectoryPool.TrajectorySet.get(i);
                    directionList.addAll(trajectory.ExtractMovingDirections());
                }
                ep.trajectoryPool_freCal_direction=new FrequencyCal(directionList, ep.statisticBinningStrategy);
            }

            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_frequency = new StatisticGraphGen(ep.trajectoryPool_freCal_direction.getMinValue(), ep.trajectoryPool_freCal_direction.getInterval(), ep.trajectoryPool_freCal_direction.getFrequencyList());
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_frequency.setColor_backGround(magenta);
            ep.graSchema_frequency.color_backGround = magenta;
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_frequency.setSchema(this.ep.graSchema_frequency);

            if (this.ep.statisticBinningStrategy.calEverySamplyInCDF)
            {
                ArrayList<Double> valueList = new ArrayList<Double>();
                ArrayList<Double> proList = new ArrayList<Double>();
                if (this.jRadioButtonMenuItem_CDF.isSelected())
                {
                    ep.trajectoryPool_freCal_direction.getCDFBySample(valueList, proList);
                } else
                {
                    ep.trajectoryPool_freCal_direction.getCCDFBySample(valueList, proList);
                }
                ((StaticGraPanel) (jPanel_groupGra)).graGenerator_cumfrequency = new StatisticGraphGen(valueList, proList);
            } else
            {
                if (this.jRadioButtonMenuItem_CDF.isSelected())
                {
                    ((StaticGraPanel) (jPanel_groupGra)).graGenerator_cumfrequency = new StatisticGraphGen(ep.trajectoryPool_freCal_direction.getMinValue(), ep.trajectoryPool_freCal_direction.getInterval(), ep.trajectoryPool_freCal_direction.getCDFByGroup());
                } else
                {
                    ((StaticGraPanel) (jPanel_groupGra)).graGenerator_cumfrequency = new StatisticGraphGen(ep.trajectoryPool_freCal_direction.getMinValue(), ep.trajectoryPool_freCal_direction.getInterval(), ep.trajectoryPool_freCal_direction.getCCDFByGroup());
                }

            }

            ep.graSchema_cumFrequency.color_backGround = magenta;
            ((StaticGraPanel) (this.jPanel_groupGra)).graGenerator_cumfrequency.setSchema(this.ep.graSchema_cumFrequency);

            FrequencyCal cal = ep.trajectoryPool_freCal_direction;
            try
            {
                this.jTextPane_groupAly.getDocument().remove(0, this.jTextPane_groupAly.getDocument().getLength());
            } catch (BadLocationException ex)
            {
                Logger.getLogger(LevyFlightView.class.getName()).log(Level.SEVERE, null, ex);
            }
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "Moving direction statistic:", Color.BLACK, true);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "total sample size: " + cal.getTotalSampleSize(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "mean value: " + cal.getAverage(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "maximum: " + cal.getMaxValue(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "minimum: " + cal.getMinValue(), Color.BLACK, false);
            ControlLib.TextPane_DeclareInfo(this.jTextPane_groupAly, "sample variance: " + cal.getSampleVariance(), Color.BLACK, false);


        }
        this.jPanel_groupGra.repaint();
    }

    /**
     * 单个轨迹分析窗口
     */
    private void singleAlyItemChange()
    {
        if (ep.selectedTrajectory_freCal == null && ep.selectedTrajectory != null)
        {
            ArrayList<Double> lengthList = new ArrayList<Double>();
            for (double d : ep.selectedTrajectory.GetStepLengthList())
            {
                lengthList.add(d);
            }
            ep.selectedTrajectory_freCal = new FrequencyCal(lengthList, ep.statisticBinningStrategy);
        }
        ((StaticGraPanel) (jPanel_singleGra)).graGenerator_frequency = new StatisticGraphGen(ep.selectedTrajectory_freCal.getMinValue(), ep.selectedTrajectory_freCal.getInterval(), ep.selectedTrajectory_freCal.getFrequencyList());
        ep.graSchema_frequency.color_backGround = jPanel_singleGra.getBackground();
        ((StaticGraPanel) (jPanel_singleGra)).graGenerator_frequency.setSchema(ep.graSchema_frequency);
        if (this.ep.statisticBinningStrategy.calEverySamplyInCDF)
        {
            ArrayList<Double> valueList = new ArrayList<Double>();
            ArrayList<Double> proList = new ArrayList<Double>();
            //ep.selectedTrajectory_freCal.getCDFBySample(valueList, proList);
            if (this.jRadioButtonMenuItem_CDF.isSelected())
            {
                ep.selectedTrajectory_freCal.getCDFBySample(valueList, proList);
            } else
            {
                ep.selectedTrajectory_freCal.getCCDFBySample(valueList, proList);
            }
            ((StaticGraPanel) (jPanel_singleGra)).graGenerator_cumfrequency = new StatisticGraphGen(valueList, proList);
        } else
        {
            if (this.jRadioButtonMenuItem_CDF.isSelected())
            {
                ((StaticGraPanel) (jPanel_singleGra)).graGenerator_cumfrequency = new StatisticGraphGen(ep.selectedTrajectory_freCal.getMinValue(), ep.selectedTrajectory_freCal.getInterval(), ep.selectedTrajectory_freCal.getCDFByGroup());
            } else
            {
                ((StaticGraPanel) (jPanel_singleGra)).graGenerator_cumfrequency = new StatisticGraphGen(ep.selectedTrajectory_freCal.getMinValue(), ep.selectedTrajectory_freCal.getInterval(), ep.selectedTrajectory_freCal.getCCDFByGroup());
            }

        }
        ep.graSchema_cumFrequency.color_backGround=jPanel_singleGra.getBackground();
        ((StaticGraPanel) (jPanel_singleGra)).graGenerator_cumfrequency.setSchema(ep.graSchema_cumFrequency);


        FrequencyCal cal = ep.selectedTrajectory_freCal;
        try
        {
            jTextPane_singleAly.getDocument().remove(0, jTextPane_singleAly.getDocument().getLength());
        } catch (BadLocationException ex)
        {
            Logger.getLogger(LevyFlightView.class.getName()).log(Level.SEVERE, null, ex);
        }
        ControlLib.TextPane_DeclareInfo(jTextPane_singleAly, "Single Trajectory Statistic:", Color.BLACK, true);
        ControlLib.TextPane_DeclareInfo(jTextPane_singleAly, "total sample size: " + cal.getTotalSampleSize(), Color.BLACK, false);
        ControlLib.TextPane_DeclareInfo(jTextPane_singleAly, "mean value: " + cal.getAverage(), Color.BLACK, false);
        ControlLib.TextPane_DeclareInfo(jTextPane_singleAly, "maximum: " + cal.getMaxValue(), Color.BLACK, false);
        ControlLib.TextPane_DeclareInfo(jTextPane_singleAly, "minimum: " + cal.getMinValue(), Color.BLACK, false);
        ControlLib.TextPane_DeclareInfo(jTextPane_singleAly, "sample variance: " + cal.getSampleVariance(), Color.BLACK, false);
        ControlLib.TextPane_DeclareInfo(jTextPane_singleAly, "center point: " + ep.selectedTrajectory.GetCentrePoint().toString(), Color.BLACK, false);
        EigenvectorCal eigenvectorCal = new EigenvectorCal(ep.selectedTrajectory);
        ControlLib.TextPane_DeclareInfo(jTextPane_singleAly, "majorSemiAxis: " + eigenvectorCal.getMajorSemiAxis(), Color.BLACK, false);
        ControlLib.TextPane_DeclareInfo(jTextPane_singleAly, "minorSemiAxis: " + eigenvectorCal.getMinorSemiAxis(), Color.BLACK, false);
        double[] eigenvector = eigenvectorCal.getEigenVector();
        ControlLib.TextPane_DeclareInfo(jTextPane_singleAly, "eigenvector: [" + eigenvector[0] + " , " + eigenvector[1] + "]", Color.BLACK, false);
        ControlLib.TextPane_DeclareInfo(jTextPane_singleAly, "rotation in angle: " + eigenvectorCal.getRotationAngle() * 180 / Math.PI, Color.BLUE, false);
        ControlLib.TextPane_DeclareInfo(jTextPane_singleAly, "span horizonal: " + ep.selectedTrajectory.getBoundryBox().GetLatitudinalSpan(), Color.BLUE, false);
        ControlLib.TextPane_DeclareInfo(jTextPane_singleAly, "span vertical: " + ep.selectedTrajectory.getBoundryBox().GetLongitudinalSpan(), Color.BLUE, false);


        jPanel_singleGra.repaint();
    }

    /**
     * 根据当前地图中的图层来设定按钮的enabled值
     */
    private void CheckButtons()
    {
        boolean flag = false;
        if (this.mapPane.getMapContext() == null)
        {
            this.jMenuItem_removeField.setEnabled(flag);
            this.jMenuItem_removeRoads.setEnabled(flag);
            this.jMenuItem_setRoadStyle.setEnabled(flag);
            this.jMenuItem_removeTra.setEnabled(flag);
            this.jMenuItem_setTrajectoryStyle.setEnabled(flag);
            this.jMenuItem_setFieldStyle.setEnabled(flag);
            return;
        }
        flag = ControlLib.IsMapContains(Field.className, this.mapPane.getMapContext());
        this.jMenuItem_removeField.setEnabled(flag);
        this.jMenuItem_setFieldStyle.setEnabled(flag);
        flag = ControlLib.IsMapContains(Roads.className, this.mapPane.getMapContext());
        this.jMenuItem_removeRoads.setEnabled(flag);
        this.jMenuItem_setRoadStyle.setEnabled(flag);
        flag = ControlLib.IsMapContains(TrajectoryPool.className, this.mapPane.getMapContext());
        this.jMenuItem_removeTra.setEnabled(flag);
        this.jMenuItem_setTrajectoryStyle.setEnabled(flag);
        this.jMenu_ExtractRevisitedPointes.setEnabled(flag);
        this.jMenuItem_traRotationAly.setEnabled(flag);
        this.jMenuItem_exportMovingDirection.setEnabled(flag);
        this.jMenuItem_exportStepLength.setEnabled(flag);
        flag=ControlLib.IsMapContains(StaticLib.PolygonRegionLayerName, this.mapPane.getMapContext());
        this.jMenuItem_removePolygon.setEnabled(flag);
        this.jMenuItem_setPolygonStyle.setEnabled(flag);
    }

    /**
     * this gets rid of exception for not using native acceleration
     */
    static
    {
        System.setProperty("com.sun.media.jai.disableMediaLib", "true");
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu AnalysisMenu;
    private javax.swing.JMenu ExportMenu;
    private javax.swing.JMenu OptionMenu;
    private javax.swing.JMenu SocialNatworkMenu;
    private javax.swing.JMenu TraGenMenu;
    private javax.swing.ButtonGroup buttonGroup_CDFStyle;
    private javax.swing.ButtonGroup buttonGroup_alyItemsGroup;
    private javax.swing.ButtonGroup buttonGroup_mode;
    private javax.swing.JButton jButton_SetStyleCancel;
    private javax.swing.JButton jButton_SetStyleOK;
    private javax.swing.JButton jButton_cr_info;
    private javax.swing.JButton jButton_cr_normal;
    private javax.swing.JButton jButton_cr_pan;
    private javax.swing.JButton jButton_cr_world;
    private javax.swing.JButton jButton_cr_zoomIn;
    private javax.swing.JButton jButton_cr_zoomOut;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem_showFreGra;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem_showcumFreGra;
    private javax.swing.JCheckBox jCheckBox_setFillColorTransporent;
    int jDialog_StyleSet_OptionIndex=JOptionPane.CANCEL_OPTION;
    private javax.swing.JDialog jDialog_StyleSet;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel_fillColor;
    private javax.swing.JLabel jLabel_lineColor;
    private javax.swing.JLabel jLabel_locationTip;
    private javax.swing.JMenuItem jMenuItem_Imoprt_T_SHP;
    private javax.swing.JMenuItem jMenuItem_Import_Custom;
    private javax.swing.JMenuItem jMenuItem_Import_F_geotiff;
    private javax.swing.JMenuItem jMenuItem_Import_GPS;
    private javax.swing.JMenuItem jMenuItem_checkinsToTrajectory;
    private javax.swing.JMenuItem jMenuItem_chooseCRS;
    private javax.swing.JMenuItem jMenuItem_exportMovingDirection;
    private javax.swing.JMenuItem jMenuItem_exportStepLength;
    private javax.swing.JMenuItem jMenuItem_extractPointFromTra;
    private javax.swing.JMenuItem jMenuItem_extractRevisitedPointesByAmount;
    private javax.swing.JMenuItem jMenuItem_extractRevisitedPointesByDistance;
    private javax.swing.JMenuItem jMenuItem_genTra_exp;
    private javax.swing.JMenuItem jMenuItem_genTra_gaussian;
    private javax.swing.JMenuItem jMenuItem_genTra_powerLaw;
    private javax.swing.JMenuItem jMenuItem_graStyleSet;
    private javax.swing.JMenuItem jMenuItem_importPolygon;
    private javax.swing.JMenuItem jMenuItem_importRoads;
    private javax.swing.JMenuItem jMenuItem_import_F_ASCII;
    private javax.swing.JMenuItem jMenuItem_removeAll;
    private javax.swing.JMenuItem jMenuItem_removeField;
    private javax.swing.JMenuItem jMenuItem_removePolygon;
    private javax.swing.JMenuItem jMenuItem_removeRoads;
    private javax.swing.JMenuItem jMenuItem_removeTra;
    private javax.swing.JMenuItem jMenuItem_setFieldStyle;
    private javax.swing.JMenuItem jMenuItem_setPolygonStyle;
    private javax.swing.JMenuItem jMenuItem_setRoadStyle;
    private javax.swing.JMenuItem jMenuItem_setTrajectoryStyle;
    private javax.swing.JMenuItem jMenuItem_showCRS;
    private javax.swing.JMenuItem jMenuItem_staBinStrategySet;
    private javax.swing.JMenuItem jMenuItem_traRotationAly;
    private javax.swing.JMenu jMenu_ExtractRevisitedPointes;
    private javax.swing.JMenu jMenu_ImportTraj;
    private javax.swing.JMenu jMenu_SetStyle;
    private javax.swing.JMenu jMenu_importField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel_groupGra;
    private javax.swing.JPanel jPanel_singleGra;
    private javax.swing.JPopupMenu jPopupMenu_graphStyleSet;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem_CCDF;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem_CDF;
    private javax.swing.JRadioButton jRadioButton_alyItem_ROG;
    private javax.swing.JRadioButton jRadioButton_alyItem_direction;
    private javax.swing.JRadioButton jRadioButton_alyItem_entropy;
    private javax.swing.JRadioButton jRadioButton_alyItem_stepLength;
    private javax.swing.JRadioButton jRadioButton_custom;
    private javax.swing.JRadioButton jRadioButton_origin;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JSpinner jSpinner_lineWidth;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JSplitPane jSplitPane_groupAly;
    private javax.swing.JTabbedPane jTabbedPane_statistic;
    private javax.swing.JTextPane jTextPane_Info;
    private javax.swing.JTextPane jTextPane_groupAly;
    private javax.swing.JTextPane jTextPane_singleAly;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;

    private class StaticGraPanel extends JPanel
    {

        /**
         * 用于绘制频率图的类
         */
        public StatisticGraphGen graGenerator_frequency = null;
        /**
         * 用于绘制累积频率图的类
         */
        public StatisticGraphGen graGenerator_cumfrequency = null;
        /**
         * 标识是否要绘制频率图
         */
        public boolean isRendeFrequencyGra = true;
        /**
         * 标识是否要绘制累积频率图
         */
        public boolean isRendeCumFrequencyGra = true;

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            this.isRendeFrequencyGra = jCheckBoxMenuItem_showFreGra.isSelected();
            this.isRendeCumFrequencyGra = jCheckBoxMenuItem_showcumFreGra.isSelected();
            if (this.getHeight() < 100 || this.getWidth() < 100)//空间太小则放弃绘制
            {
                return;
            }
            if (this.graGenerator_cumfrequency == null && this.graGenerator_frequency == null)
            {
                g.setColor(this.getBackground());
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
            if (this.isRendeCumFrequencyGra && this.isRendeFrequencyGra)//画两幅图
            {
                if (this.graGenerator_cumfrequency != null && this.graGenerator_frequency != null)//两幅图都可以画
                {
                    if (this.getWidth() > this.getHeight())//宽扁形，左右画
                    {
                        int width = this.getWidth() / 2;
                        int height = this.getHeight();
                        g.drawImage(this.graGenerator_frequency.generateDefaultGraph(width, height), 0, 0, null);
                        g.drawImage(this.graGenerator_cumfrequency.generateDefaultGraph(width, height), width, 0, null);
                    } else//高瘦性，上下画
                    {
                        int width = this.getWidth();
                        int height = this.getHeight() / 2;
                        g.drawImage(this.graGenerator_frequency.generateDefaultGraph(width, height), 0, 0, null);
                        g.drawImage(this.graGenerator_cumfrequency.generateDefaultGraph(width, height), 0, height, null);
                    }
                }
            } else if (this.isRendeFrequencyGra)//仅画频率图
            {
                if (this.graGenerator_frequency != null)
                {
                    g.drawImage(this.graGenerator_frequency.generateDefaultGraph(this.getWidth(), this.getHeight()), 0, 0, null);
                }
            } else if (this.isRendeCumFrequencyGra)//仅画累积频率图
            {
                if (this.graGenerator_cumfrequency != null)
                {
                    g.drawImage(this.graGenerator_cumfrequency.generateDefaultGraph(this.getWidth(), this.getHeight()), 0, 0, null);
                }
            } else
            {
                return;
            }

        }
    }
}
