package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.registers.EFlag;

public class Adc implements Instruction {
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        switch (opcode){
            case 21:
                return addc_21(logicAddr);
            default:return 0;
        }
    }
    private int addc_21(String logicAddr){
        EFlag eFlag = (EFlag) CPU_State.eflag;
        String imme = String.valueOf(mmu.read(logicAddr,40)).substring(8);
        if (eFlag.getCF()){
            CPU_State.eax.write(alu.add("00000000000000000000000000000001",alu.add(imme, CPU_State.eax.read())));
        }
        else
            CPU_State.eax.write(alu.add(imme, CPU_State.eax.read()));
        return 40;
    }

}
