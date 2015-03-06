import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sf.jeppers.grid.CellSpan;
import net.sf.jeppers.grid.DefaultCellStyle;
import net.sf.jeppers.grid.DefaultGridModel;
import net.sf.jeppers.grid.DefaultSelectionModel;
import net.sf.jeppers.grid.DefaultSpanModel;
import net.sf.jeppers.grid.DefaultStyleModel;
import net.sf.jeppers.grid.GenericCellEditor;
import net.sf.jeppers.grid.JGrid;
import net.sf.jeppers.grid.JGridHeader;
import net.sf.jeppers.grid.JSpanScrollGrid;
import net.sf.jeppers.grid.SelectionModel;

public class MainSpanGrid {

	private static enum Choix {
		Vide, Croix, Rond
	};

	private static class Sillon {
		String noSillon;

		String horaire;

		public Sillon(String noSillon, String horaire) {
			super();
			this.noSillon = noSillon;
			this.horaire = horaire;
		}
	}

	private static class Passage {

		public enum Type {
			Depart, Arrive, Passage
		};

		String libelle;

		Type type;

		public Passage(String libelle, Type type) {
			super();
			this.libelle = libelle;
			this.type = type;
		}
	}

	private static class MyTopGrid extends JGridHeader {

		private static final long serialVersionUID = -3462182996763033926L;

		private DefaultGridModel gridModel;

		public MyTopGrid(JGrid viewport) {
			super(viewport, SwingConstants.HORIZONTAL, 2, 0);

			gridModel = new DefaultGridModel(2, 0);
			this.setGridModel(gridModel);

			this.setShowGrid(false);

			this.setSelectionModel(new NoSelectionModel());
		}

		public void setSillons(Sillon[] sillons) {
			gridModel.removeColumns(0, gridModel.getColumnCount());
			gridModel.insertColumns(0, sillons.length);

			DefaultSpanModel spanModel = new DefaultSpanModel();

			this.setSpanModel(spanModel);

			DefaultStyleModel styleModel = new DefaultStyleModel();
			this.setStyleModel(styleModel);

			for (int i = 0; i < gridModel.getColumnCount(); i++) {
				for (int j = 0; j < gridModel.getRowCount(); j++) {
					gridModel.setCellLock(true, j, i);
				}
			}

			initSillons(sillons, gridModel, spanModel, styleModel);
		}

		private void initSillons(Sillon[] sillons, DefaultGridModel gridModel,
				DefaultSpanModel spanModel, DefaultStyleModel styleModel) {
			DefaultCellStyle sCellStyle = new DefaultCellStyle();
			sCellStyle
					.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			sCellStyle.setBackgroundColor(new Color(0xE6CFD6));
			sCellStyle.setForegroundColor(Color.BLACK);
			sCellStyle.setHorizontalAlignment(SwingConstants.CENTER);
			sCellStyle.setVerticalAlignment(SwingConstants.CENTER);
			sCellStyle.setFont(new Font("Arial", Font.PLAIN, 12));

			for (int i = 0; i < sillons.length; i++) {
				int col = i;
				Sillon s = sillons[i];

				gridModel.setValueAt(s.noSillon, 0, col);
				styleModel.setCellStyle(sCellStyle, 0, col);
				gridModel.setValueAt(s.horaire, 1, col);
				styleModel.setCellStyle(sCellStyle, 1, col);
			}
		}
	}

	private static class MyLeftGrid extends JGridHeader {

		private static final long serialVersionUID = 3993428778288525776L;

		private DefaultGridModel gridModel;

		public MyLeftGrid(JGrid viewport) {
			super(viewport, SwingConstants.VERTICAL, 30, 2);

			gridModel = new DefaultGridModel(30, 2);
			this.setGridModel(gridModel);

			this.setColumnWidth(0, 200);

			this.setShowGrid(false);

			this.setSelectionModel(new NoSelectionModel());
		}

		public void setSillons(Sillon[] sillons) {
			DefaultSpanModel spanModel = new DefaultSpanModel();
			this.setSpanModel(spanModel);

			DefaultStyleModel styleModel = new DefaultStyleModel();
			this.setStyleModel(styleModel);

			for (int i = 0; i < gridModel.getColumnCount(); i++) {
				for (int j = 0; j < gridModel.getRowCount(); j++) {
					gridModel.setCellLock(true, j, i);
				}
			}

			initJours(gridModel, spanModel, styleModel);
			initPassages(gridModel, spanModel, styleModel);
		}

