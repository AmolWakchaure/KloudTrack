package snsystems.obd.docupload;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.cocosw.bottomsheet.BottomSheet;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.T;
import snsystems.obd.classes.Validations;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.dashboard.DashboardActivity;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;
import snsystems.obd.sos.SendSosActivity;


public class DocumentUploadActivity extends AnimationActivity implements DatePickerDialog.OnDateSetListener{

    private Toolbar
            toolbar;
    private Bitmap
            bitmap;

    private static int
            RESULT_LOAD_IMAGE = 1;

    private TextView
            textView7,
            textView8,
            textView9,
            //textView10,
            a, b, c, d;

    private RelativeLayout
            drivingLicence,
            carInsurance,
            pollutionDoucument,
            serviceDueLicence,
            serviceHistoryRequestLayout,
            newServiceRequestLayout;

    private static int
            CAMERA_REQUEST = 1888;

    private ImageView
            licenceImage,
            oneImageview,
            insuranceImageView,
                    imageView2,
                    pollutionImageview,
                    ppolutionImageView;

    private String dateSetPollution,dateSetInsurance,image_for_upload_pollution,image_for_upload_insurance,image_for_upload_driving_licence,_device_id,_expiry_date,_document_type,email_id,dateSetDrivingLicence;

    private String _trick_image = null;

    private TextView
            expiryDateDrivingLicence;

    private static final int REQUEST_PERMISSIONS = 20;

    String dueDateEmpty;

    //updatingImageLayout

    @Bind(R.id.updatingImageLayout)
    RelativeLayout updatingImageLayout;

    @Bind(R.id.insuranceDateTextview)
    TextView insuranceDateTextview;

    @Bind(R.id.serviceVehicleName)
    TextView serviceVehicleName;



    @Bind(R.id.pollutionExpfgfDateTextView)
    TextView pollutionExpfgfDateTextView;

    @Bind(R.id.licenceAttachText)
    TextView licenceAttachText;

    @Bind(R.id.insuranceAttachText)
    TextView insuranceAttachText;

    @Bind(R.id.pollutionAttachText)
    TextView pollutionAttachText;

    @Bind(R.id.serviceStatusSpinner)
    Spinner serviceStatusSpinner;

    @Bind(R.id.serviceDateTextView)
    TextView serviceDateTextView;

    @Bind(R.id.serviceDueDateTextView)
    TextView serviceDueDateTextView;

    @Bind(R.id.serviceChargesEditText)
    EditText serviceChargesEditText;

    @Bind(R.id.remarkChargesEditText)
    EditText remarkChargesEditText;

//    @Bind(R.id.selectServiceDateRelativeLayout)
//    RelativeLayout selectServiceDateRelativeLayout;
//
//    @Bind(R.id.selectServiceDueDateRelativeLayout)
//    RelativeLayout selectServiceDueDateRelativeLayout;

    @Bind(R.id.serviceChargesTextInputLayout)
    TextInputLayout serviceChargesTextInputLayout;

    @Bind(R.id.remarkTextInputLayout)
    TextInputLayout remarkTextInputLayout;

    @Bind(R.id.service_history_RecyclerView)
    RecyclerView service_history_RecyclerView;


    @Bind(R.id.vehicleNameTextViewDrivingLicence)
    TextView vehicleNameTextViewDrivingLicence;

    @Bind(R.id.vehicleNameTextViewInsurance)
    TextView vehicleNameTextViewInsurance;

    @Bind(R.id.vehicleNameTextViewPollution)
    TextView vehicleNameTextViewPollution;



    private ServiceHistoryAdapter serviceHistoryAdapter;

    private ArrayList<ServiceHistoryInformation> HISTORY_INFORMATION = new ArrayList<>();

