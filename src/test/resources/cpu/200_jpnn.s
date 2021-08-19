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
    ld  a, $00                  ; a = $0a
    ld  b, $00                  ; b = $02
    ld  c, $00                  ; c = $02
	  ld  d, $00                  ; d = $02
    ld  e, $00                  ; e = $02
	  ld  h, $00                  ; h = $02
	  ld  l, $00                  ; l = $02

    jp $1020

  .org $1020
    jp $2010

  .org $2010
    ld a, $ff
