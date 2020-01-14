package cpu.instr.all_instrs;

import cpu.CPU_State;
import program.Log;

public class Jmp implements Instruction{
    String instr;
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        instr = String.valueOf(mmu.read(logicAddr,16));
        Log.write(instr);
        String imme = instr.substring(8);
        CPU_State.eip.write(alu.add(CPU_State.eip.read(),imme));
        return 0;
    }
}
