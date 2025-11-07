from difflib import SequenceMatcher


def fuzzy_token_sort(str1, str2):
    tokens1 = str1.lower().split()
    tokens2 = str2.lower().split()

    tokens1_sorted = sorted(tokens1)
    tokens2_sorted = sorted(tokens2)

    sorted_str1 = " ".join(tokens1_sorted)
    sorted_str2 = " ".join(tokens2_sorted)

    similarity = SequenceMatcher(None, sorted_str1, sorted_str2)
    for tag, i1, i2, j1, j2 in similarity.get_opcodes():
        print(f"{tag:7} s1[{i1}:{i2}] --> s2[{j1}:{j2}]  {str1[i1:i2]!r:>8} --> {str2[j1:j2]!r}")
    return int(similarity.ratio() * 100)
