/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package levyflight.socialNetwork;

import java.util.ArrayList;
import java.util.Calendar;
import levyflight.Data.Node;
import levyflight.Data.Trajectory;

/**
 *
 * @author KangWei
 */
public class CheckInTrajectory extends Trajectory
{
    /**
     * 主id值
     */
    public int id;

    public CheckInTrajectory(ArrayList<Node> nodeList)
    {
        super(nodeList);
    }

}
