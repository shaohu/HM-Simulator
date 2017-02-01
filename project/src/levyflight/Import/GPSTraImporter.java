/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Import;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import levyflight.Data.Node;
import levyflight.Data.TimePrecisionEnum;
import levyflight.Data.Trajectory;
import levyflight.Data.TrajectoryPool;
import levyflight.LevyFlightView;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * 用于导入GPS轨迹路径的类
 * Here we will transform geographic coordinates data (GPS data in WGS84 who's unit are longitude and latitude) to projected coordinates (the axis to be easting and northing).
 * @author Tiger Shao
 * 该类暂时认定所有坐标点均为2维
 */
public class GPSTraImporter extends TrajectoryGenerator
{

    /**
     * gps文件路径
     */
    private String FilePath;
    /**
     * 坐标转换类
     */
    private MathTransform transformer;

    public GPSTraImporter(String GPSFilePath, CoordinateReferenceSystem coordinateReferenceSystem)
    {
        this.FilePath = GPSFilePath;
        try
        {
            MathTransform t = CRS.findMathTransform(DefaultGeographicCRS.WGS84, coordinateReferenceSystem, true);
            this.transformer = t;
        } catch (FactoryException ex)
        {
            Logger.getLogger(GPSTraImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Trajectory GetNextTrajectory()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //转换用示例程序
    private ArrayList<String> convert(String path)
    {
        TrajectoryPool tPool = new TrajectoryPool(null,TimePrecisionEnum.SECOND);
        SHPTraImporter sImporter = new SHPTraImporter(path);
        while (sImporter.HasNext)
        {
            tPool.AddTrajectory(sImporter.GetNextTrajectory());
        }
        tPool.CoordinateReference = sImporter.CoordinateReference;
        try
        {
            CoordinateReferenceSystem crs = CRS.parseWKT(tPool.CoordinateReference.toWKT());
        } catch (FactoryException ex)
        {
            Logger.getLogger(LevyFlightView.class.getName()).log(Level.SEVERE, null, ex);
        }
        MathTransform t = null;
        try
        {
            t = CRS.findMathTransform(tPool.CoordinateReference, DefaultGeographicCRS.WGS84, true);
            //JOptionPane.showMessageDialog(this.mainPanel, String.valueOf(t.getSourceDimensions())+"||"+String.valueOf(t.getTargetDimensions()));
            //JOptionPane.showMessageDialog(this.mainPanel, t.toWKT());

        } catch (FactoryException ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            // Logger.getLogger(LevyFlightView.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<String> result = new ArrayList<String>();
        result.add(t.toWKT());

        for (Trajectory tr : tPool.TrajectorySet)
        {

            for (Node node : tr.NodeList)
            {
                double[] target = new double[]
                {
                    0, 0
                };
                try
                {
                    t.transform(new double[]
                            {
                                node.getX(), node.getY()
                            }, 0, target, 0, 1);
                } catch (TransformException ex)
                {
                    Logger.getLogger(GPSTraImporter.class.getName()).log(Level.SEVERE, null, ex);
                }

                result.add("|");
                result.add(node.toString());
                result.add(String.valueOf(target[0]) + " , " + String.valueOf(target[1]));
            }
        }
        return (result);
    }
}
