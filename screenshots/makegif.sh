#!/bin/bash

mkdir frames

# -t <seconds.millis>
# -r fps
ffmpeg -i NoNonsenseFilePickerLight.webm -r 15 -vf scale=270:-1 frames/frame%03d.png


convert -delay 10 -loop 0 -dither None -colors 80 "frames/frame*png" -fuzz "30%" -layers OptimizeFrame "anim.gif"

rm -rf frames
