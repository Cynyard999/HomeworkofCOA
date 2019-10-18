package cpu.alu;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import transformer.Transformer;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 加减乘除
 */
public class ALU {

    // 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";

    public void setOF(String OF) {
        this.OF = OF;
    }

    public void setCF(String CF) {
        this.CF = CF;
    }

    public String getCF() {
        return CF;
    }

    public String getOF() {
        return OF;
    }

    //与计算 overload
    char and(char i,char j){
        if (i==j&i=='1'){
            return '1';
        }
        return '0';
    }
    char and(char i,char j,char k){
        if (i==j&i=='1'&j==k){
            return '1';
        }
        return '0';
    }
    String and(String src, String dest) {
        char [] result = new char[32];
        for (int i=0;i<32;i++){
            result[i] = and(src.charAt(i),dest.charAt(i));
        }
        return String.valueOf(result);
    }

    //或计算 overload
    char or(char i,char j){
        if (i==j&i=='0'){
            return '0';
        }
        return '1';
    }
    char or(char i,char j,char k){
        if (i==j&i=='0'&j==k){
            return '0';
        }
        return '1';
    }
    String or(String src, String dest) {
        char [] result = new char[32];
        for (int i=0;i<32;i++){
            result[i] = or(src.charAt(i),dest.charAt(i));
        }
        return String.valueOf(result);
    }

    //取反操作
    String not(String str){
        char[] temp = new char[str.length()];
        for (int i=0;i<str.length();i++){
            temp[i] = str.charAt(i)=='1'?'0':'1';
        }
        return String.valueOf(temp);
    }
    char not(char a){
        return a=='1'?'0':'1';
    }
    //取相反数操作

    String findComplement(String str){
        int length = str.length();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0;i<length-1;i++){
            stringBuilder.append("0");
        }
        stringBuilder.append("1");
        return add(not(str),stringBuilder.toString());//返回取反+1的值
    }
    //add two integer,需要相同长度
    String add(String src, String dest) {
        setCF("0");//每个add前设置为0
        setOF("0");
        int length = src.length();
        char [] sum = new char[length];
        char c ='0';
        char x=0;
        char y=0;
        for (int i=length-1;i>=0;i--){
            x = src.charAt(i);
            y = dest.charAt(i);
            char temp = x==y?'0':'1';
            sum[i] = temp==c?'0':'1';
            c = or(and(x,c),and(y,c),and(x,y));
        }
        if (c=='1'){
            setCF("1");
        }
        if (x==y&x!=sum[0]){
            setOF("1");
        }
		return String.valueOf(sum);
    }

    //sub two integer
    // dest - src
    String sub(String src, String dest) {
        if (src.equals(dest)){
            //return "00000000000000000000000000000000";
        }
		return add(dest,findComplement(src));
	}

    //signed integer mod,dest%src,//余数的符号与被除数相同
    String imod(String src, String dest) {
        return new Transformer().intToBinary(String.valueOf(binaryToInt(dest)%binaryToInt(src)));
    }
    int binaryToInt(String code) {
        if (code==null){
            return 0;
        }
        if(code.charAt(0)=='0'){
            return Integer.parseInt(code,2);
        }
        //如果表示的是负数
        char[] numbers = code.toCharArray();
        for (int i=0;i<32;i++){
            numbers[i] = numbers[i]=='0'?'1':'0';//取反
        }
        int inversedNumber = Integer.parseInt(String.valueOf(numbers),2)+1;
        return 0-inversedNumber;
    }

    String xor(String src, String dest) {
        char [] result = new char[32];
        for (int i=0;i<32;i++){
            result[i]=src.charAt(i)==dest.charAt(i)?'0':'1';
        }
        return String.valueOf(result);
    }

    String shl(String src, String dest) {//逻辑左移,src移动位数
        char[] result = new char[dest.length()];
        int length = dest.length();
        int l = Integer.parseInt(src,2);
        if (l>=length){//移动长度大于本身长度
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=0;i<length;i++){
                stringBuilder.append("0");
            }
            return stringBuilder.toString();
        }
        for (int i=length-1;i>length-1-l;i--){
            result[i]='0';
        }
        for (int i =0;i<=length-1-l;i++){
            result[i] = dest.charAt(i+l);
        }
		return String.valueOf(result);
    }

    String shr(String src, String dest) {//逻辑右移
        int length = dest.length();
        char[] result = new char[length];
        int l = Integer.parseInt(src,2);
        if (l>=length){//移动长度大于本身长度
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=0;i<length;i++){
                stringBuilder.append("0");
            }
            return stringBuilder.toString();
        }
        for (int i=0;i<l;i++){
            result[i]='0';
        }
        for (int i =l;i<=length-1;i++){
            result[i] = dest.charAt(i-l);
        }
        return String.valueOf(result);
    }

    String sal(String src, String dest) {//算术左移
		return shl(src,dest);
    }

    String sar(String src, String dest) {//算术右移
        int length = dest.length();
        char[] result = new char[length];
        int l = Integer.parseInt(src,2);
        if (l>length){
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=0;i<length;i++){
                stringBuilder.append(0);
            }
            return stringBuilder.toString();
        }
        for (int i=0;i<l;i++){
            result[i]=dest.charAt(0)=='1'?'1':'0';
        }
        for (int i =l;i<=length-1;i++){
            result[i] = dest.charAt(i-l);
        }
        return String.valueOf(result);
    }

    String rol(String src, String dest) {//循环左移
        char[] result = new char[dest.length()];
        int l = Integer.parseInt(src,2);
        while (l>=dest.length())
            l-=dest.length();
        for (int i=dest.length()-1;i>dest.length()-1-l;i--){
            result[i]=dest.charAt(i-dest.length()+l);
        }
        for (int i =0;i<=dest.length()-1-l;i++){
            result[i] = dest.charAt(i+l);
        }
        return String.valueOf(result);
    }

    String ror(String src, String dest) {//循环右移
        char[] result = new char[dest.length()];
        int l = Integer.parseInt(src,2);
        while (l>=dest.length())
            l-=dest.length();
        for (int i=0;i<l;i++){
            result[i]=dest.charAt(dest.length()-1-i);
        }
        for (int i =l;i<=dest.length()-1;i++){
            result[i] = dest.charAt(i-l);
        }
        return String.valueOf(result);
    }

}
