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
    ld  a, $ff                 ; a  = $0f
    ld  b, $01                 ; b  = $01
    ld  c, $02                 ; c  = $02
	  ld  d, $04                 ; d  = $04
    ld  e, $08                 ; e  = $08
	  ld  h, $10                 ; h  = $10
	  ld  l, $20                 ; l  = $20
    xor a, b                   ; a  = $ff - $01 = $fe
    xor a, c                   ; a  = $0e - $02 = $fc
    xor a, d                   ; a  = $0c - $04 = $f8
    xor a, e                   ; a  = $08 - $00 = $f0
    xor a, h                   ; a  = $f5 - $10 = $e0
    xor a, l                   ; a  = $f0 - $20 = $c0
    xor a, a                   ; a  = $c0 - $c0 = $c0

    ;; check z/c flag
    ld  a, $00
    ld  b, $00
    xor a, b                   ; a = $00 + $00 = $00 / z = 1
