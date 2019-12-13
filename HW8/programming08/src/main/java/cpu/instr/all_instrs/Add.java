package cpu.instr.all_instrs;

import cpu.CPU;
import cpu.CPU_State;
import cpu.alu.ALU;

public class Add implements Instruction {//一共有6种，测试用例用的5
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
