import string
import unicodedata

def shave_marks(txt):
    """Remove all diacritic marks"""
    norm_txt = unicodedata.normalize('NFD', txt)
    shaved = ''.join(c for c in norm_txt
    if not unicodedata.combining(c))
    return unicodedata.normalize('NFC', shaved)
def shave_marks_latin(txt):
    """Remove all diacritic marks from Latin base characters"""
    norm_txt = unicodedata.normalize('NFD', txt)
    latin_base = False
    preserve = []
    for c in norm_txt:
        if unicodedata.combining(c) and latin_base:
            continue # ignore diacritic on Latin base char
        preserve.append(c)
        # if it isn't a combining char, it's a new base char
        if not unicodedata.combining(c):
            latin_base = c in string.ascii_letters
    shaved = ''.join(preserve)
    return unicodedata.normalize('NFC', shaved)

if __name__ == '__main__':
    print("====== shave marks ========")
    print(shave_marks('café'))
    print(shave_marks('OOô'))
    print(shave_marks('Straße'))
    print(shave_marks('Ö'))
    order = '“Herr Voß: • ½ cup of OEtker™ caffè latte • bowl of açaí.”'
    print(shave_marks(order))
    Greek = 'Ζέφυρος, Zéfiro'
    print(shave_marks(Greek))

    print("====== shave marks latin ========")
    print(shave_marks_latin('café'))
    print(shave_marks_latin('Straße'))
    print(shave_marks_latin('Ö'))
    print(shave_marks_latin(order))
    print(shave_marks_latin(Greek))
    print("====== test examples ========")

    tests = [
        "Zéfiro, Ζέφυρος",          # Latin + Greek (Greek accents should be preserved by _latin)
        "Ζέφυρος",                  # Greek only
        "Αθήνα",                    # Greek only
        "и\u0301",                  # Cyrillic 'и' + combining acute (explicit combining mark)
        "приве\u0301т",             # Cyrillic word with explicit combining accent
        "שָׁלוֹם",                  # Hebrew with vowel points (niqqud)
        "قُرْآن",                   # Arabic with diacritics (harakat)
    ]

    for s in tests:
        print("IN :", s)
        print("all:", shave_marks(s))
        print("lat:", shave_marks_latin(s))
        print("-" * 40)