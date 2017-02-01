/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Data;

import java.util.ArrayList;
import levyflight.Import.TrajectoryGenerator;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *路径集合管理类
 * @author Tiger Shao
 */
public class TrajectoryPool
{

    /**
     * 路径集合
     */
    public ArrayList<Trajectory> TrajectorySet;
    /**
     * 路径集合的最小外包矩形
     */
    public MBR Boundary;
    /**
     * 用于添加ID的标识符
     */
    private int IDFlag;
    /**
     * 时间精度
     */
    public TimePrecisionEnum TimePrecision;
    /**
     * 坐标参考系统
     */
    public CoordinateReferenceSystem CoordinateReference;
    /**
     * 类标识的名称
     */
    public static final String className = "LevyFlightTrajectoryPool";

    /**
     * 文件的绝对地址
     */
    public String AbsoluteFilePath;

    /**
     * 构造函数
     * @param referenceSystem 坐标参考系统
     * @param timePrecision时间精度
     */
    public TrajectoryPool(CoordinateReferenceSystem referenceSystem, TimePrecisionEnum timePrecision)
    {
        this.Boundary = new MBR();
        this.TrajectorySet = new ArrayList<Trajectory>();
        this.IDFlag = 0;
        this.TimePrecision = timePrecision;
        this.CoordinateReference = referenceSystem;
        this.RefreshBoundry();
    }

    /**
     * 向路径池中添加新的单条路径
     * @param newTrajectory
     * @return TrajectoryCollection size
     */
    public int AddTrajectory(Trajectory newTrajectory)
    {
        newTrajectory.ID = this.IDFlag;
        this.IDFlag++;
        this.TrajectorySet.add(newTrajectory);
        for (Node point : newTrajectory.NodeList)
        {
            this.Boundary.RefreshBoundary(point.getX(), point.getY());
        }
        return (this.TrajectorySet.size());
    }

    /**
     * 在改变路径集合后更新边界(添加边界时禁用)
     */
    public void RefreshBoundry()
    {
        this.Boundary.Initialize();
        for (Trajectory trajectory : this.TrajectorySet)
        {
            for (Node point : trajectory.NodeList)
            {
                this.Boundary.RefreshBoundary(point.getX(), point.getY());
            }
        }
    }

    public void Fill(TrajectoryGenerator generator) throws Exception
    {
        if ((this.CoordinateReference != null && generator.CoordinateReference != null) && (!this.CoordinateReference.equals(generator.CoordinateReference)))
        {
            throw (new Exception("坐标系统不匹配，道路数据加载失败！"));
        }

        this.AbsoluteFilePath=generator.AbsoluteFilePath;
        while (generator.HasNext)
        {
            this.AddTrajectory(generator.GetNextTrajectory());
        }
    }
}
