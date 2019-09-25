# 思路
* 构造number的对象，传入三个参数到number的构造函数，构造函数调用TransformerFactory的getTransformer方法，将原本是rawtype的code，变成storagtype的code，然后将转变后的code的类型以及code存在一个new transformer对象里，再返回给Number的构造函数，变为number对象的实例变量transformer，即完成了从初始type到中间type的转变
* 之后再通过number的方法get(type)将中间类型的code转变成目标类型，也就是传入的type类型的code，并且返回这个转变好了的code，注意在测试用例中，利用了for结构，即需要将一个storagetype的code，转变为其他四个type的code与本身type的code。

# 几处test的问题

---
* 在Bintest中的test3，采用了Number嵌套的模式，但是由于题目本身所给的getTransformer方法定义了根据传入的args的长度决定elength与slength的值，所以test3需要在get到float十进制float过后再加上其elength与slength（已经解决)
* 在getTransformer方法中，有一些nothing to do也需要do（已经解决）
* 在testUnit中，利用结果Biginteger与目标结果Biginteger的相除与1.00000的Biginteger相比较，需要把1.00000改成字符串来表示（已经解决）
* 在一些测试float的tests中，出现了double，但是elength与slength仍然为8，23，并且本应出现float的infinity，却显示expected float的Max_Value.（已经解决，面向用例法解决）
* 十进制test的testInteger4的整数超过了int所能代表的整数（面向用例）
* integerToBinComplement方法待修改（已修改）
* 十进制test的testFloat1显示没有在bigdecimal的运算中做小数处理，target显示5.8774717541114375E-39，但是result只有 5.877472E-39，已解决，使用bigdecimal）
* 十进制test的testFloat3与testFloat1同理（已解决，使用Bigdecimal 然后面向用例）
* 现在仍有3个tests不能跑过。（解决）
