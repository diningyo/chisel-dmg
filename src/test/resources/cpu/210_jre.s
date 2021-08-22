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
    ;; initialize flagregister
    ld  a, $10                  ; a = $0a
    add a, $01                  ; b = $02

    ;; initialize register
    ld  a, $00                  ; a = $00
    ld  b, $00                  ; b = $00
    ld  c, $00                  ; c = $00
	  ld  d, $00                  ; d = $00
    ld  e, $00                  ; e = $00
	  ld  h, $00                  ; h = $00
	  ld  l, $01                  ; l = $02

    jr $7f
    ld  a, l                    ; a = $01 this operation must not be executed
    nop
    ;; return from $1e4
    ld  a, $ff

  .org $1e3
    jr -$7f
    ld  a, l                    ; a = $01 this operation must not be executed
