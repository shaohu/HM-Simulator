/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TraGen;

import java.util.ArrayList;
import javax.swing.JTextPane;
import levyflight.Data.MBR;
import levyflight.Data.Node;

/**
 * 将节点限制在举行范围内的约束条件
 * @author Tiger Shao
 */
public class TC_MBRLimitation extends TraConstraint
{
    /**
     * 组件描述
     */
    final   static  public UnitDescription descripition = new UnitDescription() {

        @Override
        public String getDescription(Class unitClass)
        {
            return("Ensure generated nodes in custom region");
        }

        @Override
        public String getName()
        {
            return("boundry region constraint");
        }

        @Override
        public void giveDescriptionInDetail(JTextPane component)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    /**
     * 最小外包矩形限制
     */
    private MBR regionLimit=null;
    
    public TC_MBRLimitation(double left,double top,double right, double bottom)
    {
        this.regionLimit=new MBR();
        this.regionLimit.RefreshBoundary(left, top);
        this.regionLimit.RefreshBoundary(right, bottom);
    }

    public TC_MBRLimitation(MBR RegionLimit)
    {
        this.regionLimit=RegionLimit;
    }


    public boolean isTrajectoryMeetConstraint(Node node)
    {
        return(this.regionLimit.IsPointIn(node));
    }



}
