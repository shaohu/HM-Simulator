/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight;

import Static.StaticLib;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import levyflight.Data.Field;
import levyflight.Data.Node;
import levyflight.Data.Roads;
import levyflight.Data.TimePrecisionEnum;
import levyflight.Data.Trajectory;
import levyflight.Data.TrajectoryPool;
import levyflight.Import.MethodLib;
import levyflight.Import.SHPPolygonImporter;
import levyflight.Import.SHPRoadImporter;
import levyflight.Import.SHPTraImporter;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.ViewType;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.feature.FeatureCollections;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ColorMap;
import org.geotools.styling.ColorMapImpl;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.SLDParser;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.swing.JMapPane;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.swing.event.MapMouseEvent;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.ContrastMethod;

/**
 *
 * @author Tiger Shao
 */
public class ControlLib
{

    /**
     * 向TextPane中添加相应的信息列
     * @param component TextPane组件
     * @param info 信息
     * @param infoColor 字的颜色
     * @param isBold 是否为粗体
     */
    public static void TextPane_DeclareInfo(JTextPane component, String info, Color infoColor, Boolean isBold)
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

    /**
     * 添加shp类型的道路文件
     * @param mapPane
     * @param ep
     * @return
     * @throws Exception
     */
    public static String MapPane_addSHPRoadsAction(JMapPane mapPane, EnvironmentParameter ep) throws Exception
    {
        File sourceFile = JFileDataStoreChooser.showOpenFile("shp", StaticLib.InitialFileDirectory, null);
        StaticLib.InitialFileDirectory = sourceFile;
        String message = "";
        if (sourceFile == null)
        {
            return ("Importing .shp roads canceled");
        }
        if (!MethodLib.IsSHPLineType(sourceFile))
        {
            throw (new Exception("Roadfile " + sourceFile.getName() + " is not a line type!"));
        }
        SHPRoadImporter importer = new SHPRoadImporter(sourceFile);

        message += MatchCRS(importer.CoordinateReference, ep);


        ep.road = new Roads(importer.CoordinateReference);
        ep.road.Fill(importer);
        importer.Dispose();

        FileDataStore store = FileDataStoreFinder.getDataStore(sourceFile);
        SimpleFeatureSource featureSource = store.getFeatureSource();
        Style style = GetSourceStyle(sourceFile.getAbsolutePath());

        MapContext generator = new DefaultMapContext();
        generator.addLayer(featureSource, style);
        MapLayer newLayer = generator.getLayer(0);
        newLayer.setTitle(Roads.className);
        MapContext oldmap = mapPane.getMapContext();
        MapContext map = MapAddNewLayerSafely(oldmap, newLayer);
        mapPane.setMapContext(map);

        message += "RoadFile: " + sourceFile.getName() + " importing finished\n";
        message += String.valueOf(ep.road.roadList.size()) + " road(s) added";
        return (message);

    }


    public static String MapPane_addSHPPolygonAction(JMapPane mapPane, EnvironmentParameter ep)throws Exception
    {
        File sourceFile = JFileDataStoreChooser.showOpenFile("shp", StaticLib.InitialFileDirectory, null);
        StaticLib.InitialFileDirectory = sourceFile;
        String message = "";
        if (sourceFile == null)
        {
            return ("Importing .shp polygon canceled");
        }
        if (!MethodLib.IsSHPPolygonType(sourceFile))
        {
            throw (new Exception("Polygon file " + sourceFile.getName() + " is not a polygon type!"));
        }
        SHPPolygonImporter importer = new SHPPolygonImporter(sourceFile);
        message += MatchCRS(importer.getCoordinateReferenceSystem(), ep);
        ep.polygonImporter=importer;

        FileDataStore store = FileDataStoreFinder.getDataStore(sourceFile);
        SimpleFeatureSource featureSource = store.getFeatureSource();
        Style style = GetSourceStyle(sourceFile.getAbsolutePath());

        MapContext generator = new DefaultMapContext();
        generator.addLayer(featureSource, style);
        MapLayer newLayer = generator.getLayer(0);
        newLayer.setTitle(StaticLib.PolygonRegionLayerName);
        MapContext oldmap = mapPane.getMapContext();
        MapContext map = MapAddNewLayerSafely(oldmap, newLayer);
        mapPane.setMapContext(map);

        message += "PolygonFile: " + sourceFile.getName() + " importing finished\n";
        message += String.valueOf(ep.polygonImporter.getFeatureCollection().size()) + " polygon(s) added";
        return (message);

    }

