package com.example.budgetbuddyapp.ui.interest;

public class Interest {

    private String slug;
    private String name;
    private String parent_slug;

    // initialize
    public Interest(String slug, String name, String parent_slug){
        this.slug = slug;
        this.name = name;
        this.parent_slug = parent_slug;
    }

    @Override
    public String toString(){
        return slug + ", " + name + ", " + parent_slug;
    }

    public Interest(){
    }

    // getters and setters
    public String getSlug(){
        return slug;
    }

    public void setSlug(String slug){
        this.slug = slug;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getParent_slug(){
        return parent_slug;
    }

    public void setParent_slug(String parent_slug){
        this.parent_slug = parent_slug;
    }
}