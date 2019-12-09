package cpu.instr.all_instrs;

import cpu.CPU_State;

public class Pop implements Instruction{
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        switch (opcode){
            case 88:
                return pop_88(logicAddr);
            case 89:
                return pop_89(logicAddr);
            case 90:
                return pop_90(logicAddr);
            default:return 0;
        }
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
