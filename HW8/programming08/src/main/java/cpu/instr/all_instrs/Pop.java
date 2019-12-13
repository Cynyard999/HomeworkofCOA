package cpu.instr.all_instrs;

import cpu.CPU_State;

public class Pop implements Instruction{
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        int len;
        switch (opcode){
            case 88:
                len =  pop_88(logicAddr);break;
            case 89:
                len =  pop_89(logicAddr);break;
            case 90:
                len =  pop_90(logicAddr);break;
            default:len =  0;
        }
        CPU_State.esp.write(transformer.intToBinary(Integer.parseInt(CPU_State.esp.read(),2)+1+""));//栈指针+1
        return len;
    }
    private int pop_88(String logicAddr){
        String value = memory.topOfStack(CPU_State.esp.read());
        CPU_State.eax.write(value);
        return 8;
    }
    private int pop_89(String logicAddr){
        String value = memory.topOfStack(CPU_State.esp.read());
        CPU_State.ecx.write(value);
        return 8;
    }
    private int pop_90(String logicAddr){
        String value = memory.topOfStack(CPU_State.esp.read());
        CPU_State.edx.write(value);
        return 8;
    }

}
