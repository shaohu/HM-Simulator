/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package levyflight.socialNetwork;

import java.util.Calendar;
import levyflight.Data.Node;

/**
 *
 * @author wu
 */
public class CheckinNode extends Node
{
    public int id;
    
    public CheckinNode(double x, double y, Calendar time,int Id)
    {
        super(x,y,time);
        id=Id;
    }
}
