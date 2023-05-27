package dev.battlesweeper.objects.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class PacketHandlerModule extends Module {
    @Override
    public String getModuleName() {
        return "PacketHandlerModule";
    }

    @Override
    public Version version() {
        return new Version(0, 0, 1, "", null, null);
    }

    @Override
    public void setupModule(SetupContext context) {
        context.appendAnnotationIntrospector(new PacketAnnotationIntrospector());
    }
}
