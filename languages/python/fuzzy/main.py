# Example strings
from fuzzy import fuzzy_token_sort
from levenshtein import similarity

string1 = "Python is amazing for data science!"
string2 = "Python is great for data science."

# Calculate similarity
similarity_score = similarity(string1, string2)
print(f"Similarity between the strings: {similarity_score:.2f}%")

print(fuzzy_token_sort("fuzzy wuzzy was a bear", "wuzzy fuzzy was a bear"))
print(fuzzy_token_sort("new york city", "city new york"))
print(fuzzy_token_sort("new yokr city", "city new york"))