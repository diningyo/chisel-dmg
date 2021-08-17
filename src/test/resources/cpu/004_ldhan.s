	.memoryMap
	     defaultSlot 0
	     slot 0 $0000 size $fff0
	.endMe

	.romBankSize   $fff0 ; generates $8000 byte ROM
	.romBanks      2

	.org $150
    ldh a, ($10)               ; a = ($ff10) = $03
    ldh a, ($20)               ; a = ($ff20) = $a0

  ;; exp for ldh ($10)
  .org $ff10
  .byte $03

  ;; exp for ldh ($20)
  .org $ff20
  .byte $a0
