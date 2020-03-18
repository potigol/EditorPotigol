package br.edu.ifrn.potigol.editor;
import java.util.Scanner;

public class TesteConsole {
	public static void main(String... args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("ok");
		String s = sc.next();
		sc.close();
		System.out.println(s);
		System.out.println("fim");
	}
}
