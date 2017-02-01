/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Export;

import Static.StaticLib;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * 用于生成统计图表的类
 * @author Tiger Shao
 */
public class StatisticGraphGen
{

    /**
     * 直方图样式代码
     */
    static final public int style_histgram = 0;
    /**
     * 折线型样式代码
     */
    static final public int style_lineChart = 1;
    /**
     * 绘制四周的留白空间比例
     */
    final private double percentageLeaveUnused = 0.1;
    /**
     * x轴最小值
     */
    private double bottom;
    /**
     * x轴相邻值的间距
     */
    private double interval;
    /**
     * 总的样本数量
     */
    private int totalSampleSize;
    /**
     * y轴值的集合
     */
    private double[] yList;
    /**
     * x轴值的集合
     */
    private double[] xList;
    /**
     * 背景颜色
     */
    private Color color_backGround;
    /**
     * 轴线及文字颜色
     */
    private Color color_axis;
    /**
     * 画线的颜色
     */
    private Color color_line;
    /**
     * 填充颜色
     */
    private Color color_fill;
    /**
     * 标记是否使用线光滑模式
     */
    private boolean isLineSmooth;
    /**
     * 折线和柱形图边框的线宽
     */
    private double lineWidth;
    /**
     * 轴线的线宽
     */
    private double axisLineWidth;
    /**
     * 线折点突出显示的半径
     */
    private double dotRadius;

    /**
     * 构造函数
     * @param Bottom
     * @param Interval
     * @param YList
     */
    public StatisticGraphGen(double Bottom, double Interval, double[] YList)
    {
        this.bottom = Bottom;
        this.interval = Interval;
        this.yList = YList;
        this.color_backGround = new Color(240, 240, 240);
        this.color_axis = Color.BLACK;
        this.color_line = Color.BLUE;
        this.color_fill = new Color(74, 138, 255);
        this.isLineSmooth = true;
        this.lineWidth = 1.0;
        this.axisLineWidth = 2.0;
        this.dotRadius = 3.0;
    }

    /**
     * 构造函数
     * @param XList
     * @param YList
     */
    public StatisticGraphGen(ArrayList<Double> XList, ArrayList<Double> YList)
    {
        this.bottom = Double.NaN;
        this.interval = Double.NaN;
        this.xList = new double[XList.size()];
        for (int i = 0; i < XList.size(); i++)
        {
            xList[i] = XList.get(i);
        }
        this.yList = new double[YList.size()];
        for (int i = 0; i < YList.size(); i++)
        {
            yList[i] = YList.get(i);
        }
        this.color_backGround = new Color(240, 240, 240);
        this.color_axis = Color.BLACK;
        this.color_line = Color.BLUE;
        this.color_fill = new Color(74, 138, 255);
        this.isLineSmooth = true;
        this.lineWidth = 1.0;
        this.axisLineWidth = 2.0;
        this.dotRadius = 3.0;
    }

    /**
     * 设置图表的背景颜色
     * @param BackGroundColor
     */
    public void setColor_backGround(Color BackGroundColor)
    {
        this.color_backGround = BackGroundColor;
    }

    /**
     * 设置轴线和标度的颜色
     * @param AxisColor
     */
    public void setColor_axis(Color AxisColor)
    {
        this.color_axis = AxisColor;
    }

    /**
     * 设置折线颜色
     * @param LineColor
     */
    public void setColor_line(Color LineColor)
    {
        this.color_line = LineColor;
    }

    /**
     * 设置柱形图内部填充颜色
     * @param FillColor
     */
    public void setColor_fill(Color FillColor)
    {
        this.color_fill = FillColor;
    }

    /**
     * 设定是否使用线光滑技术
     * @param isUseLineSmoothStyle
     */
    public void setUseLineSmoothStyle(boolean isUseLineSmoothStyle)
    {
        this.isLineSmooth = isUseLineSmoothStyle;
    }

    /**
     * 设定折线或柱形图边框的线宽
     * @param LineWidth
     */
    public void setLineWidth(double LineWidth)
    {
        this.lineWidth = LineWidth;
    }

