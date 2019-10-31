package memory.cacheReplacementStrategy;

import memory.Cache;
import memory.Memory;

/**
 * 先进先出算法
 */
public class FIFOReplacement extends ReplacementStrategy {


    @Override
    public int isHit(int start, int end, char[] addrTag) {
        for (int i=start;i<=end;i++){
            char[] tag = cache.getLineTags(i);
            if (tag==addrTag){
                return i;
            }
        }
        return -1;
    }

    @Override
    public int writeCache(int start, int end, char[] addrTag, char[] input) {
        int row_to_be_updated = start;
        Long rowNO  = cache.getLineTimeStamp(start);//将要被更新的行数的stamp
        for (int i = start;i<=end;i++){
            if (cache.getLineTimeStamp(i)==0L){
                row_to_be_updated = i;
                break;
            }
            if (rowNO>=cache.getLineTimeStamp(i)){//如果遇到了比他更小的stamp
                rowNO = cache.getLineTimeStamp(i);//更新即将被淘汰的行号
                row_to_be_updated = i;
            }
        }
        cache.updateLine(row_to_be_updated,input,addrTag);
        return row_to_be_updated;
    }


}
