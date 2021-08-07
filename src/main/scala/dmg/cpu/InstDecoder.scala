import chisel3._
import chisel3.stage._
import chisel3.util._
import chisel3.experimental.ChiselEnum

import Chisel.chiselMain

object Instructions {

  /*
   * Basiccaly, instruction formats are follwing cases:
   *
   * 1. OODDDSSS
   * 2. OODDDOOO
   * 3. OOOOOSSS
   * 4. OORPOOOO
   *
   * O   : opcode
   * DDD : Destination register
   * SSS : Source register
   *
   *   DDD or SSS | register name
   *   -----------+---------------
   *    111       | A
   *    000       | B
   *    001       | C
   *    010       | D
   *    011       | E
   *    100       | H
   *    101       | L
   *
   * RP  : Register pair
   *
   *    RP  | register name
   *   -----+---------------
   *    00  | B-C
   *    01  | D-E
   *    10  | H-L
   *    11  | SP
   */
  def A = "b111".U
  def B = "b000".U
  def C = "b001".U
  def D = "b010".U
  def E = "b011".U
  def H = "b100".U
  def L = "b101".U

  def BC = "b00".U
  def DE = "b01".U
  def HL = "b10".U
  def SP = "b11".U

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
  def LDAHLI   = BitPat("b00101010") // Load from Mem(HL++) to A.
  def LDAHLD   = BitPat("b00111010") // Load from Mem(HL--) to A.
  def LDBCA    = BitPat("b00000010") // Load from A to MEM(BC).
  def LDDEA    = BitPat("b00010010") // Load from A to MEM(DE).
  def LDANN    = BitPat("b11111010") // Load from 16bit Mem(NN) to A.
  def LDNNA    = BitPat("b11101010") // Load from A to 16bit Mem(NN).
  def LDHAC    = BitPat("b11110010") // Load from Mem(C) to A.
  def LDHCA    = BitPat("b11100010") // Load from A to Mem(C).
  def LDHAN    = BitPat("b11110000") // Load from Mem(N) to A.
  def LDHNA    = BitPat("b11100000") // Load from A to Mem(N).
  def LDHLDA   = BitPat("b00110010") // Load from A to Mem(HL--).
  def LDHLIA   = BitPat("b00110010") // Load from A to Mem(HL++).
  def LDRPNN   = BitPat("b00??0001") // Load from 16bit Mem(NN) to 16bit X.
  def LDNNSP   = BitPat("b00001000") // Load from SP to 16bit Mem(NN).
  def LDSPHL   = BitPat("b11111001") // Load from HL to Mem(HL++).
  // Push/Pop
  def PUSHRP   = BitPat("b11??0101") // Push RP to stack.
  def POPRP    = BitPat("b11??0001") // Pop stack to RP.

  // Arithmetic
  // 8-bit
  def ADDAR    = BitPat("b10000???") // Add A to R.
  def ADDAN    = BitPat("b11000110") // Add A to N.
  def ADDAHL   = BitPat("b10000110") // Add A to Mem(HL).
  def ADCAR    = BitPat("b10001???") // Add A to R and Carry.
  def ADCAN    = BitPat("b11001110") // Add A to N and Carry.
  def ADCAHL   = BitPat("b10001110") // Add A to Mem(HL) and Carry.
  def SUBAR    = BitPat("b10010???") // Sub A from R.
  def SUBAN    = BitPat("b11010110") // Sub A from N.
  def SUBAHL   = BitPat("b10010110") // Sub A from Mem(HL).
  def SUCAR    = BitPat("b10011???") // Sub A from R and Carry.
  def SUCAN    = BitPat("b11011110") // Sub A from N and Carry.
  def SUCAHL   = BitPat("b10011110") // Sub A from Mem(HL) and Carry.
  def ANDAR    = BitPat("b10100???") // AND A and R.
  def ANDAN    = BitPat("b11100110") // AND A and N.
  def ANDAHL   = BitPat("b10100110") // AND A and Mem(HL).
  def XORAR    = BitPat("b10101???") // XOR A and R.
  def XORAN    = BitPat("b11101110") // XOR A and N.
  def XORAHL   = BitPat("b10101110") // XOR A and Mem(HL).
  def ORAR     = BitPat("b10100???") // OR A and R.
  def ORAN     = BitPat("b11110110") // OR A and N.
  def ORAHL    = BitPat("b10100110") // OR A and Mem(HL).
  def CPAR     = BitPat("b10101???") // Compare A and R.
  def CPAN     = BitPat("b11111110") // Compare A and N.
  def CPAHL    = BitPat("b10101110") // Compare A and Mem(HL).
  def INCR     = BitPat("b00???100") // Increment R.
  def INCHL    = BitPat("b00110100") // Increment Mem(HL).
  def DECR     = BitPat("b00???101") // Decrement R.
  def DECHL    = BitPat("b00110101") // Decrement Mem(HL).
  def DAA      = BitPat("b00100111") // Decimal adjust accmulator.

