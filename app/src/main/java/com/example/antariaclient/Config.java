package com.example.antariaclient;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
    List<Photo> photos;
    Object publications;
    Object tables;
    Object contacts;

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        for (Photo p:photos) {
            p.image = p.image.replace(" ","");
        }
    }

    public Object getPublications() {
        return publications;
    }

    public void addPhoto(Photo photo){
        photos.add(photo);
    }

    public void removePhoto(int index){
        photos.remove(index);
    }

    public void setPublications(Object publications) {
        this.publications = publications;
    }

    public Object getTables() {
        return tables;
    }

    public void setTables(Object tables) {
        this.tables = tables;
    }

    public Object getContacts() {
        return contacts;
    }

    public void setContacts(Object contacts) {
        this.contacts = contacts;
    }

    public void showAllPicturesInLayout(ViewGroup viewGroup){
        for (Photo p:photos) {
            new Thread(() -> {
                try {
                    View v = createImageView(p,viewGroup.getContext());
                    viewGroup.post(() -> {
                       viewGroup.addView(v);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private View createImageView(Photo photo, Context context) throws IOException {
        URL url = new URL(DataEditingActivity.MAIN_ADDRESS + "/" + photo.getImage());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        InputStream input = connection.getInputStream();
        Bitmap temp = BitmapFactory.decodeStream(input);
        ImageView view = new ImageView(context);
        view.setId(View.generateViewId());
        view.setImageBitmap(temp);
        return view;
    }
    public Config(){
    }

    @Override
    public String toString() {
        return "Config{" +
                "photos=" + photos +
                ", publications=" + publications +
                ", tables=" + tables +
                ", contacts=" + contacts +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(photos, config.photos) && Objects.equals(publications, config.publications)
                && Objects.equals(tables, config.tables) && Objects.equals(contacts, config.contacts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photos, publications, tables, contacts);
    }
}
