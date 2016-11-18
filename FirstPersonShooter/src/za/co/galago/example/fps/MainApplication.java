package za.co.galago.example.fps;

import com.bruynhuis.galago.app.Base3DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import za.co.galago.example.fps.screens.PlayScreen;

/**
 * First Person Shooter Test
 * @author nickidebruyn
 */
public class MainApplication extends Base3DApplication {

    public MainApplication() {
        super("First Person Shooter", 1280, 720, "fps.save", null, null, false);
    }
    

    public static void main(String[] args) {
        new MainApplication();
    }

    @Override
    protected void preInitApp() {
        
        splashInfoMessage = "Galago (FPS Example)";

        
    }

    @Override
    protected void postInitApp() {
        showScreen("play");
    }

    @Override
    protected boolean isPhysicsEnabled() {
        return true;
    }

    @Override
    protected void initScreens(ScreenManager screenManager) {
        screenManager.loadScreen("play", new PlayScreen());
    }

    @Override
    public void initModelManager(ModelManager modelManager) {
    }

    @Override
    protected void initSound(SoundManager soundManager) {
    }

    @Override
    protected void initEffect(EffectManager effectManager) {
    }

    @Override
    protected void initTextures(TextureManager textureManager) {
    }

}
