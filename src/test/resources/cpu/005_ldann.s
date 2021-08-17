	.memoryMap
	     defaultSlot 0
	     slot 0 $0000 size $4000
	     slot 1 $C000 size $4000
	.endMe

	.romBankSize   $4000 ; generates $8000 byte ROM
	.romBanks      2

	.org $150
    ld a, ($1020)               ; a = ($1020) = $03
    ld a, ($3040)               ; a = ($3040) = $ff

  ;; exp for ld a, (hl+)
  .org $1020
  .byte $03

  .org $3040
  .byte $ff
