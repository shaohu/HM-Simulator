/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TraGen;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import levyflight.Data.Node;
import org.geotools.geometry.jts.JTSFactoryFinder;

/**
 * 人口分布范围的多边形约束
 * @author Tiger Shao
 */
public class TC_PolygonRegion extends TraConstraint
{

    private Geometry polygonUnion;

    public TC_PolygonRegion(Geometry polygonUnion)
    {
        this.polygonUnion=polygonUnion;
    }

    @Override
    public boolean isTrajectoryMeetConstraint(Node node)
    {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        Coordinate coordinate=new Coordinate(node.getX(),node.getY());
        Point point=geometryFactory.createPoint(coordinate);
        if(this.polygonUnion instanceof Polygon)
        {
            Polygon p=(Polygon)this.polygonUnion;
            return p.contains(point);
        }else if(this.polygonUnion instanceof MultiPolygon)
        {
            MultiPolygon p=(MultiPolygon)this.polygonUnion;
            return p.contains(point);
        }else
        {
            return false;
        }
    }

}
