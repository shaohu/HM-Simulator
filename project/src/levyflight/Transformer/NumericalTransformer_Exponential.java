/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package levyflight.Transformer;

/**
 * 指数变换类
 * @author Tiger Shao
 */
public class NumericalTransformer_Exponential  implements NumericalTransformer
{
    /**
     * 系数a
     */
    private double a;

    /**
     * 偏移量b
     */
    private double b;

    public NumericalTransformer_Exponential(double a,double b)
    {
        this.a=a;
        this.b=b;
    }

     /**
     * 设定参数
     * @param a
     * @param b
     */
    public void setParameters(double a,double b)
    {
        this.a=a;
        this.b=b;
    }

    /**
     * 对数值进行指数变换
     * @param value
     * @return
     */
    public double transform(double value)
    {
       return a*Math.exp(value)+b;
    }


    /**
     * 获取指数变换的表达式
     * @return
     */
    public String getExpression()
    {
        return "newValue = a * exp(x) + b";
    }

     /**
     * 返回指数变换的描述
     * @return
     */
    public String getDescription()
    {
        return "Exponential transformation";
    }

}
