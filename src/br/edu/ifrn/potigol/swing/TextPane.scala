package br.edu.ifrn.potigol.swing

import scala.swing.TextComponent
import javax.swing.JTextPane
import javax.swing.text.{ EditorKit, StyledDocument }
import scala.swing.Frame
import java.io.PipedInputStream
import scala.swing.TextField
import java.awt.Rectangle
import scala.swing.BorderPanel
import scala.swing.BorderPanel.Position.{ Center, East, West, South }
import java.io.PrintWriter
import java.io.PipedOutputStream
import java.io.PrintStream
import java.util.Scanner
import scala.swing.event.KeyTyped
import scala.swing.event.Key
import scala.swing.event._



class TextPane() extends TextComponent {
  override lazy val peer: JTextPane = new JTextPane with SuperMixin
  def contentType: String = peer.getContentType
  def contentType_=(t: String) = peer.setContentType(t)
  def editorKit: EditorKit = peer.getEditorKit
  def editorKit_=(k: EditorKit) = peer.setEditorKit(k)
  def styledDocument: StyledDocument = peer.getStyledDocument()
}

class Terminal extends Frame {
  val log = new TextPane {
    editable = false
    bounds.setBounds(10, 10, 345, 250)
  }

  val prompt = new TextField() {
    bounds.setBounds(10, 270, 356, 80)

  }
  listenTo(prompt.keys)

  contents = new BorderPanel() {
    layout(log) = Center
    layout(prompt) = South
  }

  val inPipe = new PipedInputStream()
  val outPipe = new PipedInputStream()
  System.setIn(inPipe);

  System.setOut(new PrintStream(new PipedOutputStream(outPipe), true));
  val inWriter = new PrintWriter(new PipedOutputStream(inPipe), true)
  visible = true
  size = new scala.swing.Dimension(392, 400)

  def execute() {
    val text = prompt.text
    prompt.text = ""
    println(text)
    inWriter.print(text.trim().replaceAll("\r\n", ""))
  }
  import scala.swing.event.Key
  reactions += {
    case KeyPressed(a, Key.Enter, c, d) => execute()
  }
/*
  override def keyPressed(e: KeyEvent) {
    if (e.keyCode == KeyEvent.VK_ENTER) execute()
  }

  override def keyReleased(e: KeyEvent) {
    if (e.getKeyCode == KeyEvent.VK_ENTER) execute()
  }

  override def keyTyped(e: KeyEvent) {
    if (e.getKeyCode == KeyEvent.VK_ENTER) execute()
  }

  //***** Static fields/methods below, these should go in a companion object *****//
  def setConsole(title: String) {
    EventQueue.invokeLater(new Runnable() {

      def run() {
        new Console(title)
      }
    })
  }
  ***** End of static fields/methods *****
*/
}