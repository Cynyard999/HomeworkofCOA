package cpu.present.transformers;

import cpu.present.PresentType;

public interface NumberTransforming {//可以改为父类

    String transform(PresentType type);
    String getFloat();
    String getBinC();
    String getBinf();
    String getInteger();
    String getBcd();
}
