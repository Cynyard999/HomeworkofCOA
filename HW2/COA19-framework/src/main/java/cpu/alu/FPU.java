package cpu.alu;

import com.sun.xml.internal.ws.api.ha.StickyFeature;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.swing.text.StyleContext;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * TODO: 浮点数运算
 */
public class FPU {
    //创建一个类存储符号幅值加法的结果以及其符号
    class SMA{//包含了符号以及significand
        String sign;
        String significand;
        void setSign(String sign){
            this.sign = sign;
        }
        void setSignificand(String significand){
            this.significand = significand;
        }
        String getSign(){
            return sign;
        }
        String getSignificand(){
            return significand;
        }
    }

    /**
     * compute the float add of (a + b)
     **/
    String add(String a,String b){//a与b之间是加法
        if (a.equals("11111111110000000000000000000000")||a.equals("01111111110000000000000000000000")){
            return a;
        }
        if (b.equals("11111111110000000000000000000000")||b.equals("01111111110000000000000000000000")){
            return b;
        }
        if (a.equals("11111111100000000000000000000000")||a.equals("01111111100000000000000000000000")){
            return a;
        }
        if (b.equals("11111111100000000000000000000000")||b.equals("01111111100000000000000000000000")){
            return b;
        }
        ALU alu = new ALU();
        String exponent;
        String exponentA = a.substring(1,9);
        String exponentB = b.substring(1,9);
        String significandA;
        String significandB;
        significandA = "1"+a.substring(9,32)+"00000000";
        significandB = "1"+b.substring(9,32)+"00000000";
        if (exponentA .equals("00000000")){
            significandA = "0"+a.substring(9,32)+"00000000";//加入隐藏位，加入保护位
        }
        if (exponentB .equals("00000000")){
            significandB = "0"+b.substring(9,32)+"00000000";
        }
        String signA = a.substring(0,1);
        String signB = b.substring(0,1);
        if (a.equals("00000000000000000000000000000000")){
            return b;
        }
        if (b.equals("00000000000000000000000000000000"))
            return a;
        String exponentSub = alu.sub("0"+exponentB,"0"+exponentA);//exponentA-exponentB,无符号正数再加一个0变成补码
        if (exponentSub.charAt(0)=='1'){//如果减出来是负数,exponentB更大
            exponentSub = alu.sub("0"+exponentA,"0"+exponentB);
            exponent = exponentB;
            significandA = alu.shr(exponentSub,significandA);//逻辑右移更小的数的significand
            if (!significandA.contains("1")){
                return b;
            }
        }
        else {
            exponent = exponentA;//结果 exponentA更大
            significandB = alu.shr(exponentSub,significandB);
            if (!significandB.contains("1")){
                return a;
            }
        }
        SMA sma = SignMagnitudeAddition(signA,signB,significandA,significandB);
        if (!sma.getSignificand().contains("1")){
            return "00000000000000000000000000000000";
        }
		return nomalize(sma,exponent);
    }

    /**
     * compute the float add of (a - b)
     **/
    String sub(String a,String b){
        char[] c = b.toCharArray();
        c[0]=(c[0]=='1'?'0':'1');
		return add(a,String.valueOf(c));
    }

    //符号幅值加法++，+-，-+，--
    SMA SignMagnitudeAddition(String signA,String signB,String significandA,String significandB){
        ALU alu = new ALU();
        SMA sma = new SMA();
        if (signA.equals(signB)){//符号相同则做加法
            sma.setSign(signA);
            String tempSignificand = alu.add(significandA,significandB);
            sma.setSignificand(tempSignificand);
            if (alu.getCF()=="1"){
                sma.setSignificand("1"+tempSignificand);
            }
        }
        else {
            significandB = alu.findComplement(significandB);
            String tempSignificand = alu.add(significandA, significandB);
            if (alu.getCF().equals("0")) {//没有产生进位
                tempSignificand = alu.findComplement(tempSignificand);//结果取反
                sma.setSign(alu.not(signA));
                sma.setSignificand(tempSignificand);
            } else {//结果产生进位
                sma.setSign(signA);
                if (alu.getOF().equals("0")){//但是没有溢出，因为在alu的add中，只有有溢出，进位才会被表现出来
                    sma.setSignificand(tempSignificand);
                }
                //有溢出有进位
                else {
                    sma.setSignificand(tempSignificand.substring(1));//减去第一位的进位
                }
            }
        }
        return sma;
    }
    String nomalize(SMA sma,String exponent){
        ALU alu = new ALU();
        StringBuilder stringBuilder = new StringBuilder(sma.getSign());
        String significand = sma.getSignificand();
        if (significand.charAt(0)=='1'&significand.length()>32){//加法产生的进位，significand除以2，相应指数增加
            stringBuilder.append(alu.add("00000001",exponent));
            if (alu.add("00000001",exponent).equals("11111111")){
                stringBuilder.append("00000000000000000000000");//INF
                return stringBuilder.toString();
            }
            if (significand.substring(25,26).equals("0")){//没有四舍五入
                String a = significand.substring(1,24);//取23位，把第一位隐藏位舍去
                stringBuilder.append(a);
            }
            if (significand.substring(25,26).equals("1")){
                significand = alu.add(significand,"00000000000000000000000100000000");//有四舍五入
                String a = significand.substring(1,24);//取23位，把第一位隐藏位舍去
                stringBuilder.append(a);
            }
            return stringBuilder.toString();
        }
        int i = 0;
        while (significand.charAt(i)=='0'&i<32){
            exponent = alu.sub("00000001",exponent);
            i++;
        }
        stringBuilder.append(exponent);
        if (significand.substring(i+24,i+25).equals("1")&significand.substring(i+23,i+24).equals("0")){//这里有点问题？？
            significand = alu.add(significand,"00000000000000000000000100000000");//四舍五入
        }
        String a = significand.substring(i+1,i+24);//取22位
        stringBuilder.append(a);
        return stringBuilder.toString();
    }
}
