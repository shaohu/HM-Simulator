/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Import;

import Static.StaticLib;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import levyflight.Data.Node;
import levyflight.Data.Trajectory;
import levyflight.Data.TrajectoryPool;
import levyflight.EnvironmentParameter;
import levyflight.Statistic.EigenvectorCal;
import levyflight.Statistic.EllipseGen;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Tiger Shao
 */
public class MethodLib
{

    /**
     * 判断一个shp文件中包含的要素类型是否为点类型
     * @param fileToSave shpfile
     * @return Whether this shapefile is linetype
     */
    public static boolean IsSHPLineType(File file)
    {
        try
        {
            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            FeatureSource featureSource = store.getFeatureSource();
            Class<?> type = featureSource.getSchema().getGeometryDescriptor().getType().getBinding();
            if (LineString.class.isAssignableFrom(type) || MultiLineString.class.isAssignableFrom(type))
            {
                return true;
            } else
            {
                return false;
            }
        } catch (Exception e)
        {
            System.out.println(e);
            return false;
        }
    }

    /**
     * 判断一个shp文件中包含的要素类型是否为线类型
     * @param fileToSave shpfile
     * @return Whether this shapefile is linetype
     */
    public static boolean IsSHPPointType(File file)
    {
        try
        {
            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            FeatureSource featureSource = store.getFeatureSource();
            Class<?> type = featureSource.getSchema().getGeometryDescriptor().getType().getBinding();
            if (Point.class.isAssignableFrom(type) || MultiPoint.class.isAssignableFrom(type))
            {
                return true;
            } else
            {
                return false;
            }
        } catch (Exception e)
        {
            System.out.println(e);
            return false;
        }
    }

