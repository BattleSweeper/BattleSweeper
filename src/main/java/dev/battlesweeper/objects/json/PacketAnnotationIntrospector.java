package dev.battlesweeper.objects.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import dev.battlesweeper.objects.packet.Packet;
import dev.battlesweeper.objects.packet.PacketType;

import java.io.Serial;
import java.util.Arrays;
import java.util.List;

public class PacketAnnotationIntrospector extends JacksonAnnotationIntrospector {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public Version version() {
        return new Version(0, 0, 1, "", null, null);
    }

    @Override
    public List<NamedType> findSubtypes(Annotated a) {
        if (a.getAnnotated() instanceof Class<?> klass) {
            Class<?>[] permittedSubclasses;
            if (klass.getSuperclass().isSealed() && klass.getSuperclass() == Packet.class)
                permittedSubclasses = klass.getSuperclass().getPermittedSubclasses();
            else if (klass == Packet.class)
                permittedSubclasses = klass.getPermittedSubclasses();
            else
                permittedSubclasses = new Class<?>[]{};

            if (permittedSubclasses.length > 0) {
                return Arrays.stream(permittedSubclasses)
                        .map(c -> {
                            if (c.isAnnotationPresent(PacketType.class))
                                return new NamedType(c, c.getAnnotation(PacketType.class).type());
                            else
                                return new NamedType(c, regularizeClassName(c));
                        })
                        .toList();
            }
        }
        return null;
    }

    private String regularizeClassName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(PacketType.class))
            return clazz.getAnnotation(PacketType.class).type();

        var name = clazz.getSimpleName();
        if (!name.endsWith("Packet"))
            return null;

        return name.substring(0, name.length() - "Packet".length())
                .replaceAll("([A-Z])", "_$1")
                .toLowerCase()
                .substring(1);
    }
}
