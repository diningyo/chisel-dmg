import chisel3._
import chisel3.util._
import chisel3.internal.firrtl.Arg

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

  override def cloneType: this.type = new GP(bits).asInstanceOf[this.type]
}

class PC(bits: Int) extends GP(bits) {
  def inc: Unit = data := data + 1.U

  override def cloneType: this.type = new PC(bits).asInstanceOf[this.type]
}

class F extends Bundle with BaseCpuReg {
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
}

class Cpu extends Module {
  val io = IO(new CpuIO)

  io := DontCare

  // declare registers
  val w_reg_init_val = WireInit(0.U.asTypeOf(new CpuReg))
  w_reg_init_val.pc.data := 0x100.U
  val r_regs = RegInit(w_reg_init_val)

  // increment PC.
  r_regs.pc.inc

  // decode

  import Instructions._

  val op_code = WireInit(io.mem.rddata)

  val dst_reg = op_code(5, 3)
  val src_reg = op_code(2, 0)
  val rp      = op_code(5, 4)

  def decode(cycle: UInt, is_prefixed: Bool, is_imm: Bool, is_rp: Bool, dst: UInt, src: UInt) = {
    val d = Wire(new DecodedInst())
    d.cycle := cycle
    d.is_prefixed := is_prefixed
    d.is_imm := is_imm
    d.is_rp := is_rp
    d.dst := dst
    d.src := src

    d
  }

  val decode_table = Array(
    LDRN     -> List(decode(2.U, false.B, true.B,  false.B, dst_reg, src_reg)),
    LDRR     -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDRHL    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHLR    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHLN    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDABC    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDADE    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDBCA    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDDEA    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDANN    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDNNA    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHAC    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHCA    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHAN    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHNA    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDAHLD   -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHLDA   -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDAHLI   -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDHLIA   -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDRPNN   -> List(decode(1.U, false.B, false.B, true.B,  rp,      src_reg)),
    LDNNSP   -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    LDSPHL   -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    PUSHRP   -> List(decode(1.U, false.B, false.B, true.B,  dst_reg, rp)),
    POPRP    -> List(decode(1.U, false.B, false.B, true.B,  rp,      src_reg)),
    ADDAR    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    ADDAN    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    ADDAHL   -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    ADCAR    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    ADCAN    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    ADCAHL   -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    SUBAR    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    SUBAN    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    SUBAHL   -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    SUCAR    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    SUCAN    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    SUCAHL   -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    ANDAR    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    ANDAN    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    ANDAHL   -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    XORAR    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    XORAN    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    XORAHL   -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    ORAR     -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    ORAN     -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    ORAHL    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    CPAR     -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    CPAN     -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    CPAHL    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    INCR     -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    INCHL    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    DECR     -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    DECHL    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    DAA      -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    ADDHLRP  -> List(decode(1.U, false.B, false.B, true.B,  rp,      src_reg)),
    INCRP    -> List(decode(1.U, false.B, false.B, true.B,  rp,      src_reg)),
    DECRP    -> List(decode(1.U, false.B, false.B, true.B,  rp,      src_reg)),
    ADDSPR8  -> List(decode(1.U, false.B, false.B, true.B,  SP,      src_reg)),
    LDHLSPR8 -> List(decode(1.U, false.B, false.B, true.B,  SP,      src_reg)),
    RLCA     -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    RLA      -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    RRCA     -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    RRA      -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    PREFIXED -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    JPNN     -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    JPHL     -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    JPCCNN   -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    JPCCE    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    JPE      -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    CALLNN   -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    CALLCCNN -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    RET      -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    RETCC    -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    RETI     -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    RSTN     -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    DI       -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    EI       -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    CCF      -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    SCF      -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    //NOP      -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg)),
    CPL      -> List(decode(1.U, false.B, false.B, false.B, dst_reg, src_reg))
  )

  val valid = Wire(Bool())
  val ctrl = ListLookup(op_code, List(decode(1.U, false.B, false.B, false.B, 0.U, 0.U)), decode_table)

  val mcyc_counter = RegInit(0.U(3.W))
  valid := (mcyc_counter =/= 1.U)
  val r_ctrl = Reg(new DecodedInst)

  when (ctrl(0).cycle =/= 0.U) {
    mcyc_counter := ctrl(0).cycle - 1.U
    r_ctrl := ctrl(0)
  }

  val prefixed_valid = RegNext(valid)

  val prefixed_decode_table = Array(
    RLCR     -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    RLCHL    -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    RLR      -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    RLHL     -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    RRCR     -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    RRCHL    -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    RRR      -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    RRHL     -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    SLAR     -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    SLAHL    -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    SRAR     -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    SRAHL    -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    SWAPR    -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    SWAPHL   -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    SRLR     -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    SRLHL    -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    BITR     -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    BITHL    -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    RESR     -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    RESHL    -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    SETR     -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
    SETHL    -> List(decode(1.U, false.B, false.B, false.B, A, src_reg)),
  )

  val prefixed_ctrl = ListLookup(op_code, List(decode(1.U, false.B, false.B, false.B, 0.U, 0.U)), prefixed_decode_table)

  val w_wrbk = Wire(UInt(16.W))

  w_wrbk := Mux(ctrl(0).cycle === 1.U,
    Mux(r_ctrl.is_imm, io.mem.rddata, r_regs.read(r_ctrl.is_rp, r_ctrl.src)),
    r_regs.read(ctrl(0).is_rp, ctrl(0).src)
    )

  // reg writeback
  when ((ctrl(0).cycle === 1.U) || (mcyc_counter === 1.U) && r_ctrl.is_imm) {
    r_regs.write(r_ctrl.is_rp, r_ctrl.dst, w_wrbk)
  }

  io.mem.addr := r_regs.pc.data
  io.mem.wen := false.B
}