    public static boolean IsSHPPolygonType(File file)
    {
        try
        {
            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            FeatureSource featureSource = store.getFeatureSource();
            Class<?> type = featureSource.getSchema().getGeometryDescriptor().getType().getBinding();
            if (Polygon.class.isAssignableFrom(type) || MultiPolygon.class.isAssignableFrom(type))
            {
                return true;
            } else
            {
                return false;
            }
        }catch(Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * 将 trajectorypool 中的路径数据转换为geotools中定义的SimpleFeatureSource结构，以便在控件中绘制
     * @param tp
     * @return
     */
    public static File TrajectoryPoolToSHPFile(TrajectoryPool tp, File fileToSave) throws Exception
    {
        if (fileToSave == null)
        {
            return null;
        }
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(tp.CoordinateReference);

        // add attributes in order
        builder.add("Location", LineString.class);
        builder.length(19).add(StaticLib.BeginTimeFieldString, String.class);

        // build the type
        final SimpleFeatureType featureType = builder.buildFeatureType();



        SimpleFeatureCollection collection = FeatureCollections.newCollection();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);


        for (Trajectory trajectory : tp.TrajectorySet)
        {
            if (trajectory.NodeList.get(0).Time != null)//在有时间信息的条件下，加入时间维（高度）
            {
                Coordinate[] coordinates = new Coordinate[trajectory.NodeList.size()];
                long beginTime = trajectory.NodeList.get(0).Time.getTimeInMillis();

                for (int i = 0; i < trajectory.NodeList.size(); i++)
                {
                    int timeSpan = (int) ((trajectory.NodeList.get(i).Time.getTimeInMillis() - beginTime) / 1000);
                    coordinates[i] = new Coordinate(trajectory.NodeList.get(i).getX(), trajectory.NodeList.get(i).getY(), timeSpan);
                }
                LineString shpTra = geometryFactory.createLineString(coordinates);
                String time = StaticLib.LevyFlightDateFormat.format(trajectory.NodeList.get(0).Time.getTime());

                featureBuilder.add(shpTra);
                featureBuilder.add(time);
                SimpleFeature feature = featureBuilder.buildFeature(null);
                collection.add(feature);

            } else//在没有时间信息的条件下，只有二维信息
            {
                Coordinate[] coordinates = new Coordinate[trajectory.NodeList.size()];

                for (int i = 0; i < trajectory.NodeList.size(); i++)
                {
                    coordinates[i] = new Coordinate(trajectory.NodeList.get(i).getX(), trajectory.NodeList.get(i).getY());
                }
                LineString shpTra = geometryFactory.createLineString(coordinates);
                String time = StaticLib.NoTimeString;

                featureBuilder.add(shpTra);
                featureBuilder.add(time);
                SimpleFeature feature = featureBuilder.buildFeature(null);
                collection.add(feature);
            }
        }
        SimpleFeatureSource source = DataUtilities.source(collection);


//        JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
//        chooser.setDialogTitle("Save generated trajectorypool as shapefile");
//        if (StaticLib.InitialFileDirectory != null && StaticLib.InitialFileDirectory.exists())
//        {
//            chooser.setSaveFile(StaticLib.InitialFileDirectory);
//        }
//        int returnVal = chooser.showSaveDialog(null);
//        if (returnVal != JFileDataStoreChooser.APPROVE_OPTION)
//        {
//            return null;
//        }
//        File fileToSave = chooser.getSelectedFile();
        StaticLib.InitialFileDirectory = fileToSave;
//        String path = fileToSave.getAbsolutePath();
//        String extention = path.substring(path.length() - 4);
//        if (!extention.equals(".shp"))
//        {
//            path += ".shp";
//            fileToSave = new File(path);
//        }
        if (fileToSave == null)
        {
            return null;
        }

        DataStoreFactorySpi factory = new ShapefileDataStoreFactory();
        Map<String, Serializable> creat = new HashMap<String, Serializable>();
        creat.put("url", fileToSave.toURI().toURL());
        creat.put("creat spatial index", Boolean.TRUE);
        DataStore newDataStore = factory.createDataStore(creat);
        newDataStore.createSchema(featureType);
        Transaction transaction = new DefaultTransaction("WriteNew");
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = newDataStore.getFeatureWriterAppend(featureType.getTypeName(), transaction);
        SimpleFeatureIterator iterator = collection.features();
        try
        {
            while (iterator.hasNext())
            {
                SimpleFeature feature = iterator.next();
                SimpleFeature copy = writer.next();
                copy.setAttributes(feature.getAttributes());

                Geometry geometry = (Geometry) feature.getDefaultGeometry();

                copy.setDefaultGeometry(geometry);
                writer.write();
            }
            transaction.commit();
        } catch (Exception problem)
        {
            JOptionPane.showMessageDialog(null, "Export to shapefile failed" + problem.getMessage());
        } finally
        {
            writer.close();
            iterator.close();
            transaction.close();
        }

        return fileToSave;
    }

    /**
     * 将 trajectorypool 中的路径数据转换为geotools中定义的SimpleFeatureSource结构，以便在控件中绘制
     * @param tp
     * @return
     */
    public static File SaveExtractedRevistiedPointAsSHPFile(ArrayList<Double> PonitList, CoordinateReferenceSystem crs) throws Exception
    {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(crs);

        // add attributes in order
        builder.add("Location", Point.class);
        builder.add(StaticLib.RevistingProbabilityFieldString, Double.class);
        // build the type
        final SimpleFeatureType featureType = builder.buildFeatureType();



        SimpleFeatureCollection collection = FeatureCollections.newCollection();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

        for (int i = 0; i < PonitList.size(); i += 3)
        {
            Coordinate coordinate = new Coordinate(PonitList.get(i), PonitList.get(i + 1));
            Point newPoint = geometryFactory.createPoint(coordinate);
            double probability = PonitList.get(i + 2);
            featureBuilder.add(newPoint);
            featureBuilder.add(probability);
            SimpleFeature feature = featureBuilder.buildFeature(null);
            collection.add(feature);
        }

        SimpleFeatureSource source = DataUtilities.source(collection);

        JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
        chooser.setDialogTitle("Save extracted revisited pointes as shapefile");

        if (StaticLib.InitialFileDirectory != null && StaticLib.InitialFileDirectory.exists())
        {
            chooser.setSaveFile(StaticLib.InitialFileDirectory);
        }

        int returnVal = chooser.showSaveDialog(null);
        if (returnVal != JFileDataStoreChooser.APPROVE_OPTION)
        {
            return null;
        }
        File file = chooser.getSelectedFile();
        StaticLib.InitialFileDirectory = file;
        String path = file.getAbsolutePath();
        String extention = path.substring(path.length() - 4);
        if (!extention.equals(".shp"))
        {
            path += ".shp";
            file = new File(path);
        }

        DataStoreFactorySpi factory = new ShapefileDataStoreFactory();
        Map<String, Serializable> creat = new HashMap<String, Serializable>();
        creat.put("url", file.toURI().toURL());
        creat.put("creat spatial index", Boolean.TRUE);
        DataStore newDataStore = factory.createDataStore(creat);
        newDataStore.createSchema(featureType);
        Transaction transaction = new DefaultTransaction("WriteNew");
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = newDataStore.getFeatureWriterAppend(featureType.getTypeName(), transaction);
        SimpleFeatureIterator iterator = collection.features();
        try
        {
            while (iterator.hasNext())
            {
                SimpleFeature feature = iterator.next();
                SimpleFeature copy = writer.next();
                copy.setAttributes(feature.getAttributes());

                Geometry geometry = (Geometry) feature.getDefaultGeometry();

                copy.setDefaultGeometry(geometry);
                writer.write();
            }
            transaction.commit();
        } catch (Exception problem)
        {
            JOptionPane.showMessageDialog(null, "Export to shapefile failed" + problem.getMessage());
        } finally
        {
            writer.close();
            iterator.close();
            transaction.close();
        }

        return file;
    }

    /**
     * 对轨迹图层中的各个轨迹进行转轴变换，将生成的结果椭圆存储至文件中
     * @param ep 环境变量
     * @return
     * @throws Exception
     */
    public static File rotationTransfrom(EnvironmentParameter ep) throws Exception
    {

        if (ep.generalCRS == null || ep.trajectoryPool == null)
        {
            return null;
        }

        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(ep.generalCRS);

        // add attributes in order
        builder.add("Location", Polygon.class);
        builder.add(StaticLib.CenterX, Double.class);
        builder.add(StaticLib.CenterY, Double.class);
        builder.add(StaticLib.MajorSemiAxis, Double.class);
        builder.add(StaticLib.MinorSemiAxis, Double.class);
        builder.add(StaticLib.Rotation, Double.class);
        // build the type
        final SimpleFeatureType featureType = builder.buildFeatureType();



        SimpleFeatureCollection collection = FeatureCollections.newCollection();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);


        for (Trajectory trajectory : ep.trajectoryPool.TrajectorySet)
        {
            EigenvectorCal eigenvectorCal = new EigenvectorCal(trajectory);
            Node centerNode = trajectory.GetCentrePoint();
            double centerX = centerNode.getX();
            double centerY = centerNode.getY();
            double majorSemiAxis = eigenvectorCal.getMajorSemiAxis();
            double minorSemiAxis = eigenvectorCal.getMinorSemiAxis();
            double rotationAngle = eigenvectorCal.getRotationAngle();


            Coordinate[] coordinates = EllipseGen.generateEllipse(majorSemiAxis, minorSemiAxis, centerX, centerY, rotationAngle, StaticLib.encryptionNum);
            LinearRing ring = geometryFactory.createLinearRing(coordinates);

            Polygon ellipse = geometryFactory.createPolygon(ring, null);

            rotationAngle=90.0-rotationAngle/Math.PI*180.0;
            if(rotationAngle<0)
            {
                rotationAngle+=180;
            }

            featureBuilder.add(ellipse);
            featureBuilder.add(centerX);
            featureBuilder.add(centerY);
            featureBuilder.add(majorSemiAxis);
            featureBuilder.add(minorSemiAxis);
            featureBuilder.add(rotationAngle);
            SimpleFeature feature = featureBuilder.buildFeature(null);
            collection.add(feature);
        }

        SimpleFeatureSource source = DataUtilities.source(collection);

        JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
        chooser.setDialogTitle("Save rotation transfromed ellipses as shapefile");

        if (StaticLib.InitialFileDirectory != null && StaticLib.InitialFileDirectory.exists())
        {
            chooser.setSaveFile(StaticLib.InitialFileDirectory);
        }

        int returnVal = chooser.showSaveDialog(null);
        if (returnVal != JFileDataStoreChooser.APPROVE_OPTION)
        {
            return null;
        }
        File file = chooser.getSelectedFile();
        StaticLib.InitialFileDirectory = file;
        String path = file.getAbsolutePath();
        String extention = path.substring(path.length() - 4);
        if (!extention.equals(".shp"))
        {
            path += ".shp";
            file = new File(path);
        }

        DataStoreFactorySpi factory = new ShapefileDataStoreFactory();
        Map<String, Serializable> creat = new HashMap<String, Serializable>();
        creat.put("url", file.toURI().toURL());
        creat.put("creat spatial index", Boolean.TRUE);
        DataStore newDataStore = factory.createDataStore(creat);
        newDataStore.createSchema(featureType);
        Transaction transaction = new DefaultTransaction("WriteNew");
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = newDataStore.getFeatureWriterAppend(featureType.getTypeName(), transaction);
        SimpleFeatureIterator iterator = collection.features();
        try
        {
            while (iterator.hasNext())
            {
                SimpleFeature feature = iterator.next();
                SimpleFeature copy = writer.next();
                copy.setAttributes(feature.getAttributes());

                Geometry geometry = (Geometry) feature.getDefaultGeometry();

                copy.setDefaultGeometry(geometry);
                writer.write();
            }
            transaction.commit();
        } catch (Exception problem)
        {
            JOptionPane.showMessageDialog(null, "Export to shapefile failed" + problem.getMessage());
        } finally
        {
            writer.close();
            iterator.close();
            transaction.close();
        }

        return file;

    }

