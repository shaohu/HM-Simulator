/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TraGen;

import java.util.Random;
import javax.swing.JTextPane;

/**
 *
 * @author wu
 */
public class LG_Gaussian implements LengthGenerator
{

    /**
     * 均值
     */
    private double average;
    /**
     * 方差
     */
    private double variance;
    /**
     * 随机数产生类
     */
    private Random randomDouble = new Random();
    /**
     * 组件描述
     */
    final public static UnitDescription descripition = new UnitDescription()
    {

        @Override
        public String getDescription(Class unitClass)
        {
            return ("Generate random step length according to Gaussian distribution");
        }

        @Override
        public String getName()
        {
            return ("Gaussian distribution");
        }

        @Override
        public void giveDescriptionInDetail(JTextPane component)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    /**
     * 构造函数
     * @param Average 均值
     * @param Variance 方差
     * @throws Exception
     */
    public LG_Gaussian(double Average, double Variance) throws Exception
    {
        if (Variance <= 0.0)
        {
            throw (new Exception("方差必须大于等于0！"));
        }
        if (Average <= 0.0)
        {
            throw (new Exception("均值必须大于等于0！"));
        }
        this.average = Average;
        this.variance = Variance;
    }

    public double getNextStepLength()
    {
        double gamma = 0;
        gamma = this.randomDouble.nextGaussian() * Math.sqrt(this.variance) + this.average;
        while (gamma <= 0)//gamma取(0,1]之间均匀分布的随机数
        {
            gamma = this.randomDouble.nextGaussian() * Math.sqrt(this.variance) + this.average;
        }
        return (gamma);
    }
}
