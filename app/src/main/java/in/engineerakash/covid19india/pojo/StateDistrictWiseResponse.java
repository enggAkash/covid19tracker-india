package in.engineerakash.covid19india.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StateDistrictWiseResponse implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StateDistrictWiseResponse> CREATOR = new Parcelable.Creator<StateDistrictWiseResponse>() {
        @Override
        public StateDistrictWiseResponse createFromParcel(Parcel in) {
            return new StateDistrictWiseResponse(in);
        }

        @Override
        public StateDistrictWiseResponse[] newArray(int size) {
            return new StateDistrictWiseResponse[size];
        }
    };
    @SerializedName("name")
    private String name;
    @SerializedName("districtData")
    private ArrayList<District> districtArrayList;

    public StateDistrictWiseResponse(String name, ArrayList<District> districtArrayList) {
        this.name = name;
        this.districtArrayList = districtArrayList;
    }

    protected StateDistrictWiseResponse(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0x01) {
            districtArrayList = new ArrayList<District>();
            in.readList(districtArrayList, District.class.getClassLoader());
        } else {
            districtArrayList = null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<District> getDistrictArrayList() {
        return districtArrayList;
    }

    public void setDistrictArrayList(ArrayList<District> districtArrayList) {
        this.districtArrayList = districtArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (districtArrayList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(districtArrayList);
        }
    }
}
