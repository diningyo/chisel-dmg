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
    ld  a, $02                  ; a = $02
    ld  b, $02                  ; b = $02
    ld  c, $02                  ; c = $02
	  ld  d, $02                  ; d = $02
    ld  e, $02                  ; e = $02
	  ld  h, $02                  ; h = $02
	  ld  l, $02                  ; l = $02

    dec b                       ; b = $01
    dec c                       ; c = $01
	  dec d                       ; d = $01
    dec e                       ; e = $01
	  dec h                       ; h = $01
	  dec l                       ; l = $01
    dec a                       ; a = $01

    ;; check z flag
    ld  a, $01                  ; a = $01
    dec a                       ; a = $00

    ;; check h / c flag
    ld  a, $00                  ; a = $00
    dec a                       ; a = $ff

    ;; check h flag
    ld  a, $10                  ; a = $10
    dec a                       ; a = $0f
