package br.edu.ifrn.potigol.editor

/**
 * @author Lucena
 */
object Css {
  def css =
    """@media print {
  button {
    display: none;
  }
}

pre {
  font-size: 12pt;
  font-family: "DejaVu Sans Mono","Consolas";
}

.numero {
  color:blue;
}

.comentario {
  color: green;
  font-style: italic;
}

.atribuicao {
  color: black;
}

.var {
  color:red;
}

.operador {
  color:black;
}

.chave, .fim{
  color: purple;
  font-weight: bolder;
}

.texto{
  color:orange;
}

.parametro{
  color:sienna;
}

.classe{
  color:sienna;
}"""

  def save {
    val writer = new java.io.PrintWriter("potigol.css", "UTF-8")
    writer.print(css)
    writer.close()
  }
}