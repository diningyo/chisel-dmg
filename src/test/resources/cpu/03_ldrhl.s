	.memoryMap
	     defaultSlot 0
	     slot 0 $0000 size $4000
	     slot 1 $C000 size $4000
	.endMe

	.romBankSize   $4000 ; generates $8000 byte ROM
	.romBanks      2

	.org $100
    ld h, $10                   ; h  = $10
    ld l, $20                   ; l  = $20
	  ld a, (hl)                  ; hl = $1020 / a = $01
    ld l, $21
	  ld b, (hl)                  ; hl = $1021 / b = $02
    ld l, $22
	  ld c, (hl)                  ; hl = $1022 / c = $03
    ld l, $23
	  ld d, (hl)                  ; hl = $1023 / d = $04
    ld l, $24
	  ld e, (hl)                  ; hl = $1024 / e = $05
    ld l, $25
	  ld h, (hl)                  ; hl = $1025 / h = $20
    ld l, $20                   ; l  = $26
	  ld l, (hl)                  ; hl = $2020 / l = $ff

  .org $1020
  .byte $01, $02, $03, $04, $05, $20

  .org $2020
  .byte $ff
