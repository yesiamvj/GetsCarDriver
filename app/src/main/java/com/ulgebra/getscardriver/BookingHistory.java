package com.ulgebra.getscardriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Vijayakumar on 08/07/2016.
 */

public class BookingHistory extends AppCompatActivity {




    public ArrayList<Car_lists> parents;
    ListView listView;
    View rootView;
    ProgressDialog dialog;
    public DialogFragment Dialog;
    TextView from_to_inp;
    LongOperation longOperation=new LongOperation();
    private int ChildClickStatus=-1;
    String bookingStatus,cancelReason,cancelledOn,editedOn,book_user_id;

    LinearLayout linearLayout;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking_history);

        Intent intent=getIntent();
        if(intent.getExtras()!=null){
            book_user_id=intent.getStringExtra("book_user_id");
        }
        else{
            book_user_id="alll";
        }


        try{
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
//            if (prev != null) {
//                ft.remove(prev);
//            }
//            ft.addToBackStack(null);

          //  DialogFragment dialogFragment = new DialogFragment();

            // dialogFragment.show(ft,"Loading");
            dialog = ProgressDialog.show(this, "Loading...", "Please wait...", true);
         //   rootView = inflater.inflate(R.layout.activity_booking_history, container, false);

            linearLayout=(LinearLayout) findViewById(R.id.my_list_hold);




            String serverURL = "http://luxscar.com/luxscar_app/show_hstry.php?user_id="+book_user_id+"&accessac=Admin123";

            new LongOperation().execute(serverURL);




        }catch (Exception e){
            Log.v("net_err","err="+e.getMessage());

        }



        // listView=(ListView)rootView.findViewById(R.id.list_hold);


        // Use AsyncTask execute Method To Prevent ANR Problem





    }

    public class LongOperation  extends AsyncTask<String, Void, Void> {

        // Required initialization

        // private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        String data ="";
        String otpt="";



        protected void onPreExecute() {
            // NOTE: You can call UI Element here.




        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {

            /************ Make Post Call To Web Server ***********/
            BufferedReader reader=null;

            // Send data
            try
            {
                // Defined URL  where to send data
                URL url = new URL(urls[0]);

                // Send POST data request

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( "" );
                wr.flush();
                // Get the server response
                try{
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    // StringBuilder sb = new StringBuilder();
                    String line = null;


                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        // Append server response in string
                        // sb.append(line + "\n");
                        otpt+=line;

                    }

                    // Append Server Response To Content String
                    Content = otpt;
                    Log.i("my_err","output="+otpt);
                }catch (Exception e){

                }



            }
            catch(Exception ex)
            {
                Error = ex.getMessage();
            }
            finally
            {
                try
                {

                    reader.close();
                }

                catch(Exception ex) {}
            }

            /*****************************************************/
            return null;
        }


        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            dialog.dismiss();
            try {
                JSONObject jsonResponse;
                Log.i("net_err", "try json");
                /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                jsonResponse = new JSONObject(Content);

                /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                /*******  Returns null otherwise.  *******/
                JSONArray jsonMainNode = jsonResponse.optJSONArray("Hstry_items");
                Log.i("net_err", "json main");
                /*********** Process each JSON Node ************/

                int lengthJsonArr = jsonMainNode.length();
                final ArrayList<Car_lists> lists=new ArrayList<Car_lists>();
                for(int i=0; i < lengthJsonArr; i++)
                {
                    final Car_lists mp=new Car_lists();
                    Log.i("net_err", "foloop json");

                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                    /******* Fetch node values **********/
                    String name       = jsonChildNode.optString("car_name").toString();
                    String ride     = jsonChildNode.optString("ride").toString();
                    String id = jsonChildNode.optString("booking_id").toString();
                    String bk_car_bookDate=jsonChildNode.optString("booked_date").toString();
                    String bk_customer_name=jsonChildNode.optString("booked_username").toString();
                    String bk_customer_mob=jsonChildNode.optString("booking_user_mobile").toString();
                    bookingStatus=jsonChildNode.optString("booking_status").toString();
                    cancelledOn=jsonChildNode.optString("cancelled_on").toString();
                    cancelReason=jsonChildNode.optString("booking_cancel_reason").toString();
                    editedOn=jsonChildNode.optString("edited_on").toString();


                    mp.set_carname(name);
                    mp.setRide_dure(ride);
                    mp.setBooking_id(id);
                    mp.setBooked_on(bk_car_bookDate);
                    mp.setBk_customer_name(bk_customer_name);
                    mp.setBk_customer_mob(bk_customer_mob);
                    mp.setBookingStatus(bookingStatus);
                    mp.setCancelledOn(cancelledOn);
                    mp.setCancelReason(cancelReason);
                    mp.setEditedOn(editedOn);
                    lists.add(mp);
                }
                Log.i("net_err", "out for json");

                loadHosts(lists);

            } catch (JSONException e) {

                e.printStackTrace();
                Log.i("net_err", "error json");
            }
            if(otpt.hashCode()==0){
                Toast.makeText(getApplicationContext(),"Check your Internet Connection",Toast.LENGTH_LONG).show();

            }else{
                // Toast.makeText(getActivity().getApplicationContext(),otpt,Toast.LENGTH_LONG).show();

            }


        }

    }
    public void loadHosts(final ArrayList<Car_lists> newParents)
    {
        if (newParents == null){
            Log.i("net_err", "lh returned");
            return;
        }else{
            Log.i("net_err","lh ok");
        }



        parents = newParents;


        for(int i=0;i<parents.size();i++){
            final Car_lists my_parent=parents.get(i);
            View vi= LayoutInflater.from(getApplicationContext()).inflate(R.layout.single_history,null);

            TextView bk_no_inp=(TextView)vi.findViewById(R.id.booking_no);
            TextView bk_ca_num_inp=(TextView)vi.findViewById(R.id.bk_car_name);
            TextView bk_ride_dur=(TextView) vi.findViewById(R.id.bk_duration);
            TextView bk_car_bookDate=(TextView) vi.findViewById(R.id.bk_car_bookDate);
            TextView booked_username=(TextView) vi.findViewById(R.id.bk_customer_name);
            TextView booked_userMob=(TextView) vi.findViewById(R.id.bk_customer_mob);
            Button supSHa=(Button) vi.findViewById(R.id.supSHa);

            LinearLayout holder_inp=(LinearLayout)vi.findViewById(R.id.single_hstry_hold);
            //TextView ecit_txt=(TextView)vi.findViewById(R.id.bk_editd);
            // ecit_txt.setText(my_parent.getBooking_id());









            bk_ca_num_inp.setText(my_parent.getCar_name());
            bk_ride_dur.setText(my_parent.getRide_dure());
            bk_ca_num_inp.setText(""+my_parent.getCar_name());

            booked_userMob.setText(my_parent.getBk_customer_mob());
            booked_username.setText(my_parent.getBk_customer_name());
            if(my_parent.getBookingStatus().contains("9")){
                Log.v("bookingsts aa",bookingStatus+""+my_parent.getBooking_id());
                bk_no_inp.setText("Cancelled Booking No "+my_parent.getBooking_id());
                bk_no_inp.setBackgroundColor(Color.parseColor("#c0392b"));
                supSHa.setBackgroundColor(Color.parseColor("#c0392b"));
                bk_car_bookDate.setText("Booked On "+my_parent.getBooked_on()+"\n Cancelled On "+my_parent.getCancelledOn()+" Reason" +
                        ": "+my_parent.getCancelReason());


            }
            if(my_parent.getBookingStatus().contains("5")){
                Log.v("bookingsts aa",bookingStatus+""+my_parent.getBooking_id());
                bk_no_inp.setText("Edited Booking No "+my_parent.getBooking_id());
                bk_no_inp.setBackgroundColor(Color.parseColor("#16a085"));
                supSHa.setBackgroundColor(Color.parseColor("#16a085"));
                bk_car_bookDate.setText("Booked On "+my_parent.getBooked_on()+"\n Edited On "+my_parent.getEditedOn()+"");


            }
            if(my_parent.getBookingStatus().contains("3")){
                Log.v("bookingsts",bookingStatus+""+my_parent.getBooking_id());
                bk_no_inp.setText("New Booking No "+my_parent.getBooking_id());
                bk_no_inp.setBackgroundColor(Color.parseColor("#2980b9"));
                supSHa.setBackgroundColor(Color.parseColor("#2980b9"));
                bk_car_bookDate.setText("On "+my_parent.getBooked_on());
            }



            holder_inp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),SingleBookingDetails.class);
                    intent.putExtra("booking_idd",my_parent.getBooking_id());
                    startActivity(intent);
                }
            });





            linearLayout.addView(vi);

        }


    }

}
