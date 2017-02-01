/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Statistic;
import java.util.Iterator;
import levyflight.Data.Trajectory;
import levyflight.Data.Node;

/**
 * 计算轨迹回转半径的类
 * @author shili
 */
public class RadiusOfGyrationCal
{

   /**
     * 轨迹各点X坐标平均值
     */
    private double xAverage;
    /**
     * 轨迹各点Y坐标平均值
     */
    private double yAverage;
    /**
     * 轨迹回转半径
     */
    private double radiusOfGyration;
    /**
     * 构造函数
     * @param trajectory轨迹对象
     */
    public RadiusOfGyrationCal(Trajectory trajectory)
    {

        double xSum=0;
        double ySum=0;
        double rSum2=0;
        Iterator iter = trajectory.NodeList.iterator();
        while(iter.hasNext())
        {
            Node tempNode=(Node)iter.next();
            xSum+=tempNode.getX();
            ySum+=tempNode.getY();
        }
       // this.numberOfNode=trajectory.NodeList.size();
        /*for(int i=0;i<this.numberOfNode;i++)
        {
            xSum+=trajectory.NodeList.get(i).X;
            ySum+=trajectory.NodeList.get(i).Y;           
        }*/
        this.xAverage=xSum/trajectory.NodeList.size();
        this.yAverage=ySum/trajectory.NodeList.size();
        iter = trajectory.NodeList.iterator();
        while(iter.hasNext())
        {
            Node tempNode=(Node)iter.next();
            rSum2=rSum2+Math.pow(tempNode.getX()-this.xAverage, 2)+Math.pow(tempNode.getY()-this.yAverage, 2);
        }
        this.radiusOfGyration=Math.sqrt(rSum2/trajectory.NodeList.size());

    }
    /**
     * 构造函数
     * @return double radiusOfGyration
     */
    public double GetRadiusOfGyration()
    {
        return this.radiusOfGyration;
    }

}
