package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.registers.EFlag;

public class Jz implements Instruction{
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        switch (opcode){
            case 116:
                return Jz_116(logicAddr);
            default:return 0;
        }
    }
    private int Jz_116(String logicAddr){
        EFlag eflag =  (EFlag) CPU_State.eflag;
        String imme = String.valueOf(mmu.read(logicAddr,16)).substring(8);
        if (eflag.getZF()){
            CPU_State.eip.write(alu.add(imme,CPU_State.eip.read()));
            return 0;
        }
        else {
            return 16;
        }
    }

}
