package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;

public class AssociativeMapping extends MappingStrategy {  // 全相联映射

    /**
     * @param blockNO 内存数据块的块号
     * @return cache数据块号 22-bits  [前22位有效]
     */
    @Override
    public char[] getTag(int blockNO) {
        char[] tag;
        String addr = getAddress(blockNO);
        tag = addr.substring(0,22).toCharArray();
        return tag;
    }

    @Override
    public int map(int blockNO) {
        char [] tag = getTag(blockNO);
        //在所有的cache内没有找到
        if (replacementStrategy.isHit(0,1023,tag)==-1){//如果没有找到
            return -1;
        }
        else
        return replacementStrategy.isHit(0,1023,tag);//找到
    }

    @Override
    public int writeCache(int blockNO) {
        String addr = getAddress(blockNO);
        char [] data = memory.read(addr,1024);
        return replacementStrategy.writeCache(0,1023,getTag(blockNO),data);
    }
}
