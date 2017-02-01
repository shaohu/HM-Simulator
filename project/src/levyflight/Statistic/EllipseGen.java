/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Statistic;

import com.vividsolutions.jts.geom.Coordinate;

/**
 *生成椭圆的相应的类
 * @author Tiger Shao
 */
public class EllipseGen
{

    /**
     * 根据参数产生特定椭圆上的一系列点
     * @param majorSemiAxis 长半轴
     * @param minorSemiAxis 短半轴
     * @param centerX 中心点X
     * @param centerY 中心点Y
     * @param rotateionAngle 旋转角度
     * @param pointsNum 生成的点的个数
     * @return
     */
    public static Coordinate[] generateEllipse(double majorSemiAxis, double minorSemiAxis, double centerX, double centerY, double rotateionAngle, int pointsNum)
    {
        Coordinate[] ellipse = new Coordinate[pointsNum+1];

        double x0 = 0, y0 = 0;
        double x = 0, y = 0, angle = 0;
        final double sin = Math.sin(rotateionAngle);
        final double cos = Math.cos(rotateionAngle);

        for (int i = 0; i < pointsNum; i++)
        {
            //求角度
            angle = 2 * Math.PI * (double) i / pointsNum;

            //先根据椭圆参数方程求点坐标
            x0 = majorSemiAxis * Math.cos(angle);
            y0 = minorSemiAxis * Math.sin(angle);

            //再旋转
            x = x0 * cos - y0 * sin;
            y = x0 * sin - y0 * cos;

            //再平移
            x += centerX;
            y += centerY;

            ellipse[i]=new Coordinate(x,y);
        }

        ellipse[pointsNum]=new Coordinate(ellipse[0].x,ellipse[0].y);

        return ellipse;
    }
}
