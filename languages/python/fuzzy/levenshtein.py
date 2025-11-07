def levenshtein_distance(str1, str2):
    """
    Calculate the Levenshtein distance between two strings.
    """
    len_str1 = len(str1) + 1
    len_str2 = len(str2) + 1

    # Create a matrix to store distances
    dp = [[0 for _ in range(len_str2)] for _ in range(len_str1)]

    # Initialize the first row and column
    for i in range(len_str1):
        dp[i][0] = i
    for j in range(len_str2):
        dp[0][j] = j

    # Fill the matrix using the distance formula
    for i in range(1, len_str1):
        for j in range(1, len_str2):
            if str1[i - 1] == str2[j - 1]:
                dp[i][j] = dp[i - 1][j - 1]  # No operation required
            else:
                dp[i][j] = min(
                    dp[i - 1][j] + 1,  # Deletion
                    dp[i][j - 1] + 1,  # Insertion
                    dp[i - 1][j - 1] + 1  # Substitution
                )

    return dp[-1][-1]  # Return the Levenshtein distance


def similarity(str1, str2):
    """
    Calculate the similarity between two strings based on the Levenshtein distance.
    Returns a percentage.
    """
    distance = levenshtein_distance(str1, str2)
    max_len = max(len(str1), len(str2))
    similarity_percentage = ((max_len - distance) / max_len) * 100
    return similarity_percentage
