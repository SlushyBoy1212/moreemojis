package org.bloodcircuit.moreemojis.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import java.io.Reader;
import java.util.List;
public class cr {
    private static final Pattern pattern = Pattern.compile(":([a-z0-9_]+):");
    private static final Identifier file =
            Identifier.fromNamespaceAndPath("moreemojis", "icons.json");
    private static final FontDescription font =
            new FontDescription.Resource(Identifier.fromNamespaceAndPath("moreemojis", "icons"));
    private static volatile Map<String, String> icons = Map.of();
    public static void register() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                Identifier.fromNamespaceAndPath("moreemojis", "icons"),
                (ResourceManagerReloadListener) cr::reload);
    }
    private static void reload(ResourceManager manager) {
        Map<String, String> loaded = new HashMap<>();
        List<Resource> stack = manager.getResourceStack(file);
        for (Resource resource : stack) {
            try (Reader reader = resource.openAsReader()) {
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                obj.entrySet().forEach(entry -> loaded.put(entry.getKey(), entry.getValue().getAsString()));
            } catch (Exception e) {
                System.err.println("[moreemojis] Bad icons.json in pack " + resource.sourcePackId() + ": " + e);
            }
        }
        icons = Map.copyOf(loaded);
        System.out.println("[moreemojis] Loaded " + loaded.size() + " icons");
    }
    public static Component replace(Component original) {
        MutableComponent result = Component.empty();
        original.visit((style, text) -> {
            Matcher matcher = pattern.matcher(text);
            int i = 0;
            while (matcher.find()) {
                String icon = icons.get(matcher.group(1));
                if (icon != null) {
                    if (i < matcher.start()) {
                        result.append(Component.literal(text.substring(i, matcher.start())).setStyle(style));
                    }
                    result.append(Component.literal(icon).setStyle(style.withFont(font)));
                    i = matcher.end();
                }
            }
            if (i < text.length()) {
                result.append(Component.literal(text.substring(i)).setStyle(style));
            }
            return Optional.empty();
        }, Style.EMPTY);
        return result;
    }
}