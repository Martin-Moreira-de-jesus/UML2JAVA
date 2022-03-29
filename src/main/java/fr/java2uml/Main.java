package fr.java2uml;

import org.json.JSONException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, JSONException, ArgumentException {
		checkArgs(args);
		JavaAnalyser analyser = new JavaAnalyser(args[0], args[1]);
	}

	public static void checkArgs(String[] args) throws ArgumentException {
		switch (args.length) {
			case 0, 1 ->
					throw new ArgumentException("Renseignez le dossier contenant les fichiers java en premier argument et le dossier de sortie en second argument.");
			case 2 -> {}
			default ->
					throw new ArgumentException("Trop d'arguments");
		}
	}




}
