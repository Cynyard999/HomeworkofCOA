package cpu.instr.all_instrs;

import com.sun.xml.internal.ws.api.server.InstanceResolverAnnotation;
import cpu.CPU;
import cpu.CPU_State;

public class Push implements Instruction {
    public int exec(String eip, int opcode){
        String segReg = CPU_State.cs.read();
        String logicAddr = segReg+eip;
        switch (opcode){
            case 83:
                return push_83(logicAddr);
            default:return 0;
        }
    }
    private int push_83(String logicAddr){
        memory.pushStack(CPU_State.esp.read(),CPU_State.ebx.read());
        return 8;
    }

}
