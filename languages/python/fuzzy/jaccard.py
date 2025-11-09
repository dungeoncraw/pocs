def jaccard_string_chars(str1, str2, is_words = False):
    set1 = set(str1.lower()) if not is_words else set(str1.lower().split())
    set2 = set(str2.lower()) if not is_words else set(str2.lower().split())
    # intersection / union
    return len(set1 & set2) / len(set1 | set2)