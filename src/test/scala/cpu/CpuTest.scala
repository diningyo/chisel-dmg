import chisel3._
import chisel3.util.experimental.BoringUtils

import org.scalatest._
import chiseltest._
import chiseltest.internal.VerilatorBackendAnnotation
import chiseltest.experimental.TestOptionBuilder._



object BlarggGbTests {

  val testSourceDir = "src/test/resources/blargg-gb-tests"

  def get_cpu_instrs_ind_tests(): Seq[String] = {
    val cpu_instrs_ind_path = s"${testSourceDir}/cpu_instrs/individual"
    val cpu_instrs_ind = Seq(
      "01-special.hex",
      //"02-interrupts.gb",
      //"03-op sp,hl.gb",
      //"04-op r,imm.gb",
      //"05-op rp.gb",
      //"06-ld r,r.gb",
      //"07-jr,jp,call,ret,rst.gb",
      //"08-misc instrs.gb",
      //"09-op r,r.gb",
      //"10-bit ops.gb",
      //"11-op a,(hl).gb",
    )

    cpu_instrs_ind.map(cpu_instrs_ind_path + "/" + _)
  }

  val dmg_sound_rom_path = "dmg_sound/rom_singles"
  val dmg_sound_rom_singles = Seq(
    "01-registers.gb",
    "02-len ctr.gb",
    "03-trigger.gb",
    "04-sweep.gb",
    "05-sweep details.gb",
    "06-overflow on trigger.gb",
    "07-len sweep period sync.gb",
    "08-len ctr during power.gb",
    "09-wave read while on.gb",
    "10-wave trigger while on.gb",
    "11-regs after power.gb",
    "12-wave write while on.gb",
  )

  val mem_timing_path = "mem_timing/indivisual"
  val mem_timing_ind = Seq(
    "01-read_timing.gb",
    "02-write_timing.gb",
    "03-modify_timing.gb"
  )

  val mem_timing_2_path = "mem_timing-2/rom_singles"
  val mem_timing_2_ind = Seq(
    "01-read_timing.gb",
    "02-write_timing.gb",
    "03-modify_timing.gb"
  )

  val regression = Seq(
  "cpu_instrs/cpu_instrs.gb",
  "dmg_sound/dmg_sound.gb",
  "halt_bug.gb",
  "instr_timing/instr_timing.gb",
  "interrupt_time/interrupt_time.gb",
  "mem_timing/mem_timing.gb",
  "mem_timing-2/mem_timing-2.gb",
  "oam_bug/oam_bug.gb"
  )
}

class CpuTestTb(val testRom: String) extends Module {

  val io = IO(new Bundle {
    val finish = Output(Bool())
    val is_success = Output(Bool())
    val timeout = Output(Bool())
    val regs = Output(new CpuReg)
  })

  io := DontCare

  val mem = Module(new Mem(testRom))

  val dut_cpu = Module(new Cpu)

  dut_cpu.io.mem <> mem.io


  val w_regs = WireDefault(0.U.asTypeOf(new CpuReg))
  BoringUtils.bore(dut_cpu.r_regs, Seq(w_regs))

  io.regs := w_regs
}

class CpuTest extends FlatSpec with ChiselScalatestTester with Matchers {

  val annos = Seq(VerilatorBackendAnnotation)

  behavior of "Cpu"

  import BlarggGbTests._

  def compareReg(
    a: Int, b: Int, c: Int, d: Int, e: Int,
    h: Int, l: Int, sp: Int, pc: Int,
    f_z: Boolean, f_n: Boolean, f_h: Boolean, f_c: Boolean
  )(implicit dut: CpuTestTb): Unit = {
    println(s"reg = ${dut.io.regs.peek}")

    dut.io.regs.a.read.expect(a.U)
    dut.io.regs.b.read.expect(b.U)
    dut.io.regs.c.read.expect(c.U)
    dut.io.regs.d.read.expect(d.U)
    dut.io.regs.e.read.expect(e.U)
    dut.io.regs.h.read.expect(h.U)
    dut.io.regs.l.read.expect(l.U)
    dut.io.regs.sp.read.expect(sp.U)
    dut.io.regs.pc.read.expect(pc.U)

    // flagはとりあえず各ビット単位で比較
    dut.io.regs.f.z.expect(f_z.B)
    dut.io.regs.f.n.expect(f_n.B)
    dut.io.regs.f.h.expect(f_h.B)
    dut.io.regs.f.c.expect(f_c.B)
  }

  it should f"be passed 01_ld.s test" in {
    val testHexFilePath = s"src/test/resources/cpu/01_ld.s.gb.hex"
    test(new CpuTestTb(testHexFilePath)).withAnnotations(annos) { c =>

      implicit val dut = c
      c.clock.setTimeout(100)

      compareReg(0, 0, 0, 0, 0, 0, 0, 0, 0x100, false, false, false, false)
      c.clock.step(1)

      // 1cycleごとに期待値を比較していく
      // NOTE: 初期値どうしよう。。bgbの値に合わせても良いのかも。
      // ld a, $a5                  ; a = $a5
      // ld a, imm -> need 2 cycles
      //            a     b     c     d     e     h     l    sp     pc    f_z    f_n    f_h    f_c
      compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x101, false, false, false, false)
      c.clock.step(1)
      compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x102, false, false, false, false)
      c.clock.step(1)
      compareReg(0xa5, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x103, false, false, false, false)
      c.clock.step(1)

	    // ld b, a
      compareReg(0xa5, 0xa5, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x102, false, false, false, false)
      c.clock.step(1)

	    // ld c, b
      compareReg(0xa5, 0xa5, 0xa5, 0x00, 0x00, 0x00, 0x00, 0x00, 0x100, false, false, false, false)
      c.clock.step(1)

	    // ld d, c
      compareReg(0xa5, 0xa5, 0xa5, 0xa5, 0x00, 0x00, 0x00, 0x00, 0x100, false, false, false, false)
      c.clock.step(1)

	    // ld e, d
      compareReg(0xa5, 0xa5, 0xa5, 0xa5, 0xa5, 0x00, 0x00, 0x00, 0x100, false, false, false, false)
      c.clock.step(1)

	    // ld l, e
      compareReg(0xa5, 0xa5, 0xa5, 0xa5, 0xa5, 0x00, 0x00, 0x00, 0x100, false, false, false, false)
      c.clock.step(1)

	    // ld e, l
      compareReg(0xa5, 0xa5, 0xa5, 0xa5, 0xa5, 0x00, 0x00, 0x00, 0x100, false, false, false, false)
    }
  }

  //get_cpu_instrs_ind_tests().foreach { test_name =>
  //  it should f"be passed ${test_name} tests" in {
  //    test(new CpuTestTb(test_name)).withAnnotations(annos) { c =>
  //      c.clock.setTimeout(10)
  //
  //      // 1cycleごとに期待値を比較していく
  //
  //
  //    }
  //  }
  //}

}