  // 16-bit
  def ADDHLRP  = BitPat("b0???1001") // 16bit Add HL to RP.
  def INCRP    = BitPat("b0???0100") // Increment RP.
  def DECRP    = BitPat("b0???1010") // Decrement RP.
  def ADDSPR8  = BitPat("b11101000") // Add SP to Singed 8-bit.
  def LDHLSPR8 = BitPat("b11111000") // Load from SP + Singed 8-bit to HL.

  // Rotate and Shift
  def RLCA     = BitPat("b00001110") // Rotate accumlator left.
  def RLA      = BitPat("b00011110") // Rotate accumlator left through carry.
  def RRCA     = BitPat("b00001111") // Rotate accumlator right.
  def RRA      = BitPat("b00011111") // Rotate accumlator right through carry.

  // Prefixed commands
  def PREFIXED = BitPat("b11001010") // This is a byte signiture for 16-bit commands.
  def RLCR     = BitPat("b00000???") // Rotate left.
  def RLCHL    = BitPat("b00000110") // Rotate left.
  def RLR      = BitPat("b00010???") // Rotate left through carry.
  def RLHL     = BitPat("b00010110") // Rotate left through carry.
  def RRCR     = BitPat("b00001???") // Rotate left.
  def RRCHL    = BitPat("b00001110") // Rotate left.
  def RRR      = BitPat("b00011???") // Rotate left through carry.
  def RRHL     = BitPat("b00011110") // Rotate left through carry.
  def SLAR     = BitPat("b00100???") // Shift left arithmetic.
  def SLAHL    = BitPat("b00100110") // Shift left arithmetic.
  def SRAR     = BitPat("b00101???") // Shift right arithmetic.
  def SRAHL    = BitPat("b00101110") // Shift right arithmetic.
  def SWAPR    = BitPat("b00110???") // Exchange low/hi-nibble.
  def SWAPHL   = BitPat("b00110110") // Exchange low/hi-nibble.
  def SRLR     = BitPat("b00111???") // Shift right logical.
  def SRLHL    = BitPat("b00111110") // Shift right logical.
  // Single-bit - bit[5:3] = N / bit[2:0] = R
  def BITR     = BitPat("b01??????") // Test bit n.
  def BITHL    = BitPat("b01???110") // Test bit n.
  def RESR     = BitPat("b10??????") // Reset bit n.
  def RESHL    = BitPat("b10???110") // Reset bit n.
  def SETR     = BitPat("b11??????") // Set bit n.
  def SETHL    = BitPat("b11???110") // Set bit n.

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
  def CPL      = BitPat("b00101111") // Flips A register, sets N and H flags.
}

object OP extends ChiselEnum {
  val LD, LDRHL, LDINC, LDDEC, STORE, STOREINC, STOREDEC = Value
  val PUSH, POP = Value
  val ADD, ADC, SUB, SUC, AND, XOR, OR, CP, INC, DEC, DAA, CPL = Value
  val PREFIXED = Value
  val RLCA, RLA, RRCA, RRA, RLC, RL, RRC, RR, SLA, SWAP, SRA, SRL = Value
  val BIT, SET, RES = Value
  val CCF, SCF, NOP, HALT, STOP, DI, EI = Value
  val JP, JR, CALL, RET, RETI, RST = Value
}

class DecodedInst extends Bundle {
  val op = OP()
  val cycle = UInt(3.W)
  val is_prefixed = Bool()
  val is_imm = Bool()
  val is_mem = Bool()
  val is_dst_rp = Bool()
  val is_src_rp = Bool()
  val dst = UInt(3.W)
  val src = UInt(3.W)
}

class InstDecoder extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(8.W))
    val decoded = ValidIO(new DecodedInst)
  })

  io := DontCare

