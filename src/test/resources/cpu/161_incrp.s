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
    ld  l, $00                  ; l = $00

    inc bc                      ; b = $01
    inc de                      ; d = $01
    inc hl                      ; h = $01
    inc sp                      ; a = $01

    ;; check h / h = 0
    ld  c, $ff
    inc bc

    ;; check z / c -> z = 0 / c = 0
    ld  d, $ff
    ld  e, $ff
    inc de
