package cpu.instr.all_instrs;

import cpu.CPU_State;

public class Sbb implements Instruction {
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        switch (opcode){
            case 29:
                return sbb_29(logicAddr);
            default:return 0;
        }
    }
    private int sbb_29(String logicAddr){
        String imme = String.valueOf(mmu.read(logicAddr,40)).substring(8);
        CPU_State.eax.write(alu.sub("00000000000000000000000000000001",alu.sub(imme, CPU_State.eax.read())));
        return 40;
    }

}
