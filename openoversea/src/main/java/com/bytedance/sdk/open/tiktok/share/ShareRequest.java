package com.bytedance.sdk.open.tiktok.share;

import com.bytedance.sdk.open.tiktok.base.MediaContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A configuration class for a share request. This class replaces the externally exposed
 * {@link Share.Request} interface and abstracts the developer from TikTok media classes
 */
public class ShareRequest {

    public enum MediaType {
        IMAGE,
        VIDEO
    }

    private Share.Request shareRequest;

    private ShareRequest(Share.Request shareRequest) {
        this.shareRequest = shareRequest;
    }

    /** @return proxied share request */
    public Share.Request getShareRequest() {
        return shareRequest;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Builder class for config */
    public static class Builder {
        private List<String> mediaPaths;
        private MediaType mediaType;
        private List<String> hashtags;
        private String extra;
        private String anchorSourceType;
        private Share.Format shareFormat = Share.Format.DEFAULT;
        private final HashMap<String, Integer> extraShareOptions = new HashMap<String, Integer>();

        private Builder() {}

        /**
         * @param mediaPaths paths to media to set
         * @return Builder builder instance
         */
        public Builder mediaPaths(List<String> mediaPaths) {
            this.mediaPaths = mediaPaths;
            return this;
        }

        /**
         * @param mediaType type of media for paths
         * @return Builder builder instance
         */
        public Builder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        /**
         * @param hashtags hashtags to share the video with
         * @return Builder builder instance
         */
        public Builder hashtags(List<String> hashtags) {
            this.hashtags = hashtags;
            return this;
        }

        /**
         * @param extra extra information to pass to client in a serialized json format {"hello": "world"}
         * @return Builder builder instance
         */
        public Builder extra(String extra) {
            this.extra = extra;
            return this;
        }

        /**
         * @param anchorSourceType used for anchor auto selection, specifying the source of anchor
         * @return Builder builder instance
         */
        public Builder anchorSourceType(String anchorSourceType) {
            this.anchorSourceType = anchorSourceType;
            return this;
        }
        public Builder shareFormat(Share.Format format) {
            this.shareFormat = format;
            return this;
        }

        /**
         * @param key used for populating extra share options
         * @param value used for populating extra share options
         * @return Builder builder instance
         * */
        public Builder putExtraShareOptions(String key, Integer value) {
            this.extraShareOptions.put(key, value);
            return this;
        }

        /** @return Immutable ShareRequest instance */
        public ShareRequest build() {
            if (mediaType == null) {
                throw new IllegalStateException("Share request must specify media type");
            }

            if (mediaPaths == null) {
                throw new IllegalStateException("Share request must specify media paths");
            }

            Share.Request shareReq = new Share.Request();

            MediaContent mediaContent;
            switch (mediaType) {
                case IMAGE:
                    mediaContent = new MediaContent(ShareKt.MediaType.IMAGE, new ArrayList<>(mediaPaths));
                    break;
                case VIDEO:
                    mediaContent = new MediaContent(ShareKt.MediaType.VIDEO, new ArrayList<>(mediaPaths));
                    break;
                default:
                    throw new IllegalStateException("Unsupported media type");
            }
            shareReq.mMediaContent = mediaContent;
            shareReq.mExtra = extra;
            shareReq.mAnchorSourceType = anchorSourceType;
            shareReq.extraShareOptions = extraShareOptions;
            shareReq.mShareFormat = this.shareFormat;
            if (hashtags != null) {
                shareReq.mHashTagList = new ArrayList<>(hashtags);
            }

            return new ShareRequest(shareReq);
        }
    }
}