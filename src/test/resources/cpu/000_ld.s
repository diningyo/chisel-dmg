	.memoryMap
	     defaultSlot 0
	     slot 0 $0000 size $4000
	     slot 1 $C000 size $4000
	.endMe

	.romBankSize   $4000 ; generates $8000 byte ROM
	.romBanks      2

	.org $150
    ;; ld r, a
	  ld a, $01                  ; a = $01
	  ld a, a
	  ld b, a
	  ld c, a
	  ld d, a
	  ld e, a
	  ld h, a
	  ld l, a

    ;; ld r, b
	  ld b, $02                  ; b = $02
	  ld a, b
	  ld b, b
	  ld c, b
	  ld d, b
	  ld e, b
	  ld h, b
	  ld l, b

    ;; ld r, c
    ld c, $03                  ; c = $03
	  ld a, c
	  ld b, c
	  ld c, c
	  ld d, c
	  ld e, c
	  ld h, c
	  ld l, c

    ;; ld r, d
    ld d, $04                  ; d = $04
	  ld a, d
	  ld b, d
	  ld c, d
	  ld d, d
	  ld e, d
	  ld h, d
	  ld l, d

    ;; ld r, e
    ld e, $05                  ; e = $05
	  ld a, e
	  ld b, e
	  ld c, e
	  ld d, e
	  ld e, e
	  ld h, e
	  ld l, e

    ;; ld r, h
    ld h, $06                  ; h = $06
	  ld a, h
	  ld b, h
	  ld c, h
	  ld d, h
	  ld e, h
	  ld h, h
	  ld l, h

    ;; ld r, l
    ld l, $07                  ; l = $07
	  ld a, l
	  ld b, l
	  ld c, l
	  ld d, l
	  ld e, l
	  ld h, l
	  ld l, l
