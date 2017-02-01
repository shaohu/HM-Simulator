/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TraGen;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import levyflight.Import.PowerLawTraGen;

/**
 * 组件单元参数接口
 * @author Tiger Shao
 */
abstract public class ConstructParam
{
    /**
     * 参数列表
     */
    public ArrayList<Param> paramList=null;

    /**
     * 默认参数值数组
     */
    public ArrayList<DefaultValueGroup>defaultValuesList=null;

    public ConstructParam()
    {
        this.paramList=new ArrayList<Param>();
        Class a=PowerLawTraGen.class;
        Constructor[] c=a.getConstructors();

    }

    /**
     * 参数描述类
     */
    public class Param
    {
        /**
         * int型标识符
         */
        final static public int Param_int = 0;
        /**
         * double型标识符
         */
        final static public int Param_double = 1;
        /**
         * String型标识符
         */
        final static public int Param_String = 2;
        /**
         * File型标识符
         */
        final static public int Param_File = 3;
        /**
         * 时间型标识符
         */
        final static public int Param_DateTime = 4;
        /**
         * 参数类型
         */
        public int paramType = -1;
        /**
         * 参数描述
         */
        public String description="";

        /**
         * 构造函数
         * @param ParamType 参数类型，来自于 Param.**
         * @param Description 参数描述
         */
        public Param(int ParamType,String Description)
        {
            this.description=Description;
            this.paramType=ParamType;
        }
    }

    /**
     * 默认参数值集合
     */
    public class DefaultValueGroup
    {
        /**
         * 一组默认值的集合
         */
        public ArrayList<Object> defaultValueList=new ArrayList<Object>();
        /**
         * 该组值的简要信息或名字
         */
        public String defaultValueGroupDescription="name not defined";
    }
}
