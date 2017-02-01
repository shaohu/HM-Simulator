/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TraGen;

import java.util.Random;
import javax.swing.JTextPane;

/**
 * 获取服从幂指数分布的随机步长
 * @author Tiger Shao
 */
public class LG_Exponential implements LengthGenerator
{

    /**
     * 用于截断的最小值
     */
    private double xBottom;
    /**
     * 指数项，不能小于或等于1
     */
    private double beta;
    /**
     * 随机数产生类
     */
    private Random randomDouble = new Random();

    /**
     * 组件描述
     */
    final public static UnitDescription descripition=new UnitDescription() {

        @Override
        public String getDescription(Class unitClass)
        {
             return ("Generate random step length according to Exponential distribution");
        }

        @Override
        public String getName()
        {
            return ("Exponential distribution");
        }

        @Override
        public void giveDescriptionInDetail(JTextPane component)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    /**
     * 构造函数
     * @param XBottom 最短飞行距离(截断值)
     * @param Beta 指数项必须大于1
     */
    public LG_Exponential(double XBottom, double Beta) throws Exception
    {
        if (Beta <= 0.0)
        {
            throw (new Exception("beta值必须大于或等于0！"));
        }
        if (XBottom <= 0.0)
        {
            throw (new Exception("xBottom值必须大于或等于0！"));
        }
        this.beta = Beta;
        this.xBottom = XBottom;
    }

    public double getNextStepLength()
    {
        double gamma = 0;
        while (gamma == 0)//gamma取(0,1]之间均匀分布的随机数
        {
            gamma = this.randomDouble.nextDouble();
        }
        // double power = 1 / (this.Beta - 1);
        double result = this.xBottom - Math.log10(gamma) / this.beta;

        return (result);
    }

    /**
     * 返回理论上步长的均值
     * @param xBottom 最短飞行距离(截断值)
     * @param beta 指数项必须大于0
     * @return
     */
    static public double getAverageInTheory(double xBottom, double beta)
    {
        return (xBottom + 1 / beta);
    }
}
