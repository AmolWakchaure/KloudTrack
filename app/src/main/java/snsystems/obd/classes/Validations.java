package snsystems.obd.classes;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


/**
 * Created by shree on 06-Jan-17.
 */
public class Validations
{
    //pincode
    public static boolean validatePincodelessthan(EditText editText,String message,TextInputLayout textInputLayout)
    {
        if (editText.getText().toString().length() < 6)
        {
            textInputLayout.setError(message);
            //requestFocus(editText);
            return false;
        }
        else
        {
            return true;
        }


    }

    //empty field
    public static boolean validateEmptyField(EditText editText,String message,TextInputLayout textInputLayout)
    {
        if (editText.getText().toString().trim().isEmpty())
        {
            textInputLayout.setError(message);
            //requestFocus(editText);
            return false;
        }
        else
        {
            return true;
        }


    }
    //empty field
    public static boolean validateEmptyEditext(Context context,EditText editText,String message)
    {
        if (editText.getText().toString().trim().isEmpty())
        {
            T.tTop(context, message);
            //requestFocus(editText);
            return false;
        }
        else
        {
            return true;
        }


    }

    //validate mobile length
    public static boolean validateMobileLength(EditText editText,
                                               String message,
                                               TextInputLayout textInputLayout,
                                               Context context)
    {

        String mobN = editText.getText().toString().substring(0, 1);
        if(mobN.equals("+"))
        {
            String mobileNumbeTemp = editText.getText().toString().substring(editText.getText().toString().lastIndexOf("+") + 1);

            if(mobileNumbeTemp.length() < 12)
            {
                T.t(context,""+message);
                textInputLayout.setError(message);

                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            if(editText.getText().toString().length() < 10 || editText.getText().toString().length() > 10)
            {
                T.t(context,""+message);
                textInputLayout.setError(message);
                return false;
            }
            else
            {
                return true;
            }
        }




    }

    public static boolean validateSosMobile(EditText editText)
    {

        boolean returnStatus = false;
        try
        {
            String mobN = editText.getText().toString().substring(0, 1);
            if(mobN.equals("+"))
            {
                String mobileNumbeTemp = editText.getText().toString().substring(editText.getText().toString().lastIndexOf("+") + 1);

                if(mobileNumbeTemp.length() < 12)
                {
//                    T.t(context,""+message);
//                    textInputLayout.setError(message);

                    returnStatus = false;
                }
                else
                {
                    returnStatus = true;
                }
            }
            else
            {
                if(editText.getText().toString().length() < 10 || editText.getText().toString().length() > 10)
                {
//                    T.t(context,""+message);
//                    textInputLayout.setError(message);
                    returnStatus = false;
                }
                else
                {
                    returnStatus = true;
                }
            }
        }
        catch (Exception e)
        {

        }

        return returnStatus;



    }

    public  static  boolean checkNullField(String data,Context context,String message)
    {
        
        boolean statuss = false;
        try
        {
            if (data.equals("") || data == null || data.length() == 0)
            {
                Toast toast = Toast.makeText(context, "" + message, Toast.LENGTH_LONG);
                toast.show();
                statuss = false;
            }
            else
            {
                statuss = true;
            }
        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(context, "" + message, Toast.LENGTH_LONG);
            toast.show();
            statuss = false;
        }
        
        return statuss;
    }
    //validate text view empty

    public static boolean validateTextViewEmpty(TextView textView,
                                                String message,
                                                Context context,
                                                String textData)
    {

        if (textView.getText().toString().equals(textData))
        {
            Toast toast = Toast.makeText(context, "" + message, Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        else
        {
            return true;
        }


    }
    public static boolean validateButtnEmpty(Button textView,
                                                String message,
                                                Context context,
                                                String textData)
    {

        if (textView.getText().toString().equals(textData))
        {
            Toast toast = Toast.makeText(context, "" + message, Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        else
        {
            return true;
        }


    }
    //validate spinner empty
    public static boolean validateSpinnerEmpty(Spinner textView,
                                                String message,
                                                Context context,
                                                String spinnerData)
    {

        if (textView.getSelectedItem().toString().equals(spinnerData))
        {
            Toast toast = Toast.makeText(context, "" + message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP,0,0);
            toast.show();
            return false;
        }
        else
        {
            return true;
        }


    }
    //feedback length
    public static boolean validateFeedbackLength(Context context,EditText editText,String message)
    {

        int nameLength = editText.getText().toString().length();
        if (nameLength > 25 && nameLength < 1000)
        {
            return true;
        }
        else
        {
            T.tTop(context,message);
            return false;
        }

    }
    //validate year
    public static boolean validateYear(EditText editText, String message, TextInputLayout textInputLayout) {

        Calendar c = Calendar.getInstance();

        int now_year = c.get(Calendar.YEAR);
        int given_year = Integer.parseInt(editText.getText().toString());

        if (now_year < given_year) {
            textInputLayout.setError(message);
            return false;

        } else {
            return true;
        }

    }

    public static boolean validateHundredSpecificNumber(EditText editText,String message,TextInputLayout textInputLayout)
    {
        if (!editText.getText().toString().equals("100"))
        {
            textInputLayout.setError(message);
            //requestFocus(editText);
            return false;
        }
        else
        {
            return true;
        }


    }

    public static boolean validateHundredSpecificNumberLength(EditText editText,String message,TextInputLayout textInputLayout)
    {
        if (!editText.getText().toString().equals("100"))
        {
            textInputLayout.setError(message);
            //requestFocus(editText);
            return false;
        }
        else
        {
            return true;
        }


    }

    public static boolean validateRadioEmpty(RadioGroup radioGroup,
                                             Context context,
                                             String message)
    {

        if (radioGroup.getCheckedRadioButtonId() == -1)
        {
            Toast toast = Toast.makeText(context, "" + message, Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        else
        {
            return true;
        }


    }

    public static boolean validateVehicleNumber(String vehicleNumber,Context context)
    {


        //System.out.println(""+firstTwoCap);


        if(vehicleNumber.length() > 10)
        {
            String firstTwoCap = vehicleNumber.substring(0, 2);
            String firstSpace = vehicleNumber.substring(2, 3);
            String firstDigit = vehicleNumber.substring(3, 5);
            String secondSpace = vehicleNumber.substring(5, 6);
            String secondTwoCap = vehicleNumber.substring(6, 8);
            String thirdSpace = vehicleNumber.substring(8, 9);
            String lastDigits = vehicleNumber.substring(9, vehicleNumber.length());

            if(firstTwoCap == firstTwoCap.toUpperCase())
            {
                if(firstSpace.equals(" "))
                {


                    if(isNumeric(firstDigit))
                    {
                        if(secondSpace.equals(" "))
                        {


                            if(secondTwoCap == secondTwoCap.toUpperCase())
                            {
                                if(thirdSpace.equals(" "))
                                {
                                    if(!lastDigits.trim().isEmpty())
                                    {
                                        if(isNumeric(lastDigits))
                                        {
                                            return true;
                                        }
                                        else
                                        {

                                            T.t(context,"Invalid vehicle number.");
                                            return false;
                                        }
                                    }
                                    else
                                    {
                                        T.t(context,"Invalid vehicle number.");
                                        return false;
                                    }


                                }
                                else
                                {
                                    T.t(context,"Invalid vehicle number.");
                                    return false;
                                }

                            }
                            else
                            {
                                T.t(context,"Invalid vehicle number.");
                                return false;
                            }

                        }
                        else
                        {
                            T.t(context,"Invalid vehicle number.");
                            return false;
                        }

                    }
                    else
                    {
                        T.t(context,"Invalid vehicle number.");
                        return false;
                    }
                }
                else
                {
                    T.t(context,"Invalid vehicle number.");
                    return false;
                }

            }
            else
            {
                T.t(context,"Invalid vehicle number.");
                return false;
            }
        }
        else
        {
            T.t(context,"Invalid vehicle number length.");
            return false;
        }




    }
    public static boolean isNumeric(String str)
    {
        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }


//    public static boolean checkUniqueContacts(String  firstContact,String secondContact,String thirdContact,String fourthContact)
//    {
//        if (!firstContact.equals())
//        {
//            textInputLayout.setError(message);
//            //requestFocus(editText);
//            return false;
//        }
//        else
//        {
//            return true;
//        }
//    }

}
