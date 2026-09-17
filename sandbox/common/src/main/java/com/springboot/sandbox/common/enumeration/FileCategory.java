package com.springboot.sandbox.common.enumeration;

public enum FileCategory {
    AVATAR("Avatar", true),
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
