package br.edu.ifrn.potigol.editor

import java.awt.{ Color, Dimension, Font }
import java.awt.event.{ ActionEvent, KeyEvent }
import java.io.PrintWriter
import scala.collection.mutable.Stack
import scala.swing.{ Action, BorderPanel }
import scala.swing.{ FileChooser, MainFrame, Menu, MenuBar, MenuItem, Separator, SimpleSwingApplication }
import scala.swing.BorderPanel.Position.{ Center, West }
import scala.swing.event.{ Key, KeyReleased, KeyTyped }
import javax.swing.{ BorderFactory, KeyStroke }
import javax.swing.text.SimpleAttributeSet
import br.edu.ifrn.potigol.swing.TextPane
import javax.swing.text.StyleConstants.{ setBold, setFontFamily, setForeground }
import scala.swing.Frame
import java.awt.Point
import scala.swing.Dialog
import br.edu.ifrn.potigol.parser.potigolParser.BOOLEANO
import br.edu.ifrn.potigol.parser.potigolParser.BS
import br.edu.ifrn.potigol.parser.potigolParser.CHAR
import br.edu.ifrn.potigol.parser.potigolParser.COMMENT
import br.edu.ifrn.potigol.parser.potigolParser.ES
import br.edu.ifrn.potigol.parser.potigolParser.FLOAT
import br.edu.ifrn.potigol.parser.potigolParser.ID
import br.edu.ifrn.potigol.parser.potigolParser.INT
import br.edu.ifrn.potigol.parser.potigolParser.MS
import br.edu.ifrn.potigol.parser.potigolParser.STRING
import javax.swing.text.Caret
import scala.swing.ScrollPane
import javax.swing.text.DefaultCaret
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import java.awt.Robot

object Editor extends SimpleSwingApplication {
  System.setErr(new java.io.PrintStream(new java.io.OutputStream() {
    override def write(i: Int) {}
  }))

  val tipos = List("Inteiro", "Real", "Texto", "Lógico", "Logico")
  val metodos = List("inverta", "cabeça", "ordene", "Lista", "Matriz", "Cubo",
    "inteiro", "arredonde", "texto", "real", "tamanho", "posição", "posiçao",
    "posicão", "posicao", "contém", "contem", "maiúsculo", "maiusculo",
    "minúsculo", "minusculo", "inverta", "divida", "lista", "cabeça", "cabeca",
    "cauda", "último", "ultimo", "pegue", "descarte", "selecione", "mapeie",
    "descarte_enquanto", "pegue_enquanto", "ordene", "junte", "insira",
    "remova", "mutável", "mutavel", "imutável", "imutavel", "vazia", "injete",
    "primeiro", "segundo", "terceiro", "quarto", "quinto", "sexto", "sétimo",
    "setimo", "oitavo", "nono", "décimo", "decimo")
  val funcoes = List("leia_inteiro", "leia_inteiros", "leia_real", "leia_reais",
    "leia_texto", "leia_textos", "sen", "cos", "tg", "aleatório", "aleatorio",
    "arcsen", "arccos", "arctg", "abs", "raiz", "log", "log10")

  val compilador = new br.edu.ifrn.potigol.Compilador(false)
  var arq: Option[String] = None
  var modificado = false
  val is = getClass().getResource("/fonts/DejaVuSansMono.ttf")
  // val fontname = "DejaVu Sans Mono"
  val fonte = Font.createFont(Font.TRUETYPE_FONT, is.openStream()).deriveFont(Font.BOLD, 20);
  //  var fonte = new java.awt.Font(fontname, Font.BOLD, 20)
  var corFrente = new Color(248, 248, 242)
  var corFundo = new Color(39, 40, 34)
  val undo = Stack[(String, Int)]()
  val robot = new Robot()

