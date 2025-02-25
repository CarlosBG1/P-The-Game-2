import java.awt.Graphics2D
import scala.io.Source
import scala.util.Using


case class Item(x: Int, y: Int, texto: String)
case class Hitbox(x1: Int, y1: Int, x2: Int, y2: Int)

class Elementos {
  private val (items, collisionItems) = readItemsFromFile("/Res/Escenarios/Esc1.txt")

  private def readItemsFromFile(filePath: String): (List[Item], List[Hitbox]) = {
    val resource = getClass.getResourceAsStream(filePath)
    if (resource == null) {
      println(s"Error: File not found: $filePath")
      (List.empty[Item], List.empty[Hitbox])
    } else {
      Using(Source.fromInputStream(resource)) { source =>
        val allItems = source.getLines().flatMap { line =>
          val Array(tipo, datos) = line.split("/,")
          tipo match {
            case "t" =>
              val Array(x, y, texto) = datos.split("/.")
              Some(Left(Item(x.toInt, y.toInt, texto)))
            case "e" =>
              val Array(x, y, texto) = datos.split("/.")
              val estructura = getClass.getResourceAsStream(s"/Res/Estructuras/$texto.txt")
              if (estructura == null) {
                println(s"Error: File not found: /Res/Estructuras/$texto.txt")
                None
              } else {
                val (nestedItems, nestedHitboxes) = readItemsFromFile(s"/Res/Estructuras/$texto.txt")
                val itemsWithOffset = nestedItems.map { item =>
                  Item(item.x + x.toInt, item.y + y.toInt, item.texto)
                }
                val hitboxesWithOffset = nestedHitboxes.map { hitbox =>
                  Hitbox(hitbox.x1 + x.toInt, hitbox.y1 + y.toInt, hitbox.x2 + x.toInt, hitbox.y2 + y.toInt)
                }
                itemsWithOffset.map(Left(_)) ++ hitboxesWithOffset.map(Right(_))
              }
            case "c" =>
              val Array(x1, y1, x2, y2) = datos.split("/.")
              Some(Right(Hitbox(x1.toInt, y1.toInt, x2.toInt, y2.toInt)))
          }
        }.toList

        val items = allItems.collect { case Left(item: Item) => item }
        val hitboxes = allItems.collect { case Right(hitbox) => hitbox }
        (items, hitboxes)
      }.getOrElse {
        println(s"Error reading file: $filePath")
        (List.empty[Item], List.empty[Hitbox])
      }
    }
  }

  def dibujarMarco(g: Graphics2D, width: Int, vWidth: Int, height: Int, fuente: Int): Unit = {
    for (y1 <- 1 until (height - 1) / 10) {
      g.drawString("║", 0, y1 * fuente + 10)
      if (y1 != 5) {
        g.drawString("║", (width / 5) * (fuente / 2), y1 * fuente + 10)
      } else {
        g.drawString("╠", (width / 5) * (fuente / 2), y1 * fuente + 10)
        for (x1 <- 1 until (vWidth - width) / 5 - 3) {
          g.drawString("═", x1 * (fuente / 2) + (width / 5) * (fuente / 2), y1 * fuente + 10)
        }
      }
    }
    for (x1 <- 1 until (width + 2) / 5) {
      g.drawString("═", x1 * (fuente / 2), 10)
      g.drawString("═", x1 * (fuente / 2), (height / 10 - 1) * fuente + 10)
    }
    g.drawString("╔", 0, 10)
    g.drawString("╗", (width / 5) * (fuente / 2), 10)
    g.drawString("╚", 0, (height / 10 - 1) * fuente + 10)
    g.drawString("╝", (width / 5) * (fuente / 2), (height / 10 - 1) * fuente + 10)
  }

  def dibujar(g: Graphics2D): Unit = {
    items.foreach { item =>
      g.drawString(item.texto, item.x, item.y)
    }
  }

  def distancia(x: Int, y: Int, fuente: Int, direction: String): Int = {
    val distances = collisionItems.flatMap { item =>
      direction match {
        case "l" =>
          if (x >= item.x2 && y > item.y1 - fuente && y < item.y2) Some(x - item.x2) else None
        case "r" =>
          if (x <= item.x1 && y > item.y1 - fuente && y < item.y2) Some(item.x1 - x - fuente/2) else None
        case "u" =>
          if (y >= item.y2 && x > item.x1 - fuente/2 && x < item.x2) Some(y - item.y2) else None
        case "d" =>
          if (y <= item.y1 - fuente && x > item.x1 - fuente/2 && x < item.x2) Some(item.y1 - y - fuente) else None
        case _ => None
      }
    }
    if (distances.nonEmpty) distances.min else Int.MaxValue
  }

  def atac (g: Graphics2D, x: Int, y: Int, fuente: Int, tipo: Int): Int = {
    1
  }
}