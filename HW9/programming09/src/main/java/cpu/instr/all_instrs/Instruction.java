package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.registers.EFlag;
import memory.Memory;
import transformer.Transformer;

public interface Instruction {
    Transformer transformer = new Transformer();
    MMU mmu = MMU.getMMU();
    Memory memory = Memory.getMemory();
    ALU alu = new ALU();
    int exec(String eip, int opcode);
    EFlag eflag = (EFlag)CPU_State.eflag;
}
