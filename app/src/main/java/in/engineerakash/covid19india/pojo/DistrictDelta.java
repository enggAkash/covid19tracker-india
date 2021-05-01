package in.engineerakash.covid19india.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class DistrictDelta implements Parcelable {

    public static final Creator<DistrictDelta> CREATOR = new Creator<DistrictDelta>() {
        @Override
        public DistrictDelta createFromParcel(Parcel in) {
            return new DistrictDelta(in);
        }

        @Override
        public DistrictDelta[] newArray(int size) {
            return new DistrictDelta[size];
        }
    };
    @SerializedName("confirmed")
    private int confirmed;

    public DistrictDelta(int confirmed) {
        this.confirmed = confirmed;
    }

    protected DistrictDelta(Parcel in) {
        confirmed = in.readInt();
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(confirmed);
    }
}