		private void initPassages(DefaultGridModel gridModel,
				DefaultSpanModel spanModel, DefaultStyleModel styleModel) {
			DefaultCellStyle reCellStyle = new DefaultCellStyle();
			reCellStyle.setBorder(BorderFactory
					.createLineBorder(Color.BLACK, 1));
			reCellStyle.setBackgroundColor(new Color(0xCED0BD));
			reCellStyle.setForegroundColor(Color.BLACK);
			reCellStyle.setHorizontalAlignment(SwingConstants.CENTER);
			reCellStyle.setVerticalAlignment(SwingConstants.CENTER);
			reCellStyle.setFont(new Font("Arial", Font.PLAIN, 12));

			gridModel.setValueAt("RELATIONS / TRAIN", 8, 0);
			styleModel.setCellStyle(reCellStyle, 8, 0);
			styleModel.setCellStyle(reCellStyle, 8, 1);

			DefaultCellStyle pCellStyle1 = new DefaultCellStyle();
			pCellStyle1.setBorder(BorderFactory
					.createLineBorder(Color.BLACK, 1));
			pCellStyle1.setBackgroundColor(new Color(0xF6F7E6));
			pCellStyle1.setForegroundColor(Color.BLACK);
			pCellStyle1.setHorizontalAlignment(SwingConstants.LEFT);
			pCellStyle1.setVerticalAlignment(SwingConstants.CENTER);
			pCellStyle1.setFont(new Font("Arial", Font.BOLD, 12));

			DefaultCellStyle fCellStyle1 = new DefaultCellStyle(pCellStyle1);
			fCellStyle1.setHorizontalAlignment(SwingConstants.RIGHT);

			DefaultCellStyle tCellStyle1 = new DefaultCellStyle(pCellStyle1);
			tCellStyle1.setHorizontalAlignment(SwingConstants.CENTER);

			DefaultCellStyle pCellStyle2 = new DefaultCellStyle(pCellStyle1);
			pCellStyle2.setBackgroundColor(new Color(0xFFFFFD));

			DefaultCellStyle fCellStyle2 = new DefaultCellStyle(pCellStyle2);
			fCellStyle2.setHorizontalAlignment(SwingConstants.RIGHT);

			DefaultCellStyle tCellStyle2 = new DefaultCellStyle(pCellStyle2);
			tCellStyle2.setHorizontalAlignment(SwingConstants.CENTER);

			Passage[] ps = {
					new Passage("PARIS NORD", Passage.Type.Depart),
					new Passage("Wannehain (frontière)", Passage.Type.Passage),
					new Passage("BRUXELLES MIDI", Passage.Type.Arrive),
					new Passage("BRUXELLES MIDI", Passage.Type.Depart),
					new Passage("LIEGE GUILLEMINS", Passage.Type.Arrive),
					new Passage("LIEGE GUILLEMINS", Passage.Type.Depart),
					new Passage("Aachen (frontière)", Passage.Type.Passage),
					new Passage("Aachen HBF", Passage.Type.Arrive),
					new Passage("Aachen HBF", Passage.Type.Depart),
					new Passage("KOLN HBF", Passage.Type.Arrive),
					new Passage("ANTWERPEN C (niveau -2)", Passage.Type.Arrive),
					new Passage("ANTWERPEN C (niveau -2)", Passage.Type.Depart),
					new Passage("Roosendaal (frontière)", Passage.Type.Passage),
					new Passage("Hazeldonk (frontière)", Passage.Type.Passage),
					new Passage("Rotterdam CS", Passage.Type.Arrive),
					new Passage("Rotterdam CS", Passage.Type.Depart),
					new Passage("Den Haag HS", Passage.Type.Arrive),
					new Passage("Den Haag HS", Passage.Type.Depart),
					new Passage("Schiphol (Aéroport)", Passage.Type.Arrive),
					new Passage("Schiphol (Aéroport)", Passage.Type.Depart),
					new Passage("AMSTERDAM CS", Passage.Type.Arrive) };

			for (int i = 0; i < 21; i++) {
				Passage p = ps[i];

				int row = i + 9;

				gridModel.setValueAt(p.libelle, row, 0);

				String type = "-";
				switch (p.type) {
				case Depart:
					type = "Dep.";
					break;
				case Arrive:
					type = "Arr.";
					break;
				}

				gridModel.setValueAt(type, row, 1);
				if (i % 2 == 0) {
					styleModel.setCellStyle(
							p.type == Passage.Type.Passage ? fCellStyle1
									: pCellStyle1, row, 0);
					styleModel.setCellStyle(tCellStyle1, row, 1);
				} else {
					styleModel.setCellStyle(
							p.type == Passage.Type.Passage ? fCellStyle2
									: pCellStyle2, row, 0);
					styleModel.setCellStyle(tCellStyle2, row, 1);
				}
			}
		}

