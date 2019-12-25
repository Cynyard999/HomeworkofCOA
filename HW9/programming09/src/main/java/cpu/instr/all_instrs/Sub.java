package cpu.instr.all_instrs;

import cpu.CPU_State;

public class Sub implements Instruction {
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        switch (opcode){
            case 45:
                return sub_45(logicAddr);
            default:return 0;
        }
    }
    private int sub_45(String logicAddr){
        String imme = String.valueOf(mmu.read(logicAddr,40)).substring(8);
        CPU_State.eax.write(alu.sub(imme, CPU_State.eax.read()));
        return 40;
    }

}
