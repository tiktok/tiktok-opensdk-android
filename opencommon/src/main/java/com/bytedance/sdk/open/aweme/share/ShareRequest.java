package com.bytedance.sdk.open.aweme.share;


import com.bytedance.sdk.open.aweme.base.IMediaObject;
import com.bytedance.sdk.open.aweme.base.ImageObject;
import com.bytedance.sdk.open.aweme.base.MediaContent;
import com.bytedance.sdk.open.aweme.base.VideoObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A configuration class for a share request. This class replaces the externally exposed
 * {@link Share.Request} interface and abstracts the developer from TikTok media classes
 */
public class ShareRequest {

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
        private List<String> videoPaths;
        private List<String> imagePaths;
        private List<String> hashtags;

        private Builder() {}

        /**
         * @param imagePaths paths to videos to set
         * @return Builder builder instance
         */
        public Builder imagePaths(List<String> imagePaths) {
            if (videoPaths != null) {
                throw new IllegalStateException("Share requests do not support both images and videos");
            }

            this.imagePaths = imagePaths;
            return this;
        }

        /**
         * @param videoPaths paths to videos to set
         * @return Builder builder instance
         */
        public Builder videoPaths(List<String> videoPaths) {
            if (imagePaths != null) {
                throw new IllegalStateException("Share requests do not support both images and videos");
            }

            this.videoPaths = videoPaths;
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

        /** @return Immutable ShareRequest instance */
        public ShareRequest build() {
            if (videoPaths == null && imagePaths == null) {
                throw new IllegalStateException("Share requests must contain media paths");
            }

            Share.Request shareReq = new Share.Request();

            IMediaObject mediaObject;
            if (videoPaths != null) {
                VideoObject videoObject = new VideoObject();
                videoObject.mVideoPaths = new ArrayList<>(videoPaths);
                mediaObject = videoObject;

            } else {
                ImageObject imageObject = new ImageObject();
                imageObject.mImagePaths = new ArrayList<>(imagePaths);
                mediaObject = imageObject;
            }

            MediaContent mediaContent = new MediaContent();
            mediaContent.mMediaObject = mediaObject;
            shareReq.mMediaContent = mediaContent;

            if (hashtags != null) {
                shareReq.mHashTagList = new ArrayList<>(hashtags);
            }

            return new ShareRequest(shareReq);
        }
    }
}