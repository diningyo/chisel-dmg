import chisel3._
import chisel3.util._

object Instructions {

  // N  : Mem(PC+1)
  // NN : {Mem(PC+2), Mem(PC+1)}
  // Load
  def LDRR     = BitPat("b01??????") // Load from X to Y.
  def LDRN     = BitPat("b00???110") // Load from N to X.
  def LDRHL    = BitPat("b01???110") // Load from Mem(HL) to X.
  def LDHLR    = BitPat("b01110???") // Load from Reg to Mem(HL).
  def LDHLN    = BitPat("b01110110") // Load from N to Mem(HL).
  def LDABC    = BitPat("b00001010") // Load from MEM(BC) to A.
  def LDADE    = BitPat("b00011010") // Load from MEM(DE) to A.
  def LDBCA    = BitPat("b00000010") // Load from A to MEM(BC).
  def LDDEA    = BitPat("b00010010") // Load from A to MEM(DE).
  def LDANN    = BitPat("b11111010") // Load from 16bit Mem(NN) to A.
  def LDNNA    = BitPat("b11101010") // Load from A to 16bit Mem(NN).
  def LDHAC    = BitPat("b11110010") // Load from Mem(C) to A.
  def LDHCA    = BitPat("b11100010") // Load from A to Mem(C).
  def LDHAN    = BitPat("b11110000") // Load from Mem(N) to A.
  def LDHNA    = BitPat("b11100000") // Load from A to Mem(N).
  def LDAHLD   = BitPat("b00111010") // Load from Mem(HL--) to A.
  def LDHLDA   = BitPat("b00110010") // Load from A to Mem(HL--).
  def LDAHLI   = BitPat("b00111010") // Load from Mem(HL++) to A.
  def LDHLIA   = BitPat("b00110010") // Load from A to Mem(HL++).
  def LDRRNN   = BitPat("b00??0001") // Load from 16bit Mem(NN) to 16bit X.
  def LDNNSP   = BitPat("b00001000") // Load from SP to 16bit Mem(NN).
  def LDSPHL   = BitPat("b11111001") // Load from HL to Mem(HL++).
  // Push/Pop
  def PUSHRR   = BitPat("b11??0101") // Push RR to stack.
  def POPRR    = BitPat("b11??0001") // Pop stack to RR.

  // Arithmetic
  def ADDAR    = BitPat("b10000???") // Add A and R.
  def ADDAN    = BitPat("b11000110") // Add A and N.
  def ADDAHL   = BitPat("b10000110") // Add A and Mem(HL).
  def ADCAR    = BitPat("b10001???") // Add A and R and Carry.
  def ADCAN    = BitPat("b11001110") // Add A and N and Carry.
  def ADCAHL   = BitPat("b10001110") // Add A and Mem(HL) and Carry.

  // Control
  def JPNN     = BitPat("b11000011") // Jump to NN.
  def JPHL     = BitPat("b11101001") // Jump to HL.
  // CC : branch condition
  def JPCCNN   = BitPat("b110??010") // Conditional (CC) Jump to NN.
  def JPCCE    = BitPat("b001??000") // Conditional (CC) Jump to E.
  // E : singed_8(PC)
  def JPE      = BitPat("b00011000") // Unconditional relative Jump to E.
  def CALLNN   = BitPat("b11001101") // Unconditional function call to NN.
  def CALLCCNN = BitPat("b110??100") // Conditional (CC) function call to NN.
  def RET      = BitPat("b11001001") // Unconditional return from function.
  def RETCC    = BitPat("b110??000") // Conditional (CC) return from function.
  def RETI     = BitPat("b11011001") // Unconditional return from function / set IME = 1
  def RSTN     = BitPat("b11???111") // Unconditional function call to abs. address.

  // Miscellaneous
  //def HALT     = BitPat("b11???111") // Unconditional function call to abs. address.
  //def STOP     = BitPat("b11???111") // Unconditional function call to abs. address.
  def DI       = BitPat("b11110011") // Disable interrupts (means set IME = 0).
  def EI       = BitPat("b11111011") // Enable interrupts (means set IME = 1).
  def CCF      = BitPat("b00111111") // Flips C flag, clears N and H flags.
  def SCF      = BitPat("b00110111") // Sets C flag, clears N and H flags.
  def NOP      = BitPat("b00000000") // No-operation.
  def DAA      = BitPat("b00100111") // Flips Z and C flags, clear H flag?
  def CPL      = BitPat("b00101111") // Flips A register, sets N and H flags.

}

class InstDecoder extends Module {
  val io = IO(new Bundle {
    val inst = UInt(8.W)
    val opcode = UInt(2.W)
    val src_reg = UInt(3.W)
    val dst_reg = UInt(3.W)
  })
}
