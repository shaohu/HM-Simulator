/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TraGen;


/**
 * 抽象接口，用于获取符合一定分布条件的步长值
 * @author TigerShao
 */
public interface LengthGenerator
{
    /**
     * 获取下一个步长值
     * @return
     */
    public double getNextStepLength();

}
