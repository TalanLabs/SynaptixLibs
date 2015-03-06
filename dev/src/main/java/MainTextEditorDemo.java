import helper.MainHelper;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.html.HtmlLanguageSupport;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class MainTextEditorDemo extends JFrame {

	public MainTextEditorDemo() {
		JPanel cp = new JPanel(new BorderLayout());

		RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
		LanguageSupportFactory lsf = LanguageSupportFactory.get();
		lsf.register(textArea);
		HtmlLanguageSupport support = (HtmlLanguageSupport) lsf.getSupportFor(SyntaxConstants.SYNTAX_STYLE_HTML);
		support.setShowDescWindow(false);

		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
		textArea.setCodeFoldingEnabled(true);
		RTextScrollPane sp = new RTextScrollPane(textArea);
		cp.add(sp);

		setContentPane(cp);
		setTitle("Text Editor Demo");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);

	}

	public static void main(String[] args) {
		MainHelper.init();
		// Start all Swing applications on the EDT.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainTextEditorDemo().setVisible(true);
			}
		});
	}

}
