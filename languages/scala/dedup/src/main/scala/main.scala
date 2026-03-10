
import java.io.{File, PrintWriter}
import scala.io.Source
import scala.collection.mutable

case class Person(email: String, nome: String, sobrenome: String, telefone: String, var count: Int)

@main
def main(): Unit = {
  val resourcesDir = new File("src/main/resources")
  if (!resourcesDir.exists() || !resourcesDir.isDirectory) {
    println("Resource folder not found.")
    return
  }

  val csvFiles = resourcesDir.listFiles().filter(_.getName.endsWith(".csv"))
  if (csvFiles == null || csvFiles.isEmpty) {
    println("No CSV files found in resource folder.")
    return
  }

  val sortedCsvFiles = csvFiles.sortBy(_.getName)
  val dedupedMap = mutable.Map[String, Person]()

  sortedCsvFiles.foreach { file =>
    val lines = Source.fromFile(file).getLines().toList
    if (lines.nonEmpty) {
      val header = lines.head.split(",").map(_.trim)
      val emailIdx = header.indexOf("Email")
      val nomeIdx = header.indexOf("Nome")
      val sobrenomeIdx = header.indexOf("Sobrenome")
      val telefoneIdx = header.indexOf("Telefone")

      if (emailIdx != -1 && nomeIdx != -1 && sobrenomeIdx != -1 && telefoneIdx != -1) {
        lines.tail.foreach { line =>
          val columns = splitCsvLine(line)
          if (columns.length > Math.max(emailIdx, Math.max(nomeIdx, Math.max(sobrenomeIdx, telefoneIdx)))) {
            val email = columns(emailIdx).trim
            val nome = columns(nomeIdx).trim
            val sobrenome = columns(sobrenomeIdx).trim
            val telefone = columns(telefoneIdx).trim
            
            if (email.nonEmpty && telefone.nonEmpty) {
              if (dedupedMap.contains(email)) {
                dedupedMap(email).count += 1
              } else {
                dedupedMap.put(email, Person(email, nome, sobrenome, telefone, 1))
              }
            }
          }
        }
      }
    }
  }

  println(s"\nDeduplicated result (${dedupedMap.size} records):")
  println("Email | Nome | Sobrenome | Telefone | Count")
  println("-" * 60)
  
  val outputFile = new File("deduped_results.csv")
  val writer = new PrintWriter(outputFile)
  
  try {
    writer.println("Email,Nome,Sobrenome,Telefone,Count")
    
    dedupedMap.values.foreach { p =>
      val csvLine = s"${quoteIfNecessary(p.email)},${quoteIfNecessary(p.nome)},${quoteIfNecessary(p.sobrenome)},${quoteIfNecessary(p.telefone)},${p.count}"
      writer.println(csvLine)
    }
  } finally {
    writer.close()
  }
}

def quoteIfNecessary(value: String): String = {
  if (value.contains(",") || value.contains("\"")) {
    "\"" + value.replace("\"", "\"\"") + "\""
  } else {
    value
  }
}

def splitCsvLine(line: String): Array[String] = {
  line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")
}

