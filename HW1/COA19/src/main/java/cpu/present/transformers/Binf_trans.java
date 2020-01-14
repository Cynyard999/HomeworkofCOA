package cpu.present.transformers;

import cpu.present.PresentType;
import org.omg.CORBA.TRANSACTION_MODE;

public class Binf_trans implements NumberTransforming {


    private String code;
    private String float1;
    private String integer;
    Binf_trans(String code,int eLength, int sLength){
        this.code = code;
        this.float1 = TransMethods.binfToFloat(code,eLength,sLength);
        this.integer = TransMethods.floatToInteger(float1);
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
        else if (type.equals(PresentType.DEC.FLOAT)){
            return getFloat();
        }
        return getBinf();
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
        return code;
    }
    public String getBcd(){
        return TransMethods.integerTo8421BCD(integer);
    }
}
