# Example strings
from levenshtein import similarity

string1 = "Python is amazing for data science!"
string2 = "Python is great for data science."

# Calculate similarity
similarity_score = similarity(string1, string2)
print(f"Similarity between the strings: {similarity_score:.2f}%")
