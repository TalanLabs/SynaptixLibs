package simpledaystimeline;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import simpledaystimeline.common.Hours;
import simpledaystimeline.common.HoursTransferable;
import simpledaystimeline.common.MyMissionSimpleDaysTask;
import simpledaystimeline.common.MySimpleDaysTimelineDayDatesHeaderRenderer;
import simpledaystimeline.common.MySimpleDaysTimelineDrag;
import simpledaystimeline.common.MySimpleDaysTimelineDrop;
import simpledaystimeline.common.MySimpleDaysTimelineModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.DayDate;
import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineSelectionModel;
import com.synaptix.swing.utils.SwingComponentFactory;

public class MainSimpleDaysTimeline {

	private static int nbDays = 22;

	private static final class LabelTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 6560212511433794019L;

		public int getSourceActions(JComponent c) {
			return COPY;
		}

		protected Transferable createTransferable(JComponent c) {
			return new HoursTransferable(new Hours(-1, "gaby", 5 * 60, 15 * 60));
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.getContentPane().setLayout(new BorderLayout());

				JTable table = new JTable(new Object[][] { { "drag me" } }, new Object[] { "Test1" });
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				table.setDragEnabled(true);
				table.setTransferHandler(new LabelTransferHandler());

				JPopupMenu popupMenu = new JPopupMenu();
				popupMenu.add(new JMenuItem("Gaby"));

				final MySimpleDaysTimelineModel model = new MySimpleDaysTimelineModel();

				final JSimpleDaysTimeline simpleDaysTimeline1 = new JSimpleDaysTimeline(model);
				simpleDaysTimeline1.setDropMode(JSimpleDaysTimeline.DropMode.FILL_GHOST);
				simpleDaysTimeline1.setSimpleDaysTimelineDrop(new MySimpleDaysTimelineDrop());
				simpleDaysTimeline1.setSimpleDaysTimelineDrag(new MySimpleDaysTimelineDrag());
				simpleDaysTimeline1.getSelectionModel().setSelectionMode(SimpleDaysTimelineSelectionModel.Mode.MULTIPLE_SELECTION);
				simpleDaysTimeline1.setHeaderRenderer(new MySimpleDaysTimelineDayDatesHeaderRenderer());
				simpleDaysTimeline1.setShowIntersection(false);
				simpleDaysTimeline1.setMultiLine(true);
				simpleDaysTimeline1.setAutoHeight(true);

				simpleDaysTimeline1.setNbDays(nbDays);
				simpleDaysTimeline1.setCenterPopupMenu(popupMenu);
				simpleDaysTimeline1.setName("1");
				simpleDaysTimeline1.setMinDayWidth(100);
				simpleDaysTimeline1.setMaxDayWidth(3000);
				simpleDaysTimeline1.setDayCycle(7);
				simpleDaysTimeline1.getResourcesHeader().setReorderingAllowed(false);

				simpleDaysTimeline1.getResourcesHeader().setResizingAllowed(false);

				simpleDaysTimeline1.setDefaultResourceHeight(100);

				simpleDaysTimeline1.setDynamicDayDatesHeader(true);

				simpleDaysTimeline1.getDayWidthSlider().setPreferredSize(new Dimension(50, 10));

				FormLayout layout2 = new FormLayout("RIGHT:MAX(40DLU;PREF):NONE,FILL:4DLU:NONE, FILL:10dlu:NONE,FILL:4DLU:NONE", //$NON-NLS-1$
						"CENTER:PREF:GROW"); //$NON-NLS-1$
				PanelBuilder builder2 = new PanelBuilder(layout2);
				CellConstraints cc2 = new CellConstraints();
				builder2.addLabel("Date", cc2.xy(1, 1));
				builder2.add(SwingComponentFactory.createDateField(false, true), cc2.xy(3, 1));

				simpleDaysTimeline1.setUpLeftComponent(builder2.getPanel());

				simpleDaysTimeline1.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "doCopy");
				simpleDaysTimeline1.getActionMap().put("doCopy", new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						System.out.println("Copie coller");
					}
				});

				final JSpinner spinner = new JSpinner(new SpinnerNumberModel(simpleDaysTimeline1.getNbDays(), 1, nbDays, 1));
				spinner.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						simpleDaysTimeline1.setNbDays((Integer) spinner.getValue());
					}
				});

				JButton button = new JButton("Gaby");
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// model.update();

						simpleDaysTimeline1.getSelectionModel().clearSelection();
						for (int r = 0; r < model.getResourceCount(); r++) {
							List<MyMissionSimpleDaysTask> sts = model.getTasks(r);
							if (sts != null && !sts.isEmpty()) {
								for (MyMissionSimpleDaysTask st : sts) {
									if (st.getNum() == 50) {
										int re = simpleDaysTimeline1.convertResourceIndexToView(r);
										simpleDaysTimeline1.getSelectionModel().addSelectionIndexResource(re, new DayDate(st.getDayDateMin().getDay()), st);
										simpleDaysTimeline1.scrollRectToVisible(simpleDaysTimeline1.getSimpleDayTaskRect(re, st));
									}
								}
							}
						}
					}
				});

				FormLayout layout = new FormLayout("FILL:600DLU:GROW(1.0)", "FILL:DEFAULT:NONE,CENTER:3DLU:NONE,FILL:200DLU:GROW(0.5),CENTER:3DLU:NONE,FILL:100DLU:NONE,CENTER:3DLU:NONE,DEFAULT");
				PanelBuilder builder = new PanelBuilder(layout);
				CellConstraints cc = new CellConstraints();
				builder.add(spinner, cc.xy(1, 1));
				builder.add(simpleDaysTimeline1, cc.xy(1, 3));
				builder.add(new JScrollPane(table), cc.xy(1, 5));
				builder.add(button, cc.xy(1, 7));

				frame.getContentPane().add(builder.getPanel(), BorderLayout.CENTER);

				frame.setTitle("SimpleDaysTimeline");
				frame.pack();
				// frame.setSize(300, 400);

				frame.setVisible(true);
				// timeline.centerAtCurrentDay();
			}
		});
	}

}
