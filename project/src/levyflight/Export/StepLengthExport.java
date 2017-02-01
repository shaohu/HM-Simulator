/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Export;

import Static.StaticLib;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import levyflight.Data.Trajectory;
import levyflight.EnvironmentParameter;
import org.geotools.swing.data.JFileDataStoreChooser;

/**
 *用于输出轨迹步长值的类
 * @author Tigher Shao
 */
public class StepLengthExport
{

    /**
     * 环境变量
     */
    private EnvironmentParameter ep;

    public StepLengthExport(EnvironmentParameter ep)
    {
        this.ep = ep;
    }

    /**
     * 输出步长文件
     */
    public void exportStepLengthFile()
    {
        String path = "";
        JFileDataStoreChooser chooser = new JFileDataStoreChooser("txt");
        chooser.setDialogTitle("Save trajectories' step length as txt file");
        if (StaticLib.InitialFileDirectory != null && StaticLib.InitialFileDirectory.exists())
        {
            if(StaticLib.InitialFileDirectory.isFile())
            {
                path=StaticLib.InitialFileDirectory.getParent()+"\\StepLength_File.txt";
            } else if(StaticLib.InitialFileDirectory.isDirectory())
            {
                path=StaticLib.InitialFileDirectory.getAbsolutePath()+"\\StepLength_File.txt";
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
                    double[] stepLengthList=trajectory.GetStepLengthList();
                    for(int i=0;i<stepLengthList.length;i++)
                    {
                        sb.append(stepLengthList[i]);
                        if(i!=stepLengthList.length-1)
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
                javax.swing.JOptionPane.showMessageDialog(null, "Step length exporting finished.", "Info", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }

        }
    }
}
