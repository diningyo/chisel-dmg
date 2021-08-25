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
    ld  c, $01                  ; c = $01
    ld  d, $00                  ; d = $00
    ld  e, $01                  ; e = $01
    ld  h, $00                  ; h = $00
    ld  l, $01                  ; l = $01

    dec bc                      ; bc = $00
    dec de                      ; de = $00
    dec hl                      ; hl = $00
    dec sp                      ; sp = $ffff

    ;; check h / h = 0
    ld  c, $00
    ld  b, $01
    dec bc

    ;; check z / c -> z = 0 / c = 0
    ld  d, $00
    ld  e, $00
    dec de
