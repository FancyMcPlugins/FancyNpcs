package de.oliver.fancynpcs.tests.api;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.skins.SkinManagerImpl;
import de.oliver.fancynpcs.skins.cache.SkinCacheMemory;
import de.oliver.plugintests.annotations.FPBeforeEach;
import de.oliver.plugintests.annotations.FPTest;
import org.bukkit.entity.Player;

import java.io.File;

import static de.oliver.plugintests.Expectable.expect;

public class SkinManagerTest {

    private SkinManagerImpl skinFetcher;

    @FPBeforeEach
    public void setUp(Player player) {

        skinFetcher = new SkinManagerImpl(new SkinCacheMemory(), new SkinCacheMemory());
    }

    @FPTest(name = "Test fetch skin by UUID")
    public void testSkinByUUID(Player player) {
        SkinData skin = skinFetcher.getByUUID(player.getUniqueId(), SkinData.SkinVariant.AUTO);

        expect(skin).toBeDefined();
        expect(skin.getIdentifier()).toEqual(player.getUniqueId().toString());
        expect(skin.getVariant()).toEqual(SkinData.SkinVariant.AUTO);
        expect(skin.getTextureValue()).toBeDefined();
        expect(skin.getTextureValue().length()).toBeGreaterThan(0);
        expect(skin.getTextureSignature()).toBeDefined();
        expect(skin.getTextureSignature().length()).toBeGreaterThan(0);
    }

    @FPTest(name = "Test fetch skin by username")
    public void testSkinByUsername(Player player) {
        SkinData skin = skinFetcher.getByUsername(player.getName(), SkinData.SkinVariant.AUTO);

        expect(skin).toBeDefined();
        expect(skin.getIdentifier()).toEqual(player.getUniqueId().toString());
        expect(skin.getVariant()).toEqual(SkinData.SkinVariant.AUTO);
        expect(skin.getTextureValue()).toBeDefined();
        expect(skin.getTextureValue().length()).toBeGreaterThan(0);
        expect(skin.getTextureSignature()).toBeDefined();
    }

    @FPTest(name = "Test fetch skin by URL")
    public void testSkinByURL(Player player) {
        SkinData skin = skinFetcher.getByURL("https://s.namemc.com/i/de7d8a3ffd1f584c.png", SkinData.SkinVariant.AUTO);

        expect(skin).toBeDefined();
        expect(skin.getIdentifier()).toEqual("https://s.namemc.com/i/de7d8a3ffd1f584c.png");
        expect(skin.getVariant()).toEqual(SkinData.SkinVariant.AUTO);
        expect(skin.getTextureValue()).toBeDefined();
        expect(skin.getTextureValue().length()).toBeGreaterThan(0);
        expect(skin.getTextureSignature()).toBeDefined();
        expect(skin.getTextureSignature().length()).toBeGreaterThan(0);
    }

    @FPTest(name = "Test fetch skin by file")
    public void testSkinByFile(Player player) {
        FancyNpcs.getInstance().getPlugin().saveResource("skins/testskin.png", true);
        SkinData skin = skinFetcher.getByFile("testskin.png", SkinData.SkinVariant.AUTO);

        expect(skin).toBeDefined();
        expect(skin.getIdentifier()).toEqual("testskin.png");
        expect(skin.getVariant()).toEqual(SkinData.SkinVariant.AUTO);
        expect(skin.getTextureValue()).toBeDefined();
        expect(skin.getTextureValue().length()).toBeGreaterThan(0);
        expect(skin.getTextureSignature()).toBeDefined();
        expect(skin.getTextureSignature().length()).toBeGreaterThan(0);

        new File("plugins/FancyNpcs/skins/testskin.png").delete();
    }
}
