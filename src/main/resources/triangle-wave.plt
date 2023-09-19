p = {PERIOD}
a = {AMPLITUDE}
s = {SHIFT}

set key noautotitle
set style line 1 linecolor rgb '{COLOR}' linewidth {LINE_WIDTH}

set xrange [{X_RANGE_START}:{X_RANGE_END}]
set yrange [{Y_RANGE_START}:{Y_RANGE_END}]

# set xtics 0, 1 format "%d"

set terminal pngcairo enhanced size {PLOT_WIDTH},{PLOT_HEIGHT}
set output '{OUTPUT_FILE_NAME}'

f(x) = (4 * a / p) * (x - s - (p / 2) * floor(2 * (x - s) / p + 0.5)) * (-1) ** floor(2 * (x - s) / p + 0.5)
plot f(x) with lines ls 1

replot
exit