  override def main(args: Array[String]) {
    super.main(args)
    if (args.length > 1) {
      arq = Some(args(1))
      modificado = false
    }
  }
  /*
  override def startup(args: Array[String]) {
    println(args.length)
    if (args.length > 0) {
      arq = Some(args(1))
      modificado = false
    }
    super.startup(args)
  }
*/
  def top = new MainFrame {
    import javax.imageio.ImageIO
    val i = ImageIO.read(getClass().getResource("/potigol.png"));
    iconImage = i
    this.location = new Point(200, 100)
    title = s"${arq.getOrElse("Sem nome")} - Potigol"
    /*   arq match {
      case Some(nome) =>
        editor.text = scala.io.Source.fromFile(nome, "utf-8").getLines.mkString("\n")
        atualizar()
      case None=>
    }*/

    val numeracao = new TextPane() {
      border = BorderFactory.createCompoundBorder(
        border,
        BorderFactory.createEmptyBorder(10, 10, 10, 10));
      background = new Color(56, 57, 49)
      foreground = new Color(170, 167, 149)
      enabled = false
      editable = false
      focusable = false
      font = Font.createFont(Font.TRUETYPE_FONT, is.openStream()).deriveFont(Font.BOLD, 20);
      //new java.awt.Font(fontname, Font.PLAIN, 20)
    }
    def ed = editor
    val editor = new TextPane() {
      this.border = BorderFactory.createCompoundBorder(
        border,
        BorderFactory.createEmptyBorder(10, 10, 10, 10));
      caret.color = corFrente
      background = corFundo
      foreground = corFrente
      font = fonte
      text = "\n"
      caret.position = 0
    }
    contents = new scala.swing.ScrollPane() {
      contents = new BorderPanel() {
        layout(numeracao) = West
        layout(editor) = Center
      }
    }

    val arquivo = new FileChooser() {
      fileFilter = FiltroPotigol
    }

    var texto = ""
    size = new Dimension(800, 600)
    menuBar = new MenuBar {
      val menuEditar = new Menu("Editar") {
        peer.setMnemonic('E')
        val itemAumentar = new MenuItem("Aumentar Fonte") {
          action = Action("Aumentar Fonte") {
            editor.font = Font.createFont(Font.TRUETYPE_FONT, is.openStream()).deriveFont(Font.BOLD, editor.font.getSize() + 2);
            //editor.font = new java.awt.Font(fontname, Font.BOLD, editor.font.getSize() + 2)
            numeracao.font = editor.font
          }
          iconTextGap = 20
          peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, ActionEvent.CTRL_MASK))
        }
        val itemDiminuir = new MenuItem("Diminuir Fonte") {
          action = Action("Diminuir Fonte") {
            if (editor.font.getSize() > 2) {
              editor.font = Font.createFont(Font.TRUETYPE_FONT, is.openStream()).deriveFont(Font.BOLD, editor.font.getSize() - 2);
              // editor.font = new java.awt.Font(fontname, Font.BOLD, editor.font.getSize() - 2)
              numeracao.font = editor.font
            }
          }
          iconTextGap = 20
          peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK))
        }

        val itemDesfazer = new MenuItem("Desfazer digitação") {
          action = Action("Desfazer digitação") {
            if (!undo.isEmpty) {
              val ultimo = undo.pop
              editor.text = ultimo._1
              editor.caret.position = ultimo._2
              texto = ultimo._1
            }
            colorir()
          }
          iconTextGap = 20
          peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK))
        }

        val itemRecortar = new MenuItem("Recortar") {
          action = Action("Recortar") {
            editor.cut()
          }
          iconTextGap = 20
          peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK))
        }

        val itemCopiar = new MenuItem("Copiar") {
          action = Action("Copiar") {
            editor.copy()
          }
          iconTextGap = 20
          peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK))
        }

        val itemColar = new MenuItem("Colar") {
          action = Action("Colar") {
            editor.paste()
          }
          iconTextGap = 20
          peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK))
        }

        val itemSelecionarTudo = new MenuItem("Selecionar tudo") {
          action = Action("Selecionar tudo") {
            editor.selectAll
          }
          iconTextGap = 20
          peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK))
        }

        val itemFormatar = new MenuItem("Formatar código") {
          action = Action("Formatar") {
            editor.text = ParserEditor.pretty(editor.text)
            texto = ""
            atualizar
          }
          iconTextGap = 20
          peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK))
        }

        contents += itemDesfazer
        contents += new Separator
        contents += itemRecortar
        contents += itemCopiar
        contents += itemColar
        contents += new Separator
        contents += itemSelecionarTudo
        contents += new Separator
        contents += itemFormatar
        contents += itemAumentar
        contents += itemDiminuir
      }
      val menuArquivo = new Menu("Arquivo") {
        peer.setMnemonic('A')
        val itemNovo = new MenuItem("Novo") {
          action = Action("Novo") {
            if (!modificado) {
              editor.text = ""
              undo.clear()
              arq = None
            }
            else {
              salvar {
                itemSalvar.action.apply()
                if (!modificado) {
                  editor.text = ""
                  undo.clear()
                  atualizar()
                  arq = None
                }
              } {
                editor.text = ""
                undo.clear()
                atualizar()
                arq = None
              }
            }
            title = s"${arq.getOrElse("Sem nome").split("/").last} - Potigol"

          }
          peer.setMnemonic('N')
          iconTextGap = 20
          peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK))
        }

        val itemAbrir = new MenuItem("Abrir") {
          action = Action("Abrir") {
            if (!modificado) {
              val res = arquivo.showOpenDialog(editor)
              res match {
                case FileChooser.Result.Approve =>
                  arq = Some(arquivo.selectedFile.getPath)
                  editor.text = scala.io.Source.fromFile(arq.get, "utf-8").getLines.mkString("\n")
                  atualizar()
                  modificado = false
              }
            }
            else {
              salvar {
                itemSalvar.action.apply()
                if (!modificado) {
                  val res = arquivo.showOpenDialog(editor)
                  res match {
                    case FileChooser.Result.Approve =>
                      arq = Some(arquivo.selectedFile.getPath)
                      editor.text = scala.io.Source.fromFile(arq.get, "utf-8").getLines.mkString("\n")
                      atualizar()
                      modificado = false
                  }
                }
              } {
                val res = arquivo.showOpenDialog(editor)
                res match {
                  case FileChooser.Result.Approve =>
                    arq = Some(arquivo.selectedFile.getPath)
                    println(arq)
                    editor.text = scala.io.Source.fromFile(arq.get, "utf-8").getLines.mkString("\n")
                    atualizar()
                    modificado = false
                }
              }
            }
            title = s"${arq.getOrElse("Sem nome").split("/").last} - Potigol"
          }
          this.enabled = true
          iconTextGap = 20
          peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK))
        }

        val itemSalvar = new MenuItem("Salvar") {
          action = Action("Salvar") {
            if (arq != None) {
              val writer = new PrintWriter(arq.get, "UTF-8")
              writer.print(editor.text)
              writer.close()
              modificado = false
            }
            else {
              itemSalvarComo.action.apply()
            }
            title = s"${arq.getOrElse("Sem nome")} - Potigol"

          }
          iconTextGap = 20
          this.enabled = true
          peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK))
        }

        val itemSalvarComo = new MenuItem("SalvarComo") {
          action = Action("Salvar Como ...") {
            val res = arquivo.showSaveDialog(editor)
            res match {
              case FileChooser.Result.Approve =>
                arq = Some(arquivo.selectedFile.getPath)
                if (!arq.get.endsWith(".poti")) arq = Some(arq.get + ".poti")
                val writer = new PrintWriter(arq.get, "UTF-8")
                writer.print(editor.text)
                writer.close()
                modificado = false
                title = s"${arq.getOrElse("Sem nome")} - Potigol"
            }
          }
          iconTextGap = 20
          this.enabled = true
          peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK))
        }
        val itemImprimir = new MenuItem("Imprimir") {
          action = Action("Imprimir") {
            val writer = new PrintWriter("print.html", "UTF-8")
            writer.print(ParserEditor.print(editor.text))
            writer.close()
            Css.save
            import java.awt.Desktop
            import java.net.URI
            Desktop.getDesktop.browse(new URI("print.html"))

          }
          iconTextGap = 20
          peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK))
        }
        import scala.sys.process._
        val executar = new MenuItem("Executar") {
          iconTextGap = 20
          action = Action("Executar") {
            val rt = Runtime.getRuntime()
            if (!modificado) {
              if (System.getProperty("os.name").startsWith("Windows")) {
                rt.exec("cmd.exe /T:1F /c start exec.bat " + arq.get)
              }
              else { rt.exec("./exec.sh " + arq.get) }
            }
            else {
              salvar {
                itemSalvar.action.apply()
              } {
                if (System.getProperty("os.name").startsWith("Windows")) {
                  rt.exec("cmd.exe /T:1F /c start exec.bat " + arq.get)
                }
                else { rt.exec("./exec.sh " + arq.get) }
              }
            }
          }
          iconTextGap = 20
          this.enabled = true
          peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK))
        }

        val itemSair = new MenuItem("Sair") {
          iconTextGap = 20
          action = Action("Sair") {
            if (!modificado) {
              sys.exit(0)
            }
            else {
              salvar {
                itemSalvar.action.apply()
              } {
                sys.exit(0)
              }
            }
          }
        }

        contents += itemAbrir
        contents += itemNovo
        contents += new Separator
        contents += itemSalvar
        contents += itemSalvarComo
        contents += itemImprimir
        contents += new Separator
        contents += executar
        contents += new Separator
        contents += itemSair
      }

      val menuAjuda = new Menu("Ajuda") {
        peer.setMnemonic('J')

        val itemConteudo = new MenuItem("Conteúdo de Ajuda") {
          iconTextGap = 20
          this.enabled = true
          action = Action("Ajuda") {
            import java.awt.Desktop
            import java.net.URI
            Desktop.getDesktop.browse(new URI("http://potigol.github.io"))
          }
        }
        val itemSobre = new MenuItem("Sobre") {
          iconTextGap = 20
          action = Action("Sobre") {
            Sobre.visible = true
          }
        }
        contents += itemConteudo
        contents += new Separator
        contents += itemSobre
      }

      contents += menuArquivo
      contents += menuEditar
      contents += menuAjuda
    }

    listenTo(editor.keys)
    atualizar()

    reactions += {
      case KeyTyped(_, c @ ('}' | ']' | ')'), _, _) => {
        modificado = true
        val p = editor.caret.position
        if (editor.text.drop(p).headOption == Some(c))
          editor.text = editor.text.take(p) + editor.text.drop(p + 1)
        editor.caret.position = p
        atualizar
      }

      case KeyTyped(_, a @ ('"' | '{' | '(' | '['), _, _) => {
        modificado = true
        val p = editor.caret.position
        if (a == '"' && editor.text.drop(p).headOption == Some('"')) {

          editor.text = editor.text.take(p) + editor.text.drop(p + 1)
          editor.caret.position = p
        }
        else {
          val c = a match { case '"' => '"' case '{' => '}' case '(' => ')' case '[' => ']' }
          editor.text = editor.text.take(p) + c + editor.text.drop(p)
          editor.caret.position = p
        }
        atualizar
      }

      case KeyReleased(_, Key.Enter, _, _) =>
        {
          val p = editor.caret.position - 1
          val p1 = editor.caret.dot
          val fim = editor.text.take(p)
          val linha = fim.drop(fim.lastIndexOf('\n') + 1)
          val espacos = linha.prefixLength(_ == ' ')
          val loc = contents(0).asInstanceOf[ScrollPane].verticalScrollBar.location;
          if (fim.endsWith("senão") ||
            fim.endsWith("senao")) {
            editor.text = editor.text.take(p) + "\n  " + " " * espacos + editor.text.drop(p + 1)
            editor.caret.position = p + 3 + espacos
          }
          else if (fim.endsWith("faça") ||
            linha.trim.startsWith("tipo") ||
            fim.endsWith("faca") ||
            fim.endsWith("então") ||
            fim.endsWith("entao")) {
            editor.text = editor.text.take(p) + "\n  " + " " * espacos + "\n" + " " * espacos + "fim" + editor.text.drop(p + 1)
            editor.caret.position = p + 3 + espacos
          }
          else if (linha.trim.startsWith("escolha")) {
            editor.text = editor.text.take(p) + "\n  " + " " * espacos + "caso => \n" + " " * espacos + "fim" + editor.text.drop(p + 1)
            editor.caret.position = p + 8 + espacos
          }
          else // String multilinha
          if (editor.text.drop(p + 1).startsWith("\"") ||
            editor.text.drop(p + 1).startsWith("|")) {
            val c = editor.text.drop(p + 1).head
            val u = linha.lastIndexOf("\"") + linha.lastIndexOf("|")
            editor.text = editor.text.take(p) + "\n" + " " * (u + 1 + (if (c == '"') 0 else 1)) + "|" + editor.text.drop(p + 1)
            editor.caret.position = p + 3 + u
          }
          else {
            linha.prefixLength(_ == ' ')

            editor.text = editor.text.take(p) + "\n" + " " * espacos + editor.text.drop(p + 1)
            editor.caret.position = p + 1 + espacos
          }
          robot.mouseWheel(-100);

          atualizar()
          robot.mouseWheel(-100);
          robot.mouseWheel(-100);
        }

      case KeyReleased(_, _, _, _) => {
        atualizar()
      }
    }

    def salvar(sim: => Unit)(nao: => Unit) {
      val resp = Dialog.showOptions(
        editor,
        s"Deseja salvar as alterações em ${arq.getOrElse("Sem nome")}?",
        "Potigol",
        Dialog.Options.YesNoCancel,
        Dialog.Message.Warning, null, Seq("Sim", "Não", "Cancelar"), 2)
      resp match {
        case Dialog.Result.Yes    => sim
        case Dialog.Result.No     => nao
        case Dialog.Result.Cancel =>
      }
    }

    def atualizar() {
      if (editor.text != texto) {
        //    modificado = true
        texto = editor.text
        val y = editor.caret.position

        contents(0) match {
          case p: scala.swing.ScrollPane =>
            val a = p.verticalScrollBar.value
            colorir()
            //            p.verticalScrollBar.value = a
            editor.caret.position = y
          //            editor.caret.position = y-1
          //                     editor.caret.position = y+1
          /*  p.peer.updateUI()
            p.peer.repaint()
            p.verticalScrollBar.value = a
            editor.caret.position = y
            editor.caret.dot = y
            editor.repaint()
            p.peer.setLocation(1, 1)
            p.peer.getVerticalScrollBar.setValue(10)
            println(p.verticalScrollBar.value)*/
        }

        if (undo.isEmpty || texto != undo.top._1) {
          undo.push((texto, y))
        }
      }
    }

    private def colorir() {
      val linhas = editor.text.filter(_ == '\n').length + 1
      if (linhas != numeracao.text.filter(_ == '\n').length + 1) {
        numeracao.text = (for (i <- 1 to linhas) yield f"$i%3d").toList.mkString("\n")
      }

      val elementos = ParserEditor.parse(editor.text)
      val styledDocument = editor.styledDocument
      styledDocument.setCharacterAttributes(0, 10000, config.cinza, true)
      for (elem <- elementos) {
        val a = elem.getStartIndex
        val b = elem.getStopIndex - a + 1
        import br.edu.ifrn.potigol.parser.potigolParser._
        val s = elem.getType match {
          case INT | FLOAT | BOOLEANO | CHAR => config.azul
          case STRING | BS | MS | ES => config.amarelo
          case COMMENT => config.vermelho
          case ID if List("verdadeiro", "falso").contains(elem.getText) => config.azul
          case ID if tipos.contains(elem.getText) => config.cyan
          case ID if a > 0 && editor.text.charAt(a - 1) == '.' && metodos.contains(elem.getText) => config.cyan
          case ID if funcoes.contains(elem.getText) => config.cyan
          case ID => config.bege
          case _ if elem.getText == "isto" => config.azul
          case _ => config.vermelho
        }
        styledDocument.setCharacterAttributes(a, b, s, true)
      }
    }

    if (arq.isDefined) {
      editor.text = scala.io.Source.fromFile(arq.get, "utf-8").getLines.mkString("\n")
      // atualizar()
    }
  }

  object config {
    import javax.swing.text.StyleConstants._
    case class Cor(r: Int, g: Int, b: Int) extends Color(r, g, b)
    case class Atributos(cor: Color, italico: Boolean = false) extends SimpleAttributeSet {
      setFontFamily(this, "DejaVu Sans Mono")
      setForeground(this, cor)
      setBold(this, false)
      setItalic(this, italico)
    }
    val amarelo = Atributos(Cor(230, 219, 116))
    val vermelho = Atributos(Cor(249, 38, 114))
    val cinza = Atributos(Cor(118, 113, 94))
    val azul = Atributos(Cor(174, 129, 255))
    val bege = Atributos(Cor(248, 248, 242))
    val cyan = Atributos(Cor(102, 217, 206), italico = true)
  }
}

object Sobre extends Frame {
  visible = false
  peer.setDefaultCloseOperation(1)
  resizable = false
  preferredSize.setSize(300, 200)
  this.location = new Point(400, 200)
  contents = new TextPane() {
    border = BorderFactory.createCompoundBorder(
      border,
      BorderFactory.createEmptyBorder(0, 20, 20, 20));
    contentType = "text/html"
    text = """<html><body><h1>Editor Potigol</h1>
             |<p>
             |Versão: 0.9.16<br/>
             |21/03/2019
             |<p>
             |(c) Copyright Leonardo Lucena, 2015-2019.<p>
             |Visite: <a href="http://potigol.github.io">http://potigol.github.io</a>
             |</body></html>""".stripMargin('|')
    font = Font.createFont(Font.TRUETYPE_FONT, Editor.is.openStream()).deriveFont(Font.BOLD, 14);
    //new java.awt.Font("DejaVu Sans Mono", Font.BOLD, 14)
  }
}