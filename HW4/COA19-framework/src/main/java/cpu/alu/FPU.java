package cpu.alu;

import transformer.Transformer;
import util.IEEE754Float;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用4位保护位进行计算，计算完毕直接舍去保护位
 * TODO: 浮点数运算
 */
public class FPU {
    ALU alu = new ALU();
    /**
     * compute the float mul of a / b
     */
    String remainder;
    public void setRemainder(String remainder) {
        this.remainder = remainder;
    }
    String quotient;
    public void setQuotient(String quotient) {
        this.quotient = quotient;
    }
    String sigDivisor;
    String quotient_0;

    public void setQuotient_0(String quotient_0) {
        this.quotient_0 = quotient_0;
    }

    public void setSigDivisor(String sigDivisor) {
        this.sigDivisor = sigDivisor;
    }
    private void leftShift() {
        StringBuilder stringBuilder = new StringBuilder(remainder);
        stringBuilder.append(quotient);
        stringBuilder.append(quotient_0);
        String temp = alu.shl("1", stringBuilder.toString());
        setRemainder(temp.substring(0, 28));
        setQuotient(temp.substring(28,56));
        setQuotient_0("0");
    }
    String div(String a, String b) {
        String dividend = a;
        String divisor  = b;
        if ((dividend.equals(IEEE754Float.N_ZERO)||dividend.equals(IEEE754Float.P_ZERO))&&(divisor.equals(IEEE754Float.P_ZERO)||divisor.equals(IEEE754Float.N_ZERO))){
            return IEEE754Float.NaN;
        }
        if (divisor.equals(IEEE754Float.P_ZERO)||divisor.equals(IEEE754Float.N_ZERO)){
            throw  new ArithmeticException();
        }
        if (dividend.equals(IEEE754Float.N_ZERO)||dividend.equals(IEEE754Float.P_ZERO)){
            return dividend;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (dividend.charAt(0)==divisor.charAt(0)){
            stringBuilder.append("0");
        }
        else stringBuilder.append("1");
        String exponent = alu.add(alu.sub(divisor.substring(1,9),dividend.substring(1,9)),"01111111");
        if (divisor.substring(1,9).equals("00000000")){
            setSigDivisor("0"+divisor.substring(9,32)+"0000");
        }
        else setSigDivisor("1"+divisor.substring(9,32)+"0000");
        if (dividend.substring(1,9).equals("00000000")){
            setRemainder("0"+dividend.substring(9,32)+"0000");
        }
        else setRemainder("1"+dividend.substring(9,32)+"0000");
        setQuotient("0000000000000000000000000000");
        for (int i =0;i<28;i++){//一共移动28次
            String temp = alu.sub("0"+sigDivisor,"0"+remainder);//换算成补码计算，如果减法出了负数，那么not enough
            if (temp.charAt(0)=='0'){
                setRemainder(temp.substring(1));
                setQuotient_0("1");
                leftShift();
            }
            else {
                setQuotient_0("0");
                leftShift();
            }
        }
        String normalizedQuotient = quotient.substring(1,24);
        if (quotient.charAt(24)=='1'){
            normalizedQuotient = alu.add("00000000000000000000001",normalizedQuotient);
        }
        stringBuilder.append(exponent);
        stringBuilder.append(normalizedQuotient);
        return stringBuilder.toString();
    }
}
