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

.chave, .fim, .operador {
  color: purple;
  font-weight: bolder;
}

.ponto {
  color: darkblue;
  font-style: italic;
}

.texto{
  color:darkgoldenrod;
}

.parametro{
  color:sienna;
}

.classe{
  color:sienna;
}"""

  def save {
    val writer = new java.io.PrintWriter("c:\\temp\\potigol.css", "UTF-8")
    writer.print(css)
    writer.close()
  }
}