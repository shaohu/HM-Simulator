/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levyflight.Data;

import com.vividsolutions.jts.geom.Coordinate;
import java.io.File;
import java.util.ArrayList;
import org.geotools.coverage.grid.GridCoordinates2D;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.DataSourceException;
import org.geotools.factory.Hints;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.opengis.geometry.DirectPosition;

/**
 *用于存储产场数据的类
 * @author Tiger Shao
 */
public class Field
{

    public static Field newGeotiffField(GridCoverage2D coverage, String path)
    {
        Field field = new Field(coverage, path);
        field.Type = Field.FieldType_GEOTIFF;
        return field;
    }

    public static Field newASCIIField(GridCoverage2D coverage, String path, Hints crsHints)
    {
        Field field = new Field(coverage, path);
        field.CRSHints = crsHints;
        field.Type = Field.FieldType_ASCII;
        return field;
    }
    /**
     * 标识field的原数据类型为图片类型
     */
    final public static int FieldType_GEOTIFF = 0;
    /**
     * 标识field的原数据类型为ASCII类型
     */
    final public static int FieldType_ASCII = 1;
    /**
     * 二维栅格模型
     */
    public GridCoverage2D GridCoverage;
    /**
     * 栅格文件的绝对路径
     */
    public String AbsolutePath;
    /**
     * 场数据的最小外包矩形
     */
    public MBR Boundary;
    private Hints CRSHints;
    /**
     * 标识该field的原数据类型
     */
    private int Type;
    private int BandToRender = 1;
    /**
     * 类标识的名称
     */
    public static final String className = "LevyFlightField";
    private double[] extremums;

    private Field(GridCoverage2D coverage, String path)
    {
        this.GridCoverage = coverage;
        this.AbsolutePath = path;
        this.Boundary = new MBR();
        this.Boundary.Left = coverage.getEnvelope().getLowerCorner().getCoordinate()[0];
        this.Boundary.Bottom = coverage.getEnvelope().getLowerCorner().getCoordinate()[1];
        this.Boundary.Right = coverage.getEnvelope().getUpperCorner().getCoordinate()[0];
        this.Boundary.Top = coverage.getEnvelope().getUpperCorner().getCoordinate()[1];
        this.extremums = null;
    }

    /**
     * 获取数据源的Reader，用于绘制图像
     * @return
     * @throws DataSourceException
     */
    public AbstractGridCoverage2DReader getReader() throws DataSourceException
    {
        File sourceFile = new File(this.AbsolutePath);
        if (sourceFile == null)
        {
            return null;
        }
        AbstractGridCoverage2DReader reader = null;
        if (this.Type == Field.FieldType_GEOTIFF)
        {
            AbstractGridFormat gridFormat = GridFormatFinder.findFormat(sourceFile);
            reader = gridFormat.getReader(sourceFile);
        } else if (this.Type == Field.FieldType_ASCII)
        {
            ArcGridReader agr = new ArcGridReader(sourceFile, this.CRSHints);
            reader = agr;
        }
        return reader;
    }

    /**
     * 获取用于标识“无数据”区域的值
     * @return
     */
    public double getNoDataValue()
    {
        if (this.GridCoverage == null)
        {
            return Double.NaN;
        }
        double noDataValue = Double.NaN;
        try
        {
            noDataValue = Double.parseDouble(this.GridCoverage.getProperty("GC_NODATA").toString());
        } catch (Exception e)
        {
            noDataValue = Double.NaN;
        }
        return noDataValue;
    }