//  def decode(imm: Bool, rp: Bool, dst: UInt, src: UInt) = {
//    val d = Wire(new DecodedInst())
//    d.is_imm := imm
//    d.is_dst_rp := dst_rp
//    d.is_src_rp := dst_rp
//    d.dst := dst
//    d.src := src
//
//    d
//  }
//
//  import Instructions._
//
//  val dst_reg = io.inst(5, 3)
//  val src_reg = io.inst(2, 0)
//  val rp      = io.inst(5, 4)
//
//  //
//  val decode_table = Array(
//    // OP    ->      cycle,          is_imm,    is_rp,     dst,     src
//    LDRR     -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDRN     -> List(false.B, decode(true.B,  false.B, dst_reg, src_reg)),
//    LDRHL    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDHLR    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDHLN    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDABC    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDADE    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDBCA    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDDEA    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDANN    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDNNA    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDHAC    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDHCA    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDHAN    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDHNA    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDAHLD   -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDHLDA   -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDAHLI   -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDHLIA   -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDRPNN   -> List(false.B, decode(false.B, true.B,  rp,      src_reg)),
//    LDNNSP   -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    LDSPHL   -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    PUSHRP   -> List(false.B, decode(false.B, true.B,  dst_reg, rp)),
//    POPRP    -> List(false.B, decode(false.B, true.B,  rp,      src_reg)),
//    ADDAR    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    ADDAN    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    ADDAHL   -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    ADCAR    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    ADCAN    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    ADCAHL   -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    SUBAR    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    SUBAN    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    SUBAHL   -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    SUCAR    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    SUCAN    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    SUCAHL   -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    ANDAR    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    ANDAN    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    ANDAHL   -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    XORAR    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    XORAN    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    XORAHL   -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    ORAR     -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    ORAN     -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    ORAHL    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    CPAR     -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    CPAN     -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    CPAHL    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    INCR     -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    INCHL    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    DECR     -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    DECHL    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    DAA      -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    ADDHLRP  -> List(false.B, decode(false.B, true.B,  rp,      src_reg)),
//    INCRP    -> List(false.B, decode(false.B, true.B,  rp,      src_reg)),
//    DECRP    -> List(false.B, decode(false.B, true.B,  rp,      src_reg)),
//    ADDSPR8  -> List(false.B, decode(false.B, true.B,  SP,      src_reg)),
//    LDHLSPR8 -> List(false.B, decode(false.B, true.B,  SP,      src_reg)),
//    RLCA     -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    RLA      -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    RRCA     -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    RRA      -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    PREFIXED -> List(true.B,  decode(false.B, false.B, dst_reg, src_reg)),
//    JPNN     -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    JPHL     -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    JPCCNN   -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    JPCCE    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    JPE      -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    CALLNN   -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    CALLCCNN -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    RET      -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    RETCC    -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    RETI     -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    RSTN     -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    DI       -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    EI       -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    CCF      -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    SCF      -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    NOP      -> List(false.B, decode(false.B, false.B, dst_reg, src_reg)),
//    CPL      -> List(false.B, decode(false.B, false.B, dst_reg, src_reg))
//  )
//
//  val ctrl = ListLookup(io.inst, List(true.B, decode(false.B, false.B, 0.U, 0.U)), decode_table)
//
//  val valid = ctrl(0).asTypeOf(Bool())
//  val prefixed_valid = RegNext(valid)
//
//  val prefixed_decode_table = Array(
//    RLCR     -> List(decode(false.B, false.B, A, src_reg)),
//    RLCHL    -> List(decode(false.B, false.B, A, src_reg)),
//    RLR      -> List(decode(false.B, false.B, A, src_reg)),
//    RLHL     -> List(decode(false.B, false.B, A, src_reg)),
//    RRCR     -> List(decode(false.B, false.B, A, src_reg)),
//    RRCHL    -> List(decode(false.B, false.B, A, src_reg)),
//    RRR      -> List(decode(false.B, false.B, A, src_reg)),
//    RRHL     -> List(decode(false.B, false.B, A, src_reg)),
//    SLAR     -> List(decode(false.B, false.B, A, src_reg)),
//    SLAHL    -> List(decode(false.B, false.B, A, src_reg)),
//    SRAR     -> List(decode(false.B, false.B, A, src_reg)),
//    SRAHL    -> List(decode(false.B, false.B, A, src_reg)),
//    SWAPR    -> List(decode(false.B, false.B, A, src_reg)),
//    SWAPHL   -> List(decode(false.B, false.B, A, src_reg)),
//    SRLR     -> List(decode(false.B, false.B, A, src_reg)),
//    SRLHL    -> List(decode(false.B, false.B, A, src_reg)),
//    BITR     -> List(decode(false.B, false.B, A, src_reg)),
//    BITHL    -> List(decode(false.B, false.B, A, src_reg)),
//    RESR     -> List(decode(false.B, false.B, A, src_reg)),
//    RESHL    -> List(decode(false.B, false.B, A, src_reg)),
//    SETR     -> List(decode(false.B, false.B, A, src_reg)),
//    SETHL    -> List(decode(false.B, false.B, A, src_reg)),
//  )
//
//  val prefixed_ctrl = ListLookup(io.inst, List(decode(false.B, false.B, 0.U, 0.U)), prefixed_decode_table)
//
//  io.decoded.valid := valid || prefixed_valid
//  io.decoded.bits := Mux(prefixed_valid, prefixed_ctrl(0), ctrl(1))
}

object InstDecoder extends App {
  val name = "InstDecoder"
  val rtl = (new ChiselStage).emitVerilog(
      new InstDecoder,
      Array(
    "-td=rtl", s"-o=$name"
      ))

  println(rtl)
}
