package snsystems.obd.advertise;

import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import snsystems.obd.R;

import static com.google.android.gms.internal.zzhu.runOnUiThread;

/**
 * Created by snsystem_amol on 2/23/2017.
 */

public class DisplayAdvertiseAdapter extends BaseExpandableListAdapter
{


    ProgressBar pb;
    Dialog dialog;
    int downloadedSize = 0;
    int totalSize = 0;


    TextView cur_val;

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public DisplayAdvertiseAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData)
    {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);
    }
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_categoty_child, null);
        }
        // Typeface mTypeFaceLight = Typeface.createFromAsset(convertView.getContext().getAssets(), "OpenSans-Light.ttf");
        TextView categoryChildNames = (TextView) convertView.findViewById(R.id.addDescription);
        TextView downloadButton = (TextView) convertView.findViewById(R.id.downloadButton);
        TextView downloadButtondsfd = (TextView) convertView.findViewById(R.id.downloadButtondsfd);
        //downloadButtondsfd

        final String [] data = childText.split("#");
        categoryChildNames.setText(data[0]);

        if(data[1].equals("0") || data[1].equals("NA"))
        {
            downloadButtondsfd.setVisibility(View.GONE);
            downloadButton.setVisibility(View.GONE);
        }
        else
        {
            downloadButton.setText(data[1]);
        }

//        downloadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//
//
//                downloadAdverise(data[1]);
//
//            }
//        });



        return convertView;
    }

    private void downloadAdverise(final String link)
    {

        showProgress(link);

        new Thread(new Runnable() {
            public void run() {
                downloadFile(link);
            }
        }).start();
    }
    void downloadFile(String dwnload_file_path){

        try {
            URL url = new URL(dwnload_file_path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            //connect
            urlConnection.connect();

            //set the path where we want to save the file
            File SDCardRoot = Environment.getExternalStorageDirectory();
            //create a new file, to save the downloaded file
            File file = new File(SDCardRoot,"downloaded_file.png");

            FileOutputStream fileOutput = new FileOutputStream(file);

            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            //this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();

            runOnUiThread(new Runnable() {
                public void run() {
                    pb.setMax(totalSize);
                }
            });

            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                // update the progressbar //
                runOnUiThread(new Runnable() {
                    public void run() {
                        pb.setProgress(downloadedSize);
                        float per = ((float)downloadedSize/totalSize) * 100;
                        cur_val.setText("Downloaded " + downloadedSize + "KB / " + totalSize + "KB (" + (int)per + "%)" );
                    }
                });
            }
            //close the output stream when complete //
            fileOutput.close();
            runOnUiThread(new Runnable() {
                public void run() {
                    // pb.dismiss(); // if you want close it..
                }
            });

        } catch (final MalformedURLException e) {
            showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            showError("Error : IOException " + e);
            e.printStackTrace();
        }
        catch (final Exception e) {
            showError("Error : Please check your internet connection " + e);
        }
    }

    void showError(final String err)
    {

        runOnUiThread(new Runnable(

        )
        {
            public void run()
            {
                Toast.makeText(_context, err, Toast.LENGTH_LONG).show();
            }
        });
    }

    void showProgress(String file_path){
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.myprogressdialog);
        dialog.setTitle("Download Progress");

        TextView text = (TextView) dialog.findViewById(R.id.tv1);
        text.setText("Downloading file from ... " + file_path);
        cur_val = (TextView) dialog.findViewById(R.id.cur_pg_tv);
        cur_val.setText("Starting download...");
        dialog.show();

        pb = (ProgressBar)dialog.findViewById(R.id.progress_bar);
        pb.setProgress(0);
        pb.setProgressDrawable(_context.getResources().getDrawable(R.drawable.green_progress));
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }
    @Override
    public Object getGroup(int groupPosition)
    {
        return this._listDataHeader.get(groupPosition);
    }
    @Override
    public int getGroupCount()
    {
        return this._listDataHeader.size();
    }
    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {

        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_categoty_group, null);
        }


        String [] groupData = _listDataHeader.get(groupPosition).split("#");

        TextView adTitleTextView = (TextView) convertView.findViewById(R.id.adTitleTextView);
        TextView adSuTitleTextView = (TextView) convertView.findViewById(R.id.adSuTitleTextView);


        adTitleTextView.setText(groupData[0]);
        adSuTitleTextView.setText(groupData[1]);

        return convertView;
    }
    @Override
    public boolean hasStableIds()
    {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        // TODO Auto-generated method stub
        return true;
    }
}
