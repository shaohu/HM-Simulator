/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Import;

import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.util.ArrayList;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 *
 * @author Tiger Shao
 */
public class SHPRoadImporter extends RoadGenerator
{

    /**
     * 地理对象的访问接口
     */
    private FeatureIterator<SimpleFeature> FeatureIterator;

    //public ArrayList<String>info;
    public SHPRoadImporter(File sourcefile)
    {
        super(sourcefile.getAbsolutePath());
        // File file = new File(shpfilePath);
        ShapefileDataStore shpDataStore = null;
        try
        {
            shpDataStore = new ShapefileDataStore(sourcefile.toURI().toURL());
            String typeName = shpDataStore.getTypeNames()[0];

            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;
            featureSource = (FeatureSource<SimpleFeatureType, SimpleFeature>) shpDataStore.getFeatureSource(typeName);

            FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureSource.getFeatures();
            this.FeatureIterator = features.features();
            this.CoordinateReference = features.getSchema().getCoordinateReferenceSystem();
            this.Name = shpDataStore.getInfo().getTitle();
            this.type = featureSource.getSchema().getGeometryDescriptor().getType().getBinding();
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ArrayList<Geometry> GetAllRoads()
    {
        ArrayList<Geometry> roads = new ArrayList<Geometry>();
        while (this.FeatureIterator.hasNext())
        {
            roads.add((Geometry) this.FeatureIterator.next().getDefaultGeometry());
        }
        return (roads);

    }

    /**
     * 释放资源
     */
    public void Dispose()
    {
        this.FeatureIterator.close();
    }
}
