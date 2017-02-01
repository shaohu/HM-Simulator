/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Data;

import java.lang.Integer;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JOptionPane;

/**
 * 记录用户个体移动路径的轨迹类
 * @author Tiger Shao, shili
 */
public class Trajectory
{

    /**
     * 路径点集合链表
     */
    public ArrayList<Node> NodeList;
    /**
     * 唯一标识号
     */
    public int ID;

    /**
     * 构造函数
     * @param nodeList 节点列表
     */
    public Trajectory(ArrayList<Node> nodeList)
    {
        this.NodeList = nodeList;
        this.ID = -1;
    }

    /**
     * 向路径中添加新的节点
     * @param newNode 新的节点
     * @return NodeList size
     */
    public int AddNode(Node newNode)
    {
        this.NodeList.add(newNode);
        return (this.NodeList.size());
    }

    /**
     * 获取飞行轨迹中的所有步长值
     * @return
     */
    public double[] GetStepLengthList()
    {
        if (this.NodeList == null || this.NodeList.size() < 2)
        {
            return (null);
        } else
        {
            double[] result = new double[this.NodeList.size() - 1];
            for (int i = 0; i < this.NodeList.size() - 1; i++)
            {
                result[i] = this.NodeList.get(i).DistanceTo(this.NodeList.get(i + 1));
            }
            return (result);
        }
    }

    /**
     * 获取该条轨迹的中心点（中心）
     * @return
     */
    public Node GetCentrePoint()
    {
        if (this.NodeList == null || this.NodeList.isEmpty())
        {
            return null;
        }
        double xAverage = 0;
        double yAverage = 0;
        for (Node node : this.NodeList)
        {
            xAverage += node.getX();
            yAverage += node.getY();
        }
        xAverage /= this.NodeList.size();
        yAverage /= this.NodeList.size();
        return (new Node(xAverage, yAverage, null));
    }

    /**
     * 计算轨迹的ROG-回转半径（如果路径点个数为0，返回Double.NaN）
     * @return
     */
    public double GetRadiusOfGyration()
    {
        Node centre = this.GetCentrePoint();
        if (centre == null)
        {
            return Double.NaN;
        }
        double result = 0;
        for (Node node : this.NodeList)
        {
            result += Math.pow(node.getX() - centre.getX(), 2) + Math.pow(node.getY() - centre.getY(), 2);
        }
        result /= this.NodeList.size();
        result = Math.sqrt(result);
        return result;
    }

    /**
     * 获取特征向量的大小以及方向（与x轴的夹角）
     * @return double[半长轴, 半短轴, 与x轴夹角的cos值]
     */
    public double[] GetPrincipalAxes()
    {
        if (this.NodeList == null || this.NodeList.isEmpty())
        {
            return null;
        }
        double Ixx = 0;
        double Iyy = 0;
        double Ixy = 0;
        Node center = this.GetCentrePoint();
        for (Node node : this.NodeList)
        {
            double x = node.getX() - center.getX();
            double y = node.getY() - center.getY();
            Ixx += Math.pow(y, 2);
            Iyy += Math.pow(x, 2);
            Ixy -= x * y;
        }
        double u = Math.sqrt(4 * Ixy * Ixy + Ixx * Ixx + Iyy * Iyy - 2 * Ixx * Iyy);
        double semiMajorAxis = (Ixx + Iyy + u) / 2;
        double semiMinorAxis = (Ixx + Iyy - u) / 2;
        double v1 = 2 * Ixy / (Ixx - Ixy + u);
        double angle = -v1 / Math.sqrt(1 + v1 * v1);
        return (new double[]
                {
                    semiMajorAxis, semiMinorAxis, angle
                });
    }

    public MBR getBoundryBox()
    {
        MBR boundry=new MBR();
        for(Node node: this.NodeList)
        {
            boundry.RefreshBoundary(node.getX(), node.getY());
        }
        return boundry;
    }

