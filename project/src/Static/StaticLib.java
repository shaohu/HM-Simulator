/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Static;

import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;

/**
 * 用于存储一些本程序应用的到的基本控制变量
 * @author Tiger Shao
 */
public class StaticLib
{

    /**
     * 时间的基本文本格式 (yyyy-MM-dd hh:mm:ss)
     */
    final public static SimpleDateFormat LevyFlightDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    /**
     * 表示没有时间记录
     */
    final public static String NoTimeString = "null";
    /**
     * shp中开始时间字段标准名称
     */
    final public static String BeginTimeFieldString = "BeginTime";
    /**
     * shp中重访点的字段标准名称
     */
    final public static String RevistingProbabilityFieldString = "RevistingProbability";
    /**
     * 用于显示字体的较深的绿色
     */
    final public static Color fontGreenColor = new Color(0, 102, 0);
    /**
     * 打开文件的初始路径
     */
    public static File InitialFileDirectory = null;
    /**
     * shp中地理对象中心X的字段标准名称
     */
    final public static String CenterX = "CenterX";
    /**
     * shp中地理对象中心Y的字段标准名称
     */
    final public static String CenterY = "CenterY";
    /**
     * shp中椭圆半长轴的字段标准名称
     */
    final public static String MajorSemiAxis = "MajorSemiAxis";
    /**
     * shp中椭圆半短轴的字段标准名称
     */
    final public static String MinorSemiAxis = "MinorSemiAxis";
    /**
     * shp中旋转角度的字段标准名称
     */
    final public static String Rotation = "Rotation";
    /**
     * shp中对应轨迹的字段标准名称
     */
    final public static String TrajectoryID = "TraID";
    /**
     * 用于控制轨迹生成边界的多边形图层名称
     */
    final public static String PolygonRegionLayerName="polygonconstraint";
    /**
     * 默认的加密点的个数
     */
    final public static int encryptionNum = 1000;

    /**
     * 获取一个值的有效数字位数
     * @param value
     * @return
     */
    public static int getDoubleEValue(double value)
    {
        value = Math.abs(value);
        double testValue = 1.0;
        int E = 0;
        if (value == testValue)
        {
            return E;
        } else if (value < testValue)
        {
            while (testValue > value)
            {
                testValue /= 10;
                E--;
            }
            return E;
        } else
        {
            testValue *= 10;
            while (testValue < value)
            {
                testValue *= 10;
                E++;
            }
            return E;
        }
    }
}
