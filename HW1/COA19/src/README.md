# 几处test的问题
---
* 在Bintest中的test3，采用了Number嵌套的模式，但是由于题目本身所给的getTransformer方法定义了根据传入的args的长度决定elength与slength的值，所以test3需要在get到float十进制float过后再加上其elength与slength（已经解决)
* 在getTransformer方法中，有一些nothing to do也需要do（已经解决）
* 在testUnit中，利用结果Biginteger与目标结果Biginteger的相除与1.00000的Biginteger相比较，需要把1.00000改成字符串来表示（已经解决）
* 在一些测试float的tests中，出现了double，但是elength与slength仍然为8，23，并且本应出现float的infinity，却显示expected float的Max_Value.（正在解决中）
* 现在仍有3-4个tests不能跑过。（正在解决中）
