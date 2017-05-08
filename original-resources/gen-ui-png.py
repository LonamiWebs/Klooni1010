#!/usr/bin/python3.6

import os
from subprocess import run, DEVNULL

multipliers = [0.75, 1.0, 1.25, 1.5, 2.0, 4.0]

# Another option would be to query all IDs 'inkscape -S' as described on:
#   http://tavmjong.free.fr/INKSCAPE/MANUAL/html/CommandLine-Query.html
#
# More exporting notes (arguments used and default DPI):
#   http://tavmjong.free.fr/INKSCAPE/MANUAL/html/CommandLine-General.html
#   http://tavmjong.free.fr/INKSCAPE/MANUAL/html/CommandLine-Export.html
ids = [
    'back',
    'button_down',
    'button_up',
    'cancel',
    'credits',
    'cup',
    'home',
    'issues',
    'ok',
    'palette',
    'play',
    'play_saved',
    'replay',
    'share',
    'snap_off',
    'snap_on',
    'sound_off',
    'sound_on',
    'star',
    'stats',
    'stopwatch',
    'web'
]

cells = [
    'basic',
    'bubble'
]

inkscape_default_dpi = 90
svg = 'buttons.svg'
root = '../android/assets/ui'

for multiplier in multipliers:
    folder = os.path.join(root, f'x{multiplier}')
    os.makedirs(folder, exist_ok=True)
    
    dpi = int(inkscape_default_dpi * multiplier)
    print('Generating assets for', folder)
    for objectid in ids:
        filename = os.path.join(folder, objectid + '.png')
        # -z not to use the X server
        # -i to select the given object id
        # -j to only export that object, even with others overlapped
        # -e to export a  file
        # -d to specify the DPI
        run(f'inkscape -z -i{objectid} -j -e{filename} -d{dpi} {svg}',
            shell=True, stdout=DEVNULL)
    
    folder = os.path.join(folder, 'cells')
    os.makedirs(folder, exist_ok=True)
    for cellid in cells:
        filename = os.path.join(folder, cellid + '.png')
        run(f'inkscape -z -i{cellid} -j -e{filename} -d{dpi} {svg}',
            shell=True, stdout=DEVNULL)