    /**
     * 设定轴线的线宽
     * @param LineWidth
     */
    public void setAxisLineWidth(double AxisLineWidth)
    {
        this.axisLineWidth = AxisLineWidth;
    }

    /**
     * 设定线折点出突出显示点的宽度
     * @param Radius
     */
    public void setDotRadius(double Radius)
    {
        this.dotRadius = Radius;
    }

    /**
     * 设置默认样式
     * @param GraphicStyle
     */
    public void setDefaultStyle(int graphicStyle)
    {
        this.default_style = graphicStyle;
    }

    /**
     * 统一设置绘制样式
     * @param schema
     */
    public void setSchema(StatisticGraphGenSchema schema)
    {
        this.setAxisLineWidth(schema.axisLineWidth);
        this.setColor_axis(schema.color_axis);
        this.setColor_backGround(schema.color_backGround);
        this.setColor_fill(schema.color_fill);
        this.setColor_line(schema.color_line);
        this.setDefaltLogScale(schema.isXAxisLogStyle, schema.isYAxisLogStyle);
        this.setDefaultStyle(schema.style);
        this.setDotRadius(schema.dotRadius);
        this.setLineWidth(schema.lineWidth);
        this.setUseLineSmoothStyle(schema.isUseLineSmoothStyle);
    }

    /**
     * 设定默认 X,Y轴是否用Log标度
     * @param isXAxisLogStyle
     * @param isYAxisLogStyle
     */
    public void setDefaltLogScale(boolean isXAxisLogStyle, boolean isYAxisLogStyle)
    {
        this.default_isXAxisLogStyle = isXAxisLogStyle;
        this.default_isYAxisLogStyle = isYAxisLogStyle;
    }
    private BufferedImage default_graph = null;
    private int default_style = -1;
    private boolean default_isXAxisLogStyle = false;
    private boolean default_isYAxisLogStyle = false;

