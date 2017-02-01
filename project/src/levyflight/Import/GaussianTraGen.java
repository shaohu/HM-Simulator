/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Import;

import TraGen.TC_MBRLimitation;
import TraGen.TC_PolygonRegion;
import TraGen.TC_Population;
import TraGen.TraConstraint;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Deque;
import java.util.Queue;
import java.util.Random;
import levyflight.Data.MBR;
import levyflight.Data.Node;
import levyflight.Data.Trajectory;

/**
 *生成理想状态下服从指数分布的轨迹的类-步长服从指数分布，方向服从均匀分布
 * @author Tiger Shao
 */
public class GaussianTraGen extends TrajectoryGenerator
{

    /**
     * 均值
     */
    private double Average;
    /**
     * 方差
     */
    private double Variance;
    /**
     * 随机数产生类
     */
    private Random RandomDouble = new Random();
    /**
     * 起始位置点
     */
    private Node StartNode;
    /**
     * 起始位置点的随机变化范围
     */
    private MBR StratPointRegion;
    /**
     * 起始飞行时间
     */
    private Calendar StartTime;
    /**
     * 飞行速度-meter-per-second(米/秒)
     */
    private double FlightSpeed;
    /**
     * The ID of the Spatial Reference System
     */
    private int SRID;
    /**
     * 最小外包矩形限制
     */
    private MBR RegionLimit;
    /**
     * 轨迹节点数列表
     */
    private ArrayDeque<Integer> NodeNumBand;
    /**
     * 人口密度的约束
     */
    private TC_Population populationConstraint;
    /**
     * 多边形边界的约束
     */
    private TC_PolygonRegion polygonRegionConstraint;

    /**
     * 构造函数
     * @param average 均值
     * @param variance 方差
     * @param startNode 起始飞行点
     * @param flightSpeed 飞行速度 meters /second
     * @param totalNumToGen 总共要产生的路径条数
     * @param nodesPerTra每一条路径的节点数目
     * @param PopulationConstraint 人口密度的约束,没有的话传空值
     * @param PolygonRegionConstraint 多边形边界约束，没有的话传空值
     * @throws Exception
     */
    public GaussianTraGen(double average, double variance, Node startNode, double flightSpeed, int totalNumToGen, int nodesPerTra, TC_Population PopulationConstraint,TC_PolygonRegion PolygonRegionConstraint) throws Exception
    {
        if (variance <= 0)
        {
            throw (new Exception("0！"));
        }
        if (flightSpeed <= 0.0000000000)
        {
            throw (new Exception("飞行速度不能小于或等于0！"));
        }
        this.Average = average;
        this.Variance = variance;
        this.StartNode = startNode;
        this.FlightSpeed = flightSpeed;
        this.CoordinateReference = null;
        this.populationConstraint = PopulationConstraint;
        this.polygonRegionConstraint=PolygonRegionConstraint;
        if (totalNumToGen > 0)
        {
            this.HasNext = true;
            this.NodeNumBand = new ArrayDeque<Integer>();
            for (int i = 0; i < totalNumToGen; i++)
            {
                this.NodeNumBand.add(nodesPerTra);
            }
        } else
        {
            this.HasNext = false;
        }
    }

    /**
     * 构造函数
     * @param average 均值
     * @param variance 方差
     * @param startNode 起始飞行点
     * @param flightSpeed 飞行速度 
     * @param totalNumToGen 总共要产生的路径条数
     * @param nodesPerTra每一条路径的节点数目
     * @param regionLimit 范围控制
     * @param PopulationConstraint 人口密度的约束,没有的话传空值
     * @param PolygonRegionConstraint 多边形边界约束，没有的话传空值
     * @throws Exception
     */
    public GaussianTraGen(double average, double variance, Node startNode, double flightSpeed, int totalNumToGen, int nodesPerTra, MBR regionLimit, TC_Population PopulationConstraint,TC_PolygonRegion PolygonRegionConstraint) throws Exception
    {
        if (variance <= 1.000000000)
        {
            throw (new Exception("指数项不能小于或等于1！"));
        }
        if (flightSpeed <= 0.0000000000)
        {
            throw (new Exception("飞行速度不能小于或等于0！"));
        }
        this.Average = average;
        this.Variance = variance;
        this.StartNode = startNode;
        this.FlightSpeed = flightSpeed;
        this.CoordinateReference = null;
        this.RegionLimit = regionLimit;
        this.populationConstraint = PopulationConstraint;
        this.polygonRegionConstraint=PolygonRegionConstraint;
        if (totalNumToGen > 0)
        {
            this.HasNext = true;
            this.NodeNumBand = new ArrayDeque<Integer>();
            for (int i = 0; i < totalNumToGen; i++)
            {
                this.NodeNumBand.add(nodesPerTra);
            }
        } else
        {
            this.HasNext = false;
        }
    }

