	.memoryMap
	     defaultSlot 0
	     slot 0 $0000 size $4000
	     slot 1 $C000 size $4000
	.endMe

	.romBankSize   $4000 ; generates $8000 byte ROM
	.romBanks      2

	.org $100
    ld  a, $00                 ; a  = $00
    ld  b, $01                 ; b  = $01
    ld  c, $02                 ; c  = $02
	  ld  d, $03                 ; d  = $03
    ld  e, $04                 ; e  = $04
	  ld  h, $05                 ; h  = $05
	  ld  l, $06                 ; l  = $06
    add a, b                   ; a  = $00 + $01 = $01 / z = 0 / c = 0
    add a, c                   ; a  = $01 + $02 = $03 / z = 0 / c = 0
    add a, d                   ; a  = $03 + $03 = $06 / z = 0 / c = 0
    add a, e                   ; a  = $06 + $04 = $0a / z = 0 / c = 0
    add a, h                   ; a  = $0a + $05 = $0f / z = 0 / c = 0
    add a, l                   ; a  = $0f + $06 = $15 / z = 0 / c = 0
    add a, a                   ; a  = $15 + $15 = $2a / z = 0 / c = 0

    ;; check z/c flag
    ld  a, $00
    ld  b, $00
    add a, b                   ; a = $00 + $00 = $00 / z = 1 / c = 0

    ;; clear z/c flag
    add a, c                   ; a = $00 + $02 = $02 / z = 0 / c = 0

    ld  a, $ff
    ld  b, $01
    add a, b                   ; a = $ff + $01 = $00 / z = 1 / c = 1

    ;; clear z/c flag
    add a, c                   ; a = $00 + $02 = $02 / z = 0 / c = 0

    ;;
    ld  a, $0f
    ld  b, $01
    add a, b                   ; a = $0f + $01 = $10 / h = 1
