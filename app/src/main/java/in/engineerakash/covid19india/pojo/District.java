package in.engineerakash.covid19india.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class District implements Parcelable {

    public static final Creator<District> CREATOR = new Creator<District>() {
        @Override
        public District createFromParcel(Parcel in) {
            return new District(in);
        }

        @Override
        public District[] newArray(int size) {
            return new District[size];
        }
    };
    @SerializedName("name")
    private
    String name;
    @SerializedName("confirmed")
    private
    int confirmed;
    @SerializedName("lastupdatedtime")
    private
    String lastUpdateTime;
    @SerializedName("delta")
    private
    DistrictDelta delta;

    public District(String name, int confirmed, String lastUpdateTime, DistrictDelta delta) {
        this.name = name;
        this.confirmed = confirmed;
        this.lastUpdateTime = lastUpdateTime;
        this.delta = delta;
    }

    protected District(Parcel in) {
        name = in.readString();
        confirmed = in.readInt();
        lastUpdateTime = in.readString();
        delta = in.readParcelable(DistrictDelta.class.getClassLoader());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public DistrictDelta getDelta() {
        return delta;
    }

    public void setDelta(DistrictDelta delta) {
        this.delta = delta;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(confirmed);
        dest.writeString(lastUpdateTime);
        dest.writeParcelable(delta, flags);
    }
}
