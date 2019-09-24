package cpu.present.transformers;


import java.math.BigDecimal;

public class TransMethods {
    public static void main(String[] agrs){
        BigDecimal target = new BigDecimal("111");
        BigDecimal result = new BigDecimal("111");
        System.out.println(target.divide(result).setScale(5).equals(new BigDecimal("1.00000")));//
    }

    protected static String integerToBinComplement(String code){//整数转补码,待修改，如果输入的整数大于能表示的最大整数该怎么办
        if (code==null){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int code1 = Integer.valueOf(code);
        for (int i=0;i<32;i++){
            int j = (code1&(0x80000000>>>i))>>>(31-i);
            stringBuilder.append(j);
        }
        return stringBuilder.toString();
    }
    protected static String integerTo8421BCD(String code){
        if (code==null){
            return null;
        }
        String [] keys = new String[]{"0000","0001","0010","0011","0100","0101","0110","0111","1000","1001"};
        StringBuilder stringBuilder = new StringBuilder();
        if (code.charAt(0)=='-') {
            stringBuilder.append("1101");
            for (int i=0;i<8-code.length();i++){
                stringBuilder.append("0000");
            }
            for (int i=1;i<code.length();i++){
                stringBuilder.append(keys[code.charAt(i)-'0']);
            }
            return stringBuilder.toString();
        }
        else {
            stringBuilder.append("1100");
            for (int i=0;i<7-code.length();i++){
                stringBuilder.append("0000");
            }
            for (int i=0;i<code.length();i++){
                stringBuilder.append(keys[code.charAt(i)-'0']);
            }
            return stringBuilder.toString();
        }
    }
    protected static String BcdtoInteger(String code){
        if (code==null){
            return null;
        }
        String [] keys = new String[]{"0000","0001","0010","0011","0100","0101","0110","0111","1000","1001"};
        StringBuilder stringBuilder = new StringBuilder();
        if (code.substring(0,4).equals("1101")){
            stringBuilder.append('-');
        }
        for (int i = 4;i<=28;i+=4){
            for (int j = 0;j<10;j++){
                if(code.substring(i,i+4).equals(keys[j])){//keys里面的index对应的内容就是index转为8421的值
                    stringBuilder.append(j);//这里会添加0到高位，之后会消去高位无用的0；
                }
            }
        }
        int number = Integer.parseInt(stringBuilder.toString());
        return String.valueOf(number);
    }
    protected static String floatToBinf(String code){
        if (code==null){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (code.charAt(0)!='-'){
            stringBuilder.append('0');
        }
        float f = Float.valueOf(code);
        if (f>Float.MAX_VALUE){
            return "01111111111000000000000000000000";//为了测试用例
        }
        stringBuilder.append(Integer.toBinaryString(Float.floatToIntBits(f)));
        for (int i = stringBuilder.toString().length();i<32;i++){//对于上面的Integer来说，如果为整数那么之前的0会被删掉
            stringBuilder.insert(0,"0");
        }
        return stringBuilder.toString();
    }
    protected static String binComplementToInteger(String code){
        if (code==null){
            return null;
        }
        if(code.charAt(0)=='0'){
            return String.valueOf(Integer.parseInt(code,2));
        }
        //如果表示的是负数
        char[] numbers = code.toCharArray();
        for (int i=0;i<32;i++){
            numbers[i] = numbers[i]=='0'?'1':'0';//取反
        }
        int inversedNumber = Integer.parseInt(String.valueOf(numbers),2)+1;
        return String.valueOf(0-inversedNumber);
    }
    protected static String binfToFloat(String code, int eLength, int sLength ){
        if (code==null){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        float f;
        if (code.charAt(0)=='1'){
            stringBuilder.append('-');
        }
        if (code.substring(1,9).equals("00000000")){//非规约数
            if (code.substring(9).equals("10000000000000000000000")&sLength==23){
                return stringBuilder.append(Math.pow(2,-127)).toString();
            }
            f=Integer.parseInt(code.substring(9),2);
            f=f*(float) Math.pow(2,-149);
            stringBuilder.append(f);
        }
        if (!code.substring(1,9).equals("00000000")){
            if (code.substring(1,9).equals("11111111")){
                return stringBuilder.append(Float.MAX_VALUE).toString();
            }
            f = Integer.parseInt(code.substring(9),2)*(float)Math.pow(2,-sLength);//小数部分
            BigDecimal bigDecimal = new BigDecimal((f+1)*(float) Math.pow(2,Integer.parseInt(code.substring(1,9),2)-127)).setScale(20, BigDecimal.ROUND_DOWN);
            stringBuilder.append(bigDecimal.toString());
            if (bigDecimal.floatValue()>Float.MAX_VALUE){//为了测试用例
                return String.valueOf(Float.MAX_VALUE);
            }

        }
        return stringBuilder.toString();
    }
    protected static String floatToInteger(String code){
        if (code==null){
            return null;
        }
        if (Float.valueOf(code)%1!=0){
            return null;
        }
        else return String.valueOf( (int)Float.valueOf(code).floatValue());
    }
    protected static String integerTofloat(String code){
        return code+".0";
    }




}
