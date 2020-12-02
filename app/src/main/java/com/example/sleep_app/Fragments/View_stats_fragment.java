package com.example.sleep_app.Fragments;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sleep_app.MyDBHandler;
import com.example.sleep_app.R;
import com.example.sleep_app.Sleep;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link View_stats_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class View_stats_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //popup window variables
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button buttonClose;

    public View_stats_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment View_stats_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static View_stats_fragment newInstance(String param1, String param2) {
        View_stats_fragment fragment = new View_stats_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_view_stats_fragment, container, false);
        LinearLayout linearLayout = v.findViewById(R.id.layoutscroll);
        //LinearLayout layout = (LinearLayout) findViewById(R.id.info);

        MyDBHandler dbHandler = new MyDBHandler(getActivity());
        String sleep_table = dbHandler.loadSleepHandler();
        String[] lines = sleep_table.split(System.getProperty("line.separator"));
        int count = Integer.parseInt(lines[0]);


        for (int i=count; i>0; i--){
            String[] data = lines[i].split(" ");


            View divider = new View(getActivity());
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1
            ));
            divider.setBackgroundColor(Color.parseColor("#000000"));

            Button btn = new Button((getActivity()));
            btn.setText(R.string.this_night);
            btn.setId(i-1);
            int sleep_id = Integer.parseInt(data[0]);
            btn.setOnClickListener(new View.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    ShowNightGraph(sleep_id, dbHandler);
                }
            });

            Button deleteButton = new Button(getActivity());
            deleteButton.setText("X");
            deleteButton.setId(10000 + (i-1));
            deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    dbHandler.deleteSleep(sleep_id);
                    linearLayout.postInvalidate();
                }
            });

            TextView test = new TextView(getActivity());
            test.setText(String.format("%s%s", getString(R.string.start_str), data[1]));
            test.setId(i-1);
            test.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            ((LinearLayout) linearLayout).addView(test);
            ((LinearLayout) linearLayout).addView(btn);
            ((LinearLayout) linearLayout).addView(deleteButton);
            ((LinearLayout) linearLayout).addView(divider);
        }
        return v;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        MyDBHandler dbHandler = new MyDBHandler(getActivity());
        int sleep_id = dbHandler.GetLastSleepId();
        //int sleep_id=3; //Voor tests
        ShowNightGraph(sleep_id, dbHandler);
        Button moreInfo = view.findViewById(R.id.buttonMoreInfo);
        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreInfoDialog(v);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void ShowNightGraph(int sleep_id, MyDBHandler dbHandler){
        /*GraphView graph = (GraphView) getView().findViewById(R.id.graph);
        graph.removeAllSeries();
        // activate horizontal zooming and scrolling
        graph.getViewport().setScalable(true);
        // activate horizontal scrolling
        graph.getViewport().setScrollable(true);
        // activate horizontal and vertical zooming and scrolling
        graph.getViewport().setScalableY(true);
        // activate vertical scrolling
        graph.getViewport().setScrollableY(true);
        DataPoint[] value_time_pairs = dbHandler.getSleepData(sleep_id);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(
                value_time_pairs
        );
        graph.addSeries(series);

        String strHourFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(strHourFormat);
        sdf = new SimpleDateFormat(strHourFormat);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), sdf));
        //graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
        //graph.getGridLabelRenderer().setHumanRounding(false);*/

        int[] activity_counts = dbHandler.getActivityCounts(sleep_id);
        double[] times = dbHandler.getTimestamps(sleep_id);

        int length = times.length;
        DataPoint[] value_time_pairs;
        if (length-6>0){
            value_time_pairs = new DataPoint[length-6] ;
            for (int i = 4; i<length-2; i++){
                float sleep = (float) 0.5; //0.5=slaap, 1=wakker
                double sleep_coeff = 0.0033*(1.06*activity_counts[i-4]+0.54*activity_counts[i-3]+0.58*activity_counts[i-2]+0.76*activity_counts[i-1]+2.3*activity_counts[i]+0.74*activity_counts[i+1]+0.67*activity_counts[i+2]);
                if (sleep_coeff > 1){
                    sleep=1;
                }
                value_time_pairs[i-4] = new DataPoint(times[i], sleep);
                Log.d("Bartest", String.valueOf(sleep_coeff));
            }

            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                    new DataPoint(times[0], 1)
            });

            GraphView graph = (GraphView) getView().findViewById(R.id.graph);
            graph.removeAllSeries();
            try{
                series = new BarGraphSeries<>(value_time_pairs);
            }
            catch (Exception e){
                Log.d("BarGraph", value_time_pairs[0].toString());
            }

            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(1.5);

            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(times[4]);
            graph.getViewport().setMaxX(times[length-2]);

            // enable scaling and scrolling
            graph.getViewport().setScalable(true);

            String strHourFormat = "HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(strHourFormat);

            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), sdf));
            graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
            graph.addSeries(series);

// styling
            series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) Math.abs(data.getY()*150), (int) Math.abs(data.getY()*255), 100);
                }
            });

            series.setSpacing(0);
        }
        else{
            Log.d("Barview", "Night too short");
            Toast.makeText(getActivity(), "Night too short", Toast.LENGTH_SHORT).show();
        }


    }

    public void moreInfoDialog(View view){
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View moreInfoPopupView = getLayoutInflater().inflate(R.layout.popup, null);
        buttonClose = (Button) moreInfoPopupView.findViewById(R.id.buttonClose);

        dialogBuilder.setView(moreInfoPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}