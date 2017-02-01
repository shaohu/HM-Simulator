/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TraGen;

import java.util.ArrayList;
import java.util.Random;
import levyflight.Data.Field;
import levyflight.Data.Node;
import levyflight.Transformer.NumericalTransformer;

/**
 * 人口密度约束
 * @author Tiger Shao
 */
public class TC_Population extends TraConstraint
{

    /**
     * 用于表示人口密度的场数据
     */
    private Field populationField;
    /**
     * 场数据最小值
     */
    private double minValue;
    /**
     * 场数据最大值
     */
    private double maxValue;
    /**
     * 最大最小值之差
     */
    private double interval;
    /**
     * 均匀分布随机数产生类
     */
    final private Random RandomDouble = new Random();

    private NumericalTransformer transformer;

    /**
     * 构造函数
     * @param PopulationField
     * @param transformer 数值转换规则
     */
    public TC_Population(Field PopulationField,NumericalTransformer transformer)
    {
        this.populationField = PopulationField;
        this.transformer=transformer;
        double max = transformer.transform(this.populationField.getExtremums()[0]);
        double min = transformer.transform(this.populationField.getExtremums()[1]);
        if(max>min)
        {
            this.minValue = max;
            this.maxValue = min;
        }else
        {
            this.minValue = min;
            this.maxValue = max;
        }
        this.interval = this.maxValue - this.minValue;
    }

    public boolean isTrajectoryMeetConstraint(Node node)
    {

        //如果节点不在population场中，直接剔除掉
        if (!this.populationField.Boundary.IsPointIn(node))
        {
            return false;
        }

        double nodeValue = transformer.transform(this.populationField.getValueAt(node, 0));

        //无数据点不能生成值
        if (nodeValue == this.populationField.getNoDataValue())
        {
            return false;
        }

        double decisionMakingValue=this.RandomDouble.nextDouble()*this.interval+this.minValue;
        if(decisionMakingValue<=nodeValue)
        {
            return true;
        }else
        {
            return false;
        }

    }
}
