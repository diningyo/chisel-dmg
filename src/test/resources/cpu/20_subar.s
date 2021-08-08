	.memoryMap
	     defaultSlot 0
	     slot 0 $0000 size $4000
	     slot 1 $C000 size $4000
	.endMe

	.romBankSize   $4000 ; generates $8000 byte ROM
	.romBanks      2

	.org $100
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

    ;; check z/c flag
    ld  a, $00
    ld  b, $00
    sub a, b                   ; a = $00 + $00 = $00 / z = 1 / c = 0

;    ;; clear z/c flag
;    sub a, c                   ; a = $00 + $02 = $02 / z = 0 / c = 0
;
;    ld  a, $ff
;    ld  b, $01
;    sub a, b                   ; a = $ff + $01 = $00 / z = 1 / c = 1
;
;    ;; clear z/c flag
;    sub a, c                   ; a = $00 + $02 = $02 / z = 0 / c = 0
;
;    ;;
;    ld  a, $0f
;    ld  b, $01
;    sub a, b                   ; a = $0f + $01 = $10 / h = 1
