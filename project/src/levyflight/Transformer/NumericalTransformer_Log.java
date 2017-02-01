/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package levyflight.Transformer;

/**
 * 对数变换类
 * @author Tiger Shao
 */
public class NumericalTransformer_Log implements NumericalTransformer
{

     /**
     * 系数
     */
    private double a;
     /**
     * 外偏移量
     */
    private double b;
    /**
     * 内偏移量
     */
    private double k;


    public NumericalTransformer_Log(double a,double b,double k)
    {
        this.a=a;
        this.b=b;
        this.k=k;
    }
    /**
     * 对数值进行对数变换
     * @param value
     * @return
     */
    public double transform(double value)
    {
        return a*Math.log10(value + k)+b;
    }

        /**
     * 设定参数
     * @param b
     */
    public void setParameters(double a,double b,double k)
    {
        this.a=a;
        this.b=b;
        this.k=k;
    }
    /**
     * 获取对数变换的表达式
     * @return
     */
    public String getExpression()
    {
        return "newValue = a * log10( x + k ) + b";
    }

     /**
     * 返回对数变换的描述
     * @return
     */
    public String getDescription()
    {
        return "Logarithmic transformation";
    }

}
