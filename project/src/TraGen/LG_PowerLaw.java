/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TraGen;

import java.util.Random;
import javax.swing.JTextPane;

/**
 * 获取服从幂律分布的随机步长
 * @author Tiger Shao
 */
public class LG_PowerLaw implements LengthGenerator
{

    /**
     * 用于截断的最小值
     */
    private double xBottom;
    /**
     * 幂指数，不能小于或等于1
     */
    private double exponent;
    /**
     * 均匀分布随机数产生类
     */
    final private Random RandomDouble = new Random();

    /**
         * 组件描述
     */
    final public static UnitDescription descripition=new UnitDescription() {

        @Override
        public String getDescription(Class unitClass)
        {
            return ("Generate random step length according to Power_Law distribution");
        }

        @Override
        public String getName()
        {
            return ("Power Law distribution");
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
     * @param Exponent 幂指数必须大于1
     */
    public LG_PowerLaw(double XBottom, double Exponent) throws Exception
    {
        if (XBottom <= 0.0)
        {
            throw (new Exception("最短飞行距离不能小于或等于0！"));
        }

        if (Exponent <= 1.000000000)
        {
            throw (new Exception("幂指数不能小于或等于1！"));
        }
        this.exponent = Exponent;
        this.xBottom = XBottom;
    }

    /**
     * 返回理论上步长的均值
     * @param xBottom 最短飞行距离(截断值)
     * @param exponent 幂指数必须大于1
     * @return
     */
    static public double getAverageInTheory(double xBottom, double exponent)
    {
        if (exponent <= 2)
        {
            return (Double.NaN);
        } else
        {
            return ((exponent - 1) / (exponent - 2) * xBottom);
        }
    }

    public double getNextStepLength()
    {
        double gamma = 0;
        while (gamma == 0)//gamma取(0,1]之间均匀分布的随机数
        {
            gamma = this.RandomDouble.nextDouble();
        }
        double power = 1 / (this.exponent - 1);
        double result = Math.pow((1 / gamma), power) * this.xBottom;
        // result=gamma*this.XBottom;
        return (result);
    }



}
