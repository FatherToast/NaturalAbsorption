package fathertoast.naturalabsorption.datagen;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber( modid = NaturalAbsorption.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class DataGatherListener {
    
    @SubscribeEvent
    public static void onGatherData( GatherDataEvent event ) {
        DataGenerator generator = event.getGenerator();
        
        if( event.includeClient() ) {
            for( Map.Entry<String, NALanguageProvider.TranslationKey> entry : NALanguageProvider.LANG_CODE_MAP.entrySet() ) {
                generator.addProvider( true, new NALanguageProvider( generator, entry.getKey(), entry.getValue() ) );
            }
        }
        if( event.includeServer() ) {
            generator.addProvider( true, new NALootModifierProvider( generator ) );
            generator.addProvider( true, new NARecipeProvider( generator ) );
        }
    }
}