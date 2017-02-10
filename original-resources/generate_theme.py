#!/usr/bin/env python3

# Generates a theme from `theme.svg`
import re
import os
import subprocess

color_re = \
    re.compile('<g\s+id="export_(\w+)"\s+style=".*?fill:#([0-9a-f]+)[\S\s]*?')

template = '''{{
    "name": "{name}",
    "price": {price},
    "colors": {{
        "background": "{background}",
        "buttons": [
            "{button_0}",
            "{button_1}",
            "{button_2}",
            "{button_3}"
        ],
        "empty_cell": "{empty_cell}",
        "cells": [
            "{cell_0}", "{cell_1}", "{cell_2}",
            "{cell_3}", "{cell_4}", "{cell_5}", "{cell_6}",
            "{cell_7}", "{cell_8}"
        ],
        "current_score": "{current_score}",
        "high_score": "{high_score}",
        "bonus": "{bonus}",
        "band": "{band}"
    }},
    "cell_texture": "{cell_tex}"
}}
'''

def price_ok(price):
    try:
        price = int(price)
        if price < 0:
            raise ValueError('Price must be ≥ 0.')
    except:
        print('Invalid price detected. Using 0.')
        return False
    
    return True


def main():
    if not os.path.isfile('in.svg'):
        print('Error: No "in.svg" found. Aborting.')
        return

    print('Reading "in.svg"…')
    with open('in.svg', 'r', encoding='utf-8') as f:
        xml = f.read().replace('\n', '')
    
    print('Finding used colors…')
    replacements = {}
    
    for m in color_re.finditer(xml):
        # Append 'ff' because the themes require the alpha to be set
        replacements[m.group(1)] = m.group(2)+'ff'
    
    print('Almost done, we only need some more information.')
    replacements['name'] = input('Enter theme name: ')
    replacements['price'] = input('Enter theme price: ')
    replacements['cell_tex'] = \
        input('Enter cell texture (default "basic.png"): ')
    
    if not replacements['price'] or not price_ok(replacements['price']):
        print('Invalid price detected. Using 0.')
        replacements['price'] = 0
    
    if not replacements['cell_tex']:
        print('No texture specified. Using default "basic.png" texture.')
        replacements['cell_tex'] = 'basic.png'
    
    print('Generating theme…')
    with open('out.theme', 'w', encoding='utf-8') as f:
        f.write(template.format_map(replacements))
    
    print('Done!')


if __name__ == '__main__':
    main()
