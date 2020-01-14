package cpu.instr.all_instrs;

import cpu.CPU_State;

public class Cmp implements Instruction {
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        switch (opcode){
            case 61:
                return cmp_61(logicAddr);
            default:return 0;
        }
    }
    private int cmp_61(String logicAddr){
        String imme = String.valueOf(mmu.read(logicAddr,40)).substring(8);
        if(alu.sub(CPU_State.eax.read(),imme).equals("00000000000000000000000000000000")){
            eflag.setZF(true);
        }
        else if (alu.sub(CPU_State.eax.read(),imme).substring(0,1).equals("1")){//imme < eax
            eflag.setZF(false);
        }
        else {
            eflag.setZF(false);
        }
        return 40;
    }

}
