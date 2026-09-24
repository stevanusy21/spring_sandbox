package com.springboot.sandbox.common.enumeration;

public enum FileCategory {
    IMAGE_PROFILE("Image_Profile", true),
    IMAGE_PRODUCT("Image_Product", true),
    DOCUMENT("Document", false);

    private String bucketName;
    private boolean isPublic;

    FileCategory(String bucketName, boolean isPublic) {
        this.bucketName = bucketName;
        this.isPublic = isPublic;
    }

    public String getBucketName() {
        return bucketName;
    }

    public boolean isPublic() {
        return isPublic;
    }
}
