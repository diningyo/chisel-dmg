import chisel3._
import chisel3.experimental.{ChiselEnum, EnumType}
import chisel3.util._

class CpuIO extends Bundle {
  val mem = new MemIO()
}

/**
  * Base class for CPU register
  */
trait BaseCpuReg extends Bundle {
  // NOTE: these methods must be implemented in derived class.
  def write(data: UInt)
  def read(): UInt
}

/**
  * General Purpose register
  *
  * @param bits : numbers of register bit width
  */
class GP(bits: Int) extends BaseCpuReg {
  val data = UInt(bits.W)
  def write(wr_val: UInt): Unit = data := wr_val
  def read(): UInt = data

  val printWidth = bits / 4;
  val printStr = s"0x%0${printWidth}x"

  override def cloneType: this.type = new GP(bits).asInstanceOf[this.type]
  override def toString(): String = printStr.format(data.litValue())
}

class PC(bits: Int) extends GP(bits) {
  def inc: Unit = data := data + 1.U

  override def cloneType: this.type = new PC(bits).asInstanceOf[this.type]
  override def toString(): String = f"0x${data.litValue()}%04x"
}

class F extends BaseCpuReg {
  val z = Bool()
  val n = Bool()
  val h = Bool()
  val c = Bool()

  def write(data: UInt): Unit = {
    z := data(7)
    n := data(6)
    h := data(5)
    c := data(4)
  }
  def read(): UInt = Cat(z, n, h, c, 0.U(4.W))

  override def cloneType: this.type = new F().asInstanceOf[this.type]

  override def toString(): String = {
    val reg = z.litValue << 7 | n.litValue << 6 | h.litValue << 5 | c.litValue << 4
    f"0x${reg}%02x{z:${z.litValue}/n:${n.litValue}/h:${h.litValue}/c:${c.litValue}}"
  }
}

class CpuReg extends Bundle {

  import Instructions._

  val a = new GP(8)
  val f = new F
  val b = new GP(8)
  val c = new GP(8)
  val d = new GP(8)
  val e = new GP(8)
  val h = new GP(8)
  val l = new GP(8)
  val sp = new GP(16)
  val pc = new PC(16)

  // methods for 16 bit access
  def write_bc(data: UInt): Unit = {
    b.write(data(15, 8))
    c.write(data( 7, 0))
  }

  def write_de(data: UInt): Unit = {
    d.write(data(15, 8))
    e.write(data( 7, 0))
  }

  def write_hl(data: UInt): Unit = {
    h.write(data(15, 8))
    l.write(data( 7, 0))
  }

  def write_sp(data: UInt): Unit = {
    sp.write(data)
  }

  def read_bc: UInt = Cat(b.read(), c.read())
  def read_de: UInt = Cat(d.read(), e.read())
  def read_hl: UInt = Cat(h.read(), l.read())

  def read(rp: Bool, addr: UInt): UInt = {
    val ret = Wire(UInt(16.W))
    when (rp) {
      ret := MuxCase(sp.read, Seq(
        (addr === BC) -> read_bc,
        (addr === DE) -> read_de,
        (addr === HL) -> read_hl
      ))
    }.otherwise {
      ret := MuxCase(a.read, Seq(
        (addr === B) -> b.read,
        (addr === C) -> c.read,
        (addr === D) -> d.read,
        (addr === E) -> e.read,
        (addr === H) -> h.read,
        (addr === L) -> l.read
      ))
    }
    ret
  }

  def write(rp: Bool, addr: UInt, wrdata: UInt): Unit = {
    when (rp) {
      switch (addr) {
        is (BC) { write_bc(wrdata) }
        is (DE) { write_de(wrdata) }
        is (HL) { write_hl(wrdata) }
        is (SP) { sp.write(wrdata) }
      }
    }.otherwise {
      switch (addr) {
        is (A) { a.write(wrdata(7, 0)) }
        is (B) { b.write(wrdata(7, 0)) }
        is (C) { c.write(wrdata(7, 0)) }
        is (D) { d.write(wrdata(7, 0)) }
        is (E) { e.write(wrdata(7, 0)) }
        is (H) { h.write(wrdata(7, 0)) }
        is (L) { l.write(wrdata(7, 0)) }
      }
    }
  }

  val regList = Seq("a" -> a)

  override def toString(): String = {
    //regList.foldLeft("") { (s, r) =>  s + s"${r._1}:${r._2.toString}" }
    s"""|a:${a.toString}/b:${b.toString}/c:${c.toString}/d:${d.toString}/e:${e.toString}/h:${h.toString}/l:${l.toString}/sp:${sp.toString}/pc:${pc.toString}/f:${f.toString}""".stripMargin
  }
}

class Cpu extends Module {
  val io = IO(new CpuIO)

  io := DontCare