    /**
     * 对该轨迹根据节点数量比例采集重访点并计算熵值
     * @param amountPercentage 聚类分析后余下节点数占原节点数的比例。在 0~1 之间
     * @return
     */
    public double GetEntropyByAmount(double amountPercentage)
    {
        final int size = this.NodeList.size();
        int temp = (int) (size * amountPercentage);
        if (temp < 1 || temp > size)
        {
            temp = size / 2;
        }
        final int numRemain = temp;
        double[][] disMatrix = new double[size][size];
        for (int i = 1; i < size; i++)
        {
            for (int j = 0; j < i; j++)//由于是对称矩阵，只用算一半的距离,i从1才开始有数据
            {
                disMatrix[i][j] = this.NodeList.get(i).DistanceTo(this.NodeList.get(j));
            }
        }
        //=============================================================================================
        //此处计算各个点的最邻近距离，可删除
        //        MBR boundry = new MBR();
        //        for (Node node : this.NodeList)
        //        {
        //            boundry.RefreshBoundary(node.getX(), node.getY());
        //        }
        //        double[] disToMBRList = new double[size];
        //        for (int i = 0; i < size; i++)
        //        {
        //            disToMBRList[i] = boundry.GetPointDistanceToBoundry(this.NodeList.get(i).getX(), this.NodeList.get(i).getY());
        //        }
        //        ArrayList<Double> minDisList = new ArrayList<Double>();
        //        for (int i = 1; i < size; i++)
        //        {
        //            double tempMin = Double.POSITIVE_INFINITY;
        //            for (int j = 0; j < i; j++)
        //            {
        //                if (tempMin > disMatrix[i][j])
        //                {
        //                    tempMin = disMatrix[i][j];
        //                }
        //            }
        //            if (tempMin < disToMBRList[i])
        //            {
        //                minDisList.add(tempMin);
        //            }
        //        }
        //
        //        double minAverage = 0;
        //        for (double d : minDisList)
        //        {
        //            minAverage += d;
        //        }
        //        minAverage /= minDisList.size();
        //
        //        //R值为该轨迹点集的临近指数。R越小，表示点越趋近与凝集分布，从中提取重访点越有意义。
        //        double R = 2 * minAverage * Math.sqrt((double) size / boundry.GetArea());
        //=============================================================================================

        //算法来源，聚类分析法，地学数学模型-毛善君-P135
        int[] indexBand = new int[size];//用来存储合并后以前每一个点在新的组中的索引
        int[] linkBand = new int[size];//动态变化，用来记载在找到最小值，矩阵删除相应的行列缩小后，从0到length所分别对应的组的index
        for (int i = 0; i < size; i++)
        {
            indexBand[i] = i;
            linkBand[i] = i;
        }

        int min_i = -1;
        int min_j = -1;
        double min_value = Double.POSITIVE_INFINITY;
        int currentStep = size;
        int newGroupIndex = size;
        while (currentStep > numRemain)
        {
            min_i = -1;
            min_j = -1;
            min_value = Double.POSITIVE_INFINITY;
            for (int i = 1; i < currentStep; i++)//寻找当前最小值，注意，此处从第一行开始才有数据的。
            {
                for (int j = 0; j < i; j++)
                {
                    //   System.out.print(disMatrix[i][j] + " ");
                    if (min_value > disMatrix[i][j])
                    {
                        min_value = disMatrix[i][j];
                        min_i = i;
                        min_j = j;
                    }
                }
                //    System.out.println();
            }
//            System.out.print(min_i + " , " + min_j + " , ");
//            System.out.println(min_value);
//            System.out.println();
            //此处 j 总是小于 i ，min_j 总是小于 min_i
            int oldGroupIndex1 = linkBand[min_i];
            int oldGroupIndex2 = linkBand[min_j];
            for (int i = 0; i < size; i++)//在此处将以前的两组归入新组中
            {
                if (indexBand[i] == oldGroupIndex1 || indexBand[i] == oldGroupIndex2)
                {
                    indexBand[i] = newGroupIndex;
                }
            }

            //此处开始调整linkBand,由于删除了min_j，min_i对应的行列数据，因此要将相应的数据前移
            for (int i = min_j; i < currentStep - 1; i++)
            {
                linkBand[i] = linkBand[i + 1];
            }
            for (int i = min_i - 1; i < currentStep - 1; i++)//前面已经向前错动了一次，因此此处要min_i-1
            {
                linkBand[i] = linkBand[i + 1];
            }
            linkBand[currentStep - 2] = newGroupIndex;

            //此处开始调整矩阵。充分利用矩阵，首先要生成新的一行
            int rowIndex = currentStep % size;//获取新的行号，如果是满的，则新行放在第0行（第0行是空行）
            for (int i = 0; i < currentStep; i++)//从第0列开始
            {
                if (i == min_i || i == min_j)
                {
                    disMatrix[rowIndex][i] = -10.0;
                } else
                {
                    double dis1 = disMatrix[Math.max(i, min_i)][Math.min(i, min_i)];
                    double dis2 = disMatrix[Math.max(i, min_j)][Math.min(i, min_j)];
                    disMatrix[rowIndex][i] = Math.min(dis1, dis2);
                }
            }
            //删除所有行列号中涉及min_i , min_j 的行列
            //先删除行
            for (int i = min_j; i < currentStep; i++)
            {
                if (i == 0)//第 0 行可能保存有关键数据，并且在删除过程中可能无关紧要，此处忽略
                {
                    continue;
                }
//                for (int j = 0; j < currentStep; j++)
//                {
//                    disMatrix[i][j] = disMatrix[(i + 1) % size][j];
//                }
                System.arraycopy(disMatrix[(i + 1) % size], 0, disMatrix[i], 0, currentStep);
            }
            for (int i = min_i - 1; i < currentStep - 1; i++)//前面移动了一次，所以此处应当减1
            {
                if (i == 0)
                {
                    continue;
                }
//                for (int j = 0; j < currentStep; j++)
//                {
//                    disMatrix[i][j] = disMatrix[(i + 1) % size][j];
//                }
                System.arraycopy(disMatrix[(i + 1) % size], 0, disMatrix[i], 0, currentStep);
            }

            //再删除列
            for (int i = 1; i < currentStep; i++)
            {
                for (int j = min_j; j < currentStep - 1; j++)
                {
                    disMatrix[i][j] = disMatrix[i][j + 1];
                }
            }
            for (int i = 1; i < currentStep; i++)
            {
                for (int j = min_i - 1; j < currentStep - 2; j++)//之前已经删去一列，因此要min_i-1
                {
                    disMatrix[i][j] = disMatrix[i][j + 1];
                }
            }

            currentStep--;
            newGroupIndex++;
        }

        //此处统计各组的数量
        ArrayList<Integer> countList = new ArrayList<Integer>();
        while (true)
        {
            int index = -1;
            int num = 0;
            for (int i = 0; i < indexBand.length; i++)
            {
                if (indexBand[i] != -1 && index == -1)//初始化index
                {
                    index = indexBand[i];
                    indexBand[i] = -1;
                    num++;
                }
                if (indexBand[i] != -1 && index == indexBand[i])
                {
                    indexBand[i] = -1;
                    num++;
                }
            }
            if (index == -1)
            {
                break;
            }
            countList.add(num);
        }

        //开始计算熵
        double result = 0;
        for (int num : countList)
        {
            double probability = (double) num / (double) size;
            result -= probability * Math.log10(probability) / Math.log10(2.0);
        }
        return result;
    }

