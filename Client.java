

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;


public class Client implements Runnable 
{

	enum Dir 
	{
		NORD("N"),
		SUD("S"),
		EST("E"),
		OUEST("O"),
		JUMP_NORD("JN"),
		JUMP_SUD("JS"),
		JUMP_EST("JE"),
		JUMP_OUEST("JO");
		String code;
		Dir(String dir) 
		{
			code = dir;
		}
	}
	
	private String	ipServer;
	private long	teamId;
	private String	secret;
	private int	 	socketNumber;
	private long	gameId;
	private 

	Random rand = new Random();

	

	public Client(String ipServer, long teamId, String secret, int socketNumber, long gameId) 
	{
		this.ipServer 		= ipServer;
		this.teamId 		= teamId;
		this.secret 		= secret;
		this.socketNumber 	= socketNumber;
		this.gameId 		= gameId;
	}

	public void run() 
	{
		System.out.println("Demarrage du client");
		Socket 			socket = null;
		String 			message;
		BufferedReader 	in;
		PrintWriter 	out;
		try 
		{
			socket = new Socket(ipServer, socketNumber);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			System.out.println("Envoi de l'incription");
			out.println(secret + "%%inscription::" + gameId + ";" + teamId);
			out.flush();

			do 
			{
				message = in.readLine();
				System.out.println("Message recu : " + message);
				if (message != null) 
				{
					if (message.equalsIgnoreCase("Inscription OK")) 
					{
						System.out.println("Je me suis bien inscrit a la battle");
					}
					else if (message.startsWith("worldstate::")) 
					{
						String[] components = message.substring("worldstate::".length()).split(";", -1);
						int round = Integer.parseInt(components[0]);
						// On joue
						String action = secret + "%%action::" + teamId + ";" + gameId + ";" + round + ";" + computeDirection().code;
						System.out.println(action);
						out.println(action);
						out.flush();
					}
					else if (message.equalsIgnoreCase("Inscription KO")) 
					{
						System.out.println("inscription KO");
					}
					else if (message.equalsIgnoreCase("game over")) 
					{
						System.out.println("game over");
						System.exit(0);
					}
					else if (message.equalsIgnoreCase("action OK")) 
					{
						System.out.println("Action bien prise en compte");
					}
				}
				System.out.println("Pour voir la partie : http://" + ipServer + ":8080/?gameId=" + gameId);
			}
			while (message != null);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			if (socket != null) 
			{
				try 
				{
					socket.close();
				}
				catch (IOException e) 
				{
					// Tant pis
				}
			}

		}
	}

	public Dir computeDirection() 
	{
		//ici on va bosser (le reste on peut laisser tel quel, tout ce qui nous importe, c'est l'algorithmie)

		//On doit déja récupérer les positions des adversaires
		getEnemiesPositions();
		//Récupérer les positions des logos

		//Déterminer en fonction de notre option de jeu actuelle (défensif, agressif, passif) quelle action faire
			//Déterminer si depuis le choix de la stratégie actuelle, un changement a eu lieu, qui nécessite un changement de stratégie (perte de jeton pour notre cible, score qui évolue, ...
				//Si changement majeur, déterminer la nouvelle stratégie
			//Calculer le coefficient de chaque case
			//Se retourner la position de la case atteignable au plus haut coefficient
		return Dir.values()[rand.nextInt(Dir.values().length)];


		/*
		
		Critères de calcul de coefficient:
			- Position des ennemis (-2 sur les cases adjacentes à notre position finale)
			- Position des ennemis avec un jeton (3 points garantis si jump disponible)
			- Position des jetons (+1 pour ramasser, +30 pour rapporter au cadi)
			- Position du caddy par rapport à la notre si notre jeton est la
			- Position des caddys des adversaires qui portent un jeton

		Type d'action envisageable:
			- Retour à la base avec jeton
			- Attaque de l'adersaire avec jeton
			- Attaque de l'adversaire pour points
			- Attaque de l'adversaire à la base
		
		Situations (uniquement si on est capable de connaitre les scores adverses):
			- On est en tête
			- On est dans le milieu
			- On est à la traine
		*:
	}

}
