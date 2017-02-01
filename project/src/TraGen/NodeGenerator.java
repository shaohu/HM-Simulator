/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TraGen;

import levyflight.Data.Node;

/**
 * 抽象接口，用于获取模拟轨迹的节点
 * @author TigerShao
 */
public interface NodeGenerator
{
    /**
     * 获取下一个符合相应约束条件的节点
     * @return
     */
    public Node getNextNode();
}
