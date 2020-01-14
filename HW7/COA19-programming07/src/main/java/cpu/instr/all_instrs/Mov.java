package cpu.instr.all_instrs;

import cpu.CPU_State;

public class Mov implements Instruction {
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        switch (opcode){
            case 184:
                return mov_184(logicAddr);
            default:return 0;
        }
    }
    private int mov_184(String logicAddr){
        String imme = String.valueOf(mmu.read(logicAddr,40)).substring(8);
        CPU_State.eax.write(imme);
        return 40;
    }
}
