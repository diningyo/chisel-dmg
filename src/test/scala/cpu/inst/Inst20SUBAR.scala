import chisel3._
import org.scalatest._
import chiseltest._

object Inst20SUBAR extends FlatSpec with ChiselScalatestTester with TestUtil {
  def apply(implicit dut: CpuTestTb) {
    // ld  a, $ff                 ; a  = $ff
    //            a     b     c     d     e     h     l    sp      pc    f_z    f_n    f_h    f_c
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0100, false, false, false, false) // m1
    dut.clock.step(1)
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0101, false, false, false, false) // m2
    dut.clock.step(1)

    // ld  b, $01                 ; b  = $01
    //            a     b     c     d     e     h     l    sp      pc    f_z    f_n    f_h    f_c
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0102, false, false, false, false) // m1
    dut.clock.step(1)
    compareReg(0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0103, false, false, false, false) // m2
    dut.clock.step(1)

    // ld  c, $02                 ; c  = $02
    //            a     b     c     d     e     h     l    sp      pc    f_z    f_n    f_h    f_c
    compareReg(0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0104, false, false, false, false) // m1
    dut.clock.step(1)
    compareReg(0xff, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0105, false, false, false, false) // m2
    dut.clock.step(1)

	  // ld  d, $03                 ; d  = $03
    //            a     b     c     d     e     h     l    sp      pc    f_z    f_n    f_h    f_c
    compareReg(0xff, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0106, false, false, false, false) // m1
    dut.clock.step(1)
    compareReg(0xff, 0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0107, false, false, false, false) // m2
    dut.clock.step(1)

    // ld  e, $04                 ; e  = $04
    //            a     b     c     d     e     h     l    sp      pc    f_z    f_n    f_h    f_c
    compareReg(0xff, 0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0108, false, false, false, false) // m1
    dut.clock.step(1)
    compareReg(0xff, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00, 0x00, 0x0109, false, false, false, false) // m2
    dut.clock.step(1)

	  // ld  h, $05                 ; h  = $05
    //            a     b     c     d     e     h     l    sp      pc    f_z    f_n    f_h    f_c
    compareReg(0xff, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00, 0x00, 0x010a, false, false, false, false) // m1
    dut.clock.step(1)
    compareReg(0xff, 0x01, 0x02, 0x03, 0x04, 0x00, 0x00, 0x00, 0x010b, false, false, false, false) // m2
    dut.clock.step(1)

	  // ld  l, $06                 ; l  = $06
    //            a     b     c     d     e     h     l    sp      pc    f_z    f_n    f_h    f_c
    compareReg(0xff, 0x01, 0x02, 0x03, 0x04, 0x00, 0x00, 0x00, 0x010c, false, false, false, false) // m1
    dut.clock.step(1)
    compareReg(0xff, 0x01, 0x02, 0x03, 0x04, 0x05, 0x00, 0x00, 0x010d, false, false, false, false) // m2
    dut.clock.step(1)

    // sub a, b                   ; a  = $ff - $01 = $fe / z = 0 / c = 0
    compareReg(0xff, 0x01, 0x02, 0x03, 0x04, 0x05, 0x00, 0x00, 0x010e, false, false, false, false) // m1
    dut.clock.step(1)

    // sub a, c                   ; a  = $fe - $02 = $fc / z = 0 / c = 0
    compareReg(0xff, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x010f, false, false, false, false) // m1
    dut.clock.step(1)

    // sub a, d                   ; a  = $fc - $03 = $f9 / z = 0 / c = 0
    compareReg(0xfe, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0110, false, true,  false, false) // m1
    dut.clock.step(1)

    // sub a, e                   ; a  = $f9 - $04 = $f5 / z = 0 / c = 0
    compareReg(0xfc, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0111, false, true,  false, false) // m1
    dut.clock.step(1)

    // sub a, h                   ; a  = $f5 - $05 = $f0 / z = 0
    compareReg(0xf9, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0112, false, true,  false, false) // m1
    dut.clock.step(1)

    // sub a, l                   ; a  = $f0 - $06 = $ea / z = 0 / h = 0 -> 1
    compareReg(0xf5, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0113, false, true,  false, false) // m1
    dut.clock.step(1)

    // sub a, a                   ; a  = $ea - $ea = $00 / z = 0 ->
    compareReg(0xf0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0114, false, true,  false, false) // m1
    dut.clock.step(1)

    // ;; check z/c flag
    // ld  a, $00
    compareReg(0xea, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0115, false, true,  true,  false) // m1
    dut.clock.step(1)
    compareReg(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0116, true,  true,  false, false) // m2
    dut.clock.step(1)

    // ld  b, $00
    compareReg(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0117, true,  true,  false, false) // m1
    dut.clock.step(1)
    compareReg(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0118, true,  true,  false, false) // m2
    dut.clock.step(1)
    compareReg(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0119, true,  true,  false, false) // m1
    dut.clock.step(1)

    // sub a, b                   ; a = $00 - $00 = $00 / z = 1
    compareReg(0x00, 0x00, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x011a, true,  true,  false, false) // m1
    dut.clock.step(1)
    compareReg(0x00, 0x00, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x011b, true,  true,  false, false) // m1 - nop
    dut.clock.step(1)
    compareReg(0x00, 0x00, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x011c, true,  true,  false, false) // m1 - nop
    dut.clock.step(1)
  }
}
