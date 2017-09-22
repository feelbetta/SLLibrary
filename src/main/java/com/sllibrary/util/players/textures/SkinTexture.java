package com.sllibrary.util.players.textures;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor @Getter @Setter
public class SkinTexture {

    private String value, signature;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SkinTexture && ((SkinTexture) obj).getSignature().equals(this.signature) && ((SkinTexture) obj).getValue().equals(this.value);
    }
}
