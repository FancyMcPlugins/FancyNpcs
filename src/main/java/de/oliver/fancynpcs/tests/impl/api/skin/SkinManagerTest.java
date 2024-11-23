package de.oliver.fancynpcs.tests.impl.api.skin;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.skins.SkinManagerImpl;
import de.oliver.fancynpcs.skins.cache.SkinCacheFake;
import de.oliver.fancynpcs.tests.annotations.FNBeforeEach;
import de.oliver.fancynpcs.tests.annotations.FNTest;
import org.bukkit.entity.Player;

import java.io.File;

import static de.oliver.fancynpcs.tests.Expectable.expect;

public class SkinManagerTest {

    private SkinManagerImpl skinFetcher;

    @FNBeforeEach
    public void setUp(Player player) {

        skinFetcher = new SkinManagerImpl(new SkinCacheFake());
    }

    @FNTest(name = "Test fetch skin by UUID")
    public void testSkinByUUID(Player player) {
        SkinData skin = skinFetcher.getByUUID(player.getUniqueId());

        expect(skin).toBeDefined();
        expect(skin.identifier()).toEqual(player.getUniqueId().toString());
        expect(skin.type()).toEqual(SkinData.SkinType.UUID);
        expect(skin.variant()).toEqual(SkinData.SkinVariant.DEFAULT);
        expect(skin.textureValue()).toBeDefined();
        expect(skin.textureValue().length()).toBeGreaterThan(0);
        expect(skin.textureSignature()).toBeDefined();
        expect(skin.textureSignature().length()).toBeGreaterThan(0);
    }

    @FNTest(name = "Test fetch skin by username")
    public void testSkinByUsername(Player player) {
        SkinData skin = skinFetcher.getByUsername(player.getName());

        expect(skin).toBeDefined();
        expect(skin.identifier()).toEqual(player.getName());
        expect(skin.type()).toEqual(SkinData.SkinType.USERNAME);
        expect(skin.variant()).toEqual(SkinData.SkinVariant.DEFAULT);
        expect(skin.textureValue()).toBeDefined();
        expect(skin.textureValue().length()).toBeGreaterThan(0);
        expect(skin.textureSignature()).toBeDefined();
    }

    @FNTest(name = "Test fetch skin by URL")
    public void testSkinByURL(Player player) {
        SkinData skin = skinFetcher.getByURL("https://s.namemc.com/i/de7d8a3ffd1f584c.png");

        expect(skin).toBeDefined();
        expect(skin.identifier()).toEqual("https://s.namemc.com/i/de7d8a3ffd1f584c.png");
        expect(skin.type()).toEqual(SkinData.SkinType.URL);
        expect(skin.variant()).toEqual(SkinData.SkinVariant.DEFAULT);
        expect(skin.textureValue()).toBeDefined();
        expect(skin.textureValue().length()).toBeGreaterThan(0);
        expect(skin.textureSignature()).toBeDefined();
        expect(skin.textureSignature().length()).toBeGreaterThan(0);
    }

    @FNTest(name = "Test fetch skin by file")
    public void testSkinByFile(Player player) {
        SkinData skin = skinFetcher.getByFile("plugins/FancyNpcs/testskin.png");
        FancyNpcs.getInstance().getPlugin().saveResource("testskin.png", false);

        expect(skin).toBeDefined();
        expect(skin.identifier()).toEqual("plugins/FancyNpcs/testskin.png");
        expect(skin.type()).toEqual(SkinData.SkinType.FILE);
        expect(skin.variant()).toEqual(SkinData.SkinVariant.DEFAULT);
        expect(skin.textureValue()).toBeDefined();
        expect(skin.textureValue().length()).toBeGreaterThan(0);
        expect(skin.textureSignature()).toBeDefined();
        expect(skin.textureSignature().length()).toBeGreaterThan(0);

        new File("plugins/FancyNpcs/testskin.png").delete();
    }

    @FNTest(name = "Test get skin")
    public void testGetSkin(Player player) {
        SkinData skin = skinFetcher.get("skinname", "value", "signature");

        expect(skin).toBeDefined();
        expect(skin.identifier()).toEqual("skinname");
        expect(skin.type()).toEqual(SkinData.SkinType.VALUE_SIGNATURE);
        expect(skin.variant()).toEqual(SkinData.SkinVariant.DEFAULT);
        expect(skin.textureValue()).toEqual("value");
        expect(skin.textureSignature()).toEqual("signature");
    }
}
