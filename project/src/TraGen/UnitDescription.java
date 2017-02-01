/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TraGen;

import javax.swing.JTextPane;

/**
 * 对轨迹模拟中的各组件进行描述的类，所有模块应当选择将其设置为静态类并实现其中的抽象方法
 * @author Tiger Shao
 */
public abstract class UnitDescription
{

    /**
     * 获取相应的描述信息
     */
    abstract  public String getDescription(Class unitClass);

    /**
     * 获取模块名称
     * @return
     */
    abstract public String getName();

    /**
     * 给出关于模块的详细描述信息(包括图片以及设置文字的颜色等等)，可以不实现
     * @param component 直接在特定组件中定义
     */
     abstract public void giveDescriptionInDetail(JTextPane component);
}
