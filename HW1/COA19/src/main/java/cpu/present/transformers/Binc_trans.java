package cpu.present.transformers;

import cpu.present.PresentType;

public class Binc_trans implements NumberTransforming {

    private String integer;
    private String code;
    private String float1;
    Binc_trans(String code){
        this.integer = TransMethods.binComplementToInteger(code);
        this.code = code;
        this.float1 = TransMethods.integerTofloat(integer);
    }
    public String transform(PresentType type){
        if (type.equals(PresentType.BCD.W8421)){
            return getBcd();
        }
        if (type.equals(PresentType.DEC.FLOAT)){
            return getFloat();
        }
        if (type.equals(PresentType.DEC.INTEGER)){
            return getInteger();
        }
        else if (type.equals(PresentType.BIN.FLOAT)){
            return getBinf();
        }
        return getBinC();
    }


    public String getFloat(){
        return float1;
    }
    public String getInteger() {
        return integer;
    }
    public String getBinC(){
        return code;
    }
    public String getBinf(){
        return TransMethods.floatToBinf(float1);
    }
    public String getBcd(){
        return TransMethods.integerTo8421BCD(integer);
    }
}
