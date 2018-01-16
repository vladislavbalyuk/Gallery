package com.status.gallery;

import android.content.DialogInterface;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.status.gallery.main.MediaFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailDialog extends DialogFragment {

    private MediaFile mediafile;
    private TextView textView;
    private TextView textName2, textLocation, textModel, textParametrs1, textParametrs2, textPosition;

    private Double latitude, longitude;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(getActivity().getResources().getString(R.string.delete));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.dialog_detail, null);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        Date datetime = mediafile.getDateTime();
        textView = (TextView) v.findViewById(R.id.textDateTime1);
        String strDate = "";
        if (datetime != null) {
            strDate = sdf.format(datetime);
        }
        textView.setText(strDate);

        sdf = new SimpleDateFormat("EEEE HH:mm");
        textView = (TextView) v.findViewById(R.id.textDateTime2);
        strDate = "";
        if (datetime != null) {
            strDate = sdf.format(datetime);
        }
        textView.setText(strDate);

        textView = (TextView) v.findViewById(R.id.textName1);
        textView.setText(mediafile.getFile().getName());

        textName2 = (TextView) v.findViewById(R.id.textName2);
        textModel = (TextView) v.findViewById(R.id.textModel);
        textParametrs1 = (TextView) v.findViewById(R.id.textParametrs1);
        textParametrs2 = (TextView) v.findViewById(R.id.textParametrs2);
        textPosition = (TextView) v.findViewById(R.id.textPosition);
        textLocation = (TextView) v.findViewById(R.id.textLocation);

        new ExifTask().execute();

        return v;
    }


    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    public void setMediaFile(MediaFile mediafile) {
        this.mediafile = mediafile;
    }

    public String getTagFromVideo(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String d = mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        return d;
    }

    public String getTagFromImage(File file, String tag) {
        String value = null;
        try {

            ExifInterface exif = null;
            exif = new ExifInterface(file.getAbsolutePath());
            if (exif != null) {
                if (tag.equals("TAG_GPS_LATITUDE")) {
                    value = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                }
                if (tag.equals("TAG_GPS_LONGITUDE")) {
                    value = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                }
                if (tag.equals("TAG_GPS_LATITUDE_REF")) {
                    value = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                }
                if (tag.equals("TAG_GPS_LONGITUDE_REF")) {
                    value = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                }
                if (tag.equals("TAG_MODEL")) {
                    value = exif.getAttribute(ExifInterface.TAG_MODEL);
                }
                if (tag.equals("TAG_MAKE")) {
                    value = exif.getAttribute(ExifInterface.TAG_MAKE);
                }
                if (tag.equals("TAG_EXPOSURE_TIME")) {
                    value = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
                }
                if (tag.equals("TAG_ISO_SPEED_RATINGS")) {
                    value = exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);
                }
                if (tag.equals("TAG_FOCAL_LENGTH")) {
                    value = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
                }
                if (tag.equals("TAG_LIGHT_SOURCE")) {
                    value = exif.getAttribute(ExifInterface.TAG_LIGHT_SOURCE);
                }
                if (tag.equals("TAG_IMAGE_LENGTH")) {
                    value = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                }
                if (tag.equals("TAG_IMAGE_WIDTH")) {
                    value = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                }
            }
        } catch (IOException e) {
        }
        ;
        return value;
    }

    private Double convertToDegree(String stringDMS) {
        Double result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Double(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;


    }

    public static StringBuilder getStringJSON(String urlReqest) {
        BufferedReader reader = null;
        StringBuilder buf = new StringBuilder();
        try {
            URL url = new URL(urlReqest);
            HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setReadTimeout(10000);
            c.connect();
            reader = new BufferedReader(new InputStreamReader(c.getInputStream()));

            String line = null;
            while ((line = reader.readLine()) != null) {
                buf.append(line + "\n");
            }
        } catch (Exception e) {
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }

        return buf;
    }

    private class ExifTask extends AsyncTask<Void, Void, Void> {

        int i;
        String name2, model, parametrs1, parametrs2, location;

        @Override
        protected Void doInBackground(Void... params) {
            if (mediafile.isImage()) {

                String length = getTagFromImage(mediafile.getFile(), "TAG_IMAGE_LENGTH");
                String width = getTagFromImage(mediafile.getFile(), "TAG_IMAGE_WIDTH");
                double size = ((double) mediafile.getFile().length()) / (1024 * 1024);
                name2 = String.format("%.2fM", size) + (length == null || width == null ? "" : "    " + length + "×" + width + "px");

                model = getTagFromImage(mediafile.getFile(), "TAG_MODEL") + ", " + getTagFromImage(mediafile.getFile(), "TAG_MAKE");
                if (model.contains("null")) {
                    model = "";
                } else {
                    String exposure_time = getTagFromImage(mediafile.getFile(), "TAG_EXPOSURE_TIME");
                    i = exposure_time.indexOf(".");
                    if (i >= 0) {
                        exposure_time = exposure_time.substring(0, Math.min(i + 4, exposure_time.length()));
                    }

                    parametrs1 = exposure_time + "    ISO" + getTagFromImage(mediafile.getFile(), "TAG_ISO_SPEED_RATINGS");
                    parametrs2 = "";
                    String focalLength = getTagFromImage(mediafile.getFile(), "TAG_FOCAL_LENGTH");
                    if (focalLength != null && focalLength.contains("/")) {
                        i = focalLength.indexOf("/");
                        double d1 = Double.parseDouble(focalLength.substring(0, i));
                        double d2 = Double.parseDouble(focalLength.substring(i + 1));
                        double focal = d1 / d2;
                        parametrs2 = String.format("%.2f", focal);
                    }
                    String light = getTagFromImage(mediafile.getFile(), "TAG_LIGHT_SOURCE");
                    parametrs2 = parametrs2 + "    " + (light.equals("0") ? getResources().getString(R.string.nolight) : getResources().getString(R.string.light));
                }

                String lat = getTagFromImage(mediafile.getFile(), "TAG_GPS_LATITUDE");
                String lat_ref = getTagFromImage(mediafile.getFile(), "TAG_GPS_LATITUDE_REF");
                if (lat != null && lat_ref != null) {
                    if (lat_ref.equals("N")) {
                        latitude = convertToDegree(lat);
                    } else {
                        latitude = 0 - convertToDegree(lat);
                    }
                }

                String lng = getTagFromImage(mediafile.getFile(), "TAG_GPS_LONGITUDE");
                String lng_ref = getTagFromImage(mediafile.getFile(), "TAG_GPS_LONGITUDE_REF");
                if (lng != null && lng_ref != null) {
                    if (lng_ref.equals("N")) {
                        longitude = convertToDegree(lng);
                    } else {
                        longitude = 0 - convertToDegree(lng);
                    }
                    longitude = -longitude;
                }
                location = "";
                if (latitude != null && longitude != null && (latitude != 0.0 || longitude != 0.0)) {
                    location = String.format("%.4f", latitude) + ", " + String.format("%.4f", longitude);
                }
            } else {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(mediafile.getFile().getAbsolutePath());
                String d = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);
                if (d != null) {
                    i = d.indexOf("+");
                    d = d.substring(i + 1);
                    i = d.indexOf("+");
                    latitude = Double.parseDouble(d.substring(0, i));
                    d = d.substring(i + 1);
                    longitude = Double.parseDouble(d);
                    location = "";
                    if (latitude != null && longitude != null && (latitude != 0.0 || longitude != 0.0)) {
                        location = String.format("%.4f", latitude) + ", " + String.format("%.4f", longitude);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            textName2.setText(name2);
            textModel.setText(model);
            textParametrs1.setText(parametrs1);
            textParametrs2.setText(parametrs2);
            textPosition.setText(mediafile.getFile().getAbsolutePath());

            textLocation.setText(location);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(
                    PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            objectMapper.configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://maps.googleapis.com/")
                    .addCallAdapterFactory(
                            RxJavaCallAdapterFactory.create())
                    .addConverterFactory(
                            JacksonConverterFactory.create(objectMapper))
                    .build();
            GoogleApi googleApi = retrofit.create(GoogleApi.class);

            if(latitude != null && longitude != null) {
                Observable<Results> results = googleApi.getData(latitude.toString() + "," + longitude.toString(), "ru", getResources().getString(R.string.API_KEY));
                Observable<Result> resultObs = results
                        .concatMapIterable(Results::getResults);
                Observable<String> map = resultObs.take(1).map(Result::getFormattedAddress);
                map.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(s -> textLocation.setText(s));
            }
        }
    }


    public interface GoogleApi {
        @GET("/maps/api/geocode/json")
        Observable<Results> getData(
                @Query("latlng") String latlng,
                @Query("language") String language,
                @Query("key") String key
        );
    }

    public static class Result {

        @JsonCreator
        public Result(@JsonProperty("formatted_address") String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }

        @JsonProperty("formatted_address")
        private String formattedAddress;

        @JsonProperty("formatted_address")
        public String getFormattedAddress() {
            return formattedAddress;
        }

        @JsonProperty("formatted_address")
        public void setFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }
    }

    public static class Results {

        @JsonCreator
        public Results(@JsonProperty("results") List<Result> results) {
            this.results = results;
        }

        @JsonProperty("results")
        private List<Result> results;

        @JsonProperty("results")
        public List<Result> getResults() {
            return results;
        }

        @JsonProperty("results")
        public void setResults(List<Result> results) {
            this.results = results;
        }


    }

}