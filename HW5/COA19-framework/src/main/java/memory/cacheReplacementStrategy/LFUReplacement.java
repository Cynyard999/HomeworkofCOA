package memory.cacheReplacementStrategy;

import memory.Cache;

import java.util.Arrays;

/**
 * 最近不经常使用算法,找到visited最小的行，然后替换，如果被使用过，那么这一行的visited+1，
 * 如果被visit了一次过后，那么这个行的visit加1，刚加入的visited为1
 */
public class LFUReplacement extends ReplacementStrategy {

    @Override
    public int isHit(int start, int end, char[] addrTag) {
        for (int i=start;i<=end;i++){
            char[] tag = cache.getLineTags(i);
            if (Arrays.equals(tag,addrTag) &&cache.getLineValid(i)){
                cache.increaseLineVisited(i);//这一行的使用加1
                return i;
            }
        }
        return -1;
    }

    @Override
    public int writeCache(int start, int end, char[] addrTag, char[] input) {
        int row_to_be_updated = start;
        int earliestVisitTime  = cache.getLineVisited(start);//将要被更新的行数的stamp
        for (int i = start;i<=end;i++){
            if (cache.getLineVisited(i)==0||(!cache.getLineValid(i))){//如果遇到了无效的数据,或者这一行没有被访问过
                row_to_be_updated = i;
                break;
            }
            if (earliestVisitTime>cache.getLineVisited(i)){//如果遇到了比他visit更少的行，如果等于那么还是这一行
                earliestVisitTime = cache.getLineVisited(i);//更新即将被淘汰的行号
                row_to_be_updated = i;
            }
        }
        cache.updateLine(row_to_be_updated,input,addrTag);
        return row_to_be_updated;
    }
}
