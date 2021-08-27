	.memoryMap
	     defaultSlot 0
	     slot 0 $0000 size $4000
	     slot 1 $C000 size $4000
	.endMe

	.romBankSize   $4000 ; generates $8000 byte ROM
	.romBanks      2

	.org $150
    ;; initialize flag register
    ld  a, $10
    ld  b, $01
    add a, b                    ; clear flag

    ;; initialize register
    ld  a, $00
    ld  b, $00
    ld  c, $00
	  ld  d, $00
    ld  e, $00
	  ld  h, $20
	  ld  l, $00

;;; Test start
    add a, (hl)                ; a = $00 + $01 = $01 / z = 0 / c = 0

    ;; check h flag
    ld  a, $01
    ld  l, $01
    add a, (hl)                ; a = $00 + $01 = $01 / z = 0 / c = 0 / h = 1

    ;; check z / c flag
    ld  l, $02
    add a, (hl)                ; a = $00 + $01 = $01 / z = 1 / c = 1 / h = 0

  .org $2000
    .byte $a0 $0f $f0
