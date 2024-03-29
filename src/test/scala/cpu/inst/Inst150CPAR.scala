import chisel3._
import org.scalatest._
import chiseltest._

object Inst150CPAR extends FlatSpec with ChiselScalatestTester with TestUtil {
  def apply(implicit dut: CpuTestTb) {
    // test code starts from $0150.
    dut.clock.step(0x50)

    // ld  a, $00                 ; a  = $00
    //            a     b     c     d     e     h     l    sp      pc    f_z    f_n    f_h    f_c
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0150, false, false, false, false) // m1
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0151, false, false, false, false) // m2

    // ld  b, $01                 ; b  = $01
    //            a     b     c     d     e     h     l    sp      pc    f_z    f_n    f_h    f_c
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0152, false, false, false, false) // m1
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0153, false, false, false, false) // m2

    // ld  c, $02                 ; c  = $02
    //            a     b     c     d     e     h     l    sp      pc    f_z    f_n    f_h    f_c
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0154, false, false, false, false) // m1
    compareReg(0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0155, false, false, false, false) // m2

	  // ld  d, $04                 ; d  = $04
    //            a     b     c     d     e     h     l    sp      pc    f_z    f_n    f_h    f_c
    compareReg(0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0156, false, false, false, false) // m1
    compareReg(0x00, 0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0157, false, false, false, false) // m2

    // ld  e, $08                 ; e  = $08
    //            a     b     c     d     e     h     l    sp      pc    f_z    f_n    f_h    f_c
    compareReg(0x00, 0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0158, false, false, false, false) // m1
    compareReg(0x00, 0x01, 0x02, 0x04, 0x00, 0x00, 0x00, 0x00, 0x0159, false, false, false, false) // m2

	  // ld  h, $10                 ; h  = $10
    //            a     b     c     d     e     h     l    sp      pc    f_z    f_n    f_h    f_c
    compareReg(0x00, 0x01, 0x02, 0x04, 0x00, 0x00, 0x00, 0x00, 0x015a, false, false, false, false) // m1
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x00, 0x00, 0x00, 0x015b, false, false, false, false) // m2

	  // ld  l, $20                 ; l  = $20
    //            a     b     c     d     e     h     l    sp      pc    f_z    f_n    f_h    f_c
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x00, 0x00, 0x00, 0x015c, false, false, false, false) // m1
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x00, 0x00, 0x015d, false, false, false, false) // m2

    // cp a, b                    ; cp : $00 <-> $01 = $01 / z = 0 / n = 0 / h = 0 / c = 0
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x00, 0x00, 0x015e, false, false, false, false) // m1

    // cp a, c                    ; cp : $00 <-> $02 = $03
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x00, 0x015f, false, false, false, false) // m1

    // cp a, d                    ; cp : $00 <-> $04 = $07
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x00, 0x0160, false, true,  true,  true) // m1

    // cp a, e                    ; cp : $00 <-> $08 = $0f
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x00, 0x0161, false, true,  true,  true) // m1

    // cp a, h                    ; cp : $00 <-> $10 = $1f
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x00, 0x0162, false, true,  true,  true) // m1

    // cp a, l                    ; cp : $00 <-> $20 = $3f
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x00, 0x0163, false, true,  true,  true) // m1

    // cp a, a                    ; cp : $00 <-> $3f = $3f
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x00, 0x0164, false, true,  false, true) // m1

    // ;; check z flag
    // ld  a, $00
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x00, 0x0165, false, true,  false, true) // m1
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x00, 0x0166, true,  true,  false, false) // m2

    // ld  b, $00
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x00, 0x0167, true,  true,  false, false) // m1
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x00, 0x0168, true,  true,  false, false) // m2
    compareReg(0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x00, 0x0169, true,  true,  false, false) // m1

    // cp a, b                   ; cp : $00 <-> $00 = $00 / z = 1
    compareReg(0x00, 0x00, 0x02, 0x04, 0x08, 0x10, 0x20, 0x00, 0x016a, true,  true,  false, false) // m1
    compareReg(0x00, 0x00, 0x02, 0x04, 0x08, 0x10, 0x20, 0x00, 0x016b, true,  true,  false, false) // m1 - nop
    compareReg(0x00, 0x00, 0x02, 0x04, 0x08, 0x10, 0x20, 0x00, 0x016c, true,  true,  false, false) // m1 - nop
  }
}