    /**
     * 根据地理对象生成相应的显示风格
     * @param sourceFilePath
     * @return
     * @throws Exception
     */
    private static Style GetSourceStyle(String sourceFilePath) throws Exception
    {
        String base = sourceFilePath.substring(0, sourceFilePath.length() - 4);
        String newPath = base + ".sld";
        File sld = new File(newPath);
        Boolean isSldExists = false;
        if (sld.exists())
        {
            isSldExists = true;
        }
        if (!isSldExists)
        {
            newPath = base + ".SLD";
            sld = new File(newPath);
            if (sld.exists())
            {
                isSldExists = true;
            }
        }
        if (isSldExists)
        {
            SLDParser stylereader = new SLDParser(CommonFactoryFinder.getStyleFactory(null), sld.toURI().toURL());
            Style[] style = stylereader.readXML();
            return style[0];
        } else
        {
            return null;
        }
        //此处featureSource暂时未用到，以后可以再进一步拓展！
    }

    /**
     * 添加shp类型的路径文件
     * @param mapPane
     * @param ep
     * @return
     * @throws Exception
     */
    public static String MapPane_addSHPTrajectoryAction(JMapPane mapPane, EnvironmentParameter ep) throws Exception
    {
        File sourceFile = JFileDataStoreChooser.showOpenFile("shp", StaticLib.InitialFileDirectory, null);
        StaticLib.InitialFileDirectory = sourceFile;
        String message = "";
        if (sourceFile == null)
        {
            return ("Importing canceled");
        }
        if (!MethodLib.IsSHPLineType(sourceFile))
        {
            throw (new Exception("Trajectoryfile " + sourceFile.getName() + " is not a line type!"));
        }
        SHPTraImporter importer = new SHPTraImporter(sourceFile.getAbsolutePath());

        message += MatchCRS(importer.CoordinateReference, ep);

        ep.trajectoryPool = new TrajectoryPool(importer.CoordinateReference, TimePrecisionEnum.SECOND);
        ep.trajectoryPool.Fill(importer);
        importer.Dispose();

        FileDataStore store = FileDataStoreFinder.getDataStore(sourceFile);
        SimpleFeatureSource featureSource = store.getFeatureSource();
        Style style = GetSourceStyle(sourceFile.getAbsolutePath());

        MapContext generator = new DefaultMapContext();
        generator.addLayer(featureSource, style);
        MapLayer newLayer = generator.getLayer(0);
        newLayer.setTitle(TrajectoryPool.className);
        MapContext oldmap = mapPane.getMapContext();
        MapContext map = MapAddNewLayerSafely(oldmap, newLayer);
        mapPane.setMapContext(map);

        message += "TrajectoryFile: " + sourceFile.getName() + " importing finished\n";
        message += String.valueOf(ep.trajectoryPool.TrajectorySet.size()) + " trajectory(s) added";
        return (message);
    }

    /**
     * 添加shp类型的路径文件
     * @param mapPane
     * @param ep
     * @param sourceFile shp源文件
     * @param checkCRS 控制是否要检查CRS
     * @return
     * @throws Exception
     */
    public static String MapPane_addSHPTrajectoryAction(JMapPane mapPane, EnvironmentParameter ep, File sourceFile, boolean checkCRS) throws Exception
    {
        StaticLib.InitialFileDirectory = sourceFile;
        String message = "";
        if (sourceFile == null)
        {
            return ("File dosen't exist, importing failed.");
        }
        if (!MethodLib.IsSHPLineType(sourceFile))
        {
            throw (new Exception("Trajectoryfile " + sourceFile.getName() + " is not a line type!"));
        }
        SHPTraImporter importer = new SHPTraImporter(sourceFile.getAbsolutePath());

        if (checkCRS)
        {
            message += MatchCRS(importer.CoordinateReference, ep);
        }

        ep.trajectoryPool = new TrajectoryPool(importer.CoordinateReference, TimePrecisionEnum.SECOND);
        ep.trajectoryPool.Fill(importer);
        importer.Dispose();

        FileDataStore store = FileDataStoreFinder.getDataStore(sourceFile);
        SimpleFeatureSource featureSource = store.getFeatureSource();
        Style style = GetSourceStyle(sourceFile.getAbsolutePath());

        MapContext generator = new DefaultMapContext();
        generator.addLayer(featureSource, style);
        MapLayer newLayer = generator.getLayer(0);
        newLayer.setTitle(TrajectoryPool.className);
        MapContext oldmap = mapPane.getMapContext();
        MapContext map = MapAddNewLayerSafely(oldmap, newLayer);
        mapPane.setMapContext(map);


        message += "TrajectoryFile: " + sourceFile.getName() + " importing finished\n";
        message += String.valueOf(ep.trajectoryPool.TrajectorySet.size()) + " trajectory(s) added";
        return (message);
    }

