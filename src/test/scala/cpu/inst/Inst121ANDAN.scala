import chisel3._
import org.scalatest._
import chiseltest._

object Inst121ANDAN extends FlatSpec with ChiselScalatestTester with TestUtil {
  def apply(implicit dut: CpuTestTb) {
    // test code starts from $0150.
    dut.clock.step(0x50)

    // skip register initialization.
    dut.clock.step(0x16)

    //ld  a, $01                  ; a = $01
    //and a, $01                  ; a = $01 & $01 = $01 / z = 0 / c = 0
    compareReg(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0166, false, false, false, false) // read memory
    compareReg(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0167, false, false, false, false) // and
    compareReg(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0168, false, false, true,  false) // check register value

    //;; check z flag
    //ld  a, $01                  ; a = $01
    //and a, $00                  ; a = $01 & $00 = $00 / z = 1 / c = 0
    dut.clock.step(1)
    compareReg(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x016a, false, false, true,  false) // read memory
    compareReg(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x016b, false, false, true,  false) // and
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x016c, true,  false, true,  false) // check register value
  }
}
