## COA2019
* stamp完全可以调用系统的现在的时间，即System.currentTimeMills.
思路
* 首先设置映射策略以及替换策略 setStrategy

* 给定char的数组以及其在memory中的首地址，用memory的write写入

* 再用cache的read解析这个地址，再给出需要读出的数据的个数（即上面字符数组的长度）

* 注意地址用的是十进制来表示

* cache一共1024行

* miss过后，读的数据是从更新了过后的cache中读取的，实际应该是从memory中读取

* 每个mapping的tag长度不同，但是需要在比较的时候变成22位

* 填充的时候，首先填补cache中为invalid的

* 每个替换策略，每个mapping的具体实现在相应的java文件里

* 测试用例里， direct mapping最后需要clear或者在setMapping方法中，添加clear；不然在组关联策略里的test1不能通过，因为用例假定的是cache为空的情况





  ------

  
1. 
在Cache类中实现fetch方法将数据从内存读到Cache(如果还没有加载到Cache)
并返回被更新的Cache行的行号(需要调用不同的映射策略和替换策略)

``` java
 String fetch(String sAddr, int len){
```

2.

实现三种映射策略(直接映射、全关联映射、组关联映射)和三种替换策略(先进先出、最少使用、最近使用)

``` java
 String sub(String a, String b){
```

