## COA2019
---
* 13/10 在alu中添加了CF,OF对应的操作，并且改动方法，使其对任意长度的字符串都有效，添加了找相反数的方法
* 13/10 写完了FPU，完全按照流程，细节需要修改
* alu在溢出和进位那里有问题，应该返回的是相同长度的值（已修改）
* 在NBCDU中用OF判定有问题（已经解决）
* alu中的一些方法并没有匹配所有长度的字符串（已修改）
* 只要运算涉及到了NAN，那么result就是NAN



在ALU类中实现12个方法(别担心，绝大部分都很简单)，具体如下
注意，所有参数和返回值都是32位的二进制数，其中除了位移运算的src参数为无符号数，其余参数均为有符号的整数补码


1.计算两个32位二进制整数补码真值的和
``` java
 String add(String src, String sub)
```

2.计算两个32位二进制整数补码真值的差，dest表示被减数，src表示减数(即计算dest - src)
``` java
 String sub(String src, String dest)
```

3.计算两个32位有符号二进制数的模，dest表示被除数，src表示除数(dest mod src)
``` java
 String imod(String src, String dest)
```

4.与运算
``` java
 String and(String src, String dest)
```

5.或运算
``` java
 String or(String src, String dest)
```

6.异或运算
``` java
 String xor(String src, String dest)
```

7.逻辑左移运算，其中dest表示操作数(有符号)，src表示移动位数(无符号)，8-12同理
``` java
 String shl(String src, String dest)
```

8.逻辑右移
``` java
 String shr(String src, String dest)
```

9.算数左移
``` java
 String sal(String src, String dest)
```

10.算术右移
``` java
 String sar(String src, String dest)
```

11.循环左移
``` java
 String rol(String src, String dest)
```

12.循环右移
``` java
 String ror(String src, String dest)
```

---

在FPU类中实现2个方法，具体如下

1.计算两个浮点数真值的和，参数与返回结果为32位单精度浮点数。（符号位、指数部分与尾数部分分别为1、8、23位。）
``` java
 String add(String a, String b)
```

2.计算两个浮点数真值的差
``` java
 String sub(String a, String b)
```

---

在NBCDU类中实现2个方法，具体如下

1.计算两个32位NBCD(8421码)的和，参数与返回结果为32位NBCD码。（前四位为符号位，"1100"表示正数，"1101"表示负数，0用正数符号位表示）
``` java
 String add(String a, String b)
```

2.计算两个32位NBCD(8421码)的差，b表示被减数，a表示减数(即计算b - a)
``` java
 String sub(String a, String b)
```