    /**
     * 对该轨迹根据节点相互之间平均距离比例采集重访点并计算熵值
     * @param averageDistancePercentage 将相互距离 小于 平均距离*averageDistancePercentage 的点归为一类， averageDistancePercentage大于0，小于maxDistance/averageDistance
     * @return
     */
    public double GetEntropyByDistance(double averageDistancePercentage)
    {
        final int size = this.NodeList.size();
        double[][] disMatrix = new double[size][size];
        double minDistance = Double.POSITIVE_INFINITY;
        double maxDistance = Double.NEGATIVE_INFINITY;
        for (int i = 1; i < size; i++)
        {
            for (int j = 0; j < i; j++)//由于是对称矩阵，只用算一半的距离,i从1才开始有数据
            {
                disMatrix[i][j] = this.NodeList.get(i).DistanceTo(this.NodeList.get(j));
                if (minDistance > disMatrix[i][j])
                {
                    minDistance = disMatrix[i][j];
                }
                if (maxDistance < disMatrix[i][j])
                {
                    maxDistance = disMatrix[i][j];
                }
            }
        }
        double distance = 0;
        double[] distanceBand = this.GetStepLengthList();
        for (double d : distanceBand)
        {
            distance += d;
        }
        distance /= (double) distanceBand.length;
        if (distance * averageDistancePercentage > minDistance && distance * averageDistancePercentage < maxDistance)
        {
            distance *= averageDistancePercentage;
        }
        final double distanceControl = distance;

        //=============================================================================================
        //此处计算各个点的最邻近距离，可删除
        //        MBR boundry = new MBR();
        //        for (Node node : this.NodeList)
        //        {
        //            boundry.RefreshBoundary(node.getX(), node.getY());
        //        }
        //        double[] disToMBRList = new double[size];
        //        for (int i = 0; i < size; i++)
        //        {
        //            disToMBRList[i] = boundry.GetPointDistanceToBoundry(this.NodeList.get(i).getX(), this.NodeList.get(i).getY());
        //        }
        //        ArrayList<Double> minDisList = new ArrayList<Double>();
        //        for (int i = 1; i < size; i++)
        //        {
        //         double tempMin = Double.POSITIVE_INFINITY;
        //            for (int j = 0; j < i; j++)
        //            {
        //                if (tempMin > disMatrix[i][j])
        //                {
        //                    tempMin = disMatrix[i][j];
        //                }
        //            }
        //            if (tempMin < disToMBRList[i])
        //            {
        //                minDisList.add(tempMin);
        //            }
        //        }
        //
        //        double minAverage = 0;
        //        for (double d : minDisList)
        //        {
        //            minAverage += d;
        //        }
        //        minAverage /= minDisList.size();
        //
        //        //R值为该轨迹点集的临近指数。R越小，表示点越趋近与凝集分布，从中提取重访点越有意义。
        //        double R = 2 * minAverage * Math.sqrt((double) size / boundry.GetArea());
        //=============================================================================================

        //算法来源，聚类分析法，地学数学模型-毛善君-P135
        int[] indexBand = new int[size];//用来存储合并后以前每一个点在新的组中的索引
        int[] linkBand = new int[size];//动态变化，用来记载在找到最小值，矩阵删除相应的行列缩小后，从0到length所分别对应的组的index
        for (int i = 0; i < size; i++)
        {
            indexBand[i] = i;
            linkBand[i] = i;
        }

        int min_i = -1;
        int min_j = -1;
        double min_value = Double.POSITIVE_INFINITY;
        int currentStep = size;
        int newGroupIndex = size;
        while (currentStep > 1)
        {
            min_i = -1;
            min_j = -1;
            min_value = Double.POSITIVE_INFINITY;
            for (int i = 1; i < currentStep; i++)//寻找当前最小值，注意，此处从第一行开始才有数据的。
            {
                for (int j = 0; j < i; j++)
                {
                    //   System.out.print(disMatrix[i][j] + " ");
                    if (min_value > disMatrix[i][j])
                    {
                        min_value = disMatrix[i][j];
                        min_i = i;
                        min_j = j;
                    }
                }
                //    System.out.println();
            }
            if (min_value > distanceControl)
            {
                break;
            }
//            System.out.print(min_i + " , " + min_j + " , ");
//            System.out.println(min_value);
//            System.out.println();
            //此处 j 总是小于 i ，min_j 总是小于 min_i
            int oldGroupIndex1 = linkBand[min_i];
            int oldGroupIndex2 = linkBand[min_j];
            for (int i = 0; i < size; i++)//在此处将以前的两组归入新组中
            {
                if (indexBand[i] == oldGroupIndex1 || indexBand[i] == oldGroupIndex2)
                {
                    indexBand[i] = newGroupIndex;
                }
            }

            //此处开始调整linkBand,由于删除了min_j，min_i对应的行列数据，因此要将相应的数据前移
            for (int i = min_j; i < currentStep - 1; i++)
            {
                linkBand[i] = linkBand[i + 1];
            }
            for (int i = min_i - 1; i < currentStep - 1; i++)//前面已经向前错动了一次，因此此处要min_i-1
            {
                linkBand[i] = linkBand[i + 1];
            }
            linkBand[currentStep - 2] = newGroupIndex;

            //此处开始调整矩阵。充分利用矩阵，首先要生成新的一行
            int rowIndex = currentStep % size;//获取新的行号，如果是满的，则新行放在第0行（第0行是空行）
            for (int i = 0; i < currentStep; i++)//从第0列开始
            {
                if (i == min_i || i == min_j)
                {
                    disMatrix[rowIndex][i] = -10.0;
                } else
                {
                    double dis1 = disMatrix[Math.max(i, min_i)][Math.min(i, min_i)];
                    double dis2 = disMatrix[Math.max(i, min_j)][Math.min(i, min_j)];
                    disMatrix[rowIndex][i] = Math.min(dis1, dis2);
                }
            }
            //删除所有行列号中涉及min_i , min_j 的行列
            //先删除行
            for (int i = min_j; i < currentStep; i++)
            {
                if (i == 0)//第 0 行可能保存有关键数据，并且在删除过程中可能无关紧要，此处忽略
                {
                    continue;
                }
//                for (int j = 0; j < currentStep; j++)
//                {
//                    disMatrix[i][j] = disMatrix[(i + 1) % size][j];
//                }
                System.arraycopy(disMatrix[(i + 1) % size], 0, disMatrix[i], 0, currentStep);
            }
            for (int i = min_i - 1; i < currentStep - 1; i++)//前面移动了一次，所以此处应当减1
            {
                if (i == 0)
                {
                    continue;
                }
//                for (int j = 0; j < currentStep; j++)
//                {
//                    disMatrix[i][j] = disMatrix[(i + 1) % size][j];
//                }
                System.arraycopy(disMatrix[(i + 1) % size], 0, disMatrix[i], 0, currentStep);
            }

            //再删除列
            for (int i = 1; i < currentStep; i++)
            {
                for (int j = min_j; j < currentStep - 1; j++)
                {
                    disMatrix[i][j] = disMatrix[i][j + 1];
                }
            }
            for (int i = 1; i < currentStep; i++)
            {
                for (int j = min_i - 1; j < currentStep - 2; j++)//之前已经删去一列，因此要min_i-1
                {
                    disMatrix[i][j] = disMatrix[i][j + 1];
                }
            }

            currentStep--;
            newGroupIndex++;
        }

        //此处统计各组的数量
        ArrayList<Integer> countList = new ArrayList<Integer>();
        while (true)
        {
            int index = -1;
            int num = 0;
            for (int i = 0; i < indexBand.length; i++)
            {
                if (indexBand[i] != -1 && index == -1)//初始化index
                {
                    index = indexBand[i];
                    indexBand[i] = -1;
                    num++;
                }
                if (indexBand[i] != -1 && index == indexBand[i])
                {
                    indexBand[i] = -1;
                    num++;
                }
            }
            if (index == -1)
            {
                break;
            }
            countList.add(num);
        }

        //开始计算熵
        double result = 0;
        for (int num : countList)
        {
            double probability = (double) num / (double) size;
            result -= probability * Math.log10(probability) / Math.log10(2.0);
        }
        return result;
    }