    /**
     * 添加“场”-栅格文件，tif/jpg
     * @param mapPane
     * @param ep
     * @param component
     * @return
     * @throws Exception
     */
    public static String ImportFieldGeotiff(JMapPane mapPane, EnvironmentParameter ep, JTextPane component) throws Exception
    {
        File sourceFile = JFileDataStoreChooser.showOpenFile(new String[]
                {
                    "tif", "jpg"
                }, StaticLib.InitialFileDirectory, null);
        if (sourceFile == null)
        {
            return ("Importing Field Canceled");
        }
        StaticLib.InitialFileDirectory = sourceFile;
        String message = "";
        AbstractGridFormat gridFormat = GridFormatFinder.findFormat(sourceFile);
        AbstractGridCoverage2DReader reader = gridFormat.getReader(sourceFile);
        GridCoverage2D cov = reader.read(null);


        message += MatchCRS(cov.getCoordinateReferenceSystem(), ep);

        ep.field = Field.newGeotiffField(cov, sourceFile.getAbsolutePath());

        if (ep.field.getFieldType() == Field.FieldType_GEOTIFF && ep.field.getNumBands() >= 3)
        {
            message += MapPane_ShowRGBField(mapPane, ep);
        } else
        {
            TextPane_DeclareInfo(component, "File has less than 3 bands.\nRendering band 1 in greyscale style...", Color.BLACK, Boolean.FALSE);
            ep.field.setBandToRender(1);
            message += MapPane_ShowSingleBandField(mapPane, ep);
        }
        return (message);
    }

    /**
     * 向程序中加入ASCII格式的栅格数据
     * @param mapPane
     * @param ep
     * @param component
     * @return
     * @throws Exception
     */
    public static String ImportFieldASCII(JMapPane mapPane, EnvironmentParameter ep, JTextPane component) throws Exception
    {
        File sourceFile = JFileDataStoreChooser.showOpenFile(new String[]
                {
                    "asc", "arx", "txt"
                }, StaticLib.InitialFileDirectory, null);
        if (sourceFile == null)
        {
            return ("Importing Field Canceled");
        }
        StaticLib.InitialFileDirectory = sourceFile;
        String message = "";

        //here we check the prj file
        String prjFilePath = sourceFile.getAbsolutePath();
        int index = prjFilePath.lastIndexOf(".");
        if (index != -1)
        {
            prjFilePath = prjFilePath.substring(0, index);
        }
        prjFilePath += ".prj";
        final File prjFile = new File(prjFilePath);
        if (!prjFile.exists())//ASCII文件没有匹配的投影信息
        {
            if (ep.generalCRS == null)//current CRS 不存在
            {
                int dialogResult = JOptionPane.showConfirmDialog(null, "The projection file as \"" + prjFilePath + "\" dosen't exist, either dose current coordinate reference system.\nSelect \"OK\" to load the ASCII file anyway, current coordinate reference system will not be initialized. \nOr \"Cancel\" to  abandon loading the file.", "Notice", JOptionPane.OK_CANCEL_OPTION);
                if (dialogResult == JOptionPane.OK_OPTION)
                {
                    ArcGridReader agr = new ArcGridReader(sourceFile);
                    GridCoverage2D cov = (GridCoverage2D) agr.read(null);
                    ep.field = Field.newASCIIField(cov, sourceFile.getAbsolutePath(), null);
                } else
                {
                    return ("Importing Field Canceled");
                }

            } else//current CRS 存在尝试使用current 作为ASCII的CRS
            {
                TextPane_DeclareInfo(component, "The projection file as \"" + prjFilePath + "\" dosen't exist, Trying to force current coordinate reference system to the ASCII file.", Color.BLACK, Boolean.FALSE);
                Hints crsHints = new Hints();
                crsHints.putSystemDefault(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, ep.generalCRS);
                ArcGridReader agr = new ArcGridReader(sourceFile, crsHints);
                GridCoverage2D cov = (GridCoverage2D) agr.read(null);
                ep.field = Field.newASCIIField(cov, sourceFile.getAbsolutePath(), crsHints);
            }

        } else//ASCII文件具有投影信息
        {
            ArcGridReader agr = new ArcGridReader(sourceFile);
            GridCoverage2D cov = (GridCoverage2D) agr.read(null);

            message += MatchCRS(cov.getCoordinateReferenceSystem(), ep);

            ep.field = Field.newASCIIField(cov, sourceFile.getAbsolutePath(), null);
        }

        if (ep.field.getFieldType() == Field.FieldType_GEOTIFF && ep.field.getNumBands() >= 3)
        {
            message += MapPane_ShowRGBField(mapPane, ep);
        } else
        {
            TextPane_DeclareInfo(component, "File has less than 3 bands.\nRendering band 1 in greyscale style...", Color.BLACK, Boolean.FALSE);
            ep.field.setBandToRender(1);
            message += MapPane_ShowSingleBandField(mapPane, ep);
        }


        return message;
    }