    /**
     *  从轨迹图层中提取离散的一个个结点并进行进一步的分析
     * @param ep 环境变量
     * @return
     */
    public static File extractNodesFromTrajectoryLayer(EnvironmentParameter ep) throws Exception
    {
        if (ep.generalCRS == null || ep.trajectoryPool == null)
        {
            return null;
        }

        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(ep.generalCRS);

        // add attributes in order
        builder.add("Location", Point.class);
        builder.add(StaticLib.TrajectoryID, Integer.class);
        // build the type
        final SimpleFeatureType featureType = builder.buildFeatureType();



        SimpleFeatureCollection collection = FeatureCollections.newCollection();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);


        for (Trajectory trajectory : ep.trajectoryPool.TrajectorySet)
        {
            for (Node node : trajectory.NodeList)
            {
                Coordinate coordinate = new Coordinate(node.getX(), node.getY());
                Point point = geometryFactory.createPoint(coordinate);
                featureBuilder.add(point);
                featureBuilder.add(trajectory.ID);
                SimpleFeature feature = featureBuilder.buildFeature(null);
                collection.add(feature);
            }
        }

        SimpleFeatureSource source = DataUtilities.source(collection);

        JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
        chooser.setDialogTitle("Save extracted pointes as shapefile");

        if (StaticLib.InitialFileDirectory != null && StaticLib.InitialFileDirectory.exists())
        {
            chooser.setSaveFile(StaticLib.InitialFileDirectory);
        }