    /**
     * 构造函数
     * @param xBottom 均值
     * @param variance 方差
     * @param startPointRegion 起始飞行点的动态变化范围
     * @param startTime 起始飞行时间
     * @param flightSpeed飞行速度
     * @param totalNumToGen总共要产生的路径条数
     * @param nodesPerTra每一条路径的节点数目
     * @param PopulationConstraint 人口密度的约束,没有的话传空值
     * @param PolygonRegionConstraint 多边形边界约束，没有的话传空值
     * @throws Exception
     */
    public GaussianTraGen(double average, double variance, MBR startPointRegion, Calendar startTime, double flightSpeed, int totalNumToGen, int nodesPerTra, TC_Population PopulationConstraint,TC_PolygonRegion PolygonRegionConstraint) throws Exception
    {
        if (variance <= 0.000000000)
        {
            throw (new Exception("指数项不能小于或等于1！"));
        }
        if (flightSpeed <= 0.0000000000)
        {
            throw (new Exception("飞行速度不能小于或等于0！"));
        }
        this.Average = average;
        this.Variance = variance;
        this.StratPointRegion = startPointRegion;
        this.StartTime = startTime;
        this.FlightSpeed = flightSpeed;
        this.CoordinateReference = null;
        this.populationConstraint = PopulationConstraint;
        this.polygonRegionConstraint=PolygonRegionConstraint;
        if (totalNumToGen > 0)
        {
            this.HasNext = true;
            this.NodeNumBand = new ArrayDeque<Integer>();
            for (int i = 0; i < totalNumToGen; i++)
            {
                this.NodeNumBand.add(nodesPerTra);
            }
        } else
        {
            this.HasNext = false;
        }
    }

    /**
     * 构造函数
     * @param average 均值
     * @param variance 方差
     * @param startPointRegion 起始飞行点的动态变化范围
     * @param startTime 起始飞行时间
     * @param flightSpeed飞行速度
     * @param totalNumToGen总共要产生的路径条数
     * @param nodesPerTra每一条路径的节点数目
     * @param regionLimit范围控制
     * @param PopulationConstraint 人口密度的约束,没有的话传空值
     * @param PolygonRegionConstraint 多边形边界约束，没有的话传空值
     * @throws Exception
     */
    public GaussianTraGen(double average, double variance, MBR startPointRegion, Calendar startTime, double flightSpeed, int totalNumToGen, int nodesPerTra, MBR regionLimit, TC_Population PopulationConstraint,TC_PolygonRegion PolygonRegionConstraint) throws Exception
    {
        if (variance <= 0.000000000)
        {
            throw (new Exception("指数项不能小于或等于1！"));
        }
        if (flightSpeed <= 0.0000000000)
        {
            throw (new Exception("飞行速度不能小于或等于0！"));
        }
        this.Average = average;
        this.Variance = variance;
        this.StratPointRegion = startPointRegion;
        this.StartTime = startTime;
        this.FlightSpeed = flightSpeed;
        this.CoordinateReference = null;
        this.RegionLimit = regionLimit;
        this.populationConstraint = PopulationConstraint;
        this.polygonRegionConstraint=PolygonRegionConstraint;
        if (totalNumToGen > 0)
        {
            this.HasNext = true;
            this.NodeNumBand = new ArrayDeque<Integer>();
            for (int i = 0; i < totalNumToGen; i++)
            {
                this.NodeNumBand.add(nodesPerTra);
            }
        } else
        {
            this.HasNext = false;
        }
    }