    /**
     * 以RBG形式显示栅格文件，如果小于3波段，以灰度形式显示第一波段
     * @param mapPane
     * @param ep
     * @return
     * @throws Exception
     */
    public static String MapPane_ShowRGBField(JMapPane mapPane, EnvironmentParameter ep) throws Exception
    {
        int numBands = ep.field.GridCoverage.getNumSampleDimensions();
        String message = "";
        String[] sampleDimensionNames = new String[numBands];
        for (int i = 0; i < numBands; i++)
        {
            GridSampleDimension dim = ep.field.GridCoverage.getSampleDimension(i);
            sampleDimensionNames[i] = dim.getDescription().toString();
        }
        StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
        final int red = 0, green = 1, blue = 2;
        int[] channelNum =
        {
            -1, -1, -1
        };
        for (int i = 0; i < numBands; i++)
        {
            String name = sampleDimensionNames[i].toLowerCase();
            if (name != null)
            {
                if (name.matches("red.*"))
                {
                    channelNum[red] = i + 1;
                } else if (name.matches("green.*"))
                {
                    channelNum[green] = i + 1;
                } else if (name.matches("blue.*"))
                {
                    channelNum[blue] = i + 1;
                }
            }
        }
        if (channelNum[red] < 0 || channelNum[green] < 0 || channelNum[blue] < 0)
        {
            channelNum[red] = 1;
            channelNum[green] = 2;
            channelNum[blue] = 3;
        }
        SelectedChannelType[] sct = new SelectedChannelType[numBands];
        ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
        for (int i = 0; i < 3; i++)
        {
            sct[i] = sf.createSelectedChannelType(String.valueOf(channelNum[i]), ce);
        }
        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        ChannelSelection sel = sf.channelSelection(sct[red], sct[green], sct[blue]);
        sym.setChannelSelection(sel);
        Style style = SLD.wrapSymbolizers(sym);

        MapContext oldmap = mapPane.getMapContext();
        MapLayer newLayer = new MapLayer(ep.field.getReader(), style, Field.className);
        MapContext map = MapAddNewLayerSafely(oldmap, newLayer);
        mapPane.setMapContext(map);

        message = "Field rendering finished. RBG style.\n";
        message += "Red:band-" + String.valueOf(channelNum[red]) + ", ";
        message += "Green:band-" + String.valueOf(channelNum[green]) + ", ";
        message += "Blue:band-" + String.valueOf(channelNum[blue]) + ".";
        return (message);

    }

    /**
     * 以灰度形式显示某个单一波段
     * @param mapPane
     * @param ep
     * @return
     * @throws Exception
     */
    public static String MapPane_ShowSingleBandField(JMapPane mapPane, EnvironmentParameter ep) throws Exception
    {
        int bandIndex = ep.field.getBandToRender();
        int num = ep.field.GridCoverage.getNumSampleDimensions();
        if (bandIndex < 1 || bandIndex > num)
        {
            throw (new Exception("Selected band not exists, rendering canceled"));
        }
        StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);


//        ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
//        SelectedChannelType sct = sf.createSelectedChannelType(String.valueOf(bandIndex), ce);
//        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
//        ChannelSelection sel = sf.channelSelection(sct);
//        sym.setChannelSelection(sel);
//        Style style = SLD.wrapSymbolizers(sym);
//        MapContext oldmap = mapPane.getMapContext();
//        MapLayer newLayer = new MapLayer(ep.field.getReader(), style, Field.className);
//        MapContext map = MapAddNewLayerSafely(oldmap, newLayer);
//        mapPane.setMapContext(map);

        double[] values = ep.field.getExtremums();
        double minValue = values[(bandIndex - 1) * 2];
        double maxValue = values[(bandIndex - 1) * 2 + 1];
        double noDataValue = ep.field.getNoDataValue();
        if (minValue == maxValue)
        {
            minValue -= 1;
            maxValue += 1;
        }


        String[] labelList = null;
        double[] valueStation = null;
        Color[] colorList = null;
        if (String.valueOf(noDataValue) == null ? String.valueOf(Double.NaN) == null : String.valueOf(noDataValue).equals(String.valueOf(Double.NaN)))//不需要用到noDataColor
        {
            int count = ep.field_colorBand.length;
            labelList = new String[count];
            valueStation = new double[count];
            colorList = ep.field_colorBand;
            for (int i = 0; i < count; i++)
            {
                valueStation[i] = (double) i / (double) count * (maxValue - minValue) + minValue;
                labelList[i] = "value-" + valueStation[i] + " color: R-" + colorList[i].getRed() + " G-" + colorList[i].getGreen() + " B-" + colorList[i].getBlue();
            }

        } else if (noDataValue < minValue)//在队列头加入noDatacolor
        {
            int count = ep.field_colorBand.length + 1;
            labelList = new String[count];
            valueStation = new double[count];
            colorList = new Color[count];
            valueStation[0] = noDataValue;
            colorList[0] = ep.field_noDataColor;
            labelList[0] = "value-noData color: R-" + ep.field_noDataColor.getRed() + " G-" + ep.field_noDataColor.getGreen() + " B-" + ep.field_noDataColor.getBlue();
            count -= 1;
            System.arraycopy(ep.field_colorBand, 0, colorList, 1, count);
            for (int i = 0; i < count; i++)
            {
                valueStation[i + 1] = (double) i / (double) count * (maxValue - minValue) + minValue;
                labelList[i + 1] = "value-" + valueStation[i + 1] + " color: R-" + colorList[i + 1].getRed() + " G-" + colorList[i + 1].getGreen() + " B-" + colorList[i].getBlue();
            }

        } else if (noDataValue > maxValue)
        {
            int count = ep.field_colorBand.length + 1;
            labelList = new String[count];
            valueStation = new double[count];
            colorList = new Color[count];
            count -= 1;
            valueStation[count] = noDataValue;
            colorList[count] = ep.field_noDataColor;
            labelList[count] = "value-noData color: R-" + ep.field_noDataColor.getRed() + " G-" + ep.field_noDataColor.getGreen() + " B-" + ep.field_noDataColor.getBlue();
            System.arraycopy(ep.field_colorBand, 0, colorList, 0, count);
            for (int i = 0; i < count; i++)
            {
                valueStation[i] = (double) i / (double) count * (maxValue - minValue) + minValue;
                labelList[i] = "value-" + valueStation[i] + " color: R-" + colorList[i].getRed() + " G-" + colorList[i].getGreen() + " B-" + colorList[i].getBlue();
            }
        } else
        {
            throw (new Exception("Error occurs with the field data, rendering failed"));
        }

