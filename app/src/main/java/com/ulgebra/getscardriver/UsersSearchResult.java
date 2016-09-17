package com.ulgebra.getscardriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Vijayakumar on 08/07/2016.
 */

public class UsersSearchResult extends AppCompatActivity {




    public ArrayList<UsersList> parents;
    ListView listView;
    View rootView;
    ProgressDialog dialog;
    public DialogFragment Dialog;
    TextView from_to_inp;
    LongOperation longOperation=new LongOperation();
    private int ChildClickStatus=-1;
    String bookingStatus,cancelReason,cancelledOn,editedOn,user_filter_q;

    LinearLayout linearLayout;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_users);

        Intent intent=getIntent();
        user_filter_q=intent.getStringExtra("filter_q");
        try {
            user_filter_q= URLEncoder.encode(user_filter_q,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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




            String serverURL = "http://luxscar.com/luxscar_app/searchUser.php?filterQuery="+user_filter_q;

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
                Log.i("net_err", lengthJsonArr+"is len");
                final ArrayList<UsersList> lists=new ArrayList<UsersList>();
                for(int i=0; i < lengthJsonArr; i++)
                {
                    final UsersList mp=new UsersList();


                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                    /******* Fetch node values **********/
                    String username       = jsonChildNode.optString("username").toString();
                    String userMob     = jsonChildNode.optString("user_mob").toString();
                    String userId=jsonChildNode.optString("user_id").toString();

                    Log.i("net_err", "user anem"+username+" mo"+userMob);


                    mp.setUsername(username);
                    mp.setUser_id(userId);
                    mp.setUser_mobile(userMob);

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
    public void loadHosts(final ArrayList<UsersList> newParents)
    {
        if (newParents == null){
            Log.i("net_err", "lh returned");
            return;
        }else{
            Log.i("net_err","lh ok");
        }



        parents = newParents;


        for(int i=0;i<parents.size();i++){
            final UsersList my_parent=parents.get(i);
            View vi= LayoutInflater.from(getApplicationContext()).inflate(R.layout.single_user,null);


            TextView booked_username=(TextView) vi.findViewById(R.id.bk_customer_name);
            TextView booked_userMob=(TextView) vi.findViewById(R.id.bk_customer_mob);

            LinearLayout holder_inp=(LinearLayout)vi.findViewById(R.id.single_user_hold);


            booked_username.setText(my_parent.getUsername());
            booked_userMob.setText(my_parent.getUser_mobile());


            holder_inp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),SingleUserDetails.class);
                    intent.putExtra("user_idd",my_parent.getUser_id());
                    startActivity(intent);
                }
            });

            linearLayout.addView(vi);

        }


    }

}
