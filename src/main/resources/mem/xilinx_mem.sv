module xilinx_mem
  #(
    parameter p_ADDR_BITS     = 16,
    parameter p_DATA_BITS     = 8,
    parameter p_MEM_ROW_NUM   = 'h1000,
    parameter p_INIT_HEX_FILE = ""
    )
  (
   // external
   input                        clk,

   // memory
   input [p_ADDR_BITS-1:0]      addr,
   output reg [p_DATA_BITS-1:0] q,
   input                        ren,
   input                        wen,
   input [p_DATA_BITS-1:0]      data
   );

  int    i;

  reg [p_DATA_BITS-1:0] mem[0:p_MEM_ROW_NUM-1];

  initial begin
    if (p_INIT_HEX_FILE != "") begin
      $display("Load: %s", p_INIT_HEX_FILE);
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
      $display("write addr %x, %x", addr, data);
      mem[addr] <= data;
    end
  end

endmodule // data_ram
