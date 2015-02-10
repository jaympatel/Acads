

package guifilesplitter;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

public class GuiFileSplitterApp extends SingleFrameApplication {
    @Override protected void startup() {
        show(new GuiFileSplitterView(this));
    }

    @Override protected void configureWindow(java.awt.Window root) {
    }

    public static GuiFileSplitterApp getApplication() {
        return Application.getInstance(GuiFileSplitterApp.class);
    }

    public static void main(String[] args) {
        launch(GuiFileSplitterApp.class, args);
    }
}
