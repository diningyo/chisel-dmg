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

    ;;; The case of adding $06
    ;; a[3:0] = 0 ~ 3 / h = 1 => a[3:0] + $06 / h = 0
    ld  a, $0f                  ; a = $0f
    inc a                       ; a = $10 / h = 1
    ld  a, $00                  ; a = $03
    daa                         ; a = $06

    ld  a, $0f                  ; a = $0f
    inc a                       ; a = $10 / h = 1
    ld  a, $09                  ; a = $09
    daa                         ; a = $0f

    ;; a[3:0] = $a ~ $f / h = 0 / c = 0 => a[3:0] + $06
    ld  a, $0a                  ; a = $0a
    daa                         ; a = $10
    ld  a, $0f                  ; a = $0f
    daa                         ; a = $15

    ;; The case of adding $60
    ;; a[7:4] = 0 ~ 9 / c = 1 => a[7:4] + $60 / c = 1 / When C Flag is high, this flag keep after DAA.
    ld  a, $20                  ; a = $ff
    add a, $f0                  ; a = $01 / c = 1
    ld  a, $00                  ; a = $00
    daa                         ; a = $60

    ld  a, $20                  ; a = $ff
    add a, $f0                  ; a = $01 / c = 1
    ld  a, $90                  ; a = $30
    daa                         ; a = $f0

    ;; a[7:4] = a ~ f / c = 0 => a[7:4] + $60 / c = 1
    ld  a, $10                  ; a = $0a
    add a, $01                  ; b = $02 / c = 0
    ld  a, $a0                  ; a = $a0
    daa                         ; a = $00 / z = 1 / c = 1
    ld  a, $10                  ; a = $0a
    add a, $01                  ; b = $02 / c = 0
    ld  a, $f0                  ; a = $f0
    daa                         ; a = $50 / c = 1

    ;;; The case of adding $66
    ;; a[7:4] = 0x8, a[3:0] = 0xf
    ld  a, $10                  ; a = $ff
    add a, $e0                  ; a = $01 / c = 0
    ld  a, $9f                  ; a = $00
    daa                         ; a = $05 / c = 1
