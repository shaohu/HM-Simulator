/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Statistic;

import java.util.ArrayList;
import java.util.Arrays;
import levyflight.Data.Trajectory;
import levyflight.Data.TrajectoryPool;

/**
 *用于计算点距的频率和累积频率的类
 * @author shili, Tiger Shao
 */
public class FrequencyCal
{

    /**
     * 数据集合链表
     */
    private ArrayList<Double> dataList;
    /**
     * 相邻各点距离最小长度
     */
    private double minValue;
    /**
     * 相邻各点距离最大长度
     */
    private double maxValue;
//    /**
//     * 分组值的下限（并非等于最小值）
//     */
//    private double bottom;
    /**
     * 频率柱状图分段数
     */
    private int segment;
    /**
     * 相邻频率段对应距离间隔
     */
    private double interval;
    /**
     * 各频率值集合链表
     */
    //private ArrayList<Double> FrenquencyList;
    /**
     * 各累积频率值集合链表
     */
    //private ArrayList<Double> CumulativeFrenquencyList;
    /**
     * 记录落在各个区段中的点的个数
     */
    private int[] regionDataNumList;

    /**
     * 构造函数
     * @param trajectoryPool
     * @param statisticBinningStrategy 统计策略
     */
    public FrequencyCal(TrajectoryPool trajectoryPool, StatisticBinningStrategy statisticBinningStrategy)
    {
        if (statisticBinningStrategy.getStrategy() == StatisticBinningStrategy.strategy_fixedInterval)
        {
            this.structure(trajectoryPool, statisticBinningStrategy.getValue());
        } else if ((statisticBinningStrategy.getStrategy() == StatisticBinningStrategy.strategy_fixedSegment))
        {
            this.structure(trajectoryPool, (int) (statisticBinningStrategy.getValue()));
        } else if ((statisticBinningStrategy.getStrategy() == StatisticBinningStrategy.strategy_percentageOfSampleSize))
        {
            int sampleSize = 0;
            for (Trajectory trajectory : trajectoryPool.TrajectorySet)
            {
                sampleSize += (trajectory.NodeList.size() - 1);
            }
            int Segment = (int) (sampleSize * statisticBinningStrategy.getValue());
            this.structure(trajectoryPool, Segment);
        } else if ((statisticBinningStrategy.getStrategy() == StatisticBinningStrategy.strategy_optimal))
        {
            this.structure(trajectoryPool);
        }
    }

    public FrequencyCal(ArrayList<Double> DataList, StatisticBinningStrategy statisticBinningStrategy)
    {
        if (statisticBinningStrategy.getStrategy() == StatisticBinningStrategy.strategy_fixedInterval)
        {
            this.structure(DataList, statisticBinningStrategy.getValue());
        } else if ((statisticBinningStrategy.getStrategy() == StatisticBinningStrategy.strategy_fixedSegment))
        {
            this.structure(DataList, (int) (statisticBinningStrategy.getValue()));
        } else if ((statisticBinningStrategy.getStrategy() == StatisticBinningStrategy.strategy_percentageOfSampleSize))
        {
            int sampleSize = DataList.size();
            int Segment = (int) (sampleSize * statisticBinningStrategy.getValue());
            this.structure(DataList, Segment);
        } else if ((statisticBinningStrategy.getStrategy() == StatisticBinningStrategy.strategy_optimal))
        {
            this.structure(DataList);
        }
    }

    /**
     * 构造函数
     * @param trajectoryPool 轨迹集合
     * @param Interval 频率段距离间隔
     */
    private void structure(TrajectoryPool trajectoryPool, double Interval)
    {
        ArrayList<Double> DataList = new ArrayList<Double>();
        for (Trajectory trajectory : trajectoryPool.TrajectorySet)
        {
            double[] distanceList = trajectory.GetStepLengthList();
            for (double d : distanceList)
            {
                DataList.add(d);
            }
        }

        this.dataList = DataList;
        this.minValue = Double.POSITIVE_INFINITY;
        this.maxValue = Double.NEGATIVE_INFINITY;
        for (Double d : DataList)
        {
            if (d > maxValue)
            {
                maxValue = d;
            }
            if (d < minValue)
            {
                minValue = d;
            }
        }

        this.interval = Interval;
        int Segment = (int) ((maxValue - minValue) / this.interval);
        while (Segment * this.interval + minValue < maxValue)
        {
            Segment++;
        }
        this.segment = Segment;
        this.initializeRegionDataNumList();
    }

    /**
     * 构造函数
     * @param trajectory 轨迹集合
     * @param Segment 频率柱状图分段数
     */
    private void structure(TrajectoryPool trajectoryPool, int Segment)
    {
        ArrayList<Double> DataList = new ArrayList<Double>();
        for (Trajectory trajectory : trajectoryPool.TrajectorySet)
        {
            double[] distanceList = trajectory.GetStepLengthList();
            for (double d : distanceList)
            {
                DataList.add(d);
            }
        }

        this.dataList = DataList;
        this.segment = Segment;
        this.minValue = Double.POSITIVE_INFINITY;
        this.maxValue = Double.NEGATIVE_INFINITY;
        for (Double d : DataList)
        {
            if (d > maxValue)
            {
                maxValue = d;
            }
            if (d < minValue)
            {
                minValue = d;
            }
        }
        this.interval = (maxValue - minValue) / (double) (this.segment);
        this.initializeRegionDataNumList();
    }

