package in.engineerakash.covid19india.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TimeSeriesStateWiseResponse implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TimeSeriesStateWiseResponse> CREATOR = new Parcelable.Creator<TimeSeriesStateWiseResponse>() {
        @Override
        public TimeSeriesStateWiseResponse createFromParcel(Parcel in) {
            return new TimeSeriesStateWiseResponse(in);
        }

        @Override
        public TimeSeriesStateWiseResponse[] newArray(int size) {
            return new TimeSeriesStateWiseResponse[size];
        }
    };
    @SerializedName("cases_time_series")
    private ArrayList<TimeSeriesData> casesTimeSeriesArrayList;
    @SerializedName("statewise")
    private ArrayList<StateWiseData> stateWiseDataArrayList;

    public TimeSeriesStateWiseResponse(ArrayList<TimeSeriesData> casesTimeSeriesArrayList,
                                       ArrayList<StateWiseData> stateWiseDataArrayList) {
        this.casesTimeSeriesArrayList = casesTimeSeriesArrayList;
        this.stateWiseDataArrayList = stateWiseDataArrayList;
    }

    protected TimeSeriesStateWiseResponse(Parcel in) {
        if (in.readByte() == 0x01) {
            casesTimeSeriesArrayList = new ArrayList<TimeSeriesData>();
            in.readList(casesTimeSeriesArrayList, TimeSeriesData.class.getClassLoader());
        } else {
            casesTimeSeriesArrayList = null;
        }
        if (in.readByte() == 0x01) {
            stateWiseDataArrayList = new ArrayList<StateWiseData>();
            in.readList(stateWiseDataArrayList, StateWiseData.class.getClassLoader());
        } else {
            stateWiseDataArrayList = null;
        }
    }

    public ArrayList<TimeSeriesData> getCasesTimeSeriesArrayList() {
        return casesTimeSeriesArrayList;
    }

    public void setCasesTimeSeriesArrayList(ArrayList<TimeSeriesData> casesTimeSeriesArrayList) {
        this.casesTimeSeriesArrayList = casesTimeSeriesArrayList;
    }

    public ArrayList<StateWiseData> getStateWiseDataArrayList() {
        return stateWiseDataArrayList;
    }

    public void setStateWiseDataArrayList(ArrayList<StateWiseData> stateWiseDataArrayList) {
        this.stateWiseDataArrayList = stateWiseDataArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (casesTimeSeriesArrayList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(casesTimeSeriesArrayList);
        }
        if (stateWiseDataArrayList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(stateWiseDataArrayList);
        }
    }
}
