/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Import;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.io.File;
import java.util.ArrayList;
import levyflight.Data.MBR;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.hsqldb.lib.Collection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *用于读取shp格式多边形的类
 * @author wu
 */
public class SHPPolygonImporter
{

    /**
     * 多边形对象集合
     */
    private FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = null;
    /**
     * 多边形合并的结果
     */
    private Geometry polygonUnion;
    /**
     * 文件的绝对路径
     */
    private String absolutePath;

    public SHPPolygonImporter(File sourceFile)
    {
        ShapefileDataStore shpDataStore = null;
        absolutePath = sourceFile.getAbsolutePath();
        try
        {
            shpDataStore = new ShapefileDataStore(sourceFile.toURI().toURL());
            String typeName = shpDataStore.getTypeNames()[0];

            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;
            featureSource = (FeatureSource<SimpleFeatureType, SimpleFeature>) shpDataStore.getFeatureSource(typeName);

            featureCollection = featureSource.getFeatures();
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        polygonUnion = null;
    }

    /**
     * 返回图层要素集
     * @return
     */
    public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatureCollection()
    {
        return featureCollection;
    }

    /**
     * 返回shp文件的坐标系统
     * @return
     */
    public CoordinateReferenceSystem getCoordinateReferenceSystem()
    {
        return this.featureCollection.getSchema().getCoordinateReferenceSystem();
    }

    /**
     * 获取所有多边形合并后的结果
     * @return
     */
    public Geometry getPolygonUnion()
    {
        if (this.polygonUnion == null)
        {
            FeatureIterator<SimpleFeature> featureIterator = this.featureCollection.features();
            while (featureIterator.hasNext())
            {
                Geometry geo = (Geometry) featureIterator.next().getDefaultGeometry();
                if (this.polygonUnion == null)
                {
                    this.polygonUnion = geo;
                } else
                {
                    this.polygonUnion = this.polygonUnion.union(geo);
                }
            }
        }
        return this.polygonUnion;
    }

    /**
     * 获取文件的绝对位置
     * @return
     */
    public String getAbsolutePath()
    {
        return this.absolutePath;
    }

    /**
     * 获取多边形的边界盒
     * @return
     */
    public MBR getMBR()
    {
        MBR mbr = new MBR();
        Geometry polygon = this.getPolygonUnion();
        Polygon envelop = (Polygon) polygon.getEnvelope();
        LineString lineString=envelop.getExteriorRing();
        for (int i = 0; i < lineString.getNumPoints(); i++)
        {
            Point point = (Point) lineString.getPointN(i);
            mbr.RefreshBoundary(point.getX(), point.getY());
        }
        return mbr;
    }
}
