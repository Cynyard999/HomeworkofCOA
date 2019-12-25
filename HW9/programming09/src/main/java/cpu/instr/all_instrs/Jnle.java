package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.alu.ALU;
import cpu.registers.EFlag;
import program.Log;

public class Jnle implements Instruction {
    String instr;
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        switch (opcode){
            case 127:
                instr = String.valueOf(mmu.read(logicAddr,16));
                Log.write(instr);
                return Jnle_127();
            default:return 0;
        }
    }
    private int Jnle_127(){
        EFlag eflag =  (EFlag) CPU_State.eflag;
        String imme = instr.substring(8);

        if(!eflag.getZF()&&!eflag.getOF()&&!eflag.getSF()) {//大于就跳转，需要是正号 不为0 没有进位
            CPU_State.eip.write(alu.add(CPU_State.eip.read(),imme));
        }
        else {
            CPU_State.eip.write(alu.add(CPU_State.eip.read(),"00000000000000000000000000010000"));
        }
        return 0;
    }
}
