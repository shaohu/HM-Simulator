/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package levyflight.Transformer;

/**
 * 线性变换类
 * @author Tiger Shao
 */
public class NumericalTransformer_Linear implements NumericalTransformer
{
    /**
     * 系数a
     */
    private double a;
    
    /**
     * 偏移量b
     */
    private double b;

    public NumericalTransformer_Linear(double a,double b)
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
     * 对数值进行线性变换
     * @param value
     * @return
     */
    public double transform(double value)
    {
        return a*value+b;
    }

    /**
     * 获取线性变换的表达式
     * @return
     */
    public String getExpression()
    {
        return "newValue = a(x) + b";
    }


    /**
     * 返回线性变换的描述
     * @return
     */
    public String getDescription()
    {
        return "Linear transformation";
    }

}