        StyleBuilder builder = new StyleBuilder();
        ColorMap colorMap = builder.createColorMap(labelList, valueStation, colorList, ColorMap.TYPE_RAMP);
        RasterSymbolizer sym2 = builder.createRasterSymbolizer(colorMap, 1);
        ContrastEnhancement ce2 = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NONE);
        SelectedChannelType sct2 = sf.createSelectedChannelType(String.valueOf(bandIndex), ce2);
        ChannelSelection sel2 = sf.channelSelection(sct2);
        sym2.setChannelSelection(sel2);
        Style style2 = SLD.wrapSymbolizers(sym2);
        MapContext oldmap = mapPane.getMapContext();
        MapLayer newLayer = new MapLayer(ep.field.getReader(), style2, Field.className);
        MapContext map = MapAddNewLayerSafely(oldmap, newLayer);
        mapPane.setMapContext(map);





        return ("Band " + String.valueOf(bandIndex) + " rendering finished");
    }

    /**
     * 判断因加入的文件和已有的坐标系统是否匹配
     * @param crs
     * @param ep
     * @return
     * @throws Exception
     */
    public static String MatchCRS(CoordinateReferenceSystem crs, EnvironmentParameter ep) throws Exception
    {
        if (ep.generalCRS != null && !ep.generalCRS.equals(crs))//CRS.equalsIgnoreMetadata(ep.generalCRS, importer.CoordinateReference)
        {
            throw (new Exception("Coordinate reference systems don't match, importing failed"));
        }
        String message = "";
        if (ep.generalCRS != null)
        {
            return (message);
        } else
        {
            ep.generalCRS = crs;
            message += "General coordinate reference system initialized.\n";
            message += crs.toWKT() + "\n";
            return message;
        }
    }

    /**
     * 适当调节JMapPane以保证其正确显示
     * @param pane
     */
    public static void AdjustMapPane(JSplitPane pane)
    {
        int location = pane.getDividerLocation();
        if (location % 2 == 0)
        {
            location += 1;
        } else
        {
            location -= 1;
        }
        pane.setDividerLocation(location);
    }