        int returnVal = chooser.showSaveDialog(null);
        if (returnVal != JFileDataStoreChooser.APPROVE_OPTION)
        {
            return null;
        }
        File file = chooser.getSelectedFile();
        StaticLib.InitialFileDirectory = file;
        String path = file.getAbsolutePath();
        String extention = path.substring(path.length() - 4);
        if (!extention.equals(".shp"))
        {
            path += ".shp";
            file = new File(path);
        }

        DataStoreFactorySpi factory = new ShapefileDataStoreFactory();
        Map<String, Serializable> creat = new HashMap<String, Serializable>();
        creat.put("url", file.toURI().toURL());
        creat.put("creat spatial index", Boolean.TRUE);
        DataStore newDataStore = factory.createDataStore(creat);
        newDataStore.createSchema(featureType);
        Transaction transaction = new DefaultTransaction("WriteNew");
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = newDataStore.getFeatureWriterAppend(featureType.getTypeName(), transaction);
        SimpleFeatureIterator iterator = collection.features();
        try
        {
            while (iterator.hasNext())
            {
                SimpleFeature feature = iterator.next();
                SimpleFeature copy = writer.next();
                copy.setAttributes(feature.getAttributes());

                Geometry geometry = (Geometry) feature.getDefaultGeometry();

                copy.setDefaultGeometry(geometry);
                writer.write();
            }
            transaction.commit();
        } catch (Exception problem)
        {
            JOptionPane.showMessageDialog(null, "Export to shapefile failed" + problem.getMessage());
        } finally
        {
            writer.close();
            iterator.close();
            transaction.close();
        }

        return file;
    }
}
