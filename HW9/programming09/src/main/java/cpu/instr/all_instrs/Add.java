package cpu.instr.all_instrs;

import cpu.CPU_State;

public class Add implements Instruction {
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        switch (opcode){
            case 5:
                return add_5(logicAddr);
            default:return 0;
        }
    }
    private int add_5(String logicAddr){
        String imme = String.valueOf(mmu.read(logicAddr,40)).substring(8);
        CPU_State.eax.write(alu.add(imme, CPU_State.eax.read()));
        return 40;
    }
}