//    /**
//     * 向地图中安全地加入元素
//     * @param mapPane
//     * @param ep
//     */
//    private static void MapPaneAddLayerSafely(JMapPane mapPane, MapLayer newLayer)
//    {
//        MapContext map = mapPane.getMapContext();
//        if (map == null)
//        {
//            map = new MapContext();
//            map.addLayer(newLayer);
//        } else
//        {
//            for (MapLayer layer : map.getLayers())
//            {
//                if (layer.getTitle().equals(newLayer.getTitle()))
//                {
//                    map.removeLayer(layer);
//                }
//            }
//
//            if (newLayer.getTitle().equals(Field.className) && map.getLayerCount() > 0)
//            {
//                map.addLayer(0, newLayer);
//            } else
//            {
//                map.addLayer(newLayer);
//            }
//        }
//
//        mapPane.setMapContext(map);
//    }
    /**
     * 向地图中添加新的图层
     * @param newLayer
     * @param map
     * @return
     */
    private static MapContext MapAddNewLayerSafely(MapContext map, MapLayer newLayer)
    {
        if (map == null)//地图不存在时,直接新建并加入新的图层
        {
            map = new DefaultMapContext();
            map.addLayer(newLayer);
        } else//地图存在时
        {
            for (MapLayer layer : map.getLayers())
            {
                if (layer.getTitle().equals(newLayer.getTitle()))//相应图层已存在，替换
                {
                    int index = map.indexOf(layer);
                    map.removeLayer(index);
                    map.addLayer(index, newLayer);
                    return (map);
                }
            }
            //相应图层不存在时
            if (newLayer.getTitle().equals(Field.className)||newLayer.getTitle().equals(StaticLib.PolygonRegionLayerName))//栅格图层放在最底层
            {
                map.addLayer(0, newLayer);
            } else
            {
                map.addLayer(newLayer);
            }
        }
        return (map);
    }

    /**
     * 移除地图中的某个图层
     * @param mapPane 地图控件
     * @param title 图层名称
     * @return 余下的图层数
     */
    public static int RemoveLayer(JMapPane mapPane, String title, EnvironmentParameter ep) throws Exception
    {
        MapContext map = mapPane.getMapContext();
        if (map == null)
        {
            return 0;
        }
        for (MapLayer layer : map.getLayers())
        {
            if (layer.getTitle().equals(title))
            {
                map.removeLayer(layer);
            }
        }

        if (map.getLayerCount() == 0)
        {
            RemoveAllLayers(mapPane, ep);
            return 0;
        }

        if (title.equals(Field.className))
        {
            ep.field = null;
        } else if (title.equals(Roads.className))
        {
            ep.road = null;
        } else if (title.equals(TrajectoryPool.className))
        {

            ep.trajectoryPool = null;

        } else
        {
            throw (new Exception("Layer " + title + " not defined!"));
        }

        mapPane.setMapContext(map);

        return (map.getLayerCount());

    }

    /**
     * 移除地图中的所有图层
     * @param mapPane
     * @param ep
     */
    public static void RemoveAllLayers(JMapPane mapPane, EnvironmentParameter ep)
    {
        mapPane.setMapContext(null);
        ep.field = null;
        ep.road = null;
        ep.trajectoryPool = null;
        ep.generalCRS = null;

    }

    /**
     *判断地图中是否含有某个图层
     * @param layerTitle 地图名称
     * @return
     */
    public static boolean IsMapContains(String layerTitle, MapContext map)
    {
        if (map == null)
        {
            return false;
        }
        for (MapLayer layer : map.getLayers())
        {
            if (layer.getTitle().equals(layerTitle))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 重新设置线图层显示方式
     * @param mapPane
     * @param layerTitle
     * @param filePath
     * @param isUseOriginalStyle
     * @param lineColor
     * @param lineWidth
     * @return
     * @throws Exception
     */
    public static String ResetPolylineLayerStyle(JMapPane mapPane, String layerTitle, String filePath, boolean isUseOriginalStyle, Color lineColor, double lineWidth) throws Exception
    {
        Style style = null;
        if (isUseOriginalStyle)
        {
            style = GetSourceStyle(filePath);
        } else
        {
            StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
            FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);
            Stroke stroke = styleFactory.createStroke(filterFactory.literal(lineColor), filterFactory.literal(lineWidth));
            LineSymbolizer sym = styleFactory.createLineSymbolizer(stroke, null);
            Rule rule = styleFactory.createRule();
            rule.symbolizers().add(sym);
            FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]
                    {
                        rule
                    });
            style = styleFactory.createStyle();
            style.featureTypeStyles().add(fts);
        }
        if (mapPane.getMapContext() == null)
        {
            throw (new Exception("Map missing, set style falled"));
        }
        MapLayer[] layers = mapPane.getMapContext().getLayers();
        MapLayer layer = null;
        if (style == null)
        {
            throw (new Exception("Layer style can't get or generate, set style failed"));
        }
        for (MapLayer l : layers)
        {
            if (l.getTitle().equals(layerTitle))
            {
                layer = l;
                break;
            }
        }
        if (layer == null)
        {
            throw (new Exception("layer " + layerTitle + " missing, set style failed"));
        }
        layer.setStyle(style);
        mapPane.repaint();

        return ("Layer style reset finished.");
    }


    /**
     * 重新设置多边形图层显示方式
     * @param mapPane
     * @param layerTitle
     * @param filePath
     * @param isUseOriginalStyle
     * @param lineColor
     * @param lineWidth
     * @param fillColor
     * @return
     * @throws Exception
     */
    public static String ResetPolygonLayerStyle(JMapPane mapPane, String layerTitle, String filePath, boolean isUseOriginalStyle, Color lineColor, double lineWidth, Color fillColor) throws Exception
    {
        Style style = null;
        if (isUseOriginalStyle)
        {
            style = GetSourceStyle(filePath);
        } else
        {
            StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
            FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);
            Stroke stroke = styleFactory.createStroke(filterFactory.literal(lineColor), filterFactory.literal(lineWidth), filterFactory.literal(1.0));

            Fill fill = styleFactory.createFill(filterFactory.literal(fillColor),filterFactory.literal(0.5));



//            PolygonSymbolizer  sym = styleFactory.createPolygonSymbolizer(stroke,fill, null);
            PolygonSymbolizer  sym = styleFactory.createPolygonSymbolizer(stroke,fill, null);
            Rule rule = styleFactory.createRule();
            rule.symbolizers().add(sym);
            FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]
                    {
                        rule
                    });
            style = styleFactory.createStyle();
            style.featureTypeStyles().add(fts);
        }
        if (mapPane.getMapContext() == null)
        {
            throw (new Exception("Map missing, set style falled"));
        }
        MapLayer[] layers = mapPane.getMapContext().getLayers();
        MapLayer layer = null;
        if (style == null)
        {
            throw (new Exception("Layer style can't get or generate, set style failed"));
        }
        for (MapLayer l : layers)
        {
            if (l.getTitle().equals(layerTitle))
            {
                layer = l;
                break;
            }
        }
        if (layer == null)
        {
            throw (new Exception("layer " + layerTitle + " missing, set style failed"));
        }
        layer.setStyle(style);
        mapPane.repaint();

        return ("Layer style reset finished.");
    }


    /**
     * 更新位置信息
     * @param mapPane
     * @param component
     * @param x 屏幕位置X
     * @param y 屏幕位置Y
     */
    public static void RefershLocationTip(JMapPane mapPane, JLabel component, double x, double y)
    {
        String message = "Location";
        if (mapPane.getMapContext() != null)
        {
            AffineTransform transform = mapPane.getScreenToWorldTransform();
            double locations[] = new double[4];
            locations[0] = x;
            locations[1] = y;
            transform.transform(locations, 0, locations, 2, 1);

            message = "x:" + String.valueOf(locations[2]);
            message += " y:" + String.valueOf(locations[3]);
        }
        component.setText(message);
    }

    public static String MapPane_SelectFeatures(MapMouseEvent evt, JMapPane mapPane, EnvironmentParameter ep) throws IOException
    {
        String message = "";
        MapContext map = mapPane.getMapContext();
        if (map == null || map.getLayerCount() == 0)
        {
            message = "Current map is empty, no feature selected.";
            return message;
        }

        boolean isFeatureSelected = false;
        DirectPosition2D mapPosition = evt.getMapPosition();

        message += mapPosition.toString();

        String geometryAttributeName = "";
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);

        Point screenPoint = evt.getPoint();
        Rectangle screenRect = new Rectangle(screenPoint.x - 2, screenPoint.y - 2, 5, 5);
        AffineTransform screenToWorld = mapPane.getScreenToWorldTransform();
        Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
        ReferencedEnvelope bbox = new ReferencedEnvelope(worldRect, map.getCoordinateReferenceSystem());

        for (MapLayer layer : map.getLayers())
        {
            if (layer.getTitle().equals(Roads.className))//道路类型数据
            {
                SimpleFeatureSource featureSource = (SimpleFeatureSource) layer.getFeatureSource();
                GeometryDescriptor geomDesc = featureSource.getSchema().getGeometryDescriptor();
                geometryAttributeName = geomDesc.getLocalName();
                Filter filter = ff.intersects(ff.property(geometryAttributeName), ff.literal(bbox));

                SimpleFeatureCollection selectedFeatures = featureSource.getFeatures(filter);
                SimpleFeatureIterator iter = selectedFeatures.features();
                Set<FeatureId> IDs = new HashSet<FeatureId>();

                message += "\nLayer Roads:  ";
                try
                {
                    while (iter.hasNext())
                    {
                        SimpleFeature feature = iter.next();
                        IDs.add(feature.getIdentifier());
                        message += feature.getIdentifier() + "  ";
                    }

                } finally
                {
                    iter.close();
                }

                if (IDs.isEmpty())
                {
                    message += "no feature selected.";
                } else
                {
                    isFeatureSelected = true;
                    ResetSelectedLineFeaturesStyle(layer, IDs, ep.road_lineColor, ep.highLightColor, ep.road_lineWidth);
                }
            } else if (layer.getTitle().equals(TrajectoryPool.className))//轨迹类型数据
            {
                SimpleFeatureSource featureSource = (SimpleFeatureSource) layer.getFeatureSource();
                GeometryDescriptor geomDesc = featureSource.getSchema().getGeometryDescriptor();
                geometryAttributeName = geomDesc.getLocalName();
                Filter filter = ff.intersects(ff.property(geometryAttributeName), ff.literal(bbox));

                SimpleFeatureCollection selectedFeatures = featureSource.getFeatures(filter);
                SimpleFeatureIterator iter = selectedFeatures.features();
                Set<FeatureId> IDs = new HashSet<FeatureId>();

                message += "\nLayer TrajectoryPool:  ";
                try
                {
                    while (iter.hasNext())
                    {
                        SimpleFeature feature = iter.next();
                        IDs.add(feature.getIdentifier());
                        message += feature.getIdentifier() + "  ";
                    }

                } finally
                {
                    iter.close();
                }

                if (IDs.isEmpty())
                {
                    message += "no feature selected.";
                } else
                {
                    isFeatureSelected = true;
                    ResetSelectedLineFeaturesStyle(layer, IDs, ep.trajectoryPool_lineColor, ep.highLightColor, ep.trajectoryPool_lineWidth);
                }

            } else if (layer.getTitle().equals(Field.className))//栅格类型数据
            {

                if (ep.field.GridCoverage != null)
                {

                    message += "\nLayer Field:  ";
                    if (ep.field.GridCoverage.getEnvelope2D().contains(mapPosition))
                    {
                        int bandNum = ep.field.GridCoverage.getNumSampleDimensions();
                        double[] values = new double[bandNum];
                        ep.field.GridCoverage.evaluate((DirectPosition) mapPosition, values);
                        for (int i = 0; i < bandNum; i++)
                        {
                            message += "band" + (i + 1) + ": " + values[i] + " ";
                        }
                    } else
                    {
                        message += "map position out of field envelop.";
                    }
                }
            }

        }

        if (isFeatureSelected)
        {
            mapPane.repaint();
        }
        return message;

    }

    public static Trajectory MapPane_SelectSingleTrajectory(MapMouseEvent evt, JMapPane mapPane, EnvironmentParameter ep) throws IOException
    {
        MapContext map = mapPane.getMapContext();
        if (map == null || map.getLayerCount() == 0)
        {
            return null;
        }

        boolean isFeatureSelected = false;
        DirectPosition2D mapPosition = evt.getMapPosition();

        String geometryAttributeName = "";
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);

        Point screenPoint = evt.getPoint();
        Rectangle screenRect = new Rectangle(screenPoint.x - 2, screenPoint.y - 2, 5, 5);
        AffineTransform screenToWorld = mapPane.getScreenToWorldTransform();
        Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
        ReferencedEnvelope bbox = new ReferencedEnvelope(worldRect, map.getCoordinateReferenceSystem());

        Trajectory result = null;
        for (MapLayer layer : map.getLayers())
        {
            if (layer.getTitle().equals(TrajectoryPool.className))//轨迹类型数据
            {
                SimpleFeatureSource featureSource = (SimpleFeatureSource) layer.getFeatureSource();
                GeometryDescriptor geomDesc = featureSource.getSchema().getGeometryDescriptor();
                geometryAttributeName = geomDesc.getLocalName();
                Filter filter = ff.intersects(ff.property(geometryAttributeName), ff.literal(bbox));

                SimpleFeatureCollection selectedFeatures = featureSource.getFeatures(filter);
                SimpleFeatureIterator iter = selectedFeatures.features();
                Set<FeatureId> IDs = new HashSet<FeatureId>();


                try
                {
                    if (iter.hasNext())//仅仅获取选择到的第一个路径数据
                    {
                        SimpleFeature feature = iter.next();
                        IDs.add(feature.getIdentifier());
                        Geometry geometry = (Geometry) feature.getDefaultGeometry();
                        if (geometry.getClass() == LineString.class)//单线
                        {
                            ArrayList<Node> nodeList = new ArrayList<Node>();
                            for (int i = 0; i < ((LineString) geometry).getNumPoints(); i++)
                            {
                                nodeList.add(new Node(((LineString) geometry).getPointN(i), null));//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!在此处添加获取时间的函数
                            }
                            result = new Trajectory(nodeList);
                        } else if (geometry.getClass() == MultiLineString.class)//多线
                        {

                            ArrayList<Node> nodeList = new ArrayList<Node>();
                            LineString lineString = (LineString) geometry.getGeometryN(0);
                            for (int j = 0; j < lineString.getNumPoints(); j++)
                            {
                                nodeList.add(new Node(lineString.getPointN(j), null));//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!在此处添加获取时间的函数
                            }
                            Trajectory newTrajectory = new Trajectory(nodeList);
                            result = newTrajectory;
                        }
                    }

                } finally
                {
                    iter.close();
                }

                if (!IDs.isEmpty())
                {
                    isFeatureSelected = true;
                    ResetSelectedLineFeaturesStyle(layer, IDs, ep.trajectoryPool_lineColor, ep.highLightColor, ep.trajectoryPool_lineWidth);
                }

            }

        }
        if (isFeatureSelected)
        {
            mapPane.repaint();
        }
        return result;

    }

    /**
     * 以高亮方式显示图层中被选中的对象
     * @param layer
     * @param IDs
     */
    static private void ResetSelectedLineFeaturesStyle(MapLayer layer, Set<FeatureId> IDs, Color defaultLineColor, Color highLightLineColor, double lineWidth)
    {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);

        Stroke highLightStroke = styleFactory.createStroke(filterFactory.literal(highLightLineColor), filterFactory.literal(lineWidth));
        LineSymbolizer highLightSym = styleFactory.createLineSymbolizer(highLightStroke, null);
        Rule highLightRule = styleFactory.createRule();
        highLightRule.symbolizers().add(highLightSym);
        highLightRule.setFilter(filterFactory.id(IDs));

        Stroke defaultStroke = styleFactory.createStroke(filterFactory.literal(defaultLineColor), filterFactory.literal(lineWidth));
        LineSymbolizer defaultSym = styleFactory.createLineSymbolizer(defaultStroke, null);
        Rule defaultRule = styleFactory.createRule();
        defaultRule.symbolizers().add(defaultSym);
        defaultRule.setElseFilter(true);

        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
        fts.rules().add(highLightRule);
        fts.rules().add(defaultRule);

        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        layer.setStyle(style);
    }
}
