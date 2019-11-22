package memory;

import transformer.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 内存抽象类
 */
public class Memory {

    /*
     * ------------------------------------------
     *            |  Segment=true | Segment=false
     * ------------------------------------------
     * Page=true  |     段页式    |    不存在
     * ------------------------------------------
     * Page=false |    只有分段   |   实地址模式
     * ------------------------------------------
     *
     * 请实现三种模式下的存储管理方案：
     * 实模式：
     * 		无需管理，该情况下不好判断数据是否已经加载到内存(除非给每个字节建立有效位)，干脆每次都重新读Disk(地址空间不会超过1 MB)
     * 分段： 不足够，先替换，如果能放了，不整理，直接放，如果总空间足够但是不能放，整理了再放
     * 		最先适应 -> 空间不足则判断总剩余空间是否足够 -> 足够则进行碎片整理，将内存数据压缩
     * 													 -> 不足则采用最近使用算法LRU直到总剩余空间足够 -> 碎片整理
     * 段页：
     * 		如果数据段已经在内存，使用全关联映射+LRU加载物理页框；如果数据段不在内存，先按照分段模式进行管理，分配的段长度为数据段包含的总物理页框数/2，再将物理页框加载到内存
     */
    public static boolean SEGMENT = false;

    public static boolean PAGE = false;

    public static int MEM_SIZE_B = 32 * 1024 * 1024;      // 主存大小 32 MB, 2^25 Bytes

    public static int PAGE_SIZE_B = 1 * 1024;      // 页大小 1 KB 2^10Bytes

    Transformer t = new Transformer();

    public Disk disk = Disk.getDisk();

    private static char[] memory = new char[MEM_SIZE_B];

    private static ArrayList<SegDescriptor> segTbl = new ArrayList<>();//段表,长度不固定

    private static PageItem[] pageTbl = new PageItem[Disk.DISK_SIZE_B / Memory.PAGE_SIZE_B]; // 页表大小为2^17  128K,一行存储一个页的信息

    private static ReversedPageItem[] reversedPageTbl = new ReversedPageItem[Memory.MEM_SIZE_B / Memory.PAGE_SIZE_B]; // 反向页表大小为2^15   32K，根据每行的信息，可以知道这个页在磁盘中的信息

    private static Memory memoryInstance = new Memory();

    private Memory() {}

    public static Memory getMemory() {
        return memoryInstance;
    }

    public static ArrayList<SegDescriptor> getSegTbl() {
        return segTbl;
    }

    //存储memory中每个段的首地址

    private static int size;

    private SegDescriptor findSeg_toBeUpdated(){//找到将要被修改的段
        Long stamp = segTbl.get(0).timeStamp;
        SegDescriptor toBeUpdated = segTbl.get(0);
        for (SegDescriptor s:segTbl){
            if (s.validBit&&s.timeStamp<stamp){
                stamp = s.timeStamp;
                toBeUpdated = s;
            }
        }
        return toBeUpdated;
    }
    //修改段表中对应的内容，
    private void makeInvalid(SegDescriptor s){
        int start = Integer.parseInt(t.binaryToInt(String.valueOf(s.base)));//段的起始地址
        int len = Integer.parseInt(t.binaryToInt(String.valueOf(s.limit)));//段的长度
        size-=len;
        for (int ptr=0; ptr<len; ptr++) {//这一段的所有数据设置为初始值
            memory[start+ptr] = '\0';
        }
        s.validBit = false;//段更改为不可用
    }

    private String mem_Defragmentation(){
        return "";

    }

    /**
     * 分段开启的情况下，read方法不允许一次性读取两个段的内容，但是可以一次性读取单个段内多页的内容
     * 注意， read方法应该在load方法被调用之后调用，即read方法的目标页(如果开启分页)都是合法的
     * @param eip
     * @param len
     * @return
     */
    public char[] read(String eip, int len){
        for(SegDescriptor s:segTbl){
            if (eip.equals(String.valueOf(s.base))){//更新段表里的stamp
                s.updateTimeStamp();
            }
        }
        char []data = new char[len];
        int start = Integer.parseInt(new Transformer().binaryToInt(eip));
        for (int ptr=0; ptr<len; ptr++) {
            data[ptr] = memory[start+ptr];
        }
        return data;
    }

