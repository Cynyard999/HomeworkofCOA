package cpu.instr.all_instrs;

import cpu.CPU_State;

public class Hlt implements Instruction{
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        return 8;
    }
}
