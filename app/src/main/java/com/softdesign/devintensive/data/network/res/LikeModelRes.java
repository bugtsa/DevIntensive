package com.softdesign.devintensive.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LikeModelRes {
    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {

        @SerializedName("homeTask")
        @Expose
        private int homeTask;
        @SerializedName("projects")
        @Expose
        private int projects;
        @SerializedName("linesCode")
        @Expose
        private int linesCode;
        @SerializedName("likesBy")
        @Expose
        private List<String> likesBy = new ArrayList<String>();
        @SerializedName("rait")
        @Expose
        private int rait;
        @SerializedName("updated")
        @Expose
        private String updated;
        @SerializedName("rating")
        @Expose
        private int rating;

        public int getRating() {
            return rating;
        }

        public int getRait() {
            return rait;
        }

        public List<String> getLikesBy() {
            return likesBy;
        }
    }
}
