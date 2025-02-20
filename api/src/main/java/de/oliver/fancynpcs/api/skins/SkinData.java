package de.oliver.fancynpcs.api.skins;

public class SkinData {

    private String identifier;
    private SkinVariant variant;

    private String textureValue;
    private String textureSignature;

    public SkinData(String identifier, SkinVariant variant, String textureValue, String textureSignature) {
        this.identifier = identifier;
        this.variant = variant;
        this.textureValue = textureValue;
        this.textureSignature = textureSignature;
    }

    public SkinData(String identifier, SkinVariant variant) {
        this(identifier, variant, null, null);
    }

    public boolean hasTexture() {
        return textureValue != null &&
                textureSignature != null &&
                !textureValue.isEmpty() &&
                !textureSignature.isEmpty();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public SkinVariant getVariant() {
        return variant;
    }

    public void setVariant(SkinVariant variant) {
        this.variant = variant;
    }

    public String getTextureValue() {
        return textureValue;
    }

    public void setTextureValue(String textureValue) {
        this.textureValue = textureValue;
    }

    public String getTextureSignature() {
        return textureSignature;
    }

    public void setTextureSignature(String textureSignature) {
        this.textureSignature = textureSignature;
    }

    public enum SkinVariant {
        AUTO,
        SLIM,
    }
}
