import re

WORD_RE = re.compile(r'\w+')

def count_words(filename):
    index = {}
    with open(filename, encoding='utf-8') as fp:
        for line_no, line in enumerate(fp, start=1):
            for match in WORD_RE.finditer(line):
                word = match.group()
                column_no = match.start() + 1
                location = (line_no, column_no)
                # ugly code
                occurrences = index.get(word, [])
                occurrences.append(location)
                index[word] = occurrences
    for word in sorted(index, key=str.upper):
        print(word, index[word])
if __name__ == '__main__':
    count_words('example_3_4_words')