import java.awt.{Graphics, Graphics2D, Color, Font}
import javax.swing.{JPanel, Timer}
import java.awt.event.{ActionEvent, ActionListener}
import scala.io.Source
import scala.util.Using

class AnimationPanel extends JPanel {
  private var lines: List[String] = List()
  private var currentLine: Int = 0
  private var timer: Timer = _
  private var textToDraw: Option[(String, Int, Int)] = None

  def loadAnimation(x: Int, y: Int, nombre: String): Unit = {
    val resource = getClass.getResourceAsStream(s"/Res/Animaciones/$nombre.txt")
    if (resource == null) {
      println(s"Error: File not found: $nombre.txt")
    } else {
      Using(Source.fromInputStream(resource)) { source =>
        lines = source.getLines().toList
        currentLine = 0
        if (timer != null) timer.stop()
        timer = new Timer(0, new ActionListener {
          override def actionPerformed(e: ActionEvent): Unit = {
            if (currentLine < lines.length) {
              val Array(tipo, datos) = lines(currentLine).split("/,")
              tipo match {
                case "v" =>
                  val delay = datos.toInt
                  timer.setDelay(delay)
                case "f" =>
                  val Array(dx, dy, texto) = datos.split("/.")
                  textToDraw = Some((texto, dx.toInt + x, dy.toInt + y))
                case "p" =>
                  textToDraw = None
              }
              currentLine += 1
              repaint()
            } else {
              timer.stop()
            }
          }
        })
        timer.start()
      }.recover {
        case e: Exception => println(s"Error reading animation file: ${e.getMessage}")
      }
    }
  }

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    textToDraw.foreach { case (texto, x, y) =>
      g.setColor(Color.WHITE) // Cambia el color del texto a rojo
      g.setFont(new Font("Monospaced", Font.PLAIN, 12))
      g.drawString(texto, x, y)
    }
  }
}