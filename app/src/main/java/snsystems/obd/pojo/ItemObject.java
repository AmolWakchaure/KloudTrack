package snsystems.obd.pojo;

/**
 * Created by Admin on 12/30/2016.
 */
public class ItemObject {
    private String name;
    private int imageId;

    public ItemObject(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}