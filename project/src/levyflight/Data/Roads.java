/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Data;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import levyflight.Import.RoadGenerator;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Tiger Shao
 */
public class Roads
{

    /**
     * 道路名称
     */
    public String Name;
    /**
     * 道路集合类
     */
    public ArrayList<Geometry> roadList;
    /**
     * 道路类型
     */
    public Class<?> type;
    /**
     * 坐标参考系统
     */
    public CoordinateReferenceSystem CoordinateReference;
    /**
     * 路径集合的最小外包矩形
     */
    public MBR Boundary;

    /**
     * 类标识的名称
     */
    public static final String className="LevyFlightRoad";

    /**
     * 文件的绝对地址
     */
    public String AbsoluteFilePath;

    /**
     * 构造函数
     * @param coordinateReferenceSystem 道路的坐标参照系统
     */
    public Roads(CoordinateReferenceSystem coordinateReferenceSystem)
    {
        this.Boundary = new MBR();
        this.CoordinateReference = coordinateReferenceSystem;
    }

    /**
     * 通过RoadGenerator中的接口填充Roads数据集
     * @param roadGenerator 道路产生的类
     * @throws Exception 地理参照信息不匹配造成的错误
     */
    public void Fill(RoadGenerator roadGenerator) throws Exception
    {
        this.roadList=roadGenerator.GetAllRoads();
        this.type = roadGenerator.type;
        this.AbsoluteFilePath=roadGenerator.AbsoluteFilePath;
        this.RefreshMBR();
    }

    /**
     * 在数据更改后刷新最小外包矩形信息
     */
    private void RefreshMBR()
    {
        this.Boundary.Initialize();
        for (Geometry road : this.roadList)
        {
            MultiPoint mPoint = (MultiPoint) road.getBoundary();//or Envelope???

            for (int i = 0; i < mPoint.getNumPoints(); i++)
            {
                Point point=(Point) mPoint.getGeometryN(i);
                this.Boundary.RefreshBoundary(point.getX(), point.getY());
            }

        }

    }


}