		private void initJours(DefaultGridModel gridModel,
				DefaultSpanModel spanModel, DefaultStyleModel styleModel) {
			String[] jours = { "Lundi", "Mardi", "Mercredi", "Jeudi",
					"Vendredi", "Samedi", "Dimanche" };

			DefaultCellStyle joursCellStyle = new DefaultCellStyle();
			joursCellStyle.setBorder(BorderFactory.createLineBorder(
					Color.BLACK, 1));
			joursCellStyle.setBackgroundColor(new Color(0xF7F7E5));
			joursCellStyle.setForegroundColor(Color.BLACK);
			joursCellStyle.setHorizontalAlignment(SwingConstants.CENTER);
			joursCellStyle.setVerticalAlignment(SwingConstants.CENTER);
			joursCellStyle.setFont(new Font("Arial", Font.PLAIN, 12));

			// Jour de la semaine
			for (int i = 0; i < 7; i++) {
				spanModel.addSpan(new CellSpan(i, 0, 1, 2));
				gridModel.setValueAt(jours[i], i, 0);
				styleModel.setCellStyle(joursCellStyle, i, 0);
			}
		}
	}

	private static class MyCenterGrid extends JGrid {

		private static final long serialVersionUID = -2473703963415661513L;

		private DefaultGridModel gridModel;

		public MyCenterGrid() {
			super(30, 0);

			gridModel = new DefaultGridModel(30, 0);
			this.setGridModel(gridModel);

			this.setShowGrid(false);
			this.setRowHeight(7, 5);

			this.setSelectionMode(SelectionModel.SelectionMode.Single);
		}

		public void setSillons(Sillon[] sillons) {
			gridModel.removeColumns(0, gridModel.getColumnCount());
			gridModel.insertColumns(0, sillons.length);

			DefaultSpanModel spanModel = new DefaultSpanModel();
			this.setSpanModel(spanModel);

			DefaultStyleModel styleModel = new DefaultStyleModel();
			this.setStyleModel(styleModel);

			for (int i = 0; i < gridModel.getColumnCount(); i++) {
				for (int j = 0; j < gridModel.getRowCount(); j++) {
					gridModel.setCellLock(true, j, i);
				}
			}

			initJours(sillons, gridModel, spanModel, styleModel);
			initPassage(sillons, gridModel, spanModel, styleModel);
		}

		private void initPassage(Sillon[] sillons, DefaultGridModel gridModel,
				DefaultSpanModel spanModel, DefaultStyleModel styleModel) {
			DefaultCellStyle reCellStyle = new DefaultCellStyle();
			reCellStyle.setBorder(BorderFactory
					.createLineBorder(Color.BLACK, 1));
			reCellStyle.setBackgroundColor(new Color(0xCED0BD));
			reCellStyle.setForegroundColor(Color.BLACK);
			reCellStyle.setHorizontalAlignment(SwingConstants.CENTER);
			reCellStyle.setVerticalAlignment(SwingConstants.CENTER);
			reCellStyle.setFont(new Font("Arial", Font.PLAIN, 12));

			for (int i = 0; i < sillons.length; i++) {
				Sillon s = sillons[i];

				gridModel.setValueAt(s.noSillon, 8, i);
				styleModel.setCellStyle(reCellStyle, 8, i);
			}

			DefaultCellStyle hCellStyle1 = new DefaultCellStyle();
			hCellStyle1.setBorder(BorderFactory
					.createLineBorder(Color.BLACK, 1));
			hCellStyle1.setBackgroundColor(new Color(0xE6CFD6));
			hCellStyle1.setForegroundColor(Color.BLACK);
			hCellStyle1.setHorizontalAlignment(SwingConstants.CENTER);
			hCellStyle1.setVerticalAlignment(SwingConstants.CENTER);
			hCellStyle1.setFont(new Font("Arial", Font.BOLD, 12));

			DefaultCellStyle hCellStyle2 = new DefaultCellStyle(hCellStyle1);
			hCellStyle2.setBackgroundColor(new Color(0xFFFFFA));

			Random rand = new Random(10);

			for (int i = 0; i < sillons.length; i++) {
				Sillon s = sillons[i];

				for (int j = 0; j < 21; j++) {
					int row = j + 9;

					gridModel.setValueAt(rand.nextInt(5) == 0 ? s.horaire : "",
							row, i);
					styleModel.setCellStyle(j % 2 == 0 ? hCellStyle2
							: hCellStyle1, row, i);
				}
			}
		}