    /**
     * 构造函数
     * @param trajectoryPool
     */
    private void structure(TrajectoryPool trajectoryPool)
    {
        ArrayList<Double> DataList = new ArrayList<Double>();
        for (Trajectory trajectory : trajectoryPool.TrajectorySet)
        {
            double[] distanceList = trajectory.GetStepLengthList();
            for (double d : distanceList)
            {
                DataList.add(d);
            }
        }
        int Segment = 1 + (int) (3.32 * Math.log10((double) DataList.size()) + 0.5);

        this.dataList = DataList;
        this.segment = Segment;
        this.minValue = Double.POSITIVE_INFINITY;
        this.maxValue = Double.NEGATIVE_INFINITY;
        for (Double d : DataList)
        {
            if (d > maxValue)
            {
                maxValue = d;
            }
            if (d < minValue)
            {
                minValue = d;
            }
        }
        this.interval = (maxValue - minValue) / (double) (this.segment);
        this.initializeRegionDataNumList();
    }

    /**
     * 构造函数
     * @param DataList 需要进行计算的数据集
     * @param segment 分段数
     */
    private void structure(ArrayList<Double> DataList, int Segment)
    {
        this.dataList = DataList;
        this.segment = Segment;
        this.minValue = Double.POSITIVE_INFINITY;
        this.maxValue = Double.NEGATIVE_INFINITY;
        for (Double d : DataList)
        {
            if (d > maxValue)
            {
                maxValue = d;
            }
            if (d < minValue)
            {
                minValue = d;
            }
        }
        this.interval = (maxValue - minValue) / (double) (this.segment);
        this.initializeRegionDataNumList();
    }

    /**
     * 构造函数
     * @param DataList 需要进行计算的数据集
     * @param Interval 频率段距离间隔
     */
    private void structure(ArrayList<Double> DataList, double Interval)
    {
        this.dataList = DataList;
        this.minValue = Double.POSITIVE_INFINITY;
        this.maxValue = Double.NEGATIVE_INFINITY;
        for (Double d : DataList)
        {
            if (d > maxValue)
            {
                maxValue = d;
            }
            if (d < minValue)
            {
                minValue = d;
            }
        }

        this.interval = Interval;
        int Segment = (int) ((maxValue - minValue) / this.interval);
        while (Segment * this.interval + minValue < maxValue)
        {
            Segment++;
        }
        this.segment = Segment;
        this.initializeRegionDataNumList();
    }

    /**
     * 构造函数
     * @param DataList 需要进行计算的数据集
     */
    private void structure(ArrayList<Double> DataList)
    {
        int Segment = 1 + (int) (3.32 * Math.log10((double) DataList.size()) + 0.5);

        this.dataList = DataList;
        this.segment = Segment;
        this.minValue = Double.POSITIVE_INFINITY;
        this.maxValue = Double.NEGATIVE_INFINITY;
        for (Double d : DataList)
        {
            if (d > maxValue)
            {
                maxValue = d;
            }
            if (d < minValue)
            {
                minValue = d;
            }
        }
        this.interval = (maxValue - minValue) / (double) (this.segment);
        this.initializeRegionDataNumList();
    }

    /**
     * 初始化regionDataNumList
     */
    private void initializeRegionDataNumList()
    {
        this.regionDataNumList = new int[this.segment];
        Arrays.fill(this.regionDataNumList, 0);
        int index = 0;
        for (Double d : this.dataList)
        {
            index = (int) ((d - this.minValue) / this.interval);
            if (index >= this.segment)
            {
                index = this.segment - 1;
            }
            this.regionDataNumList[index]++;
        }
    }
    private double[] result_CDFByGroup = null;

    /**
     * 根据事先分好的组产生累积频率分布列表
     * @return ArrayList<Double> CumulativeFrequencyList  注意:此处包括[bottom，0]点，第0个点为[bottom,0]
     */
    public double[] getCDFByGroup()
    {
        if (this.result_CDFByGroup != null)
        {
            return this.result_CDFByGroup;
        }
        double[] resultList = new double[this.segment + 1];
        int totalNum = 0;
        for (int i = 0; i < resultList.length; i++)
        {
            resultList[i] = (double) totalNum / (double) this.dataList.size();
            if (i < this.regionDataNumList.length)
            {
                totalNum += this.regionDataNumList[i];
            }
        }
        this.result_CDFByGroup = resultList;
        return resultList;
    }
    private double[] result_CCDFByGroup = null;

    /**
     * 根据事先分好的组产生互补累积频率分布列表
     * @return ArrayList<Double> CumulativeFrequencyList  注意:此处包括[bottom，0]点，第0个点为[bottom,0]
     */
    public double[] getCCDFByGroup()
    {
        if (this.result_CCDFByGroup != null)
        {
            return this.result_CCDFByGroup;
        }
        double[] resultList = new double[this.segment + 1];
        int totalNum = this.dataList.size();
        for (int i = 0; i < resultList.length; i++)
        {
            resultList[i] = (double) totalNum / (double) this.dataList.size();
            if (i < this.regionDataNumList.length)
            {
                totalNum -= this.regionDataNumList[i];
            }
        }
        this.result_CCDFByGroup = resultList;
        return resultList;
    }

