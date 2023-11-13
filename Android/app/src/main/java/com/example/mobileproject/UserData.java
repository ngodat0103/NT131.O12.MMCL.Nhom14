package com.example.mobileproject;
import com.google.gson.annotations.SerializedName;

public class UserData {
    @SerializedName("id")
    private Id id;

    @SerializedName("createdOn")
    private long createdOn;

    @SerializedName("assetName")
    private String assetName;

    @SerializedName("parentAssetName")
    private String parentAssetName;

    @SerializedName("userFullName")
    private String userFullName;



    public UserData(){

    }

    public Id getId() {
        return id;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getParentAssetName() {
        return parentAssetName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public static class Id {
        @SerializedName("realm")
        private String realm;

        @SerializedName("userId")
        private String userId;

        @SerializedName("assetId")
        private String assetId;

        public String getRealm() {
            return realm;
        }

        public String getUserId() {
            return userId;
        }

        public String getAssetId() {
            return assetId;
        }
    }
}
