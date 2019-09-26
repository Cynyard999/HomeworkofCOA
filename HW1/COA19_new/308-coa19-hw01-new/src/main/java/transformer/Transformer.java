package transformer;

import java.math.BigDecimal;

public class Transformer {
    public static void main(String[] args){
        Transformer transformer = new Transformer();
        System.out.println(transformer.floatToBinary("-3.4E39"));
    }

    /**
     * Integer to binaryString
     *
     * @param code to be converted
     * @return result
     */
    public String intToBinary(String code) {
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

    /**
     * BinaryString to Integer
     *
     * @param code : Binary string in 2's complement
     * @return :result
     */
    public String binaryToInt(String code) {
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

    /**
     * Float true value to binaryString
     * @param code : The string of the float true value
     * */
    public String floatToBinary(String code) {
        if (code==null){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (code.charAt(0)!='-'){
            stringBuilder.append('0');
        }
        float f = Float.valueOf(code);
        if (f>=Float.MAX_VALUE){
            return "+Inf";
            //return "01111111110000000000000000000000";//infinity
        }
        if(f<=Float.NEGATIVE_INFINITY){
            return "-Inf";
        }
        stringBuilder.append(Integer.toBinaryString(Float.floatToIntBits(f)));
        for (int i = stringBuilder.toString().length();i<32;i++){//对于上面的Integer来说，如果为整数那么之前的0会被删掉
            stringBuilder.insert(0,"0");
        }
        return stringBuilder.toString();
    }

    /**
     * Binary code to its float true value
     * */
    public String binaryToFloat(String code) {//如果使用float精度会缺失，如果使用bigdecimal精度会更高或者是有些时候没有用科学计数法表示，只有用double了
        if (code==null){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        double f;
        if (code.charAt(0)=='1'){
            stringBuilder.append('-');
        }
        if (code.substring(1,9).equals("00000000")){//非规约数
            f=Integer.parseInt(code.substring(9),2);
            f=f*(float) Math.pow(2,-149);
            stringBuilder.append(f);
        }
        if (!code.substring(1,9).equals("00000000")){
            if (code.substring(1,9).equals("11111111")){
                if (code.charAt(0)=='0'){
                stringBuilder.append("+Inf");
                return stringBuilder.toString();
                }
                stringBuilder.append("Inf");
                return stringBuilder.toString();
            }
            f = Integer.parseInt(code.substring(9),2)*Math.pow(2,-23);//小数部分
            f= (f+1)*(float) Math.pow(2,Integer.parseInt(code.substring(1,9),2)-127);
            stringBuilder.append(f);
        }
        return stringBuilder.toString();

    }

    /**
     * The decimal number to its NBCD code
     * */
    public String decimalToNBCD(String code) {
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

    /**
     * NBCD code to its decimal number
     * */
    public String NBCDToDecimal(String code) {
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




}
