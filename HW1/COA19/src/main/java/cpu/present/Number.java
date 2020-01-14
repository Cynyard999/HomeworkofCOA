package cpu.present;

import cpu.present.transformers.Transformer;
import cpu.present.transformers.TransformerFactory;

public class Number {

    public static final int DATA_SIZE_LIMITATION = 32;

    protected PresentType type;                     //The type of the binCode

    protected Transformer transformer;              //The transformer for the later transform

    /**
     *
     * @param type the presentation-type of input {@link PresentType}
     * @param args A String or String[] which contains {Input Number, Exponent length, Decimal Length}.
     *             For a standard Integer Number, it could be like {Input, 0, 32} or solely {Input}
     *             and for a standard Float Number, it could be like {Input, 8, 23}
     */
    public Number(PresentType type, PresentType storageType, String... args) {//第一个为当前，第二个为中间存储
        this.type = storageType;
        this.transformer = TransformerFactory.getTransformer(type, storageType, args);
    }

    public String get(PresentType type) {
        return transformer.to(type);
    }//返回一个这个type的string，将这个transformer里面的

}
