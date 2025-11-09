# Example strings
from fuzzy import fuzzy_token_sort
from jaccard import jaccard_string_chars
from levenshtein import similarity

string1 = "Python is amazing for data science!"
string2 = "Python is great for data science."

# Calculate similarity
similarity_score = similarity(string1, string2)
print(f"Levenshtein Similarity: {similarity_score:.2f}%")

print(fuzzy_token_sort("fuzzy wuzzy was a bear", "wuzzy fuzzy was a bear"))
print(fuzzy_token_sort("new york city", "city new york"))
print(fuzzy_token_sort("new yokr city", "city new york"))

s1 = "night"
s2 = "nacht"
similarity = jaccard_string_chars(s1, s2)
print(f"Jaccard Similarity: {similarity:.3f}")

text1 = "the quick brown fox jumps"
text2 = "the lazy brown dog sleeps"
similarity = jaccard_string_chars(text1, text2)
print(f"Jaccard Phrase Similarity: {similarity:.3f}")