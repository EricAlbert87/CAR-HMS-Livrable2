"""Génère des annotations Cucumber exactes pour les étapes du livrable 1.
Les assertions réelles sont dans Steps.kt (aucune étape ignorée silencieusement).
"""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
contracts = ROOT / 'src/test/resources/contracts'
dest = ROOT / 'src/test/kotlin/ca/uottawa/seg3102/carhms/contracts/StepBindings.kt'
prefixes = ('Étant donné ', 'Quand ', 'Alors ', 'Et ')
steps = sorted({line.strip()[len(p):] for f in contracts.glob('*.feature') for line in f.read_text(encoding='utf-8').splitlines() for p in prefixes if line.strip().startswith(p)})

def kotlin_quote(value: str) -> str:
    return '"' + value.replace('\\', '\\\\').replace('"', '\\"').replace('$', '${\'$\'}') + '"'

lines = ['package ca.uottawa.seg3102.carhms.contracts', '', 'import io.cucumber.java.en.Given', '',
         '/** Correspondances exactes des étapes; générées depuis les 11 contrats. */',
         'class StepBindings {', '    private val handler = Steps()', '']
for n, sentence in enumerate(steps, 1):
    pattern = r'^\Q' + sentence.replace(r'\E', r'\E\\E\Q') + r'\E$'
    lines.append(f'    @Given({kotlin_quote(pattern)})')
    lines.append(f'    fun etape{n:03d}() = handler.step({kotlin_quote(sentence)})')
    lines.append('')
lines.extend(['}', ''])
dest.parent.mkdir(parents=True, exist_ok=True)
dest.write_text('\n'.join(lines), encoding='utf-8')
print(f'{len(steps)} définitions générées : {dest}')
