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
	  ld  h, $00
	  ld  l, $00

;;; test start
    ld  a, $ff                 ; a  = $ff
    ld  b, $01                 ; b  = $01
    ld  c, $02                 ; c  = $02
	  ld  d, $03                 ; d  = $03
    ld  e, $04                 ; e  = $04
	  ld  h, $05                 ; h  = $05
	  ld  l, $06                 ; l  = $06
    sub a, b                   ; a  = $ff - $01 = $fe / n = 0 -> 1
    sub a, c                   ; a  = $fe - $02 = $fc
    sub a, d                   ; a  = $fc - $03 = $f9
    sub a, e                   ; a  = $f9 - $04 = $f5
    sub a, h                   ; a  = $f5 - $05 = $f0
    sub a, l                   ; a  = $f0 - $06 = $ea / h = 0 -> 1
    sub a, a                   ; a  = $ea - $ea = $00

    ;; check z flag
    ld  a, $01
    ld  b, $01
    sub a, b                   ; a - b = $01 - $01 = $00 / z = 1 / c = 0

    ;; check h flag
    ld  a, $10
    ld  b, $01
    add a, b                   ; clear flag
    ld  a, $10
    ld  b, $01
    sub a, b                   ; a - b = $10 - $01 = $0f / h = 1

    ;;  check c flag
    ld  a, $10
    ld  b, $01
    add a, b                    ; clear flag
    ld  a, $10
    ld  b, $20
    sub a, $20                  ; a - b = $10 - $20 = $f0 / c = 1

    ;;  check z / h / c flag
    ld  a, $10
    ld  b, $01
    add a, b                    ; clear flag
    ld  a, $00
    ld  b, $01
    sub a, $01                  ; a - b = $00 - $01 = $ff / z = 1 / h = 1 / c = 1
