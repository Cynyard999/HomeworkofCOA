package cpu.alu;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * TODO: 浮点数运算
 */
public class FPU {

    /**
     * compute the float add of (a + b)
     **/
    String add(String a,String b){
        ALU alu = new ALU();
        String fractionA = a.substring(9,32);
        String fractionB = b.substring(9,32);
        String exponentA = a.substring(1,9);
        String exponentB = b.substring(1,9);
        String signA = a.substring(0,1);
        String signB = b.substring(0,1);
        if (a==b){
            return "00000000000000000000000000000000";
        }
        if (a=="00000000000000000000000000000000"){
            return b;
        }
        if (b=="00000000000000000000000000000000")
            return a;
        String exponentSub = alu.sub("0"+exponentB,"0"+exponentA);//exponentA-exponentB,无符号减法
        String exponent = exponentA;//结果的exponent为大的exponent
        if (exponentSub.charAt(0)=='1'){//如果减出来是负数
            exponentSub = alu.sub("0"+exponentA,"0"+exponentB);
            exponent = exponentB;
        }


		return null;
    }

    /**
     * compute the float add of (a - b)
     **/
    String sub(String a,String b){
        // TODO
		return null;
    }


}
