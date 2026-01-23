import re
import collections

WORD_RE = re.compile(r'\w+')

def count_words(filename):
    # Is an assignment of default dict using default_factory
    # so if index[k] is not present, it will be created with the default value instead of raising KeyError
    index = collections.defaultdict(list)
    with open(filename, encoding='utf-8') as fp:
        for line_no, line in enumerate(fp, 1):
            for match in WORD_RE.finditer(line):
                word = match.group()
                column_no = match.start() + 1
                location = (line_no, column_no)
                index[word].append(location)
    for word in sorted(index, key=str.upper):
        print(word, index[word])

if __name__ == '__main__':
    count_words('example_3_4_words')