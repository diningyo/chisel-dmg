import chisel3._

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
  })

  io := DontCare

  val mem = Module(new Mem(testRom))

  val dut_cpu = Module(new Cpu())

  dut_cpu.io.mem <> mem.io
}

class CpuTest extends FlatSpec with ChiselScalatestTester with Matchers {

  val annos = Seq(VerilatorBackendAnnotation)

  behavior of "Cpu"

  import BlarggGbTests._

  get_cpu_instrs_ind_tests().foreach { test_name =>
    it should f"be passed ${test_name} tests" in {
      test(new CpuTestTb(test_name)).withAnnotations(annos) { c =>
        c.clock.setTimeout(10)
        //println(s"finish = ${c.io.finish.peek()}")
        while(c.io.finish.peek() != true.B) {
          c.clock.step(1)
        }
      }
    }
  }
}
