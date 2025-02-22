import scala.swing._
import javax.swing.{Timer, ImageIcon}
import java.awt.{Color, Graphics2D, Font, Dimension}
import java.awt.event.{KeyListener, KeyEvent, ComponentAdapter, ComponentEvent}

object PTG_2 extends SimpleSwingApplication {
  private var x = 200
  private var y = 240
  private var atac = 0
  private val teclasPresionadas: scala.collection.mutable.Set[Int] = scala.collection.mutable.Set()

  def top: Frame = new MainFrame {
    private var contador = 0
    private var vHeight = 800
    private var vWidth = 1200
    private var sprint = 2
    private var dLeft = 0
    private var dRight = 0
    private var dUp = 0
    private var dDown = 0
    private var moveLeft = false
    private var moveRight = false
    private var moveUp = false
    private var moveDown = false

    private val diagonalSpeed = 5
    private val height = 360
    private val width = 360
    private val fuente = 12
    private val etiqueta = new Label("Texto inicial") { foreground = Color.WHITE }
    private val elementos = new Elementos

    val temporizador = new Timer(50, _ => {
      contador += 1
      sprint = 2
      if (teclasPresionadas.contains(KeyEvent.VK_ESCAPE)) System.exit(0)
      if (teclasPresionadas.contains(KeyEvent.VK_SHIFT)) sprint = 1
      if (teclasPresionadas.contains(KeyEvent.VK_SPACE)) atac = 1
      if (contador % sprint == 0) {
        etiqueta.text = s"Frame: $dLeft, $dRight, $dUp, $dDown"

        dLeft = elementos.distancia(x, y, fuente, "l")
        dRight = elementos.distancia(x, y, fuente, "r")
        dUp = elementos.distancia(x, y, fuente, "u")
        dDown = elementos.distancia(x, y, fuente, "d")

        moveLeft = teclasPresionadas.contains(KeyEvent.VK_A) && (x > fuente / 2)
        moveRight = teclasPresionadas.contains(KeyEvent.VK_D) && (x < (width / 5 - 1) * (fuente / 2))
        moveUp = teclasPresionadas.contains(KeyEvent.VK_W) && (y > fuente + 10)
        moveDown = teclasPresionadas.contains(KeyEvent.VK_S) && (y < (height / 10 - 2) * fuente + 10)

        if (moveLeft && moveRight) {
          moveLeft = false
          moveRight = false
        }
        if (moveUp && moveDown) {
          moveUp = false
          moveDown = false
        }
        if (moveLeft && moveUp) {
          x -= math.min(dLeft, diagonalSpeed)
          dUp = elementos.distancia(x, y, fuente, "u")
          y -= math.min(dUp, diagonalSpeed)

        } else if (moveLeft && moveDown) {
          x -= math.min(dLeft, diagonalSpeed)
          dDown = elementos.distancia(x, y, fuente, "d")
          y += math.min(dDown, diagonalSpeed)

        } else if (moveRight && moveUp) {
          x += math.min(dRight, diagonalSpeed)
          dUp = elementos.distancia(x, y, fuente, "u")
          y -= math.min(dUp, diagonalSpeed)

        } else if (moveRight && moveDown) {
          x += math.min(dRight, diagonalSpeed)
          dDown = elementos.distancia(x, y, fuente, "d")
          y += math.min(dDown, diagonalSpeed)

        } else {
          if (moveLeft) x -= math.min(dLeft, fuente/2)
          if (moveRight) x += math.min(dRight, fuente/2)
          if (moveUp) y -= math.min(dUp, fuente/2)
          if (moveDown) y += math.min(dDown, fuente/2)
        }
      }
      repaint()
    })
    temporizador.start()

    private val panel = new Panel {
      preferredSize = new Dimension(vWidth, vHeight)
      background = Color.BLACK
      opaque = true
      override def paintComponent(g: Graphics2D): Unit = {
        super.paintComponent(g)
        g.setColor(Color.WHITE)
        g.setFont(new Font("Consolas", Font.PLAIN, fuente))
        g.drawString(etiqueta.text, width * fuente / 10 + fuente, 10)
        elementos.dibujar(g)
        elementos.dibujarMarco(g, width, vWidth, height, fuente)
        g.drawString("P", x, y)
        if (atac == 1) {
          elementos.animar(g, x, y, "ataque")
          atac = 0
        }
      }

      focusable = true
      requestFocusInWindow()
      peer.addKeyListener(new KeyListener {
        override def keyPressed(e: KeyEvent): Unit = teclasPresionadas += e.getKeyCode
        override def keyReleased(e: KeyEvent): Unit = teclasPresionadas -= e.getKeyCode
        override def keyTyped(e: KeyEvent): Unit = {}
      })
    }
    peer.addComponentListener(new ComponentAdapter {
      override def componentResized(e: ComponentEvent): Unit = {
        vWidth = size.width
        vHeight = size.height
        panel.preferredSize = new Dimension(vWidth, vHeight)
        panel.revalidate()
      }
    })

    contents = new BoxPanel(Orientation.Vertical) {
      contents += panel
    }

    title = "PTG 2"
    private val icono = new ImageIcon(getClass.getResource("Res/images.png")).getImage
    iconImage = icono

    size = new Dimension(vWidth, vHeight)
    centerOnScreen()
  }
}