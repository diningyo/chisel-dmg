import org.scalatest._
import chiseltest._
import chisel3._

object BlarggGbTests {
  def get_cpu_instrs_ind_tests(): Seq[String] = {
    val cpu_instrs_ind_path = "cpu_instrs/individual"
    val cpu_instrs_ind = Seq(
      "01-special.gb",
      "02-interrupts.gb",
      "03-op sp,hl.gb",
      "04-op r,imm.gb",
      "05-op rp.gb",
      "06-ld r,r.gb",
      "07-jr,jp,call,ret,rst.gb",
      "08-misc instrs.gb",
      "09-op r,r.gb",
      "10-bit ops.gb",
      "11-op a,(hl).gb",
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

class CpuTestTb(val test_rom: String) extends Module {

  val io = IO(new Bundle {})

  io := DontCare

}

class CpuTest extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "Cpu"

  import BlarggGbTests._

  get_cpu_instrs_ind_tests().foreach { test_name =>
    it should f"be passed ${test_name} tests" in {
      test(new CpuTestTb(test_name)) { c =>
        fail()
      }
    }
  }

}
