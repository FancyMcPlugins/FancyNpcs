package de.oliver.fancynpcs.tests.impl.api.skin;

import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.skins.SkinFetcherImpl;
import de.oliver.fancynpcs.tests.annotations.FNBeforeEach;
import de.oliver.fancynpcs.tests.annotations.FNTest;
import org.bukkit.entity.Player;

public class SkinFetcherTest {

    private SkinFetcherImpl skinFetcher;

    @FNBeforeEach
    public void setUp(Player player) {
        skinFetcher = new SkinFetcherImpl();
    }

    @FNTest(name = "Test fetch skin by UUID")
    public void testSkinByUUID(Player player) {
        SkinData skin = skinFetcher.getByUUID(player.getUniqueId());

        assert skin != null;
        assert skin.identifier().equals(player.getUniqueId().toString());
        assert skin.type().equals(SkinData.SkinType.UUID);
        assert skin.variant().equals(SkinData.SkinVariant.DEFAULT);
        assert skin.textureValue() != null && !skin.textureValue().isEmpty();
        assert skin.textureSignature() != null && !skin.textureSignature().isEmpty();
    }

    @FNTest(name = "Test fetch skin by username")
    public void testSkinByUsername(Player player) {
        SkinData skin = skinFetcher.getByUsername(player.getName());

        assert skin != null;
        assert skin.identifier().equals(player.getName());
        assert skin.type().equals(SkinData.SkinType.USERNAME);
        assert skin.variant().equals(SkinData.SkinVariant.DEFAULT);
        assert skin.textureValue() != null && !skin.textureValue().isEmpty();
        assert skin.textureSignature() != null && !skin.textureSignature().isEmpty();
    }

    @FNTest(name = "Test fetch skin by URL")
    public void testSkinByURL(Player player) {
        SkinData skin = skinFetcher.getByURL("https://s.namemc.com/i/de7d8a3ffd1f584c.png");

        assert skin != null;
        assert skin.identifier().equals("https://s.namemc.com/i/de7d8a3ffd1f584c.png");
        assert skin.type().equals(SkinData.SkinType.URL);
        assert skin.variant().equals(SkinData.SkinVariant.DEFAULT);
        assert skin.textureValue() != null && !skin.textureValue().isEmpty();
        assert skin.textureSignature() != null && !skin.textureSignature().isEmpty();
    }

}