    /**
     * 加载磁盘中的数据到内存中，涉及了统计内存大小，替换以及碎片整理
     * 总空间足够，1. 需要碎片整理 2. 直接存
     * 总空间不足，需要替换> 1. 替换的段能容纳新的段的数据，最先适应 2. 替换后空间足够，不能容纳新的段的数据> 碎片整理
     *
     *
     * @param memory_base
     * @param disk_base
     * @param len
     * @return
     */
    public String load(String memory_base, String disk_base,int len){
        String newMemory_base = memory_base;
        if (len+size>MEM_SIZE_B) {//总空间不足够
            while (len + size > MEM_SIZE_B) {//总空间不够需要一个被替换掉,stamp最小的，即很久没有访问过的，将这个段写到新分配的内存地址中，修改段表
                SegDescriptor s = findSeg_toBeUpdated();
                makeInvalid(s);
                if (len <= Integer.parseInt(String.valueOf(s.limit))) {//最先适应
                    newMemory_base = String.valueOf(s.base);
                    write(newMemory_base, len, disk.read(disk_base, len));
                    return newMemory_base;
                }
            }
            //跳出while说明替换完了，但是并没有直接能进入的内存，那么开始整理,得到内存开始为空的首地址
            newMemory_base = mem_Defragmentation();
            write(newMemory_base, len, disk.read(disk_base, len));
            return newMemory_base;
        }

        //总空间足够：
        write(newMemory_base, len, disk.read(disk_base, len));
        return newMemory_base;
    }

    public String getPhysicalAddr(String logicAddr,int len){
        String physicaoAddr = "";
        if (SEGMENT&&!PAGE){//只分段
            int segSelector = Integer.parseInt(t.binaryToInt("0"+logicAddr.substring(0,13)));
            SegDescriptor segDescriptor = segTbl.get(segSelector);//通过段号获得段描述符
            if (!segDescriptor.validBit){//内存中没有这个段
                String disk_base = String.valueOf(segDescriptor.getDisk());//得到段在磁盘中的地址
                String base = String.valueOf(segDescriptor.getBase());//得到段在内存中分配的地址
                base = load(base,disk_base,len);
                return base;//得到新的在内存中的地址
            }
        }

        return physicaoAddr;
    }

    public void write(String eip, int len, char []data){
        // 通知Cache缓存失效
        // 本作业只要求读数据，不要求写数据，因此不存在程序修改数据导致Cache修改 -> Mem修改 -> Disk修改等一系列write back/write through操作，
        //     write方法只用于测试用例中的下层存储修改数据导致上层存储数据失效，Disk.write同理
//        Cache.getCache().invalid(eip, len);
        // 更新数据
        size+=len;
        int start = Integer.parseInt(new Transformer().binaryToInt(eip));
        for (int ptr=0; ptr<len; ptr++) {
            memory[start + ptr] = data[ptr];
        }
    }


    /*************************************************以下为数据结构和测试用例使用的接口*************************************************/

    /**
     * 强制创建一个段描述符，指向指定的物理地址，以便测试用例可以直接修改Disk，
     * 而不用创建一个模拟进程，自上而下进行修改，也不用实现内存分配策略
     * 此方法仅被测试用例使用，而且使用时需小心，此方法不会判断多个段描述符对应的物理存储区间是否重叠
     * @param segSelector
     * @param eip 32-bits
     * @param len
     */
    public void alloc_seg_force(int segSelector, String eip, int len, boolean isValid, String disk_base) {
        SegDescriptor sd = new SegDescriptor();
        Transformer t = new Transformer();
        sd.setDisk(disk_base.toCharArray());
        sd.setBase(eip.toCharArray());
        sd.setLimit(t.intToBinary(String.valueOf(len)).substring(1, 32).toCharArray());
        sd.setValidBit(isValid);
        Memory.segTbl.add(segSelector,sd);
    }

    /**
     * 清空段表页表，用于测试用例
     */
    public void clear() {
        segTbl = new ArrayList<>();
        for (PageItem pItem:pageTbl) {
            if (pItem != null) {
                pItem.isInMem = false;
            }
        }
    }

