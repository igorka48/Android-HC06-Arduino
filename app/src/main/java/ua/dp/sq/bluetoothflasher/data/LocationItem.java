package ua.dp.sq.bluetoothflasher.data;


import java.io.Serializable;

public class LocationItem implements Serializable {

    private static final long serialVersionUID = 1L;

    public String orderNumber;
    public String title;
    public String groupName;
    public double lat;
    public double lng;
    public String navigatorNumber;
    public boolean isChecked;
    public String key;

    public LocationItem() {
    }

    public LocationItem(String orderNumber, String title, String groupName, double lat, double lng,
                        String navigatorNumber) {
        this.orderNumber = orderNumber;
        this.title = title;
        this.groupName = groupName;
        this.lat = lat;
        this.lng = lng;
        this.navigatorNumber = navigatorNumber;
    }

    public LocationItem(String orderNumber, String title, String groupName, double lat, double lng,
                        String navigatorNumber, boolean isChecked) {
        this.orderNumber = orderNumber;
        this.title = title;
        this.groupName = groupName;
        this.lat = lat;
        this.lng = lng;
        this.navigatorNumber = navigatorNumber;
        this.isChecked = isChecked;
    }

    public LocationItem(String s, String s1, boolean b) {
        this.title = s;
        this.groupName = s1;
        this.isChecked = b;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getNavigatorNumber() {
        return navigatorNumber;
    }

    public void setNavigatorNumber(String navigatorNumber) {
        this.navigatorNumber = navigatorNumber;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean getChecked() {
        return isChecked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
