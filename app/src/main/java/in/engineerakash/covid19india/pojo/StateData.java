package in.engineerakash.covid19india.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StateData implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StateData> CREATOR = new Parcelable.Creator<StateData>() {
        @Override
        public StateData createFromParcel(Parcel in) {
            return new StateData(in);
        }

        @Override
        public StateData[] newArray(int size) {
            return new StateData[size];
        }
    };
    @SerializedName("statewise")
    private ArrayList<StateWiseData> stateWiseData;

    protected StateData(Parcel in) {
        if (in.readByte() == 0x01) {
            stateWiseData = new ArrayList<StateWiseData>();
            in.readList(stateWiseData, StateWiseData.class.getClassLoader());
        } else {
            stateWiseData = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (stateWiseData == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(stateWiseData);
        }
    }
}
