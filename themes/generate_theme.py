#!/usr/bin/env python3

# Generates a theme from `theme.svg`
import re
import os
import subprocess

group_id_re = \
    re.compile('<g[\S\s]+?id="export_(\w+)"[\S\s]+?>')

fill_re = \
    re.compile('fill:#([0-9a-f]+)')

template = '''{{
    "name": "{name}",
    "price": {price},
    "colors": {{
        "background": "{background}",
        "foreground": "{foreground}",
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
        "band": "{band}",
        "text": "{text}"
    }},
    "cell_texture": "{cell_tex}"
}}
'''

out_dir = '../android/assets/themes/'
theme_list = os.path.join(out_dir, 'theme.list')


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
    # Look for all the files which are not called 'template.svg'
    files = [f for f in os.listdir()
                if f.endswith('.svg') and f != 'template.svg']

    if not files:
        print('No .svg files were found. '
              'Please see CREATING-THEMES.txt for more information')
        return

    # Work on all the files to generate the corresponding themes
    for filename in files:
        work(filename)

    print('Updating theme.list…')
    themes = []
    with open(theme_list, 'r', encoding='utf-8') as f:
        themes = [line.strip() for line in f]

    added_names = [os.path.splitext(f)[0] for f in files]
    added_count = 0
    for name in added_names:
        if name not in themes:
            themes.append(name)
            added_count += 1

    with open(theme_list, 'w', encoding='utf-8') as f:
        f.write('\n'.join(themes))
        f.write('\n')

    print('Added {} new theme(s), updated {}.'.format(
            added_count, len(files) - added_count))


def work(filename):
    name = os.path.splitext(filename)[0]
    with open(filename, 'r', encoding='utf-8') as f:
        xml = f.read().replace('\n', '')

    replacements = {}
    for m in group_id_re.finditer(xml):
        f = fill_re.search(m.group(0))
        if not f:
            raise ValueError(
                'Error: The object %s missing the fill attribute' % m.group(1))

        # Append 'ff' because the themes require the alpha to be set
        replacements[m.group(1)] = f.group(1) + 'ff'

    replacements['name'] = input('Enter theme name for "{}": '.format(name))
    replacements['price'] = input('Enter theme price: ')
    replacements['cell_tex'] = \
        input('Enter cell texture (default "basic.png"): ')

    if not replacements['price'] or not price_ok(replacements['price']):
        print('Invalid price detected. Using 0.')
        replacements['price'] = 0

    if not replacements['cell_tex']:
        print('No texture specified. Using default "basic.png" texture.')
        replacements['cell_tex'] = 'basic.png'

    output = os.path.join(out_dir, name+'.theme')

    print('Saving theme to {}…'.format(output))
    with open(output, 'w', encoding='utf-8') as f:
        f.write(template.format_map(replacements))

    print('Done!')


if __name__ == '__main__':
    main()
