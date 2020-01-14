package cpu.present.transformers;

import cpu.present.PresentType;

public class BCD8421 implements NumberTransforming{//有时间改个名字
    private String float1;
    private String integer;
    private String code;
    BCD8421(String code){
        integer = TransMethods.BcdtoInteger(code);
        float1 = TransMethods.integerTofloat(integer);
        this.code = code;
    }
    public String transform(PresentType type){
        if (type.equals(PresentType.DEC.INTEGER)){
            return getInteger();
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
        return getBcd();
    }


     public String getFloat(){
        return float1;
    }
    public String getInteger() {
        return integer;
    }
    public String getBinC(){
        return TransMethods.integerToBinComplement(integer);
    }
    public String getBinf(){
        return TransMethods.floatToBinf(float1);
    }
    public String getBcd(){
        return code;
    }
}
