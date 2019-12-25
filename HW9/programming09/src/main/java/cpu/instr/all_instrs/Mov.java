package cpu.instr.all_instrs;

import cpu.CPU;
import cpu.CPU_State;
import program.Log;

public class Mov implements Instruction {
    String instr;
    String MOD;
    String Reg_Opcode;
    String R_M;
    String addr;
    String baseAddr;
    String displacement;
    String data;
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;//指令的逻辑地址
        switch (opcode){
            case 184:
                instr = String.valueOf(mmu.read(logicAddr,40));
                Log.write(instr);
                return mov_184();
            case 199:
                instr = String.valueOf(mmu.read(logicAddr,80));
                Log.write(instr);
                return mov_199();
            case 139:
                instr = String.valueOf(mmu.read(logicAddr,48));
                Log.write(instr);
                return mov_139();
            case 137:
                instr = String.valueOf(mmu.read(logicAddr,48));
                Log.write(instr);
                return mov_137();
            default:return 0;
        }
    }
    private int mov_184(){
        String imme = instr.substring(8);
        CPU_State.eax.write(imme);
        return 40;
    }
    private int mov_199(){//对内存中的变量赋值 MOV Ev Iv
        MOD = instr.substring(8,10);
        Reg_Opcode = instr.substring(10,13);
        R_M = instr.substring(13,16);
        String displacement = instr.substring(16,48);
        String imme = instr.substring(48,80);
        if (MOD.equals("11")){
            //Ev表示寄存器
            switch (R_M){
                case "000":CPU_State.eax.write(imme);break;
                case "001":CPU_State.ecx.write(imme);break;
                case "010":CPU_State.edx.write(imme);break;
                case "011":CPU_State.ebx.write(imme);break;
                default:break;
            }
        }
        else {
            //Ev表示内存
            switch (R_M){
                case "000":baseAddr = CPU_State.eax.read();break;
                case "001":baseAddr = CPU_State.ecx.read();break;
                case "010":baseAddr = CPU_State.edx.read();break;
                case "011":baseAddr = CPU_State.ebx.read();break;
                default:baseAddr = "";
            }
            addr = alu.add(baseAddr,displacement);
            memory.write(addr,4*8,imme.toCharArray());
        }
        return 80;
    }
    private int mov_139(){//将内存中的值加载到寄存器 MOV Gv Ev
        MOD = instr.substring(8,10);
        Reg_Opcode = instr.substring(10,13);
        R_M = instr.substring(13,16);
        displacement = instr.substring(16,48);
        //取得数据
        if (MOD.equals("11")){
            //Ev表示寄存器
            switch (R_M){
                case "000":data = CPU_State.eax.read();break;
                case "001":data = CPU_State.ecx.read();break;
                case "010":data = CPU_State.edx.read();break;
                case "011":data = CPU_State.ebx.read();break;
                default:data = "000000000000000000000000000000";
            }
        }
        else {
            //Ev表示内存
            switch (R_M){
                case "000":baseAddr = CPU_State.eax.read();break;
                case "001":baseAddr = CPU_State.ecx.read();break;
                case "010":baseAddr = CPU_State.edx.read();break;
                case "011":baseAddr = CPU_State.ebx.read();break;
                default:baseAddr = "000000000000000000000000000000";
            }
            data = String.valueOf(mmu.read(CPU_State.cs.read()+alu.add(baseAddr,displacement),32));
        }
        switch (Reg_Opcode){
            case "000":CPU_State.eax.write(data);break;
            case "001":CPU_State.ecx.write(data);break;
            case "010":CPU_State.edx.write(data);break;
            case "011":CPU_State.ebx.write(data);break;
            default:break;
        }
        return 48;
    }
    private int mov_137(){//MOV Ev Gv
        MOD = instr.substring(8,10);
        Reg_Opcode = instr.substring(10,13);
        R_M = instr.substring(13,16);
        displacement = instr.substring(16);
        switch (Reg_Opcode){
            case "000":data = CPU_State.eax.read();break;
            case "001":data = CPU_State.ecx.read();break;
            case "010":data = CPU_State.edx.read();break;
            case "011":data = CPU_State.ebx.read();break;
            default:break;
        }
        if (MOD.equals("11")){
            //Ev表示寄存器
            switch (R_M){
                case "000":CPU_State.eax.write(data);break;
                case "001":CPU_State.ecx.write(data);break;
                case "010":CPU_State.edx.write(data);break;
                case "011":CPU_State.ebx.write(data);break;
                default:break;
            }
        }
        else {
            //Ev表示内存
            switch (R_M){
                case "000":baseAddr = CPU_State.eax.read();break;
                case "001":baseAddr = CPU_State.ecx.read();break;
                case "010":baseAddr = CPU_State.edx.read();break;
                case "011":baseAddr = CPU_State.ebx.read();break;
                default:baseAddr = "000000000000000000000000000000";
            }
            memory.write(alu.add(baseAddr,displacement),32,data.toCharArray());
        }
        return 48;
    }
}
