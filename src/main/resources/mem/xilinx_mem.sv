module xilinx_mem
  #(
     parameter p_ADDR_BITS  = 16
    ,parameter p_DATA_BITS  = 8
    ,parameter p_MEM_ROW_NUM = 0
    ,parameter p_INIT_HEX_FILE = "test.hex"
    )
  (
   // external
   input                        clk,

   // memory
   input [p_ADDR_BITS-1:0]      addr
   output reg [p_DATA_BITS-1:0] q
   input                        ren
   input                        wen
   input [p_DATA_BITS-1:0]      data
   );

  int    i;

  reg [p_DATA_BITS-1:0] mem[0:p_MEM_ROW_NUM-1];

  initial begin
    if (p_INIT_HEX_FILE != "") begin
      $readmemh(p_INIT_HEX_FILE, mem);
    end
  end

  // dmem side
  always @(posedge clk) begin
    // read
    if (ren) begin
      q <= mem[addr];
    end

    // write
    if (wen) begin
      mem[addr] <= data;
    end
  end

endmodule // data_ram
