/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Statistic;

/**
 *用于记录统计分级策略的类
 * @author Tiger Shao
 */
public class StatisticBinningStrategy
{

    /**
     * 优化的分级策略
     */
    final static public int strategy_optimal = 0;
    /**
     * 固定步长
     */
    final static public int strategy_fixedInterval = 1;
    /**
     * 固定分段数
     */
    final static public int strategy_fixedSegment = 2;
    /**
     * 样本数的百分比
     */
    final static public int strategy_percentageOfSampleSize = 3;

    public StatisticBinningStrategy(int Strategy, double Value)
    {
        this.strategy = Strategy;
        this.value = Value;
        this.calEverySamplyInCDF=true;
    }

    /**
     * 标识在累积频率统计中是否对每个样本做统计
     */
    public boolean calEverySamplyInCDF;

    /**
     *分级策略
     */
    private int strategy = 0;
    /**
     * 分级规则，针对不同策略具有不同的含义
     */
    private double value = 0;

    /**
     * 设置分级策略
     * @param Strategy
     */
    public void setStrategy(int Strategy)
    {
        this.strategy = Strategy;
    }

    /**
     * 获取分级策略
     * @return
     */
    public int getStrategy()
    {
        return this.strategy;
    }

    /**
     * 获取对应策略所需的值
     * @return
     */
    public double getValue()
    {
        return this.value;
    }

    /**
     * 设置对应策略所需的值
     * @param Value
     */
    public void setValue(double Value)
    {
        this.value = Value;
    }
}
