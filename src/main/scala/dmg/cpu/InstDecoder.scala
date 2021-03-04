import chisel3._
import chisel3.util._

object Instructions {

  // N  : Mem(PC+1)
  // NN : {Mem(PC+2), Mem(PC+1)}
  def LDRR   = BitPat("b01??????") // Load from X to Y.
  def LDRN   = BitPat("b00???110") // Load from N to X.
  def LDRHL  = BitPat("b01???110") // Load from Mem(HL) to X.
  def LDHLR  = BitPat("b01110???") // Load from Reg to Mem(HL).
  def LDHLN  = BitPat("b01110110") // Load from N to Mem(HL).
  def LDABC  = BitPat("b00001010") // Load from MEM(BC) to A.
  def LDADE  = BitPat("b00011010") // Load from MEM(DE) to A.
  def LDBCA  = BitPat("b00000010") // Load from A to MEM(BC).
  def LDDEA  = BitPat("b00010010") // Load from A to MEM(DE).
  def LDANN  = BitPat("b11111010") // Load from 16bit Mem(NN) to A.
  def LDNNA  = BitPat("b11101010") // Load from A to 16bit Mem(NN).
  def LDHAC  = BitPat("b11110010") // Load from Mem(C) to A.
  def LDHCA  = BitPat("b11100010") // Load from A to Mem(C).
  def LDHAN  = BitPat("b11110000") // Load from Mem(N) to A.
  def LDHNA  = BitPat("b11100000") // Load from A to Mem(N).
  def LDAHLD = BitPat("b00111010") // Load from Mem(HL--) to A.
  def LDHLDA = BitPat("b00110010") // Load from A to Mem(HL--).
  def LDAHLI = BitPat("b00111010") // Load from Mem(HL++) to A.
  def LDHLIA = BitPat("b00110010") // Load from A to Mem(HL++).
  def LDRRNN = BitPat("b00??0001") // Load from 16bit Mem(NN) to 16bit X.
  def LDNNSP = BitPat("b00001000") // Load from SP to 16bit Mem(NN)
  def LDSPHL = BitPat("b11111001") // Load from HL to Mem(HL++)
}

class InstDecoder extends Module {
  val io = IO(new Bundle {
    val inst = UInt(8.W)
    val opcode = UInt(2.W)
    val src_reg = UInt(3.W)
    val dst_reg = UInt(3.W)
  })
}
