/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Export;

import java.awt.Color;

/**
 *
 * @author wu
 */
public class StatisticGraphGenSchema
{

    /**
     *标识图表类型的字段。来源于StatisticGraphicGen的static
     */
    public int style = 1;
    /**
     * 背景颜色
     */
    public Color color_backGround;
    /**
     * 轴线颜色
     */
    public Color color_axis;
    /**
     * 绘制线的颜色
     */
    public Color color_line;
    /**
     * 填充色
     */
    public Color color_fill;
    /**
     * 是否使用消锯齿技术
     */
    public boolean isUseLineSmoothStyle;
    /**
     * 线宽
     */
    public double lineWidth;
    /**
     * 轴线宽度
     */
    public double axisLineWidth;
    /**
     * 小圆点半径
     */
    public double dotRadius;
    /**
     * X轴是否要使用log形式
     */
    public boolean isXAxisLogStyle;
    /**
     * Y轴是否要使用log形式
     */
    public boolean isYAxisLogStyle;

    public StatisticGraphGenSchema()
    {
        this.color_backGround = new Color(240, 240, 240);
        this.color_axis = Color.BLACK;
        this.color_line = Color.BLUE;
        this.color_fill = new Color(74, 138, 255);
        this.isUseLineSmoothStyle = true;
        this.lineWidth = 1.0;
        this.axisLineWidth = 2.0;
        this.dotRadius = 3.0;
        this.style = StatisticGraphGen.style_lineChart;
        this.isXAxisLogStyle = true;
        this.isYAxisLogStyle = true;
    }

    /**
     * 将本实例中的所有变量值复制到目标变量中
     * @param target
     */
    public void copyTo(StatisticGraphGenSchema target)
    {
        target.axisLineWidth = this.axisLineWidth;
        target.color_axis = this.color_axis;
        target.color_backGround = this.color_backGround;
        target.color_fill = this.color_fill;
        target.color_line = this.color_line;
        target.dotRadius = this.dotRadius;
        target.isUseLineSmoothStyle = this.isUseLineSmoothStyle;
        target.isXAxisLogStyle = this.isXAxisLogStyle;
        target.isYAxisLogStyle = this.isYAxisLogStyle;
        target.lineWidth = this.lineWidth;
        target.style = this.style;
    }
}