    /**
     * 获取服从指数分布的步长
     * @return
     */
    private double GenerateLength()
    {
        double gamma = 0;
        gamma = this.RandomDouble.nextGaussian() * Math.sqrt(this.Variance) + this.Average;
        while (gamma <= 0)//gamma取(0,1]之间均匀分布的随机数
        {
            gamma = this.RandomDouble.nextGaussian() * Math.sqrt(this.Variance) + this.Average;
        }

        return (gamma);
    }

    /**
     * 生成均匀分布的方向
     * @return
     */
    private double GenerateDirecation()
    {
        return ((this.RandomDouble.nextFloat() * Math.PI * 2));
    }

    /**
     * 获取轨迹的起始点
     * @return
     */
    private Node GenerateStartNode()
    {
        if (this.StartNode != null)
        {
            return this.StartNode.Clone();
        } else
        {
            double x = this.StratPointRegion.GetLatitudinalSpan() * this.RandomDouble.nextDouble() + this.StratPointRegion.Left;
            double y = this.StratPointRegion.GetLongitudinalSpan() * this.RandomDouble.nextDouble() + this.StratPointRegion.Bottom;

            ArrayList<TraConstraint> constraintList = new ArrayList<TraConstraint>();
            if (this.RegionLimit != null)
            {
                constraintList.add(new TC_MBRLimitation(this.RegionLimit));
            }
            if (this.populationConstraint != null)
            {
                constraintList.add(populationConstraint);
            }
            if(this.polygonRegionConstraint!=null)
            {
                constraintList.add(polygonRegionConstraint);
            }

            while (!TraConstraint.isTrajectoryMeetConstraints(new Node(x, y, null), constraintList))
            {
                x = this.StratPointRegion.GetLatitudinalSpan() * this.RandomDouble.nextDouble() + this.StratPointRegion.Left;
                y = this.StratPointRegion.GetLongitudinalSpan() * this.RandomDouble.nextDouble() + this.StratPointRegion.Bottom;
            }


            return (new Node(x, y, this.StartTime));
        }
    }

    @Override
    public Trajectory GetNextTrajectory()
    {
        ArrayList<Node> nodeList = new ArrayList<Node>();
        int nodeNumber = this.NodeNumBand.pollFirst();
        if (nodeNumber < 2)
        {
            Trajectory newTrajectory = new Trajectory(nodeList);
            return (newTrajectory);
        }

        nodeList.add(this.GenerateStartNode());

        for (int i = 1; i < nodeNumber; i++)
        {
            double length = this.GenerateLength();
            double direction = this.GenerateDirecation();
            Node lastNode = nodeList.get(i - 1);
            double x = lastNode.getX() + length * Math.cos(direction);
            double y = lastNode.getY() + length * Math.sin(direction);

//            if (this.RegionLimit != null)//加入范围约束
//            {
//                if (!this.RegionLimit.IsPointIn(x, y))
//                {
//                    while (!this.RegionLimit.IsPointIn(x, y))
//                    {
//                        length = this.GenerateLength();
//                        direction = this.GenerateDirecation();
//                        x = lastNode.getX() + length * Math.cos(direction);
//                        y = lastNode.getY() + length * Math.sin(direction);
//                    }
//                }
//            }

            ArrayList<TraConstraint> constraintList = new ArrayList<TraConstraint>();
            if (this.RegionLimit != null)
            {
                constraintList.add(new TC_MBRLimitation(this.RegionLimit));
            }
            if (this.populationConstraint != null)
            {
                constraintList.add(populationConstraint);
            }
            if(this.polygonRegionConstraint!=null)
            {
                constraintList.add(polygonRegionConstraint);
            }

            while (!TraConstraint.isTrajectoryMeetConstraints(new Node(x, y, null), constraintList))
            {
                length = this.GenerateLength();
                direction = this.GenerateDirecation();
                x = lastNode.getX() + length * Math.cos(direction);
                y = lastNode.getY() + length * Math.sin(direction);
            }

            int secondSpan = (int) (length / this.FlightSpeed);
            Calendar time = Calendar.getInstance();
            time.clear();
            time.setTime(lastNode.Time.getTime());
            time.add(Calendar.SECOND, secondSpan);
            Node newNode = new Node(x, y, time);
            nodeList.add(newNode);
        }

        if (this.NodeNumBand.isEmpty())
        {
            this.HasNext = false;
        }
        Trajectory newTrajectory = new Trajectory(nodeList);
        return (newTrajectory);
    }
}
