package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.alu.ALU;
import program.Log;
import transformer.Transformer;

public class Cmp implements Instruction {
    String instr;
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        switch (opcode){
            case 61:
                instr = String.valueOf(mmu.read(logicAddr,40));
                Log.write(instr);
                return cmp_61();
            case 57:
                instr = String.valueOf(mmu.read(logicAddr,16));
                Log.write(instr);
                return cmp_57();
            default:return 0;
        }
    }
    private int cmp_61(){
        String imme = instr.substring(8);
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
    private int cmp_57(){//cmp Ev Gv
        String a;//Load Ev
        String b;//Load Gv
        String MOD = instr.substring(8,10);
        String Reg_Opcode = instr.substring(10,13);
        String R_M = instr.substring(13,16);
        String zero = "00000000000000000000000000000000";
        switch (Reg_Opcode){
            case "000":b = CPU_State.eax.read();break;
            case "001":b = CPU_State.ecx.read();break;
            case "010":b = CPU_State.edx.read();break;
            case "011":b = CPU_State.ebx.read();break;
            default:b = zero;break;
        }
        if (MOD.equals("11")){
            //Ev表示寄存器
            switch (R_M){
                case "000":a = CPU_State.eax.read();break;
                case "001":a = CPU_State.ecx.read();break;
                case "010":a = CPU_State.edx.read();break;
                case "011":a = CPU_State.ebx.read();break;
                default:a = zero;break;
            }
        }
        else {
            //Ev表示内存
            //这个指令没必要
            System.out.println("Wrong Instruction");
            return 0;
        }
        if(Integer.parseInt(transformer.binaryToInt(new ALU().sub(b,a)))>0){
            eflag.setZF(false);
            eflag.setOF(false);
            eflag.setSF(false);
        }
        else if(Integer.parseInt(new Transformer().binaryToInt(new ALU().sub(b,a)))==0) {
            eflag.setZF(true);
            eflag.setOF(false);
            eflag.setSF(false);
        }
        else if(Integer.parseInt(new Transformer().binaryToInt(new ALU().sub(b,a)))<0){
            eflag.setZF(false);
            eflag.setOF(false);
            eflag.setSF(true);
        }
        return 16;
    }

}
