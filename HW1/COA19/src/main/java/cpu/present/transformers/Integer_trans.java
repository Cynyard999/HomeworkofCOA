package cpu.present.transformers;

import cpu.present.PresentType;

public class Integer_trans implements NumberTransforming {

    private String float1;
    private String code;
    Integer_trans(String code){
        float1 = TransMethods.integerTofloat(code);
        this.code = code;
    }
    public String transform(PresentType type){
        if (type.equals(PresentType.BCD.W8421)){
            return getBcd();
        }
        if (type.equals(PresentType.DEC.FLOAT)){
            return getFloat();
        }
        if (type.equals(PresentType.BIN.TWOS_COMPLEMENT)){
            return getBinC();
        }
        else if (type.equals(PresentType.BIN.FLOAT)){
            return getBinf();
        }
        return getInteger();
    }


    public String getFloat(){
        return float1;
    }
    public String getInteger() {
        return code;
    }
    public String getBinC(){
        return TransMethods.integerToBinComplement(code);
    }
    public String getBinf(){
        return TransMethods.floatToBinf(float1);
    }
    public String getBcd(){
        return TransMethods.integerTo8421BCD(code);
    }
}
