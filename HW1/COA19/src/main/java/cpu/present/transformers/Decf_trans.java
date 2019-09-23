package cpu.present.transformers;

import cpu.present.PresentType;

public class Decf_trans {


    private String code;
    private String integer;
    Decf_trans(String code){
        this.code = code;
        this.integer = TransMethods.floatToInteger(this.code);
        //System.out.println(integer);
    }
    public String transform(PresentType type){
        if (type.equals(PresentType.BCD.W8421)){
            return getBcd();
        }
        if (type.equals(PresentType.BIN.TWOS_COMPLEMENT)){
            return getBinC();
        }
        if (type.equals(PresentType.DEC.INTEGER)){
            return getInteger();
        }
        else if (type.equals(PresentType.BIN.FLOAT)){
            return getBinf();
        }
        return getFloat();
    }


    public String getFloat(){
        return code;
    }
    public String getInteger() {
        return integer;
    }
    public String getBinC(){
        return TransMethods.integerToBinComplement(integer);
    }
    public String getBinf(){
        return TransMethods.floatToBinf(code);
    }
    public String getBcd(){
        return TransMethods.integerTo8421BCD(integer);
    }
}
