package in.engineerakash.covid19india.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import in.engineerakash.covid19india.pojo.District;
import in.engineerakash.covid19india.pojo.DistrictDelta;
import in.engineerakash.covid19india.pojo.StateDistrictWiseResponse;

public final class JsonExtractor {

    public static ArrayList<StateDistrictWiseResponse> parseStateDistrictWiseResponseJson(String json) {
        ArrayList<StateDistrictWiseResponse> stateDistrictList = new ArrayList<>();

        try {
            JSONObject rootJo = new JSONObject(json);

            Iterator<String> stateKeys = rootJo.keys();

            while (stateKeys.hasNext()) {

                String stateName = stateKeys.next();

                JSONObject stateJo = rootJo.getJSONObject(stateName);
                JSONObject districtDataJo = stateJo.getJSONObject("districtData");

                Iterator<String> districtKeys = districtDataJo.keys();

                ArrayList<District> districtList = new ArrayList<>();
                while (districtKeys.hasNext()) {
                    String districtName = districtKeys.next();

                    JSONObject districtJo = districtDataJo.getJSONObject(districtName);

                    int confirmed = districtJo.optInt("confirmed");
                    String lastUpdatedTime = districtJo.optString("lastupdatedtime");

                    JSONObject deltaJo = districtJo.getJSONObject("delta");
                    int deltaConfirmed = deltaJo.optInt("confirmed");

                    DistrictDelta districtDelta = new DistrictDelta(deltaConfirmed);

                    District district = new District(districtName, confirmed, lastUpdatedTime, districtDelta);
                    districtList.add(district);
                }

                StateDistrictWiseResponse stateDistrictWiseResponse =
                        new StateDistrictWiseResponse(stateName, districtList);

                stateDistrictList.add(stateDistrictWiseResponse);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return stateDistrictList;
    }


}
