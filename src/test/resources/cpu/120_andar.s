	.memoryMap
	     defaultSlot 0
	     slot 0 $0000 size $4000
	     slot 1 $C000 size $4000
	.endMe

	.romBankSize   $4000 ; generates $8000 byte ROM
	.romBanks      2

  ;; 0x100 - 0x14f is enrtry point and cartridge haeder.
  ;; So skip this region for cpu instruction test.
	.org $150
    ld  a, $ff                 ; a  = $ff
    ld  b, $fe                 ; b  = $fe
    ld  c, $fc                 ; c  = $fc
	  ld  d, $f8                 ; d  = $f8
    ld  e, $f0                 ; e  = $f0
	  ld  h, $e0                 ; h  = $e0
	  ld  l, $c0                 ; l  = $c0
    and a, b                   ; a  = $ff - $fe = $fe / n = 0 -> 1
    and a, c                   ; a  = $fe - $fc = $fc
    and a, d                   ; a  = $fc - $f8 = $f8
    and a, e                   ; a  = $f9 - $f0 = $f0
    and a, h                   ; a  = $f5 - $e0 = $e0
    and a, l                   ; a  = $f0 - $c0 = $c0 / h = 0 -> 1
    and a, a                   ; a  = $c0 - $c0 = $c0

    ;; check z/c flag
    ld  a, $00
    ld  b, $00
    and a, b                   ; a = $00 + $00 = $00 / z = 1 / c = 0

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
