package fathertoast.naturalabsorption.client;

import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.fml.DistExecutor;

/**
 * This class provides a safe bridge to access physical-client code from common areas.
 * This is safe to reference/load on the physical-server, but it will throw errors if any methods are called.
 */
public class ClientUtil {
    
    private static boolean armorFeaturesEnabled;
    private static boolean hideArmorBar;

    public static boolean OVERLAY_ENABLED = true;
    
    /** Sets whether the user wants armor features enabled, and toggles rendering if needed. */
    public static DistExecutor.SafeRunnable setArmorFeaturesEnabled( boolean value ) {
        // Non-lambda function is required to pass Forge's validation
        //noinspection Convert2Lambda
        return new DistExecutor.SafeRunnable() {
            @Override
            public void run() {
                armorFeaturesEnabled = value;
                toggleArmorBarRender( armorFeaturesEnabled && hideArmorBar );
            }
        };
    }
    
    /** Sets whether the user wants the armor bar hidden, and toggles rendering if needed. */
    public static DistExecutor.SafeRunnable setHideArmorBar( boolean value ) {
        // Non-lambda function is required to pass Forge's validation
        //noinspection Convert2Lambda
        return new DistExecutor.SafeRunnable() {
            @Override
            public void run() {
                hideArmorBar = value;
                toggleArmorBarRender( armorFeaturesEnabled && hideArmorBar );
            }
        };
    }
    
    /** Disables (or enables) the vanilla armor bar renderer. */
    private static void toggleArmorBarRender( boolean disable ) {
        OverlayRegistry.enableOverlay( ForgeIngameGui.ARMOR_LEVEL_ELEMENT, !disable );
    }
    
    /** Swaps the vanilla heart bar renderer for ours, or the other way around. */
    public static DistExecutor.SafeRunnable toggleAbsorptionBackgroundRender( boolean enable ) {
        // Non-lambda function is required to pass Forge's validation
        //noinspection Convert2Lambda
        return new DistExecutor.SafeRunnable() {
            @Override
            public void run() {
                ClientUtil.OVERLAY_ENABLED = enable;
            }
        };
    }
}