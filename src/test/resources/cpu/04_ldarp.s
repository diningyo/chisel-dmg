	.memoryMap
	     defaultSlot 0
	     slot 0 $0000 size $4000
	     slot 1 $C000 size $4000
	.endMe

	.romBankSize   $4000 ; generates $8000 byte ROM
	.romBanks      2

	.org $100
    ld b, $10                   ; h  = $10
    ld c, $20                   ; l  = $20
	  ld a, (bc)                  ; bc = $1020 / a = $01
    ld d, $20                   ; d  = $20
	  ld e, $10                   ; e  = $10
	  ld a, (de)                  ; hl = $2010 / a = $02
    ld h, $01                   ; h  = $01
	  ld l, $ff                   ; l  = $ff
	  ld a, (hl+)                 ; a  = (hl = $01ff) = $03 -> hl = $0200
    ld h, $03                   ; d  = $02
	  ld l, $00                   ; e  = $00
	  ld a, (hl-)                 ; a  = (hl = $0200) = $03 -> hl = $01ff

  ;; exp for ld a, (hl+)
  .org $1ff
  .byte $03

  ;; exp for ld a, (hl-)
  .org $300
  .byte $04

  ;; exp for ld a, (bc)
  .org $1020
  .byte $01

  ;; exp for ld a, (de)
  .org $2010
  .byte $02
