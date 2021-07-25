	.memoryMap
	     defaultSlot 0
	     slot 0 $0000 size $4000
	     slot 1 $C000 size $4000
	.endMe

	.romBankSize   $4000 ; generates $8000 byte ROM
	.romBanks      2

	.org $100
	  ld a, $a5                  ; a = $a5
	  ld b, a
	  ld c, b
	  ld d, c
	  ld e, d
	  ld h, e
	  ld l, h
