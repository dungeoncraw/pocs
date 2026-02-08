import locale
import pyuca

if __name__ == '__main__':
    my_locale = locale.setlocale(locale.LC_COLLATE, 'pt_BR.UTF-8')
    print(my_locale)
    fruits = ['caju', 'caja', 'atemoia', 'cajá', 'açaí', 'acerola']
    sorted_fruits = sorted(fruits, key=locale.strxfrm)
    print(sorted_fruits)

    coll = pyuca.Collator()
    sorted_fruits = sorted(fruits, key=coll.sort_key)
    print(sorted_fruits)