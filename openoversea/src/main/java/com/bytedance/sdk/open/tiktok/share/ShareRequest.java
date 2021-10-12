package com.bytedance.sdk.open.tiktok.share;


import com.bytedance.sdk.open.tiktok.base.IMediaObject;
import com.bytedance.sdk.open.tiktok.base.ImageObject;
import com.bytedance.sdk.open.tiktok.base.MediaContent;
import com.bytedance.sdk.open.tiktok.base.VideoObject;

import java.util.ArrayList;
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

        /** @return Immutable ShareRequest instance */
        public ShareRequest build() {
            if (mediaType == null) {
                throw new IllegalStateException("Share request must specify media type");
            }

            if (mediaPaths == null) {
                throw new IllegalStateException("Share request must specify media paths");
            }

            Share.Request shareReq = new Share.Request();

            IMediaObject mediaObject;
            switch (mediaType) {
                case IMAGE:
                    ImageObject imageObject = new ImageObject();
                    imageObject.mImagePaths = new ArrayList<>(mediaPaths);
                    mediaObject = imageObject;
                    break;
                case VIDEO:
                    VideoObject videoObject = new VideoObject();
                    videoObject.mVideoPaths = new ArrayList<>(mediaPaths);
                    mediaObject = videoObject;
                    break;
                default:
                    throw new IllegalStateException("Unsupported media type");
            }

            MediaContent mediaContent = new MediaContent();
            mediaContent.mMediaObject = mediaObject;
            shareReq.mMediaContent = mediaContent;
            shareReq.mExtra = extra;
            shareReq.mAnchorSourceType = anchorSourceType;

            if (hashtags != null) {
                shareReq.mHashTagList = new ArrayList<>(hashtags);
            }

            return new ShareRequest(shareReq);
        }
    }
}