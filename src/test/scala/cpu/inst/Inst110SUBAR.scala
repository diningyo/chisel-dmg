import chisel3._
import org.scalatest._
import chiseltest._

object Inst110SUBAR extends FlatSpec with ChiselScalatestTester with TestUtil {
  def apply(implicit dut: CpuTestTb) {
    // test code starts from $0150.
    dut.clock.step(0x50)

    // skip register initialization.
    dut.clock.step(0x21)

    compareReg(0xff, 0x01, 0x02, 0x03, 0x04, 0x05, 0x00, 0x00, 0x0171, false, false, false, false) // fetch
    compareReg(0xff, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0172, false, false, false, false) // execute

    // sub a, b                   ; a  = $ff - $01 = $fe / z = 0 / c = 0
    compareReg(0xfe, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0173, false, true,  false, false) // check

    // sub a, c                   ; a  = $fe - $02 = $fc / z = 0 / c = 0
    compareReg(0xfc, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0174, false, true,  false, false) // check

    // sub a, d                   ; a  = $fc - $03 = $f9 / z = 0 / c = 0
    compareReg(0xf9, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0175, false, true,  false, false) // check

    // sub a, e                   ; a  = $f9 - $04 = $f5 / z = 0 / c = 0
    compareReg(0xf5, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0176, false, true,  false, false) // check

    // sub a, h                   ; a  = $f5 - $05 = $f0 / z = 0
    compareReg(0xf0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0177, false, true,  false, false) // check

    // sub a, l                   ; a  = $f0 - $06 = $ea / z = 0 / h = 0 -> 1
    compareReg(0xea, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0178, false, true,  true,  false) // check

    // sub a, a                   ; a  = $ea - $ea = $00 / z = 0 ->
    compareReg(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0179, true,  true,  false, false) // check

    // ;; check z/c flag
    // ld  a, $01
    // ld  b, $01
    dut.clock.step(0x2)

    // sub a, b                   ; a = $00 - $00 = $00 / z = 1
    compareReg(0x01, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x017c, true,  true,  false, false) // fetch
    compareReg(0x01, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x017d, true,  true,  false, false) // execute
    compareReg(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x017e, true,  true,  false, false) // check

    // ;; check h flag
    // ld  a, $10
    // ld  b, $01
    // add a, b                   ; clear flag
    // ld  a, $10
    // ld  b, $01
    dut.clock.step(0x7)

    // sub a, b                   ; a - b = $10 - $01 = $0f / h = 1
    compareReg(0x10, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0186, false, false, false, false) // fetch
    compareReg(0x10, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0187, false, false, false, false) // execute
    compareReg(0x0f, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0188, false, true,  true,  false) // check

    // ;;  check c flag
    // ld  a, $10
    // ld  b, $01
    // add a, b                    ; clear flag
    // ld  a, $10
    // ld  b, $20
    dut.clock.step(0x7)

    // sub a, $20                  ; a - b = $10 - $20 = $f0 / c = 1
    compareReg(0x10, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0190, false, false, false, false) // fetch
    compareReg(0x10, 0x20, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0191, false, false, false, false) // execute
    compareReg(0xf0, 0x20, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0192, false, true,  false, true)  // check

    // ;;  check z / h / c flag
    // ld  a, $10
    // ld  b, $01
    // add a, b                    ; clear flag
    // ld  a, $00
    // ld  b, $01
    dut.clock.step(0x7)

    // sub a, b                    ; a - b = $00 - $01 = $ff / z = 0 / n = 1 / h = 1 / c = 1
    compareReg(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x019a, false, false, false, false) // fetch
    compareReg(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x019b, false, false, false, false) // execute
    compareReg(0xff, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x019c, false, true,  true,  true)  // check
  }
}
