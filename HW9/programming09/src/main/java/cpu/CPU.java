package cpu;

import cpu.instr.all_instrs.InstrFactory;
import cpu.instr.all_instrs.Instruction;
import transformer.Transformer;

public class CPU {

    Transformer transformer = new Transformer();
    MMU mmu = MMU.getMMU();


    /**
     * execInstr specific numbers of instructions
     *
     * @param number numbers of instructions
     */
    public int execInstr(long number) {
        // 执行过的指令的总长度
        int totalLen = 0;
        while (number > 0) {
            totalLen+=execInstr();
            number--;
        }
        return totalLen;
    }

    /**
     * execInstr a single instruction according to eip value
     */
    private int execInstr() {
        String eip = CPU_State.eip.read();
        int len = decodeAndExecute(eip);
        //注意如果len传回来为0，那么eip已经被改变了，所以还是需要+0；但是需要的是读取新的eip的值
        CPU_State.eip.write(transformer.intToBinary(Integer.parseInt(CPU_State.eip.read(),2)+len+""));
        return len;
    }

    private int decodeAndExecute(String eip) {
        int opcode = instrFetch(eip, 8);
        Instruction instruction = InstrFactory.getInstr(opcode);
        assert instruction != null;
        //exec the target instruction
        int len = instruction.exec(eip, opcode);
        return len;


    }

    /**
     * @param eip
     * @param length opcode的字节数，本作业只使用单字节opcode
     * @return
     */
    private int instrFetch(String eip, int length) {
        // TODO X   FINISHED √
        String segReg = CPU_State.cs.read();
        char[] opcode = mmu.read(segReg+eip,length);
        return Integer.valueOf(transformer.binaryToInt(String.valueOf(opcode)));
    }

    public void execUntilHlt(){
        while (true){
            if (execInstr()==8){
                break;
            }
        }
    }

}

