/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TraGen;

import java.util.ArrayList;
import levyflight.Data.Node;

/**
 * 轨迹内部尺度下的约束判断接口
 * @author Tiger Shao
 */
abstract public class TraConstraint
{

    /**
     * 判断当前的轨迹是否满足相应的约束
     * @param nodeList 节点链表
     * @return
     */
    abstract public boolean isTrajectoryMeetConstraint(Node node);

    /**
     * 判断一个节点是否满足约束条件的组合
     * @param node 节点
     * @param Constraint 约束值列表(可以包含null值并判断为true)
     * @return
     */
    static public boolean isTrajectoryMeetConstraints(Node node, ArrayList<TraConstraint> constraintList)
    {
        boolean isMeet = true;
        for (TraConstraint constraint : constraintList)
        {
            if (constraint != null)
            {
                isMeet = constraint.isTrajectoryMeetConstraint(node);
                if (!isMeet)
                {
                    break;
                }
            }
        }
        return isMeet;
    }
}
