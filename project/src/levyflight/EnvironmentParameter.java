/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight;

import java.awt.Color;
import levyflight.Data.Field;
import levyflight.Data.Roads;
import levyflight.Data.Trajectory;
import levyflight.Data.TrajectoryPool;
import levyflight.Export.StatisticGraphGenSchema;
import levyflight.Import.SHPPolygonImporter;
import levyflight.Statistic.FrequencyCal;
import levyflight.Statistic.StatisticBinningStrategy;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *环境变量管理类
 * @author wu
 */
public class EnvironmentParameter
{

    /**
     * 公共坐标投影系统
     */
    public CoordinateReferenceSystem generalCRS;
    /**
     * 道路集合
     */
    public Roads road;
    /**
     * 路径管理集合
     */
    public TrajectoryPool trajectoryPool;
    /**
     * 用于存储"场"的栅格类数据
     */
    public Field field;
    /**
     * 用于NODATA区域显示的颜色
     */
    public Color field_noDataColor;
    /**
     * 用于绘制栅格数据的颜色带
     */
    public Color[] field_colorBand;
    /**
     * 当前轨迹的显示颜色
     */
    public Color trajectoryPool_lineColor;
    /**
     * 当前轨迹的线条宽度
     */
    public double trajectoryPool_lineWidth;
    /**
     * 步长频率计算包
     */
    public FrequencyCal trajectoryPool_freCal_stepLength = null;
    /**
     * ROG频率计算包
     */
    public FrequencyCal trajectoryPool_freCal_ROG = null;
    /**
     * 熵频率计算包
     */
    public FrequencyCal trajectoryPool_freCal_entropy = null;
     /**
     * 移动方向频率计算包
     */
    public FrequencyCal trajectoryPool_freCal_direction = null;
    /**
     * 当前道路的显示颜色
     */
    public Color road_lineColor;
    /**
     * 当前道路的线条宽度
     */
    public double road_lineWidth;
    /**
     * 用于高亮显示地理对象的颜色
     */
    public Color highLightColor;
    /**
     * 频率统计的样式变量集合
     */
    final public StatisticGraphGenSchema graSchema_frequency = new StatisticGraphGenSchema();
    /**
     * 累积频率统计的样式变量集合
     */
    final public StatisticGraphGenSchema graSchema_cumFrequency = new StatisticGraphGenSchema();
    /**
     * 当前被选中的轨迹
     */
    public Trajectory selectedTrajectory = null;
    /**
     * 单个路径频率计算包
     */
    public FrequencyCal selectedTrajectory_freCal = null;
    /**
     * (累积)频率统计策略
     */
    final public StatisticBinningStrategy statisticBinningStrategy=new StatisticBinningStrategy(StatisticBinningStrategy.strategy_optimal,0);

    /**
     * shp类型多边形获取对象
     */
    public SHPPolygonImporter polygonImporter;

    public EnvironmentParameter()
    {
        this.generalCRS = null;
        this.road = null;
        this.trajectoryPool = null;
        this.field = null;
        this.polygonImporter=null;
        this.trajectoryPool_lineColor = Color.BLACK;
        this.trajectoryPool_lineWidth = 1;
        this.road_lineColor = Color.BLACK;
        this.road_lineWidth = 1;
        this.highLightColor = Color.CYAN;
        this.field_colorBand = new Color[]
                {
                    Color.BLACK, Color.WHITE
                };
        this.field_noDataColor = new Color(240, 240, 240);
    }
}
