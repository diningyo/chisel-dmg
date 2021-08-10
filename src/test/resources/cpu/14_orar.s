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
    ld  a, $00                 ; a  = $00
    ld  b, $01                 ; b  = $01
    ld  c, $02                 ; c  = $02
	  ld  d, $04                 ; d  = $04
    ld  e, $08                 ; e  = $08
	  ld  h, $10                 ; h  = $10
	  ld  l, $20                 ; l  = $20
    and a, b                   ; a  = $00 | $01 = $01 / n = 0 -> 1
    and a, c                   ; a  = $fe | $02 = $03
    and a, d                   ; a  = $fc | $04 = $07
    and a, e                   ; a  = $f9 | $08 = $0f
    and a, h                   ; a  = $f5 | $10 = $1f
    and a, l                   ; a  = $f0 | $20 = $3f / h = 0 -> 1
    and a, a                   ; a  = $3f | $3f = $3f

    ;; check z/c flag
    ld  a, $00
    ld  b, $00
    and a, b                   ; a = $00 + $00 = $00 / z = 1 / c = 0
