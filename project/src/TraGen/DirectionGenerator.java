/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TraGen;

/**
 * 抽象接口，用于获取服从一定条件的方向分布
 * @author wu
 */
public interface DirectionGenerator
{
    /**
     * 获取下一步的前进方向，取值范围在[0~2pi] 之间，弧度制
     * @return
     */
    public double getNextDirection();
}
