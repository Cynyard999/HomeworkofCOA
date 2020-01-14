package cpu.present.transformers;

import cpu.present.PresentType;

public class Transformer {

	int eLength;

	int sLength;

	String code;                                            //The bin code indicate the two's complement

	PresentType type;

	String[] args = null;

	public Transformer(PresentType codeType, String... args) {//传入code的type
		this.type = codeType;
		this.args = args;
		this.code = args[0];
		if (args.length > 1) {
			eLength = Integer.parseInt(args[1]);
			sLength = Integer.parseInt(args[2]);
		}
	}

	public String to(PresentType type) {//中间转目标，将这个实例的code转为传入的type类型的code
		if (this.type.equals(type)){
			return code;
		}
		if (this.type.equals(PresentType.BCD.W8421)){
			BCD8421 bcd8421 = new BCD8421(code);
			return bcd8421.transform(type);
		}
		if (this.type.equals(PresentType.DEC.INTEGER)){
			Integer_trans integer_trans = new Integer_trans(code);
			return integer_trans.transform(type);
		}
		if (this.type.equals(PresentType.BIN.FLOAT)){
			Binf_trans binf_trans = new Binf_trans(code,eLength,sLength);
			return binf_trans.transform(type);
		}
		if (this.type.equals(PresentType.BIN.TWOS_COMPLEMENT)){
			Binc_trans binc_trans = new Binc_trans(code);
			return binc_trans.transform(type);
		}
		else{
			Decf_trans decf_trans = new Decf_trans(code);
			return decf_trans.transform(type);
		}
	}

}