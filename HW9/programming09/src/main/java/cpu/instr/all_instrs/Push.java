package cpu.instr.all_instrs;

import cpu.CPU_State;

public class Push implements Instruction {
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        CPU_State.esp.write(transformer.intToBinary(Integer.parseInt(CPU_State.esp.read(),2)-1+""));//栈指针-1,向低地址压栈
        int len = 0;
        switch (opcode){
            case 83:
                len =  push_83(logicAddr);break;
            default:len = 0;
        }
        return len;
    }
    private int push_83(String logicAddr){
        memory.pushStack(CPU_State.esp.read(), CPU_State.ebx.read());
        return 8;
    }

}
