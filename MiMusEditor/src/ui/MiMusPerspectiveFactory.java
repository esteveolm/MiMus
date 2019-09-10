package ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class MiMusPerspectiveFactory implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.createFolder("left", IPageLayout.LEFT, 0.15f, editorArea);
		layout.createFolder("leftTop", IPageLayout.TOP, 0.6f, "left")
				.addView("MiMusEditor.documentsView");
		layout.createFolder("leftBottom", IPageLayout.BOTTOM, 0.4f, "left")
				.addView("MiMusEditor.loginView");
		IFolderLayout right = 
				layout.createFolder("right", IPageLayout.RIGHT, 0.6f, editorArea);
		right.addView("MiMusEditor.biblioView");
		right.addView("MiMusEditor.artistaView");
		right.addView("MiMusEditor.casaView");
		right.addView("MiMusEditor.genereView");
		right.addView("MiMusEditor.instrumentView");
		right.addView("MiMusEditor.llocView");
		right.addView("MiMusEditor.oficiView");
		right.addView("MiMusEditor.promotorView");
	}
}
