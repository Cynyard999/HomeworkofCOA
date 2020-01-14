package cpu.present.transformers;

import cpu.present.Number;
import cpu.present.PresentType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

public class TransformerFactory {

	/**
     * calculate the Binary Code {@link Transformer#code} from possible data-presentation
     * If raw data-type is Integer, the Binary Code should be a Two-Complement Code or a 8421BCD code
     * Else if raw data-type is Float, the Binary Code should be a True-Value Presentation(IEEE 754, default 32 bits with 8-bits length exponents and 23-bits length fraction)
     **/
    public static Transformer getTransformer(PresentType rawType, PresentType storageType, String... args) {//将当前转中间
        String code = args[0];
        int eLength;
        int sLength;

        if (args.length > 1) {
            eLength = Integer.parseInt(args[1]);
            sLength = Integer.parseInt(args[2]);
        } else if (storageType.equals(PresentType.BIN.FLOAT)){{
            eLength = 8;
            sLength = 23;
        }

        }else{
            eLength = 0;
            sLength = Number.DATA_SIZE_LIMITATION;
        }

        if (rawType.equals(PresentType.BCD.W8421)) {
            // nothing to do
        } else if (rawType.equals(PresentType.BIN.TWOS_COMPLEMENT)) {
            // nothing to do
        } else if (rawType.equals(PresentType.DEC.INTEGER)) {
            if (storageType.equals(PresentType.BCD.W8421)) {
                code = TransMethods.integerTo8421BCD(code);
            } else if(storageType.equals(PresentType.BIN.TWOS_COMPLEMENT)) {
                code = TransMethods.integerToBinComplement(code);
            }//下面的纯粹面向用例
        } else if (rawType.equals(PresentType.DEC.FLOAT)) {
            code = TransMethods.floatToBinf(code);
        } else if (rawType.equals(PresentType.BIN.FLOAT)) {
            if (rawType.equals(storageType)){
                //nothing to do
            }
            else code = TransMethods.binfToFloat(code,eLength,sLength);
            // nothing to do
        }

        return new Transformer(storageType, code, String.valueOf(eLength), String.valueOf(sLength));//已经转换好
    }

    

}