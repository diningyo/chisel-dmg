import chisel3._
import org.scalatest._
import chiseltest._

object Inst101ADDAN extends FlatSpec with ChiselScalatestTester with TestUtil {
  def apply(implicit dut: CpuTestTb) {
    // test code starts from $0150.
    dut.clock.step(0x50)

    // skip register initialization.
    dut.clock.step(0x16)

    //ld  a, $00                  ; a = $00
    //add a, $01                  ; a = $00 + $01 = $01 / z = 0 / c = 0
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0166, false, false, false, false) // read memory
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0167, false, false, false, false) // add
    compareReg(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0168, false, false, false, false) // check register value

    //;; check z flag
    //ld  a, $00                  ; a = $00
    //add a, $00                  ; a = $00 + $00 = $00 / z = 1 / c = 0
    dut.clock.step(1)
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x016a, false, false, false, false) // read memory
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x016b, false, false, false, false) // add
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x016c, true,  false, false, false) // check register value

    //;; check h flag
    //ld  a, $10
    //ld  b, $01
    //add a, b                    ; clear flag
    //ld  a, $0f                  ; a = $00
    //add a, $01                  ; a = $0f + $01 = $10 / h = 1
    dut.clock.step(6)
    compareReg(0x0f, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0173, false, false, false, false) // read memory
    compareReg(0x0f, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0174, false, false, false, false) // add
    compareReg(0x10, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0175, false, false, true,  false) // check register value

    //;;  check c flag
    //ld  a, $10
    //ld  b, $01
    //add a, b                    ; clear flag
    //ld  a, $f0                  ; a = $f0
    //add a, $20                  ; a = $f0 + $20 = $10 / c = 1
    dut.clock.step(6)
    compareReg(0xf0, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x017c, false, false, false, false) // read memory
    compareReg(0xf0, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x017d, false, false, false, false) // add
    compareReg(0x10, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x017e, false, false, false, true) // check register value

    //;;  check z / h / c flag
    //ld  a, $10
    //ld  b, $01
    //add a, b                    ; clear flag
    //ld  a, $ff                  ; a = $ff
    //add a, $01                  ; a = $ff + $01 = $00 / z = 1 / h = 1 / c = 1
    dut.clock.step(6)
    compareReg(0xff, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0185, false, false, false, false) // read memory
    compareReg(0xff, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0186, false, false, false, false) // add
    compareReg(0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0187, true,  false, true,  true)  // check register value
  }
}
