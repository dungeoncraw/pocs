
val longText = "And Eurypylus, son of Euaemon, killed Hypsenor, the son of noble Dolopion, who had been made priest of the river Scamander, and was honoured among the people as though he were a god. Eurypylus gave him chase as he was flying before him, smote him with his sword upon the arm, and lopped his strong hand from off it. The bloody hand fell to the ground, and the shades of death, with fate that no man can withstand, came over his eyes. Thus furiously did the battle rage between them. As for the son of Tydeus, you could not say whether he was more among the Achaeans or the Trojans. He rushed across the plain like a winter torrent that has burst its barrier in full flood; no dykes, no walls of fruitful vineyards can embank it when it is swollen with rain from heaven, but in a moment it comes tearing onward, and lays many a field waste that many a strong man hand has reclaimed- even so were the dense phalanxes of the Trojans driven in rout by the son of Tydeus, and many though they were, they dared not abide his onslaught.\n"

@main
def main(): Unit = {
  /*
  * convert the string into a list of words. Should be memory efficient.
  * It can allow duplicated words, so we can count how many times a word appears.
  */
  val words = longText.split(" ").toList
  val normalizedWords = words.map(_.toUpperCase)
  val wordCount = normalizedWords.groupBy(identity).map{
    case (word, occurrence) => word -> occurrence.length
  }
  println(wordCount)
  val sortedWords = wordCount.toList.sortBy(_._2).reverse
  sortedWords.take(5).foreach(println)

}

