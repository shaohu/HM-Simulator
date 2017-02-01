/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Import;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Administrator
 */
abstract public class RoadGenerator
{

    /**
     * 坐标参考系统
     */
    public CoordinateReferenceSystem CoordinateReference;
    /**
     * 道路名称
     */
    public String Name;
    /**
     * 道路类型
     */
    public Class<?> type;

    /**
     * 文件的绝对地址
     */
    public String AbsoluteFilePath;

    /**
     * 获取所有道路对象
     * @return 新的道路对象，对象类型可能为<LineString> 或<MultiLineString>
     */
    abstract public ArrayList<Geometry> GetAllRoads();

    public RoadGenerator(String filePath)
    {
        this.AbsoluteFilePath=filePath;
    }
}
