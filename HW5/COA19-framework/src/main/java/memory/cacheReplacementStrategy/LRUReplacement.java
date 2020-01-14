package memory.cacheReplacementStrategy;

import memory.Cache;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * 最近最少用算法，与fifo相差不多，但是每次hit过后，就更新一下这一行的 使用率，
 * 没有数据的行的stamp为0
 */
public class LRUReplacement extends ReplacementStrategy {

    /**
     *
     * @param start 起始位置
     * @param end 结束位置 闭区间
     */
    @Override
    public int isHit(int start, int end,char[] addrTag) {
        for (int i=start;i<=end;i++){
            char[] tag = cache.getLineTags(i);
            if (Arrays.equals(tag,addrTag) &&cache.getLineValid(i)){
                cache.setLineTimeStamp(i);//将这一行的使用更新
                return i;
            }
        }
        return -1;
    }


    /**
     * 找到最小时间戳的行，替换
     * @param start 起始行
     * @param end 结束行 闭区间
     * @param addrTag tag
     * @param input  数据
     * @return
     */
    @Override
    public int writeCache(int start, int end, char[] addrTag, char[] input) {
        int row_to_be_updated = start;
        Long rowNO  = cache.getLineTimeStamp(start);//将要被更新的行数的stamp
        for (int i = start;i<=end;i++){
            if (cache.getLineTimeStamp(i)==0L||(!cache.getLineValid(i))){//如果遇到了无效的数据
                row_to_be_updated = i;
                break;
            }
            if (rowNO>cache.getLineTimeStamp(i)){//如果遇到了比他更小的stamp
                rowNO = cache.getLineTimeStamp(i);//更新即将被淘汰的行号
                row_to_be_updated = i;
            }
        }
        cache.updateLine(row_to_be_updated,input,addrTag);
        return row_to_be_updated;
    }

}





























