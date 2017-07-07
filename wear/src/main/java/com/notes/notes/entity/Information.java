package com.notes.notes.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Information implements Parcelable {

    protected long Id;
    protected String Title;
    protected String Text;
    protected String Date;
    protected String Type;
    protected String Mark;
    protected String Image;
    protected String Color;

    protected int Id_id;
    protected int Title_id;
    protected int Text_id;
    protected int Date_id;
    protected int Type_id;
    protected int Mark_id;
    protected int Image_id;

    protected int Color_id;

    public Information(long id, String Title, String Text) {

    }

    protected Information(Parcel in) {
        Id = in.readLong();
        Title = in.readString();
        Text = in.readString();
        Date = in.readString();
        Type = in.readString();
        Mark = in.readString();
        Image = in.readString();
        Color = in.readString();
        Id_id = in.readInt();
        Title_id = in.readInt();
        Text_id = in.readInt();
        Date_id = in.readInt();
        Type_id = in.readInt();
        Mark_id = in.readInt();
        Image_id = in.readInt();
        Color_id = in.readInt();
    }

    public static final Creator<Information> CREATOR = new Creator<Information>() {
        @Override
        public Information createFromParcel(Parcel in) {
            return new Information(in);
        }

        @Override
        public Information[] newArray(int size) {
            return new Information[size];
        }
    };

    public long getId() {
        return Id;
    }

    public Long setId(long id) {
        Id = id;
        return null;
    }

    public String getTitle() {
        return Title;
    }

    public String setTitle(String title) {
        Title = title;
        return title;
    }

    public String getText() {
        return Text;
    }

    public String setText(String text) {
        Text = text;
        return text;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getMark() {
        return Mark;
    }

    public void setMark(String mark) {
        Mark = mark;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public int getId_id() {
        return Id_id;
    }

    public void setId_id(int id_id) {
        Id_id = id_id;
    }

    public int getTitle_id() {
        return Title_id;
    }

    public void setTitle_id(int title_id) {
        Title_id = title_id;
    }

    public int getText_id() {
        return Text_id;
    }

    public void setText_id(int text_id) {
        Text_id = text_id;
    }

    public int getDate_id() {
        return Date_id;
    }

    public void setDate_id(int date_id) {
        Date_id = date_id;
    }

    public int getType_id() {
        return Type_id;
    }

    public void setType_id(int type_id) {
        Type_id = type_id;
    }

    public int getMark_id() {
        return Mark_id;
    }

    public void setMark_id(int mark_id) {
        Mark_id = mark_id;
    }

    public int getImage_id() {
        return Image_id;
    }

    public void setImage_id(int image_id) {
        Image_id = image_id;
    }

    public int getColor_id() {
        return Color_id;
    }

    public void setColor_id(int color_id) {
        Color_id = color_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(Id);
        dest.writeString(Title);
        dest.writeString(Text);
    }
}