    /**
     * 强制使段/页失效，仅用于测试用例
     * @param segNO
     * @param pageNO
     */
    public void invalid(int segNO, int pageNO) {
        if (segNO >= 0) {
            segTbl.get(segNO).validBit = false;
        }
        if (Memory.PAGE) {
            if (pageNO >= 0) {
                pageTbl(pageNO).setInMem(false);
            }
        }
    }

    PageItem pageTbl(int index) {
        if (pageTbl[index] == null) {
            pageTbl[index] = new PageItem();
            return pageTbl[index];
        } else {
            return pageTbl[index];
        }
    }


    ReversedPageItem reversedPageTbl(int index){
        if(reversedPageTbl[index] == null){
            reversedPageTbl[index] = new ReversedPageItem();
            return reversedPageTbl[index] ;
        }else{
            return reversedPageTbl[index];
        }
    }


    /**
     * 理论上应该为Memory分配出一定的系统保留空间用于存放段表和页表，并计算出每个段表和页表所占的字节长度，通过地址访问
     * 不过考虑到Java特性，在此作业的实现中为了简化难度，全部的32M内存都用于存放数据(代码段)，段表和页表直接用独立的数据结构表示，不占用"内存空间"
     * 除此之外，理论上每个进程都会有对应的段表和页表，作业中则假设系统只有一个进程，因此段表和页表也只有一个，不需要再根据进程号选择相应的表
     */
    /**
     * 段选择符理论长度为64-bits，包括32-bits基地址和20-bits的限长(1 MB)，为了测试用例填充内存方便，未被使用的11-bits数据被添加到限长，即作业中限长为31-bits
     */
    private class SegDescriptor {
        // 段基址在缺段中断发生时可能会产生变化，内存重新为段分配内存基址
        private char[] base = new char[32];  // 32位基地址

        private char[] limit = new char[31]; // 31位限长，表示段在内存中的长度

        private boolean validBit = false;    // 有效位,为true表示被占用（段已在内存中），为false表示空闲（不在内存中）

        private long timeStamp = 0l;

        // 段在物理磁盘中的存储位置，真实段描述符里不包含此字段，本作业规定，段在磁盘中连续存储，并且磁盘中的存储位置不会发生变化
        private char[] disk_base = new char[32];

        public SegDescriptor () {
            timeStamp = System.currentTimeMillis();
        }

        public char[] getBase() {
            return base;
        }//返回内存中的基地址

        public void setBase(char[] base) {
            this.base = base;
        }

        public char[] getDisk() {
            return disk_base;
        }//返回磁盘中的基地址

        public void setDisk(char[] base) {
            this.disk_base = base;
        }

        public char[] getLimit() {
            return limit;
        }

        public void setLimit(char[] limit) {
            this.limit = limit;
        }

        public boolean isValidBit() {
            return validBit;
        }

        public void setValidBit(boolean validBit) {
            this.validBit = validBit;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void updateTimeStamp() {
            this.timeStamp = System.currentTimeMillis();
        }//返回当前时间
    }


    /**
     * 页表项为长度为20-bits的页框号
     */
    private class PageItem {

        private char[] frameAddr;

        private boolean isInMem = false;

        public char[] getFrameAddr() {
            return frameAddr;
        }

        public void setFrameAddr(char[] frameAddr) {
            this.frameAddr = frameAddr;
        }

        public boolean isInMem() {
            return isInMem;
        }

        public void setInMem(boolean inMem) {
            isInMem = inMem;
        }

    }

    /**
     * 反向页表 32 * 1024 * 1024 / 1 * 1024 = 32 * 1024 = 32 KB 个反向页表项
     */
    private class ReversedPageItem {

        private boolean isValid = false;    // false表示不在内存，true表示在内存中

        private int vPageNO = -1;           // 虚页页号

        private long timeStamp = System.currentTimeMillis();

        public long getTimeStamp(){
            return this.timeStamp;
        }

        public void updateTimeStamp(){
            this.timeStamp = System.currentTimeMillis();
        }

    }

}