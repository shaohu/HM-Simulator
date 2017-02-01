/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Data;

/**
 *Minimum Boundary Rectangle
 * @author Tiger Shao
 */
public class MBR
{

    public double Left;
    public double Right;
    public double Top;
    public double Bottom;

    public MBR()
    {
        this.Bottom = Double.POSITIVE_INFINITY;
        this.Left = Double.POSITIVE_INFINITY;
        this.Right = Double.NEGATIVE_INFINITY;
        this.Top = Double.NEGATIVE_INFINITY;
    }

    /**
     * 初始化最小外包矩形！
     */
    public void Initialize()
    {
        this.Bottom = Double.POSITIVE_INFINITY;
        this.Left = Double.POSITIVE_INFINITY;
        this.Right = Double.NEGATIVE_INFINITY;
        this.Top = Double.NEGATIVE_INFINITY;
    }

    /**
     * 根据新的坐标更新最小外包矩形的边界
     * @param x 横坐标
     * @param y 纵坐标
     */
    public void RefreshBoundary(double x, double y)
    {
        if (this.Left > x)
        {
            this.Left = x;
        }
        if (this.Right < x)
        {
            this.Right = x;
        }
        if (this.Top < y)
        {
            this.Top = y;
        }
        if (this.Bottom > y)
        {
            this.Bottom = y;
        }
    }

    @Override
    public String toString()
    {
        String result = "LevyFlight Minimum Boundary Rectangle[ Left:" + String.valueOf(this.Left) + ",Right:" + String.valueOf(this.Right) + ",Top:" + String.valueOf(this.Top) + ",Bottom:" + String.valueOf(this.Bottom) + " ]";
        return (result);
    }

    /**
     * 用此MBR和新的MBR合并
     * @param newMBR
     */
    public void Union(MBR newMBR)
    {
        if (newMBR != null)
        {
            this.RefreshBoundary(newMBR.Left, newMBR.Bottom);
            this.RefreshBoundary(newMBR.Right, newMBR.Top);
        }
    }

    /**
     * 返回一个新的MBR副本
     * @return
     */
    public static MBR Clone(MBR mbr)
    {
        if (mbr == null)
        {
            return null;
        }
        MBR boundry = new MBR();
        boundry.Bottom = mbr.Bottom;
        boundry.Left = mbr.Left;
        boundry.Right = mbr.Right;
        boundry.Top = mbr.Top;
        return boundry;
    }

    /**
     * 判断某一点是否落在MBR中，包含边界
     * @param x
     * @param y
     * @return
     */
    public boolean IsPointIn(double x, double y)
    {
        if (x < this.Left || x > this.Right)
        {
            return false;
        }
        if (y < this.Bottom || y > this.Top)
        {
            return false;
        }
        return true;
    }

    /**
     * 判断某一点是否落在MBR中，包含边界
     * @param node
     * @return
     */
    public boolean IsPointIn(Node node)
    {
        return (IsPointIn(node.getX(), node.getY()));
    }

    /**
     * 该MBR中的一个点到其边界的距离
     * @param x 横坐标
     * @param y 纵坐标
     * @return
     */
    public double GetPointDistanceToBoundry(double x, double y)
    {
        if (!this.IsPointIn(x, y))
        {
            return Double.NaN;
        }
        double distance = Double.POSITIVE_INFINITY;
        if (x - this.Left < distance)
        {
            distance = x - this.Left;
        }
        if (this.Right - x < distance)
        {
            distance = this.Right - x;
        }
        if (y - this.Bottom < distance)
        {
            distance = y - this.Bottom;
        }
        if (this.Top - y < distance)
        {
            distance = this.Top - y;
        }
        return distance;
    }

    /**
     * 计算该MBR的面积
     * @return
     */
    public double GetArea()
    {
        return (this.GetLatitudinalSpan() * this.GetLongitudinalSpan());
    }

    /**
     * 计算MBR的横向跨度
     * @return
     */
    public double GetLatitudinalSpan()
    {
        return (this.Right - this.Left);
    }

    /**
     * 计算MBR的纵向跨度
     * @return
     */
    public double GetLongitudinalSpan()
    {
        return (this.Top - this.Bottom);
    }

    /**
     * 检查该MBR和另一个MBR是否有交集
     * @param otherMBR
     * @return
     */
    public boolean IsHasOverlapWith(MBR otherMBR)
    {
        if (otherMBR == null)
        {
            return false;
        }
        if (this.Bottom >= otherMBR.Top || this.Top <= otherMBR.Bottom)
        {
            return false;
        }
        if (this.Left >= otherMBR.Right || this.Right <= otherMBR.Left)
        {
            return false;
        }
        return true;
    }
}
