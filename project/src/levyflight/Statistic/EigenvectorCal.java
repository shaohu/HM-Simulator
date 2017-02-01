/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Statistic;

import java.util.Iterator;
import levyflight.Data.Trajectory;
import levyflight.Data.Node;

/**
 *计算轨迹特征向量的类
 * @author shili
 */
public class EigenvectorCal
{

    /**
     * 轨迹各点X坐标平均值
     */
    private double xAverage;

    private Trajectory trajectory=null;
    /**
     * 轨迹各点Y坐标平均值
     */
    private double yAverage;
    private double Ixx = 0;
    private double Iyy = 0;
    private double Ixy = 0;
    private double u;

    /**
     * 构造函数
     * @param trajectory轨迹对象
     */
    public EigenvectorCal(Trajectory trajectory)
    {
        Node centrePoint = trajectory.GetCentrePoint();
        this.xAverage = centrePoint.getX();
        this.yAverage = centrePoint.getY();
        this.trajectory=trajectory;
        Iterator iter = trajectory.NodeList.iterator();
        while (iter.hasNext())
        {
            Node tempNode = (Node) iter.next();
            this.Ixx += Math.pow(tempNode.getY() - this.yAverage, 2);
            this.Iyy += Math.pow(tempNode.getX() - this.xAverage, 2);
            this.Ixy = this.Ixy - (tempNode.getX() - this.xAverage) * (tempNode.getY() - this.yAverage);
        }
        this.u = Math.sqrt(4 * Ixy * Ixy + Ixx * Ixx + Iyy * Iyy - 2 * Ixx * Iyy);

    }

    /**
     * 计算轨迹坐标XY的平均值
     * @param trajectory轨迹对象
     */
    public double getMajorSemiAxis()
    {
        double  I = Math.sqrt(0.5 * (Ixx + Iyy + u)/this.trajectory.NodeList.size());
        // I =0.5 * Math.sqrt( (Ixx + Iyy + u));
        // I =0.5 * ( Math.sqrt(Ixx + Iyy) + Math.sqrt(u));
        return I;

    }

    /**
     * 计算轨迹坐标XY的平均值
     * @param trajectory轨迹对象
     */
    public double getMinorSemiAxis()
    {
        double  I = Math.sqrt(0.5 * (Ixx + Iyy - u)/this.trajectory.NodeList.size());
       // I = 0.5 *Math.sqrt( (Ixx + Iyy - u));
       // I =0.5 * ( Math.sqrt(Ixx + Iyy) - Math.sqrt(u));
        return I;

    }

    /**
     * 返回特征向量值
     * @return double[]eigenVector
     */
    public double[] getEigenVector()
    {
        double denominator;//分母上的值
        denominator = 0.5 * (this.Ixx - this.Iyy + this.u);

        double[] eigenVector=new double [2];
//        if (denominator < Double.MIN_NORMAL)    //处理特征向量指向y轴的情况，即特征向量的x分量为NaN；
//        {
//            this.eigenVector[0] = 1;
//            this.eigenVector[1] = 0;
//        } else
//        {
//            if (this.Ixy == 0)
//            {
//                this.eigenVector[0] = 0;
//            } else
//            {
//                this.eigenVector[0] = (-this.Ixy) / denominator;
//            }
//            this.eigenVector[1] = 1;
//        }
        eigenVector[0]=(-this.Ixy) / denominator;
        eigenVector[1]=1;
        return eigenVector;
    }

    /**
     * 计算旋转角度的cos值
     * @return
     */
    public double getRotationAngleInCOS()
    {
        double eigenVector1=this.getEigenVector()[0];
        return (eigenVector1/Math.sqrt(1+eigenVector1*eigenVector1));
    }

        /**
     * 计算旋转角度值, 沿逆时针方向
     * @return
     */
    public double getRotationAngle()
    {
        return (Math.acos(this.getRotationAngleInCOS()));
    }
}
