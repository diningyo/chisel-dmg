import chisel3._
import org.scalatest._
import chiseltest._

object Inst005LDANN extends FlatSpec with ChiselScalatestTester with TestUtil {
  def apply(implicit dut: CpuTestTb) {
    dut.clock.step(0x51)

    // ld a, ($1020)             ; a = ($1020) = $03
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0151, false, false, false, false) // m2
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0152, false, false, false, false) // m3
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0153, false, false, false, false) // m4
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0153, false, false, false, false) // m1

    // ld a, ($3040)             ; a = ($3040) = $ff
    compareReg(0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0154, false, false, false, false) // m2
    compareReg(0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0155, false, false, false, false) // m3
    compareReg(0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0156, false, false, false, false) // m4
    compareReg(0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0156, false, false, false, false) // m1
    compareReg(0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0157, false, false, false, false) // m1
  }
}
