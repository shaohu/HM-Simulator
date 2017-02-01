/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Data;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.util.Calendar;
import org.geotools.geometry.jts.JTSFactoryFinder;

/**
 *记录飞行轨迹每一个节点的类
 * @author Tiger Shao
 */
public class Node
{

    /**
     * Geotools中定义的点类，在有投影系统时使用
     */
    private Point Point;
    /**
     * 到访时刻
     */
    public Calendar Time;

    /**
     * 构造函数
     * @param x 横坐标
     * @param y 纵坐标
     * @param time 到访时刻
     */
    public Node(double x, double y, Calendar time)
    {
        Coordinate coordinate = new Coordinate(x, y);
        GeometryFactory geometryFactory=JTSFactoryFinder.getGeometryFactory(null);
        Point p = geometryFactory.createPoint(coordinate);
        this.Time = time;
        this.Point = p;

    }

    /**
     * 构造函数
     * @param point 位置点
     * @param time 到访时刻
     */
    public Node(Point point, Calendar time)
    {
        this.Point =point;
        this.Time = time;
    }

//    /**
//     * 获取Node的地理参照系统码
//     * @return
//     */
//    public int getSRID()
//    {
//        return (this.Point.getSRID());
//    }

    /**
     * 复制一个新的节点类
     * @return 返回节点的副本
     */
    public Node Clone()
    {
        return (new Node(this.Point.getX(), this.Point.getY(), this.Time));
    }

    /**
     * 获取点的横坐标
     * @return
     */
    public double getX()
    {
        return (this.Point.getX());
    }

    /**
     * 获取点的纵坐标
     * @return
     */
    public double getY()
    {
        return (this.Point.getY());
    }

    /**
     * 获取当前节点到目标节点的距离
     * @param node
     * @return
     */
    public double DistanceTo(Node node)
    {
        return (this.Point.distance(node.Point));
    }

    /**
     * 获取当前节点到任意地理对象的(最短)距离
     * @param geometry
     * @return
     */
    public double DistanceTo(Geometry geometry)
    {
        return(this.Point.distance(geometry));
    }

    @Override
    public String toString()
    {
        String result = "";
        if (this.Time == null)
        {
            result = "LevyFlight Node [X:" + String.valueOf(this.getX()) + " Y:" + String.valueOf(this.getY()) + " Time not defined]";
        } else
        {
            result = "LevyFlight Node [X:" + String.valueOf(this.getX()) + " Y:" + String.valueOf(this.getY()) + " Time:" + this.Time.toString() + "]";
        }
        return result;
    }
}
