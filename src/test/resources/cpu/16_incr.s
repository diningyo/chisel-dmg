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
    ;; initialize register
    ld  a, $00                  ; a = $00
    ld  b, $00                  ; b = $00
    ld  c, $00                  ; c = $00
	  ld  d, $00                  ; d = $00
    ld  e, $00                  ; e = $00
	  ld  h, $00                  ; h = $00
	  ld  l, $00                  ; l = $00

    inc b                       ; b = $01
    inc c                       ; c = $01
	  inc d                       ; d = $01
    inc e                       ; e = $01
	  inc h                       ; h = $01
	  inc l                       ; l = $01
    inc a                       ; a = $01

    ;; check z / h / c flag
    ld  a, $ff                  ; a = $ff
    inc a                       ; a = $00

    ;; check h flag
    ld  a, $0f                  ; a = $0f
    inc a                       ; a = $1f
