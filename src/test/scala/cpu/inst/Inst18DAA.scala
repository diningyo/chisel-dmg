import chisel3._
import org.scalatest._
import chiseltest._

object Inst18DAA extends FlatSpec with ChiselScalatestTester with TestUtil {
  def apply(implicit dut: CpuTestTb) {
    // test code starts from $0150.
    dut.clock.step(0x50)

    // skip register initialization.
    dut.clock.step(0x12)

    //ld  a, $0f                  ; a = $0f
    //inc a                       ; a = $10 / h = 1
    //ld  a, $00                  ; a = $03
    //daa                         ; a = $06
    dut.clock.step(6)
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0168, false, false, true,  false) // execute
    compareReg(0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0169, false, false, false, false) // check result

    //ld  a, $0f                  ; a = $0f
    //inc a                       ; a = $10 / h = 1
    //ld  a, $09                  ; a = $09
    //daa                         ; a = $0f
    dut.clock.step(4)
    compareReg(0x09, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x016e, false, false, true,  false) // execute
    compareReg(0x0f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x016f, false, false, false, false) // check result

    //;; a[3:0] = $a ~ $f / h = 0 / c = 0 => a[3:0] + $06
    //ld  a, $0a                  ; a = $0a
    //daa                         ; a = $10
    //ld  a, $0f                  ; a = $0f
    //daa                         ; a = $15
    dut.clock.step(4)
    compareReg(0x0f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0174, false, false, false, false) // execute
    compareReg(0x15, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0175, false, false, false, false) // check result

    //;; The case of adding $60
    //;; a[7:4] = 0 ~ 9 / c = 1 => a[7:4] + $60 / c = 1 / When C Flag is high, this flag keep after DAA.
    //ld  a, $20                  ; a = $ff
    //add a, $f0                  ; a = $10 / c = 1
    //ld  a, $00                  ; a = $00
    //daa                         ; a = $60
    dut.clock.step(5)
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x017b, false, false, false, true)  // execute
    compareReg(0x60, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x017c, false, false, false, false) // check result

    //ld  a, $20                  ; a = $ff
    //add a, $f0                  ; a = $01 / c = 1
    //ld  a, $90                  ; a = $30
    //daa                         ; a = $f0
    dut.clock.step(5)
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0182, false, false, false, false) // execute
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0183, false, false, false, false) // check result

    //;; a[7:4] = a ~ f / c = 0 => a[7:4] + $60 / c = 1
    //ld  a, $10                  ; a = $0a
    //add a, $01                  ; b = $02 / c = 0
    //ld  a, $a0                  ; a = $a0
    //daa                         ; a = $00 / z = 0 / c = 1
    dut.clock.step(5)
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0189, false, false, false, false) // execute
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x018a, false, false, false, false) // check result

    //ld  a, $10                  ; a = $0a
    //add a, $01                  ; b = $02 / c = 0
    //ld  a, $f0                  ; a = $f0
    //daa                         ; a = $50 / c = 1
    dut.clock.step(5)
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0190, false, false, false, false) // execute
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0191, false, false, false, false) // check result

    //;;; The case of adding $66
    //;; a[7:4] = 0x8, a[3:0] = 0xf
    //ld  a, $10                  ; a = $ff
    //add a, $e0                  ; a = $01 / c = 0
    //ld  a, $9f                  ; a = $00
    //daa                         ; a = $05 / c = 1
    dut.clock.step(5)
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0197, false, false, false, false) // execute
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0198, false, false, false, false) // check result
  }
}
