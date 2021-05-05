#!/bin/bash
set -ux

dst_dir=blargg-gb-tests
site_url=https://gbdev.gg8.se/files/roms/blargg-gb-tests
blargg_gb_tests=(
                 cbg_sound
                 cpu_instrs
                 dmg_sound
                 halt_bug
                 instr_timing
                 interrupt_time
                 mem_timing-2
                 mem_timing
                 oam_bug
)

if [ ! -e ${dst_dir} ]; then
    mkdir ${dst_dir}
    cd ${dst_dir}
fi
for test in "${blargg_gb_tests[@]}" ; do
    filename=${test}.zip
    wget ${site_url}/${filename} -O ${filename}
    unzip ${filename}
done
