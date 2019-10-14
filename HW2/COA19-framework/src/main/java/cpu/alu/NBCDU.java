package cpu.alu;

import com.sun.org.apache.regexp.internal.RE;

public class NBCDU {

	// 模拟寄存器中的进位标志位
	private String CF = "0";

	// 模拟寄存器中的溢出标志位
	private String OF = "0";
	private String ableToChangeSign = "0";

	public void setAbleToChangeSign(String ableToChangeSign) {
		this.ableToChangeSign = ableToChangeSign;
	}

	public void setCF(String CF) {
		this.CF = CF;
	}

	public void setOF(String OF) {
		this.OF = OF;
	}

	ALU alu = new ALU();

	/**
	 *
	 * @param a A 32-bits NBCD String
	 * @param b A 32-bits NBCD String
	 * @return a + b
	 */
	String add(String a, String b) {
		String[] result = new String[8];
		StringBuilder stringBuilder = new StringBuilder();
		if (a.substring(0,4).equals(b.substring(0,4))){
			for(int i=32;i>=8;i-=4){
				result[i/4-1] = fourBitsAdd(a.substring(i-4,i),b.substring(i-4,i),CF);
			}
			result[0] = a.substring(0,4);
			if (CF.equals("0")&ableToChangeSign.equals("1")){//强行减去，再取补码
				result[0] = result[0].equals("1101")?"1100":"1101";//变号
				for(int i=32;i>=8;i-=4){
					result[i/4-1] = complement(result[i/4-1]);
					if (i==32){
						result[i/4-1] = fourBitsAdd(result[i/4-1],"0001","0");
					}
				}
			}
			for (int i=0;i<=7;i++){
				stringBuilder.append(result[i]);
			}
			return stringBuilder.toString();
		}
		else {
			return sub(opposite(b),a);//a+(-b)==a-b
		}
	}

	/***
	 *
	 * @param a A 32-bits NBCD String
	 * @param b A 32-bits NBCD String
	 * @return b - a
	 */
	String sub(String a, String b) {//-39-(-28)
		if (a.equals(b)){
			return "11000000000000000000000000000000";
		}
		if (!a.substring(0,4).equals(b.substring(0,4))){//异号
			return add(b,opposite(a));//b-(-a)=b+a
		}
		else{//b-a
			System.out.println(1);
			StringBuilder stringBuilder = new StringBuilder(a.substring(0,4));
			for (int i=1;i<8;i++){
				String c = complement(a.substring(4*i,4*i+4));
				if (i==7){
					c = fourBitsAdd(c,"0001","0");
				}
				stringBuilder.append(c);
			}
			setAbleToChangeSign("1");
			return add(b,stringBuilder.toString());
		}
	}
	String fourBitsAdd(String a,String b,String c){
		setOF("1");
		setCF("0");
		String resultOne = alu.add(a,b);
		setCF(alu.getCF());
		if (c.equals("1")){
			resultOne = alu.add(resultOne,"0001");
		}
		String resultTwo;
		if (Integer.parseInt(resultOne,2)>9||CF.equals("1")) {//可能有进位可能没有进位
			setCF("1");//加了6必定有进位啊
			if (alu.getOF().equals("1")) {//此时resultone为5位数
				resultTwo = alu.add(resultOne, "00110");
				return resultTwo.substring(1);
			} else {
				resultTwo = alu.add(resultOne, "0110");
				if (alu.getOF().equals("1")){
					return resultTwo.substring(1);//如果变成了五位数
				}
				else return resultTwo;
			}
		}
		return resultOne;
	}
	String opposite(String b){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(b.substring(0,4).equals("1100")?"1101":"1100");
		for (int i=1;i<8;i++){
			String a = b.substring(4*i,4*i+4);
			stringBuilder.append(a);
		}
		return stringBuilder.toString();

	}
	String complement(String b){
		ALU alu = new ALU();
		String result = alu.not(alu.add(b,"0110"));//取每四位的inversion
		return result.equals("1010")?"0000":result;//先加6，再取反，再加1,如果得到10，那么返回0，因为0的补码就是0
	}


}