    /**
     * 产生柱形统计图
     * @param graphWidth
     * @param graphHeight
     * @param isYAxisLogStyle
     * @return
     */
    public BufferedImage generateGraphAsHistogram(int graphWidth, int graphHeight, boolean isYAxisLogStyle)
    {
        if (this.xList != null)
        {
            return null;
        }
        if (this.bottom == Double.NaN || this.interval == Double.NaN)
        {
            return null;
        }

        if (this.default_style == StatisticGraphGen.style_histgram)
        {
            if (this.default_graph != null && this.default_graph.getWidth() == graphWidth && this.default_graph.getHeight() == graphHeight && this.default_isYAxisLogStyle == isYAxisLogStyle)
            {
                return this.default_graph;
            }
        }

        BufferedImage pic = new BufferedImage(graphWidth, graphHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) pic.getGraphics();
        graphics.setColor(this.color_backGround);
        graphics.fillRect(0, 0, graphWidth, graphHeight);

//        if (this.isLineSmooth)
//        {
//            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        }
        int p_left = (int) (graphWidth * this.percentageLeaveUnused);
        int p_right = graphWidth - p_left;
        int p_top = (int) (graphHeight * this.percentageLeaveUnused);
        int p_bottom = graphHeight - p_top;
        int span_horizon = p_right - p_left;
        int span_vertical = p_bottom - p_top;

        //1.绘制直方矩形
        //画每个格子主要要确定左上点和宽高
        //此处x轴必须用bottom和interval
        if (this.bottom == Double.NaN || this.interval == Double.NaN)
        {
            return null;
        }

        BasicStroke pen = new BasicStroke((float) this.lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        graphics.setStroke(pen);
        double yMax = Double.NEGATIVE_INFINITY;
        double yMin = Double.POSITIVE_INFINITY;
        double span_yValue = Double.NaN;
        if (isYAxisLogStyle)//y轴单log
        {
            //提取数据中的极值
            for (double d : this.yList)
            {
                if (yMax < d)
                {
                    yMax = d;
                }
                if (yMin > d && d > 0)
                {
                    yMin = d;
                }
            }
            int minInt_y = (int) Math.log10(yMin);
            if (minInt_y <= 0)
            {
                minInt_y -= 1;
            }
            int maxInt_y = (int) Math.log10(yMax);
            if (maxInt_y > 0)
            {
                maxInt_y += 1;
            }
            span_yValue = maxInt_y - minInt_y;
            for (int i = 0; i < this.yList.length; i++)
            {
                int x0 = (int) (((double) i / (double) this.yList.length) * span_horizon) + p_left;
                int x1 = (int) (((double) (i + 1) / (double) this.yList.length) * span_horizon) + p_left;
                int histWidth = x1 - x0;
                if (this.yList[i] <= 0)
                {
                    graphics.setColor(this.color_axis);
                    graphics.drawLine(x0 + histWidth, p_bottom, x0 + histWidth, p_bottom - 4);
                    continue;
                }
                int histHight = (int) ((Math.log10(this.yList[i]) - (double) minInt_y) / span_yValue * span_vertical);
                int y0 = p_bottom - histHight;
                graphics.setColor(this.color_fill);
                graphics.fillRect(x0, y0, histWidth, histHight);
                graphics.setColor(this.color_line);
                graphics.drawRect(x0, y0, histWidth, histHight);
                graphics.setColor(this.color_axis);
                graphics.drawLine(x0 + histWidth, p_bottom, x0 + histWidth, p_bottom - 4);
            }

        } else
        {
            //提取数据中的极值
            yMax = Double.NEGATIVE_INFINITY;
            yMin = Double.POSITIVE_INFINITY;
            for (double d : this.yList)
            {
                if (yMax < d)
                {
                    yMax = d;
                }
                if (yMin > d)
                {
                    yMin = d;
                }
            }
            span_yValue = yMax - yMin;
            for (int i = 0; i < this.yList.length; i++)
            {
                int x0 = (int) (((double) i / (double) this.yList.length) * span_horizon) + p_left;
                int x1 = (int) (((double) (i + 1) / (double) this.yList.length) * span_horizon) + p_left;
                int histWidth = x1 - x0;
                int histHight = (int) ((this.yList[i] - yMin) / span_yValue * span_vertical);//+histHight_min;
                int y0 = p_bottom - histHight;
                graphics.setColor(this.color_fill);
                graphics.fillRect(x0, y0, histWidth, histHight);
                graphics.setColor(this.color_line);
                graphics.drawRect(x0, y0, histWidth, histHight);
                graphics.setColor(this.color_axis);
                graphics.drawLine(x0 + histWidth, p_bottom, x0 + histWidth, p_bottom - 4);
            }
        }

        // 2.绘制轴线
        pen = new BasicStroke((float) this.axisLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        graphics.setStroke(pen);
        graphics.setColor(this.color_axis);
        graphics.setPaint(this.color_axis);
        int x0 = p_left;
        int y0 = p_bottom;
        // 2.1绘制x轴
        int x1 = graphWidth - p_left / 2;
        int y1 = y0;
        graphics.drawLine(x0, y0, x1, y1);
        int xTr0 = (int) (x1 - 3 * this.axisLineWidth);
        int yTr0 = (int) (y1 + 2 * this.axisLineWidth);
        int xTr1 = xTr0;
        int yTr1 = (int) (y1 - 2 * this.axisLineWidth);
        int xTr2 = (int) (x1 + 3 * this.axisLineWidth);
        int yTr2 = y1;
        int[] xPoints = new int[]
        {
            xTr0, xTr1, xTr2
        };
        int[] yPoints = new int[]
        {
            yTr0, yTr1, yTr2
        };
        graphics.fillPolygon(xPoints, yPoints, 3);
        // 2.2绘制y轴
        int x2 = x0;
        int y2 = p_top / 2;
        graphics.drawLine(x0, y0, x2, y2);
        xPoints[0] = (int) (x2 - 3 * this.axisLineWidth);
        yPoints[0] = (int) (y2 + 3 * this.axisLineWidth);
        xPoints[1] = (int) (x2 + 2 * this.axisLineWidth);
        yPoints[1] = yPoints[0];
        xPoints[2] = x2;
        yPoints[2] = (int) (y2 - 3 * this.axisLineWidth);
        graphics.fillPolygon(xPoints, yPoints, 3);

        //3.绘制标尺
        //3.1绘制X轴标尺
        double span_xValue = this.interval * this.yList.length;
        double xMax = this.bottom + this.interval * this.yList.length;
        double xMin = this.bottom;
        int e = StaticLib.getDoubleEValue(span_xValue);
        double xInterval = Math.pow(10, e);
        double startXValue = ((int) (xMax / xInterval) * xInterval);
        pen = new BasicStroke((float) this.axisLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        graphics.setStroke(pen);
        for (double xValue = startXValue; xValue > xMin; xValue -= xInterval)
        {
            int histWidth = (int) ((xValue - xMin) / span_xValue * span_horizon);//+histHight_min;
            int pX = p_left + histWidth;
            graphics.drawLine(pX, p_bottom, pX, p_bottom - 15);

            FontMetrics fontMetrics = graphics.getFontMetrics();
            String label = String.valueOf(xValue);
            int xBias = fontMetrics.charsWidth(label.toCharArray(), 0, label.length()) / 2;
            int yBias = fontMetrics.getHeight();
            graphics.drawString(label, pX - xBias, p_bottom + yBias);

            if (xValue == startXValue)
            {
                for (int i = 1; i < 10; i++)
                {
                    double value = xValue + ((double) i * xInterval / 10);
                    if (value <= xMax)
                    {
                        histWidth = (int) ((value - xMin) / span_xValue * span_horizon);//+histHight_min;
                        pX = p_left + histWidth;
                        graphics.drawLine(pX, p_bottom, pX, p_bottom - 5);
                    }
                }
            }
            for (int i = 1; i < 10; i++)
            {
                double value = xValue - ((double) i * xInterval / 10);
                if (value > xMin)
                {
                    histWidth = (int) ((value - xMin) / span_xValue * span_horizon);//+histHight_min;
                    pX = p_left + histWidth;
                    graphics.drawLine(pX, p_bottom, pX, p_bottom - 5);
                }
            }
        }
        //3.2绘制Y轴标尺
        if (isYAxisLogStyle)
        {
            int minInt_y = (int) Math.log10(yMin);
            if (minInt_y <= 0)
            {
                minInt_y -= 1;
            }
            int maxInt_y = (int) Math.log10(yMax);
            if (maxInt_y > 0)
            {
                maxInt_y += 1;
            }
            graphics.setColor(this.color_axis);
            pen = new BasicStroke((float) this.axisLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(pen);
            for (int i = maxInt_y; i > minInt_y; i--)
            {
                int height = (int) ((double) (i - minInt_y) / span_yValue * span_vertical);
                y0 = p_bottom - height;
                graphics.drawLine(p_left, y0, p_left + 15, y0);

                FontMetrics fontMetrics = graphics.getFontMetrics();
                String label = "10E" + String.valueOf(i);
                int xBias = fontMetrics.charsWidth(label.toCharArray(), 0, label.length());
                int yBias = fontMetrics.getHeight() / 3;
                if (p_left >= xBias)
                {
                    graphics.drawString(label, p_left - xBias - (int) this.dotRadius, y0 + yBias);
                } else
                {
                    graphics.drawString(label, 0, y0 + yBias);
                }

                for (int j = 1; j < 9; j++)
                {
                    double yValue = Math.pow(10, i) - Math.pow(10, i - 1) * j;
                    int histHight = (int) ((Math.log10(yValue) - (double) minInt_y) / span_yValue * span_vertical);
                    y0 = p_bottom - histHight;
                    graphics.drawLine(p_left, y0, p_left + 5, y0);
                }
            }

        } else
        {
            e = StaticLib.getDoubleEValue(span_yValue);
            double yInterval = Math.pow(10, e);
            double startYValue = ((int) (yMax / yInterval) * yInterval);
            pen = new BasicStroke((float) this.axisLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(pen);

            int label_yValue = (int) (yMax / yInterval);//特殊字段，用于产生标度字段，防止产生诸如0.2000000001或0.79999999998的情况
            for (double yValue = startYValue; yValue > yMin; yValue -= yInterval)
            {
                int histHight = (int) ((yValue - yMin) / span_yValue * span_vertical);//+histHight_min;
                y0 = p_bottom - histHight;
                graphics.drawLine(p_left, y0, p_left + 15, y0);

                FontMetrics fontMetrics = graphics.getFontMetrics();
                String label = "";
                if (yInterval >= 1.0)
                {
                    label = String.valueOf((double) (label_yValue) * yInterval);
                } else
                {
                    label = String.valueOf((double) (label_yValue) / (int) (1 / yInterval));
                }
                label_yValue--;
                int xBias = fontMetrics.charsWidth(label.toCharArray(), 0, label.length());
                int yBias = fontMetrics.getHeight() / 3;
                if (p_left >= xBias)
                {
                    graphics.drawString(label, p_left - xBias - (int) this.dotRadius, y0 + yBias);
                } else
                {
                    graphics.drawString(label, 0, y0 + yBias);
                }


                for (int i = 1; i < 10; i++)
                {
                    double value = yValue - ((double) i * yInterval / 10);
                    if (value > yMin)
                    {
                        histHight = (int) ((value - yMin) / span_yValue * span_vertical);//+histHight_min;
                        y0 = p_bottom - histHight;
                        graphics.drawLine(p_left, y0, p_left + 5, y0);
                    }
                }
                if (yValue == startYValue)
                {
                    for (int i = 1; i < 10; i++)
                    {
                        double value = yValue + ((double) i * yInterval / 10);
                        if (value <= yMax)
                        {
                            histHight = (int) ((value - yMin) / span_yValue * span_vertical);//+histHight_min;
                            y0 = p_bottom - histHight;
                            graphics.drawLine(p_left, y0, p_left + 5, y0);
                        }
                    }
                }
            }
        }

        graphics.dispose();

        this.default_graph = pic;
        this.default_style = this.style_histgram;
        this.default_isYAxisLogStyle = isYAxisLogStyle;

        return pic;
    }

    /**
     * 产生折线型统计图
     * @param graphWidth
     * @param graphHeight
     * @param isXAxisLogStyle
     * @param isYAxisLogStyle
     * @return
     */
    public BufferedImage generateGraphAsLineChart(int graphWidth, int graphHeight, boolean isXAxisLogStyle, boolean isYAxisLogStyle)
    {

        if (this.default_style == StatisticGraphGen.style_lineChart)
        {
            if (this.default_graph != null && this.default_graph.getWidth() == graphWidth && this.default_graph.getHeight() == graphHeight && this.default_isXAxisLogStyle == isXAxisLogStyle && this.default_isYAxisLogStyle == isYAxisLogStyle)
            {
                return this.default_graph;
            }
        }
        BufferedImage pic = new BufferedImage(graphWidth, graphHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) pic.getGraphics();
        graphics.setColor(this.color_backGround);
        graphics.fillRect(0, 0, graphWidth, graphHeight);

        if (this.isLineSmooth)
        {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        int p_left = (int) (graphWidth * this.percentageLeaveUnused);
        int p_right = graphWidth - p_left;
        int p_top = (int) (graphHeight * this.percentageLeaveUnused);
        int p_bottom = graphHeight - p_top;
        int span_horizon = p_right - p_left;
        int span_vertical = p_bottom - p_top;

        //1.绘制折线部分 
        BasicStroke pen = new BasicStroke((float) this.lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        graphics.setStroke(pen);
        double yMax = Double.NEGATIVE_INFINITY;
        double yMin = Double.POSITIVE_INFINITY;
        double span_yvalue = Double.NaN;
        if (isYAxisLogStyle)//如果Y轴用log表现
        {
            //提取数据中的极值
            for (double d : this.yList)
            {
                if (yMax < d)
                {
                    yMax = d;
                }
                if (yMin > d && d > 0)
                {
                    yMin = d;
                }
            }
            int minInt_y = (int) Math.log10(yMin);
            if (minInt_y <= 0)
            {
                minInt_y--;
            }
            int maxInt_y = (int) Math.log10(yMax);
            yMin = minInt_y;
            yMax = maxInt_y;
        } else//如果Y轴正常表现
        {
            for (double d : this.yList)
            {
                if (yMax < d)
                {
                    yMax = d;
                }
                if (yMin > d)
                {
                    yMin = d;
                }
            }
        }
        span_yvalue = yMax - yMin;
        double xMax = Double.NEGATIVE_INFINITY;
        double xMin = Double.POSITIVE_INFINITY;
        double span_xvalue = Double.NaN;
        if (isXAxisLogStyle)//如果X轴用log表现
        {
            if (this.xList != null)
            {
                xMin = Math.log10(this.xList[0]);
                xMax = Math.log10(this.xList[this.xList.length - 1]);
            } else
            {
                double min = this.bottom + this.interval / 2;
                xMin = Math.log10(min);
                xMax = Math.log10(this.bottom + this.interval * this.yList.length - this.interval / 2);
            }
        } else
        {
            if (this.xList != null)
            {
                xMin = this.xList[0];
                xMax = this.xList[this.xList.length - 1];
            } else
            {
                xMin = this.bottom + this.interval / 2;
                xMax = this.bottom + this.interval * this.yList.length - this.interval / 2;
            }
        }
        span_xvalue = xMax - xMin;
        Point pStart = new Point(p_left, p_bottom);//定义起始点
        //此处开始逐一画线
        for (int i = 0; i < this.yList.length; i++)
        {
            double yValue = this.yList[i];
            double xValue = Double.NaN;
            if (this.xList != null)
            {
                xValue = this.xList[i];
            } else
            {
                xValue = this.bottom + i * this.interval + this.interval / 2;
                if (xValue < xMin)
                {
                    xValue = xMin;
                }
            }
            int pX = 0;
            int pY = 0;
            if (isXAxisLogStyle)//X轴用log表示
            {
                pX = (int) ((Math.log10(xValue) - xMin) / span_xvalue * span_horizon) + p_left;
            } else
            {
                pX = (int) ((xValue - xMin) / span_xvalue * span_horizon) + p_left;
            }

            if (isYAxisLogStyle)//Y轴用log表示
            {
                if (yValue < Math.pow(10.0, yMin))//防止出现yValue=0的情况
                {
                    continue;
                }
                int histHight = (int) ((Math.log10(yValue) - yMin) / span_yvalue * span_vertical);
                pY = p_bottom - histHight;
            } else
            {
                int histHight = (int) ((yValue - yMin) / span_yvalue * span_vertical);
                pY = p_bottom - histHight;
            }
            Point pEnd = new Point(pX, pY);
            graphics.setColor(this.color_fill);
            graphics.fillOval((int) (pX - this.dotRadius), (int) (pY - this.dotRadius), (int) (2 * this.dotRadius + 1), (int) (2 * this.dotRadius + 1));
            graphics.setColor(this.color_line);
            if (i != 0)
            {
                graphics.drawLine(pStart.x, pStart.y, pEnd.x, pEnd.y);
            }
            pStart = pEnd;
        }

        // 2.绘制轴线
        pen = new BasicStroke((float) this.axisLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        graphics.setStroke(pen);
        graphics.setColor(this.color_axis);
        int x0 = p_left;
        int y0 = p_bottom;
        // 2.1绘制x轴
        int x1 = graphWidth - p_left / 2;
        int y1 = y0;
        graphics.drawLine(x0, y0, x1, y1);
        int xTr0 = (int) (x1 - 3 * this.axisLineWidth);
        int yTr0 = (int) (y1 + 2 * this.axisLineWidth);
        int xTr1 = xTr0;
        int yTr1 = (int) (y1 - 2 * this.axisLineWidth);
        int xTr2 = (int) (x1 + 3 * this.axisLineWidth);
        int yTr2 = y1;
        int[] xPoints = new int[]
        {
            xTr0, xTr1, xTr2
        };
        int[] yPoints = new int[]
        {
            yTr0, yTr1, yTr2
        };
        graphics.fillPolygon(xPoints, yPoints, 3);
        // 2.2绘制y轴
        int x2 = x0;
        int y2 = p_top / 2;
        graphics.drawLine(x0, y0, x2, y2);
        xPoints[0] = (int) (x2 - 3 * this.axisLineWidth);
        yPoints[0] = (int) (y2 + (3 + 1) * this.axisLineWidth);
        xPoints[1] = (int) (x2 + 2 * this.axisLineWidth);
        yPoints[1] = yPoints[0];
        xPoints[2] = x2;
        yPoints[2] = (int) (y2 - 3 * this.axisLineWidth);
        graphics.fillPolygon(xPoints, yPoints, 3);

        //3.绘制标尺
        //3.1绘制X轴标尺
        if (isXAxisLogStyle)
        {
            graphics.setColor(this.color_axis);
            pen = new BasicStroke((float) this.axisLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(pen);
            for (int i = (int) xMax; i > (int) xMin; i--)
            {
                int width = (int) ((double) (i - xMin) / span_xvalue * span_horizon);
                int pX = p_left + width;
                graphics.drawLine(pX, p_bottom, pX, p_bottom - 15);

                FontMetrics fontMetrics = graphics.getFontMetrics();
                String label = "10E" + String.valueOf(i);
                int xBias = fontMetrics.charsWidth(label.toCharArray(), 0, label.length()) / 2;
                int yBias = fontMetrics.getHeight();
                graphics.drawString(label, pX - xBias, p_bottom + yBias);

                for (int j = 1; j < 9; j++)
                {
                    double xValue = Math.pow(10, i) - Math.pow(10, i - 1) * j;
                    width = (int) ((Math.log10(xValue) - xMin) / span_xvalue * span_horizon);
                    if (width < 0)
                    {
                        break;
                    }
                    pX = p_left + width;
                    graphics.drawLine(pX, p_bottom, pX, p_bottom - 5);
                }
                if (i == (int) xMax)
                {
                    for (int j = 1; j < 10; j++)
                    {
                        double xValue = Math.pow(10, i) + Math.pow(10, i) * j;//由于是针对最大值作图，此处是i而不是i-1
                        width = (int) ((Math.log10(xValue) - xMin) / span_xvalue * span_horizon);
                        pX = p_left + width;
                        if (pX > graphWidth - p_left / 2 - 5)
                        {
                            break;
                        }
                        graphics.drawLine(pX, p_bottom, pX, p_bottom - 5);
                    }
                }
            }
        } else
        {
            int e = StaticLib.getDoubleEValue(span_xvalue);
            double xInterval = Math.pow(10, e);
            double startXValue = ((int) (xMax / xInterval) * xInterval);
            pen = new BasicStroke((float) this.axisLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(pen);
            for (double xValue = startXValue; xValue > xMin; xValue -= xInterval)
            {
                int histWidth = (int) ((xValue - xMin) / span_xvalue * span_horizon);//+histHight_min;
                int pX = p_left + histWidth;
                graphics.drawLine(pX, p_bottom, pX, p_bottom - 15);

                FontMetrics fontMetrics = graphics.getFontMetrics();
                String label = String.valueOf(xValue);
                int xBias = fontMetrics.charsWidth(label.toCharArray(), 0, label.length()) / 2;
                int yBias = fontMetrics.getHeight();
                graphics.drawString(label, pX - xBias, p_bottom + yBias);

                if (xValue == startXValue)
                {
                    for (int i = 1; i < 10; i++)
                    {
                        double value = xValue + ((double) i * xInterval / 10);
                        if (value <= xMax)
                        {
                            histWidth = (int) ((value - xMin) / span_xvalue * span_horizon);//+histHight_min;
                            pX = p_left + histWidth;
                            graphics.drawLine(pX, p_bottom, pX, p_bottom - 5);
                        }
                    }
                }
                for (int i = 1; i < 10; i++)
                {
                    double value = xValue - ((double) i * xInterval / 10);
                    if (value > xMin)
                    {
                        histWidth = (int) ((value - xMin) / span_xvalue * span_horizon);//+histHight_min;
                        pX = p_left + histWidth;
                        graphics.drawLine(pX, p_bottom, pX, p_bottom - 5);
                    }
                }
            }
        }

        //3.2绘制Y轴标尺
        if (isYAxisLogStyle)
        {
            graphics.setColor(this.color_axis);
            pen = new BasicStroke((float) this.axisLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(pen);
            for (int i = (int) yMax; i > (int) yMin; i--)
            {
                int height = (int) ((double) (i - yMin) / span_yvalue * span_vertical);
                int pY = p_bottom - height;
                graphics.drawLine(p_left, pY, p_left + 15, pY);

                FontMetrics fontMetrics = graphics.getFontMetrics();
                String label = "10E" + String.valueOf(i);
                int xBias = fontMetrics.charsWidth(label.toCharArray(), 0, label.length());
                int yBias = fontMetrics.getHeight() / 3;
                if (p_left >= xBias)
                {
                    graphics.drawString(label, p_left - xBias - (int) this.dotRadius, pY + yBias);
                } else
                {
                    graphics.drawString(label, 0, pY + yBias);
                }

                for (int j = 1; j < 9; j++)
                {
                    double yValue = Math.pow(10, i) - Math.pow(10, i - 1) * j;
                    int histHight = (int) ((Math.log10(yValue) - (double) yMin) / span_yvalue * span_vertical);
                    pY = p_bottom - histHight;
                    graphics.drawLine(p_left, pY, p_left + 5, pY);
                }
            }
        } else
        {
            int e = StaticLib.getDoubleEValue(span_yvalue);
            double yInterval = Math.pow(10, e);
            double startYValue = ((int) (yMax / yInterval) * yInterval);
            pen = new BasicStroke((float) this.axisLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(pen);
            int label_yValue = (int) (yMax / yInterval);//特殊字段，用于产生标度字段，防止产生诸如0.2000000001或0.79999999998的情况
            for (double yValue = startYValue; yValue > yMin; yValue -= yInterval)
            {
                int histHight = (int) ((yValue - yMin) / span_yvalue * span_vertical);//+histHight_min;
                int pY = p_bottom - histHight;
                graphics.drawLine(p_left, pY, p_left + 15, pY);


                FontMetrics fontMetrics = graphics.getFontMetrics();
                String label = "";
                if (yInterval >= 1.0)
                {
                    label = String.valueOf((double) (label_yValue) * yInterval);
                } else
                {
                    label = String.valueOf((double) (label_yValue) / (int) (1 / yInterval));
                }
                label_yValue--;
                int xBias = fontMetrics.charsWidth(label.toCharArray(), 0, label.length());
                int yBias = fontMetrics.getHeight() / 3;
                if (p_left >= xBias)
                {
                    graphics.drawString(label, p_left - xBias - (int) this.dotRadius, pY + yBias);
                } else
                {
                    graphics.drawString(label, 0, pY + yBias);
                }

                if (yValue == startYValue)
                {
                    for (int i = 1; i < 10; i++)
                    {
                        double value = yValue + ((double) i * yInterval / 10);
                        if (value <= yMax)
                        {
                            histHight = (int) ((value - yMin) / span_yvalue * span_vertical);//+histHight_min;
                            pY = p_bottom - histHight;
                            graphics.drawLine(p_left, pY, p_left + 5, pY);
                        }
                    }
                }
                for (int i = 1; i < 10; i++)
                {
                    double value = yValue - ((double) i * yInterval / 10);
                    if (value > yMin)
                    {
                        histHight = (int) ((value - yMin) / span_yvalue * span_vertical);//+histHight_min;
                        pY = p_bottom - histHight;
                        graphics.drawLine(p_left, pY, p_left + 5, pY);
                    }
                }
            }
        }


        graphics.dispose();

        this.default_graph = pic;
        this.default_style = this.style_lineChart;
        this.default_isXAxisLogStyle = isXAxisLogStyle;
        this.default_isYAxisLogStyle = isYAxisLogStyle;


        return pic;
    }

    /**
     * 获取默认样式的统计图
     * @param width
     * @param height
     * @return
     */
    public BufferedImage generateDefaultGraph(int width, int height)
    {
        if (this.default_style == StatisticGraphGen.style_histgram)
        {
            return this.generateGraphAsHistogram(width, height, default_isYAxisLogStyle);
        } else if (this.default_style == StatisticGraphGen.style_lineChart)
        {
            return this.generateGraphAsLineChart(width, height, default_isXAxisLogStyle, default_isYAxisLogStyle);
        } else
        {
            return null;
        }
    }
}
