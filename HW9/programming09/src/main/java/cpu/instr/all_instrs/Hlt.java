package cpu.instr.all_instrs;

import cpu.CPU_State;
import program.Log;

public class Hlt implements Instruction {
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        Log.write(String.valueOf(mmu.read(logicAddr,8)));
        return 8;
    }
}
