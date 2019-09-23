package cpu.present.transformers;

import cpu.present.PresentType;

public interface NumberTransforming {//可以改为父类
    /*public String integer;
    public String float1;
    public String BCD;
    public String Binf;
    public String Binc;*/
    String transform(PresentType type);
    String getFloat();
    String getBinC();
    String getBinf();
    String getInteger();
    String getBcd();
}
