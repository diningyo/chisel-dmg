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

    // add a, b                   ; a  = $00 + $01 = $01 / z = 0 / c = 0
    compareReg(0xff, 0x01, 0x02, 0x03, 0x04, 0x05, 0x00, 0x00, 0x010e, false, false, false, false) // m1
    dut.clock.step(1)

    // add a, c                   ; a  = $01 + $02 = $03 / z = 0 / c = 0
    compareReg(0xff, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x010f, false, false, false, false) // m1
    dut.clock.step(1)

    // add a, d                   ; a  = $03 + $03 = $06 / z = 0 / c = 0
    compareReg(0xfe, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0110, false, false, false, false) // m1
    dut.clock.step(1)

    // add a, e                   ; a  = $06 + $04 = $0a / z = 0 / c = 0
    compareReg(0xfc, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0111, false, false, false, false) // m1
    dut.clock.step(1)

    // add a, h                   ; a  = $0a + $05 = $0f / z = 0 / c = 0
    compareReg(0xf9, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0112, false, false, false, false) // m1
    dut.clock.step(1)

    // add a, l                   ; a  = $0f + $06 = $15 / z = 0 / c = 0
    compareReg(0xf5, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0113, false, false, false, false) // m1
    dut.clock.step(1)

    // add a, a                   ; a  = $15 + $15 = $2a / z = 0 / c = 0
    compareReg(0xf0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0114, false, false, false, false) // m1
    dut.clock.step(1)

    // ;; check z/c flag
    // ld  a, $00
    compareReg(0xea, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0115, false, false, true,  false) // m1
    dut.clock.step(1)
    compareReg(0x2a, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0116, false, false, false, false) // m2
    dut.clock.step(1)

    // ld  b, $00
    compareReg(0x2a, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0117, false, false, false, false) // m1
    dut.clock.step(1)
    compareReg(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0118, false, false, false, false) // m2
    dut.clock.step(1)
    compareReg(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0119, false, false, false, false) // m1
    dut.clock.step(1)

    // add a, b                   ; a = $00 + $00 = $00 / z -> 1 / c -> 0
    compareReg(0x00, 0x00, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x011a, false, false, false, false) // m2
    dut.clock.step(1)

    // ;; clear z/c flag
    // add a, c                   ; a = $00 + $02 = $02 / z -> 0 / c -> 0
    compareReg(0x00, 0x00, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x011b, true,  false, false, false) // m2
    dut.clock.step(1)

    // ld  a, $ff
    compareReg(0x02, 0x00, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x011c, false, false, false, false) // m2
    dut.clock.step(1)
    compareReg(0x02, 0x00, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x011d, false, false, false, false) // m2
    dut.clock.step(1)

    // ld  b, $01
    compareReg(0xff, 0x00, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x011e, false, false, false, false) // m2
    dut.clock.step(1)
    compareReg(0xff, 0x00, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x011f, false, false, false, false) // m2
    dut.clock.step(1)

    // add a, b                   ; a = $ff + $01 = $00 / z -> 1 / c -> 1
    compareReg(0xff, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0120, false, false, false, false) // m2
    dut.clock.step(1)

    // ;; clear z/c flag
    // add a, c                   ; a = $00 + $02 = $02 / z -> 0 / c -> 0
    compareReg(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0121, true,  false, false,  true) // m2
    dut.clock.step(1)

    // ld  a, $0f
    compareReg(0x02, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0122, false, false, false, false) // m2
    dut.clock.step(1)
    compareReg(0x02, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0123, false, false, false, false) // m2
    dut.clock.step(1)

    // ld  b, $01
    compareReg(0x0f, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0124, false, false, false, false) // m2
    dut.clock.step(1)
    compareReg(0x0f, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0125, false, false, false, false) // m2
    dut.clock.step(1)

    // add a, b                   ; a = $0f + $01 = $10 / h = 1
    compareReg(0x0f, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0126, false, false, false, false) // m1
    dut.clock.step(1)
    compareReg(0x10, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x00, 0x0127, false, false, true,  false) // m1
    dut.clock.step(1)
  }
}
