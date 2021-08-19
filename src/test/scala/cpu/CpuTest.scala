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

class CpuTest extends FlatSpec with ChiselScalatestTester with Matchers with ParallelTestExecution {

  val annos = Seq(VerilatorBackendAnnotation)

  behavior of "Cpu"

  import BlarggGbTests._

  val unitInstructionTests = Seq(
    "000_ld.s"    -> ((c: CpuTestTb) => Inst000LD(c)),
    "001_ldi.s"   -> ((c: CpuTestTb) => Inst001LDI(c)),
    "002_ldrhl.s" -> ((c: CpuTestTb) => Inst002LDRHL(c)),
    "003_ldarp.s" -> ((c: CpuTestTb) => Inst003LDARP(c)),
    "004_ldhan.s" -> ((c: CpuTestTb) => Inst004LDHAN(c)),
    "005_ldann.s" -> ((c: CpuTestTb) => Inst005LDANN(c)),
    "100_addar.s" -> ((c: CpuTestTb) => Inst100ADDAR(c)),
    "101_addan.s" -> ((c: CpuTestTb) => Inst101ADDAN(c)),
    "110_subar.s" -> ((c: CpuTestTb) => Inst110SUBAR(c)),
    "111_suban.s" -> ((c: CpuTestTb) => Inst111SUBAN(c)),
    "120_andar.s" -> ((c: CpuTestTb) => Inst120ANDAR(c)),
    "121_andan.s" -> ((c: CpuTestTb) => Inst121ANDAN(c)),
    "130_xorar.s" -> ((c: CpuTestTb) => Inst130XORAR(c)),
    "140_orar.s"  -> ((c: CpuTestTb) => Inst140ORAR(c)),
    "150_cpar.s"  -> ((c: CpuTestTb) => Inst150CPAR(c)),
    "160_incr.s"  -> ((c: CpuTestTb) => Inst160INCR(c)),
    "170_decr.s"  -> ((c: CpuTestTb) => Inst170DECR(c)),
    "180_daa.s"   -> ((c: CpuTestTb) => Inst180DAA(c)),
  )

  unitInstructionTests foreach { testInfo =>
    val (asmFile, testFunc) = testInfo
    it should f"be passed ${asmFile} test" in {
      val testHexFilePath = s"src/test/resources/cpu/${asmFile}.gb.hex"
      test(new CpuTestTb(testHexFilePath)).withAnnotations(annos) { c =>
        testFunc(c)
      }
    }
  }
}