    /**
     * 根据总的路径点的数量来提取轨迹中的重访点
     * @param amountPercentage 得到的重访点占原来总点数的百分比
     * @return 返回"3维"列表，包括重访点的x, y坐标和重访概率
     */
    public ArrayList<Double> ExtractRevistiedPointByAmount(double amountPercentage)
    {
        final int size = this.NodeList.size();
        int temp = (int) (size * amountPercentage);
        if (temp < 1 || temp > size)
        {
            temp = size / 2;
        }
        final int numRemain = temp;
        double[][] disMatrix = new double[size][size];
        for (int i = 1; i < size; i++)
        {
            for (int j = 0; j < i; j++)//由于是对称矩阵，只用算一半的距离,i从1才开始有数据
            {
                disMatrix[i][j] = this.NodeList.get(i).DistanceTo(this.NodeList.get(j));
            }
        }

        //算法来源，聚类分析法，地学数学模型-毛善君-P135
        int[] indexBand = new int[size];//用来存储合并后以前每一个点在新的组中的索引
        int[] linkBand = new int[size];//动态变化，用来记载在找到最小值，矩阵删除相应的行列缩小后，从0到length所分别对应的组的index
        for (int i = 0; i < size; i++)
        {
            indexBand[i] = i;
            linkBand[i] = i;
        }

        int min_i = -1;
        int min_j = -1;
        double min_value = Double.POSITIVE_INFINITY;
        int currentStep = size;
        int newGroupIndex = size;

        while (currentStep > numRemain)
        {
            min_i = -1;
            min_j = -1;
            min_value = Double.POSITIVE_INFINITY;
            for (int i = 1; i < currentStep; i++)//寻找当前最小值，注意，此处从第一行开始才有数据的。
            {
                for (int j = 0; j < i; j++)
                {
                    //   System.out.print(disMatrix[i][j] + " ");
                    if (min_value > disMatrix[i][j])
                    {
                        min_value = disMatrix[i][j];
                        min_i = i;
                        min_j = j;
                    }
                }
            }

            //此处 j 总是小于 i ，min_j 总是小于 min_i
            int oldGroupIndex1 = linkBand[min_i];
            int oldGroupIndex2 = linkBand[min_j];
            for (int i = 0; i < size; i++)//在此处将以前的两组归入新组中
            {
                if (indexBand[i] == oldGroupIndex1 || indexBand[i] == oldGroupIndex2)
                {
                    indexBand[i] = newGroupIndex;
                }
            }

            //此处开始调整linkBand,由于删除了min_j，min_i对应的行列数据，因此要将相应的数据前移
            for (int i = min_j; i < currentStep - 1; i++)
            {
                linkBand[i] = linkBand[i + 1];
            }
            for (int i = min_i - 1; i < currentStep - 1; i++)//前面已经向前错动了一次，因此此处要min_i-1
            {
                linkBand[i] = linkBand[i + 1];
            }
            linkBand[currentStep - 2] = newGroupIndex;

            //此处开始调整矩阵。充分利用矩阵，首先要生成新的一行
            int rowIndex = currentStep % size;//获取新的行号，如果是满的，则新行放在第0行（第0行是空行）
            for (int i = 0; i < currentStep; i++)//从第0列开始
            {
                if (i == min_i || i == min_j)
                {
                    disMatrix[rowIndex][i] = -10.0;
                } else
                {
                    double dis1 = disMatrix[Math.max(i, min_i)][Math.min(i, min_i)];
                    double dis2 = disMatrix[Math.max(i, min_j)][Math.min(i, min_j)];
                    disMatrix[rowIndex][i] = Math.min(dis1, dis2);
                }
            }
            //删除所有行列号中涉及min_i , min_j 的行列
            //先删除行
            for (int i = min_j; i < currentStep; i++)
            {
                if (i == 0)//第 0 行可能保存有关键数据，并且在删除过程中可能无关紧要，此处忽略
                {
                    continue;
                }
                System.arraycopy(disMatrix[(i + 1) % size], 0, disMatrix[i], 0, currentStep);
            }
            for (int i = min_i - 1; i < currentStep - 1; i++)//前面移动了一次，所以此处应当减1
            {
                if (i == 0)
                {
                    continue;
                }
                System.arraycopy(disMatrix[(i + 1) % size], 0, disMatrix[i], 0, currentStep);
            }

            //再删除列
            for (int i = 1; i < currentStep; i++)
            {
                for (int j = min_j; j < currentStep - 1; j++)
                {
                    disMatrix[i][j] = disMatrix[i][j + 1];
                }
            }
            for (int i = 1; i < currentStep; i++)
            {
                for (int j = min_i - 1; j < currentStep - 2; j++)//之前已经删去一列，因此要min_i-1
                {
                    disMatrix[i][j] = disMatrix[i][j + 1];
                }
            }

            currentStep--;
            newGroupIndex++;
        }


        ArrayList<Double> pointList = new ArrayList<Double>();

        while (true)
        {
            int index = -1;
            int num = 0;
            double location_x = 0;
            double location_y = 0;
            for (int i = 0; i < indexBand.length; i++)
            {
                if (indexBand[i] != -1 && index == -1)//初始化index
                {
                    index = indexBand[i];
                    indexBand[i] = -1;
                    num++;
                    location_x += this.NodeList.get(i).getX();
                    location_y += this.NodeList.get(i).getY();
                }
                if (indexBand[i] != -1 && index == indexBand[i])
                {
                    indexBand[i] = -1;
                    num++;
                    location_x += this.NodeList.get(i).getX();
                    location_y += this.NodeList.get(i).getY();
                }
            }
            if (index == -1)
            {
                break;
            }
            location_x /= num;
            location_y /= num;
            double probability = (double) num / (double) size;
            pointList.add(location_x);
            pointList.add(location_y);
            pointList.add(probability);
        }

        return pointList;

    }

