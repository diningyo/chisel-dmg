	.memoryMap
	     defaultSlot 0
	     slot 0 $0000 size $4000
	     slot 1 $C000 size $4000
	.endMe

	.romBankSize   $4000 ; generates $8000 byte ROM
	.romBanks      2

	.org $100
	  ld a, $01                  ; a = $01
	  ld b, $02                  ; b = $02
	  ld c, $03                  ; c = $03
	  ld d, $04                  ; d = $04
	  ld e, $05                  ; e = $05
	  ld h, $06                  ; h = $06
	  ld l, $07                  ; l = $07
