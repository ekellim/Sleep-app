package com.example.sleep_app.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sleep_app.MyDBHandler;
import com.example.sleep_app.R;
import com.example.sleep_app.Sleep;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
        return inflater.inflate(R.layout.fragment_view_stats_fragment, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        GraphView graph = (GraphView) getView().findViewById(R.id.graph);

        // activate horizontal zooming and scrolling
        graph.getViewport().setScalable(true);

        // activate horizontal scrolling
        graph.getViewport().setScrollable(true);

        // activate horizontal and vertical zooming and scrolling
        graph.getViewport().setScalableY(true);

        // activate vertical scrolling
        graph.getViewport().setScrollableY(true);


        MyDBHandler dbHandler = new MyDBHandler(getActivity());
        //int sleep_id = dbHandler.GetLastSleepId();
        int sleep_id = 3;
        DataPoint[] value_time_pairs = dbHandler.getSleepData(sleep_id);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(
                value_time_pairs
        );
        graph.addSeries(series);

        //graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        //graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
        //graph.getGridLabelRenderer().setHumanRounding(false);
    }
}