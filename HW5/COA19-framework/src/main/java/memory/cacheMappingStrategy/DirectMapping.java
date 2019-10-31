package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;

/**
 * 直接映射 12位标记 + 10位块号 + 10位块内地址
 */
public class DirectMapping extends MappingStrategy{

    /**
     * @param blockNO 内存数据块的块号
     * @return cache数据块号 22-bits  [前12位有效]
     */
    @Override
    public char[] getTag(int blockNO) {
        char[] tag;
        String addr = getAddress(blockNO);
        tag = (addr.substring(0,12)+"0000000000").toCharArray();//直接映射需要添加tag后面的位数
        return tag;
    }


    /**
     * 根据内存地址找到对应的行是否命中，直接映射不需要用到替换策略
     * @param blockNO
     * @return -1 表示未命中
     */
    @Override
    public int map(int blockNO) {
        char [] tags = getTag(blockNO);
        int rowNumber = blockNO%1024;
        char [] rowtags = cache.getLineTags(rowNumber);
        if (tags == rowtags){
            return rowNumber;
        }
        return -1;
    }

    /**
     * 在未命中情况下重写cache，直接映射不需要用到替换策略
     * @param blockNO
     * @return
     */
    @Override
    public int writeCache(int blockNO) {
        String addr = getAddress(blockNO);
        char [] data = memory.read(addr,1024);
        char [] tag = getTag(blockNO);
        int rowNO = blockNO%1024;
        cache.updateLine(rowNO,data,tag);
        return rowNO;
    }


}