    /**
     * 对该轨迹根据节点相互之间平均距离比例来提取轨迹中的重访点
     * @param averageDistancePercentage 将相互距离 小于 平均距离*averageDistancePercentage 的点归为一类， averageDistancePercentage大于0，小于maxDistance/averageDistance
     * @return 返回"3维"列表，包括重访点的x, y坐标和重访概率
     */
    public ArrayList<Double> ExtractRevistiedPointByDistance(double averageDistancePercentage)
    {
        final int size = this.NodeList.size();
        double[][] disMatrix = new double[size][size];
        double minDistance = Double.POSITIVE_INFINITY;
        double maxDistance = Double.NEGATIVE_INFINITY;
        for (int i = 1; i < size; i++)
        {
            for (int j = 0; j < i; j++)//由于是对称矩阵，只用算一半的距离,i从1才开始有数据
            {
                disMatrix[i][j] = this.NodeList.get(i).DistanceTo(this.NodeList.get(j));
                if (minDistance > disMatrix[i][j])
                {
                    minDistance = disMatrix[i][j];
                }
                if (maxDistance < disMatrix[i][j])
                {
                    maxDistance = disMatrix[i][j];
                }
            }
        }
        double distance = 0;
        double[] distanceBand = this.GetStepLengthList();
        for (double d : distanceBand)
        {
            distance += d;
        }
        distance /= (double) distanceBand.length;
        if (distance * averageDistancePercentage > minDistance && distance * averageDistancePercentage < maxDistance)
        {
            distance *= averageDistancePercentage;
        }
        final double distanceControl = distance;

        //算法来源，聚类分析法，地学数学模型-毛善君-P135
        int[] indexBand = new int[size];//用来存储合并后以前每一个点在新的组中的索引
        int[] linkBand = new int[size];//动态变化，用来记载在找到最小值，矩阵删除相应的行列缩小后，从0到length所分别对应的组的index
        for (int i = 0; i < size; i++)
        {
            indexBand[i] = i;
            linkBand[i] = i;
        }

        int min_i = -1;
        int min_j = -1;
        double min_value = Double.POSITIVE_INFINITY;
        int currentStep = size;
        int newGroupIndex = size;
        while (currentStep > 1)
        {
            min_i = -1;
            min_j = -1;
            min_value = Double.POSITIVE_INFINITY;
            for (int i = 1; i < currentStep; i++)//寻找当前最小值，注意，此处从第一行开始才有数据的。
            {
                for (int j = 0; j < i; j++)
                {
                    //   System.out.print(disMatrix[i][j] + " ");
                    if (min_value > disMatrix[i][j])
                    {
                        min_value = disMatrix[i][j];
                        min_i = i;
                        min_j = j;
                    }
                }
                //    System.out.println();
            }
            if (min_value > distanceControl)
            {
                break;
            }
//            System.out.print(min_i + " , " + min_j + " , ");
//            System.out.println(min_value);
//            System.out.println();
            //此处 j 总是小于 i ，min_j 总是小于 min_i
            int oldGroupIndex1 = linkBand[min_i];
            int oldGroupIndex2 = linkBand[min_j];
            for (int i = 0; i < size; i++)//在此处将以前的两组归入新组中
            {
                if (indexBand[i] == oldGroupIndex1 || indexBand[i] == oldGroupIndex2)
                {
                    indexBand[i] = newGroupIndex;
                }
            }

            //此处开始调整linkBand,由于删除了min_j，min_i对应的行列数据，因此要将相应的数据前移
            for (int i = min_j; i < currentStep - 1; i++)
            {
                linkBand[i] = linkBand[i + 1];
            }
            for (int i = min_i - 1; i < currentStep - 1; i++)//前面已经向前错动了一次，因此此处要min_i-1
            {
                linkBand[i] = linkBand[i + 1];
            }
            linkBand[currentStep - 2] = newGroupIndex;

            //此处开始调整矩阵。充分利用矩阵，首先要生成新的一行
            int rowIndex = currentStep % size;//获取新的行号，如果是满的，则新行放在第0行（第0行是空行）
            for (int i = 0; i < currentStep; i++)//从第0列开始
            {
                if (i == min_i || i == min_j)
                {
                    disMatrix[rowIndex][i] = -10.0;
                } else
                {
                    double dis1 = disMatrix[Math.max(i, min_i)][Math.min(i, min_i)];
                    double dis2 = disMatrix[Math.max(i, min_j)][Math.min(i, min_j)];
                    disMatrix[rowIndex][i] = Math.min(dis1, dis2);
                }
            }
            //删除所有行列号中涉及min_i , min_j 的行列
            //先删除行
            for (int i = min_j; i < currentStep; i++)
            {
                if (i == 0)//第 0 行可能保存有关键数据，并且在删除过程中可能无关紧要，此处忽略
                {
                    continue;
                }
//                for (int j = 0; j < currentStep; j++)
//                {
//                    disMatrix[i][j] = disMatrix[(i + 1) % size][j];
//                }
                System.arraycopy(disMatrix[(i + 1) % size], 0, disMatrix[i], 0, currentStep);
            }
            for (int i = min_i - 1; i < currentStep - 1; i++)//前面移动了一次，所以此处应当减1
            {
                if (i == 0)
                {
                    continue;
                }
//                for (int j = 0; j < currentStep; j++)
//                {
//                    disMatrix[i][j] = disMatrix[(i + 1) % size][j];
//                }
                System.arraycopy(disMatrix[(i + 1) % size], 0, disMatrix[i], 0, currentStep);
            }

            //再删除列
            for (int i = 1; i < currentStep; i++)
            {
                for (int j = min_j; j < currentStep - 1; j++)
                {
                    disMatrix[i][j] = disMatrix[i][j + 1];
                }
            }
            for (int i = 1; i < currentStep; i++)
            {
                for (int j = min_i - 1; j < currentStep - 2; j++)//之前已经删去一列，因此要min_i-1
                {
                    disMatrix[i][j] = disMatrix[i][j + 1];
                }
            }

            currentStep--;
            newGroupIndex++;
        }

        ArrayList<Double> pointList = new ArrayList<Double>();

        while (true)
        {
            int index = -1;
            int num = 0;
            double location_x = 0;
            double location_y = 0;
            for (int i = 0; i < indexBand.length; i++)
            {
                if (indexBand[i] != -1 && index == -1)//初始化index
                {
                    index = indexBand[i];
                    indexBand[i] = -1;
                    num++;
                    location_x += this.NodeList.get(i).getX();
                    location_y += this.NodeList.get(i).getY();
                }
                if (indexBand[i] != -1 && index == indexBand[i])
                {
                    indexBand[i] = -1;
                    num++;
                    location_x += this.NodeList.get(i).getX();
                    location_y += this.NodeList.get(i).getY();
                }
            }
            if (index == -1)
            {
                break;
            }
            location_x /= num;
            location_y /= num;
            double probability = (double) num / (double) size;
            pointList.add(location_x);
            pointList.add(location_y);
            pointList.add(probability);
        }

        return pointList;
    }

