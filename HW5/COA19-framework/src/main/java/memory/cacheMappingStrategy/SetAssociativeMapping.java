package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;

/**
 * 4路-组相连映射 n=4,   14位标记 + 8位组号 + 10位块内地址
 * 256个组，每个组4行
 */
public class SetAssociativeMapping extends MappingStrategy{

    /**
     *
     * @param blockNO 内存数据块的块号
     * @return cache数据块号 22-bits  [前14位有效]
     */
    @Override
    public char[] getTag(int blockNO) {
        char[] tag;
        String addr = getAddress(blockNO);
        tag = (addr.substring(0,14)+"00000000").toCharArray();
        return tag;
    }

    /**
     *
     * @param blockNO 目标数据内存地址前22位int表示
     * @return -1 表示未命中
     */
    @Override
    public int map(int blockNO) {
        char [] tag = getTag(blockNO);
        //在所有的cache内没有找到
        //模256找到组数，再乘以4找到这个组在cache中开头的行号
        if (replacementStrategy.isHit((blockNO%256)*4,(blockNO%256)*4+3,tag)==-1){//如果没有找到
            return -1;
        }
        else
            return replacementStrategy.isHit((blockNO%256)*4,(blockNO%256)*4+3,tag);//找到
    }

    @Override
    public int writeCache(int blockNO) {
        String addr = getAddress(blockNO);
        char [] data = memory.read(addr,1024);
        return replacementStrategy.writeCache((blockNO%256)*4,(blockNO%256)*4+3,getTag(blockNO),data);
    }
}










