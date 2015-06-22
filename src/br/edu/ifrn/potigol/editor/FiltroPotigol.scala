package br.edu.ifrn.potigol.editor

import javax.swing.filechooser.FileFilter
import java.io.File

object FiltroPotigol extends FileFilter {
  def accept(f: File) = {
    if (f.isDirectory())
      true
    else if (f.toString.endsWith(".poti"))
      true
    else
      false
  }
  def getDescription() = "Arquivos Potigol"
}