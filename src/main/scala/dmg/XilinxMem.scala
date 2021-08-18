import chisel3._
import chisel3.util._
import chisel3.experimental.{IntParam, StringParam}

///**
//  *
//  * @param ramType RAM type.
//  * @param numOfMemBytes number of Memory bytes.
//  * @param dataBits Data bit width.
//  * @param initHexFile File path of Hex data file for initializing memory.
//  */
//case class RAMParams
//(
//  ramType: RAMType,
//  numOfMemBytes: Int,
//  dataBits: Int,
//  initHexFile: String = ""
//) {
//  require(dataBits % 8 == 0, "dataBits must be multiply of 8")
//  val strbBits = dataBits / 8
//  val addrBits = log2Ceil(numOfMemBytes / strbBits)
//  val numOfMemRows = numOfMemBytes / strbBits // convert byte to number of row
//  val portAParams = RAMIOParams(RAMRO, addrBits, dataBits, hasRddv = true)
//  val portBParams = RAMIOParams(RAMRW, addrBits, dataBits, hasRddv = true)
//}

class xilinx_mem(val hexPath: String) extends BlackBox(
  Map(
    "p_ADDR_BITS" -> IntParam(17),
    "p_DATA_BITS" -> IntParam(8),
    "p_MEM_ROW_NUM" -> IntParam(0x20000),
    "p_INIT_HEX_FILE" -> StringParam(hexPath)
  )) with HasBlackBoxResource {
  val io = IO(new Bundle {
    // external
    val clk = Input(Clock())

   // memory
    val addr = Input(UInt(17.W))
    val q = Output(UInt(8.W))
    val ren = Input(Bool())
    val wen = Input(Bool())
    val data = Input(UInt(8.W))
  })

  addResource("/mem/xilinx_mem.sv")
}