    private  ArrayList<String> VEHICLE_NAMES;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);

        try
        {
            ButterKnife.bind(this);
            initializeWidgets();

            checkPermissionforsms();

            String device_id_mail = S.getDeviceIdUserName(new DBHelper(DocumentUploadActivity.this));

            String [] data = device_id_mail.split("#");
            _device_id = data[0];
            email_id = data[1];

            //get vehicle Names
            VEHICLE_NAMES = S.getVehicleName(new DBHelper(DocumentUploadActivity.this));


            String device_name = S.getNameByDEviceId(new DBHelper(DocumentUploadActivity.this),_device_id);
            serviceVehicleName.setText(device_name);


            boolean c = T.checkConnection(DocumentUploadActivity.this);
            if(c)
            {

                getDocumentsDetails();
               // getServiceHistory();
            }

        }
        catch (Exception e)
        {

        }


    }

    private void checkPermissionforsms()
    {

        if ( T.checkPermission(DocumentUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) )
        {

        }
        else T.askPermission(DocumentUploadActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void getServiceHistory()
    {
        String [] parameters =
                {
                        Constants.DEVICE_ID+"#"+"2345"
                };
        VolleyResponseClass.getResponseWithoutProgress(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e("VolleyResponse", "" + result);


                        HISTORY_INFORMATION = parseHistoryResponse(result);
                        serviceHistoryAdapter.setHistoryData(HISTORY_INFORMATION);
                        serviceHistoryAdapter.notifyDataSetChanged();

                    }
                },
                DocumentUploadActivity.this,
                getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.serviceHistory),
                pollutionDoucument,
                parameters);
    }

    private ArrayList<ServiceHistoryInformation> parseHistoryResponse(String result) {

        ArrayList<ServiceHistoryInformation> historyInformations = new ArrayList<>();
        try
        {
            String device_id = Constants.NA;
            String service_date = Constants.NA;
            String service_due_date = Constants.NA;
            String service_charges = Constants.NA;
            String remark = Constants.NA;
            String service_status = Constants.NA;

            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject historyJsonObject = new JSONObject(result);
                    String status = historyJsonObject.getString("status");

                    if(status.equals("1"))
                    {
                        JSONArray historyJsonArray = historyJsonObject.getJSONArray("history");

                        for(int i = 0; i < historyJsonArray.length(); i++)
                        {
                            JSONObject hhistoryJsonObject = historyJsonArray.getJSONObject(i);

                            if(hhistoryJsonObject.has("device_id") && !hhistoryJsonObject.isNull("device_id"))
                            {
                                device_id = hhistoryJsonObject.getString("device_id");
                            }
                            if(hhistoryJsonObject.has("service_date") && !hhistoryJsonObject.isNull("service_date"))
                            {
                                service_date = hhistoryJsonObject.getString("service_date");
                            }
                            if(hhistoryJsonObject.has("service_due_date") && !hhistoryJsonObject.isNull("service_due_date"))
                            {
                                service_due_date = hhistoryJsonObject.getString("service_due_date");
                            }
                            if(hhistoryJsonObject.has("service_charges") && !hhistoryJsonObject.isNull("service_charges"))
                            {
                                service_charges = hhistoryJsonObject.getString("service_charges");
                            }
                            if(hhistoryJsonObject.has("remark") && !hhistoryJsonObject.isNull("remark"))
                            {
                                remark = hhistoryJsonObject.getString("remark");
                            }
                            if(hhistoryJsonObject.has("service_status") && !hhistoryJsonObject.isNull("service_status"))
                            {
                                service_status = hhistoryJsonObject.getString("service_status");
                            }

                            ServiceHistoryInformation serviceHistoryInformation = new ServiceHistoryInformation();

                            serviceHistoryInformation.setDeviceId(device_id);
                            serviceHistoryInformation.setServiceDate(service_date);
                            serviceHistoryInformation.setServiceDueDate(service_due_date);
                            serviceHistoryInformation.setServiceCharges(service_charges);
                            serviceHistoryInformation.setServiceRemark(remark);
                            serviceHistoryInformation.setServiceStatus(service_status);

                            historyInformations.add(serviceHistoryInformation);
                        }
                    }
                    else if(status.equals("0"))
                    {
                        T.t(DocumentUploadActivity.this,"No service history found");
                    }
                }
                else
                {
                    T.t(DocumentUploadActivity.this,"incorect json");
                }
            }
            else
            {
                T.t(DocumentUploadActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }

       return historyInformations;
    }


    private void getDocumentsDetails()
    {

        try
        {

            //final String device_id_get = S.getDeviceId(new DBHelper(DocumentUploadActivity.this));

            final KProgressHUD progressDialog = KProgressHUD.create(DocumentUploadActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait...")
                    //.setDetailsLabel("Downloading data")
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();


            String path = getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.displayDocument);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, path,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            progressDialog.dismiss();


                            parseDocumentInformation(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            progressDialog.dismiss();
                            boolean c = T.handleVolleyerror(error,toolbar);
                            if(c)
                            {

                                updatingImageLayout.setVisibility(View.GONE);

                                getDocumentsDetails();
//                                Snackbar snackbar = Snackbar
//                                        .make(toolbar, "Fail to load document try again", Snackbar.LENGTH_LONG)
//                                        .setAction("Try Again", new View.OnClickListener()
//                                        {
//                                            @Override
//                                            public void onClick(View view)
//                                            {
//                                                getDocumentsDetails();
//                                            }
//                                        });
//
//                                snackbar.show();

                            }
                        }
                    }){
                @Override
                protected Map<String,String> getParams()
                {

                    Map<String,String> params = new HashMap<String, String>();

                    params.put("device_id",_device_id);

                    return params;
                }
            };
            RequestQueue requestQueue1 = Volley.newRequestQueue(DocumentUploadActivity.this);
            requestQueue1.getCache().clear();
            RetryPolicy policy = new DefaultRetryPolicy(40000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue1.add(stringRequest);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void parseDocumentInformation(String response)
    {
        ArrayList<DocumentInformation> DOCUMENT_INFORMATION = new ArrayList<>();

        String document_type = Constants.NA;
        String document_image_path = Constants.NA;
        String document_expiry_date = Constants.NA;

        try
        {
            if(response != null || response.length() > 0)
            {


                Object json = new JSONTokener(response).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject docJsonObject = new JSONObject(response);

                    String status = docJsonObject.getString("success");

                    if(status.equals("1"))
                    {

                        JSONArray docJsonArray = docJsonObject.getJSONArray("documentdata");

                        for(int i = 0; i < docJsonArray.length(); i++)
                        {
                            JSONObject ddocJsonObject = docJsonArray.getJSONObject(i);

                            if(ddocJsonObject.has("document_type") && !ddocJsonObject.isNull("document_type"))
                            {
                               document_type = ddocJsonObject.getString("document_type");
                            }
                            if(ddocJsonObject.has("path") && !ddocJsonObject.isNull("path"))
                            {
                                document_image_path = ddocJsonObject.getString("path");

                                //Log.e("document_image_path",""+document_image_path);
                            }
                            if(ddocJsonObject.has("expiry_date") && !ddocJsonObject.isNull("expiry_date"))
                            {
                                document_expiry_date = ddocJsonObject.getString("expiry_date");
                            }

                            DocumentInformation documentInformation = new DocumentInformation();

                            documentInformation.setDocumentType(document_type);
                            documentInformation.setDocumentImagePath(document_image_path);
                            documentInformation.setDocumentExpiryDate(document_expiry_date);

                            DOCUMENT_INFORMATION.add(documentInformation);



                        }

                        //setData\

                        String device_name = S.getNameByDEviceId(new DBHelper(DocumentUploadActivity.this),_device_id);

                        for(int j = 0; j < DOCUMENT_INFORMATION.size(); j++)
                        {

                            DocumentInformation documentInformation = DOCUMENT_INFORMATION.get(j);

                            String document_typee = documentInformation.getDocumentType();
                            String document_image_pathh = getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.documentImagePath)+""+documentInformation.getDocumentImagePath();
                            String document_expiry_datee = documentInformation.getDocumentExpiryDate();

                            if(document_typee.equals("Driving Licence"))
                            {

                                licenceAttachText.setText("Document Attached");
                                vehicleNameTextViewDrivingLicence.setText(device_name);
                                expiryDateDrivingLicence.setText(document_expiry_datee);


                                checkImageExists(document_image_pathh,oneImageview,licenceImage);




                            }
                            else if(document_typee.equals("Insurance"))
                            {
                                insuranceAttachText.setText("Document Attached");
                                vehicleNameTextViewInsurance.setText(device_name);
                                insuranceDateTextview.setText(document_expiry_datee);


                                checkImageExists(document_image_pathh,imageView2,insuranceImageView);


                            }
                            else if(document_typee.equals("Pollution"))
                            {
                                pollutionAttachText.setText("Document Attached");
                                vehicleNameTextViewPollution.setText(device_name);
                                pollutionExpfgfDateTextView.setText(document_expiry_datee);

                                checkImageExists(document_image_pathh,ppolutionImageView,pollutionImageview);
                            }
                        }

                    }

                }
                else
                {
                    T.t(DocumentUploadActivity.this,"Invalid jsonobject format");
                }
//                //you have an object
//                else if (json instanceof JSONArray)
//                {
//
//                }
            }
            else
            {
                T.t(DocumentUploadActivity.this,"Zero or null json found");
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void checkImageExists(final String document_image_pathh, final ImageView imageViewHide, final ImageView imageViewSet)
    {


            VolleyResponseClass.checkImageExistAtServer(
                    new VolleyCallback()
                    {
                        @Override
                        public void onSuccess(String result)
                        {

                            imageViewHide.setVisibility(View.GONE);
                            setImage(imageViewSet,document_image_pathh);
                            Log.e("VOLLY_ERROR","OK");
                           // parseFeedback(result);

                        }
                    },
                    new VolleyErrorCallback()
                    {
                        @Override
                        public void onError(VolleyError result)
                        {


                        }
                    },
                    DocumentUploadActivity.this,
                    document_image_pathh);


    }
    private boolean handleErrorFileExixts(VolleyError error)
    {
        boolean status = false;
        try
        {

            if(error instanceof TimeoutError || error instanceof NoConnectionError)
            {

                status = true;
                Log.e("VOLLY_ERROR","TimeoutError/NoConnectionError"+"Server not responding or no connection.");


            }
            else if(error instanceof AuthFailureError)
            {

                status = true;
                Log.e("VOLLY_ERROR","AuthFailureError"+"Remote server returns (401) Unauthorized?.");

            }
            else if(error instanceof ServerError)
            {


                status = false;

                Log.e("VOLLY_ERROR","ServerError"+"Wrong webservice call or wrong webservice url.");

            }
            else if (error instanceof NetworkError)
            {
                status = true;
                Log.e("VOLLY_ERROR","NetworkError"+"you doesn't have a data connection and wi-fi Connection.");

            }
            else if(error instanceof ParseError)
            {

                status = true;
                Log.e("VOLLY_ERROR","NetworkError"+"Incorrect json response.");
            }



        }
        catch (Exception e)
        {

        }
        return status;

    }
    private void setImage(ImageView insuranceImageView,String document_image_pathh) {


        Glide
                .with(DocumentUploadActivity.this)
                .load(document_image_pathh)
                        // .centerCrop()
                //.placeholder(R.drawable.ic_destinatin)
                        // .crossFade()
                .into(insuranceImageView);
    }

    private void grantRuntimePermissions() {

        boolean runtime = T.sdkLevel();

        if(runtime)
        {

        }
        else
        {

        }
    }



    private void initializeWidgets() {

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_24dp);

      //  Typeface lightTypeface = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
       // Typeface boldTypeface = Typeface.createFromAsset(getAssets(), "OpenSans-Bold.ttf");

        textView7 = (TextView)findViewById(R.id.textView7);
                textView8 = (TextView)findViewById(R.id.textView8);
                textView9 = (TextView)findViewById(R.id.textView9);
              //  textView10 = (TextView)findViewById(R.id.textView10);


                d = (TextView)findViewById(R.id.d);

        expiryDateDrivingLicence = (TextView)findViewById(R.id.expiryDateDrivingLicence);

        licenceImage = (ImageView)findViewById(R.id.licenceImage);
        oneImageview = (ImageView)findViewById(R.id.oneImageview);

        insuranceImageView = (ImageView)findViewById(R.id.insuranceImageView);
        imageView2 = (ImageView)findViewById(R.id.imageView2);
        pollutionImageview = (ImageView)findViewById(R.id.pollutionImageview);
        ppolutionImageView = (ImageView)findViewById(R.id.ppolutionImageView);

        drivingLicence = (RelativeLayout)findViewById(R.id.drivingLicence);
        carInsurance = (RelativeLayout)findViewById(R.id.carInsurance);
        pollutionDoucument = (RelativeLayout)findViewById(R.id.pollutionDoucument);
        serviceDueLicence = (RelativeLayout)findViewById(R.id.serviceDueLicence);
        serviceHistoryRequestLayout = (RelativeLayout)findViewById(R.id.serviceHistoryRequestLayout);
        newServiceRequestLayout = (RelativeLayout)findViewById(R.id.newServiceRequestLayout);


        service_history_RecyclerView.setLayoutManager(new LinearLayoutManager(DocumentUploadActivity.this));
        serviceHistoryAdapter = new ServiceHistoryAdapter(DocumentUploadActivity.this);
        service_history_RecyclerView.setAdapter(serviceHistoryAdapter);


//        textView7.setTypeface(boldTypeface);
//        textView8.setTypeface(boldTypeface);
//        textView9.setTypeface(boldTypeface);
//        textView10.setTypeface(boldTypeface);
//
//        a.setTypeface(lightTypeface);
//        b.setTypeface(lightTypeface);
//        c.setTypeface(lightTypeface);
//        d.setTypeface(lightTypeface);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_document_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void attachDrivingLicence(View view) {

        drivingLicence.setVisibility(View.VISIBLE);

    }

    public void cancelDialog(View view) {

        drivingLicence.setVisibility(View.GONE);
    }

    public void cancelInsuranceDialog(View view)
    {

        carInsurance.setVisibility(View.GONE);
    }

    public void ataacheInsurance(View view)
    {

        carInsurance.setVisibility(View.VISIBLE);
    }

    public void cancelPollutionDialog(View view) {

        pollutionDoucument.setVisibility(View.GONE);
    }

    public void attachPollution(View view) {

        pollutionDoucument.setVisibility(View.VISIBLE);
    }

    public void cancelDueDialog(View view) {

        serviceDueLicence.setVisibility(View.GONE);
    }

    public void attachserviceDue(View view) {

        serviceDueLicence.setVisibility(View.VISIBLE);
    }

    public void newServiceRequest(View view) {

        newServiceRequestLayout.setVisibility(View.VISIBLE);
        serviceHistoryRequestLayout.setVisibility(View.GONE);

    }

    public void serviceHistory(View view) {

        newServiceRequestLayout.setVisibility(View.GONE);
        serviceHistoryRequestLayout.setVisibility(View.VISIBLE);
    }

    public void pictureDrivingLicence(final View view)
    {

        openSheet("Driving Licence", 11);
    }

    public void docummentInsurance(View view)
    {
        openSheet("Insurance", 12);


    }

    public void documentPollution(View view)
    {
        openSheet("Pollution", 13);

    }

    private void openSheet(final String title, final int request)
    {

        new BottomSheet.Builder(this).title(title)
                .sheet(R.menu.camera_options)
                .grid()
                .listener(new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (which)
                        {
                            case R.id.camera:

                                byCamera(title);

                                break;
                            case R.id.gallery:

                                byGallery(title);

                                break;
                            case R.id.cancel:
                                dialog.dismiss();
                                break;
                        }
                    }
                }).show();

    }

    private void byGallery(String docName) {

        if(docName.equals("Driving Licence"))
        {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 222);
        }
        else if(docName.equals("Insurance"))
        {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 333);
        }
        else if(docName.equals("Pollution"))
        {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 444);
        }


    }

    private void byCamera(String docName)
    {

        if(docName.equals("Driving Licence"))
        {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 111);
        }
        else if(docName.equals("Insurance"))
        {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 555);
        }
        else if(docName.equals("Pollution"))
        {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 666);
        }



    }
    public Uri getImageUri(Context inContext, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

//        T.e("AMOL",""+requestCode);
//        T.e("AMOL",""+resultCode);
//        T.e("AMOL",""+RESULT_OK);
        //for licence
        if (requestCode == 111 && resultCode == RESULT_OK)
        {

            try
            {



                bitmap = (Bitmap) data.getExtras().get("data");
                licenceImage.setImageBitmap(bitmap);

                oneImageview.setVisibility(View.GONE);

                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(DocumentUploadActivity.this, bitmap);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));

                File sdfs = saveBitmapToFile(finalFile);

                String image_for_uploadd = sdfs.toString();



                image_for_upload_driving_licence = image_for_uploadd.replace("/","_");
                //uploadDocumentImage();


            }
            catch (Exception e)
            {
                T.displayException(DocumentUploadActivity.this,""+e);
            }

            /*
            Uri filePath = data.getData();
            try
            {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //bitmap = BitmapFactory.decodeFile(filePath);

                licenceImage.setImageBitmap(bitmap);
                //Setting the Bitmap to ImageView
                oneImageview.setVisibility(View.GONE);

                //compress image here


                String image_for_uploadd = getImagePath(filePath);

                image_for_upload_driving_licence = image_for_uploadd.replace(" ","_");



            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
             */


        }


        //insurance
        if (requestCode == 555 && resultCode == RESULT_OK)
        {

            try
            {



                bitmap = (Bitmap) data.getExtras().get("data");
                insuranceImageView.setImageBitmap(bitmap);
                imageView2.setVisibility(View.GONE);

                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(DocumentUploadActivity.this, bitmap);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));

                String image_for_uploadd = finalFile.toString();



                image_for_upload_insurance = image_for_uploadd.replace("/","_");
                //uploadDocumentImage();


            }
            catch (Exception e)
            {
                T.displayException(DocumentUploadActivity.this,""+e);
            }



        }
        //pollution
        if (requestCode == 666 && resultCode == RESULT_OK)
        {

            try
            {



                bitmap = (Bitmap) data.getExtras().get("data");
                pollutionImageview.setImageBitmap(bitmap);
                ppolutionImageView.setVisibility(View.GONE);

                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(DocumentUploadActivity.this, bitmap);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));

                String image_for_uploadd = finalFile.toString();



                image_for_upload_pollution = image_for_uploadd.replace("/","_");
                //uploadDocumentImage();


            }
            catch (Exception e)
            {
                T.displayException(DocumentUploadActivity.this,""+e);
            }



        }

        //for gallery

        //licence
        if (requestCode == 222 && resultCode == RESULT_OK && null != data)
        {

            Uri filePath = data.getData();
            try
            {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //bitmap = BitmapFactory.decodeFile(filePath);

                licenceImage.setImageBitmap(bitmap);
                //Setting the Bitmap to ImageView
                oneImageview.setVisibility(View.GONE);

                //compress image here


                String image_for_uploadd = getImagePath(filePath);

                image_for_upload_driving_licence = image_for_uploadd.replace(" ","_");



            }
            catch (IOException e)
            {
                T.displayException(DocumentUploadActivity.this,""+e);
                //e.printStackTrace();
            }

//            Uri filePath = data.getData();
//            try
//            {
//
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                licenceImage.setImageBitmap(bitmap);
//                oneImageview.setVisibility(View.GONE);
//                //viewUserImageProfile.setImageBitmap(bitmap);
//
//               // String image_for_uploadd = getImagePath(filePath);
//
//                //image_for_upload = image_for_uploadd.replace(" ","_");
//
//
//               // uploadProfileImage();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
        }
        //insurance
        if (requestCode == 333 && resultCode == RESULT_OK && null != data)
        {
            Uri filePath = data.getData();
            try
            {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //bitmap = BitmapFactory.decodeFile(filePath);

                insuranceImageView.setImageBitmap(bitmap);
                //Setting the Bitmap to ImageView
                imageView2.setVisibility(View.GONE);

                //compress image here


                String image_for_uploadd = getImagePath(filePath);

                image_for_upload_insurance = image_for_uploadd.replace(" ","_");



            }
            catch (IOException e)
            {
                T.displayException(DocumentUploadActivity.this,""+e);
            }
        }
        //pollution
        if (requestCode == 444 && resultCode == RESULT_OK && null != data)
        {
            Uri filePath = data.getData();
            try
            {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //bitmap = BitmapFactory.decodeFile(filePath);

                pollutionImageview.setImageBitmap(bitmap);
                //Setting the Bitmap to ImageView
                ppolutionImageView.setVisibility(View.GONE);

                //compress image here


                String image_for_uploadd = getImagePath(filePath);

                image_for_upload_pollution = image_for_uploadd.replace(" ","_");



            }
            catch (IOException e)
            {
                T.displayException(DocumentUploadActivity.this,""+e);
            }

        }
    }
    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    private String getImagePath(Uri filePath)
    {

        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(filePath, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();


        String fileNameSegments[] = picturePath.split("/");
        String fileName = fileNameSegments[fileNameSegments.length - 1];

        return fileName;
    }

    private void uploadDocumentImage(final String device_id, final String image_for_upload)
    {


        try
        {
//            String device_id_mail = S.getDeviceIdUserName(new DBHelper(DocumentUploadActivity.this));
//
//            String [] data = device_id_mail.split("#");

            Log.e("image_for_new",""+image_for_upload);

            final KProgressHUD progressDialog = KProgressHUD.create(DocumentUploadActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Uploading...")
                    //.setDetailsLabel("Downloading data")
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();

            String path = getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.uploadDocument);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, path,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            progressDialog.dismiss();
                            parseDocumentUploadInformation(response,device_id,image_for_upload);
                           //Log.e("ERROR",""+response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {

                            //Log.e("DOCUMENTSS",""+error);
                            boolean c = T.handleVolleyerror(error,toolbar);
                            if(c)
                            {

                                progressDialog.dismiss();

                                handleError(error,device_id,image_for_upload);

//                                Snackbar snackbar = Snackbar
//                                        .make(toolbar, "Fail to Upload image try again", Snackbar.LENGTH_LONG)
//                                        .setAction("Try Again", new View.OnClickListener()
//                                        {
//                                            @Override
//                                            public void onClick(View view)
//                                            {
//                                                uploadDocumentImage(device_id,image_for_upload);
//                                            }
//                                        });
//
//                                snackbar.show();

                            }
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){



                    Map<String,String> params = new HashMap<String, String>();



                    int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);


                    String _trick_image = getStringImage(scaled).toString();

                    //Log.e("IMAGESSSS",""+_trick_image);

                    params.put("device_id",_device_id);
                    params.put("email_id",email_id);
                    params.put("image_name",image_for_upload);
                    params.put("image_code", _trick_image);
                    params.put("expiry_date", _expiry_date);
                    params.put("document_type", _document_type);

//                    Log.e("IMAGE_DATA","_device_id"+_device_id);
//                    Log.e("IMAGE_DATA","email_id"+email_id);
//                    Log.e("IMAGE_DATA","image_for_upload"+image_for_upload);
//                    Log.e("IMAGE_DATA","_trick_image"+_trick_image);
//                    Log.e("IMAGE_DATA","_expiry_date"+_expiry_date);
//                    Log.e("IMAGE_DATA","_document_type"+_document_type);
//


                    return params;
                }
            };
            RequestQueue requestQueue1 = Volley.newRequestQueue(DocumentUploadActivity.this);
            requestQueue1.getCache().clear();
            RetryPolicy policy = new DefaultRetryPolicy(40000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue1.add(stringRequest);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void handleError(VolleyError error, String device_id, String image_for_upload)
    {
        try
        {

            if(error instanceof TimeoutError || error instanceof NoConnectionError)
            {

                displayError("TimeoutError/NoConnectionError","Server not responding or no connection.",device_id,image_for_upload);


            }
            else if(error instanceof AuthFailureError)
            {

                displayError("AuthFailureError","Remote server returns (401) Unauthorized?.",device_id,image_for_upload);

            }
            else if(error instanceof ServerError)
            {


                displayError("ServerError","Wrong webservice call or wrong webservice url.",device_id,image_for_upload);

            }
            else if (error instanceof NetworkError)
            {
                displayError("NetworkError","you doesn't have a data connection and wi-fi Connection.",device_id,image_for_upload);

            }
            else if(error instanceof ParseError)
            {

                displayError("NetworkError","Incorrect json response.",device_id,image_for_upload);
            }



        }
        catch (Exception e)
        {

        }

    }


    private void displayError(String title, String error, final String device_id, final String image_for_upload)
    {

        new SweetAlertDialog(DocumentUploadActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setConfirmText("Try again")
                .setContentText(error)
                .setCancelText("Cancel")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        sDialog.dismissWithAnimation();
                        uploadDocumentImage(device_id,image_for_upload);
                    }
                })
                .show();

    }

    private void parseDocumentUploadInformation(String response, final String device_id,final String image_for_upload) {

        try
        {
            if(response.length() > 0 || response != null)
            {

                JSONObject imageJsonObject = new JSONObject(response);
                String  status = imageJsonObject.getString("success");

                if(status.equals("1"))
                {

                    new SweetAlertDialog(DocumentUploadActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Success")
                            .setConfirmText("Ok")
                            .setContentText(_document_type+" details sucessfully uploaded")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();

                                    boolean c = T.checkConnection(DocumentUploadActivity.this);
                                    if(c)
                                    {

                                        getDocumentsDetails();
                                        // getServiceHistory();
                                    }


                                }
                            })
                            .show();


                }
                else
                {
                    new SweetAlertDialog(DocumentUploadActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Fail")
                            .setConfirmText("OK")
                            .setContentText("Fail to upload driving details click ok to try again.")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();

                                    uploadDocumentImage(device_id,image_for_upload);


                                }
                            })
                            .show();
                }

                //success
            }
            else
            {
                T.t(DocumentUploadActivity.this,"Incorrect json");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getStringImage(Bitmap bmp)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void submitServiceScheduleDetails(View view)
    {

        if(!Validations.validateTextViewEmpty(serviceVehicleName, "Select vehicle name", DocumentUploadActivity.this,"Select Vehicle Name*"))
        {
            return;
        }
        if(!Validations.validateTextViewEmpty(serviceDateTextView, "Select Servicing date", DocumentUploadActivity.this, "Service Date*"))
        {
            return;
        }
//        if(!Validations.validateTextViewEmpty(serviceDateTextView, "Select Servicing date", DocumentUploadActivity.this,"Service Due Date*"))
//        {
//            //return;
//            dueDateEmpty = "none";
//        }
        if(!Validations.validateEmptyField(serviceChargesEditText, "Enter service charges", serviceChargesTextInputLayout))
        {
            return;
        }
        if(!Validations.validateEmptyField(remarkChargesEditText, "Enter your remark", remarkTextInputLayout))
        {
            return;
        }
        if(!Validations.validateSpinnerEmpty(serviceStatusSpinner,"Select service status",DocumentUploadActivity.this,"Select Status*"))
        {
            return;
        }

        boolean c = T.checkConnection(DocumentUploadActivity.this);

        if(c)
        {
            submitDetails();
        }
        else
        {
            T.t(DocumentUploadActivity.this,"Network connection off");
        }

    }

    private void submitDetails() {

        String dueDate;

        String dueDateTest = serviceDueDateTextView.getText().toString();

        if(dueDateTest.equals("Service Due Date*"))
        {
            dueDate = serviceDateTextView.getText().toString();
        }
        else
        {
            dueDate = serviceDueDateTextView.getText().toString();
        }
        String [] parameters =
                {

                        Constants.DEVICE_ID+"#"+"2345",
                        "service_date"+"#"+serviceDateTextView.getText().toString(),
                        "service_due_date"+"#"+dueDate,
                        "service_charges"+"#"+serviceChargesEditText.getText().toString(),
                        "remark"+"#"+remarkChargesEditText.getText().toString(),
                        "service_status"+"#"+serviceStatusSpinner.getSelectedItem().toString()


                };
        VolleyResponseClass.getResponseProgressDialog(
                new VolleyCallback()
                {
                    @Override
                    public void onSuccess(String result) {
                        //Log.e("VolleyResponse", "" + result);


                        parseResponse(result);

                    }
                },
                DocumentUploadActivity.this,
                getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.serviceSchedule),
                pollutionDoucument,
                parameters,
                "Submitting details...");
    }

    private void parseResponse(String result) {

        //{"status":1,"success":"Success! Record added successfully."}
        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject tripsJsonObject = new JSONObject(result);
                    String status = tripsJsonObject.getString("status");

                    if(status.equals("0"))
                    {
                        T.displayErrorMessage(DocumentUploadActivity.this, "Fail", "Cancel", "Fail to submitService details. Try again...");
                    }
                    else if(status.equals("1"))
                    {
                        //for get updated service history
                        getServiceHistory();
                        T.displaySuccessMessage(DocumentUploadActivity.this,"Success","OK","Service details successfully submited.");
                    }
                    else if(status.equals("2"))
                    {
                        T.displayErrorMessage(DocumentUploadActivity.this,"Oops...","OK","Service details already submited.");
                    }
                }
                else
                {
                    T.t(DocumentUploadActivity.this,"incorrect json");
                }
            }
            else
            {
                T.t(DocumentUploadActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }


    public void selectDateDrivingLicence(View view)
    {

        //expiryDateDrivingLicence
        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                DocumentUploadActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setMinDate(Calendar.getInstance());
        dpd.setAccentColor(Color.parseColor("#0066B3"));
        dpd.show(getFragmentManager(), "Pick Date");

        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int date)
            {



                //String dateSet = "" + date + "-" + (++month) + "-" + year;
                dateSetDrivingLicence = "" + year + "-" + (++month) + "-" + date;
                expiryDateDrivingLicence.setText(dateSetDrivingLicence);


            }
        });


    }

    public void selectDateInsurance(View view)
    {
        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                DocumentUploadActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setMinDate(Calendar.getInstance());
        dpd.setAccentColor(Color.parseColor("#0066B3"));
        dpd.show(getFragmentManager(), "Pick Date");

        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int date)
            {


                dateSetInsurance = "" + year + "-" + (++month) + "-" + date;
                insuranceDateTextview.setText(dateSetInsurance);


            }
        });
    }

    public void selectDatePollution(View view) {

        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                DocumentUploadActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setMinDate(Calendar.getInstance());
        dpd.setAccentColor(Color.parseColor("#0066B3"));
        dpd.show(getFragmentManager(), "Pick Date");

        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int date)
            {

                //String dateSet = "" + date + "-" + (++month) + "-" + year;
                dateSetPollution = "" + year + "-" + (++month) + "-" + date;
                pollutionExpfgfDateTextView.setText(dateSetPollution);



            }
        });
    }

    public void selectServiceDate(View view) {

        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                DocumentUploadActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setMinDate(Calendar.getInstance());
        dpd.setAccentColor(Color.parseColor("#0066B3"));
        dpd.show(getFragmentManager(), "Pick Date");

        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int date)
            {
                //String dateSet = "" + date + "-" + (++month) + "-" + year;
                String dateSet = "" + year + "-" + (++month) + "-" + date;
                serviceDateTextView.setText(T.parseDate(dateSet));



//                _device_id = "2345";
//                _expiry_date = T.parseDate(dateSet);
//                _document_type = "Pollution";
//
//                uploadDocumentImage();
            }
        });
    }
    public void selectServiceDueDate(View view) {

        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                DocumentUploadActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setMinDate(Calendar.getInstance());
        dpd.setAccentColor(Color.parseColor("#0066B3"));
        dpd.show(getFragmentManager(), "Pick Date");

        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int date)
            {
                //String dateSet = "" + date + "-" + (++month) + "-" + year;
                String dateSet = "" + year + "-" + (++month) + "-" + date;
                serviceDueDateTextView.setText(T.parseDate(dateSet));



//                _device_id = "2345";
//                _expiry_date = T.parseDate(dateSet);
//                _document_type = "Pollution";
//
//                uploadDocumentImage();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {

    }

    public void selectVehicleNameService(View view)
    {
        if(VEHICLE_NAMES.isEmpty())
        {
            T.t(DocumentUploadActivity.this,"No vehicle names found");
        }
        else
        {
            selectVehicleNamesService();
        }

    }


    public void selectVehicleNameDrivingLicence(View view)
    {
        if(VEHICLE_NAMES.isEmpty())
        {
            T.t(DocumentUploadActivity.this,"No vehicle names found");
        }
        else
        {
            selectVehicleNames();
        }

    }

    public void selectVehicleNameInsurance(View view)
    {
        if(VEHICLE_NAMES.isEmpty())
        {
            T.t(DocumentUploadActivity.this,"No vehicle names found");
        }
        else
        {
            selectVehicleNames();
        }

    }
    public void selectVehicleNamePollution(View view)
    {
        if(VEHICLE_NAMES.isEmpty())
        {
            T.t(DocumentUploadActivity.this,"No vehicle names found");
        }
        else
        {
            selectVehicleNames();
        }

    }
    private void selectVehicleNamesService()
    {


        new MaterialDialog.Builder(DocumentUploadActivity.this)
                .title("select Vehicle")
                .items(VEHICLE_NAMES)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        try
                        {

                            serviceVehicleName.setText(text.toString());

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        return true;
                    }
                })
                .show();

    }


    private void selectVehicleNames()
    {


        new MaterialDialog.Builder(DocumentUploadActivity.this)
                .title("select Vehicle")
                .items(VEHICLE_NAMES)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        try
                        {

                            vehicleNameTextViewDrivingLicence.setText(text.toString());
                            vehicleNameTextViewInsurance.setText(text.toString());
                            vehicleNameTextViewPollution.setText(text.toString());

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        return true;
                    }
                })
                .show();

    }

    public void submitDrivingLicenceDetails(View view) {


        try
        {



            //T.t(DocumentUploadActivity.this,""+image_for_upload_driving_licence);
            //checkValidation
            if(!Validations.validateTextViewEmpty(vehicleNameTextViewDrivingLicence,"Select Vehicle Name",DocumentUploadActivity.this,"Vehicle Name*"))
            {
                return;
            }
            if(!Validations.checkNullField(image_for_upload_driving_licence,DocumentUploadActivity.this,"Select document image or photo"))
            {
                return;
            }
            if(!Validations.validateTextViewEmpty(expiryDateDrivingLicence,"Select Document Expiry Date",DocumentUploadActivity.this,"Expiry Date*"))
            {
                return;
            }


            //T.t(DocumentUploadActivity.this,"Success");

            //_expiry_date = T.parseDate(dateSetDrivingLicence);
            _expiry_date = expiryDateDrivingLicence.getText().toString();
            _document_type = "Driving Licence";

            String  device_iiid = S.returnDeviceIdByVehicleName(new DBHelper(DocumentUploadActivity.this),vehicleNameTextViewDrivingLicence.getText().toString());
            uploadDocumentImage(device_iiid,image_for_upload_driving_licence);
        }
        catch (Exception e)
        {

        }
    }

    public void submitInsuranceDetails(View view) {


        try
        {
            if(!Validations.validateTextViewEmpty(vehicleNameTextViewInsurance,"Select Vehicle Name",DocumentUploadActivity.this,"Vehicle Name*"))
            {
                return;
            }
            if(!Validations.checkNullField(image_for_upload_insurance,DocumentUploadActivity.this,"Select document image or photo"))
            {
                return;
            }
            if(!Validations.validateTextViewEmpty(insuranceDateTextview,"Select Document Expiry Date",DocumentUploadActivity.this,"Expiry Date*"))
            {
                return;
            }

           // _expiry_date = T.parseDate(dateSetInsurance);
            _expiry_date = insuranceDateTextview.getText().toString();
            _document_type = "Insurance";

            String  device_iiid = S.returnDeviceIdByVehicleName(new DBHelper(DocumentUploadActivity.this),vehicleNameTextViewInsurance.getText().toString());
            uploadDocumentImage(device_iiid,image_for_upload_insurance);
        }
        catch (Exception e)
        {

        }

    }

    public void submitPollutionDetails(View view)
    {

        try
        {
//                    //checkValidation
            if(!Validations.validateTextViewEmpty(vehicleNameTextViewPollution,"Select Vehicle Name",DocumentUploadActivity.this,"Vehicle Name*"))
            {
                return;
            }
            if(!Validations.checkNullField(image_for_upload_pollution,DocumentUploadActivity.this,"Select document image or photo"))
            {
                return;
            }
            if(!Validations.validateTextViewEmpty(pollutionExpfgfDateTextView,"Select Document Expiry Date",DocumentUploadActivity.this,"Expiry Date*"))
            {
                return;
            }



            //T.t(DocumentUploadActivity.this,"Success");

            //_expiry_date = T.parseDate(dateSetPollution);
            _expiry_date = pollutionExpfgfDateTextView.getText().toString();
            _document_type = "Pollution";

            String  device_iiid = S.returnDeviceIdByVehicleName(new DBHelper(DocumentUploadActivity.this),vehicleNameTextViewPollution.getText().toString());
            uploadDocumentImage(device_iiid,image_for_upload_pollution);


        }
        catch (Exception e)
        {
            //T.t(DocumentUploadActivity.this,""+e);

            Log.e("VALIDATIONS","error" + e);
        }

    }
}
//vehicleNameTextViewDrivingLicence,vehicleNameTextViewInsurance,vehicleNameTextViewPollution
