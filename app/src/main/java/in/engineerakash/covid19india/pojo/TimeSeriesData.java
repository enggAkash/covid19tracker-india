package in.engineerakash.covid19india.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TimeSeriesData implements Parcelable {

    public static final Creator<TimeSeriesData> CREATOR = new Creator<TimeSeriesData>() {
        @Override
        public TimeSeriesData createFromParcel(Parcel in) {
            return new TimeSeriesData(in);
        }

        @Override
        public TimeSeriesData[] newArray(int size) {
            return new TimeSeriesData[size];
        }
    };
    @SerializedName("dailyconfirmed")
    private String dailyConfirmed;
    @SerializedName("dailydeceased")
    private String dailyDeceased;
    @SerializedName("dailyrecovered")
    private String dailyRecovered;
    @SerializedName("date")
    private String date;
    @SerializedName("totalconfirmed")
    private String totalConfirmed;
    @SerializedName("totaldeceased")
    private String totalDeceased;
    @SerializedName("totalrecovered")
    private String totalRecovered;

    public TimeSeriesData(String dailyConfirmed, String dailyDeceased, String dailyRecovered, String date,
                          String totalConfirmed, String totalDeceased, String totalRecovered) {
        this.dailyConfirmed = dailyConfirmed;
        this.dailyDeceased = dailyDeceased;
        this.dailyRecovered = dailyRecovered;
        this.date = date;
        this.totalConfirmed = totalConfirmed;
        this.totalDeceased = totalDeceased;
        this.totalRecovered = totalRecovered;
    }

    protected TimeSeriesData(Parcel in) {
        dailyConfirmed = in.readString();
        dailyDeceased = in.readString();
        dailyRecovered = in.readString();
        date = in.readString();
        totalConfirmed = in.readString();
        totalDeceased = in.readString();
        totalRecovered = in.readString();
    }

    public String getDailyConfirmed() {
        return dailyConfirmed;
    }

    public void setDailyConfirmed(String dailyConfirmed) {
        this.dailyConfirmed = dailyConfirmed;
    }

    public String getDailyDeceased() {
        return dailyDeceased;
    }

    public void setDailyDeceased(String dailyDeceased) {
        this.dailyDeceased = dailyDeceased;
    }

    public String getDailyRecovered() {
        return dailyRecovered;
    }

    public void setDailyRecovered(String dailyRecovered) {
        this.dailyRecovered = dailyRecovered;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalConfirmed() {
        return totalConfirmed;
    }

    public void setTotalConfirmed(String totalConfirmed) {
        this.totalConfirmed = totalConfirmed;
    }

    public String getTotalDeceased() {
        return totalDeceased;
    }

    public void setTotalDeceased(String totalDeceased) {
        this.totalDeceased = totalDeceased;
    }

    public String getTotalRecovered() {
        return totalRecovered;
    }

    public void setTotalRecovered(String totalRecovered) {
        this.totalRecovered = totalRecovered;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dailyConfirmed);
        dest.writeString(dailyDeceased);
        dest.writeString(dailyRecovered);
        dest.writeString(date);
        dest.writeString(totalConfirmed);
        dest.writeString(totalDeceased);
        dest.writeString(totalRecovered);
    }
}
