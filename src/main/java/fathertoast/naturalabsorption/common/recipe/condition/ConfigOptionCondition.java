package fathertoast.naturalabsorption.common.recipe.condition;

import com.google.gson.JsonObject;
import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Supplier;

public class ConfigOptionCondition implements ICondition {

    private static final ResourceLocation ID = NaturalAbsorption.resourceLoc("config_option");

    public enum Operation {

        SPONGE_BOOK("sponge_book", Config.ABSORPTION.GENERAL.spongeBookEnabled::get);

        Operation(String name, Supplier<Boolean> test) {
            this.name = name;
            this.test = test;
        }
        final String name;
        final Supplier<Boolean> test;

        public String getName() {
            return this.name;
        }

        public boolean test() {
            return this.test.get();
        }

        @Nullable
        public static Operation getFromName(String operationName) {
            for (Operation operation : values()) {
                if (operation.getName().equals(operationName)) {
                    return operation;
                }
            }
            return null;
        }
    }

    private final Operation operation;

    public ConfigOptionCondition(Operation operation) {
        this.operation = operation;
    }

    @Override
    public ResourceLocation getID( ) { return ID; }

    @Override
    public boolean test( ) {
        return this.operation.test();
    }

    public static class Serializer implements IConditionSerializer<ConfigOptionCondition> {

        public Serializer() { }

        @Override
        public void write(JsonObject json, ConfigOptionCondition value) {
            json.addProperty("operation", value.operation.getName());
        }

        @Override
        public ConfigOptionCondition read(JsonObject json) {
            String operationName = json.getAsJsonPrimitive("operation").getAsString();
            Operation operation = Operation.getFromName(operationName);

            if (operation == null) {
                throw new IllegalArgumentException("Attempted to read a config option crafting condition with invalid operation type. " +
                        "Expected any of \"" + Arrays.toString(Operation.values()) + "\" but found \"" + operationName + "\"");
            }
            return new ConfigOptionCondition(operation);
        }

        @Override
        public ResourceLocation getID( ) { return ID; }
    }
}
