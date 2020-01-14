package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.registers.EFlag;
import program.Log;

public class Jz implements Instruction {
    String instr;
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        switch (opcode){
            case 116:
                instr = String.valueOf(mmu.read(logicAddr,16));
                Log.write(instr);
                return Jz_116();
            default:return 0;
        }
    }
    private int Jz_116(){
        EFlag eflag =  (EFlag) CPU_State.eflag;
        String imme = instr.substring(8);
        if (eflag.getZF()){//为0
            CPU_State.eip.write(alu.add(imme, CPU_State.eip.read()));
            return 0;
        }
        else {
            CPU_State.eip.write(alu.add(CPU_State.eip.read(),"00000000000000000000000000010000"));
            return 0;
        }
    }

}
