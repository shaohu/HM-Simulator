/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Import;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import levyflight.Data.Node;
import levyflight.Data.Trajectory;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * shpfile Trajectory reader class
 * 用于从shpfile中读入轨迹的类
 * @author Tiger Shao
 */
public class SHPTraImporter extends TrajectoryGenerator
{

    /**
     * 地理对象的访问接口
     */
    FeatureIterator<SimpleFeature> FeatureIterator;
    /**
     * 简单的路径集合，用于处理MultiLineString对象
     */
    ArrayDeque<Trajectory> TrajectoryArray;

    /**
     * 返回shp文件属性信息库的描述信息
     * @param FilePath shp文件路径
     * @return 各个属性字段信息
     */
    static public ArrayList<String> GetFeatureInfo(String FilePath)
    {
        try
        {
            FileChannel in = new FileInputStream(FilePath).getChannel();
            DbaseFileReader fileReader = new DbaseFileReader(in, true, Charset.forName("UTF-8"));
            DbaseFileHeader fileHeader = fileReader.getHeader();
            int numFields = fileHeader.getNumFields();
            ArrayList<String> result = new ArrayList<String>();
            result.add(fileReader.toString());
            result.add(fileHeader.toString());
            for (int i = 0; i < numFields; i++)
            {
                result.add(fileHeader.getFieldClass(i).toString());
                result.add(fileHeader.getFieldName(i));
                result.add(String.valueOf(fileHeader.getFieldType(i)));
            }
            return (result);
        } catch (IOException ex)
        {
            Logger.getLogger(SHPTraImporter.class.getName()).log(Level.SEVERE, null, ex);
            return (null);
        }
    }

    /**
     * 构造函数
     * @param shpfilePath *.shp 文件路径
     */
    public SHPTraImporter(String shpfilePath)
    {
        this.AbsoluteFilePath=shpfilePath;
        File file = new File(shpfilePath);
        ShapefileDataStore shpDataStore = null;
        this.TrajectoryArray = new ArrayDeque();
        try
        {
            shpDataStore = new ShapefileDataStore(file.toURI().toURL());
            String typeName = shpDataStore.getTypeNames()[0];

            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;
            featureSource = (FeatureSource<SimpleFeatureType, SimpleFeature>) shpDataStore.getFeatureSource(typeName);

            FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureSource.getFeatures();
            this.FeatureIterator = features.features();
            this.CoordinateReference = features.getSchema().getCoordinateReferenceSystem();
            this.HasNext = this.FeatureIterator.hasNext();

        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Trajectory GetNextTrajectory()
    {
        if (this.HasNext)
        {
            if (this.TrajectoryArray.isEmpty())//对列为空，取出新的对象
            {
                SimpleFeature feature = this.FeatureIterator.next();
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                if (geometry.getClass() == LineString.class)//单线
                {
                    ArrayList<Node> nodeList = new ArrayList<Node>();
                    for (int i = 0; i < ((LineString) geometry).getNumPoints(); i++)
                    {
                        nodeList.add(new Node(((LineString) geometry).getPointN(i), null));//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!在此处添加获取时间的函数
                    }
                    Trajectory result = new Trajectory(nodeList);
                    this.HasNext = this.FeatureIterator.hasNext();
                    return (result);
                } else if (geometry.getClass() == MultiLineString.class)//多线
                {
                    for (int i = 0; i < geometry.getNumGeometries(); i++)
                    {
                        ArrayList<Node> nodeList = new ArrayList<Node>();
                        LineString lineString = (LineString) geometry.getGeometryN(i);
                        for (int j = 0; j < lineString.getNumPoints(); j++)
                        {
                            nodeList.add(new Node(lineString.getPointN(j), null));//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!在此处添加获取时间的函数
                        }
                        Trajectory newTrajectory = new Trajectory(nodeList);
                        this.TrajectoryArray.addLast(newTrajectory);
                    }
                    return (this.GetNextTrajectory());
                } else
                {
                    return (null);
                }
            } else//队列不为空，从队列中取出轨迹对象
            {
                Trajectory result = this.TrajectoryArray.pollFirst();
                if (!this.FeatureIterator.hasNext() && this.TrajectoryArray.isEmpty())
                {
                    this.HasNext = false;
                }
                return (result);
            }
        } else//没有新的要生成的路径类了
        {
            return (null);
        }
    }

    /**
     * 释放资源
     */
    public void Dispose()
    {
        this.FeatureIterator.close();
    }
}