    /**
     * 根据样本数据产生累积概率统计列表,计算结果就存储在输入参数中
     * @param valueList
     * @param probabilityList
     */
    public void getCDFBySample(ArrayList<Double> valueList, ArrayList<Double> probabilityList)
    {
        double[] DataList = new double[this.dataList.size()];
        for (int i = 0; i < this.dataList.size(); i++)
        {
            DataList[i] = this.dataList.get(i);
        }
        Arrays.sort(DataList);//对DataList进行升序排列
        valueList.clear();
        probabilityList.clear();
        ArrayList<Integer> countList = new ArrayList<Integer>();
        for (int i = 0; i < DataList.length; i++)
        {
            if (valueList.isEmpty())
            {
                valueList.add(DataList[i]);
                countList.add(1);
            } else
            {
                double value = valueList.get(valueList.size() - 1);
                if (value == DataList[i])
                {
                    countList.set(countList.size() - 1, countList.get(countList.size() - 1) + 1);
                } else
                {
                    valueList.add(DataList[i]);
                    countList.add(1);
                }
            }
        }
        int sum = 0;
        for (int i = 0; i < countList.size(); i++)
        {
            sum += countList.get(i);
            probabilityList.add((double) sum / (double) DataList.length);
        }
    }

    /**
     * 根据样本数据产生互补累积概率统计列表,计算结果就存储在输入参数中
     * @param valueList
     * @param probabilityList
     */
    public void getCCDFBySample(ArrayList<Double> valueList, ArrayList<Double> probabilityList)
    {
        double[] DataList = new double[this.dataList.size()];
        for (int i = 0; i < this.dataList.size(); i++)
        {
            DataList[i] = this.dataList.get(i);
        }
        Arrays.sort(DataList);//对DataList进行升序排列
        valueList.clear();
        probabilityList.clear();
        ArrayList<Integer> countList = new ArrayList<Integer>();
        int numLeft = DataList.length;
        for (int i = 0; i < DataList.length; i++)
        {
            if (valueList.isEmpty())//第0个
            {
                valueList.add(DataList[i]);
                countList.add(numLeft);
            } else
            {
                double value = valueList.get(valueList.size() - 1);
                if (value == DataList[i])//相同，不变
                {
                    //countList.set(countList.size() - 1, countList.get(countList.size() - 1) - 1);
                } else//不相同，建立新的值对
                {
                    valueList.add(DataList[i]);
                    countList.add(numLeft);
                }
            }
            numLeft--;
        }
        for (int i = 0; i < countList.size(); i++)
        {
            probabilityList.add((double) countList.get(i) / (double) DataList.length);
        }
    }
    private double[] result_frequencyList = null;

    /**
     * 返回分段频率分布列表
     * @return ArrayList<Double>FrenquencyList
     */
    public double[] getFrequencyList()
    {
        if (this.result_frequencyList != null)
        {
            return this.result_frequencyList;
        }
        double[] resultList = new double[this.segment];
        for (int i = 0; i < this.segment; i++)
        {
            resultList[i] = (double) this.regionDataNumList[i] / (double) this.dataList.size();
        }
        this.result_frequencyList = resultList;
        return resultList;
    }

    /**
     * 获取统计组距
     * @return
     */
    public double getInterval()
    {
        return this.interval;
    }

    /**
     * 获取统计分段数
     * @return
     */
    public int getSegmentCount()
    {
        return this.segment;
    }
    double result_average = Double.NEGATIVE_INFINITY;

    /**
     * 获取统计集合的平均值
     * @return
     */
    public double getAverage()
    {
        if (this.result_average != Double.NEGATIVE_INFINITY)
        {
            return this.result_average;
        }
        double result = 0;
        for (double d : this.dataList)
        {
            result += d;
        }
        result /= this.dataList.size();
        this.result_average = result;
        return result;
    }
    private double result_sampleVariance = Double.NEGATIVE_INFINITY;

    /**
     * 计算数据集的样本方差
     * @return
     */
    public double getSampleVariance()
    {
        if (this.result_sampleVariance != Double.NEGATIVE_INFINITY)
        {
            return this.result_sampleVariance;
        }
        double average = this.getAverage();
        double result = 0;
        for (double d : this.dataList)
        {
            result += (d - average) * (d - average);
        }
        result /= (this.dataList.size() - 1);
        this.result_sampleVariance = result;
        return result;
    }

    /**
     * 获取总的样本数量
     * @return
     */
    public int getTotalSampleSize()
    {
        return this.dataList.size();
    }

    /**
     * 查找统计样本中的最小值
     * @return
     */
    public double getMinValue()
    {
        return this.minValue;
    }

    /**
     * 查找统计样本中的最大值
     * @return
     */
    public double getMaxValue()
    {
        return this.maxValue;
    }
}
