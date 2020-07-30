package com.bytedance.sdk.open.tiktok;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class TikTokConfig {

    /** @return the client key initialized with this configuration */
    public abstract String getClientKey();

    /** @return Builder object to create config */
    public static Builder builder() {
        return new AutoValue_TikTokConfig.Builder();
    }

    /** Builder class for config */
    @AutoValue.Builder
    public abstract static class Builder {

        /**
         * @param clientKey client key to set
         * @return Builder builder instance
         */
        public abstract Builder clientKey(String clientKey);

        /** @return Immutable TikTokConfig instance */
        public abstract TikTokConfig build();
    }
}