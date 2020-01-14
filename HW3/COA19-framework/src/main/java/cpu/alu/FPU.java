package cpu.alu;

import util.IEEE754Float;

import java.security.AlgorithmConstraints;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用4位保护位进行计算，计算完毕直接舍去保护位
 * TODO: 浮点数运算
 */
public class FPU {

    /**
     * compute the float mul of a * b
     * 分数部分(23 bits)的计算结果直接截取前23位
     */
    boolean a_Isregular = true;
    boolean b_Isregular = true;
    ALU alu = new ALU();
    String mul(String a, String b) {
        StringBuilder stringBuilder = new StringBuilder();
        if (a.charAt(0)!=b.charAt(0)){//负数
            stringBuilder.append(1);
        }
        else stringBuilder.append(0);
        if ((!a.substring(1).contains("1"))||(!b.substring(1).contains("1"))){
            if (a.substring(1,9).equals("11111111")||(b.substring(1,9).equals("11111111"))){
                return "(0|1){1}1{8}(0+1+|1+0+)(0|1)*";//NAN
            }
            stringBuilder.append("0000000000000000000000000000000");//Inf
            return stringBuilder.toString();
        }
        String fractionX = "1"+a.substring(9)+"0000";
        String fractionY = "1"+b.substring(9)+"0000";
        if (a.substring(1,9).equals("00000000")){
            a_Isregular = false;
            fractionX = "0"+a.substring(9)+"0000";
        }
        if (b.substring(1,9).equals("00000000")){
            fractionY = "0"+b.substring(9)+"0000";//保护位
            b_Isregular = false;
        }
        //先相加，再减去127(化成9位补码的加减）
        String add_result = alu.add("0"+a.substring(1,9),"0"+b.substring(1,9));
        String exponent = alu.add("110000001",add_result);
        //如何判断溢出？
        if (exponent.charAt(0)=='1'&alu.getCF().equals("1")){//如果减出来是负数
            stringBuilder.append("00000000");
            stringBuilder.append("00000000000000000000000");
            return stringBuilder.toString();
        }
        if (exponent.charAt(0)=='1'&alu.getCF().equals("0")){//如果减出来的数大于255
            stringBuilder.append("11111111");
            stringBuilder.append("00000000000000000000000");
            return stringBuilder.toString();
        }

        stringBuilder.append(exponent.substring(1));
        String productAndY = "0000000000000000000000000000"+fractionY;
        for (int i=0;i<28;i++){//28 = 1+23+4
            if (productAndY.endsWith("0")){
                productAndY = alu.shr("01",productAndY);//逻辑右移
            }
            else {
                productAndY = alu.add(fractionX+"0000000000000000000000000000",productAndY);
                productAndY = alu.shr("01",productAndY);
            }
        }
        String fraction;
        fraction = productAndY.substring(0,23);
        if (a_Isregular||b_Isregular){//只有一个为regular
            fraction = productAndY.substring(1,24);
        }
        if (a_Isregular&b_Isregular){//两个都为regular
            fraction = productAndY.substring(2,25);
        }
        stringBuilder.append(fraction);
        return stringBuilder.toString();
    }

}
