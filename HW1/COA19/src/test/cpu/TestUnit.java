package src.test;

import cpu.present.Number;
import cpu.present.PresentType;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

public class TestUnit {

    HashMap<PresentType, String> reals = new HashMap<>();

    public TestUnit(String integer, String decf, String binf, String w8421, String twoBin) {
        if (integer != null) {
            reals.put(PresentType.DEC.INTEGER, integer);
            reals.put(PresentType.BCD.W8421, w8421);
            reals.put(PresentType.BIN.TWOS_COMPLEMENT, twoBin);
        }
        if (decf != null) {
            reals.put(PresentType.DEC.FLOAT, decf);
            reals.put(PresentType.BIN.FLOAT, binf);
        }
    }

    public ArrayList<PresentType> eq(Number number) {
        ArrayList<PresentType> errList = new ArrayList<>();
        for (PresentType t:reals.keySet()) {
            if (t.equals(PresentType.DEC.INTEGER)) {
                BigInteger target = new BigInteger(reals.get(t));
                BigInteger result = new BigInteger(number.get(t));
                if (! target.equals(result)) {
                    errList.add(t);
                }
            } else if (t.equals(PresentType.DEC.FLOAT)) {
                BigDecimal target = new BigDecimal(reals.get(t));
                BigDecimal result = new BigDecimal(number.get(t));//
                if (! target.equals(0)) {
                    if (! result.divide(target).setScale(5).equals(new BigDecimal("1.00000"))) {//尝试把1.00000变为字符串形式
                        errList.add(t);
                    }
                } else {
                    if (! result.equals(0)) {
                        //System.out.println("No match 0.2");
                        errList.add(t);
                    }
                }
            } else {
                if ( ! reals.get(t).equals(number.get(t)) ) {
                    //System.out.println("No two match");
                    errList.add(t);
                }
            }
        }
        return errList;
    }

    public ArrayList<PresentType> eq(PresentType type, PresentType storageType, String... args) {
        Number number = new Number(type, storageType, args);
        return eq(number);
    }


}
