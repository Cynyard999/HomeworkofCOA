package memory;

import transformer.Transformer;

import java.awt.print.Pageable;
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
//假设虚存大小等于磁盘大小，虚存地址空间等于磁盘地址空间(实际情况下虚存大小会远大于内存空间，并且远小于磁盘空间)
    public static int PAGE_SIZE_B = 1 * 1024;      // 页大小 1 KB 有1024个地址

    Transformer t = new Transformer();

    public Disk disk = Disk.getDisk();

    private static char[] memory = new char[MEM_SIZE_B];

    private static ArrayList<SegDescriptor> segTbl = new ArrayList<>();//段表,长度不固定

    private static PageItem[] pageTbl = new PageItem[Disk.DISK_SIZE_B / Memory.PAGE_SIZE_B]; // 页表大小为2^17行  128*1024行,一行存储一个页的信息

    private static ReversedPageItem[] reversedPageTbl = new ReversedPageItem[Memory.MEM_SIZE_B / Memory.PAGE_SIZE_B]; // 反向页表大小为2^15   32K，根据每行的信息，可以知道这个页在磁盘中的信息

    private static Memory memoryInstance = new Memory();

    private Memory() {}

    public static Memory getMemory() {
        return memoryInstance;
    }

    public static ArrayList<SegDescriptor> getSegTbl() {
        return segTbl;
    }

    private static int size;

    private SegDescriptor findSeg_toBeReplaced(){//找到在内存中将要被替换的段
        Long stamp = segTbl.get(0).timeStamp;
        SegDescriptor toBeReplaced = segTbl.get(0);
        for (SegDescriptor s:segTbl){
            if (s.validBit&&s.timeStamp<stamp){
                stamp = s.timeStamp;
                toBeReplaced = s;
            }
        }
        return toBeReplaced;
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
        String startAddr = "00000000000000000000000000000000";
        for (SegDescriptor s: segTbl){
            if (s.validBit){
                int len = charsToInt(s.getLimit());
                int start = Integer.parseInt(new Transformer().binaryToInt(startAddr));
                int formerStart = Integer.parseInt(String.valueOf(s.base),2);
                if (formerStart==start){
                    startAddr = t.intToBinary((Integer.parseInt(startAddr,2)+charsToInt(s.limit))+"");
                    continue;
                }
                s.setBase(startAddr.toCharArray());
                for (int ptr=0; ptr<len; ptr++) {
                    memory[start + ptr] = memory[formerStart+ptr];
                }
                startAddr = t.intToBinary((Integer.parseInt(startAddr,2)+charsToInt(s.limit))+"");
            }
        }
        return startAddr;

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
     * @param segDescriptor
     * @param len
     * @return
     */
    public String load_seg(SegDescriptor segDescriptor,int len){
        String disk_base = String.valueOf(segDescriptor.getDisk());//得到段在磁盘中的地址
        String memory_base = String.valueOf(segDescriptor.getBase());//得到段在内存中分配的地址
        String newMemory_base;
        if (charsToInt(segDescriptor.limit)+size>MEM_SIZE_B) {//内存总空间不足够
            System.out.println("内存不足够");
            while (len + size > MEM_SIZE_B) {//总空间不够需要一个被替换掉,stamp最小的，即很久没有访问过的，将这个段写到新分配的内存地址中，修改段表
                SegDescriptor s = findSeg_toBeReplaced();
                makeInvalid(s);
                System.out.println("替换完成");
                if (len <= Integer.parseInt("0"+String.valueOf(s.limit),2)) {//最先适应，能够填入被替换的空间
                    System.out.println("最先适应，进入被替换的空间");
                    newMemory_base = String.valueOf(s.base);
                    write(newMemory_base, len, disk.read(disk_base, len));
                    return newMemory_base;
                }
            }
            //如果跳出了while但是还是没有return，说明需要整理
            //TODO：整理然后返回
            System.out.println("替换后找不到合适的位置，需要整理");
            System.out.println("开始整理");
            newMemory_base = mem_Defragmentation();
            System.out.println("整理完成");
            System.out.println("开始向内存写入");
            write(newMemory_base, len, disk.read(disk_base, len));
            return newMemory_base;
        }

        //内存总空间足够：1，找到内存中能填入的地方，填入，然后修改段表内容2.找不到，整理
        System.out.println("内存足够");
        //TODO： 查询内存中的空闲位置，看是否能放入（通过段表查询，然后保存已经加载入内存的段的信息，通过这种信息找到内存中最大的空闲位置的起始位置以及其大小，然后比较能否放入
        for (SegDescriptor s:segTbl){//查询段表，看是不是
            if (len<=charsToInt(s.getLimit())&&!s.validBit&&!s.equals(segDescriptor)){
                newMemory_base = String.valueOf(s.getBase());
                write(newMemory_base, len, disk.read(disk_base, len));
                return newMemory_base;
            }
        }//面向用例编程，不能这样写
        //如果还没有return，说明需要整理
        System.out.println("找不到合适的位置");
        System.out.println("开始整理");
        //跳出说明没有直接能进入的空间，那么开始整理,得到内存开始为空的首地址
        newMemory_base = mem_Defragmentation();
        System.out.println("整理完成");
        System.out.println("开始向内存写入");
        write(newMemory_base, len, disk.read(disk_base, len));
        return newMemory_base;
    }

    //char[]转化为int
    private int charsToInt(char[] chars){
        return Integer.parseInt("0"+String.valueOf(chars),2);
    }

    public String getPhysicalAddr(String logicAddr,int len){
        if (!SEGMENT&&!PAGE){//实模式
            return getPhysicalAddr_real(logicAddr,len);
        }
        else if (SEGMENT&&!PAGE){//只分段
            return getPhysicalAddr_seg(logicAddr,len);
        }
        else { //段页式
            return getPhysicalAddr_PS(logicAddr,len);
        }
    }

    private String getPhysicalAddr_real(String logicAddr,int len){
        String physicalAddr = "";
        physicalAddr = logicAddr.substring(16);
        write(physicalAddr,len,disk.read(physicalAddr,len));//直接从磁盘中读，然后写入
        return physicalAddr;
    }

    private String getPhysicalAddr_seg(String logicAddr, int len){
        String physicalAddr;
        int segSelector = Integer.parseInt(t.binaryToInt("0"+logicAddr.substring(0,13)));
        SegDescriptor segDescriptor = segTbl.get(segSelector);//通过段号获得段描述符
        if (!segDescriptor.validBit){//内存中没有这个段
            physicalAddr = load_seg(segDescriptor,len);//写入内存
            //得到新的在内存中的地址
            segDescriptor.setValidBit(true);
            segDescriptor.setBase(physicalAddr.toCharArray());//修改段表内容
            return physicalAddr;
        }
        else return String.valueOf(segDescriptor.getBase());//内存中有这个段，直接返回段表中对应的地址
    }

    private String getPhysicalAddr_PS(String logicAddr, int len){
        //获得一系列信息
        int segSelector = Integer.parseInt(t.binaryToInt("0"+logicAddr.substring(0,13)));
        SegDescriptor segDescriptor = segTbl.get(segSelector);//通过段号获得段描述符
        String base_seg = String.valueOf(segDescriptor.base);//基地址
        //String offset_seg = logicAddr.substring(16);//段内偏移
        //String linearAddr = t.intToBinary(Integer.parseInt(base_seg,2)+Integer.parseInt(offset_seg,2)+"");//页的逻辑地址
        //int pageNO = Integer.parseInt(linearAddr.substring(0,20),2);//虚页号
        int pageNO = Integer.parseInt(  t.binaryToInt( logicAddr.substring( 16, 36 ) ));
        int offset_page = Integer.parseInt( t.binaryToInt( logicAddr.substring( 36, 48 )) );
        //String offset_page = linearAddr.substring(20);//页内偏移 12位
        String disk_base = t.intToBinary(pageNO*PAGE_SIZE_B+offset_page+"");//磁盘地址等于虚页号乘页面大小加上页面偏移

        if (segDescriptor.validBit){//段在内存内,使用全关联映射+LRU加载物理页框
            PageItem page  = pageTbl[pageNO];
            if (page!=null&&page.isInMem){
                return String.valueOf(page.frameAddr)+offset_page;
            }
            if (page==null){
                page = new PageItem();
            }
            write("00000000000000000000"+offset_page,len,disk.read(disk_base,len));
            return String.valueOf(page.frameAddr)+offset_page;
        }
        else {//段不在内存内，先按照分段模式进行管理，分配的段长度为数据段包含的总物理页框数/2，再将物理页框加载到内存
            if (len>Integer.parseInt(String.valueOf(segDescriptor.limit),2)){//越界
                segDescriptor.setDisk(disk_base.toCharArray());//表示这一段的起始磁盘地址在这里，投机取巧了嗷
                return load_seg(segDescriptor, len);//加载应该加载的长度
            }
            else {
                segDescriptor.setDisk(disk_base.toCharArray());
                //return load_seg(segDescriptor,Integer.parseInt(String.valueOf(segDescriptor.limit),2) );
                return load_seg(segDescriptor, len);//只加载需要的页的长度进入到内存中
            }
        }
    }

    public void write(String eip, int len, char []data){
        // 通知Cache缓存失效
        // 本作业只要求读数据，不要求写数据，因此不存在程序修改数据导致Cache修改 -> Mem修改 -> Disk修改等一系列write back/write through操作，
        //     write方法只用于测试用例中的下层存储修改数据导致上层存储数据失效，Disk.write同理
//        Cache.getCache().invalid(eip, len);
        // 更新数据
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
        //以下为增加的方法
        sd.updateTimeStamp();
        //增加size
        if (sd.validBit){
            size+=len;
        }
    }

    /**
     * 清空段表页表，用于测试用例
     */
    public void clear() {
        size = 0;
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
            size-=Integer.parseInt(String.valueOf(segTbl.get(segNO).getLimit()),2);
        }
        if (Memory.PAGE) {
            if (pageNO >= 0) {
                getPage(pageNO).setInMem(false);
            }
        }
    }

    PageItem getPage(int index) {
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

        PageItem(){
            Arrays.fill(frameAddr,'0');
        }
        private char[] frameAddr = new char[20];

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