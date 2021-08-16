import chisel3._
import org.scalatest._
import chiseltest._

object Inst111SUBAN extends FlatSpec with ChiselScalatestTester with TestUtil {
  def apply(implicit dut: CpuTestTb) {
    // test code starts from $0150.
    dut.clock.step(0x50)

    // skip register initialization.
    dut.clock.step(0x16)

    //ld  a, $02                  ; a = $02
    //sub a, $01                  ; a = $02 - $01 = $01 / z = 0 / c = 0
    compareReg(0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0166, false, false, false, false) // read memory
    compareReg(0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0167, false, false, false, false) // sub
    compareReg(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0168, false, true,  false, false) // check register value

    //;; check z flag
    //ld  a, $01                  ; a = $00
    //sub a, $01                  ; a = $01 - $01 = $00 / z = 1 / c = 0
    dut.clock.step(1)
    compareReg(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x016a, false, true,  false, false) // read memory
    compareReg(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x016b, false, true,  false, false) // sub
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x016c, true,  true,  false, false) // check register value

    //;; check h flag
    //ld  a, $10
    //ld  b, $01
    //sub a, b                    ; clear flag
    //ld  a, $10                  ; a = $10
    //sub a, $01                  ; a = $10 - $01 = $0f / h = 1
    dut.clock.step(6)
    compareReg(0x10, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0173, false, false, false, false) // read memory
    compareReg(0x10, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0174, false, false, false, false) // sub
    compareReg(0x0f, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0175, false, true,  true,  false) // check register value

    //;;  check c flag
    //ld  a, $10
    //ld  b, $01
    //sub a, b                    ; clear flag
    //ld  a, $10                  ; a = $10
    //sub a, $20                  ; a = $10 - $20 = $f0 / c = 1
    dut.clock.step(6)
    compareReg(0x10, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x017c, false, false, false, false) // read memory
    compareReg(0x10, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x017d, false, false, false, false) // sub
    compareReg(0xf0, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x017e, false, true,  false, true) // check register value

    //;;  check h / c flag
    //ld  a, $10
    //ld  b, $01
    //sub a, b                    ; clear flag
    //ld  a, $00                  ; a = $00
    //sub a, $01                  ; a = $00 - $01 = $ff / z = 0 / h = 1 / c = 1
    dut.clock.step(6)
    compareReg(0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0185, false, false, false, false) // read memory
    compareReg(0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0186, false, false, false, false) // sub
    compareReg(0xff, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0187, false, true,  true,  true)  // check register value
  }
}
