/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TraGen;

import java.util.Random;
import javax.swing.JTextPane;

/**
 * 产生服从均匀分布的方向
 * @author wu
 */
public class DG_Uniform implements DirectionGenerator
{
    final static public UnitDescription descripition=new UnitDescription() {

        @Override
        public String getDescription(Class unitClass)
        {
            return("get movement direction accordong to Uniform_Distribution");
        }

        @Override
        public String getName()
        {
            throw new UnsupportedOperationException("Uniform_Distribution");
        }

        @Override
        public void giveDescriptionInDetail(JTextPane component)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    /**
     * 产生随机数的类
     */
    final   Random randomDouble=new Random();

    public double getNextDirection()
    {
        return ((this.randomDouble.nextFloat() * Math.PI * 2));
    }

}