  // declare registers
  val w_reg_init_val = WireInit(0.U.asTypeOf(new CpuReg))
  w_reg_init_val.pc.data := 0x100.U
  val r_regs = RegInit(w_reg_init_val)

  // decode

  import Instructions._

  val op_code = WireInit(io.mem.rddata)

  val dst_reg = op_code(5, 3)
  val src_reg = op_code(2, 0)
  val rp      = op_code(5, 4)

  def decode(
    op: OP.Type, cycle: UInt,
    is_prefixed: Bool, is_imm: Bool, is_mem: Bool, is_dst_rp: Bool, is_src_rp: Bool,
    dst: UInt, src: UInt) = {
    val d = Wire(new DecodedInst())
    d.op := op
    d.cycle := cycle
    d.is_prefixed := is_prefixed
    d.is_imm := is_imm
    d.is_mem := is_mem
    d.is_dst_rp := is_dst_rp
    d.is_src_rp := is_src_rp
    d.dst := dst
    d.src := src

    d
  }

  val decode_table = Array(
                //             op,     cycle,     pre,     imm,     mem,  dst_rp,  src_rp,     dst,     src
    LDRN     -> List(decode(OP.LD,       2.U, false.B, true.B,  false.B, false.B, false.B, dst_reg, src_reg)),
    LDRHL    -> List(decode(OP.LD,       2.U, false.B, false.B, true.B,  false.B, false.B, dst_reg, src_reg)),
    LDRR     -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHLR    -> List(decode(OP.STORE,    1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHLN    -> List(decode(OP.STORE,    1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    LDABC    -> List(decode(OP.LD,       2.U, false.B, false.B, true.B,  false.B,  true.B, A,       BC)), // BC/DEはまとめられる？？
    LDADE    -> List(decode(OP.LD,       2.U, false.B, false.B, true.B,  false.B,  true.B, A,       DE)),
    LDAHLD   -> List(decode(OP.LD,       2.U, false.B, false.B, true.B,  false.B,  true.B, A,       HL)),
    LDAHLI   -> List(decode(OP.LD,       2.U, false.B, false.B, true.B,  false.B,  true.B, A,       HL)),
    LDBCA    -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    LDDEA    -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    LDANN    -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    LDNNA    -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHAC    -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHCA    -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHAN    -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHNA    -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHLDA   -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHLIA   -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    LDRPNN   -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, true.B,  true.B,  rp,      src_reg)),
    LDNNSP   -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    LDSPHL   -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    PUSHRP   -> List(decode(OP.PUSH,     1.U, false.B, false.B, false.B, true.B,  true.B,  dst_reg, rp)),
    POPRP    -> List(decode(OP.POP,      1.U, false.B, false.B, false.B, true.B,  true.B,  rp,      src_reg)),
    ADDAR    -> List(decode(OP.ADD,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    ADDAN    -> List(decode(OP.ADD,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    ADDAHL   -> List(decode(OP.ADD,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    ADCAR    -> List(decode(OP.ADC,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    ADCAN    -> List(decode(OP.ADC,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    ADCAHL   -> List(decode(OP.ADC,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    SUBAR    -> List(decode(OP.SUB,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    SUBAN    -> List(decode(OP.SUB,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    SUBAHL   -> List(decode(OP.SUB,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    SUCAR    -> List(decode(OP.SUC,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    SUCAN    -> List(decode(OP.SUC,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    SUCAHL   -> List(decode(OP.SUC,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    ANDAR    -> List(decode(OP.AND,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    ANDAN    -> List(decode(OP.AND,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    ANDAHL   -> List(decode(OP.AND,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    XORAR    -> List(decode(OP.XOR,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    XORAN    -> List(decode(OP.XOR,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    XORAHL   -> List(decode(OP.XOR,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    ORAR     -> List(decode(OP.OR,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    ORAN     -> List(decode(OP.OR,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    ORAHL    -> List(decode(OP.OR,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    CPAR     -> List(decode(OP.CP,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    CPAN     -> List(decode(OP.CP,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    CPAHL    -> List(decode(OP.CP,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    INCR     -> List(decode(OP.INC,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    INCHL    -> List(decode(OP.INC,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    DECR     -> List(decode(OP.DEC,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    DECHL    -> List(decode(OP.DEC,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    DAA      -> List(decode(OP.DAA,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    ADDHLRP  -> List(decode(OP.ADD,      1.U, false.B, false.B, false.B, true.B,  true.B,  rp,      src_reg)),
    INCRP    -> List(decode(OP.INC,      1.U, false.B, false.B, false.B, true.B,  true.B,  rp,      src_reg)),
    DECRP    -> List(decode(OP.DEC,      1.U, false.B, false.B, false.B, true.B,  true.B,  rp,      src_reg)),
    ADDSPR8  -> List(decode(OP.ADD,      1.U, false.B, false.B, false.B, true.B,  true.B,  SP,      src_reg)),
    LDHLSPR8 -> List(decode(OP.LD,       1.U, false.B, false.B, false.B, true.B,  true.B,  SP,      src_reg)),
    RLCA     -> List(decode(OP.RLCA,     1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    RLA      -> List(decode(OP.RLA,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    RRCA     -> List(decode(OP.RRCA,     1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    RRA      -> List(decode(OP.RRA,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    PREFIXED -> List(decode(OP.PREFIXED, 1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    JPNN     -> List(decode(OP.JP,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    JPHL     -> List(decode(OP.JP,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    JPCCNN   -> List(decode(OP.JP,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    JPCCE    -> List(decode(OP.JP,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    JPE      -> List(decode(OP.JP,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    CALLNN   -> List(decode(OP.CALL,     1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    CALLCCNN -> List(decode(OP.CALL,     1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    RET      -> List(decode(OP.RET,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    RETCC    -> List(decode(OP.RET,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    RETI     -> List(decode(OP.RET,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    RSTN     -> List(decode(OP.RST,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    DI       -> List(decode(OP.DI,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    EI       -> List(decode(OP.EI,       1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    CCF      -> List(decode(OP.CCF,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    SCF      -> List(decode(OP.SCF,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)),
    CPL      -> List(decode(OP.CPL,      1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg))
  )

  val valid = Wire(Bool())
  val ctrl = ListLookup(op_code, List(decode(OP.NOP, 1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)), decode_table)

  val mcyc_counter = RegInit(0.U(3.W))
  valid := (mcyc_counter =/= 0.U)
  val r_ctrl = Reg(new DecodedInst)

  op_code := Mux(valid, 0.U, io.mem.rddata)

  // increment PC.
  when (!ctrl(0).is_mem) {
    r_regs.pc.inc
  }

  when (!valid && ctrl(0).cycle =/= 0.U) {
    mcyc_counter := ctrl(0).cycle - 1.U
    r_ctrl := ctrl(0)
  }.elsewhen (valid) {
    mcyc_counter := mcyc_counter - 1.U
  }

  val prefixed_valid = RegNext(valid)

  val prefixed_decode_table = Array(
    RLCR     -> List(decode(OP.RLC,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    RLCHL    -> List(decode(OP.RLC,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    RLR      -> List(decode(OP.RL,   1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    RLHL     -> List(decode(OP.RL,   1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    RRCR     -> List(decode(OP.RRC,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    RRCHL    -> List(decode(OP.RRC,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    RRR      -> List(decode(OP.RR,   1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    RRHL     -> List(decode(OP.RR,   1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    SLAR     -> List(decode(OP.SLA,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    SLAHL    -> List(decode(OP.SLA,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    SRAR     -> List(decode(OP.SRA,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    SRAHL    -> List(decode(OP.SRA,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    SWAPR    -> List(decode(OP.SWAP, 1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    SWAPHL   -> List(decode(OP.SWAP, 1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    SRLR     -> List(decode(OP.SRL,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    SRLHL    -> List(decode(OP.SRL,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    BITR     -> List(decode(OP.BIT,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    BITHL    -> List(decode(OP.BIT,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    RESR     -> List(decode(OP.RES,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    RESHL    -> List(decode(OP.RES,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    SETR     -> List(decode(OP.SET,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
    SETHL    -> List(decode(OP.SET,  1.U, false.B, false.B, false.B, false.B, false.B, A, src_reg)),
  )

  val prefixed_ctrl = ListLookup(op_code, List(decode(OP.NOP, 1.U, false.B, false.B, false.B, false.B, false.B, dst_reg, src_reg)), prefixed_decode_table)

  val w_wrbk = Wire(UInt(16.W))

  w_wrbk := Mux(valid, Mux(r_ctrl.is_imm || r_ctrl.is_mem, io.mem.rddata, r_regs.read(r_ctrl.is_src_rp, r_ctrl.src)),
    r_regs.read(ctrl(0).is_src_rp, ctrl(0).src)
    )

  // reg writeback
  when ((!valid && (ctrl(0).cycle === 1.U))) {
    when (ctrl(0).op === OP.LD) {
      r_regs.write(ctrl(0).is_dst_rp, ctrl(0).dst, w_wrbk)
    }
  }.elsewhen((mcyc_counter === 1.U) && (r_ctrl.is_imm || r_ctrl.is_mem)) {
    r_regs.write(r_ctrl.is_dst_rp, r_ctrl.dst, w_wrbk)
  }

  val addr = MuxCase(0.U, Seq(
    (ctrl(0).src === BC) -> r_regs.read_bc,
    (ctrl(0).src === DE) -> r_regs.read_de,
    (ctrl(0).src === HL) -> r_regs.read_hl,
  ))

  io.mem.addr := Mux(ctrl(0).is_mem, addr, r_regs.pc.read)
  io.mem.wen := false.B
}
