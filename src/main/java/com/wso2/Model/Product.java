package com.wso2.Model;

public class Product {
    private String name;
    private String url;
    private String subdirectory;

    public Product(String name, String url, String subdirectory) {
        this.name = name;
        this.url = url;
        this.subdirectory = subdirectory;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSubdirectory() {
        return subdirectory;
    }

    public void setSubdirectory(String subdirectory) {
        this.subdirectory = subdirectory;
    }
}