    /**
     * 获取栅格矩阵的极值
     * @return 包括最小值和最大值的数组,按照波段分组；每组中都先是最小值，后是最大值
     */
    public double[] getExtremums()
    {
        if (this.GridCoverage == null)
        {
            return null;
        } else
        {
            if (this.extremums != null)
            {
                return this.extremums;
            } else
            {
                int width = this.GridCoverage.getGridGeometry().getGridRange2D().width;
                int height = this.GridCoverage.getGridGeometry().getGridRange2D().height;
                int bandNum = this.getNumBands();
                double noData = this.getNoDataValue();
                double[] result = new double[bandNum * 2];
                for (int i = 0; i < bandNum; i++)
                {
                    result[i * 2] = Double.POSITIVE_INFINITY;
                    result[i * 2 + 1] = Double.NEGATIVE_INFINITY;
                }


                for (int i = 0; i < width; i++)
                {
                    for (int j = 0; j < height; j++)
                    {
                        double[] valueList = new double[bandNum];
                        valueList = this.GridCoverage.evaluate(new GridCoordinates2D(i, j), new double[bandNum]);
                        for (int k = 0; k < bandNum; k++)
                        {
                            if (valueList[k] == noData)
                            {
                                continue;
                            }
                            if (result[k * 2] > valueList[k])
                            {
                                result[k * 2] = valueList[k];
                            }
                            if (result[k * 2 + 1] < valueList[k])
                            {
                                result[k * 2 + 1] = valueList[k];
                            }
                        }
                    }
                }
                this.extremums = result;
                return result;
            }
        }
    }

    /**
     * 获取在某一波段的所有数据值（不包括非值区域）
     * @param bandIndex
     * @return
     */
    public ArrayList<Double> getAllValueAtBand(int bandIndex)
    {
        if (this.GridCoverage == null)
        {
            return null;
        }
        int width = this.GridCoverage.getGridGeometry().getGridRange2D().width;
        int height = this.GridCoverage.getGridGeometry().getGridRange2D().height;
        int bandNum = this.getNumBands();
        if  (bandIndex>=bandNum)
        {
            return null;
        }
        ArrayList<Double> dataList=new ArrayList<Double>();
        double noData = this.getNoDataValue();
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                double[] valueList = new double[bandNum];
                valueList = this.GridCoverage.evaluate(new GridCoordinates2D(i, j), new double[bandNum]);
                double value=valueList[bandIndex];
                if(value!=noData)
                {
                    dataList.add(valueList[bandIndex]);
                }
            }
        }
        return dataList;
    }

    /**
     * 获取该Field的原数据类型
     * @return
     */
    public int getFieldType()
    {
        return this.Type;
    }

    /**
     * 获取Field的波段数
     * @return
     */
    public int getNumBands()
    {
        return this.GridCoverage.getNumSampleDimensions();
    }

    /**
     * 获取要绘制的波段数
     * @return value greater than 0 means specific bands  index, value==0 means RGB Style(Only of GeoTiff type)
     */
    public int getBandToRender()
    {
        return this.BandToRender;
    }

    /**
     * 安全地设置需要绘制的波段索引
     * @param index index==0表示RGB绘制， 否则绘制为 index的波段
     */
    public void setBandToRender(int index)
    {
        if (index == 0)
        {
            if (this.Type == Field.FieldType_GEOTIFF && this.getNumBands() >= 3)
            {
                this.BandToRender = index;
            }
        } else
        {
            int num = this.getNumBands();
            if (index >= 1 && index <= num)
            {
                this.BandToRender = index;
            } else
            {
                this.BandToRender = 1;
            }
        }

    }

    /**
     * 返回某一节点处的场数据的特定波段值
     * @param node 节点
     * @param bandIndex 波段索引值
     * @return 场数据值
     */
    public double getValueAt(Node node, int bandIndex)
    {
        if (this.getNumBands() - 1 < bandIndex)
        {
            return Double.NaN;
        }

        double[] values = new double[this.getNumBands()];

        this.GridCoverage.evaluate((DirectPosition) (new DirectPosition2D(node.getX(), node.getY())), values);
        return (values[bandIndex]);

    }

    /**
     * 返回某一节点处的场数据的所有波段值
     * @param node
     * @return
     */
    public double[] getValuesAt(Node node)
    {
        double[] values = new double[this.getNumBands()];

        this.GridCoverage.evaluate((DirectPosition) (new DirectPosition2D(node.getX(), node.getY())), values);
        return (values);
    }
}
