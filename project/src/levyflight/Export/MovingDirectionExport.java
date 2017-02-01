/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package levyflight.Export;

import Static.StaticLib;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import levyflight.Data.Trajectory;
import levyflight.EnvironmentParameter;
import org.geotools.swing.data.JFileDataStoreChooser;

/**
 * 用于输出轨迹移动方向的类
 * @author Tiger Shao
 */
public class MovingDirectionExport
{
       /**
     * 环境变量
     */
    private EnvironmentParameter ep;

    public MovingDirectionExport(EnvironmentParameter ep)
    {
        this.ep = ep;
    }

    /**
     * 输出运动方向文件
     */
    public void exportMovingDirectionFile()
    {
        String path = "";
        JFileDataStoreChooser chooser = new JFileDataStoreChooser("txt");
        chooser.setDialogTitle("Save trajectories' step length as txt file");
        if (StaticLib.InitialFileDirectory != null && StaticLib.InitialFileDirectory.exists())
        {
            if(StaticLib.InitialFileDirectory.isFile())
            {
                path=StaticLib.InitialFileDirectory.getParent()+"\\MovingDirection_File.txt";
            } else if(StaticLib.InitialFileDirectory.isDirectory())
            {
                path=StaticLib.InitialFileDirectory.getAbsolutePath()+"\\MovingDirection_File.txt";
            }
            chooser.setSaveFile(new File(path));
        }
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileDataStoreChooser.APPROVE_OPTION)
        {
            PrintWriter writer=null;
            try
            {
                path = chooser.getSelectedFile().getAbsolutePath();
                StaticLib.InitialFileDirectory = new File(path);
                writer = new PrintWriter(path);
                for(Trajectory trajectory :ep.trajectoryPool.TrajectorySet)
                {
                    StringBuilder sb=new StringBuilder();
                    ArrayList<Double> directionList=trajectory.ExtractMovingDirections();
                    for(int i=0;i<directionList.size();i++)
                    {
                        sb.append(directionList.get(i));
                        if(i!=directionList.size()-1)
                        {
                            sb.append(',');
                        }
                    }
                    writer.println(sb.toString());
                }
                writer.flush();
            } catch (FileNotFoundException ex)
            {
                javax.swing.JOptionPane.showMessageDialog(null, ex.getMessage());
            } finally
            {
                writer.close();
                javax.swing.JOptionPane.showMessageDialog(null, "Moving direction exporting finished.", "Info", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }

        }
    }
}
