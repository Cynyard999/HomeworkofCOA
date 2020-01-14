package memory.cacheMappingStrategy;


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
        int lineNo = replacementStrategy.isHit(0,1023,tag);
        if (lineNo==-1){//如果没有找到
            return -1;
        }
        else {
            return lineNo;//找到
        }
    }

    @Override
    public int writeCache(int blockNO) {
        String addr = getAddress(blockNO);
        char [] data = memory.read(addr,1024);
        return replacementStrategy.writeCache(0,1023,getTag(blockNO),data);
    }
}
