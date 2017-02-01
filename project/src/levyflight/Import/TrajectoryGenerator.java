/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Import;

import levyflight.Data.Trajectory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * 飞行路径生成的基础抽象类
 * @author Tiger Shao
 */
public abstract class TrajectoryGenerator
{

    /**
     * 还有未生成的路径
     */
    public boolean HasNext;
    /**
     * 坐标参考系统
     */
    public CoordinateReferenceSystem CoordinateReference;
    /**
     * 文件的绝对地址
     */
    public String AbsoluteFilePath;

    /**
     * 获取下一条路径轨迹
     * @return
     */
    public abstract Trajectory GetNextTrajectory();
}