    /**
     * 提取该条轨迹的移动方向列表,角度制，从0到360度，x轴开始，逆时针
     * @return
     */
    public ArrayList<Double> ExtractMovingDirections()
    {
        ArrayList<Double> directionList=new ArrayList<Double>();
        for(int i=0; i<this.NodeList.size()-1; i++)
        {
            double dx=NodeList.get(i+1).getX()-NodeList.get(i).getX();
            double dy=NodeList.get(i+1).getY()-NodeList.get(i).getY();
            double angle=Math.atan(dx/dy)/Math.PI*180;  //-90~90之间
            if(angle>0)
            {
                if(dx<0)//第三象限
                {
                    angle+=180;
                }
            }else
            {
                if(dx>0)//第二象限
                {
                    angle+=180;
                }else//第四象限
                {
                    angle+=360;
                }
            }
            directionList.add(angle);
        }
        return directionList;
    }

    public Calendar getStartTime()
    {
        Calendar c=null;
        for(Node node: NodeList)
        {
            if(node.Time!=null)
            {
                if(c==null)
                {
                    c=node.Time;
                }else
                {
                    if(c.after(node.Time))
                    {
                        c=node.Time;
                    }
                }
            }
        }

        return c;
    }

    public Calendar getEndTime()
    {
        Calendar c=null;
        for(Node node: NodeList)
        {
            if(node.Time!=null)
            {
                if(c==null)
                {
                    c=node.Time;
                }else
                {
                    if(c.before(node.Time))
                    {
                        c=node.Time;
                    }
                }
            }
        }

        return c;
    }

}
