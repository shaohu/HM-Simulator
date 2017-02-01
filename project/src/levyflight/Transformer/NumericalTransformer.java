/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package levyflight.Transformer;

/**
 * 用于进行数值变换的接口
 * @author TigerShao
 */
public interface NumericalTransformer
{
    /**
     * 给定一个值，按照相应的规则进行变换
     * @param value
     * @return newValue
     */
    public double transform(double value);

    /**
     * 获取基本的变换表达式
     * @return
     */
    public String getExpression();

    /**
     * 获取变换类型的描述
     * @return
     */
    public String getDescription();
}
