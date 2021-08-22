	.memoryMap
	     defaultSlot 0
	     slot 0 $0000 size $4000
	     slot 1 $C000 size $4000
	.endMe

	.romBankSize   $4000 ; generates $8000 byte ROM
	.romBanks      2

	.org $150
	  ld b, $10
	  ld c, $20
	  ld d, $30
	  ld e, $40

    ;; test 0 - bc
	  ld a, $01                   ; set write value
    ld (bc), a
    ld a, $00                   ; clear
    ld a, (bc)

    ;; test 1 - de
    ld a, $02                   ; set write value
    ld (de), a
    ld a, $00                   ; clear
    ld a, (de)