		private void initJours(Sillon[] sillons, DefaultGridModel gridModel,
				DefaultSpanModel spanModel, DefaultStyleModel styleModel) {
			DefaultCellStyle cCellStyle = new DefaultCellStyle();
			cCellStyle
					.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			cCellStyle.setBackgroundColor(new Color(0xFFF5F8));
			cCellStyle.setForegroundColor(Color.BLACK);
			cCellStyle.setHorizontalAlignment(SwingConstants.CENTER);
			cCellStyle.setVerticalAlignment(SwingConstants.CENTER);
			cCellStyle.setFont(new Font("Arial", Font.PLAIN, 12));
			cCellStyle.setFormat(new Format() {
				public StringBuffer format(Object obj, StringBuffer toAppendTo,
						FieldPosition pos) {
					String res = "";
					if (obj instanceof Choix) {
						switch ((Choix) obj) {
						case Croix:
							res = "X";
							break;
						case Rond:
							res = "O";
							break;
						case Vide:
							res = "";
							break;
						}
					}
					return toAppendTo.append(res);
				}

				public Object parseObject(String source, ParsePosition pos) {
					return null;
				}
			});

			JComboBox comboBox = new JComboBox(Choix.values());
			comboBox.setRenderer(new ChoixCellRenderer());
			GenericCellEditor cellEditor = new GenericCellEditor(comboBox);
			styleModel.setDefaultEditor(Choix.class, cellEditor);

			for (int i = 0; i < sillons.length; i++) {
				for (int j = 0; j < 7; j++) {
					gridModel.setCellLock(false, j, i);
					styleModel.setCellStyle(cCellStyle, j, i);

					gridModel.setValueAt(Choix.Vide, j, i);
					gridModel.setClassAt(Choix.class, j, i);
				}
			}
		}
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Sillon[] sillons = { new Sillon("9305", "06:01"),
				new Sillon("9307", "06:25"), new Sillon("9707", "06:25"),
				new Sillon("9407", "06:25"), new Sillon("9309", "07:01"),
				new Sillon("9383", "07:19"), new Sillon("9311", "07:25"),
				new Sillon("9311", "07:25"), new Sillon("9411", "07:25"),
				new Sillon("9313", "08:01"), new Sillon("9313", "08:01"),
				new Sillon("9313", "08:01"), new Sillon("9313", "08:01"),
				new Sillon("9313", "08:01") };

		MyCenterGrid grid = new MyCenterGrid();

		MyTopGrid topGrid = new MyTopGrid(grid);
		topGrid.setColumnModel(grid.getColumnModel());

		MyLeftGrid leftGrid = new MyLeftGrid(grid);
		leftGrid.setRowModel(grid.getRowModel());

		grid.setSillons(sillons);
		topGrid.setSillons(sillons);
		leftGrid.setSillons(sillons);

		JSpanScrollGrid scrollPane = new JSpanScrollGrid(grid);
		scrollPane.setRowHeader(leftGrid);
		scrollPane.setColumnHeader(topGrid);

		scrollPane.getSpanRowHeader().addSpan(8, 22);
		scrollPane.getSpanRowHeader().addSpan(9, 11);
		scrollPane.getSpanRowHeader().addSpan(11, 5);
		scrollPane.getSpanRowHeader().addSpan(16, 3);
		scrollPane.getSpanRowHeader().addSpan(20, 3);

		scrollPane.getSpanColumnHeader().addSpan(0, 10);
		scrollPane.getSpanColumnHeader().addSpan(1, 5);
		scrollPane.getSpanColumnHeader().addSpan(11, 3);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

		frame.pack();
		frame.setSize(640, 480);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}

	private static class ChoixCellRenderer extends JLabel implements
			ListCellRenderer {

		private static final long serialVersionUID = 7388584552867300961L;

		public ChoixCellRenderer() {
			super("", JLabel.CENTER); //$NON-NLS-1$
			this.setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			if (value instanceof Choix) {
				Choix p = (Choix) value;
				String res = "";
				switch (p) {
				case Croix:
					res = "X";
					break;
				case Rond:
					res = "O";
					break;
				case Vide:
					res = " ";
					break;
				}
				this.setText(res);
			} else {
				this.setText(" ");
			}

			return this;
		}
	}

	private static class NoSelectionModel extends DefaultSelectionModel {

		public boolean isSelected(int row, int column) {
			return false;
		}
	}
}