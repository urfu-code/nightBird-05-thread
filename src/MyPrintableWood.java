

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MyPrintableWood extends MyWood {
	private OutputStream out;
	private static String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static boolean[] emptySymbol;
	private Map<String, String> code;

	public MyPrintableWood(char[][] m_wood, OutputStream output) {
		super(m_wood);
		out = output;
		emptySymbol = new boolean[symbols.length()];
		for (int j = 0; j < emptySymbol.length; j++)
			emptySymbol[j] = true;
		code = new HashMap<String, String>();
	}

	public void printWood() throws IOException {
		for (MyWoodman man : m_woodmanList.values()) {
			if (!code.containsKey(man.GetName())) {
				int Idx = 0;
				while (emptySymbol[Idx] != true) Idx++;
				code.put(man.GetName(), symbols.substring(Idx, Idx+1));
				emptySymbol[Idx] = false;
			}
		}		
		char[][] newWood = new char[m_wood.length][m_wood[0].length];
		Vector <MyWoodman> WoodmanListClone = new Vector <MyWoodman>();
		for (MyWoodman woodman : m_woodmanList.values())
			WoodmanListClone.add(woodman);
		for (int k = 0; k < WoodmanListClone.size(); k++) {
			for (int l = k+1; l < WoodmanListClone.size(); l++) {
				MyWoodman man1 = WoodmanListClone.get(k);
				MyWoodman man2 = WoodmanListClone.get(l);
				if (man1.GetLocation().equals(man2.GetLocation())&&	!man1.GetName().equals(man2.GetName())) {
					WoodmanListClone.remove(man2);
					l--;
				}
			}
		}
		for (int j = 0; j < m_wood.length; j++) {
			for (int i = 0; i < m_wood[0].length; i++) {
				boolean player = false;
				for (int k = 0; k < WoodmanListClone.size(); k++) {
					MyWoodman man = WoodmanListClone.get(k);
					Point point = new Point(i,j);
					if (man.GetLocation().equals(point)) {
						newWood[j][i] = code.get(man.GetName()).charAt(0);
						player = true;
					}
				}
				if (player == false) {
					switch (m_wood[j][i]) {
					case '1':
						String map_Symbols = "╬║║║═╔╚╠═╗╝╣═╦╩╬";
						int wall = 0;
						if (i != 0 && m_wood[j][i-1] == '1') wall += 8;
						if (i != m_wood[0].length - 1 && m_wood[j][i+1] == '1') wall += 4;
						if (j != 0 && m_wood[j-1][i] == '1') wall += 2;
						if (j != m_wood.length - 1 && m_wood[j+1][i] == '1') wall += 1;
						newWood[j][i] = map_Symbols.charAt(wall);
						break;
					case '0':
						newWood[j][i] = ' ';
						break;
					case 'K':
						newWood[j][i] = '†';
						break;
					case 'L':
						newWood[j][i] = '♥';
						break;
					}
				}
			}
		}
		// предполагаем, что игроков не более 52 :-)
		String str = "";
		for (int j = 0; j < m_wood.length; j++) {
			for (int i = 0; i < m_wood[0].length; i++) {
				str += newWood[j][i];
			}
			str += "\n";
		}
		out.write((str + "\n♥ - life\n† - dead\n").getBytes());
		for (MyWoodman man: m_woodmanList.values()) {
			out.write((code.get(man.GetName()) + " - " + man.GetName()
					+ " , lifes: " + man.GetLifeCount() + "\n").getBytes());
		}
		out.write("\n".getBytes());
		out.flush();
		for (int Idx = 0; Idx < symbols.length(); Idx++) {
			emptySymbol[Idx] = true;
		}
		for (MyWoodman man : m_woodmanList.values()) {
			char letter = code.get(man.GetName()).charAt(0);
			int Idx = 0;
			while (letter != symbols.charAt(Idx)) Idx++;
			emptySymbol[Idx] = false;
		}
	}

	@Override
	public void createWoodman(String name, Point start, Point finish) throws IOException {
		super.createWoodman(name, start, finish);
		printWood();
	}

	@Override
	public Action move(String name, Direction direction) throws IOException {
		Action action = super.move(name, direction);
		printWood();
		return action;
	}
}