package cpu.alu;

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
        char[] temp = new char[32];
        for (int i=0;i<32;i++){
            temp[i] = str.charAt(i)=='1'?'0':'1';
        }
        return String.valueOf(temp);
    }
    //add two integer
    String add(String src, String dest) {
        char [] sum = new char[32];
        char c ='0';
        char x=0;
        char y=0;
        for (int i=31;i>=0;i--){
            x = src.charAt(i);
            y = dest.charAt(i);
            char temp = x==y?'0':'1';
            sum[i] = temp==c?'0':'1';
            c = or(and(x,c),and(y,c),and(x,y));
        }
        if (x==y&x!=sum[0]){
            return "overflow";
        }
		return String.valueOf(sum);
    }

    //sub two integer
    // dest - src
    String sub(String src, String dest) {
        if (src.equals(dest)){
            //return "00000000000000000000000000000000";
        }
		return add(dest,add(not(src),"00000000000000000000000000000001"));
	}

    //signed integer mod,dest%src,//余数的符号与被除数相同
    String imod(String src, String dest) {

		return null;
    }

    String xor(String src, String dest) {
        char [] result = new char[32];
        for (int i=0;i<32;i++){
            result[i]=src.charAt(i)==dest.charAt(i)?'0':'1';
        }
        return String.valueOf(result);
    }

    String shl(String src, String dest) {//逻辑左移,src移动位数
        char[] result = new char[32];
        int l = Integer.parseInt(src,2);
        if (l>32){
            return "00000000000000000000000000000000";
        }
        for (int i=31;i>31-l;i--){
            result[i]='0';
        }
        for (int i =0;i<=31-l;i++){
            result[i] = dest.charAt(i+l);
        }
		return String.valueOf(result);
    }

    String shr(String src, String dest) {//逻辑右移
        char[] result = new char[32];
        int l = Integer.parseInt(src,2);
        if (l>32){
            return "00000000000000000000000000000000";
        }
        for (int i=0;i<l;i++){
            result[i]='0';
        }
        for (int i =l;i<=31;i++){
            result[i] = dest.charAt(i-l);
        }
        return String.valueOf(result);
    }

    String sal(String src, String dest) {//算术左移
		return shl(src,dest);
    }

    String sar(String src, String dest) {//算术右移
        char[] result = new char[32];
        int l = Integer.parseInt(src,2);
        if (l>32){
            return "00000000000000000000000000000000";
        }
        for (int i=0;i<l;i++){
            result[i]=dest.charAt(0)=='1'?'1':'0';
        }
        for (int i =l;i<=31;i++){
            result[i] = dest.charAt(i-l);
        }
        return String.valueOf(result);
    }

    String rol(String src, String dest) {//循环左移
        char[] result = new char[32];
        int l = Integer.parseInt(src,2);
        while (l>=32)
            l-=32;
        for (int i=31;i>31-l;i--){
            result[i]=dest.charAt(i-32+l);
        }
        for (int i =0;i<=31-l;i++){
            result[i] = dest.charAt(i+l);
        }
        return String.valueOf(result);
    }

    String ror(String src, String dest) {//循环右移
        char[] result = new char[32];
        int l = Integer.parseInt(src,2);
        while (l>=32)
            l-=32;
        for (int i=0;i<l;i++){
            result[i]=dest.charAt(31-i);
        }
        for (int i =l;i<=31;i++){
            result[i] = dest.charAt(i-l);
        }
        return String.valueOf(result);
    }

}
